package com.test;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.widget.view.SlideSwitchView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
//parameter setting
public class Setting extends Activity {


    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothServiceS bluetoothService = null;
    public static final String TOAST = "toast";

    private Configuration config;
    private DisplayMetrics dm;
    private Resources resources;
    public String Languages;
    private String mConnectedDeviceName = null;
    public static final String DEVICE_NAME = "device_name";
    private String BlueAddress="";
    private String BluetoothName=null;
    //database
    private ToDoDB myToDoDB;
    private Cursor myPCursor;
    private final static String TABLE_NAME_BLUET = "bluetooth_infor";
    private final static String TABLE_NAME_Language = "language_infor";
    private final static String TABLE_NAME_Sound = "sound_infor";
    private final static String TABLE_NAME_Data = "measure_data";
    private final static String TABLE_NAME_Infor = "measure_infor";
    private final static String TABLE_NAME_ENCODE = "Encode_infor";
    LinearLayout BlueButon;
    LinearLayout LangButon;
    private TextView BlueArrow;
    private TextView LangArrow;
    private TextView BlueName;
    private TextView LangName;
    private TextView EncodeName;
    private TextView BatteryP;
    private boolean ChorNot;
    private int passed = 0;

    private ImageView blue3;
    private ImageView BatteryButton;

    private Object lock = new Object();
    private ImageButton ReturnS;
    private RelativeLayout ActionbarS;
    private RelativeLayout BackS;

    private SlideSwitchView mSlideSwitchView;
    private String SoundOn="true";

    private  ProgressDialog dialog;
    private int index = 0;
    private MyHandler handler = null;

    private Message message = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        /*
        //actionbar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        Drawable myDrawable = getResources().getDrawable(R.drawable.asetting);
        actionBar.setBackgroundDrawable(myDrawable);
        //make sure the screen is on all the time
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        */

        ActionbarS=(RelativeLayout)findViewById(R.id.relativeLayoutTopS);
        BackS=(RelativeLayout)findViewById(R.id.relativeLayoutSetting);

        bluetoothService = new BluetoothServiceS(Setting.this, mHandler);
        //imagebutton
        BlueButon=(LinearLayout)findViewById(R.id.BlueSButton);
        BlueButon.setClickable(true);
        BlueButon.setOnClickListener(BlueSC);
        BlueButon.setOnTouchListener(BlueSP);

        LangButon=(LinearLayout)findViewById(R.id.LanSButton);
        LangButon.setClickable(true);
        LangButon.setOnClickListener(LanSC);
        LangButon.setOnTouchListener(LanSP);

        BlueArrow=(TextView)findViewById(R.id.BlueArrow);
        LangArrow=(TextView)findViewById(R.id.LangArrow);
        BlueName=(TextView)findViewById(R.id.textBlue);
        LangName=(TextView)findViewById(R.id.textLang);
        BatteryP=(TextView)findViewById(R.id.textBatteryN); //battery
        EncodeName=(TextView)findViewById(R.id.EncodeText); //encode

        ReturnS = (ImageButton)findViewById(R.id.imageButtonReturnS);
        ReturnS.setOnClickListener(ReturnSBC);
        ReturnS.setOnTouchListener(ReturnSBP);

        blue3=(ImageView) findViewById(R.id.imageViewBlueFS);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BatteryButton=(ImageView) findViewById(R.id.imageButtonBattery);

        ChorNot =true;
        String ENstr = getString(R.string.ENEN);
        String CNstr = getString(R.string.ENC);
        resources =getResources();//获得res资源对象
        config = resources.getConfiguration();//获得设置对象
        dm = resources.getDisplayMetrics();
        Locale curLocale = getResources().getConfiguration().locale;
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            LangName.setText(CNstr);
            ActionbarS.setBackgroundResource(R.drawable.asetting);
            BackS.setBackgroundResource(R.drawable.settingb);
        } else {
            LangName.setText(ENstr);
            ActionbarS.setBackgroundResource(R.drawable.aesetting);
            BackS.setBackgroundResource(R.drawable.settingbe);
        }
        //database
        myToDoDB = new ToDoDB(this);
        myPCursor = myToDoDB.selectBlue();
        if (myPCursor == null) {

        } else {
            if (myPCursor.getCount() > 0) {
                myPCursor.moveToFirst();
                BlueAddress = myPCursor.getString(1);
                BluetoothName = myPCursor.getString(2);
                BlueName.setText(BluetoothName);
            }
        }

        myPCursor = myToDoDB.select(TABLE_NAME_ENCODE);
        if(myPCursor.getCount()>0){
            myPCursor.moveToFirst();
            String Encode=myPCursor.getString(1);
            EncodeName.setText(Encode);
        }

        EncodeName.setOnClickListener(EditC);
        EncodeName.setOnTouchListener(EditP);

        mSlideSwitchView = (SlideSwitchView) findViewById(R.id.mSlideSwitchView);
        myPCursor = myToDoDB.select(TABLE_NAME_Sound);
        if(myPCursor.getCount()>0){
            myPCursor.moveToFirst();
            String SoundN=myPCursor.getString(1);
            if (SoundN.equals("true")){
                mSlideSwitchView.setChecked(true);
            }else {
                mSlideSwitchView.setChecked(false);
            }
        }
        mSlideSwitchView.setOnChangeListener(new SlideSwitchView.OnSwitchChangedListener() {
            @Override
            public void onSwitchChange(SlideSwitchView switchView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    SoundOn="true";
                }
                else {
                    SoundOn="false";
                }
                myPCursor = myToDoDB.select(TABLE_NAME_Sound);
                if (myPCursor.getCount()>0){
                    myToDoDB.updateSound(SoundOn);
                }else{
                    myToDoDB.InsertSound(SoundOn);
                }
            }
        });

        //old data import to database
        myPCursor = myToDoDB.select(TABLE_NAME_Data);
        if (myPCursor.getCount() > 0) {

        }else{
            ImportToDatabase();
        }
        //this handler deals the prograssbar
        handler = new MyHandler();
    }

    class MyHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                dialog.setProgress(index);
            }
            super.handleMessage(msg);
        }

    }
    public void ImportToDatabase(){

        //final ProgressDialog dialog = null;
        final CharSequence dialogTitle = getString(R.string.str_dialog_title);
        final CharSequence dialogBody = getString(R.string.str_dialog_body);
        dialog = ProgressDialog.show(Setting.this, dialogTitle, dialogBody, true);

        new Thread(new Runnable(){
            public void run() {
            File extDir = Environment.getExternalStorageDirectory();
            File FirstFilename = new File(extDir, "Measure");
            if(!FirstFilename.exists())
            {
                FirstFilename.mkdirs();
            }
            String[] filename = FirstFilename.list();
            if(filename.length>0)
            {
                File temp = null;
                for (int i = filename.length-1; i >-1; i--) {

                    String oldPath = FirstFilename.getAbsolutePath();
                    if (oldPath.endsWith(File.separator)) {
                        temp = new File(oldPath + filename[i]);
                    } else {
                        temp = new File(oldPath + File.separator + filename[i]);
                    }
                    if (temp.isFile()) {

                    } else {
                        File[] fs = temp.listFiles();
                        Arrays.sort(fs, new Comparator<File>() {
                            public int compare(File f1, File f2) {
                                long diff = f1.lastModified() - f2.lastModified();
                                if (diff > 0)
                                    return 1;
                                else if (diff == 0)
                                    return 0;
                                else
                                    return -1;
                            }

                            public boolean equals(Object obj) {
                                return true;
                            }

                        });
                        if (fs.length > 0) {
                            for (int k = 0; k < fs.length; k++) {
                                oldPath = fs[k].getPath();
                                FileDataToBase(oldPath);
                            }
                        }
                    }
                    index = 100-(i*100)/filename.length;
                    message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
                dialog.dismiss();
            }
        }).start();

        //dialog.cancel();
    }

    public void FileDataToBase(String FileName) {

        try{
            File DataFile = new File(FileName);
            String GunNStr="",GunDateStr="";

            FileInputStream finputs = new FileInputStream(DataFile);
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( finputs , "GB2312" ));
            String line,GunTStr = "",GunLStr = "",GunCLStr = "",GunOStr = "",GunOff = "",Xstr = "",Ystr = "";
            int i=0;
            while( ( line = bufferedReader.readLine()) != null ){

                switch (i) {
                    case 0:
                        GunNStr = line.substring(line.indexOf("\t") + 1, line.length());
                        String SubFileName=DataFile.getName();
                        //GunNStr=SubFileName.substring(0,SubFileName.indexOf("_"));
                        myPCursor= myToDoDB.selectGunCount(GunNStr);
                        if (myPCursor.getCount()>0){
                        }else
                        {

                        }
                        if (!SubFileName.contains(GunNStr)){ //if filename not contain the roller name, this will be return
                            return;
                        }
                        SubFileName=SubFileName.replace(GunNStr,"");
                        GunDateStr=SubFileName.substring(SubFileName.indexOf("_")+1);
                        GunDateStr=GunDateStr.replace(".txt","");
                        //get file date
                        if (GunDateStr.length()>9){
                            String temp=GunDateStr.substring(0,2)+"-";
                            temp=temp+GunDateStr.substring(2,4)+"-";
                            temp=temp+GunDateStr.substring(4,6)+" ";
                            temp=temp+GunDateStr.substring(7,9)+":";
                            temp=temp+GunDateStr.substring(9);

                            GunDateStr=temp;
                        }else {
                            return;
                        }

                        myPCursor=myToDoDB.selectGunDataXY(GunNStr,GunDateStr);
                        if (myPCursor.getCount()>0){
                            return;
                        }
                        break;
                    case 1:
                        GunTStr = line.substring(line.indexOf("\t") + 1, line.length());
                        break;
                    case 2:
                        GunLStr = line.substring(line.indexOf("\t") + 1, line.length());
                        break;
                    case 3:
                        GunCLStr = line.substring(line.indexOf("\t") + 1, line.length());
                        break;
                    case 4:
                        GunOStr = line.substring(line.indexOf("\t") + 1, line.length());
                        myToDoDB.insert(TABLE_NAME_Infor,GunNStr, GunTStr, GunLStr, GunCLStr, GunOStr, "true");
                        break;
                    case 5:
                        GunOff = line.substring(line.indexOf("\t") + 1, line.length() - 1);
                        myPCursor = myToDoDB.select(TABLE_NAME_Infor);
                        if (myPCursor.getCount() > 0) {
                            myPCursor.moveToLast();
                            int GunID = myPCursor.getInt(0);
                            myToDoDB.updateoffset(Integer.toString(GunID), GunOff);
                        }
                        break;
                    default:
                        Xstr=line.substring(0, line.indexOf("\t"));
                        Ystr = line.substring(line.indexOf("\t") + 1, line.length());
                        if (Double.valueOf(Ystr) > 10) {
                            Ystr = Double.toString(Double.valueOf(Ystr) /1000.000);
                        }
                        myToDoDB.insert(TABLE_NAME_Data, GunNStr, GunDateStr, GunTStr, GunLStr, Xstr, Ystr);
                        break;
                }
                i++;
            }
            finputs.close();

        } catch (Exception e) {
            //System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }
    //acting bar
    public View.OnClickListener ReturnSBC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Setting.this, Activity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (null != bluetoothService) {
                bluetoothService.write(StringHexUtils.hexStr2Bytes("06"));
            }
            Setting.this.finish();
            return;

        }
    };
    public View.OnTouchListener ReturnSBP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                ReturnS.setBackgroundResource(R.drawable.returnbp);
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                ReturnS.setBackgroundResource(R.drawable.returnb);
            }
            return false;
        }
    };

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:

                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            blue3.setImageResource(R.drawable.blue2);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            blue3.setImageResource(R.drawable.blue1);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    break;
                case MESSAGE_READ:
                    // MessageRead messageRead = new
                    // MessageRead(mConversationArrayAdapter,msg);
                    // messageRead.start();
                    byte[] readBuf = (byte[]) msg.obj;
                    if (msg.arg1 < 3) {
                        String stringHex = StringHexUtils.Bytes2HexString(readBuf);
                        stringHex = stringHex.substring(0, (msg.arg1) * 2);

                    int BatteryF=0;

                    BatteryF= readBuf[0]&0xFF;
                    //pos= BatteryF*100/255;
                        //int pos = Integer.parseInt(stringHex);
                        int pos = BatteryF;
                        if (-1 < pos && pos < 101) {
                            BatteryP.setText(Integer.toString(pos) + "%");
                        }
                        if (pos < 21) {
                            BatteryButton.setImageResource(R.drawable.b20);
                        } else if (pos < 41) {
                            BatteryButton.setImageResource(R.drawable.b40);
                        } else if (pos < 61) {
                            BatteryButton.setImageResource(R.drawable.b60);
                        } else if (pos < 81) {
                            BatteryButton.setImageResource(R.drawable.b80);
                        } else if (pos < 101) {
                            BatteryButton.setImageResource(R.drawable.b100);
                        }
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name

                    // 链接成功后开始发送获取称重信号
                    if (null != bluetoothService) {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                //System.out.println("update....");
                            }
                        }, 0, 2000);
                        bluetoothService.write(StringHexUtils.hexStr2Bytes("01"));
                    }

                    break;
                case MESSAGE_TOAST:

                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    // Get the BLuetoothDevice object
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter
                            .getRemoteDevice(address);
                    mConnectedDeviceName = device.getName();
                    BlueName.setText(mConnectedDeviceName);
                    //add bluetooth information to database
                    if (!BlueAddress.equals(address)) {
                        myPCursor = myToDoDB.selectOldBlue(address);
                        if (myPCursor.getCount() > 0) {
                            myToDoDB.updateblue(address,mConnectedDeviceName);
                        } else {
                            myToDoDB.insertblue(address, mConnectedDeviceName);
                        }
                    }
                    // Attempt to connect to the device
                    bluetoothService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupConnect();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private synchronized void setupConnect() {

        if (bluetoothService == null) {
            bluetoothService = new BluetoothServiceS(Setting.this, mHandler);
        }
        if(!BlueAddress.equals(null)){
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(BlueAddress);
            // Attempt to connect to the device
            bluetoothService.connect(device);
        }

    }
    public View.OnClickListener BlueSC=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (mBluetoothAdapter == null) {
                Toast.makeText(Setting.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Intent serverIntent = new Intent(Setting.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

        }
    };

    public View.OnClickListener EditC=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
/*
            if (mBluetoothAdapter == null) {
                Toast.makeText(Setting.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Intent serverIntent = new Intent(Setting.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            */

        }
    };

    public View.OnClickListener LanSC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            final String ENstr = getString(R.string.ENEN);
            final String CNstr = getString(R.string.ENC);

            new AlertDialog.Builder(Setting.this)
                    .setTitle(getString(R.string.LanguageSet))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(new String[] {CNstr,ENstr}, 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        ChorNot=true;
                                    }else {
                                        ChorNot=false;
                                    }
                                }
                            }
                    )
                    .setPositiveButton(getString(R.string.Confirm),new DialogInterface.OnClickListener() {//添加确定按钮

                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            if(ChorNot){
                                config.locale = Locale.SIMPLIFIED_CHINESE;
                                LangName.setText(CNstr);
                                Languages=CNstr;
                                ActionbarS.setBackgroundResource(R.drawable.asetting);
                            }else {
                                config.locale = Locale.ENGLISH;
                                LangName.setText(ENstr);
                                Languages=ENstr;
                                ActionbarS.setBackgroundResource(R.drawable.aesetting);
                            }
                            resources.updateConfiguration(config, dm);
                            myPCursor = myToDoDB.select(TABLE_NAME_Language);
                            if (myPCursor.getCount()>0){
                                myToDoDB.updatelan(Languages);
                            }else{
                                myToDoDB.InsertLanguage(Languages);
                            }
                            //too solve bug. when language modified, this action will recreate, but bluetooth not rest.
                            if (bluetoothService != null){
                                bluetoothService.stop();
                            }
                            onCreate(null);
                            // Attempt to connect to the device
                            setupConnect();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.Cancles), null)
                    .show();

        }
    };
    public View.OnTouchListener BlueSP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                BlueArrow.setBackgroundColor(Color.rgb(127,127,127));
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                BlueArrow.setBackgroundColor(Color.WHITE);
            }
            return false;
        }
    };

    public View.OnTouchListener LanSP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                LangArrow.setBackgroundColor(Color.rgb(127,127,127));
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                LangArrow.setBackgroundColor(Color.WHITE);
            }
            return false;
        }
    };
    public View.OnTouchListener EditP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                if(passed==0){
                final EditText editText = new EditText(Setting.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                new AlertDialog.Builder(Setting.this)
                        .setTitle(getString(R.string.Encode_Pass))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(editText)
                        .setPositiveButton(getString(R.string.Confirm),new DialogInterface.OnClickListener() {//添加确定按钮

                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件)
                                String Password1=editText.getText().toString();
                                Password1=Password1.trim();
                                if (Password1.equals("willtech")){
                                    passed = 1;
                                    EncodeName.setFocusable(false);
                                }else{
                                    //passed = 2;
                                    Toast.makeText(Setting.this, getString(R.string.Pass_Warn),Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.Cancles), null)
                        .show();
                } else if (passed == 1) {

                    final EditText editText1 = new EditText(Setting.this);
                    editText1.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

                    new AlertDialog.Builder(Setting.this)
                            .setTitle(getString(R.string.Encode_Length))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(editText1)
                            .setPositiveButton(getString(R.string.Confirm), new DialogInterface.OnClickListener() {//添加确定按钮

                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件)
                                    String EncodeL = editText1.getText().toString();
                                    EncodeL = EncodeL.trim();
                                    if(EncodeL.contains(".")){

                                    }else
                                    {
                                        EncodeL=EncodeL+".0";
                                    }
                                    //int tempLen = Integer.parseInt(EncodeL);
                                    float tempLen = Float.parseFloat(EncodeL);
                                    if (tempLen < 150 || tempLen > 200) {
                                        passed = 1;
                                        Toast.makeText(Setting.this, getString(R.string.Length_Warn),Toast.LENGTH_LONG).show();
                                    } else {
                                        passed = 2;
                                        EncodeL = EncodeL.format("%.1f",tempLen);
                                        EncodeName.setText(EncodeL);
                                        EncodeName.setFocusable(false);
                                        EncodeName.setEnabled(false);
                                        //add data to database
                                        myPCursor = myToDoDB.select(TABLE_NAME_ENCODE);
                                        if (myPCursor.getCount()>0){
                                            myToDoDB.updateEncode(EncodeL);
                                        }else{
                                            myToDoDB.InsertEncode(EncodeL);
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.Cancles), null)
                            .show();
                }
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                //LangArrow.setBackgroundColor(Color.WHITE);
            }


            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if (null != bluetoothService) {
                    bluetoothService.write(StringHexUtils.hexStr2Bytes("06"));
                }
                Intent intent = new Intent(this, Activity1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Setting.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (!BlueAddress.equals(""))
                setupConnect();
        }
    }
    public synchronized void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (bluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothService.start();
            }
        }
    }
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (bluetoothService != null)
            bluetoothService.stop();
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
