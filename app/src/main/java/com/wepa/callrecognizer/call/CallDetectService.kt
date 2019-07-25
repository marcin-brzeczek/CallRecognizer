package com.wepa.callrecognizer.call

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.wepa.callrecognizer.R
import com.wepa.callrecognizer.model.ContactModel
import com.wepa.callrecognizer.network.ContactsApi
import com.wepa.callrecognizer.utils.makeLongToast
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

class CallDetectService : Service() {

    @Inject
    internal lateinit var contactsApi: ContactsApi

    private var callHelper: CallHelper? = null

    private var contacts: List<ContactModel>? = null

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    private fun getContactsRequest() {
        GlobalScope.launch(Dispatchers.Main) {
            val getContactsRequest = withContext(Dispatchers.IO) { contactsApi.getContact() }
            try {
                val response = getContactsRequest.await()
                contacts = response.body()?.data
            } catch (exception: Exception) {
                when (exception) {
                    is SocketTimeoutException, is ConnectException -> baseContext.makeLongToast(getString(R.string.connection_error) + ": ${exception.message}")
                    else -> baseContext.makeLongToast(getString(R.string.problem_occurred) + ": ${exception.message}")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        callHelper = CallHelper(this, contacts)
        getContactsRequest()
        val res = super.onStartCommand(intent, flags, startId)
        callHelper?.start()
        return res
    }

    override fun onDestroy() {
        super.onDestroy()
        callHelper?.stop()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}