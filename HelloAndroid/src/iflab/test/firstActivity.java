package iflab.test;

import iflab.model.ECG;

import org.achartengine.*;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import iflab.model.Info;
import iflab.myinterface.DataBase;
import iflab.myinterface.EcgDAO;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import iflab.model.*;


class Recv{  
	  public String data;  
	  public Recv next;      
	  public Recv(String data) {  
	       this.data = data;  
	  }  
	}  

	
public class firstActivity extends Activity  
{
	DataBase dbBase;
	Boolean flag_pause=false;
	Boolean flag_remote=false;
	Boolean flag_sql=false;
	final int GY=3,ECG=1,PPG=2;
	Node ecg_head= new Node(0);Node ppg_head= new Node(0);
	GYNode gy_headGyNode= new GYNode(0, 0, 0);
	
	Node ecg_pre,ppg_pre;
	GYNode gy_preNode;
	
	Recv headRecv;
	Recv presRecv;
	
	final int NUM_ECG=500,NUM_PPG=500,NUM_ACC=250;
	private Timer bttimer = new Timer();
	private TimerTask bttask;
	public int timercount;
	boolean httpstart; //???HTTP????
	private Timer httptiTimer = new Timer();
	private TimerTask httptTask;
	private boolean bttimeflag=true;
    public Socket tcpSocket; 
    public boolean tcpflag=false;
    public boolean tcpaccflag=false;
    public String urlString;
    public int rate;
    
    
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
    
    
    ECG ecg;
    Info info;

	
    private static Handler drhandler;
 
    Store2Sqlite store2Sqlite;
	
	 
	public static final int RATE250 = 250;
	public static final int RATE500 = 500;
	public static final int RATE1000 = 1000;
	public final int REQUESTFORSETTING=20;
	 
	private final static int REQUEST_CONNECT_DEVICE = 1;   
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; 
    private InputStream blueStream;   
    private static String []readMessage=new String[102400];
    private static String []buffer1=new String[102400];
    private static String []buffer2=new String[102400];
    DataDealing btDataDealing;
	private static String firstmessage;
	private HttpBindService mBindService;
	public  String bluetoothaddress; 
	
	 
	public final int CONNECTBLUETOOTH=1;
	public final int INFROMATION=2;
	public final int SETTING=3;
	public final int RESULT_CODE=5;
	
	public static String bluebuffString=""; 
	boolean mIsBound;
	
	Bundle bundle;
	
 
	BluetoothDevice _device = null;    
    BluetoothSocket _socket = null;     
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    
	public boolean btflag=false;
	
 
    private SensorManager mSensorManager;
    private Sensor mLight;
 
	public GraphicsData graphicsECGData;
	public GraphicsData accdataX,accdataY,accdataZ;
	
	private MyGraphics myGraphics1=null;			

	private PlotOfAcc plotOfAcc=null;
	InfoDao infoDAO; 
	EcgDAO ecgDAO;
	HttpECGservice httpECGservice; 
	 
	Button blueStartButton; 
	Button bindButton;
	Button btnperson; 
	Button btnSettingButton;
	Button btnHistoryButton;
	Button btnLocalSqlButton;
	Button btnRemoteButton;
	ToggleButton btnControlButton;
	Button btnQRS,btnHR,btnSPO2;
	

	 @Override
	  protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    // save the current data, for instance when changing screen orientation
	    outState.putSerializable("dataset", mACCDataset);
	    outState.putSerializable("renderer", mACCRender);
	    outState.putSerializable("current_series", mSeriesX);
	    outState.putSerializable("current_series", mSeriesY);
	    outState.putSerializable("current_series", mSeriesZ);
	    outState.putSerializable("current_renderer", mCurrentRenderer);
	  }

	  @Override
	  protected void onRestoreInstanceState(Bundle savedState) {
	    super.onRestoreInstanceState(savedState);
	    // restore the current data, for instance when changing the screen
	    // orientation
	    mACCDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
	    mACCRender = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
	    mSeriesX = (XYSeries) savedState.getSerializable("current_series");
	    mSeriesY = (XYSeries) savedState.getSerializable("current_series");
	    mSeriesZ = (XYSeries) savedState.getSerializable("current_series");
	    mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
	  }
	
	
	void doBindService() 
	   { 
		   bindService(new Intent(firstActivity.this,HttpBindService.class), mConnection, Context.BIND_AUTO_CREATE);
	       mIsBound = true;
	       Log.i("log_tag", "is do binding service	..");
	   }

	   void doUnbindService() {
	    	    if (mIsBound) {
	    	        // Detach our existing connection.
	    	        unbindService(mConnection);
	    	        mIsBound = false;
	    	    }
	    }
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
	 

	public static String charToHexString(byte strPart) 
    {
        
    	  String hex = Integer.toHexString(strPart & 0x00FF);
    	  if (hex.length() == 1) 
    	  {        
    		  hex = '0' + hex;      
    	  } 
    	  hex = "0x" + hex;
        return hex;
    }
 
 ServiceConnection mConnection = new ServiceConnection() 
 {  
			@Override
			public void onServiceConnected(ComponentName name,IBinder service)
			{				 
 	    	mBindService = ((HttpBindService.LocalBinder)service).getService();
 	    	mIsBound=true;
 	    	Log.i("log_tag", " on service connected..");  	
			}
			@Override
			public void onServiceDisconnected(ComponentName name)
			{
		   
			mIsBound=false;
 	        mBindService = null;   
			}
 	};
 
 	/*
 	 * 
 	 */
 	private boolean linkbluetoothdevice()
	{
 		boolean success=false;
 		
 		_device = _bluetooth.getRemoteDevice(bluetoothaddress);
     
 		 try{
         	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
         }catch(IOException e){
         	 
         }
         try
			{	
				_socket.connect();
			
			} catch (IOException e)
			{	
     		try
				{
     
				_socket.close();
				_socket = null;
				
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					//Toast.makeText(this, "????????", Toast.LENGTH_SHORT).show();	
				}            		
				// TODO Auto-generated catch block
			}
           
         try{
     		blueStream = _socket.getInputStream(); 
     		success=true;
     	
     		}catch(IOException e){
     			 
     		}	
     		return success;
	}
 		
	/*
	 * check is bluetooth is available
	 * create a thread to always open the bluetooth
	 *        and run it
	 */
	private void BluetoothCheck()
	{
		
	    if (_bluetooth == null)
        {
        	Toast.makeText(this, "蓝牙设备就绪！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
	    
	    new Thread()
	       {
	    	   @Override
			public void run()
	    	   {
	    		   if(_bluetooth.isEnabled()==false)
	    		   {
	        		_bluetooth.enable();
	    		   }
	    	   }   	   
	       }.start();
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		final int packetnum = 14;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	    LinearLayout layout_ecg = (LinearLayout)findViewById(R.id.ECG);
	    LinearLayout layout_pluse = (LinearLayout)findViewById(R.id.PPG);
	
	    info=new Info("15295508253", "yh", 25, "东南大学四牌楼2号", "健康", "15295508253", null);

	    presRecv=headRecv;
	 //   urlString="jfzhang.nju.edu.cn";
	    urlString="115.28.76.92";
	    init_data();
	    init_ACCView();
	    init_BIOView();
	
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
	
	    dbBase=new DataBase(getBaseContext(),info.getid()+"");
	    dbBase.CreateDATALIST();
	//    dbBase.deletetable();
	  //  dbBase.CheckDataList(info.id+"", "")
	     
	    
	
	    blueStartButton = (Button)findViewById(R.id.bluestart);
	    blueStartButton.setOnClickListener(listener);
	    
	    btnperson=(Button)findViewById(R.id.infor);
	    btnperson.setOnClickListener(listener);
	    
	    btnSettingButton=(Button)findViewById(R.id.setting);
	    btnSettingButton.setOnClickListener(listener);
	    
	    btnControlButton=(ToggleButton)findViewById(R.id.control);
	    btnControlButton.setOnClickListener(listener);
	   // btnControlButton.isPressed();
	    btnHistoryButton=(Button)findViewById(R.id.history);
	    btnHistoryButton.setOnClickListener(listener);
	    
	    btnLocalSqlButton=(Button)findViewById(R.id.localsql);
	    btnLocalSqlButton.setOnClickListener(listener);
	    
	    btnRemoteButton=(Button)findViewById(R.id.remotecontrol);
	    btnRemoteButton.setOnClickListener(listener);
	    
	    btnHR=(Button)findViewById(R.id.MHR);
	    btnQRS=(Button)findViewById(R.id.MQRS);
	    btnSPO2=(Button)findViewById(R.id.MSPO2);
	    btnHR.setClickable(false);btnSPO2.setClickable(false);btnQRS.setClickable(false);
	   //
	    
	    BluetoothCheck(); //check that device
		    
	    drhandler = new Handler() 
		{
	           
			@SuppressWarnings("deprecation")
			@Override
	    	public void handleMessage(Message msg) 
	    	{
	    		 switch (msg.what) 
	    		 {   
	    		  
	        	 	case CONNECTBLUETOOTH:
	        	 	  if(linkbluetoothdevice())
	        	 	  {   
	        	 	    btflag=true;
	           	 	    tcpflag=true;
	           	 	    tcpaccflag=true;
	           	 	   try
	           	 	   {
	           	 	    ReadThread.start();
	           	 	    sqlThread.start();
	           	 	    remoteThread.start();
	           	 	   }catch(Exception e)
	           	 	   {	
	           	 	   }
	           	 	   
	           	 	   
	        	 	  }
	 		 
	        		 break;
	
	        	 	case INFROMATION:  // 	
	        	 		btnHR.setText("心率:"+btDataDealing.gethr());
						btnSPO2.setText("血氧值："+btDataDealing.getspo2()+"");
						btnQRS.setText("QRS值："+btDataDealing.getqrs()+"");
		        	break;	 
		        	
	        	 	case SETTING://
	        	 		try
						{
					    urlString=bundle.getString("server");
	        	 		rate=bundle.getInt("rate");
						} catch (Exception e)
						{
							// TODO: handle exception
						}
	        	 	
	        	 		break;
	    		 }
	    		 
		   super.handleMessage(msg);
		   
	    	}
	    };     
	
	}

	/*
	 * ------------INITIAL ALL THE DATA OF ECG DATA=======
	 */
	public void init_data(){
		
		    ecg_head.next=null;
		    Node first=ecg_head;
		    for(int i=1; i<NUM_ECG; i++){
		    	Node temp=new Node(i);
		    	ecg_head.next=temp;
		    	ecg_head=temp;
		    }
		    ecg_head.next=first;
		    ecg_head=first;
		    ecg_pre=ecg_head;
		    
		    ppg_head.next=null;
		    first=ppg_head;
		    for(int i=1; i<NUM_PPG; i++){
		    	Node temp=new Node(i);
		    	ppg_head.next=temp;
		    	ppg_head=temp;
		    }
		    ppg_head.next=first;
		    ppg_head=first;
		    ppg_pre=ppg_head;
		   
		    gy_headGyNode.next=null;
		    GYNode first1=gy_headGyNode;
		   
		    for(int i=1; i<NUM_ACC; i++){
		    	
		    	GYNode temp=new GYNode(0, 0, 0);
		        temp.stamp=0;
		    	temp.next=null;
		    	first1.next=temp;
		    	first1=first1.next;
		    }
		    
		    first1.next=gy_headGyNode;
		    gy_preNode=gy_headGyNode;
		    
		   btDataDealing=new DataDealing(ecg_pre,ppg_pre,gy_preNode,readMessage);
	}
	 
	
	
	public void init_ACCView()
	{
		
	    mACCRender.setApplyBackgroundColor(true);
	    mACCRender.setBackgroundColor(Color.BLACK);
	   
	    mACCRender.setAxisTitleTextSize(16);
	    mACCRender.setChartTitleTextSize(20);
	    mACCRender.setShowLabels(true);
	  
	    mACCRender.setLabelsTextSize(15);
	    mACCRender.setLegendTextSize(15);
	     
	    mACCRender.setMargins(new int[] { 20, 30, 15, 0 });
	    mACCRender.setZoomButtonsVisible(false);
	    mACCRender.setPointSize(10);
	    
	    String seriesTitle = "ACCX " ;
	    // create a new series of data
	    XYSeries seriesx = new XYSeries(seriesTitle);
	    mACCDataset.addSeries(seriesx);
	    
	    seriesTitle = "ACCY " ;
	    // create a new series of data
	    XYSeries seriesy = new XYSeries(seriesTitle);
	    mACCDataset.addSeries(seriesy);
	    
	    seriesTitle = "ACCZ " ;
	    
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
	   

	        LinearLayout layout = (LinearLayout) findViewById(R.id.ECG);
	        mACCChartView = ChartFactory.getLineChartView(this, mACCDataset, mACCRender);
	        // enable the chart click events
	        mACCRender.setClickEnabled(false);
	        mACCRender.setSelectableBuffer(10);
	      
	        layout.addView(mACCChartView, new LayoutParams(LayoutParams.FILL_PARENT,
	            LayoutParams.FILL_PARENT));
	        boolean enabled = mACCDataset.getSeriesCount() > 0;
	      //  setSeriesWidgetsEnabled(enabled);     
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
		    mBIORender.setZoomButtonsVisible(false);
		    mBIORender.setPointSize(10);
		    
		    String seriesTitle = "ECG" ;
		    // create a new series of data
		    XYSeries seriesecg= new XYSeries(seriesTitle);
		    mBIODataset.addSeries(seriesecg);
		    
		    seriesTitle = "PPG" ;
		    // create a new series of data
		    XYSeries seriesppg = new XYSeries(seriesTitle);
		    mBIODataset.addSeries(seriesppg);
		    
		
		    mSeriesECG = seriesecg;
		    mSeriesPPG=seriesppg;
		   
		    // create a new renderer for the new series
		    XYSeriesRenderer rendererX = new XYSeriesRenderer();
		    rendererX.setColor(Color.YELLOW);
		    rendererX.setLineWidth((float)5.0);
		    mBIORender.addSeriesRenderer(rendererX);
		    
		    XYSeriesRenderer rendererY = new XYSeriesRenderer();
		    rendererY.setColor(Color.CYAN);
		    rendererY.setLineWidth((float)5.0);
		    mBIORender.addSeriesRenderer(rendererY);
		    
		    
		  
		        LinearLayout layout = (LinearLayout) findViewById(R.id.PPG);
		        mBIOChartView = ChartFactory.getLineChartView(this, mBIODataset, mBIORender);
		        // enable the chart click events
		        mBIORender.setClickEnabled(false);
		        mBIORender.setSelectableBuffer(10);
		      
		        layout.addView(mBIOChartView, new LayoutParams(LayoutParams.FILL_PARENT,
		            LayoutParams.FILL_PARENT));
		        boolean enabled = mBIODataset.getSeriesCount() > 0;
		      //  setSeriesWidgetsEnabled(enabled); 
	}
	
	/*
	  * init_phones_sensor
	  * 手机自带传感器初始化
	  */
	public void init_sensor(){
		  mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	      mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		   SensorEventListener aListener = new SensorEventListener()
			{
				
				@Override
				public void onSensorChanged(SensorEvent event)
				{
					accdataX.adddata(event.values[0]);
					accdataY.adddata(event.values[1]);
					accdataZ.adddata(event.values[2]);
				}
				
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy)
				{
				
				}
			};
		   
	      mSensorManager.registerListener(aListener, mLight,500);
	      
	}
	
	
    /*
     * -----------------UI interface----------------------------------
     * ----------------deal with all the main button message----------------
     */
	
	public OnClickListener listener = new OnClickListener()
	{
		Bundle b = new Bundle();
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v)
		{	
			 b.putString("id", String.valueOf(info.getid()));
	         b.putString("name", info.getname());
	         b.putString("age", String.valueOf(info.getage()));
	         b.putString("address", info.getaddress());
	         b.putString("phone", info.getphone());
	         b.putString("decription", info.getdescripiton());
	         
			Button btn = (Button)v;
			switch (btn.getId())
			{
			
			//下按钮第一个
			case R.id.bluestart:
				try
				{
					ReadThread.interrupt();	
					sqlThread.interrupt();
					remoteThread.interrupt();
					 _socket.close();
					 _socket = null;
					
				} catch (Exception e)
				{
					// TODO: handle exception
				}
				
					 tcpaccflag=false;
					 tcpflag=false;
			         btflag=false;
			   ReadThread.interrupt();
		       StartBluetooth();  
				break;
            
			case R.id.infor: 
				ReadThread.interrupt();	
				sqlThread.interrupt();
				remoteThread.interrupt();
				try
				{
					 _socket.close();
					
				} catch (Exception e)
				{
					// TODO: handle exception
				}
			
					 tcpaccflag=false;
					 tcpflag=false;
			         btflag=false;       
			 		 
			         Intent intent_setting1=new Intent(firstActivity.this, PatientID.class);
			         intent_setting1.putExtras(b);
			      //   startActivity(intent_setting1);
					startActivityForResult(intent_setting1, REQUESTFORSETTING);
				break;
				
			case R.id.history:
				sqlThread.interrupt();remoteThread.interrupt();
				try
				{
					ReadThread.interrupt();
					 _socket.close();
					 _socket = null;
					
				} catch (Exception e)
				{
					// TODO: handle exception
				}
				 Intent intent_history=new Intent(firstActivity.this, History.class);
				 intent_history.putExtras(b);
				// startActivity(intent_history);
			 startActivityForResult(intent_history, REQUESTFORSETTING);
				break;
				
			case R.id.setting:
				ReadThread.interrupt();
		       	sqlThread.interrupt();
		       	remoteThread.interrupt();
				try
				{
					 _socket.close();
					
				} catch (Exception e)
				{
					// TODO: handle exception
				}	 	
			       
					 tcpaccflag=false;
					 tcpflag=false;
			         btflag=false; 
			    Intent intent_setting2=new Intent(firstActivity.this, setting.class);
			    startActivityForResult(intent_setting2, REQUESTFORSETTING);
				break;

			case R.id.control:
				//ReadThread.interrupt();
				  flag_pause=!flag_pause;
				break;
			case R.id.localsql:
			//	ReadThread.interrupt();
			      flag_sql=!flag_sql;
			      break;
			case R.id.remotecontrol:
				  flag_remote=!flag_remote;
				break;
			default:
				break;
			}
		}
	};
	
	/*
	 * function check the bluetooth module 
	 *
	 */
	public void StartBluetooth()
    { 
		 
		Toast.makeText(this, " 启动蓝牙。。。", Toast.LENGTH_LONG).show();
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  
		
    	
		if(_bluetooth.isEnabled()==false)
    	{ 
    		Toast.makeText(this, "蓝牙设备不能启动！", Toast.LENGTH_LONG).show();
    		return;
    	}
    }	
		
	/*
	 * ==================================
	 *  bluetooth activity start ==
	 * ===================================
	 */
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch(requestCode){
    	case REQUEST_CONNECT_DEVICE:     
    	 
          if (resultCode == Activity.RESULT_OK) 
          {    
                bluetoothaddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                Message msg= new Message();
                msg.what=CONNECTBLUETOOTH;
                drhandler.sendMessage(msg);       
          }
               break;
 
    	case REQUESTFORSETTING:
    		try
			{
			 bundle=data.getExtras();
    		 Message msg= new Message();
             msg.what=SETTING;
             drhandler.sendMessage(msg);  
			} catch (Exception e)
			{
				// TODO: handle exception
			}
    		
    		break;
    	default:break;
    	}  
    
    }	

    
  Thread ReadThread=new Thread(){
       int count=0;
       int num_samples=0;
       String var=new String();
       int addr_buffer1=0;
       int addr_buffer2=0;
       LinkedList<String> llist = new LinkedList<String>();
  
		@Override
		public void run()
		{
		    var=String.format("%2x", 0x7e);
			// TODO Auto-generated method stub
			while (!ReadThread.isInterrupted())
			{				
				    int num;	
				    byte []bytes=new byte[102400];
		 
					try
					{
						num = blueStream.read(bytes);
						
					int count2=count+num;	
					
					for(int i = count ; i < count2; i++)
					{ 		  
					//readMessage[i]=String.format("%2x", bytes[i-count]);	
						readMessage[i]=Integer.toHexString(bytes[i-count]&0xff);
					}
					count=count+num;
					
					num_samples=btDataDealing.count(readMessage, count, var);
				 
			    if(num_samples>=3)
				    {  	
			    	//  btDataDealing.convert(readMessage,count);  
			    	  btDataDealing.DataSolving(readMessage, num_samples, count);
			    	  count=btDataDealing.getCount();  	
			    	Log.i("msg", "gystamp  "+gy_headGyNode.stamp+"gyhead  "+gy_headGyNode.x+"gyheady "+gy_headGyNode.y+" gyheadz "+gy_headGyNode.z);
			    	
			     if(flag_pause)
			       {
			    	GYNode tempGyNode=gy_headGyNode;
			    	//mACCDataset.clear();
			    	mSeriesX.clear();mSeriesY.clear();mSeriesZ.clear();
			    	for(int j=0; j<NUM_ACC;j++)
			    	  {	
			    	 	  mSeriesX.add(j,tempGyNode.x/256.0);
			    		  mSeriesY.add(j, tempGyNode.y/256.0);
			    		  mSeriesZ.add(j, tempGyNode.z/256.0);
			    		  tempGyNode=tempGyNode.next;
			    	  }
			 
			    	mSeriesECG.clear();mSeriesPPG.clear();
			    	ecg_pre=ecg_head;ppg_pre=ppg_head;
			    	for(int j1=0; j1<NUM_ECG; j1++)
			    	{
			    		mSeriesECG.add(j1,ecg_pre.data);
			    		mSeriesPPG.add(j1, ppg_pre.data);
			    		
			    		ecg_pre=ecg_pre.next;
			    		if(j1%2==0)
			    		ppg_pre=ppg_pre.next;
			    	}
	
			    		     mACCChartView.repaint();	
			    		     mBIOChartView.repaint();
			    	  }
			       }

					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
			 
					// TODO: handle exception
				try
				{
					sleep(10);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	 
		 }
		}
   };
   
   Thread sqlThread=new Thread()
   {
	   GYNode gy_1,gy_2;
	   boolean flag_gy,flag_ecg,flag_ppg;
	   int gy_stamp=0;
	   GYNode gy_temp;
	   boolean flag;
	   int ecg_stamp=0;
	   int ppg_stamp=0;
	   Node ecg_1,ecg_2;
	   Node ppg_1,ppg_2;
	   String GYx,GYy,GYz,ecgString,ppgString;
	   @Override
	   public void run()
	   {
		  
		   gy_1=gy_2=gy_headGyNode;
		   ppg_1=ppg_2=ppg_head;
		   ecg_1=ecg_head;ecg_2=ecg_head;
			 while(!sqlThread.isInterrupted())
			 {  
				  flag_ecg=false;flag_ppg=false;flag_gy=false;
				  
				 GYx="";GYy="";GYz="";
				 gy_2=btDataDealing.getGyNode();
            
             while (gy_2!=gy_1)  {
             	GYx=GYx+gy_1.x+",";   GYy=GYy+gy_1.y+",";GYz=GYz+gy_1.z+",";
 				gy_1=gy_1.next;
 				Log.i("gy stam", gy_1.stamp+"");
 				flag_gy=true;
                } 
               
               ppgString="";ppg_2=btDataDealing.getPPGNode();    
			 while (ppg_1!=ppg_2){
					ppgString=ppgString+ppg_1.data+",";
					ppg_1=ppg_1.next;
					flag_ppg=true;
					Log.i("gy stam", ppg_1.stamp+"");
				}
			 
			  ecgString="";ecg_2=btDataDealing.getECGNode();    
				 while (ecg_1!=ecg_2){
						ecgString=ecgString+ecg_1.data+",";
						ecg_1=ecg_1.next;
						flag_ecg=true;
						Log.i("ecg_stamp", ecg_1.stamp+"");
					}
			 
               if(flag_sql)
               {   	   
               dbBase.CreateTable(flag_gy, flag_ecg, flag_ppg);
               
               //检查是否存在该数据  若存在则删除重新记录
               flag=dbBase.CheckDataList(info.id+"", dbBase.getGYtablename());
          
               if(flag==false)
               {
            	   if(flag_gy)
            	   {
            		   dbBase.Insert2List(info.id+"", dbBase.getGYtablename());
            	   }
               }
            	      
                flag=dbBase.CheckDataList(info.id+"", dbBase.getECGtablename());
               // Log.i("msg", "flag="+flag);
                if(flag==false)
                	if (flag_ecg)
					{
                		 dbBase.Insert2List(info.id+"", dbBase.getECGtablename());
					}
             	     
                flag=dbBase.CheckDataList(info.id+"", dbBase.getPPGtablename());
                // Log.i("msg", "flag="+flag);
                 if(flag==false)
                	 if (flag_ppg)
					{
                		 dbBase.Insert2List(info.id+"", dbBase.getPPGtablename());
					}
              	     
           //若有数据则进行存储否则将跳过
             if (flag_gy)
			{
            	   dbBase.StoreGY(gy_stamp++, GYx, GYy, GYz);
			}
             if(flag_ecg)
             {
            	   dbBase.StoreECG(ecg_stamp++, ecgString);
             }
              if (flag_ppg)
			{
            	  dbBase.StorePPG(ppg_stamp++, ppgString);    
			}    
               }
               
				 try
				{
				   
					sleep(1000);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				     Message msg= new Message();
	                msg.what=INFROMATION;
	                drhandler.sendMessage(msg);      
			 }
	   }
	   
	    
   };
  
   
   Thread remoteThread=new Thread()
   {  
	GYNode gy_1,gy_2;
   int qrs,spo2,hr;
   boolean flag_gy,flag_ecg,flag_ppg;
   int gy_stamp=0;
   GYNode gy_temp;
   boolean flag;
   int ecg_stamp=0;
   int ppg_stamp=0;
   Node ecg_1,ecg_2;
   Node ppg_1,ppg_2;
   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");  
   String timeString;

   String GYx,GYy,GYz,ecgString,ppgString;
	   @Override
		public void run()

		{  
		   gy_1=gy_2=gy_headGyNode;
		   ppg_1=ppg_2=ppg_head;
		   ecg_1=ecg_2=ecg_head;
			 while(!currentThread().isInterrupted())
			 {
				 if(flag_remote)
				 {
						 GYx="";GYy="";GYz="";
						 gy_2=btDataDealing.getGyNode();
		            
		             while (gy_2!=gy_1)  {
		             	GYx=GYx+gy_1.x+",";   GYy=GYy+gy_1.y+",";GYz=GYz+gy_1.z+",";
		 				gy_1=gy_1.next;
		 				flag_gy=true;
		                } 
		               
		               ppgString="";ppg_2=btDataDealing.getPPGNode();    
					 while (ppg_1!=ppg_2){
							ppgString=ppgString+ppg_1.data+",";
							ppg_1=ppg_1.next;
							flag_ppg=true;
						}
					 
					  ecgString="";ecg_2=btDataDealing.getECGNode();    
						 while (ecg_1!=ecg_2){
								ecgString=ecgString+ecg_1.data+",";
								ecg_1=ecg_1.next;
								flag_ecg=true;
							}
						 
					qrs=btDataDealing.getqrs();hr=btDataDealing.gethr();spo2=btDataDealing.getspo2();
					timeString=format.format(new Date());  
				
					/*
					 ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					 nameValuePairs.add(new BasicNameValuePair("info_id",(info.getid())));
					 nameValuePairs.add(new BasicNameValuePair("time",  timeString));
					 nameValuePairs.add(new BasicNameValuePair("ecg", ecgString));
					 nameValuePairs.add(new BasicNameValuePair("ppg", ecgString));
					 nameValuePairs.add(new BasicNameValuePair("spo2", String.valueOf(spo2)));
					 nameValuePairs.add(new BasicNameValuePair("hr", String.valueOf(hr)));
					 nameValuePairs.add(new BasicNameValuePair("qrs", String.valueOf(qrs)));
					 nameValuePairs.add(new BasicNameValuePair("accx", GYx));
					 nameValuePairs.add(new BasicNameValuePair("accy", GYy));
					 nameValuePairs.add(new BasicNameValuePair("accz", GYz));
		 */
					 ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					 
					try {  
					    // 首先最外层是{}，是创建一个对象  
					    JSONObject data = new JSONObject();  
					    // 第一个键phone的值是数组，所以需要创建数组对象  
					    
					    data.put("info_id", info.getid());  
					    data.put("time", timeString); 
					    data.put("ecg", ecgString); 
					    data.put("ppg", ppgString);
					    data.put("spo2", String.valueOf(spo2));
					    data.put("hr",  String.valueOf(hr));
					    data.put("qrs", String.valueOf(qrs));
					    data.put("accx", GYx);
					    data.put("accy", GYy);
					    data.put("accz", GYz);
					    
					    nameValuePairs.add(new BasicNameValuePair("data",data.toString()));
					    
					} catch (JSONException ex) {  
					    // 键为null或使用json不支持的数字格式(NaN, infinities)  
					    throw new RuntimeException(ex);  
					}  
							
					 try{
						 HttpClient httpclient = new DefaultHttpClient();
				     //    HttpPost httppost = new HttpPost("http://"+urlString+"/bsn/index.php/putSql");
						 HttpPost httppost = new HttpPost("http://"+urlString+"/upload");
				         httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				         HttpResponse response = httpclient.execute(httppost);
				         Log.i("importing", "ending");
				        }catch(Exception e)
				        {
				          Log.e("log_tag", "Error in http connection"+e.toString());
				        } 
			
				 }
				 
				 try
				{
					sleep(2000);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
   };
   
   
   
  Thread tcpecgThread=new Thread()
   {

	@Override
	public void run()
	{
		 while(!currentThread().isInterrupted())
		 {
			if(tcpflag)
			{		
				if(graphicsECGData.full)
			 { 
				 Log.i("full", "graphicsECGData.full="+graphicsECGData.full);
					graphicsECGData.full=false;
	 
			 try
			{
				 try
					{
						tcpSocket=new Socket(urlString, 229);
					} catch (UnknownHostException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
							
					}            		
					// TODO Auto-generated catch block
					//return;
              
                try{
            		blueStream = _socket.getInputStream();  
            		//blueoutOutputStream=_socket.getOutputStream();
            		
            		}catch(IOException e){
            			
            			return;
            		}
            		      		
            		if(bThread==false){
					
					try
					{
				      PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(tcpSocket.getOutputStream())),true);			
				        
				    	JSONObject Object= new JSONObject();
				        JSONArray ecgArray = new JSONArray();
				        for (int i = 0; i < 500; i++)
						{
						    ecgArray.put(graphicsECGData.data[i]);
					       
						}
	        
	 					Object.put("id", info.getid());
						Object.put("name", info.getname());
					 	Object.put("ECG", ecgArray);
						Object.put("bpm",60);
                    
    			        out.println(Object);        
						 
						BufferedReader br=new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
						String mstr=br.readLine();
						Log.i("socket11", mstr); 
						out.close();
						br.close();
						tcpSocket.close();
						
					} catch (Exception e)
					{
						// TODO: handle exception
					}	
            		}
			} catch (Exception e)
			{
				// TODO: handle exception
			}
			
			 }
		 }
			}
			 
	}
	   
   };
   

}


