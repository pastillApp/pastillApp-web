package com.pastillappfinal.main;

import java.util.Calendar;

import com.pastillappfinal.main.R;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class mainActivity extends Activity {
	private  WebView webview;
	private static PendingIntent pendingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivarAlarma();
		setContentView(R.layout.main);
        webview = (WebView) findViewById(R.id.webview);
        //webview.getSettings().setJavaScriptEnabled(true);
	    //webview.getSettings().setSupportZoom(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setBuiltInZoomControls(true);
        
        try{
        	Log.e("entroooooooooooooo", "aquiiiiiiiiii");
        	webview.loadUrl("http://pastillapp.heroku.com");
        	webview.setWebViewClient(new WebViewClient());
        }catch(Exception e){
        	e.printStackTrace();
        }
}
	
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}

	  private void ActivarAlarma()
	    {
	    	
	    	int comprobacionIntervaloSegundos = 30;
	    	
			   Intent myIntent = new Intent(mainActivity.this, alarmChecker.class);
			   pendingIntent = PendingIntent.getService(mainActivity.this, 0, myIntent, 0);

			   AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

			   Calendar calendar = Calendar.getInstance();
			   calendar.setTimeInMillis(System.currentTimeMillis());
			   calendar.add(Calendar.SECOND, 10);
			   alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), comprobacionIntervaloSegundos * 1000, pendingIntent);

			  
			   
			   Toast.makeText(mainActivity.this, "Alarma iniciada", Toast.LENGTH_LONG).show();
			   
			
	    }
	  
}
