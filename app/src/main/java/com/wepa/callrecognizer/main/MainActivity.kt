package com.wepa.callrecognizer.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.wepa.callrecognizer.R
import kotlinx.android.synthetic.main.activity_main.*

private const val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1
private const val MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 2

class MainActivity : AppCompatActivity() {

    private var detectEnabled: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        buttonDetectToggle.setOnClickListener { setDetectEnabled(!detectEnabled) }

        buttonExit.setOnClickListener {
            setDetectEnabled(false)
            this@MainActivity.finish()
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    private fun setDetectEnabled(enable: Boolean) {
        detectEnabled = enable

        val intent = Intent(this, CallDetectService::class.java)
        intent.putExtra(MainActivity::class.java.simpleName, editTextNumber.text.toString())
        if (enable) {
            // start detect service
            startService(intent)

            buttonDetectToggle.text = "Turn off"
            textViewDetectState.text = "Detecting"
        } else {
            // stop detect service
            stopService(intent)

            buttonDetectToggle.text = "Turn on"
            textViewDetectState.text = "Not detecting"
        }
    }

}