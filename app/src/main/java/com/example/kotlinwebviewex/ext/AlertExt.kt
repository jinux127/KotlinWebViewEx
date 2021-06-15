package com.example.kotlinwebviewex.ext

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kotlinwebviewex.R

fun Context.showAlertDialogApply(block: AlertDialog.Builder.() -> Unit) {
    AlertDialog.Builder(this).apply(block).show()
}

inline fun Context.showOverlayDialog(
    activity: Activity,
    title: String = getString(R.string.intro_candraw_title),
    cancelText: String = getString(R.string.cancel),
    crossinline edit: () -> Unit
) {
    showAlertDialogApply {
        setTitle(title)
        setMessage(getString(R.string.intro_candraw_body))
        setNegativeButton(cancelText) { d, _ ->
            d.dismiss()
            Toast.makeText(
                this@showOverlayDialog,
                getString(R.string.intro_candraw_cancel),
                Toast.LENGTH_LONG
            ).show()
            activity.finish()
        }
        setPositiveButton(getString(R.string.ok)) { d, _ ->
            edit.invoke()
            d.dismiss()
        }
        setCancelable(false)
    }
}

inline fun Context.showDenyPermissionDialog(
    activity: Activity,
    title: String = getString(R.string.permission_msg_deny_title),
    msg : String = "",
    cancelText: String = getString(R.string.cancel),
    crossinline edit: () -> Unit
) {
    showAlertDialogApply {
        setTitle(title)
        setMessage(msg)
        setNegativeButton(cancelText) { d, _ ->
            d.dismiss()
            Toast.makeText(
                this@showDenyPermissionDialog,
                getString(R.string.permission_msg_deny_toast),
                Toast.LENGTH_LONG
            ).show()
            activity.finish()
        }
        setPositiveButton(getString(R.string.ok)) { d, _ ->
            edit.invoke()
            d.dismiss()
        }
        setCancelable(false)
    }
}

