package com.wepa.callrecognizer.network

import com.wepa.callrecognizer.model.ContactsRequest
import com.wepa.callrecognizer.model.ShortContactModel
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface ContactsApi {

//    @GET("kontakty/lista")
//    fun getContacts(): Deferred<Response<ContactsRequest>>

    @GET("kontakty/lista")
    fun getContacts(): Single<ContactsRequest>

    @GET("kontakty?tel={phoneNumber}")
    fun getContactByPhoneNumber(
        @retrofit2.http.Path("phoneNumber") nummber:String
    ): Deferred<Response<ShortContactModel>>
}