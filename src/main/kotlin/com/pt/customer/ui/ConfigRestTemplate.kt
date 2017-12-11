package com.pt.customer.ui

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import java.util.concurrent.TimeUnit
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients

@Configuration
class ConfigRestTemplate{

    companion object {
        const val CONN_POOL_SIZE = 3
        const val MAX_LIFESPAN_OF_PERSISTENT_CONNECTION_MINUTES = 10L
        const val TIMEOUT_MS = 2000
    }

    @Bean
    fun customerRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
                .requestFactory(createRequestFactory())
                .build()
    }

    private fun createRequestFactory(): HttpComponentsClientHttpRequestFactory {
        // Set timeouts to mitigate problems with slowly responding sub systems. Try to fail fast.
        val factory = HttpComponentsClientHttpRequestFactory()
        factory.httpClient = createHttpClient(CONN_POOL_SIZE, MAX_LIFESPAN_OF_PERSISTENT_CONNECTION_MINUTES)
        factory.setConnectTimeout(TIMEOUT_MS)
        factory.setReadTimeout(TIMEOUT_MS)
        return factory
    }

    private fun createHttpClient(maxPoolSize: Int, timeToLive: Long): HttpClient {
        val pooling = PoolingHttpClientConnectionManager(timeToLive, TimeUnit.MINUTES)
        pooling.maxTotal = maxPoolSize
        return HttpClients.custom().setConnectionManager(pooling).build()
    }

}