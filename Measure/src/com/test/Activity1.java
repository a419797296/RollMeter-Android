package com.test;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
// this is the main interface, contains all the buttons
public class Activity1 extends Activity {
	
	private String TAG="activity1";
    private TextView MTitle;
    private ImageButton buttonp;
    private ImageButton buttons;
    private ImageButton buttonm;
    private ImageButton buttonh;
    private ImageButton buttonw;
    private ImageButton buttonlogo;

    private ToDoDB myToDoDB;
    private Cursor myPCursor;
    private final static String TABLE_NAME_Language = "language_infor";
    String Lanugage="中文";
    private Configuration config;
    private DisplayMetrics dm;
    private Resources resources;
    private boolean LanIsCn;
  
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

        //chinese title set
        MTitle=(TextView)findViewById(R.id.textViewTitleChinese);
        // 设定阴影(柔边, X 轴位移, Y 轴位移, 阴影颜色)
        MTitle.setShadowLayer(3F, 3F, 3F, R.drawable.SecondTxtClor);

        //make sure the screen is on all the time
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //default language just follow the systerm
        resources =getResources();//获得res资源对象
        config = resources.getConfiguration();//获得设置对象
        dm = resources.getDisplayMetrics();
        Locale curLocale = getResources().getConfiguration().locale;
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            LanIsCn= true;
        }else{
            LanIsCn= false;
        }

        resources =getResources();//获得res资源对象
        config = resources.getConfiguration();//获得设置对象
        dm = resources.getDisplayMetrics();
        myToDoDB = new ToDoDB(this);
        myPCursor = myToDoDB.select(TABLE_NAME_Language);
        if (myPCursor.getCount()>0){
            myPCursor.moveToFirst();
            Lanugage= myPCursor.getString(1);
            if(Lanugage.equals("中文")){
                config.locale = Locale.SIMPLIFIED_CHINESE;
                LanIsCn= true;
            }else{
                config.locale = Locale.ENGLISH;
                LanIsCn= false;
            }
            resources.updateConfiguration(config, dm);
        }

        buttonp = (ImageButton)findViewById(R.id.imageButtonPL);
        if(LanIsCn){
            buttonp.setBackgroundResource(R.drawable.bparameter);
        } else
        {
            buttonp.setBackgroundResource(R.drawable.beparameter);
        }
        //buttonp.setClickable(true);
        buttonp.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity1.this, ParameterSet.class);
                startActivity(intent);
                //Activity1.this.finish();
            }
        });
        buttonp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(LanIsCn){
                        buttonp.setBackgroundResource(R.drawable.bpparameter);
                    } else
                    {
                        buttonp.setBackgroundResource(R.drawable.bepparameter);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(LanIsCn){
                        buttonp.setBackgroundResource(R.drawable.bparameter);
                    } else
                    {
                        buttonp.setBackgroundResource(R.drawable.beparameter);
                    }
                }
                return false;
            }
        });

        buttons = (ImageButton)findViewById(R.id.imageButtonSL);
        if(LanIsCn){
            buttons.setBackgroundResource(R.drawable.bsetting);
        } else
        {
            buttons.setBackgroundResource(R.drawable.besettings);
        }
        buttons.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity1.this, Setting.class);
                startActivity(intent);
                //Activity1.this.finish();
            }
        });
        buttons.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(LanIsCn){
                        buttons.setBackgroundResource(R.drawable.bpsetting);
                    } else
                    {
                        buttons.setBackgroundResource(R.drawable.bepsettings);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(LanIsCn){
                        buttons.setBackgroundResource(R.drawable.bsetting);
                    } else
                    {
                        buttons.setBackgroundResource(R.drawable.besettings);
                    }
                }
                return false;
            }
        });
        buttonm = (ImageButton)findViewById(R.id.imageButtonML);
        if(LanIsCn){
            buttonm.setBackgroundResource(R.drawable.bmeasure);
        } else
        {
            buttonm.setBackgroundResource(R.drawable.bemeasurement);
        }
        buttonm.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity1.this, Measure.class);
                startActivity(intent);
                //Activity1.this.finish();
            }
        });
        buttonm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(LanIsCn){
                        buttonm.setBackgroundResource(R.drawable.bpmeasure);
                    } else
                    {
                        buttonm.setBackgroundResource(R.drawable.bepmeasurement);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(LanIsCn){
                        buttonm.setBackgroundResource(R.drawable.bmeasure);
                    } else
                    {
                        buttonm.setBackgroundResource(R.drawable.bemeasurement);
                    }
                }
                return false;
            }
        });
        buttonh = (ImageButton)findViewById(R.id.imageButtonHL);
        if(LanIsCn){
            buttonh.setBackgroundResource(R.drawable.bhistory);
        } else
        {
            buttonh.setBackgroundResource(R.drawable.behistory);
        }
        buttonh.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity1.this, HistoryChart.class);
                startActivity(intent);
                // Activity1.this.finish();
            }
        });
        buttonh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(LanIsCn){
                        buttonh.setBackgroundResource(R.drawable.bphistory);
                    } else
                    {
                        buttonh.setBackgroundResource(R.drawable.bephistory);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(LanIsCn){
                        buttonh.setBackgroundResource(R.drawable.bhistory);
                    } else
                    {
                        buttonh.setBackgroundResource(R.drawable.behistory);
                    }
                }
                return false;
            }
        });
        buttonw = (ImageButton)findViewById(R.id.imageButtonWL);
        if(LanIsCn){
            buttonw.setBackgroundResource(R.drawable.bhelp);
        } else
        {
            buttonw.setBackgroundResource(R.drawable.behelp);
        }
        buttonw.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(Activity1.this, HelpHelp.class);
                startActivity(intent);
                //Activity1.this.finish();
            }
        });
        buttonw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (LanIsCn) {
                        buttonw.setBackgroundResource(R.drawable.bphelp);
                    } else {
                        buttonw.setBackgroundResource(R.drawable.bephelp);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (LanIsCn) {
                        buttonw.setBackgroundResource(R.drawable.bhelp);
                    } else {
                        buttonw.setBackgroundResource(R.drawable.behelp);
                    }
                }
                return false;
            }
        });

        buttonlogo = (ImageButton)findViewById(R.id.imageViewLogo);

        buttonlogo.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //Intent intent = new Intent();
                //intent.setClass(Activity1.this, Activity2.class);
                //startActivity(intent);
                //Activity1.this.finish();
            }
        });
        buttonlogo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                /*
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    buttonlogo.setBackgroundResource(R.drawable.babout);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    buttonlogo.setBackgroundResource(R.drawable.bpabout);

                }*/
                return false;
            }
        });
        Log.v(TAG, "onCreate");    
    }
    
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
        onCreate(null);
    	Log.v(TAG, "onResume");
    }
    
    public void onStop(){
    	super.onStop();
        //clear screen on
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    public void onRestart(){
    	super.onRestart();
    	Log.v(TAG, "onRestart");
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	Log.v(TAG, "onDestroy");    	
    }
    private long exitTime = 0;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), getString(R.string.onemore_return), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
          
}