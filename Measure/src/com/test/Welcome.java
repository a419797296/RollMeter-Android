package com.test;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
//start page with animation
public class Welcome extends Activity {
    private Intent intent = new Intent("com.angel.Android.MUSIC");
    private ToDoDB myToDoDB;
    private Cursor myPCursor;
    private final static String TABLE_NAME_Sound = "sound_infor";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        myToDoDB = new ToDoDB(this);
        myPCursor = myToDoDB.select(TABLE_NAME_Sound);
        if(myPCursor.getCount()>0){
            myPCursor.moveToFirst();
            String SoundN=myPCursor.getString(1);
            if (SoundN.equals("true")){
                startService(intent);
            }
        }else {
            startService(intent);
        }

        Animation rotateAnimation=new RotateAnimation(0, -360);
        rotateAnimation.setDuration(2000);//设置动画持续时间为3秒
        rotateAnimation.setFillAfter(true);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
        View imgShow = (ImageView)findViewById(R.id.imageView);
        imgShow.startAnimation(rotateAnimation);

        Animation rotateAnimation2=new RotateAnimation(0, 360);
        rotateAnimation2.setDuration(2000);//设置动画持续时间为3秒
        rotateAnimation2.setFillAfter(true);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
        View imgShow2 = (ImageView)findViewById(R.id.imageView2);
        imgShow2.startAnimation(rotateAnimation2);

        Animation translateAnimation3=new TranslateAnimation(0, 300, 0, 0);
        translateAnimation3.setDuration(2000);//设置动画持续时间为3秒
        translateAnimation3.setInterpolator(this, android.R.anim.cycle_interpolator);//设置动画插入器
        translateAnimation3.setFillAfter(true);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
        View imgShow3 = (ImageView)findViewById(R.id.imageView3);
        imgShow3.startAnimation(translateAnimation3);


        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 3000);//3秒跳转
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    private static final int GOTO_MAIN_ACTIVITY = 0;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent();
                    intent.setClass(Welcome.this, Activity1.class);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDestroy(){
        super.onDestroy();
       stopService(intent);
        //Log.v(TAG, "onDestroy");
    }
}
