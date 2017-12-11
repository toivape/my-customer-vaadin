package com.pt.customer.ui

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
class CustomerService{
    var log = LoggerFactory.getLogger(CustomerService::class.java)

    @Value("\${customerapi.url}")
    lateinit private var customerApiUrl: String

    @Autowired
    lateinit var customerRestTemplate: RestTemplate

    fun findByName(name:String):List<Customer>{
        val url = getUrl("findByName/$name")
        val response = customerRestTemplate.exchange(url, HttpMethod.GET, null, object: ParameterizedTypeReference<List<Customer>>(){})
        return response.body
    }

    fun getCustomer(id:Long?):Customer? = customerRestTemplate.getForObject(getUrl("read/$id"), Customer::class.java)

    fun deleteCustomer(id:Long) = customerRestTemplate.delete(getUrl("delete/$id"))

    fun saveCustomer(customer:Customer) = customerRestTemplate.exchange(resolveSaveService(customer), HttpMethod.POST, HttpEntity(customer), Customer::class.java)

    private fun resolveSaveService(c:Customer) = if (c.id==null) getUrl("create") else getUrl("update")

    private fun getUrl(serviceName : String):String{
        val url = customerApiUrl + serviceName
        log.info(">>> Created service url $url")
        return url
    }

}