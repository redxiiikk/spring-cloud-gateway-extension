package com.github.redxiiikk.learn.gatewayDispatch

import com.github.redxiiikk.spring.cloud.gateway.loadbalancer.EnableLoadBalancerIsolation
import com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation.generator.LoadBalancerIsolationHeaderValueGenerator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.web.server.ServerWebExchange

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableLoadBalancerIsolation
open class StartApplication {
    @Bean
    open fun userIsolationHeaderValueGenerator() = UserIsolationHeaderGenerator()
}

fun main(args: Array<String>) {
    runApplication<StartApplication>(*args)
}

class UserIsolationHeaderGenerator : LoadBalancerIsolationHeaderValueGenerator {
    override fun generateHeaderValue(request: ServerWebExchange): String {
        return "dev-redxiiikk"
    }
}