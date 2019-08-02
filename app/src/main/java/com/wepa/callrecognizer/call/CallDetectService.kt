package com.wepa.callrecognizer.call

import android.app.Application
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.wepa.callrecognizer.main.MainActivity
import com.wepa.callrecognizer.model.ContactModel
import com.wepa.callrecognizer.network.ContactsApi
import com.wepa.callrecognizer.utils.makeLongToast
import dagger.android.AndroidInjection
import javax.inject.Inject

class CallDetectService : Service(), CallContract.ViewInterface {

    @Inject
    internal lateinit var contactsApi: ContactsApi

    private var callHelper: CallHelper? = null

    private lateinit var presenter: CallPresenter


    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        setupPresenter()
    }

    private fun setupPresenter() {
        presenter = CallPresenter(this, contactsApi)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        presenter.getContacts()
    }

    override fun displayError(message: String) {
        baseContext.makeLongToast("Error: $message")
    }

    override fun displayContacts(contacts: ArrayList<ContactModel>) {
//        baseContext.makeLongToast("First element od contacts: ${contacts[0]}")
        serviceIntent.putParcelableArrayListExtra(MainActivity::class.java.simpleName, contacts)
    }

    private class CallStateListener(private val context: Application) : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {

                    contact = contacts?.find {
                        (it.mobilePhone == incomingNumber ||
                                it.mobilePhone.drop(3) == incomingNumber ||
                                it.phone == incomingNumber)
                    }
                    contact?.let {
                        context.makeLongToast(it.toString())
                    }
                }
            }
        }
    }

//    override fun onStart(intent: Intent?, startId: Int) {
//        super.onStart(intent, startId)
//        contacts = intent?.getParcelableArrayListExtra(MainActivity::class.java.simpleName)
//    }

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
        val contacts = intent.getParcelableArrayListExtra<ContactModel>(MainActivity::class.java.simpleName)
        callHelper = CallHelper(this, contacts)
        val res = super.onStartCommand(intent, flags, startId)
        callHelper!!.start()
        return res
    }

    override fun onDestroy() {
        super.onDestroy()
        callHelper?.stop()
        presenter.stop()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}