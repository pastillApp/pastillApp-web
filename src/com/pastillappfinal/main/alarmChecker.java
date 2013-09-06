package com.pastillappfinal.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.pastillappfinal.main.R;




import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class alarmChecker extends Service  implements Runnable {



	public static final int APP_ID_NOTIFICATION = 0; 
	private NotificationManager mManager;

	private final int MSG_KEY_ES_IMPAR = 1;
	private final int MSG_KEY_ES_PAR = 2;


	/**
	 * M�todo del hilo as�ncrono, que obtiene un numero aleatorio y comprueba su paridad
	 */
	
	public void run() {

		//		final Random myRandom = new Random();


		//		int numeroAleatorio = myRandom.nextInt();

		//		Log.e("alarmChecker", "se genero un " + numeroAleatorio);
		String alarm = Context.ALARM_SERVICE;

		AlarmManager objAlarmManager = (AlarmManager) getSystemService(alarm);              

		Calendar objCalendar = Calendar.getInstance();
		objCalendar.set(Calendar.YEAR, 2013);
		objCalendar.set(Calendar.YEAR, objCalendar.get(Calendar.YEAR));
		objCalendar.set(Calendar.MONTH, 8);
		objCalendar.set(Calendar.DAY_OF_MONTH, 6);
		objCalendar.set(Calendar.HOUR_OF_DAY, 13);
		objCalendar.set(Calendar.MINUTE, 26);
		objCalendar.set(Calendar.SECOND, 0);
		objCalendar.set(Calendar.MILLISECOND, 0);
		objCalendar.set(Calendar.AM_PM, Calendar.AM); 


		Intent i= new Intent(this, retornoActivity.class);
		PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, 0,i,0 );


		long currentTime = new Date().getTime();
		long calTime = objCalendar.getTimeInMillis();		
		if (calTime <= currentTime && calTime > currentTime - 30000 )
		{
			Log.e("if","entra por el if");
			objCalendar = null;
			//Respondemos que es impar
			//			handler.sendEmptyMessage(MSG_KEY_ES_IMPAR);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//			PowerManager pm = null;
			//			PowerManager.WakeLock lck = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
			//			lck.acquire();
			//			i.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
			//	        i.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			//	        i.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
			//			this.getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);

			KeyguardManager manager = (KeyguardManager) getSystemService
					(Context.KEYGUARD_SERVICE);
//			KeyguardLock lock = manager.newKeyguardLock
//					("hh");

//			lock.disableKeyguard();


			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			r.play();
			this.startActivity(i);
			//objAlarmManager.set(AlarmManager.RTC_WAKEUP,objCalendar.getTimeInMillis(), alarmPendingIntent);



		}else
		{
			Log.e("else","entra por el else" + currentTime + "-----" + calTime);

			//Respondemos que es par
			//			handler.sendEmptyMessage(MSG_KEY_ES_PAR);
		}	
	}


	private Window getWindow() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Procesa eventos desde el hilo run
	 */
//	private Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what){
//			case MSG_KEY_ES_PAR: //Hemos obtenido un numero par
//				Notificar();
//				break;
//			}
//		}
//	};


	/**
	 * prepara y lanza la notificacion
	 */
	private void Notificar() {

		//Prepara la actividad que se abrira cuando el usuario pulse la notificacion
		Intent intentNot = new Intent(this, retornoActivity.class);

		//Prepara la notificacion
		Notification notification = new Notification(R.drawable.ic_launcher, "Hay cambios", System.currentTimeMillis());
		notification.setLatestEventInfo(this, getString(R.string.app_name), getString(R.string.notified), 
				PendingIntent.getActivity(this.getBaseContext(), 0, intentNot, PendingIntent.FLAG_CANCEL_CURRENT));

		//Le a�ade sonido
		notification.defaults |= Notification.DEFAULT_SOUND;
		//Le a�ade vibraci�n
		notification.defaults |= Notification.DEFAULT_VIBRATE;

		//Le a�ade luz mediante LED
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		//La notificaci�n se detendr� cuando el usuario pulse en ella
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		this.getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
		this.getWindow().addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
		this.getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.getWindow().addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		
		//Intenta establecer el color y el parpadeo de la bombilla lED
		try
		{
			notification.ledARGB = 0xff00ff00;
			notification.ledOnMS = 300;
			notification.ledOffMS = 1000;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		}catch(Exception ex)
		{
			//Nothing
		}

		//Lanza la notificaci�n
		mManager.notify(APP_ID_NOTIFICATION, notification);

	}	  

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		// Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();

		mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		// Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		//super.onStart(intent, startId);
		// Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();

		//Creamos un hilo que obtendra la informaci�n de forma as�ncrona
		Thread thread = new Thread(this);
		thread.start();
	}




	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		// Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
		return super.onUnbind(intent);
	}

}