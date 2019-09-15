package com.wepa.callrecognizer.network

import android.content.Context
import android.net.ConnectivityManager

internal object NetworkHelper {

    fun isInternetAvailable(pContext: Context?): Boolean {
        if (pContext == null) {
            return false
        }
        val cm = pContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}