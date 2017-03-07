package com.test;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.test.HorizontalListView;
//not used so far
public class History extends Activity {

    private final static String TABLE_NAME_Data = "measure_data";
    private ToDoDB MearueData;
    private Cursor CursorM;
    private int GunTNStr;
    private int GunDTN;
    private int GunDANum;
    private String SelectGunN="";
    private String SelectGunDate="";
    private GraphicalView charts;
    XYMultipleSeriesDataset dataset;


    private HorizontalListView hlv;
    private ListView mListView = null;
    private ArrayAdapter listAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);
        /*
        //action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable myDrawable = getResources().getDrawable(R.drawable.abouta);
        actionBar.setBackgroundDrawable(myDrawable);
        //make sure the screen is on all the time
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        */


        //init database
        MearueData = new ToDoDB(this);
        //init two lists
        mListView = (ListView) findViewById(R.id.listbuttons);

        hlv = (HorizontalListView) findViewById(R.id.horizon_listview);
        //String[] GunNAStr={"333","444","555"};
        //listAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GunNAStr);
        //mListView.setAdapter(listAdapter);
       HVList();
        mListView.setOnItemClickListener(new OnItemClickListenerImpl());
        hlv.setOnItemClickListener(new OnItemClickListeneChart());
        //init head lines
        InitHeadLines();
        //chart
        dataset = new XYMultipleSeriesDataset();
        InitChart();
    }

    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           ListView Vlist = (ListView)parent;
            SelectGunN= (String)Vlist.getItemAtPosition(position);
            //view.setBackgroundResource(android.R.color.holo_blue_dark);
            if(SelectGunN.equals("")){
                return;
            }
            HlistGetdata(SelectGunN);
            UpdateChart();
        }
    }

    private class OnItemClickListeneChart implements AdapterView.OnItemClickListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HorizontalListView Hlist = (HorizontalListView)parent;
            SelectGunDate= (String)Hlist.getItemAtPosition(position);
            if (SelectGunDate.equals("")){
                return;
            }
            Toast.makeText(getApplicationContext(), SelectGunDate, Toast.LENGTH_SHORT).show();
           UpdateChart();
        }
    }

    //init horizon vertical list
    private void HVList()
    {
        CursorM = MearueData.selectGunN();
        if(CursorM==null){
            return;
        }
        GunTNStr = CursorM.getCount();
        TextView textGunN = (TextView) findViewById(R.id.textViewGunTotal);
        textGunN.setText(Integer.toString(GunTNStr));

        if (GunTNStr > 0) {
            int i;
            CursorM.moveToFirst();
            String[] GunNAStr = new String[GunTNStr];
            for (i = 0; i < GunTNStr; i++) {
                GunNAStr[i] = CursorM.getString(0);
                if (i < GunTNStr - 1) {
                    CursorM.moveToNext();
                }
            }
            SelectGunN=GunNAStr[0];
            listAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, GunNAStr);
            mListView.setAdapter(listAdapter);
            HlistGetdata(SelectGunN);

        }
        else
        {
            //mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GunNumStr));

            //hlv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mListStr));
        }
    }

    private void HlistGetdata(String SelectGun){
        //get datetime
        if (SelectGun.equals(""))
        {
            return;
        }
        CursorM = MearueData.selectDateT(SelectGun);
        if(CursorM==null){
            return;
        }
        GunDTN=CursorM.getCount();

        int k;
        CursorM.moveToFirst();
        String[] GunNDTStr = new String[GunDTN];

        for (k = GunDTN-1; k > -1; k--) {
            GunNDTStr[k] = CursorM.getString(0);
            if (k >0) {
                CursorM.moveToNext();
            }
        }
        SelectGunDate=GunNDTStr[0];
        hlv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GunNDTStr));
        //hlv.noti
    }
    //all the headline numbers init here
    private void InitHeadLines(){
        CursorM = MearueData.selectGunDatatTotalNumber();
        if(CursorM==null){
            return;
        }
        GunDANum = CursorM.getCount();
        if(GunDANum>0) {
            CursorM.moveToLast(); // get the lastest
            TextView textGunDtime = (TextView) findViewById(R.id.textViewGunDTime);
            TextView textGunTotal = (TextView) findViewById(R.id.textViewGunTotal);
            TextView textGunDTNum = (TextView) findViewById(R.id.textViewGunDTNum);
            String datatimet=CursorM.getString(0);
            datatimet=datatimet.substring(5);
            textGunDtime.setText(datatimet);
            textGunTotal.setText(Integer.toString(GunTNStr));
            textGunDTNum.setText(Integer.toString(GunDANum));
        }
    }

    //update chart
    private  void UpdateChart(){
        String[] titles = new String[]{"Offset"};
        List x = new ArrayList();
        List y = new ArrayList();
        if (SelectGunN.equals("")||SelectGunDate.equals("")){
            return;
        }

        CursorM = MearueData.selectGunDataXY(SelectGunN, SelectGunDate);
        if(CursorM==null){
            return;
        }
        if(CursorM==null){

        }else{
        int datanumber = CursorM.getCount();
        double[] xseris= new double[datanumber];
        double[] yseris= new double[datanumber];
        if(CursorM.getCount()>0) {
            int m;
            CursorM.moveToFirst();
            for (m = 0; m< datanumber; m++) {
                xseris[m]=Double.valueOf(CursorM.getInt(0)).doubleValue();
                yseris[m]=Double.valueOf(CursorM.getInt(1)).doubleValue();
                if (m < datanumber - 1) {
                    CursorM.moveToNext();
                }
            }
            x.add(xseris);
            y.add(yseris);
        }
        //refresh chart
        refresh(titles, x, y, charts);
        }
    }

    //init chart
    private  void InitChart()
    {
        String[] titles = new String[]{"Offset"};

        List x = new ArrayList();
        List y = new ArrayList();
        if(SelectGunN.equals("")||SelectGunDate.equals(""))
        {
            return;
        }
        CursorM = MearueData.selectGunDataXY(SelectGunN, SelectGunDate);
        if(CursorM==null){
            return;
        }
        if(CursorM==null){}else {
            int datanumber = CursorM.getCount();
            double[] xseris = new double[datanumber];
            double[] yseris = new double[datanumber];
            if (CursorM.getCount() > 0) {
                int m;
                CursorM.moveToFirst();
                for (m = 0; m < datanumber; m++) {
                    xseris[m] = Double.valueOf(CursorM.getInt(0)).doubleValue();
                    yseris[m] = Double.valueOf(CursorM.getInt(1)).doubleValue();
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
            dataset = buildDataset(titles, x, y);


            int[] colors = new int[]{Color.RED};
            PointStyle[] styles = new PointStyle[]{PointStyle.POINT, PointStyle.TRIANGLE};
            XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

            setChartSettings(renderer, "测量时间", "辊子长度(mm)", "辊子中高(um)", -1, 2000, -1, 5000, Color.BLUE, Color.BLACK);
            charts = ChartFactory.getLineChartView(this, dataset, renderer);
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.chartGraphicalView);
            layout.addView(charts, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
                History.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
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
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        //renderer.setMargins(new int[]{0,0,0,0});
        renderer.setMarginsColor(Color.WHITE);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.CYAN);
        renderer.setAntialiasing(true);
    }
    public void refresh(String[] titles, List<double[]> xValues, List<double[]> yValues, GraphicalView view){

        cleardata();
        for (int i = 0; i < xValues.size(); i++) {
            XYSeries series= new XYSeries(titles[i]);
//		  series.remove(i);
//		  dataset.removeSeries(series);
//		  series.clear();
            double[] xV = xValues.get(i);
            double[] yV = yValues.get(i);
            int seriesLength = xV.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
            view.repaint();
        }
//	 view.repaint();
    }

    public void cleardata(){
        while(dataset.getSeries().length > 0){
            XYSeries series= dataset.getSeries()[0];
            dataset.removeSeries(series);
            series.clear();
        }
    }

    public void onResume(){
        super.onResume();
    }
    public void onStop(){
        super.onStop();
        //clear screen on
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
