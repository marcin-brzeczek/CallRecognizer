package com.wepa.callrecognizer.call

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.wepa.callrecognizer.main.MainActivity
import com.wepa.callrecognizer.model.ContactModel
import com.wepa.callrecognizer.network.ContactsApi
import dagger.android.AndroidInjection
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
        contacts = intent?.getParcelableArrayListExtra(MainActivity::class.java.simpleName)
    }

//    private fun getContactsRequest() {
//        GlobalScope.launch(Dispatchers.Main) {
//            val getContactsRequest = withContext(Dispatchers.IO) { contactsApi.getContacts() }
//            try {
//                val response = getContactsRequest.await()
//                contacts = response.body()?.data
//
//                callHelper = CallHelper(baseContext, contacts)
//                callHelper?.start()
//
//            } catch (exception: Exception) {
//                when (exception) {
//                    is SocketTimeoutException, is ConnectException -> baseContext.makeLongToast(getString(R.string.connection_error) + ": ${exception.message}")
//                    else -> baseContext.makeLongToast(getString(R.string.problem_occurred) + ": ${exception.message}")
//                }
//            }
//        }
//    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        callHelper = CallHelper(this, contacts)
        val res = super.onStartCommand(intent, flags, startId)
        callHelper!!.start()
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