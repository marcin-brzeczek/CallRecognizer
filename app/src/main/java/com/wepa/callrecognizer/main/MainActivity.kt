package com.wepa.callrecognizer.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.wepa.callrecognizer.R
import com.wepa.callrecognizer.call.CallDetectService
import com.wepa.callrecognizer.network.ContactsApi
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private const val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1
private const val MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 2

class MainActivity : AppCompatActivity(){

    @Inject
    lateinit var contactsApi: ContactsApi


    val serviceIntent by lazy { Intent(this@MainActivity, CallDetectService::class.java) }

    private var detectEnabled: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidInjection.inject(this)
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
        if (enable) {
            // start detect service
            startService(serviceIntent)

            buttonDetectToggle.text = "Turn off"
            textViewDetectState.text = "Detecting"
        } else {
            // stop detect service
            stopService(serviceIntent)

            buttonDetectToggle.text = "Turn on"
            textViewDetectState.text = "Not detecting"
        }
    }
}