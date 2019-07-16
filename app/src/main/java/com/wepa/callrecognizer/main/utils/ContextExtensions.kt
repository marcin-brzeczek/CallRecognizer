package com.wepa.callrecognizer.main.utils

import android.content.Context
import android.widget.Toast


 fun Context.makeLongToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()