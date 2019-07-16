package com.wepa.callrecognizer.main

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Call detect service.
 * This service is needed, because MainActivity can lost it's focus,
 * and calls will not be detected.
 *
 * @author Moskvichev Andrey V.
 */
class CallDetectService() : Service() {

    private var clientNumber = ""

    private var callHelper: CallHelper? = null

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        clientNumber = intent?.getStringExtra(MainActivity::class.java.simpleName) ?: ""
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        callHelper = CallHelper(this, clientNumber)

        val res = super.onStartCommand(intent, flags, startId)
        callHelper!!.start()
        return res
    }

    override fun onDestroy() {
        super.onDestroy()

        callHelper!!.stop()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}