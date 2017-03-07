package com.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.widget.SaundProgressBar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class Measure extends Activity {

    private List<String> datas;
    private ListView listView;
    private com.test.DoubleAdapter adapter;
    private SaundProgressBar mPbar;
    private SaundProgressBar RPbar;
    private String LeftToRiht;
    private String XOffset = "0";
    private int progress = 0;
    private Message message;
    private Handler handler;
    LinearLayout MImageButon;
    private ToDoDB myPGunInfor;
    private Cursor myPCursor;
    private final static String TABLE_NAME_Infor = "measure_infor";
    private final static String TABLE_NAME_Data = "measure_data";
    //blue
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_READ_03 = 6;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothServiceM bluetoothService = null;

    private int MaxValueX = 0;
    private int MinValueX = 5000;
    private double MaxValueY = 0.0;
    private double MinValueY = 5.0;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private String address = "";
    // Array adapter for the conversation thread
    // private ArrayAdapter<String> mConversationArrayAdapter;
    private TextView VxPos;
    private TextView VyPos;
    private TextView mTitle;
    private TextView MaxPos;
    private TextView MinxPos;
    private TextView MayPos;
    private TextView MinyPos;
    private ImageView blue2;
    public int LeftOperation = 0;
    public boolean GetDirection = false;

    private Object lock = new Object();

    private String GunNumber;
    private String GunLength;
    private String GunCLen;
    private String GunType;
    private String GunOffer;  //offset
    private int GunLeng;
    private int GunCLeng;
    private int[] x = new int[30000];
    private double[] y = new double[30000];
    private int recorder = 0;

    //File
    private Context mContext;

    private ImageButton ReturnM;
    private RelativeLayout ReturnMB;
    private RelativeLayout ActionbarM;
    private RelativeLayout BackM;

    TextView OperationL;
    TextView OperationR;
    int StartPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_measure);

        /*
        //actionbar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        Drawable myDrawable = getResources().getDrawable(R.drawable.astart);
        actionBar.setBackgroundDrawable(myDrawable);
        */

        //make sure the screen is on all the time
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //imagebutton
        LeftToRiht = "true";
        MImageButon = (LinearLayout) findViewById(R.id.btimage);
        MImageButon.setClickable(true);
        MImageButon.setOnClickListener(MImageBC);
        MImageButon.setOnTouchListener(MImageBP);

        ActionbarM = (RelativeLayout) findViewById(R.id.relativeLayoutTopM);
        BackM = (RelativeLayout) findViewById(R.id.relativeLayoutbackM);

        Locale curLocale = getResources().getConfiguration().locale;
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            ActionbarM.setBackgroundResource(R.drawable.ameasure);
            BackM.setBackgroundResource(R.drawable.measureb);
        } else {
            ActionbarM.setBackgroundResource(R.drawable.aemeasurement);
            BackM.setBackgroundResource(R.drawable.measurebe);
        }

        //database parts
        TextView textGunN = (TextView) findViewById(R.id.textViewGunN);
        TextView textGunT = (TextView) findViewById(R.id.textViewGunT);
        TextView textGunL = (TextView) findViewById(R.id.textViewGunL);
        TextView textGunCL = (TextView) findViewById(R.id.textViewGunCL);
        TextView textGunO = (TextView) findViewById(R.id.textViewGunO);
        TextView textGunD = (TextView) findViewById(R.id.textViewGunD);
        myPGunInfor = new ToDoDB(this);
        try {
            myPCursor = myPGunInfor.select(TABLE_NAME_Infor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!(myPCursor == null)) {
            if (myPCursor.getCount() > 0) {
                myPCursor.moveToLast();
                GunNumber = myPCursor.getString(1);
                textGunN.setText(GunNumber);
                GunType = myPCursor.getString(2);
                textGunT.setText(GunType);
                GunLength = myPCursor.getString(3);
                textGunL.setText(GunLength + " mm");
                GunCLen = myPCursor.getString(4);
                textGunCL.setText(GunCLen + " mm");
                GunOffer = myPCursor.getString(5);
                if (GunOffer.equals("0")) {
                    textGunO.setText("");
                } else {
                    if(GunOffer.equals("")){
                        GunOffer="0";
                    }else if(GunOffer.contains("mm")){
                        textGunO.setText(GunOffer);
                    }else {
                        textGunO.setText(GunOffer+" mm");
                    }
                }

                LeftToRiht = myPCursor.getString(6);
                if (myPCursor.getString(6).equals("true")) {
                    textGunD.setText(getResources().getString(R.string.MeasureDirectLeft));
                } else {
                    textGunD.setText(getResources().getString(R.string.MeasureDirectRight));
                }
                XOffset = myPCursor.getString(7);
                GunLength.trim();
                GunLeng = Integer.parseInt(GunLength);
                GunCLen.trim();
                GunCLeng = Integer.parseInt(GunCLen);
                if(GunOffer.equals("")){
                    GunOffer="0";
                }else{
                    GunOffer = GunOffer.replace(" mm", "");
                    GunOffer = GunOffer.trim();
                }
                StartPos = Integer.parseInt(GunOffer);
            } else {
                new AlertDialog.Builder(Measure.this)
                        .setTitle(getString(R.string.PlConfirm))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(getString(R.string.parameter_warning))
                        .setPositiveButton(getString(R.string.Confirm), null)
                        .show();
            }
        }
        //progressbar
        //LeftToRiht = "true";

        Drawable indicator = getResources().getDrawable(R.drawable.progress_indicator);
        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 15, indicator.getIntrinsicHeight() + 15);
        indicator.setBounds(bounds);

        mPbar = (SaundProgressBar) this.findViewById(R.id.regularprogressbar);
        mPbar.setMax(100);
        mPbar.setDirection(true);
        mPbar.setProgressIndicator(indicator);
        mPbar.setProgress(0);


        RPbar = (SaundProgressBar) this.findViewById(R.id.regularprogressbarright);
        RPbar.setMax(100);
        RPbar.setProgressIndicator(indicator);
        RPbar.setDirection(false);
        RPbar.setProgress(100);

        if (LeftToRiht.equals("true")) {
            mPbar.setVisibility(View.VISIBLE);
            RPbar.setVisibility(View.INVISIBLE);
        } else {
            mPbar.setVisibility(View.INVISIBLE);
            RPbar.setVisibility(View.VISIBLE);
        }

        x[recorder] = 0;
        y[recorder] = 0;

        //bluetooth
        //mTitle=(TextView) findViewById(R.id.textViewBlue);
        //moffset=(TextView) findViewById(R.id.textView12);
        // Get local Bluetooth adapter
        mTitle = (TextView) findViewById(R.id.textViewBd);
        VxPos = (TextView) findViewById(R.id.textViewofp);
        VyPos = (TextView) findViewById(R.id.textViewpop);

        MaxPos = (TextView) findViewById(R.id.textViewxMaxValue);
        MinxPos = (TextView) findViewById(R.id.textViewxMinValue);
        MayPos = (TextView) findViewById(R.id.textViewyMaxValue);
        MinyPos = (TextView) findViewById(R.id.textViewyMinValue);
        blue2 = (ImageView) findViewById(R.id.imageBlue2);

        ReturnM = (ImageButton) findViewById(R.id.imageButtonReturnM);
        ReturnMB = (RelativeLayout) findViewById(R.id.imageButtonReturnMB);
        ReturnM.setClickable(false);
        ReturnMB.setOnClickListener(ReturnMBC);
        ReturnMB.setOnTouchListener(ReturnMBP);

        // get bluetooth
        myPCursor = myPGunInfor.selectBlue();
        if (!(myPCursor == null)) {
            if (myPCursor.getCount() > 0) {
                myPCursor.moveToFirst();
                address = myPCursor.getString(1);
            } else {
                new AlertDialog.Builder(Measure.this)
                        .setTitle(getString(R.string.PlConfirm))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(getString(R.string.blue_warning))
                        .setPositiveButton(getString(R.string.Confirm), null)
                        .show();
            }
        } else {
            new AlertDialog.Builder(Measure.this)
                    .setTitle(getString(R.string.PlConfirm))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(getString(R.string.blue_warning))
                    .setPositiveButton(getString(R.string.Confirm), null)
                    .show();
        }

        OperationL = (TextView) findViewById(R.id.OperationL);
        OperationR = (TextView) findViewById(R.id.OperationR);
        OperationR.setVisibility(View.INVISIBLE); //Right is invisible
        OperationL.setVisibility(View.INVISIBLE); //Right is invisible

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService = new BluetoothServiceM(Measure.this, mHandler);

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        new AlertDialog.Builder(Measure.this)
                .setMessage(getString(R.string.Select_Direct))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.Direct_Left),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LeftOperation = 1;
                                GetDirection = true;
                                OperationL.setVisibility(View.VISIBLE);
                                OperationR.setVisibility(View.INVISIBLE);
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableIntent = new Intent(
                                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                                    // Otherwise, setup the chat session
                                } else {
                                    if (!address.equals(""))
                                        setupConnect();
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.Direct_Right),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LeftOperation = 2;
                                GetDirection = true;
                                OperationL.setVisibility(View.INVISIBLE);
                                OperationR.setVisibility(View.VISIBLE);
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableIntent = new Intent(
                                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                                    // Otherwise, setup the chat session
                                } else {
                                    if (!address.equals(""))
                                        setupConnect();
                                }
                            }
                        })
                .show();

        mContext = this;

    }

    public View.OnClickListener ReturnMBC = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Measure.this, Activity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (null != bluetoothService) {
                bluetoothService.write(StringHexUtils.hexStr2Bytes("06"));
            }
            Measure.this.finish();
            return;

        }
    };
    public View.OnTouchListener ReturnMBP = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ReturnM.setBackgroundResource(R.drawable.returnbp);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                ReturnM.setBackgroundResource(R.drawable.returnb);
            }
            return false;
        }
    };

    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult


    }

    private synchronized void setupConnect() {

        if (bluetoothService == null) {
            bluetoothService = new BluetoothServiceM(Measure.this, mHandler);
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

                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            //mTitle.setText( R.string.title_connected_to);
                            // mConversationArrayAdapter.clear();
                            blue2.setImageResource(R.drawable.blue2);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            // mTitle.setText(R.string.title_connecting);
                            //Toast.makeText(getApplicationContext(), R.string.title_connecting, Toast.LENGTH_SHORT).show();
                            // mTitle.setText( R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            //mTitle.setText(R.string.title_not_connected);
                            //Toast.makeText(getApplicationContext(), R.string.title_not_connected, Toast.LENGTH_SHORT).show();
                            //mTitle.setText(R.string.title_not_connected);
                            blue2.setImageResource(R.drawable.blue1);
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
                    //if not select operation direction, will not show progress
                    byte[] readBuf= new byte[50];
                    int numbers;
                    synchronized (lock) {
                        readBuf = (byte[]) msg.obj;
                        numbers = msg.arg1;
                    }
                    int XPos = 0;
                    double YPos = 0.000;
                    if ((numbers < 20) && (numbers > 3)) {
                        byte newbuff[] = new byte[numbers];
                        for (int i = 0; i < 4; i++) {
                            newbuff[i] = readBuf[i];
                        }
                        XPos = ((newbuff[0] & 0xFF) << 8) | (newbuff[1] & 0xFF);
                        YPos = ((newbuff[2] & 0xFF) << 8) | (newbuff[3] & 0xFF);
                        YPos = YPos / 1000.000;
                        //XPos=XPos*173/172;  //spcial parts to make up something
                        XPos=XPos+StartPos;
                        if (!LeftToRiht.equals("true")) {
                            XPos = GunLeng - XPos;
                        }
                        if((XPos>=StartPos-1&&XPos<=GunLeng&&LeftToRiht.equals("true"))||(XPos>=-1&&XPos<=GunLeng+1&&!LeftToRiht.equals("true"))) {
                            if (recorder < 30000 && YPos > 0.01) {
                                if (LeftToRiht.equals("true")) {
                                    if ((recorder == 0) || (XPos - x[recorder] > (GunCLeng - 1))) {
                                        if(LeftOperation == 1) {
                                            x[recorder] = XPos;
                                        }else {
                                            x[recorder] = GunLeng - XPos;
                                        }
                                        y[recorder] = YPos;
                                        recorder++;
                                    }
                                } else {
                                    if ((recorder == 0) || (XPos - x[recorder] > (GunCLeng))) {
                                        if(LeftOperation == 1) {
                                            x[recorder] = XPos;
                                        }else {
                                            x[recorder] = GunLeng - XPos;
                                        }
                                        y[recorder] = YPos;
                                        recorder++;
                                    }
                                }
                            }else {
                                if(YPos <=0){
                                new AlertDialog.Builder(Measure.this)
                                        .setTitle(getString(R.string.PlConfirm))
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setMessage(getString(R.string.YSenoser_Small))
                                        .setPositiveButton(getString(R.string.Confirm), null)
                                        .show();
                                }
                            }
                            if(YPos>9.9){
                                new AlertDialog.Builder(Measure.this)
                                        .setTitle(getString(R.string.PlConfirm))
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setMessage(getString(R.string.YSenoser_Big))
                                        .setPositiveButton(getString(R.string.Confirm), null)
                                        .show();

                            }
                            if (YPos > MaxValueY) {
                                MaxValueY = YPos;
                                if(LeftOperation == 1) {
                                    MaxValueX = XPos;
                                }else {
                                    MaxValueX = GunLeng - XPos;
                                }
                                    MaxPos.setText(Integer.toString(MaxValueX));
                                    MayPos.setText(Double.toString(MaxValueY));
                            }

                            if ((YPos < MinValueY) && YPos > 0.01) {
                                MinValueY = YPos;
                                if(LeftOperation == 1) {
                                    MinValueX = XPos;
                                }else {
                                    MinValueX = GunLeng - XPos;
                                }
                                MinxPos.setText(Integer.toString(MinValueX));
                                MinyPos.setText(Double.toString(MinValueY));
                            }


                            if(LeftOperation == 1){
                                VxPos.setText(Integer.toString(XPos));
                            }else {
                                VxPos.setText(Integer.toString(GunLeng - XPos));
                            }
                            VyPos.setText(Double.toString(YPos));

                            int p = XPos * 100 / GunLeng;
                            if (p > 0 && p < 101) {
                                if (LeftToRiht.equals("true")) {
                                    mPbar.setProgress(p);
                                } else {
                                    RPbar.setProgress(p);
                                }
                            }
                            if ((GunLeng - XPos < GunCLeng + 1) && LeftToRiht.equals("true")) {
                                if(GetDirection){
                                new AlertDialog.Builder(Measure.this)
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setTitle(getString(R.string.PlConfirm))
                                        .setMessage(getString(R.string.test_mention))
                                        .setPositiveButton(getString(R.string.Confirm), null)
                                        .show();
                                }
                                GetDirection = false;
                            } else if ((XPos < GunCLeng + 1) && !LeftToRiht.equals("true")) {
                                if(GetDirection){
                                if(LeftOperation == 1) {
                                    x[recorder] = XPos;
                                }else{
                                    x[recorder] = GunLeng - XPos;
                                }
                                y[recorder] = YPos;
                                recorder++;
                                new AlertDialog.Builder(Measure.this)
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setTitle(getString(R.string.PlConfirm))
                                        .setMessage(getString(R.string.test_mention))
                                        .setPositiveButton(getString(R.string.Confirm), null)
                                        .show();
                                }
                                GetDirection = false;
                            }
                        }
                    } else {
                        //String stringHex = StringHexUtils.Bytes2HexString(readBuf);
                        //stringHex = stringHex.substring(0, (msg.arg1) * 2);
                        //Toast.makeText(getApplicationContext(), "接收到"+stringHex, Toast.LENGTH_SHORT).show();
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
                        bluetoothService.write(StringHexUtils.hexStr2Bytes("04"));
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

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (LeftOperation == 1) {
            GetDirection = true;
            OperationL.setVisibility(View.VISIBLE);
            OperationR.setVisibility(View.INVISIBLE);
        } else if (LeftOperation == 2) {
            GetDirection = true;
            OperationL.setVisibility(View.INVISIBLE);
            OperationR.setVisibility(View.VISIBLE);
        }
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
        //clear screen on
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measure, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                //end of test
                if (null != bluetoothService) {
                    bluetoothService.write(StringHexUtils.hexStr2Bytes("06"));
                }
                Intent intent = new Intent(this, Activity1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Measure.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public OnClickListener MImageBC = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //end of test
            if (null != bluetoothService) {
                bluetoothService.write(StringHexUtils.hexStr2Bytes("05"));
            }
            final CharSequence dialogTitle = getString(R.string.str_dialog_title);
            final CharSequence dialogBody = getString(R.string.str_dialog_body);
            final ProgressDialog dialog = ProgressDialog.show(Measure.this, dialogTitle, dialogBody, true);
            //save data to database

            if (recorder > 10) {
                new Thread(new Runnable() {
                    public void run() {
                        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm");
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String datetimes = formatter.format(curDate);
                        //file save
                        String DataFileName;
                        String tempPath = getApplicationContext().getFilesDir().getAbsolutePath();
                        String tempdata = datetimes;
                        tempdata = tempdata.replace(":", "");
                        tempdata = tempdata.replace("-", "");
                        tempdata = tempdata.replace(" ", "_");
                        //tempdata=tempdata.substring(2);
                        DataFileName = GunNumber + "_" + tempdata + ".txt";
                        deleteDirectory(tempPath);
                        saveDataToFile(mContext, DataFileName);
                        CopyDataFolder(tempPath);

                        //database save
                        myPCursor = myPGunInfor.selectDateT(GunNumber);
                        //delete before save
                        if (myPCursor.getCount() > 100) {
                            myPCursor.moveToFirst();
                            String MoreDataDatetime = myPCursor.getString(0);
                            myPGunInfor.delete(TABLE_NAME_Data, "Gun_Data_Time", MoreDataDatetime);
                        }

                        for (int i = 0; i < recorder; i++) {
                            myPGunInfor.insert(TABLE_NAME_Data, GunNumber, datetimes, GunLength, GunCLen, Integer.toString(x[i]), Double.toString(y[i]));
                        }
                        dialog.dismiss();
                    }
                }).start();
                Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                new AlertDialog.Builder(Measure.this)
                        .setTitle(getString(R.string.PlConfirm))
                        .setMessage(getString(R.string.data_warning))
                        .setPositiveButton(getString(R.string.Confirm), null)
                        .show();
            }
        }
    };

    private void saveDataToFile(Context context, String fileName) {

        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "GB2312");
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(getString(R.string.title1) + "\t" + GunNumber + "\r\n");
            if (GunType.equals("")) {
                GunType = "0";
            }
            bufferedWriter.write(getString(R.string.title2) + "\t" + GunType + "\r\n");
            bufferedWriter.write(getString(R.string.title3) + "\t" + GunLength + "\r\n");
            bufferedWriter.write(getString(R.string.title4) + "\t" + GunCLen + "\r\n");
            if (GunOffer.equals("")) {
                GunOffer = "0";
            }
            bufferedWriter.write(getString(R.string.title5) + "\t" + GunOffer + "\r\n");
            if (XOffset.equals("")) {
                XOffset = "0";
            }
            bufferedWriter.write(getString(R.string.title7) + "\t" + GunOffer + "\r\n");
            for (int i = 0; i < recorder; i++) {
                String ddd = Integer.toString(x[i]) + "\t" + Integer.toString((int) (y[i] * 1000)) + "\r\n";
                bufferedWriter.write(ddd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void CopyDataFolder(String oldPath) {

        try {
            File extDir = Environment.getExternalStorageDirectory();
            File FirstFilename = new File(extDir, "Measure");
            if (!FirstFilename.exists()) {
                FirstFilename.mkdirs();
            }
            File SecondFilename = new File(extDir, "Measure" + File.separator + GunNumber);
            if (!SecondFilename.exists()) {
                SecondFilename.mkdirs();
            }
            String newPath = SecondFilename.getAbsolutePath();
            //delete first before save to make sure there is new one
            orderByDate(newPath);
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {

                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    if (temp.getName().contains(GunNumber)) {
                        FileInputStream input = new FileInputStream(temp);
                        FileOutputStream output = new FileOutputStream(newPath + "/" +
                                (temp.getName()).toString());
                        byte[] b = new byte[1024 * 5];
                        int len;
                        while ((len = input.read(b)) != -1) {
                            output.write(b, 0, len);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    }
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    //CopyDataFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }

        } catch (Exception e) {
            //System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }

    public static void orderByDate(String fliePath) {
        File file = new File(fliePath);
        File[] fs = file.listFiles();
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

        //if the number of files limit to 100
        if (fs.length > 100) {
            for (int i = fs.length - 1; i > 100; i--) {
                try {
                    //flag = deleteFile(files[i].getAbsolutePath());
                    fs[i].delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                try {
                    //flag = deleteFile(files[i].getAbsolutePath());
                    flag = files[i].delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!flag) break;
            } else {
                //删除子目录
                //flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return true;
    }

    public OnTouchListener MImageBP = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                MImageButon.setBackgroundColor(Color.rgb(127, 127, 127));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                MImageButon.setBackgroundColor(Color.WHITE);
            }
            return false;
        }
    };

    private long exitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), getString(R.string.onemore_return), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                if (null != bluetoothService) {
                    bluetoothService.write(StringHexUtils.hexStr2Bytes("06"));
                }
                finish();
                //System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
