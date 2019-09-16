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
import com.wepa.callrecognizer.utils.showCustomToast
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

private const val kadafiContact = "+48511113959"

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

    companion object {
        var state = Statics.STATE_SERVICE.NOT_INIT
    }

    private val TAG = CallDetectService::class.java.simpleName

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
        state = Statics.STATE_SERVICE.NOT_INIT
        callNotificationManager = CallNotificationManager()
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        telephonyManager = baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        stateListener = initStateListener()
        state = Statics.STATE_SERVICE.NOT_INIT
    }

    override fun displayError(message: String) {
        baseContext.makeLongToast("Error: $message")
        Timber.d("Error message: $message")
    }

    override fun displayContact(contact: ContactsRequest) {
        showCustomToast("${contact.data?.first()}")
    }

    private fun initStateListener() = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_RINGING)
                presenter.getContactbyPhoneNumber(incomingNumber)
        }

//    contact = contacts?.find {
//        (it.mobilePhone == incomingNumber ||
//                it.mobilePhone.drop(3) == incomingNumber ||
//                it.phone == incomingNumber)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE)

        if (intent == null) {
            stopForeground(true)
            stopSelf()
            return Service.START_NOT_STICKY
        }

        when (intent.action) {
            Statics.ACTION.START_ACTION -> {
                Log.i(TAG, "Received start Intent ")
                state = Statics.STATE_SERVICE.PREPARE
                startForeground(
                    Statics.NOTIFICATION_ID_FOREGROUND_SERVICE,
                    callNotificationManager?.prepareNotification(this.application, mNotificationManager!!)
                )
            }

            Statics.ACTION.PAUSE_ACTION -> {
                state = Statics.STATE_SERVICE.PAUSE
                mNotificationManager!!.notify(
                    Statics.NOTIFICATION_ID_FOREGROUND_SERVICE,
                    callNotificationManager?.prepareNotification(this.application, mNotificationManager!!)
                )
                mHandler.postDelayed(mDelayedShutdown, Statics.DELAY_SHUTDOWN_FOREGROUND_SERVICE)
            }

            Statics.ACTION.PLAY_ACTION -> {
                state = Statics.STATE_SERVICE.PREPARE
                mNotificationManager!!.notify(
                    Statics.NOTIFICATION_ID_FOREGROUND_SERVICE,
                    callNotificationManager?.prepareNotification(this.application, mNotificationManager!!)
                )
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

    override fun onBind(intent: Intent): IBinder? = null
}