package com.github.redxiiikk.spring.cloud.gateway.loadbalancer.isolation.generator

import org.springframework.web.server.ServerWebExchange

interface LoadBalancerIsolationHeaderValueGenerator {
    fun generateHeaderValue(request: ServerWebExchange): String
}