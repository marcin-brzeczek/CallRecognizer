package com.wepa.callrecognizer.call

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.wepa.callrecognizer.model.ContactsRequest
import com.wepa.callrecognizer.network.ContactsApi
import com.wepa.callrecognizer.notifications.CallNotificationManager
import com.wepa.callrecognizer.utils.Statics
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

    private var mNotificationManager: NotificationManager? = null

    private var callNotificationManager: CallNotificationManager? = null

    private val mHandler = Handler()

    private val mDelayedShutdown = Runnable {
        stopForeground(true)
        stopSelf()
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        initializeData()
    }

    private fun initializeData() {
        presenter = CallPresenter(this, contactsApi)
        callNotificationManager = CallNotificationManager()
        telephonyManager = baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        stateListener = initStateListener()
        state = Statics.STATE_SERVICE.NOT_INIT
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        /*todo for test*/
        presenter.getContactbyPhoneNumber("+48511113959")
        mNotificationManager!!.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, callNotificationManager?.prepareNotification(this.application, mNotificationManager!!))
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun displayError(message: String) {
        baseContext.makeLongToast("Error: $message")
        Timber.d("Error message: $message")
    }

    override fun displayContact(contact: ContactsRequest) {
        makeLongToast("${contact.data?.first()}")
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


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE)
//        val res = super.onStartCommand(intent, flags, startId)
//        return res

        if (intent == null) {
            stopForeground(true)
            stopSelf()
            return Service.START_NOT_STICKY
        }


        when (intent.action) {
            Statics.ACTION.START_ACTION -> {
                Log.i(TAG, "Received start Intent ")
                state = Statics.STATE_SERVICE.PREPARE
                startForeground(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, callNotificationManager?.prepareNotification(this.application, mNotificationManager!!))
            }

            Statics.ACTION.PAUSE_ACTION -> {
                state = Statics.STATE_SERVICE.PAUSE
                mNotificationManager!!.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, callNotificationManager?.prepareNotification(this.application, mNotificationManager!!))
                mHandler.postDelayed(mDelayedShutdown, Statics.DELAY_SHUTDOWN_FOREGROUND_SERVICE)
            }

            Statics.ACTION.PLAY_ACTION -> {
                state = Statics.STATE_SERVICE.PREPARE
                mNotificationManager!!.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, callNotificationManager?.prepareNotification(this.application, mNotificationManager!!))
            }

            Statics.ACTION.STOP_ACTION -> {
                Log.i(TAG, "Received Stop Intent")
                stopForeground(true)
                stopSelf()
            }

            else -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_NONE)
        presenter.stop()
        state = Statics.STATE_SERVICE.NOT_INIT
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private val FOREGROUND_CHANNEL_ID = "foreground_channel_id"
        private val TAG = CallDetectService::class.java.simpleName
        var state = Statics.STATE_SERVICE.NOT_INIT
            private set
    }
}