package com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation.generator

import org.springframework.web.server.ServerWebExchange

class EmptyLoadBalancerIsolationHeaderValueGenerator : LoadBalancerIsolationHeaderValueGenerator {
    override fun generateHeaderValue(request: ServerWebExchange): String {
        return ""
    }
}