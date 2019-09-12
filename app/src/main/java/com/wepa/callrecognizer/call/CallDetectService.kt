package com.wepa.callrecognizer.call

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.wepa.callrecognizer.model.ContactsRequest
import com.wepa.callrecognizer.network.ContactsApi
import com.wepa.callrecognizer.utils.makeLongToast
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

class CallDetectService : Service(), CallContract.ViewInterface {

    @Inject
    internal lateinit var contactsApi: ContactsApi

    private var callHelper: CallHelper? = null

    private lateinit var presenter: CallPresenter

    private lateinit var stateListener: PhoneStateListener

    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        initializeData()
    }

    private fun initializeData() {
        presenter = CallPresenter(this, contactsApi)
        telephonyManager = baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        stateListener = initStateListener()
        /*todo for test*/
        presenter.getContactbyPhoneNumber("+48511113959")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun displayError(message: String) {
        baseContext.makeLongToast("Error: $message")
        Timber.d("Error message: $message")
    }

    override fun displayContact(contact: ContactsRequest) {
        makeLongToast("${contact.status}")
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
    private fun initStateListener() = object : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, incomingNumber: String) {

//            if (state == TelephonyManager.CALL_STATE_RINGING)
//                presenter.getContactbyPhoneNumber(incomingNumber)

//                    contact = contacts?.find {
//                        (it.mobilePhone == incomingNumber ||
//                                it.mobilePhone.drop(3) == incomingNumber ||
//                                it.phone == incomingNumber)
//                    }
//                    contact?.let {
//                        context.makeLongToast(it.toString())
//                    }

        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE)
        val res = super.onStartCommand(intent, flags, startId)
        return res
    }

    override fun onDestroy() {
        super.onDestroy()
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_NONE)
        presenter.stop()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}