package com.pt.customer.ui

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

fun Date.toLocalDate():LocalDate = Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDate()

class CustomerTO{
    constructor(c:Customer){
        firstName = c.firstName
        lastName = c.lastName
        emailAddress = c.emailAddress
        birthDate = c.birthDate?.toLocalDate()
    }

    var firstName:String? = null
    var lastName:String? = null
    var emailAddress:String? = null
    var birthDate: LocalDate? = null
}