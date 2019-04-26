package com.ccanahuire.retooh.model

import java.io.Serializable

data class UserData(
    var name: String?,
    var lastName: String?,
    var birthdate: String?,
    var age: Int?
) : Serializable {
    constructor() : this(null, null, null, null)
}

