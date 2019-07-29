package com.wepa.callrecognizer.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class IncomingCallReceiver : BroadcastReceiver() {
//
    override fun onReceive(context: Context, intent: Intent) {
//
//        val telephonyService: ICallManager
//        try {
//            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
//            val number = intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
//
//            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING, ignoreCase = true)) {
//
//                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//                try {
//                    val m = tm.javaClass.getDeclaredMethod("getITelephony")
//
//                    m.isAccessible = true
//                    telephonyService = m.invoke(tm) as ICallManager
//
//                    if (number != null) {
//                        telephonyService.endCall()
//                        Toast.makeText(context, "Ending the call from: $number", Toast.LENGTH_SHORT).show()
//                    }
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//                Toast.makeText(context, "Ring " + number!!, Toast.LENGTH_SHORT).show()
//
//            }
//            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK, ignoreCase = true)) {
//                Toast.makeText(context, "Answered " + number!!, Toast.LENGTH_SHORT).show()
//            }
//            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE, ignoreCase = true)) {
//                Toast.makeText(context, "Idle " + number!!, Toast.LENGTH_SHORT).show()
//            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
    }
}