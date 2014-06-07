package iflab.test;
import android.util.Log;
import iflab.model.ECG;
import iflab.model.elder;
import iflab.myinterface.EcgDAO;

public class Store2Sqlite
{
  ECG ecg;
  EcgDAO ecgDAO;
  boolean isReady;
  boolean start;
  public GraphicsData graphicsData;
   elder elder;  

  
  public Store2Sqlite(EcgDAO ecgDAO2, GraphicsData graphicsECGData, elder elder)
{
	// TODO Auto-generated constructor stub	  
	  ecgDAO=ecgDAO2;
	  graphicsData=graphicsECGData;
	  isReady=true;
	  start=false;
	  this.elder=elder;
	  ecg= new ECG(elder);
}

public void StartStroing()
{
	  storeThread.start();
}
	
	
Thread storeThread = new Thread()
{
	@Override
	public void run()
	{
		currentThread();
		while(!Thread.interrupted())
		{
			
	if(isReady&start)
	{
		isReady=false;
	   /*for (int i = 0; i < 14*2; i++)
	   {
		ecg= new ECG(1, "yanghua", null,null, graphicsData.data[485+i], 0);
	    ecgDAO.add(ecg);
	    }*/
		
		ecgDAO.bulkstore(graphicsData, ecg);
	    Log.i("STROING2SQLITE", "is storing,,");
	    isReady=true;
	    start=false;
	    
	}
	
	}
		}

	
	 
};

}
