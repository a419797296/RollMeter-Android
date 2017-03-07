package com.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.widget.BatteryView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ParameterSet extends Activity {

    BatteryView mBatteryView;
    private Button LeftButton;
    private Button RightButton;
    private Boolean LeftToRight;

    private Button ClearButton;
    private Button SaveButton;

    private ImageButton ZeroButton;

    private EditText EditGunN;
    private EditText EditGunT;
    private EditText EditGunL;
    private EditText EditGunCL;
    private EditText EditGunO;
    private String GunNStr;
    private String GunTStr;
    private String GunLStr;
    private String GunCLStr;
    private String GunOStr;
    private String EncodeStr="";
    private final static String TABLE_NAME_Infor = "measure_infor";
    private final static String TABLE_NAME_ENCODE = "Encode_infor";
    //database
    private ToDoDB myToDoDB;
    private Cursor myCursor;
    private boolean ZeroIsSet;
    private RelativeLayout backgp;

    //blue tooth
    // Layout Views
    private TextView mTitle;
    private TextView moffset;
    private ListView mConversationView;
    private static final boolean D = true;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_READ_03 = 6;
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService bluetoothService = null;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public boolean LangSetCN = true;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private String address="";
    //private String address="F8:A4:5F:21:DA:38";
    // Array adapter for the conversation thread
    // private ArrayAdapter<String> mConversationArrayAdapter;
    private Object lock = new Object();
    private ImageView blue1;
    private  int GunOffset=0;
    private  int GunID=0;
    private ImageButton ReturnP;
    private RelativeLayout ActionbarP;
    private RelativeLayout ActionbarPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //softkeyboard
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_parameter_set);

        /*
        //actionbar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        Drawable myDrawable = getResources().getDrawable(R.drawable.aparameter);
        actionBar.setBackgroundDrawable(myDrawable);
        */

        //make sure the screen is on all the time
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActionbarP=(RelativeLayout)findViewById(R.id.relativeLayoutTopP);
        backgp=(RelativeLayout)findViewById(R.id.relativeLayoutBack);
        Locale curLocale = getResources().getConfiguration().locale;
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            LangSetCN = true;
            ActionbarP.setBackgroundResource(R.drawable.aparameter);
            backgp.setBackgroundResource(R.drawable.parameter);
        } else {
            LangSetCN = false;
            ActionbarP.setBackgroundResource(R.drawable.aeparameters);
            backgp.setBackgroundResource(R.drawable.parametere);
        }
        //left right buttons
        LeftToRight = true;
        LeftButton=(Button)findViewById(R.id.leftbutton);
        LeftButton.setOnClickListener(LeftButtonC);
        LeftButton.setOnTouchListener(LeftButtonP);
        LeftButton.setBackgroundColor(Color.GREEN);

        RightButton=(Button)findViewById(R.id.rightbutton);
        RightButton.setOnClickListener(RightButtonC);
        RightButton.setOnTouchListener(RightButtonP);

        //Clear Save buttons
        ClearButton=(Button)findViewById(R.id.buttonClear);
        ClearButton.setOnClickListener(ClearButtonC);
        ClearButton.setOnTouchListener(ClearButtonP);

        SaveButton=(Button)findViewById(R.id.buttonSave);
        SaveButton.setOnClickListener(SaveButtonC);
        SaveButton.setOnTouchListener(SaveButtonP);

        //zero button
        ZeroButton=(ImageButton)findViewById(R.id.imageButtonZero);
        ZeroButton.setOnClickListener(ZeroButtonC);
        ZeroButton.setOnTouchListener(ZeroButtonP);
        ZeroButton.setEnabled(false);
        ZeroIsSet=false;
        //edit text

        EditGunN = (EditText) findViewById(R.id.editTextGunN);
        EditGunT = (EditText) findViewById(R.id.editTextGunT);
        EditGunL = (EditText) findViewById(R.id.editTextGunL);
        EditGunCL = (EditText) findViewById(R.id.editTextGunCL);
        EditGunO = (EditText) findViewById(R.id.editTextGunO);
        blue1=(ImageView)findViewById(R.id.imageBlue1);

        ReturnP = (ImageButton)findViewById(R.id.imageButtonReturnP);
        ReturnP.setClickable(false);
        ActionbarPB = (RelativeLayout) findViewById(R.id.imageButtonReturnPB);
        ActionbarPB.setOnClickListener(ReturnPBC);
        ActionbarPB.setOnTouchListener(ReturnPBP);

        if (LangSetCN) {
            ClearButton.setBackgroundResource(R.drawable.clear);
            SaveButton.setBackgroundResource(R.drawable.save);
        } else {
            ClearButton.setBackgroundResource(R.drawable.clearen);
            SaveButton.setBackgroundResource(R.drawable.saveen);
        }

        //database parts
        myToDoDB = new ToDoDB(this);
	    /* 取得DataBase里的数据 */
        myCursor = myToDoDB.select(TABLE_NAME_Infor);
        if (myCursor.getCount() > 0) {
            myCursor.moveToLast();
            GunID= myCursor.getInt(0);
            String GunNumber = myCursor.getString(1);
            EditGunN.setText(GunNumber);
            EditGunT.setText(myCursor.getString(2));
            String GunLength = myCursor.getString(3);
            EditGunL.setText(GunLength);
            String GunCLen = myCursor.getString(4);
            EditGunCL.setText(GunCLen);
            if(myCursor.getString(5).equals("0")){
                EditGunO.setText("");
            }else {
                EditGunO.setText(myCursor.getString(5));
            }

            if (myCursor.getString(6).equals("true")) {
                LeftToRight=true;
                LeftButton.setBackgroundColor(Color.GREEN);
                RightButton.setBackgroundColor(Color.rgb(127,127,127));
            } else {
                LeftToRight=false;
                RightButton.setBackgroundColor(Color.GREEN);
                LeftButton.setBackgroundColor(Color.rgb(127, 127, 127));
            }
        }

        myCursor = myToDoDB.select(TABLE_NAME_ENCODE);
        if (myCursor.getCount() > 0) {
            myCursor.moveToLast();
            EncodeStr=myCursor.getString(1);
        }

        // get bluetooth
        myCursor = myToDoDB.selectBlue();
        if(myCursor==null) {
            new AlertDialog.Builder(ParameterSet.this)
                    .setTitle(getString(R.string.PlConfirm))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(getString(R.string.Cancles))
                    .setPositiveButton(getString(R.string.blue_warning), null)
                    .show();

        }else {
            if (myCursor.getCount() > 0) {
                myCursor.moveToFirst();
                address = myCursor.getString(1);
            } else {
                new AlertDialog.Builder(ParameterSet.this)
                        .setTitle(getString(R.string.PlConfirm))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(getString(R.string.blue_warning))
                        .setPositiveButton(getString(R.string.Confirm), null)
                        .show();
            }
        }

        //bluetooth
        mTitle=(TextView) findViewById(R.id.textViewBlue);
        moffset=(TextView) findViewById(R.id.textView12);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService = new BluetoothService(this, mHandler);

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }
    public View.OnClickListener ReturnPBC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ParameterSet.this, Activity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (null != bluetoothService) {
                bluetoothService.write(StringHexUtils.hexStr2Bytes("06"));
                if (!ZeroIsSet){
                    myToDoDB.updateoffset(Integer.toString(GunID), Integer.toString(GunOffset+2500));
                }
            }
            ParameterSet.this.finish();
            return;
        }
    };
    public View.OnTouchListener ReturnPBP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                ReturnP.setBackgroundResource(R.drawable.returnbp);
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                ReturnP.setBackgroundResource(R.drawable.returnb);
            }
            return false;
        }
    };
    public void onStart() {
        super.onStart();
        if (D)
            Log.e("TAG", "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (!address.equals(""))
                setupConnect();
        }
        //Intent serverIntent = new Intent(this, DeviceListActivity.class);
        //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);


    }

    private synchronized void setupConnect() {
        Log.d("TAG", "setupChat()");

        // Initialize the array adapter for the conversation thread
        //mConversationArrayAdapter = new ArrayAdapter<String>(this,R.layout.message);

        //mConversationView = (ListView) findViewById(R.id.in);
        //mConversationView.setAdapter(mConversationArrayAdapter);
        // if(!mConversationArrayAdapter.isEmpty())
        // Log.i(TAG1, mConversationArrayAdapter.getItem(0) + "hello");

        // Initialize the BluetoothChatService to perform bluetooth connections
        if (bluetoothService == null) {
            bluetoothService = new BluetoothService(ParameterSet.this, mHandler);
        }
        if (!address.equals(null)) {

            BluetoothDevice device = mBluetoothAdapter
                    .getRemoteDevice(address);
            // Attempt to connect to the device
            bluetoothService.connect(device);
        }

        // Initialize the buffer for outgoing messages
        // mOutStringBuffer = new StringBuffer("");
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i("TAG", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            //mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            //Toast.makeText(getApplicationContext(), R.string.title_connected_to, Toast.LENGTH_SHORT).show();
                            //mTitle.setText( R.string.title_connected_to);
                            // mConversationArrayAdapter.clear();
                            blue1.setImageResource(R.drawable.blue2);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            // mTitle.setText(R.string.title_connecting);
                            //Toast.makeText(getApplicationContext(), R.string.title_connecting, Toast.LENGTH_SHORT).show();
                            // mTitle.setText( R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            // mTitle.setText(R.string.title_not_connected);
                            //Toast.makeText(getApplicationContext(), R.string.title_not_connected, Toast.LENGTH_SHORT).show();
                            //mTitle.setText(R.string.title_not_connected);
                            blue1.setImageResource(R.drawable.blue1);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Sended:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    // MessageRead messageRead = new
                    // MessageRead(mConversationArrayAdapter,msg);
                    // messageRead.start();
                    synchronized (lock) {
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        // String message = new String(readBuf);
                        // Toast.makeText(OtherActivity.this, "message="+message,
                        // 1).show();
//					String readMessage = new String(readBuf, 0, msg.arg1);
//					Log.i("bt", "read message:"+readMessage);
                        int numbers= msg.arg1;
                        int offset=0;
                        if((numbers<20)&&(numbers>1)) {
                            byte newbuff[] = new byte[numbers];
                            for (int i = 0; i < 2; i++) {
                                newbuff[i] = readBuf[i];
                            }
                            offset= ((newbuff[0]&0xFF)<<8)|(newbuff[1]&0xFF);
                            String stringHex = StringHexUtils.Bytes2HexString(newbuff);
                            if(stringHex.length()>4){
                                stringHex=stringHex.substring(0,4);
                            }
                            //GunOffset=offset - 2500;
                            GunOffset=offset;
                            //if(GunOffset<5000&&GunOffset>-5000){
                            if(GunOffset<=5000&&GunOffset>=0){
                                double cc = (double)GunOffset/1000.000;
                                String tempstrss=String.format("%.3f", cc);
                                moffset.setText(tempstrss);
                            }
                            //if((offset-2500<250)&&(offset-2500>-250))
                            if((offset<5000)&&(offset>0)&&(!ZeroIsSet))
                            {
                                ZeroButton.setEnabled(true);
                                ZeroButton.setImageResource(R.drawable.zero1);
                            }else {
                                ZeroButton.setEnabled(false);
                                ZeroButton.setImageResource(R.drawable.zero);
                            }
                        }
                        else {
                            /*
                            String stringHex = StringHexUtils.Bytes2HexString(readBuf);
                            stringHex=stringHex.substring(0, (msg.arg1)*2);

                            Toast.makeText(getApplicationContext(), "接收到"+stringHex, Toast.LENGTH_SHORT).show();
                            */
                        }
                    }

                    break;
                case MESSAGE_READ_03:
                    synchronized (lock) {
                        byte[] readBuf = (byte[]) msg.obj;

                        int numbers= msg.arg1;
                        int offset=0;
                        if(numbers==1) {
                            //offset= readBuf[0];
                            offset=0;
                            moffset.setText(Integer.toString(offset));
                        }
                        /*
                        new AlertDialog.Builder(ParameterSet.this)

                                .setTitle(getString(R.string.PlConfirm))
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setMessage(getString(R.string.set_zero))
                                .setPositiveButton(getString(R.string.Confirm), null)
                                .setPositiveButton(getString(R.string.Cancles), null)
                                .show();
                                */
                        /*
                        if (offset == 0) {
                        } else {
                            //Toast.makeText(getApplicationContext(), "置零失败，请重试", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(ParameterSet.this)

                                    .setTitle("警告")
                                    .setMessage("置零失败，是否再次置零？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                        @Override

                                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                            // TODO Auto-generated method stub
                                            //bluetoothService.write(StringHexUtils.hexStr2Bytes("02"));
                                        }

                                    })
                                    .setNegativeButton("取消", null)

                                    .show();
                        }*/
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //mTitle.setText(mConnectedDeviceName);
                    // 链接成功后开始发送获取称重信号
                    if (null != bluetoothService) {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                //System.out.println("update....");
                            }
                        }, 0, 2000);
                        bluetoothService.write(StringHexUtils.hexStr2Bytes("02"));
                    }
                    break;
                case MESSAGE_TOAST:
                    // Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT)  .show();
                    mTitle.setText(msg.getData().getString(TOAST));
                    break;
            }
        }
    };


    public synchronized void onResume() {
        super.onResume();
        if (D)
            Log.e("TAG", "+ ON RESUME +");

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

    public View.OnClickListener SaveButtonC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            GunNStr="";
            GunTStr="";
            GunLStr="";
            GunCLStr="";
            GunOStr="";
            GunNStr= EditGunN.getText().toString();
            GunTStr=EditGunT.getText().toString();
            GunLStr=EditGunL.getText().toString();
            GunCLStr=EditGunCL.getText().toString();
            GunOStr=EditGunO.getText().toString();

            if(GunNStr.equals("")||GunLStr.equals("")||GunCLStr.equals("")){
                //Toast.makeText(getApplicationContext(), getString(R.string.input_warnning), Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(ParameterSet.this)
                        .setTitle(getString(R.string.PlConfirm))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(getString(R.string.input_warnning))
                        .setPositiveButton(getString(R.string.Confirm), null)
                        .show();
                return;
            }
            if(GunOStr.equals("")){
                GunOStr="0";
                EditGunO.setText(GunOStr);
            }
            if(Integer.parseInt(GunLStr)<100||Integer.parseInt(GunCLStr)<10||Integer.parseInt(GunCLStr)>100||Integer.parseInt(GunCLStr)>7000){
                //Toast.makeText(getApplicationContext(), getString(R.string.range_warnning), Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(ParameterSet.this)
                        .setTitle(getString(R.string.PlConfirm))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(getString(R.string.range_warnning))
                        .setPositiveButton(getString(R.string.Confirm), null)
                        .show();
                return;
            }

            myCursor= myToDoDB.selectGunCount(GunNStr);
            if (myCursor.getCount()>10){
                myCursor.moveToFirst();
                int gun_id = myCursor.getInt(0);
                myToDoDB.delete(TABLE_NAME_Infor,"_id",Integer.toString(gun_id));
            }
            myToDoDB.insert(TABLE_NAME_Infor,GunNStr, GunTStr, GunLStr, GunCLStr, GunOStr, LeftToRight.toString());

            if (null != bluetoothService) {

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        //System.out.println("update....");
                    }
                }, 0, 1000);

                String savev="";
                String buff;
                if(LeftToRight){
                    buff= "FF";
                }else{
                    buff= "FE";
                }

                GunLStr=GunLStr.trim();
                GunCLStr=GunCLStr.trim();

                if (Integer.parseInt(GunLStr)>255) {
                    savev = buff + algorismToHEXString(Integer.parseInt(GunLStr)) + algorismToHEXString(Integer.parseInt(GunCLStr));
                }else
                {
                    savev = buff +"00"+algorismToHEXString(Integer.parseInt(GunLStr)) + algorismToHEXString(Integer.parseInt(GunCLStr));
                }

                if (EncodeStr.equals("")){
                    EncodeStr="173.0";    //default length is 173mm
                }
                float templen = Float.parseFloat(EncodeStr);
                templen=templen*10;
                EncodeStr=EncodeStr.format("%.0f",templen);

                savev=savev+algorismToHEXString(Integer.parseInt(EncodeStr));

                bluetoothService.write(StringHexUtils.hexStr2Bytes(savev));
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        }
    };
    public static String algorismToHEXString(int algorism) {
        String result = "";
        result = Integer.toHexString(algorism);

        if (result.length() % 2 == 1) {
            result = "0" + result;

        }
        result = result.toUpperCase();

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parameter_set, menu);
        return true;
    }

    private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int power = level * 100 / scale;
                // Log.d("Deom", "电池电量：:" + power);
                mBatteryView.setPower(power);
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if (null != bluetoothService) {
                    bluetoothService.write(StringHexUtils.hexStr2Bytes("06"));
                    if (!ZeroIsSet){
                        myToDoDB.updateoffset(Integer.toString(GunID), Integer.toString(GunOffset+2500));
                    }
                }
                Intent intent = new Intent(this, Activity1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ParameterSet.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public View.OnClickListener LeftButtonC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            LeftToRight = true;
            RightButton.setBackgroundColor(Resources.getSystem().getColor(android.R.color.darker_gray));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.MeasureDirectLeft), Toast.LENGTH_SHORT).show();
        }
    };
    public View.OnTouchListener LeftButtonP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                LeftButton.setBackgroundColor(Color.rgb(127,127,127));
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                LeftButton.setBackgroundColor(Color.GREEN);
            }
            return false;
        }
    };
    public View.OnClickListener RightButtonC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            LeftToRight = false;
            LeftButton.setBackgroundColor(Resources.getSystem().getColor(android.R.color.darker_gray));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.MeasureDirectRight), Toast.LENGTH_SHORT).show();
        }
    };
    public View.OnTouchListener RightButtonP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                RightButton.setBackgroundColor(Color.rgb(127,127,127));
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                RightButton.setBackgroundColor(Color.GREEN);
            }
            return false;
        }
    };




    public View.OnClickListener ClearButtonC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            GunNStr="";
            GunTStr="";
            GunLStr="";
            GunCLStr="";
            GunOStr="";
            EditGunN.setText(GunNStr.toCharArray(), 0, GunNStr.length());
            EditGunT.setText(GunNStr.toCharArray(), 0, GunNStr.length());
            EditGunL.setText(GunNStr.toCharArray(), 0, GunNStr.length());
            EditGunCL.setText(GunNStr.toCharArray(), 0, GunNStr.length());
            EditGunO.setText(GunNStr.toCharArray(), 0, GunNStr.length());

        }
    };
    public View.OnTouchListener ClearButtonP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                if (LangSetCN) {
                    ClearButton.setBackgroundResource(R.drawable.clearp);
                } else {
                    ClearButton.setBackgroundResource(R.drawable.clearpen);
                }
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                if (LangSetCN) {
                    ClearButton.setBackgroundResource(R.drawable.clear);
                } else {
                    ClearButton.setBackgroundResource(R.drawable.clearen);
                }
            }
            return false;
        }
    };


    public View.OnTouchListener SaveButtonP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                if (LangSetCN) {
                    SaveButton.setBackgroundResource(R.drawable.savep);
                } else {
                    SaveButton.setBackgroundResource(R.drawable.savepen);
                }
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                if (LangSetCN) {
                    SaveButton.setBackgroundResource(R.drawable.save);
                } else {
                    SaveButton.setBackgroundResource(R.drawable.saveen);
                }
            }
            return false;
        }
    };
    public View.OnClickListener ZeroButtonC=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //GunOffset=GunOffset+2500;
            double tempoffset = GunOffset/1000.00;
            String tempOffsetStr,temp1,temp2;
            //temp1=Double.toString(0.00-tempoffset);
            temp1= String .format("%.3f",0.00-tempoffset);
            //temp2=Double.toString(5.00-tempoffset);
            temp2= String .format("%.3f",10.00-tempoffset);
            tempOffsetStr= temp1+"~"+temp2;
            new AlertDialog.Builder(ParameterSet.this)
                    .setTitle(getString(R.string.PlConfirm))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(getString(R.string.set_zero)+tempOffsetStr)
                    .setPositiveButton(getString(R.string.Confirm),new SentZero())
                    .setNegativeButton(getString(R.string.Cancles), null)
                    .show();
        }
    };
    public View.OnTouchListener ZeroButtonP=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                ZeroButton.setImageResource(R.drawable.zero2);
            }
            else if(event.getAction()== MotionEvent.ACTION_UP)
            {
                ZeroButton.setImageResource(R.drawable.zero);
            }
            return false;
        }
    };
    private class SentZero implements DialogInterface.OnClickListener {//添加确定按钮

        @Override

        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
            // TODO Auto-generated method stub
            if (null != bluetoothService) {
                bluetoothService.write(StringHexUtils.hexStr2Bytes("03"));
                //update database
                ZeroIsSet =true;
                //GunOffset=GunOffset+2500;
                myToDoDB.updateoffset(Integer.toString(GunID), Integer.toString(GunOffset));
                ZeroButton.setEnabled(false);
                ZeroButton.setImageResource(R.drawable.zero);
            }
        }
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), getString(R.string.onemore_return), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                if (null != bluetoothService) {
                    bluetoothService.write(StringHexUtils.hexStr2Bytes("06"));
                    if (!ZeroIsSet){
                        myToDoDB.updateoffset(Integer.toString(GunID), Integer.toString(GunOffset+2500));
                    }
                }
                finish();
                //System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
