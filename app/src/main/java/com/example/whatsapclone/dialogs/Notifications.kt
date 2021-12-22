package com.example.whatsapclone.dialogs

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.os.Build
import android.os.Message
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.whatsapclone.R
import com.example.whatsapclone.activity.messageActivity
import com.example.whatsapclone.encryption.AESCrptoChst
import com.example.whatsapclone.firebase.firebase.bitmap3
import com.example.whatsapclone.model.activeChats
import com.example.whatsapclone.model.messageModel

class Notifications() {
    val CHANNEL_ID ="message"
    lateinit var notification: Notification

    fun createNotification(user:String,message: String, context:Context, uid:String) {

        val art: Bitmap = bitmap3!!
        val homeIntent = PendingIntent.getActivity(context,0, Intent(context, messageActivity::class.java).putExtra("uid",uid),
            PendingIntent.FLAG_UPDATE_CURRENT)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationCompat = NotificationManagerCompat.from(context!!)
            notification = Notification.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.slogo2)
                .setContentTitle(user).setContentText(AESCrptoChst("lv39eptlvuhaqqsr").decrypt( message)).setLargeIcon(art).setAutoCancel(true)
                .setTicker(AESCrptoChst("lv39eptlvuhaqqsr").decrypt( message)).setContentIntent(homeIntent)
                .setShowWhen(false).setPriority(Notification.PRIORITY_HIGH).build()
            notificationCompat.notify(0, notification)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(CHANNEL_ID,0,notification)
        }
        else{
            val notificationCOmpat = NotificationManagerCompat.from(context!!)
            notification = NotificationCompat.Builder(context!!).setDefaults(Notification.DEFAULT_ALL) .setSmallIcon(
                R.drawable.slogo2)
                .setContentTitle(user).setContentText("You have a new message from $user").setLargeIcon(art)
                .setTicker("You have a new message from $user").setStyle(NotificationCompat.BigTextStyle().bigText(AESCrptoChst("lv39eptlvuhaqqsr").decrypt( message)).setBigContentTitle(user))
                .setShowWhen(false).setPriority(NotificationCompat.PRIORITY_DEFAULT) .build()
            notificationCOmpat.notify(1, notification!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(CHANNEL_ID,0,notification)
            else
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) .notify(CHANNEL_ID.hashCode(),notification)
        }


    }
}