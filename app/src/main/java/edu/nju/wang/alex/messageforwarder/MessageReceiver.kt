package edu.nju.wang.alex.messageforwarder

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.Toast

val FORWARD_NUMBER = "+8618502536524"
val smsManager = SmsManager.getDefault()
val SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED"
val NOTIFICATION_ID = 1228
class MessageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = if (SMS_ACTION.equals(intent.action) && intent.extras != null) intent.extras else return
        val messages = getMessage(bundle)
        messages.forEach {
            Toast.makeText(context, "${it.messageBody} *from* ${it.originatedAddress}", Toast.LENGTH_LONG).show()
            updateNotification(context, it.originatedAddress)
            sendMessage(context, it)
        }
    }
}

data class Message(val originatedAddress: String, val messageBody: String)
private fun getMessage(bundle: Bundle): MutableList<Message> {
    val messages = mutableListOf<Message>()
    var address: String? = null
    val body = StringBuilder()
    val pdusObj = bundle.get("pdus") as Array<Any>
    pdusObj.forEach {
        val currentMessage = SmsMessage.createFromPdu( it as ByteArray)
        if (address == null) {
            address = currentMessage.displayOriginatingAddress
            body.append(currentMessage.displayMessageBody)
            return@forEach
        }

        if (address != currentMessage.displayOriginatingAddress) {
            messages.add(Message(address ?: "unknown",body.toString()))
            address = currentMessage.displayOriginatingAddress
            body.setLength(0)
        } else {
            body.append(currentMessage.displayMessageBody)
        }
    }
    if (body.isNotEmpty()) {
        messages.add(Message(address ?: "unknown",body.toString()))
    }
    return messages
}

private fun sendMessage(context: Context, message: Message) {
    val dividedMessages = smsManager.divideMessage(message.messageBody)
    if (dividedMessages.isNotEmpty()) smsManager.sendMultipartTextMessage(FORWARD_NUMBER, null, dividedMessages, null, null)
}

private fun updateNotification(context: Context, address: String) {
    val notificationBuilder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Incoming/Forwarded SMS")
            .setContentText("New incoming SMS from $address")

    val resultIntent = Intent(context,MainActivity::class.java)

    val stackBuilder = TaskStackBuilder.create(context)
    stackBuilder.addParentStack(MainActivity::class.java)
    stackBuilder.addNextIntent(resultIntent)
    val resultPendingIntent = stackBuilder.getPendingIntent(
            0, PendingIntent.FLAG_UPDATE_CURRENT
    )
    notificationBuilder.setContentIntent(resultPendingIntent)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}