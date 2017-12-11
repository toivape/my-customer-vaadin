package com.pt.customer.ui

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.Date
import java.time.LocalDate
import java.time.ZoneId

fun LocalDate.toDate():Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

@JsonIgnoreProperties(ignoreUnknown = true)
class Customer{
    var id: Long? = null
    var version: Int? = null
    var created:Date? = null
    var updated:Date? = null
    var firstName:String? = null
    var lastName:String? = null
    var emailAddress:String? = null
    var birthDate:Date? = null

    override fun toString(): String {
        return "Customer(id=$id, version=$version, created=$created, updated=$updated, firstName=$firstName, lastName=$lastName, emailAddress=$emailAddress, birthDate=$birthDate)"
    }

    fun readProperties(to: CustomerTO?) {
        to?.let{
            firstName = to.firstName
            lastName = to.lastName
            emailAddress = to.emailAddress
            birthDate = to.birthDate?.toDate()
        }
    }
}