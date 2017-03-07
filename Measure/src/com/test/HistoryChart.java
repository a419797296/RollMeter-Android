package com.test;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HistoryChart extends Activity {

    private ToDoDB MearueData;
    private Cursor CursorM;
    private GraphicalView charts;
    XYMultipleSeriesDataset dataset;
    private List<String> datas;
    private ListView mListView;
    private DoubleAdapter adapter;
    private HorizontalListView hlv;
    private int GunTNStr;
    private int GunDTN;
    private int GunDANum;
    private int GunLeng;
    private String SelectGunN = "";
    private String SelectGunDate = "";
    private View oldView;
    private View VoldView;
    private int minx = 5000;
    private double miny = 5000.0;
    private int maxx = 0;
    private double maxy = 0.0;
    private int midx = 0;
    private double midy = 0.0;
    public int CollectLen=0;
    private TextView MinX;
    private TextView MinY;
    private TextView MaxX;
    private TextView MaxY;
    private ImageButton ReturnH;
    private RelativeLayout ReturnHB;
    private RelativeLayout ActionbarH;
    private ImageView SelRoll;
    private ImageView SelDate;
    private RelativeLayout MaxValueP;
    private RelativeLayout MinValueP;
    boolean initVlist = true;
    private final static String TABLE_NAME_Data = "measure_data";
    private RelativeLayout layout;
    private   String[] titles = new String[]{"Offset", "Max", "Min", "Blank","Middle"}; //Titles of every chart
    //if you want to add a chart line, you have to add title, value, color, style and character
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history_chart);

        /*
        //action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable myDrawable = getResources().getDrawable(R.drawable.ahelp);
        actionBar.setBackgroundDrawable(myDrawable);
        */
        ActionbarH = (RelativeLayout) findViewById(R.id.relativeLayoutTopH);
        SelRoll = (ImageView) findViewById(R.id.imageViewSelRoll);
        SelDate = (ImageView) findViewById(R.id.imageViewSelDate);
        MaxValueP = (RelativeLayout) findViewById(R.id.HlinearLayoutMax);
        MinValueP = (RelativeLayout) findViewById(R.id.HlinearLayoutMin);
        Locale curLocale = getResources().getConfiguration().locale;
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            ActionbarH.setBackgroundResource(R.drawable.ahistory);
            SelRoll.setBackgroundResource(R.drawable.selroll);
            SelDate.setBackgroundResource(R.drawable.seldate);
            MaxValueP.setBackgroundResource(R.drawable.max);
            MinValueP.setBackgroundResource(R.drawable.min);
        } else {
            ActionbarH.setBackgroundResource(R.drawable.aehistory);
            SelRoll.setBackgroundResource(R.drawable.selrolle);
            SelDate.setBackgroundResource(R.drawable.seldatee);
            MaxValueP.setBackgroundResource(R.drawable.maxe);
            MinValueP.setBackgroundResource(R.drawable.mine);
        }

        //make sure the screen is on all the time
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //init database
        MearueData = new ToDoDB(this);

        MinX = (TextView) findViewById(R.id.HtextViewxMinValue);
        MinY = (TextView) findViewById(R.id.HtextViewyMinValue);
        MaxX = (TextView) findViewById(R.id.HtextViewxMaxValue);
        MaxY = (TextView) findViewById(R.id.HtextViewyMaxValue);

        ReturnH = (ImageButton) findViewById(R.id.imageButtonReturnH);    //return button
        ReturnHB=(RelativeLayout) findViewById(R.id.imageButtonReturnHB);  // clickable
        ReturnHB.setClickable(true);
        ReturnHB.setOnClickListener(ReturnHBC);
        ReturnHB.setOnTouchListener(ReturnHBP);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new OnItemClickListenerImpl());
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemLongClickListener(new RicOnLongClick());

        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = 620;
        mListView.setLayoutParams(params);


        hlv = (HorizontalListView) findViewById(R.id.horizon_listview2);
        hlv.setOnItemClickListener(new OnItemClickListeneChart());
        hlv.setOnItemLongClickListener(new PicOnLongClick());

        HVList();


        //chart
        dataset = new XYMultipleSeriesDataset();
        InitChart();

    }

    private class RicOnLongClick implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            if (GunTNStr < 2) {
                return true;
            }
            ListView Hlist = (ListView) parent;

            if (position != 0) {
                mListView.getChildAt(0).setBackground(null);
            }
            SelectGunN = datas.get(position * 2);
            if (SelectGunN.equals("")) {
                return true;
            }
            if (VoldView != null) {

                VoldView.setBackgroundDrawable(null);
            }

            VoldView = view;
            VoldView.setBackgroundResource(R.drawable.selectroll);

            String AlterMessage = getString(R.string.title1) + SelectGunN + "\n" + getString(R.string.DateTime) + getString(R.string.date_warning);
            new AlertDialog.Builder(HistoryChart.this)
                    .setTitle(getString(R.string.PlConfirmDelete))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(AlterMessage)
                    .setPositiveButton(getString(R.string.Confirm), new OnDeleteRoll())
                    .setNegativeButton(getString(R.string.Cancles), null)
                    .show();
            return true;
        }

    }

    private class OnDeleteRoll implements DialogInterface.OnClickListener {//添加确定按钮

        @Override

        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
            // TODO Auto-generated method stub
            if (GunTNStr > 1) {
                MearueData.delete(TABLE_NAME_Data, "gun_num", SelectGunN);

                File extDir = Environment.getExternalStorageDirectory();
                File FirstFilename = new File(extDir, "Measure");
                if (!FirstFilename.exists()) {
                    FirstFilename.mkdirs();
                }
                if (SelectGunN.equals("")) {
                    return;
                }
                File SecondFilename = new File(extDir, "Measure" + File.separator + SelectGunN);
                if (!SecondFilename.exists()) {
                    SecondFilename.mkdirs();
                }
                String newPath = SecondFilename.getAbsolutePath();

                if (!newPath.equals("")) {
                    deleteDirectory(newPath);
                }
                HVList();

                UpdateChart();
                //orderByDate(newPath,SelectGunDate);
            }
        }
    }

    private class PicOnLongClick implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            HorizontalListView Hlist = (HorizontalListView) parent;

            if (position != 0) {
                hlv.getChildAt(0).setBackground(null);
            }
            SelectGunDate = (String) Hlist.getItemAtPosition(position);
            if (SelectGunDate.equals("") || SelectGunN.equals("")) {
                return true;
            }
            if (oldView != null) {

                oldView.setBackgroundDrawable(null);
            }

            oldView = view;
            view.setBackgroundResource(R.drawable.selectdate);

            String AlterMessage = getString(R.string.title1) + SelectGunN + "\n" + getString(R.string.DateTime) + SelectGunDate;
            new AlertDialog.Builder(HistoryChart.this)
                    .setTitle(getString(R.string.PlConfirmDelete))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(AlterMessage)
                    .setPositiveButton(getString(R.string.Confirm), new OnDeletedate())
                    .setNegativeButton(getString(R.string.Cancles), null)
                    .show();
            return true;
        }
    }
    //delte some data from horizon list
    private class OnDeletedate implements DialogInterface.OnClickListener {//添加确定按钮

        @Override

        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
            // TODO Auto-generated method stub
            MearueData.delete(TABLE_NAME_Data, "Gun_Data_Time", SelectGunDate);
            String DateFileNamePart=SelectGunDate;
            File extDir = Environment.getExternalStorageDirectory();
            File FirstFilename = new File(extDir, "Measure");
            if (!FirstFilename.exists()) {
                FirstFilename.mkdirs();
            }
            if (SelectGunN.equals("")) {
                return;
            }
            File SecondFilename = new File(extDir, "Measure" + File.separator + SelectGunN);
            if (!SecondFilename.exists()) {
                SecondFilename.mkdirs();
            }
            String newPath = SecondFilename.getAbsolutePath();

            //if (GunTNStr > 1) {
            if (GunDTN > 1) {
                HlistGetdata(SelectGunN);
                UpdateChart();
                DeleFiles(newPath, DateFileNamePart); //delete file
            } else {
                /*
                if (hlv.getCount() > 0) {
                    hlv.removeAllViews();
                    deleteDirectory(newPath);
                }*/
                HVList();
                deleteDirectory(newPath);
            }
        }
    }

    public View.OnClickListener ReturnHBC = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HistoryChart.this, Activity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            HistoryChart.this.finish();
            return;
        }
    };
    public View.OnTouchListener ReturnHBP = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ReturnH.setBackgroundResource(R.drawable.returnbp);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                ReturnH.setBackgroundResource(R.drawable.returnb);
            }
            return false;
        }
    };


    //vertical list
    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView Vlist = (ListView) parent;
            if (position != 0) {
                mListView.getChildAt(0).setBackground(null);
            }

            if (VoldView != null) {
                VoldView.setBackgroundDrawable(null);
            }

            VoldView = view;
            VoldView.setBackgroundResource(R.drawable.selectroll);
            //SelectGunN= (String)Vlist.getItemAtPosition(position);
            SelectGunN = datas.get(position * 2);
            String tempstr=datas.get(position * 2+1);
            tempstr=tempstr.substring(tempstr.indexOf("\n")+1,tempstr.indexOf("\n")+5);
            GunLeng=Integer.valueOf(tempstr);
            //view.setBackgroundResource(android.R.color.holo_blue_dark);
            if (SelectGunN.equals("")) {
                return;
            }
            HlistGetdata(SelectGunN);
            UpdateChart();
        }
    }

    //horizonlist
    private class OnItemClickListeneChart implements AdapterView.OnItemClickListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HorizontalListView Hlist = (HorizontalListView) parent;

            if (position != 0) {
                hlv.getChildAt(0).setBackground(null);
            }
            SelectGunDate = (String) Hlist.getItemAtPosition(position);
            if (SelectGunDate.equals("")) {
                return;
            }
            if (oldView != null) {

                oldView.setBackgroundDrawable(null);
            }

            oldView = view;
            view.setBackgroundResource(R.drawable.selectdate);
            //hlv.setSelection(0);
            UpdateChart();
        }
    }

    //update chart
    private void UpdateChart() {
        //String[] titles = new String[]{"Offset", "MaxMin"};
        List x = new ArrayList();
        List y = new ArrayList();
        if (SelectGunN.equals("") || SelectGunDate.equals("")) {
            return;
        }

        CursorM = MearueData.selectGunDataXY(SelectGunN, SelectGunDate);
        if (CursorM == null) {
            return;
        }
        if (CursorM == null) {

        } else {
            int datanumber = CursorM.getCount();
            double[] xseris = new double[datanumber];
            double[] yseris = new double[datanumber];
            if (CursorM.getCount() > 0) {
                int m;
                maxy = 0;
                miny = 5000;
                CursorM.moveToFirst();
                int TempX1=0, TempX2=0,TempXmin=2000,TempXmin2=2000;
                double TempY1 =0.0, TempY2=0.0;
                for (m = 0; m < datanumber; m++) {
                    xseris[m] = Double.valueOf(CursorM.getInt(0)).doubleValue();
                    yseris[m] = CursorM.getDouble(1);
                    int XPos = CursorM.getInt(0);
                    Double YPos = CursorM.getDouble(1);
                    if (YPos > maxy) {
                        maxy = YPos;
                        maxx = XPos;
                        MaxX.setText(Integer.toString(maxx));
                        MaxY.setText(Double.toString(maxy));
                    }
                    if (YPos < miny) {
                        minx = XPos;
                        miny = YPos;
                        MinX.setText(Integer.toString(minx));
                        MinY.setText(Double.toString(miny));
                    }

                    midx=GunLeng/2;
                    if (midx-XPos==0) {
                        // midx=XPos;
                        midy=YPos;
                    }else if(midx-XPos>0&&midx-XPos<TempXmin){
                        TempXmin=midx-XPos;
                        TempX1=XPos;
                        TempY1=YPos;
                    }else if(XPos-midx>0&&XPos-midx<TempXmin2){
                        TempXmin2=XPos-midx;
                        TempX2=XPos;
                        TempY2=YPos;
                    }
                    if (TempX1!=0&&TempX2!=0){
                        midy=(TempY2-TempY1)/(TempX2-TempX1)*(midx-TempX1)+TempY1;
                        DecimalFormat formater = new DecimalFormat();
                        //保留几位小数
                        formater.setMaximumFractionDigits(3);
                        //模式  四舍五入
                        formater.setRoundingMode(RoundingMode.UP);
                        midy=Double.valueOf(formater.format(midy));
                    }if(TempX1!=0&&TempX2==0){
                        midy=TempY1;
                    }if(TempX1==0&&TempX2!=0){
                        midy=TempY2;
                    }
                    if (m < datanumber - 1) {
                        CursorM.moveToNext();
                    }
                }
                x.add(xseris);
                y.add(yseris);
            }
            double[] xmin = new double[1];
            double[] ymin = new double[1];
            xmin[0] = minx;
            ymin[0] = miny;
            x.add(xmin);
            y.add(ymin);
            double[] xmax = new double[1];
            double[] ymax = new double[1];
            xmax[0] = maxx;
            ymax[0] = maxy;
            x.add(xmax);
            y.add(ymax);
            double[] xmaxe = new double[2];
            double[] ymaxe = new double[2];
            xmaxe[0] = GunLeng+5;
            ymaxe[0] = maxy * 1.07;
            xmaxe[1] = -5;
            ymaxe[1] = miny * 0.93;
            x.add(xmaxe);
            y.add(ymaxe);

            double[] xmid = new double[3];
            double[] ymid = new double[3];
            xmid[0] = midx;
            ymid[0] = miny;
            xmid[1] = midx;
            ymid[1] = midy;
            xmid[2] = midx;
            ymid[2] = maxy;
            x.add(xmid);
            y.add(ymid);
            //refresh chart

            refresh(titles, x, y, charts);
        }
    }

    //horizon and vertical list init
    private void HVList() {
        datas = new ArrayList<String>();

        String GunNtemp;
        if(mListView.getCount()>0){
            //mListView.removeAllViews();
        }
        CursorM = MearueData.selectGunN();
        if (CursorM == null) {
            return;
        }
        GunTNStr = CursorM.getCount(); //get all the roller count in database

        if (GunTNStr > 0) {
            int i;
            CursorM.moveToFirst();
            String[] GunNAStr = new String[GunTNStr];
            //for (i = 0; i < GunTNStr; i++) {
            for (i = GunTNStr - 1; i > -1; i--) {
                GunNtemp = CursorM.getString(0);
                GunNAStr[i] = GunNtemp;
                //datas.add(GunNtemp);
                //get infortable
                //if (i < GunTNStr - 1) {
                if (i > 0) {
                    CursorM.moveToNext();
                }
            }
            for (i = 0; i < GunTNStr; i++) {
                String tempstr = GunNAStr[i];
                CursorM = MearueData.selectGunType(tempstr);
                if (!(CursorM == null)) {
                    if (CursorM.getCount() > 0) {
                        CursorM.moveToLast();
                        String typetemp;
                        typetemp = CursorM.getString(0);
                        typetemp = typetemp + "\n" + CursorM.getString(1);
                        typetemp = typetemp + "mm";
                        datas.add(tempstr);
                        datas.add(typetemp);
                        String CollectionLen= CursorM.getString(2);
                        CollectLen=Integer.parseInt(CollectionLen);
                       // GunLeng=Integer.parseInt( CursorM.getString(1));
                    }
                }
            }
            if (GunTNStr < 8) {  //less than 8, the left list will be empty, should add blank
                for (int k = 0; k < 8 - GunTNStr; k++) {
                    datas.add("");
                    datas.add("");
                }

            }

            SelectGunN = GunNAStr[0];
            String tempstr=datas.get(1);
            tempstr=tempstr.substring(tempstr.indexOf("\n")+1,tempstr.indexOf("\n")+5);
            GunLeng=Integer.valueOf(tempstr);
            adapter = new DoubleAdapter(this, datas);
            try {
                mListView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //set first one selected
            if (datas.size() > 0) {
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (initVlist) {
                            mListView.getChildAt(0).setBackgroundResource(R.drawable.selectroll);
                            initVlist = false;
                        } else {
                            mListView.getChildAt(0).setBackground(null);
                        }
                    }
                });
            }

            //get datetime list
            HlistGetdata(SelectGunN);

        } else {
            //mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GunNumStr));
            //hlv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mListStr));
        }

    }

    // add data to horizaton list
    private void HlistGetdata(String SelectGun) {
        //get datetime
        if (SelectGun.equals("")) {
            return;
        }

        //clear horizen list first
        if (hlv.getCount() > 0) {
            hlv.removeAllViews();
        }

        CursorM = MearueData.selectDateT(SelectGun);
        if (CursorM == null) {
            return;
        }
        GunDTN = CursorM.getCount(); //get all the date of data for one roller

        if (GunDTN > 0) {
            int k;
            CursorM.moveToFirst();
            String[] GunNDTStr = new String[GunDTN];

            for (k = GunDTN - 1; k > -1; k--) {
                GunNDTStr[k] = CursorM.getString(0);
                if (k > 0) {
                    CursorM.moveToNext();
                }
            }

            SelectGunDate = GunNDTStr[0];
            ArrayAdapter Hapater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GunNDTStr);
            hlv.setAdapter(Hapater);
            hlv.post(new Runnable() {
                @Override
                public void run() {
                    hlv.getChildAt(0).setBackgroundResource(R.drawable.selectdate);
                }
            });
        }
        //hlv.noti
    }

    //init chart
    private void InitChart() {

        List x = new ArrayList();
        List y = new ArrayList();
        if (SelectGunN.equals("") || SelectGunDate.equals("")) {
            return;
        }
        CursorM = MearueData.selectGunDataXY(SelectGunN, SelectGunDate);
        if (CursorM == null) {
            return;
        }
        if (CursorM == null) {
        } else {
            int datanumber = CursorM.getCount();
            double[] xseris = new double[datanumber];
            double[] yseris = new double[datanumber];
            if (CursorM.getCount() > 0) {
                int m;
                CursorM.moveToFirst();
                int TempX1=0, TempX2=0,TempXmin=2000,TempXmin2=2000;;
                double TempY1 =0.0, TempY2=0.0;
                for (m = 0; m < datanumber; m++) {
                    xseris[m] = Double.valueOf(CursorM.getInt(0)).doubleValue();
                    yseris[m] = CursorM.getDouble(1);
                    int XPos = CursorM.getInt(0);
                    double YPos = CursorM.getDouble(1);
                    if (YPos > maxy) {
                        maxy = YPos;
                        maxx = XPos;
                        MaxX.setText(Integer.toString(maxx));
                        MaxY.setText(Double.toString(maxy));
                    }
                    if (YPos < miny) {
                        minx = XPos;
                        miny = YPos;
                        MinX.setText(Integer.toString(minx));
                        MinY.setText(Double.toString(miny));
                    }

                     midx=GunLeng/2;
                    if (midx-XPos==0) {
                        // midx=XPos;
                        midy=YPos;
                    }
                    if (midx-XPos==0) {
                        // midx=XPos;
                        midy=YPos;
                    }else if(midx-XPos>0&&midx-XPos<TempXmin){
                        TempXmin=midx-XPos;
                        TempX1=XPos;
                        TempY1=YPos;
                    }else if(XPos-midx>0&&XPos-midx<TempXmin2){
                        TempXmin2=XPos-midx;
                        TempX2=XPos;
                        TempY2=YPos;
                    }
                    if (TempX1!=0&&TempX2!=0){
                        midy=(TempY2-TempY1)/(TempX2-TempX1)*(midx-TempX1)+TempY1;
                        DecimalFormat formater = new DecimalFormat();
                        //保留几位小数
                        formater.setMaximumFractionDigits(3);
                        //模式  四舍五入
                        formater.setRoundingMode(RoundingMode.UP);
                        midy=Double.valueOf(formater.format(midy));
                    }else if(TempX1!=0&&TempX2==0){
                        midy=TempY1;
                    }if(TempX1==0&&TempX2!=0){
                        midy=TempY2;
                    }
                    if (m < datanumber - 1) {
                        CursorM.moveToNext();
                    }
                }
                x.add(xseris);
                y.add(yseris);
            } else {
                x.add(new double[]{1, 3, 5, 7, 9, 11});

                y.add(new double[]{3, 14, 5, 30, 20, 25});
            }
            double[] xmin = new double[1];
            double[] ymin = new double[1];
            xmin[0] = minx;
            ymin[0] = miny;
            x.add(xmin);
            y.add(ymin);
            double[] xmax = new double[1];
            double[] ymax = new double[1];
            xmax[0] = maxx;
            ymax[0] = maxy;
            x.add(xmax);
            y.add(ymax);
            double[] xmaxe = new double[2];
            double[] ymaxe = new double[2];
            xmaxe[0] =GunLeng+5;
            ymaxe[0] = maxy * 1.07;
            xmaxe[1] =-5;
            ymaxe[1] = miny * 0.93;
            x.add(xmaxe);
            y.add(ymaxe);

            double[] xmid = new double[3];
            double[] ymid = new double[3];
            xmid[0] = midx;
            ymid[0] = miny;
            xmid[1] = midx;
            ymid[1] = midy;
            xmid[2] = midx;
            ymid[2] = maxy;
            x.add(xmid);
            y.add(ymid);

            dataset = buildDataset(titles, x, y);
            InitChartSet();

        }
    }

    private void InitChartSet() {
                                    //history data, min, max, border, middle
        int[] colors = new int[]{Color.RED, Color.RED, Color.BLUE, Color.WHITE, Color.BLACK};
        PointStyle[] styles = new PointStyle[]{PointStyle.POINT, PointStyle.CIRCLE, PointStyle.CIRCLE, PointStyle.CIRCLE, PointStyle.POINT};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

        setChartSettings(renderer, "", getString(R.string.history_x), getString(R.string.history_y), -1, 2500, 0, 5000, Color.BLACK, Color.BLACK);
        charts = ChartFactory.getLineChartView(this, dataset, renderer);
        //charts = ChartFactory.getScatterChartView(this, dataset, renderer);
        layout = (RelativeLayout) findViewById(R.id.chartGraphicalView);
        layout.addView(charts, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    protected XYMultipleSeriesDataset buildDataset(String[] titles,
                                                   List xValues,
                                                   List yValues) {

        int length = titles.length;                  //有几条线
        for (int i = 0; i < length; i++) {
            XYSeries series = new XYSeries(titles[i]);    //根据每条线的名称创建
            double[] xV = (double[]) xValues.get(i);                 //获取第i条线的数据
            double[] yV = (double[]) yValues.get(i);
            int seriesLength = xV.length;                 //有几个点

            for (int k = 0; k < seriesLength; k++)        //每条线里有几个点
            {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }

        return dataset;
    }

    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            if (i == 0) {  //mid
                r.setColor(colors[i]);
                r.setPointStyle(styles[i]);
                r.setFillPoints(fill);
            } else if (i == 1 || i == 2) { //for min,max,
                r.setColor(colors[i]);
                r.setPointStyle(styles[i]);
                r.setDisplayChartValues(true);
                r.setChartValuesTextSize(15);
                r.setFillPoints(true);
            } else if (i == 3) { //border
                r.setColor(colors[i]);
                r.setPointStyle(styles[i]);
                r.setFillPoints(true);
            } else if ( i==4) {  //mid
                r.setColor(colors[i]);
                r.setPointStyle(styles[i]);
                r.setDisplayChartValues(true);
                r.setChartValuesTextSize(15);
                r.setFillPoints(fill);
            } else {
                r.setColor(colors[i]);
                r.setPointStyle(styles[i]);
                r.setFillPoints(true);
            }
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title,
                                    String xTitle, String yTitle, double xMin,
                                    double xMax, double yMin, double yMax,
                                    int axesColor, int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setChartTitleTextSize(20);
        renderer.setXTitle(xTitle);
        renderer.setAxisTitleTextSize(20);
        renderer.setLabelsTextSize(20);
        renderer.setYTitle(yTitle);
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        //renderer.setYLabelsPadding(10);
        //renderer.setXAxisMin(xMin);
        //renderer.setXAxisMax(xMax);
        //renderer.setYAxisMin(yMin);
        //renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        //renderer.setDisplayValues(true);
        //renderer.setMargins(new int[]{0,0,0,0});
        renderer.setZoomButtonsVisible(true);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setShowLegend(false);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.GRAY);
        renderer.setAntialiasing(true);
        renderer.setFitLegend(true);
    }

    public void refresh(String[] titles, List<double[]> xValues, List<double[]> yValues, GraphicalView view) {

        layout.removeAllViews();
        cleardata();
        for (int i = 0; i < xValues.size(); i++) {
            XYSeries series = new XYSeries(titles[i]);

            double[] xV = xValues.get(i);
            double[] yV = yValues.get(i);
            int seriesLength = xV.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }

            dataset.addSeries(series);
            //view.repaint();
        }
        /*
        int[] colors = new int[]{Color.RED, Color.GREEN};
        PointStyle[] styles = new PointStyle[]{PointStyle.POINT, PointStyle.CIRCLE};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

        setChartSettings(renderer, "", getString(R.string.history_x), getString(R.string.history_y), -1, 2500, 0, 5000, Color.BLUE, Color.BLACK);
        charts = ChartFactory.getLineChartView(this, dataset, renderer);
        layout.addView(charts);
        */
        InitChartSet();
//	 view.repaint();
    }

    public void cleardata() {
        while (dataset.getSeries().length > 0) {
            XYSeries series = dataset.getSeries()[0];
            dataset.removeSeries(series);
            series.clear();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
        //clear screen on
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, Activity1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                HistoryChart.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }else {
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }else{
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
        dirFile.delete();
        return true;
    }

    public void DeleFiles(String fliePath, String deletename) {
        File file = new File(fliePath);
        File[] fs = file.listFiles();
        deletename = deletename.replace(":", "");
        deletename = deletename.replace("-", "");
        deletename = deletename.replace(" ", "_");

        for (int i = 0; i < fs.length; i++) {
            try {
                //flag = deleteFile(files[i].getAbsolutePath());

                if (fs[i].getName().contains(deletename)) {
                    fs[i].delete();
                }else{}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private long exitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), getString(R.string.onemore_return), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                //System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
