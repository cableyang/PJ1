package iflab.test;

import iflab.model.ECG;
import iflab.model.elder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Looper;
import android.util.Log;


/*
 * httpecgservice extends a time task to send message to port
 *HTTP service is too slow a
 *while socket provides a good service
 */
public class HttpECGservice 
{
	 String urlString="http://223.3.61.67:80/ecg2mysql.php";
	 ECG ecg;
	 TimerTask httpTimerTask;
	 Timer timer;
	 private Looper mServiceLooper;
	 int num2send=500;  //??????????????
	 GraphicsData graphicsData;
	 boolean httpStart;
	 boolean isready;
	  
	 double []data=new double[num2send];
	 elder elder;
	// ArrayList <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	 public HttpECGservice(ECG ecg,GraphicsData gData,elder elder)
	 {
		 graphicsData=new GraphicsData(500,1);
		 httpStart=false;
		 isready=true;
		this.ecg=ecg;
		this.elder=elder;
	//	graphicsData.data=gData.data;
		graphicsData.data=gData.datasend;
		 Log.i("httpservice", "class is running");
		 
	 }
	
	 public void seturl(String newurlString)
	{
		 urlString=newurlString;  //??url???§Ú?? ???????223.3.35.212??80
	}
	 
     public void StartThread()
     {
	  httpSendingThread.start();
     }
	
	 
	 
	 
	 public void StartSending(GraphicsData gData)
	{
		 httpStart=true;	 
		 graphicsData.data=gData.data;		
	}
	 /*
	  * ??ECG?????json??????§Ù???
	  * ????????????
	  */
	 public void Ecg2phpwithhttp()
	{
		 //
		 ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		 nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(elder.getid())));
		 nameValuePairs.add(new BasicNameValuePair("name", elder.getname()));
 
		 nameValuePairs.add(new BasicNameValuePair("bpm", String.valueOf(ecg.getbpm())));
		 
		 try{
			 HttpClient httpclient = new DefaultHttpClient();
	         HttpPost httppost = new HttpPost(urlString);
	         httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	         HttpResponse response = httpclient.execute(httppost);
	         Log.i("importing", "ending");
	        }catch(Exception e)
	        {
	          Log.e("log_tag", "Error in http connection"+e.toString());
	        } 
	}

	 Thread httpSendingThread = new Thread()
	 {
		 
		@Override
		public void run() 
		{
		  while(!currentThread().isInterrupted())
		  {
			  if (httpStart)
				{
				  if (isready)
				  {
				 	  httpStart=false;
					  isready=false;
					  Ecg2phpwithhttp();
					  isready=true;
					  Log.i("Thread", "is sending");
				  }	
				}
			  
		  }
			
			
		};
		
	 };
 
}
