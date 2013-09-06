package com.pastillappfinal.main;



import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class retornoActivity extends Activity {
	
	 TelephonyManager manager;
	 StatePhoneReceiver myPhoneStateListener;
	 boolean callFromApp=false; // To control the call has been made from the application
	 boolean callFromOffHook=false; // To control the change to idle state is from the app call
	 
	public class hilo extends Thread{
		boolean finish = false;
		public void run(){
			Long start = new Date().getTime();
			while(!finish && new Date().getTime() < start+3000 ){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if(!finish){
			//"llamada"
				Log.e("llamada","llamandooooo");
				Intent call = new Intent(Intent.ACTION_CALL);
				manager.listen(myPhoneStateListener,
					    PhoneStateListener.LISTEN_CALL_STATE); // start listening to the phone changes
					    callFromApp=true;
//				call.setData(Uri.parse("tel:" + findViewByid(R.id.textView4).getText());
				call.setData(Uri.parse("tel:" + "07763502890"));
				startActivity(call);
			}
			
		}
		public void finish(){
			 this.finish = true;
			}
	}
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPhoneStateListener = new StatePhoneReceiver(this);
        manager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        final hilo t = new hilo ();
        t.start();
        setContentView(R.layout.retorno);
        ImageButton btn1 = (ImageButton) findViewById(R.id.imageButton1);
        btn1.setOnClickListener(new OnClickListener() {
            @Override
         public void onClick(View arg0) {
          // TODO Auto-generated method stub
            	t.finish();
//            	Intent sms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "07763502890"));
            	SmsManager sms = SmsManager.getDefault();
//            	sms.putExtra("sms_body", "Puxa Sporting!!");
//            	startActivity(sms);
            	sms.sendTextMessage("07763502890", null, "Puxa Sporting!!",null,null);
            	finish();
             }
        });
        
    }
    
    public class StatePhoneReceiver extends PhoneStateListener {
	     Context context;
	     public StatePhoneReceiver(Context context) {
	         this.context = context;
	     }
	 
	     @Override
	     public void onCallStateChanged(int state, String incomingNumber) {
	         super.onCallStateChanged(state, incomingNumber);
	         
	         switch (state) {
	         
	         case TelephonyManager.CALL_STATE_OFFHOOK: //Call is established
	          if (callFromApp) {
	              callFromApp=false;
	              callFromOffHook=true;
	                   
	              try {
	                Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
	              } catch (InterruptedException e) {
	              }
	           
	              //Activate loudspeaker
	              AudioManager audioManager = (AudioManager)
	                                          getSystemService(Context.AUDIO_SERVICE);
	              audioManager.setMode(AudioManager.MODE_IN_CALL);
	              audioManager.setSpeakerphoneOn(true);
	           }
	           break;
	         
	        case TelephonyManager.CALL_STATE_IDLE: //Call is finished
	          if (callFromOffHook) {
	                callFromOffHook=false;
	                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	                audioManager.setMode(AudioManager.MODE_NORMAL); //Deactivate loudspeaker
	                manager.listen(myPhoneStateListener, // Remove listener
	                      PhoneStateListener.LISTEN_NONE);
	             }
	          break;
	         }
	     }

    }    
}
