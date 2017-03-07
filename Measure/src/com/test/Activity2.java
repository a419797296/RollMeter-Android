package com.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.Locale;

public class Activity2 extends Activity {

	private String TAG = "activity2";
	private ImageButton ReturnLogo;
	private RelativeLayout ActionbarLogo;
    private RelativeLayout ActionbarLogoB;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity2);

        ReturnLogo = (ImageButton)findViewById(R.id.imageButtonReturnLogo);
        ReturnLogo.setClickable(false);
        ActionbarLogoB.setClickable(true);

        ActionbarLogoB=(RelativeLayout)findViewById(R.id.relativeLayoutTopLogoB);
        ActionbarLogoB.setOnClickListener(ReturnLogoBC);
        ActionbarLogoB.setOnTouchListener(ReturnLogoBP);

        ActionbarLogo=(RelativeLayout)findViewById(R.id.relativeLayoutTopLogo);
        Locale curLocale = getResources().getConfiguration().locale;
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            ActionbarLogo.setBackgroundResource(R.drawable.aabout);
        } else {
            ActionbarLogo.setBackgroundResource(R.drawable.aeabout);
        }
		Log.v(TAG, "onCreate");
	}

    public View.OnClickListener ReturnLogoBC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Activity2.this, Activity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Activity2.this.finish();
            return;
        }
    };
    public View.OnTouchListener ReturnLogoBP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                ReturnLogo.setBackgroundResource(R.drawable.returnbp);
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                ReturnLogo.setBackgroundResource(R.drawable.returnb);
            }
            return false;
        }
    };

	  public void onStart(){
	    	super.onStart();
	    	Log.v(TAG, "onStart");
	    }
	    
	    public void onPause(){
	    	super.onPause();
	    	Log.v(TAG, "onPause");
	    }
	    
	    public void onResume(){
	    	super.onResume();
	    	Log.v(TAG, "onResume");
	    }
	    
	    public void onStop(){
	    	super.onStop();
	    	Log.v(TAG, "onStop");
	    }
	    
	    public void onRestart(){
	    	super.onRestart();
	    	Log.v(TAG, "onRestart");
	    }
	    
	    public void onDestroy(){
	    	super.onDestroy();
	    	Log.v(TAG, "onDestroy");    	
	    }

}
