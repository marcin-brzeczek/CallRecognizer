package com.wepa.callrecognizer.model

import com.fasterxml.jackson.annotation.JsonProperty


data class ContactsRequest(
    @JsonProperty("status")
    val status: Status,
    @JsonProperty("dane")
    val data: ArrayList<ContactModel>
)

data class Status(
    @JsonProperty("kod")
    val code: String,
    @JsonProperty("tresc")
    val text: String
)

data class ContactModel(
    @JsonProperty("dId")
    val id: Int,
    @JsonProperty("sImie")
    val firstName: String,
    @JsonProperty("sNazwisko")
    val lastName: String,
    @JsonProperty("sTelefon kom.")
    val mobilePhone: String,
    @JsonProperty("sTelefon")
    val phone: String,
    @JsonProperty("sFaks")
    val fax: String,
    @JsonProperty("sE-mail")
    val mail: String,
    @JsonProperty("sE-mail DW")
    val mailDW: String

) {
    override fun toString() = "$firstName $lastName \n $mail"
}