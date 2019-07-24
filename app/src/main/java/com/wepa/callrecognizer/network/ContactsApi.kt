package com.wepa.callrecognizer.network

import com.wepa.callrecognizer.model.ContactsRequest
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface ContactsApi {

    @GET("kontakty/lista")
    fun getContact(): Deferred<Response<ContactsRequest>>
}