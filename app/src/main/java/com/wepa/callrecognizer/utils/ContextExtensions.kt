package com.wepa.callrecognizer.utils

import android.content.Context
import android.os.CountDownTimer
import android.support.annotation.StringRes
import android.widget.Toast

 fun Context.makeLongToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

 fun Context.makeLongToast(@StringRes textResource:Int ) = Toast.makeText(this, resources.getString(textResource), Toast.LENGTH_LONG).show()

fun Context.showToast(toastMessage:String, duration:Long) {

 val mToastToShow:Toast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG)
 val toastCountDown: CountDownTimer

 toastCountDown = object : CountDownTimer(duration, 1000 /*Tick duration*/) {
  override fun onTick(millisUntilFinished: Long) {
   mToastToShow.show()
  }

  override fun onFinish() {
   mToastToShow.cancel()
  }
 }

 mToastToShow.show()
 toastCountDown.start()
}
