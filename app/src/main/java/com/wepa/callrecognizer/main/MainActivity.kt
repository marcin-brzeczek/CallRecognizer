package com.wepa.callrecognizer.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.wepa.callrecognizer.R
import com.wepa.callrecognizer.call.CallDetectService
import com.wepa.callrecognizer.network.NetworkHelper
import com.wepa.callrecognizer.utils.Statics
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Method


private const val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1
private const val MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 2
private const val MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 3
private const val MY_PERMISSIONS_REQUEST_WRITE_CALL_LOG = 4

class MainActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidInjection.inject(this)
        checkPermissions()

        startBtn.setOnClickListener {startForegroundService() }
    }

    @SuppressLint("WrongConstant")
    private fun startForegroundService() {
        val lState = CallDetectService.state
        if (lState == Statics.STATE_SERVICE.NOT_INIT) {
            if (!NetworkHelper.isInternetAvailable(applicationContext)) {
                showError()
                return
            }
            val startIntent = Intent(applicationContext, CallDetectService::class.java)
            startIntent.setAction(Statics.ACTION.START_ACTION)
            startService(startIntent)
        } else if (lState == Statics.STATE_SERVICE.PREPARE || lState == Statics.STATE_SERVICE.PLAY) {
            val lPauseIntent = Intent(applicationContext, CallDetectService::class.java)
            lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION)
            val lPendingPauseIntent =
                PendingIntent.getService(
                    applicationContext,
                    0,
                    lPauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            try {
                lPendingPauseIntent.send()
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }

        } else if (lState == Statics.STATE_SERVICE.PAUSE) {
            if (!NetworkHelper.isInternetAvailable(applicationContext)) {
                showError()
                return
            }
            val lPauseIntent = Intent(applicationContext, CallDetectService::class.java)
            lPauseIntent.setAction(Statics.ACTION.PLAY_ACTION)
            val lPendingPauseIntent =
                PendingIntent.getService(
                    applicationContext,
                    0,
                    lPauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            try {
                lPendingPauseIntent.send()
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }
        }
        setExpandNotificationDrawer(applicationContext, true)
        this@MainActivity.finish()

    }

    fun setExpandNotificationDrawer(context: Context, expand: Boolean) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val methodName =
                if (expand)
                    if (Build.VERSION.SDK_INT >= 17) "expandNotificationsPanel" else "expand"
                else
                    if (Build.VERSION.SDK_INT >= 17) "collapsePanels" else "collapse"
            val statusBarManager: Class<*> = Class.forName("android.app.StatusBarManager")
            val method: Method = statusBarManager.getMethod(methodName)
            method.invoke(statusBarService)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (applicationContext.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CALL_LOG),
                    MY_PERMISSIONS_REQUEST_READ_CALL_LOG
                )
            }
        }
    }


    private fun showError() {
        Toast.makeText(
            applicationContext, "No internet access!",
            Toast.LENGTH_SHORT
        ).show()
    }
}
