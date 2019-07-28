package com.wepa.callrecognizer.model

import com.fasterxml.jackson.annotation.JsonProperty
import paperparcel.PaperParcel
import paperparcel.PaperParcelable


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

data class ShortContactModel(
    @JsonProperty("sNazwa")
    val name: String,
    @JsonProperty("sImie")
    val firstName: String,
    @JsonProperty("sNazwisko")
    val lastName: String
) {
    override fun toString() = "$name \n $firstName \n $lastName"
}

@PaperParcel
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

) : PaperParcelable {
    override fun toString() = "$firstName $lastName \n $mail"

    companion object {
        @JvmField
        val CREATOR = PaperParcelContactModel.CREATOR
    }
}