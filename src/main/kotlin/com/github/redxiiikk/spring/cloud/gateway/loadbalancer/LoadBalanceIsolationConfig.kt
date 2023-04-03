package com.github.redxiiikk.spring.cloud.gateway.loadbalancer

import com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation.LoadBalancerIsolationHeaderGeneratorFilter
import com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation.LoadbalancerIsolationConfigProperty
import com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation.LoadbalancerIsolationServiceInstanceListSupplier
import com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation.generator.EmptyLoadBalancerIsolationHeaderValueGenerator
import com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation.generator.LoadBalancerIsolationHeaderValueGenerator
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplierBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS


@Target(CLASS)
@Retention(RUNTIME)
@Import(LoadBalancerIsolationConfiguration::class)
@Suppress("unused")
annotation class EnableLoadBalancerIsolation

@Configuration
@EnableConfigurationProperties
@LoadBalancerClients(defaultConfiguration = [LoadBalancerConfig::class])
@ConditionalOnBean(ReactiveLoadBalancerClientFilter::class)
@ConditionalOnProperty(
    name = ["spring.cloud.gateway.loadbalancer.isolation.enable"],
    havingValue = "true",
    matchIfMissing = true
)
@Suppress("SpringFacetCodeInspection")
open class LoadBalancerIsolationConfiguration {
    @Bean
    open fun isolationConfig() = LoadbalancerIsolationConfigProperty()

    @Bean
    open fun isolationHeaderGeneratorFilter(
        property: LoadbalancerIsolationConfigProperty,
        generator: LoadBalancerIsolationHeaderValueGenerator
    ) = LoadBalancerIsolationHeaderGeneratorFilter(property, generator)

    @Bean
    @ConditionalOnMissingBean(LoadBalancerIsolationHeaderValueGenerator::class)
    open fun loadBalancerIsolationHeaderValueGenerator() =
        EmptyLoadBalancerIsolationHeaderValueGenerator()
}

open class LoadBalancerConfig {
    @Bean
    @ConditionalOnBean(ReactiveDiscoveryClient::class)
    open fun discoveryClientServiceInstanceListSupplier(
        context: ConfigurableApplicationContext,
        property: LoadbalancerIsolationConfigProperty
    ): ServiceInstanceListSupplier =
        ServiceInstanceListSupplier.builder()
            .withDiscoveryClient()
            .withIsolation(property)
            .build(context)

    private fun ServiceInstanceListSupplierBuilder.withIsolation(property: LoadbalancerIsolationConfigProperty): ServiceInstanceListSupplierBuilder {
        this.with { _: ConfigurableApplicationContext, delegate: ServiceInstanceListSupplier ->
            LoadbalancerIsolationServiceInstanceListSupplier(delegate, property)
        }

        return this
    }
}
