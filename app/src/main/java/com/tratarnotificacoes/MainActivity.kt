package com.tratarnotificacoes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.EditText
import java.io.File

class MainActivity : Activity() {

    private lateinit var txt: EditText

    private val handler = Handler(Looper.getMainLooper())

    private val atualizador = object : Runnable {
        override fun run() {

            try {

                val conteudo = File(
                    applicationContext.filesDir,
                    "http_status_log.txt"
                ).readText()

                if (txt.text.toString() != conteudo) {

                    txt.setText(conteudo)

                    // rola automaticamente até o final
                    txt.setSelection(txt.text.length)

                }

            } catch (_: Exception) {

                txt.setText("Log vazio")

            }

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        txt = EditText(this)

        txt.isFocusable = false
        txt.isFocusableInTouchMode = false
        txt.isClickable = false
        txt.isLongClickable = true

        txt.setTextIsSelectable(true)

        txt.layoutParams =
            android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT
            )

        setContentView(txt)

        if (!notificationPermissionGranted()) {
            startActivity(
                Intent(
                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(atualizador)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(atualizador)
    }

    private fun notificationPermissionGranted(): Boolean {

        val enabledListeners =
            Settings.Secure.getString(
                contentResolver,
                "enabled_notification_listeners"
            )

        return enabledListeners != null &&
               enabledListeners.contains(packageName)
    }
}