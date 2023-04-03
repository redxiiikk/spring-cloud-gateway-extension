package com.github.redxiiikk.spring.cloud.gateway.loadbalancer

fun <T> Collection<T>.isSingle(): Boolean = this.size == 1