package com.wepa.callrecognizer.notifications

import android.app.*
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.view.View
import android.widget.RemoteViews
import com.wepa.callrecognizer.R
import com.wepa.callrecognizer.call.CallDetectService
import com.wepa.callrecognizer.main.MainActivity
import com.wepa.callrecognizer.utils.Statics

private val FOREGROUND_CHANNEL_ID = "foreground_channel_id"
private val TAG = CallDetectService::class.java.getSimpleName()
private var mStateService = Statics.STATE_SERVICE.NOT_INIT

private fun prepareNotification(context: Application, mNotificationManager:NotificationManager): Notification {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && mNotificationManager.getNotificationChannel(
            FOREGROUND_CHANNEL_ID
        ) == null
    ) {
        // The user-visible name of the channel.
        val name = context.getString(R.string.text_value_radio_notification)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance)
        mChannel.enableVibration(false)
        mNotificationManager.createNotificationChannel(mChannel)
    }
    val notificationIntent = Intent(context, MainActivity::class.java)
    notificationIntent.setAction(Statics.ACTION.MAIN_ACTION)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    } else {
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val lPauseIntent = Intent(context, CallDetectService::class.java)
    lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION)
    val lPendingPauseIntent = PendingIntent.getService(context, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val playIntent = Intent(context, CallDetectService::class.java)
    playIntent.setAction(Statics.ACTION.PLAY_ACTION)
    val lPendingPlayIntent = PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val lStopIntent = Intent(context, CallDetectService::class.java)
    lStopIntent.setAction(Statics.ACTION.STOP_ACTION)
    val lPendingStopIntent = PendingIntent.getService(context, 0, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val lRemoteViews = RemoteViews(context.getPackageName(), R.layout.radio_notification)
    lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_close_button, lPendingStopIntent)

    when (mStateService) {

        Statics.STATE_SERVICE.PAUSE -> {
            lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.INVISIBLE)
            lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_player_button, lPendingPlayIntent)
            lRemoteViews.setImageViewResource(R.id.ui_notification_player_button, R.drawable.ic_play_arrow_white)
        }

        Statics.STATE_SERVICE.PLAY -> {
            lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.INVISIBLE)
            lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_player_button, lPendingPauseIntent)
            lRemoteViews.setImageViewResource(R.id.ui_notification_player_button, R.drawable.ic_pause_white)
        }

        Statics.STATE_SERVICE.PREPARE -> {
            lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.VISIBLE)
            lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_player_button, lPendingPauseIntent)
            lRemoteViews.setImageViewResource(R.id.ui_notification_player_button, R.drawable.ic_pause_white)
        }
    }

    val lNotificationBuilder: NotificationCompat.Builder
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        lNotificationBuilder = NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
    } else {
        lNotificationBuilder = NotificationCompat.Builder(context)
    }
    lNotificationBuilder
        .setContent(lRemoteViews)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setOnlyAlertOnce(true)
        .setOngoing(true)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        lNotificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC)
    }
    return lNotificationBuilder.build()

}