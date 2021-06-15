package com.example.kotlinwebviewex.ext

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.inspector.IntFlagMapping
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.kotlinwebviewex.R
import com.example.kotlinwebviewex.activity.MainActivity
import com.example.kotlinwebviewex.model.RxBusData
import com.example.kotlinwebviewex.utils.*


fun AppCompatActivity.activityResult() =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            AppCompatActivity.RESULT_OK -> {

            }
            AppCompatActivity.RESULT_CANCELED -> {
                RxBus.getSubject().onNext(
                    RxBusData(
                        MainActivity::class.java.simpleName,
                        RXBUS_TYPE_PERMISSION
                    )
                )
            } //overlay 대응}
        }
    }

fun AppCompatActivity.permissionResult() =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
        val notGrantedPermissions = ArrayList<String>()
        val denyPermission = ArrayList<String>()
        //퍼미션전체 반복문
        permission.entries.forEach {
            if (!it.value) {// 퍼미션의 value가 없을경우
                notGrantedPermissions.add(it.key) // notGrantedPermissions에 퍼미션key 추가
            }
        }
        if (notGrantedPermissions.size > 0) {//허용되지않은 퍼미션의 사이즈가 0 이상일경우
            notGrantedPermissions.forEach {
                if (shouldShowRequestPermissionRationale(it)) { //사용자가 이전에 권한 요청을 거부한경우에 true반환
                    RxBus.getSubject().onNext(
                        RxBusData(
                            MainActivity::class.java.simpleName,
                            RXBUS_TYPE_PERMISSION //다시 퍼미선 요청 RxBus
                        )
                    )
                    return@registerForActivityResult // 1개라도 거부가 있을 경우 permission 을 다시 요청 함
                } else {
                    // 2번이상 물어보면 더 이상 permission 대화창이 뜨지 않음 dialog로 처리해야함
                    denyPermission.add(it) // deny 등록
                }
            }
            showDenyPermissionDialog(this,msg = denyPermission.run {
                val msg = StringBuffer()
                forEach {
                    if (it.contains("FINE_LOCATION")) return@forEach // 위치권한은 2개지만 대화창은 1개로 표시되기 때문에 하나는 continue
                    when {
                        it.contains("LOCATION") -> msg.append("${getString(R.string.permission_msg_location)} ")
                        it.contains("NUMBERS") || it.contains("PHONE_STATE")
                        -> msg.append(
                            "${getString(R.string.permission_msg_phone)} "
                        )
                    }
                }
                msg.append("\n\n${getString(R.string.permission_msg_deny_msg)}")
                msg.toString()
            },edit = {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).run {
                    data = Uri.parse("package:$packageName")
                    startActivity(this)
                }
            } )
        } else {
            RxBus.getSubject().onNext(
                RxBusData(
                    MainActivity::class.java.simpleName,
                    RXBUS_TYPE_INTRO_TO_MAIN
                )
            )
        }
    }


fun AppCompatActivity.toastShow(
    msg: Any?,
    duration: Int = Toast.LENGTH_SHORT,
    unit: (() -> Unit)? = null
) {
    when (msg) {
        is String -> msg
        is Int -> getString(msg)
        else -> null
    }?.let { Toast.makeText(applicationContext, it, duration).show() }
    unit?.invoke()
}

fun AppCompatActivity.checkPermission(
    activity: ActivityResultLauncher<Intent>,
    permissionResult: ActivityResultLauncher<Array<String>>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
        showOverlayDialog(this, edit = {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            ).run {
                putExtra("action", Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                activity.launch(this)
            }
        })
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkPermissions(Q_USES_PERMISSIONS, permissionResult)) {
                //nextHandler.postDelayed(mRunnable, delay.toLong())
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (checkPermissions(O_USES_PERMISSIONS, permissionResult)) {
                // nextHandler.postDelayed(mRunnable, delay.toLong())
            }
        } else {
            if (checkPermissions(USES_PERMISSIONS, permissionResult)) {
                // nextHandler.postDelayed(mRunnable, delay.toLong())
            }
        }
    }
}

fun AppCompatActivity.checkPermissions(
    permissions: Array<String>,
    permissionResult: ActivityResultLauncher<Array<String>>
): Boolean {
    var isGranted = false
    val notGrantedPermissions = ArrayList<String>()
    for (permission in permissions) {
        val result = ContextCompat.checkSelfPermission(this, permission)
        if (result != PackageManager.PERMISSION_GRANTED) {
            notGrantedPermissions.add(permission)
        }
    }
    if (notGrantedPermissions.size < 1) {
        isGranted = true
    } else {
        permissionResult.launch(notGrantedPermissions.toTypedArray())
    }
    return isGranted
}

fun AppCompatActivity.createNoti(
    notifiactionId: Int,
    title: String = "제목",
    content: String = "내용",
    CHANNEL_ID: String = "CH",
    onGoing:Boolean = true
) {
    val intent = Intent(this,MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)

    var builder= NotificationCompat.Builder(this,CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(onGoing)
        .setContentIntent(pendingIntent)
    createNotificationChannel(CHANNEL_ID)

    with(NotificationManagerCompat.from(this)){
        notify(notifiactionId,builder.build())
    }
}


fun AppCompatActivity.createNotificationChannel(
    CHANNEL_ID:String
){
    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
            description = descriptionText
        }
        channel.enableVibration(true)

        val notiManager: NotificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notiManager.createNotificationChannel(channel)


    }
}
