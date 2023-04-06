package com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation

import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.loadbalancer.Request
import org.springframework.cloud.client.loadbalancer.RequestDataContext
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import reactor.core.publisher.Flux

class LoadbalancerIsolationServiceInstanceListSupplier(
    delegate: ServiceInstanceListSupplier,
    private val property: LoadbalancerIsolationConfigProperty
) : DelegatingServiceInstanceListSupplier(delegate) {

    override fun get(): Flux<MutableList<ServiceInstance>> {
        return if (property.enable) {
            delegate.get().map { selectServiceInstancesByEnvName(it, property.baselineEnvName) }
        } else {
            delegate.get()
        }
    }

    override fun get(request: Request<*>): Flux<MutableList<ServiceInstance>> {
        return if (property.enable) {
            delegate.get(request).map { filterByIsolation(request, it) }
        } else {
            delegate.get(request)
        }
    }


    private fun filterByIsolation(
        request: Request<*>, instances: MutableList<ServiceInstance>
    ): MutableList<ServiceInstance> {
        val envName: String = when (val context = request.context) {
            is RequestDataContext ->
                context.clientRequest.headers.getFirst(property.isolationHeaderKey) ?: property.baselineEnvName

            else -> property.baselineEnvName
        }


        return selectServiceInstancesByEnvName(instances, envName).ifEmpty {
            selectServiceInstancesByEnvName(instances, property.baselineEnvName)
        }
    }

    private fun selectServiceInstancesByEnvName(
        instances: MutableList<ServiceInstance>, envName: String
    ): MutableList<ServiceInstance> {
        return instances.filter { envName == it.metadata[property.isolationMetadataKey] }.toMutableList()
    }
}