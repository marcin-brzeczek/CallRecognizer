package com.wepa.callrecognizer.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.wepa.callrecognizer.utils.makeLongToast

/**
 * Helper class to detect incoming and outgoing calls.
 * @author Moskvichev Andrey V.
 */
class CallHelper(private val context: Context, number:String) {
    private var telephonyManager: TelephonyManager? = null
    private val callStateListener: CallStateListener

    private val outgoingReceiver: OutgoingReceiver

    private val callerNumber = number
    private val outgoingCall = "Wepa Połączenie wychodzące: "
    private val incomingCall = "Wepa Połączenie przychodzące: "
    private val mockedInformationNumber =
        "Jan Kowalski \n " +
                "Firma Testowa sp. z o.o. \n" +
                "Zakup za ostatnie 6 miesięcy: 35 000\n" +
                "Zamówienia za ostatnie 6 miesięcy: 45 000"

    private inner class CallStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING ->

                    if (incomingNumber == callerNumber) {
                        context.makeLongToast(mockedInformationNumber)
                    } else {
                        context.makeLongToast(incomingCall)
                    }
            }
        }
    }

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    inner class OutgoingReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                context.makeLongToast("$outgoingCall $number")
        }
    }

    init {

        callStateListener = CallStateListener()
        outgoingReceiver = OutgoingReceiver()
    }

    /**
     * Start calls detection.
     */
    fun start() {
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager!!.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        val intentFilter = IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL)
        context.registerReceiver(outgoingReceiver, intentFilter)
    }

    /**
     * Stop calls detection.
     */
    fun stop() {
        telephonyManager!!.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
        context.unregisterReceiver(outgoingReceiver)
    }
}