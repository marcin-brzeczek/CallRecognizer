package com.wepa.callrecognizer.call

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.wepa.callrecognizer.main.MainActivity

class CallDetectService : Service() {

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