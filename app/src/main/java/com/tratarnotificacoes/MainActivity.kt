package com.tratarnotificacoes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.ScrollView
import android.widget.TextView
import java.io.File
import android.view.ViewGroup

class MainActivity : Activity() {

	private lateinit var txt: TextView
	private lateinit var scroll: ScrollView

    private val handler = Handler(Looper.getMainLooper())

    private val atualizador = object : Runnable {
	
        override fun run() {

            try {

                val conteudo = File(
				
                    applicationContext.filesDir,
                    "http_status_log.txt"
					
                ).readText()

                if (txt.text.toString() != conteudo) {
				
                    txt.text = conteudo
					
                    //Rola automaticamente para o fim.
                    scroll.post {
					
                        scroll.fullScroll(android.view.View.FOCUS_DOWN)
						
                    }
					
                }

            } catch (_: Exception) {

                txt.text = "Ainda não foram identificadas notificações válidas"

            }

            handler.postDelayed(this, 1000)
			
        }
		
    }

    override fun onCreate(savedInstanceState: Bundle?) {
	
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)


	
        super.onCreate(savedInstanceState)



        txt = TextView(this)

		txt.textSize = 18f	

		txt.layoutParams = ViewGroup.LayoutParams(
		
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
			
		)
		


        scroll = ScrollView(this)
		
		scroll.setPadding(50, 50, 50, 50)
		
		
		
        scroll.addView(txt)

        setContentView(scroll)



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

        val enabledListeners = Settings.Secure.getString(
	
			contentResolver,
			"enabled_notification_listeners"
			
		)

        return enabledListeners != null && enabledListeners.contains(packageName)
		
    }
	
}