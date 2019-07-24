package com.wepa.callrecognizer.utils

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast


 fun Context.makeLongToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

 fun Context.makeLongToast(@StringRes textResource:Int ) = Toast.makeText(this, resources.getString(textResource), Toast.LENGTH_LONG).show()