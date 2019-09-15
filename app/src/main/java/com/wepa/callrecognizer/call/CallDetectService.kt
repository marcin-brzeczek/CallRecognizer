package com.wepa.callrecognizer.call

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.wepa.callrecognizer.R
import com.wepa.callrecognizer.main.MainActivity
import com.wepa.callrecognizer.model.ContactsRequest
import com.wepa.callrecognizer.network.ContactsApi
import com.wepa.callrecognizer.notifications.CallNotificationManager
import com.wepa.callrecognizer.utils.Statics
import com.wepa.callrecognizer.utils.makeLongToast
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

private const val kadafiContact = "+48511113959"

class CallDetectService : Service(), CallContract.ViewInterface {

    @Inject
    internal lateinit var contactsApi: ContactsApi

    private var callHelper: CallHelper? = null

    private lateinit var presenter: CallPresenter

    private lateinit var stateListener: PhoneStateListener

    private lateinit var telephonyManager: TelephonyManager

    private var mNotificationManager: NotificationManager? = null

    private var callNotificationManager: CallNotificationManager? = null

    private val mHandler = Handler()

    private val mTimerUpdateHandler = Handler()

    private val FOREGROUND_CHANNEL_ID = "foreground_channel_id"

    companion object {
        var state = Statics.STATE_SERVICE.NOT_INIT
    }

    private val TAG = CallDetectService::class.java.simpleName

    private val mTimerUpdateRunnable = object : Runnable {
        override fun run() {
            mNotificationManager?.notify(
                Statics.NOTIFICATION_ID_FOREGROUND_SERVICE,
                callNotificationManager?.prepareNotification(this@CallDetectService.application, mNotificationManager!!)
            )
            mTimerUpdateHandler.postDelayed(this, Statics.DELAY_UPDATE_NOTIFICATION_FOREGROUND_SERVICE)
        }
    }

    private val mDelayedShutdown = Runnable {
        stopForeground(true)
        stopSelf()
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        initializeData()
    }

    private fun initializeData() {
        presenter = CallPresenter(this, contactsApi)
        state = Statics.STATE_SERVICE.NOT_INIT
        callNotificationManager = CallNotificationManager()
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        telephonyManager = baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        stateListener = initStateListener()
        state = Statics.STATE_SERVICE.NOT_INIT
    }

    override fun displayError(message: String) {
        baseContext.makeLongToast("Error: $message")
        Timber.d("Error message: $message")
    }

    override fun displayContact(contact: ContactsRequest) {
        makeLongToast("${contact.data?.first()}")
    }

    private fun initStateListener() = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_RINGING)
                presenter.getContactbyPhoneNumber(incomingNumber)
        }

//    contact = contacts?.find {
//        (it.mobilePhone == incomingNumber ||
//                it.mobilePhone.drop(3) == incomingNumber ||
//                it.phone == incomingNumber)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE)
//        val res = super.onStartCommand(intent, flags, startId)
//        return res

        if (intent == null) {
            stopForeground(true)
            stopSelf()
            return Service.START_NOT_STICKY
        }


        when (intent.action) {
            Statics.ACTION.START_ACTION -> {
                Log.i(TAG, "Received start Intent ")
                state = Statics.STATE_SERVICE.PREPARE
                startForeground(
                    Statics.NOTIFICATION_ID_FOREGROUND_SERVICE,
                    callNotificationManager?.prepareNotification(this.application, mNotificationManager!!)
                )
            }

            Statics.ACTION.PAUSE_ACTION -> {
                state = Statics.STATE_SERVICE.PAUSE
                mNotificationManager!!.notify(
                    Statics.NOTIFICATION_ID_FOREGROUND_SERVICE,
                    callNotificationManager?.prepareNotification(this.application, mNotificationManager!!)
                )
                mHandler.postDelayed(mDelayedShutdown, Statics.DELAY_SHUTDOWN_FOREGROUND_SERVICE)
            }

            Statics.ACTION.PLAY_ACTION -> {
                state = Statics.STATE_SERVICE.PREPARE
                mNotificationManager!!.notify(
                    Statics.NOTIFICATION_ID_FOREGROUND_SERVICE,
                    callNotificationManager?.prepareNotification(this.application, mNotificationManager!!)
                )
            }

            Statics.ACTION.STOP_ACTION -> {
                Log.i(TAG, "Received Stop Intent")
                stopForeground(true)
                stopSelf()
            }

            else -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_NONE)
        presenter.stop()
        state = Statics.STATE_SERVICE.NOT_INIT
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    fun prepareNotification(
        context: Application,
        mNotificationManager: NotificationManager
    ): Notification {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && mNotificationManager.getNotificationChannel(
                FOREGROUND_CHANNEL_ID
            ) == null
        ) {
            // The user-visible name of the channel.
            val name = "Call Recogniser"
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

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val lPauseIntent = Intent(context, CallDetectService::class.java)
        lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION)
        val lPendingPauseIntent =
            PendingIntent.getService(context, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(context, CallDetectService::class.java)
        playIntent.setAction(Statics.ACTION.PLAY_ACTION)
        val lPendingPlayIntent =
            PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val lStopIntent = Intent(context, CallDetectService::class.java)
        lStopIntent.setAction(Statics.ACTION.STOP_ACTION)
        val lPendingStopIntent =
            PendingIntent.getService(context, 0, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val lRemoteViews = RemoteViews(context.getPackageName(), R.layout.radio_notification)
        lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_close_button, lPendingStopIntent)

        when (state) {

            Statics.STATE_SERVICE.PAUSE -> {
                lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.INVISIBLE)
                lRemoteViews.setOnClickPendingIntent(
                    R.id.ui_notification_player_button,
                    lPendingPlayIntent
                )
                lRemoteViews.setImageViewResource(
                    R.id.ui_notification_player_button,
                    R.drawable.ic_play_arrow_white
                )
            }

            Statics.STATE_SERVICE.PLAY -> {
                lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.INVISIBLE)
                lRemoteViews.setOnClickPendingIntent(
                    R.id.ui_notification_player_button,
                    lPendingPauseIntent
                )
                lRemoteViews.setImageViewResource(
                    R.id.ui_notification_player_button,
                    R.drawable.ic_pause_white
                )
            }

            Statics.STATE_SERVICE.PREPARE -> {
                lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.VISIBLE)
                lRemoteViews.setOnClickPendingIntent(
                    R.id.ui_notification_player_button,
                    lPendingPauseIntent
                )
                lRemoteViews.setImageViewResource(
                    R.id.ui_notification_player_button,
                    R.drawable.ic_pause_white
                )
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
}