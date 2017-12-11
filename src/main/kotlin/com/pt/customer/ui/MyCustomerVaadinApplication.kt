package com.pt.customer.ui

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MyCustomerVaadinApplication

fun main(args: Array<String>) {
    SpringApplication.run(MyCustomerVaadinApplication::class.java, *args)
}
