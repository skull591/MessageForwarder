package edu.nju.wang.alex.messageforwarder

import android.app.NotificationChannel
import android.app.NotificationManager

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