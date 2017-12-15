package com.pt.customer.ui

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@EnableMetrics
@SpringBootApplication
class MyCustomerVaadinApplication

fun main(args: Array<String>) {
    SpringApplication.run(MyCustomerVaadinApplication::class.java, *args)
}


@RefreshScope
@RestController
internal class MessageRestController {

    @Value("\${boo:Hello default}")
    @get:RequestMapping("/message")
    val message: String? = null
}