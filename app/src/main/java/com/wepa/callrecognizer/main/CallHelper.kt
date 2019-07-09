package com.wepa.callrecognizer.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast

/**
 * Helper class to detect incoming and outgoing calls.
 * @author Moskvichev Andrey V.
 */
class CallHelper(private val ctx: Context) {
    private var tm: TelephonyManager? = null
    private val callStateListener: CallStateListener

    private val outgoingReceiver: OutgoingReceiver

    /**
     * Listener to detect incoming calls.
     */
    private inner class CallStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING ->
                    // called when someone is ringing to this phone

                    Toast.makeText(
                        ctx,
                        "Incoming: $incomingNumber",
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    inner class OutgoingReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)

            Toast.makeText(
                ctx,
                "Outgoing: $number",
                Toast.LENGTH_LONG
            ).show()
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
        tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        tm!!.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        val intentFilter = IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL)
        ctx.registerReceiver(outgoingReceiver, intentFilter)
    }

    /**
     * Stop calls detection.
     */
    fun stop() {
        tm!!.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
        ctx.unregisterReceiver(outgoingReceiver)
    }

}