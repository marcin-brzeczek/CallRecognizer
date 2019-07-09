package com.wepa.callrecognizer.main

//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import com.wepa.callrecognizer.R
//import android.Manifest
//import android.content.pm.PackageManager
//import android.widget.Toast
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        Toast.makeText(this, "Started the app", Toast.LENGTH_SHORT).show()
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
//                    Manifest.permission.CALL_PHONE
//                ) == PackageManager.PERMISSION_DENIED
//            ) {
//                val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
//                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE)
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            PERMISSION_REQUEST_READ_PHONE_STATE -> {
//                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granted: $PERMISSION_REQUEST_READ_PHONE_STATE", Toast.LENGTH_SHORT)
//                        .show()
//                } else {
//                    Toast.makeText(
//                        this,
//                        "Permission NOT granted: $PERMISSION_REQUEST_READ_PHONE_STATE",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                return
//            }
//        }
//    }
//
//    companion object {
//        private val PERMISSION_REQUEST_READ_PHONE_STATE = 1
//    }
//
//}
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.wepa.callrecognizer.R

/**
 * Main activity, with button to toggle phone calls detection on and off.
 * @author Moskvichev Andrey V.
 */
class MainActivity : Activity() {

    private var detectEnabled: Boolean = false

    private var textViewDetectState: TextView? = null
    private var buttonToggleDetect: Button? = null
    private var buttonExit: Button? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewDetectState = findViewById<View>(R.id.textViewDetectState) as TextView

        buttonToggleDetect = findViewById<View>(R.id.buttonDetectToggle) as Button
        buttonToggleDetect!!.setOnClickListener { setDetectEnabled(!detectEnabled) }

        buttonExit = findViewById<View>(R.id.buttonExit) as Button
        buttonExit!!.setOnClickListener {
            setDetectEnabled(false)
            this@MainActivity.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    private fun setDetectEnabled(enable: Boolean) {
        detectEnabled = enable

        val intent = Intent(this, CallDetectService::class.java)
        if (enable) {
            // start detect service
            startService(intent)

            buttonToggleDetect!!.text = "Turn off"
            textViewDetectState!!.text = "Detecting"
        } else {
            // stop detect service
            stopService(intent)

            buttonToggleDetect!!.text = "Turn on"
            textViewDetectState!!.text = "Not detecting"
        }
    }

}