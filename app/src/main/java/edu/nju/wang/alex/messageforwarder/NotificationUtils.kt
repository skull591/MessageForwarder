package edu.nju.wang.alex.messageforwarder

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews

/**
 * Created by alex-wang on 17-12-24.
 */
//
//val SMS_NOTIFICATION_CHANNEL_ID : String
//    get() {
//        if (_sms_notification_channel_id == null) {
//            _sms_notification_channel_id = "edu.nju.wang.alex.messageforwarder.notification.channel.receivesms"
//            createChannel()
//        }
//        return _sms_notification_channel_id ?: throw AssertionError("set to null somehow")
//    }
//private var _sms_notification_channel_id: String? = null
//private val SMS_CHANNEL_NAME = "SMS Notification"
//private fun createChannel() {
//    val smsChannel = NotificationChannel(_sms_notification_channel_id
//            , SMS_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
//}
val FROM_NOTI_ACTION = "edu.nju.alex.wang.from_intent"
/**
 * using remoteViews
 * */
fun updateNotification(context: Context, address: String, message: String) {
    val notificationBuilder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Incoming/Forwarded SMS")
            .setContentText("New incoming SMS from $address")
            .setAutoCancel(true)

    val resultIntent = Intent(context,MainActivity::class.java)
    resultIntent.action = FROM_NOTI_ACTION

    val stackBuilder = TaskStackBuilder.create(context)
    stackBuilder.addParentStack(MainActivity::class.java)
    stackBuilder.addNextIntent(resultIntent)
    val resultPendingIntent = stackBuilder.getPendingIntent(
            0, PendingIntent.FLAG_UPDATE_CURRENT
    )
    //create the expanded view
    val expandedView = RemoteViews(context.packageName, R.layout.mnotification_layout)
    expandedView.setTextViewText(R.id.count, "There has been $notificationCount new SMS")
    expandedView.setTextViewText(R.id.address, "Latest from $address")
    expandedView.setTextViewText(R.id.sms_body, message)
    val resendIntent = Intent()
    resendIntent.action = RESEND_ACTION
    resendIntent.putExtra(RESEND_ADDRESS_KEY, address)
    resendIntent.putExtra(RESEND_BODY_KEY, message)
    val resendPendingIntent = PendingIntent.getBroadcast(
            context, 0, resendIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )
    expandedView.setOnClickPendingIntent(R.id.resent_button, resendPendingIntent)


    notificationBuilder.setContentIntent(resultPendingIntent)
    notificationBuilder.setCustomBigContentView(expandedView)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}

/**
 * using bigview
 * */

fun updateBigNotification(context: Context, address: String, message: String) {
    val resultIntent = Intent(context,MainActivity::class.java)
    resultIntent.action = FROM_NOTI_ACTION

    val stackBuilder = TaskStackBuilder.create(context)
    stackBuilder.addParentStack(MainActivity::class.java)
    stackBuilder.addNextIntent(resultIntent)
    val resultPendingIntent = stackBuilder.getPendingIntent(
            0, PendingIntent.FLAG_UPDATE_CURRENT
    )

    val resendIntent = Intent()
    resendIntent.action = RESEND_ACTION
    resendIntent.putExtra(RESEND_ADDRESS_KEY, address)
    resendIntent.putExtra(RESEND_BODY_KEY, message)
    val resendPendingIntent = PendingIntent.getBroadcast(
            context, 0, resendIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationBuilder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Incoming/Forwarded SMS")
            .setContentText("There are $notificationCount new SMS")
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(2)
            .setContentIntent(resultPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("There are $notificationCount new SMS, Latest one:\n$message"))
            .addAction(R.drawable.ic_dashboard_black_24dp,
                    context.getString(R.string.resend), resendPendingIntent)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}
