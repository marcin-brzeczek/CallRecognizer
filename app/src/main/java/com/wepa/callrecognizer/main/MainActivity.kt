package com.wepa.callrecognizer.main

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.wepa.callrecognizer.R
import com.wepa.callrecognizer.call.CallDetectService
import com.wepa.callrecognizer.network.NetworkHelper
import com.wepa.callrecognizer.utils.Statics
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*

private const val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1
private const val MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 2

class MainActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidInjection.inject(this)
        checkPermissions()

        startBtn.setOnClickListener { view->startForegroundService(view) }
    }

    private fun startForegroundService(view:View) {
        val lState = CallDetectService.state
        if (lState == Statics.STATE_SERVICE.NOT_INIT) {
            if (!NetworkHelper.isInternetAvailable(view.getContext())) {
                showError(view)
                return
            }
            val startIntent = Intent(view.getContext(), CallDetectService::class.java)
            startIntent.setAction(Statics.ACTION.START_ACTION)
            startService(startIntent)
        } else if (lState == Statics.STATE_SERVICE.PREPARE || lState == Statics.STATE_SERVICE.PLAY) {
            val lPauseIntent = Intent(view.getContext(), CallDetectService::class.java)
            lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION)
            val lPendingPauseIntent =
                PendingIntent.getService(view.getContext(), 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            try {
                lPendingPauseIntent.send()
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }

        } else if (lState == Statics.STATE_SERVICE.PAUSE) {
            if (!NetworkHelper.isInternetAvailable(view.getContext())) {
                showError(view)
                return
            }
            val lPauseIntent = Intent(view.getContext(), CallDetectService::class.java)
            lPauseIntent.setAction(Statics.ACTION.PLAY_ACTION)
            val lPendingPauseIntent =
                PendingIntent.getService(view.getContext(), 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            try {
                lPendingPauseIntent.send()
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }
        }
    }


    private fun checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (applicationContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
                )
            }
            if (applicationContext.checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS),
                    MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS
                )
            }
        }
    }


    private fun showError(v: View) {
        Toast.makeText(
            applicationContext, "No internet access!",
            Toast.LENGTH_SHORT
        ).show()
    }
}