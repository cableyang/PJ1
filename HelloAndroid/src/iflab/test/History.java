package iflab.test;

import iflab.model.ECG;
import iflab.model.Info;

import org.achartengine.*;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import iflab.model.elder;
import iflab.myinterface.DataBase;
import iflab.myinterface.EcgDAO;
import iflab.myinterface.ElderDAO;
import iflab.myinterface.InfoDao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
 
	
public class History extends Activity  
{
	
	List <String>data;
	ListView lvListView;
	int size=0;
	ArrayAdapter<String> adapter;
	Info info;
	TextView infoTextView;
	InfoDao infoDao;
	TextView plotDate;
	DataBase mydataBase;
	///================================================================//
    //==========define the instruction of view===================//
    private XYMultipleSeriesDataset mACCDataset = new XYMultipleSeriesDataset();
    /** The main renderer that includes all the renderers customizing a chart. */
    private XYMultipleSeriesRenderer mACCRender = new XYMultipleSeriesRenderer();
    /** The most recently added series. */
    private XYSeries mSeriesX,mSeriesY,mSeriesZ;
    /** The most recently created renderer, customizing the current series. */
    private XYSeriesRenderer mCurrentRenderer;
    /** Button for creating a new series of data. */
    private Button mNewSeries;
    private GraphicalView mACCChartView;
    
    
    private XYMultipleSeriesDataset mBIODataset = new XYMultipleSeriesDataset();
    /** The main renderer that includes all the renderers customizing a chart. */
    private XYMultipleSeriesRenderer mBIORender = new XYMultipleSeriesRenderer();
    /** The most recently added series. */
    private XYSeries mSeriesECG,mSeriesPPG;
    /** The most recently created renderer, customizing the current series. */
    private XYSeriesRenderer mCurrentRendererECG;
    /** Button for creating a new series of data. */
    private GraphicalView mBIOChartView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {	
	 	super.onCreate(savedInstanceState);
        infoDao=new InfoDao(getBaseContext());
	    info=new Info("1", "cableyang", 23, "SEU", "15295508253", "VERY GOOD", null);
		mydataBase=new DataBase(getBaseContext(), info.id);
	 //	Toast.makeText(this, " 。。", Toast.LENGTH_LONG).show();
		setContentView(R.layout.histroy);
	  //  LinearLayout layout_ecg = (LinearLayout)findViewById(R.id.History_ECG);
	    //findViewById(R.id.History_PPG);
	   
	    infoTextView=(TextView)findViewById(R.id.infoTextView);
	    plotDate=(TextView)findViewById(R.id.DataPlot);
	    
	     get_intent_data();
	     infoTextView.setText("姓名:"+info.getname()+" ID:"+info.getid()+" 手机号:"+info.getphone()+" 住址:"+info.getaddress()); 
	      
	    lvListView=(ListView)findViewById(R.id.listView);
	   init_listview();
	   init_acc_view();
	   
	   LinearLayout layout = (LinearLayout) findViewById(R.id.History_PPG);
		  
       mACCChartView = ChartFactory.getLineChartView(this, mACCDataset, mACCRender);
     
       layout.addView(mACCChartView, new LayoutParams(LayoutParams.FILL_PARENT,
           LayoutParams.FILL_PARENT));
       boolean enabled = mACCDataset.getSeriesCount() > 0;
       
	 //   init_BIOView();
	    lvListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// TODO Auto-generated method stub
				//Log.i("msg", "positionis"+ data.get(position));
				try
				{
					Printf_data(data.get(position));
				} catch (Exception e)
				{
					// TODO: handle exception
				}
				
				
			}
	    	
		});
	  
   
	}  

	
   public void get_intent_data()
   {
	try
	{
		    Intent intent=getIntent();
			Bundle bundle=intent.getExtras();
			info.setname(bundle.getString("name"));
			info.setid(((bundle.getString("id"))));
			info.setage(Integer.parseInt((bundle.getString("age"))));
			info.setaddress(bundle.getString("address"));
			info.setphone(bundle.getString("phone"));
			info.setdescription(bundle.getString("decription"));	
	} catch (Exception e)
	{
		// TODO: handle exception
	}  
   }
   
   public void init_listview()
   {
	   data = new ArrayList<String>();

	   if (data == null)

           return;

       data=mydataBase.FindDatalistByID(info.id);

       adapter = new ArrayAdapter<String>(this, R.layout.simple_text,

               R.id.text1, data);
       // 第三步：给listview设置适配器（view）

       lvListView.setAdapter(adapter);
   }
	 
   public void init_acc_view()
   {
	  
	    mACCRender.setApplyBackgroundColor(true);
	    mACCRender.setBackgroundColor(Color.BLACK);
	   
	    mACCRender.setAxisTitleTextSize(16);
	    mACCRender.setChartTitleTextSize(20);
	    mACCRender.setShowLabels(true);
	  
	    mACCRender.setLabelsTextSize(15);
	    mACCRender.setLegendTextSize(15);
	    mACCRender.setSelectableBuffer(1000000);
	    mACCRender.setMargins(new int[] { 20, 30, 15, 0 });
	    mACCRender.setZoomButtonsVisible(true);
	 //  mACCRender.setPanEnabled(true, true);
	    mACCRender.setPointSize(10);
	   // mACCRender.setInScroll(true);
	    String seriesTitle = "Line1 " ;
	    // create a new series of data
	    XYSeries seriesx = new XYSeries(seriesTitle);
	    mACCDataset.addSeries(seriesx);
	    mACCRender.setClickEnabled(true);
	    seriesTitle = "Line2 " ;
	    // create a new series of data
	    XYSeries seriesy = new XYSeries(seriesTitle);
	    mACCDataset.addSeries(seriesy);
	    
	    seriesTitle = "Line3 " ;
	    // create a new series of data
	    XYSeries seriesz = new XYSeries(seriesTitle);
	    mACCDataset.addSeries(seriesz);
	    
	    mSeriesX = seriesx;
	    mSeriesY=seriesy;
	    mSeriesZ=seriesz;
	    // create a new renderer for the new series
	    XYSeriesRenderer rendererX = new XYSeriesRenderer();
	    rendererX.setColor(Color.YELLOW);
	    rendererX.setLineWidth((float)5.0);
	    mACCRender.addSeriesRenderer(rendererX);
	    
	    XYSeriesRenderer rendererY = new XYSeriesRenderer();
	    rendererY.setColor(Color.BLUE);
	    rendererY.setLineWidth((float)5.0);
	    mACCRender.addSeriesRenderer(rendererY);
	    
	    XYSeriesRenderer rendererZ = new XYSeriesRenderer();
	    rendererZ.setColor(Color.RED);
	    rendererZ.setLineWidth((float)5.0);
	    mACCRender.addSeriesRenderer(rendererZ);
	   
   }
	       
	         
   
   
   public void init_BIOView()
	{
		    mBIORender.setApplyBackgroundColor(true);
		    mBIORender.setBackgroundColor(Color.BLACK);
		   
		    mBIORender.setAxisTitleTextSize(16);
		    mBIORender.setChartTitleTextSize(20);
		    mBIORender.setLabelsTextSize(15);
		    mBIORender.setLegendTextSize(15);
		     
		    mBIORender.setMargins(new int[] { 20, 30, 15, 0 });
		    mBIORender.setZoomButtonsVisible(true);
		    mBIORender.setPointSize(10);
		   // mBIORender.setInScroll(true);
		    
		    String seriesTitle = "ECG" ;
		    // create a new series of data
		    XYSeries seriesecg= new XYSeries(seriesTitle);
		    mBIODataset.addSeries(seriesecg);
		    
		/*    seriesTitle = "PPG" ;
		    // create a new series of data
		    XYSeries seriesppg = new XYSeries(seriesTitle);
		    mBIODataset.addSeries(seriesppg);
		    */
		
		    mSeriesECG = seriesecg;
		//    mSeriesPPG=seriesppg;
		   
		    // create a new renderer for the new series
		    XYSeriesRenderer rendererX = new XYSeriesRenderer();
		    rendererX.setColor(Color.YELLOW);
		    rendererX.setLineWidth((float)5.0);
		    mBIORender.addSeriesRenderer(rendererX);
		    
		    /*
		    XYSeriesRenderer rendererY = new XYSeriesRenderer();
		    rendererY.setColor(Color.CYAN);
		    rendererY.setLineWidth((float)5.0);
		    mBIORender.addSeriesRenderer(rendererY);
		  */
		        LinearLayout layout = (LinearLayout) findViewById(R.id.History_PPG);
		        mBIOChartView = ChartFactory.getLineChartView(this, mBIODataset, mBIORender);
		        // enable the chart click events
		        mBIORender.setClickEnabled(false);
		        mBIORender.setSelectableBuffer(10);
		      
		        layout.addView(mBIOChartView, new LayoutParams(LayoutParams.FILL_PARENT,
		            LayoutParams.FILL_PARENT));
		        boolean enabled = mBIODataset.getSeriesCount() > 0;
		      //  setSeriesWidgetsEnabled(enabled); 
	}
   

   public void Printf_data(String tablename) 
   {
	   ArrayList<String>data;
	   String detail[]=tablename.split("_");
	   SimpleDateFormat fmt =new SimpleDateFormat("yyyyMMddHH");
	   try
	{
		java.util.Date date= fmt.parse(detail[2]);
		Log.i("msg", ""+date);
		SimpleDateFormat sdf = new SimpleDateFormat("北京时间:yyyy年MM月dd日HH时");
		String startTime = sdf.format(date);
		plotDate.setText("数据记录时间"+startTime);
	} catch (ParseException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//   String accx,accy,accz;
	   double []accx;double[]accy;double []accz;
	   
	   
	   switch(tablename.charAt(0))
	   {
	   case 'G':
		  // init_acc_view();
		  
		   mSeriesX.clear();mSeriesY.clear();mSeriesZ.clear();
		    LayoutInit();
		   data=mydataBase.GetGYdata(tablename);
		   String strx[] = data.get(0).split(",");  String stry[] = data.get(1).split(",");  String strz[] = data.get(2).split(",");  
		  
		   for(int i=0;i<strx.length-1;i++){  
		     mSeriesX.add(i/50.0, Double.parseDouble(strx[i]));
		     mSeriesY.add(i/50.0, Double.parseDouble(stry[i]));
		     mSeriesZ.add(i/50.0, Double.parseDouble(strz[i]));
		   // Log.i("msg", str[i]+" ");
		   }
		 //  mACCRender.setFitLegend(true);  
		   mACCChartView.repaint();
		   //Log.i("msg", "gy IS LOADING "+accx1[0]);
		   break;
		   
	   case 'P':
		  
		   mSeriesX.clear();mSeriesY.clear();mSeriesZ.clear(); LayoutInit();
		   data=mydataBase.GetPPGdata(tablename);
		   String strppg[] = data.get(0).split(",");  
		   for(int i=0;i<strppg.length-1;i++){  
			   mSeriesX.add(i/100.0, Double.parseDouble(strppg[i]));
		   // Log.i("msg", str[i]+" ");
		   }
		 //  mACCRender.setFitLegend(true);  
		   mACCChartView.repaint();
		 //  Log.i("msg", "PPG IS LOADING "+tablename);
		   break;
		   
	   case 'E':
		    //init_BIOView();
		   
		   mSeriesX.clear();mSeriesY.clear();mSeriesZ.clear();LayoutInit();
		   data=mydataBase.GetECGdata(tablename);
		   String strecg[] = data.get(0).split(",");  
		   for(int i=0;i<strecg.length-1;i++){  
			   mSeriesX.add(i/200.0, Double.parseDouble(strecg[i]));
		   // Log.i("msg", str[i]+" ");
		   }
		  // mACCRender.setFitLegend(true);  
		   mACCChartView.repaint();
		   Log.i("msg", "ECG IS LOADING "+tablename);
		   break;
	   }
	   
   }

   public void LayoutInit()
{
	   LinearLayout layout = (LinearLayout) findViewById(R.id.History_PPG);
		  
       mACCChartView = ChartFactory.getLineChartView(this, mACCDataset, mACCRender);
     
       layout.addView(mACCChartView, new LayoutParams(LayoutParams.FILL_PARENT,
           LayoutParams.FILL_PARENT));
       boolean enabled = mACCDataset.getSeriesCount() > 0;
       mACCRender.setClickEnabled(false);
}
}
