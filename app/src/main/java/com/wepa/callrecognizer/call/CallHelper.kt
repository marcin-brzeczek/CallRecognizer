package com.wepa.callrecognizer.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.wepa.callrecognizer.model.ContactModel
import com.wepa.callrecognizer.utils.makeLongToast

class CallHelper(private val context: Context, private val contacts: List<ContactModel>?) {
    private var telephonyManager: TelephonyManager? = null
    private val callStateListener: CallStateListener
    private var contact: ContactModel? = null

    private val outgoingReceiver: OutgoingReceiver

    private val outgoingCall = " Połączenie wychodzące: "

    private inner class CallStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {

                    contact = contacts?.first { it.mobilePhone == incomingNumber || it.phone == incomingNumber }
                    contact?.let {
                        context.makeLongToast(it.toString())
                    }
                }
            }
        }
    }

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

    fun start() {
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager?.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        val intentFilter = IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL)
        context.registerReceiver(outgoingReceiver, intentFilter)
    }

    fun stop() {
        telephonyManager?.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
        context.unregisterReceiver(outgoingReceiver)
    }
}