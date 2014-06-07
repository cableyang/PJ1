package iflab.myinterface;

 
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import iflab.test.GraphicsData;
 
import android.R.bool;
import android.R.integer;
import android.R.string;
import android.database.sqlite.SQLiteDatabase;
import iflab.test.firstActivity;
import iflab.model.ECG;
import iflab.model.Info;
import iflab.model.elder;
import iflab.test.GraphicsData;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import iflab.model.*;

	
public class DataBase
{
	  String tableNameString;
	  String id;
	  DBOpenHelper helper;
	  SQLiteDatabase db;
	  GYNode gy_pre;
	  Node ecg_pre,ppg_pre;
	  String ecgString,ppgString,accxString,accyString,acczString;
   public DataBase(Context context,String id)
   {
		helper= new DBOpenHelper(context);  
		this.id=id;
   }
   
   public void Store2DATA(GYNode gy_preNode, Node ecg_pre, Node ppg_pre)
   {
	    db=helper.getWritableDatabase();
	    GYNode gytempGyNode=gy_preNode;
	    Node ecgtemp=ecg_pre,ppgtemp=ppg_pre;
	    
	    String GYx="";  String GYy="";  String GYz="";
	    do
		{
			GYx=GYx+gytempGyNode.x+";";
			GYy=GYy+gytempGyNode.y+";";
			GYz=GYz+gytempGyNode.z+";";
			gytempGyNode=gytempGyNode.next;
		} while (gytempGyNode!=gy_preNode);
	    
		db.execSQL("insert into DATA (id,ecg,ppg,accx,accy,accz) values (?,?,?,?,?,?)", new Object[]
		{ 12, "test","test",GYx,GYy,GYz});
   }
   
   
   public void StoreGY(int stamp, String GYx, String GYy, String GYz)
{
	    db=helper.getWritableDatabase();
	    try
		{
			db.execSQL("insert into GY"+tableNameString+" (accx,accy,accz) values (?,?,?)", new Object[]
		{  GYx,GYy,GYz});
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		
}
   public void StorePPG(int stamp, String ppg)
   {
   	    db=helper.getWritableDatabase();
   	    try
   		{
   			db.execSQL("insert into PPG"+tableNameString+" (ppg) values (?)", new Object[]
   		{  ppg});
   		} catch (Exception e)
   		{
   			// TODO: handle exception
   		}
   }
   
   
   public void StoreECG(int stamp, String ecg)
   {
   	    db=helper.getWritableDatabase();
   	    try
   		{
   			db.execSQL("insert into ECG"+tableNameString+" (ecg) values (?)", new Object[]
   		{ ecg});
   		} catch (Exception e)
   		{
   			// TODO: handle exception
   		}
   }
   
   public void update(ECG ecg)
	{
		db=helper.getWritableDatabase();
		db.execSQL("update ECG_DATA set name = ?,date = ?,time = ? , ecg = ?,bpm= ? where  id = ?", new Object[]
      {ecg.getname(),ecg.getdate(), ecg.getTime(),ecg.getecg(),ecg.getbpm(),ecg.getid()});
	}
   
       /*
	   * 创建表
	   */
	  public void CreateTable(boolean flag_gy, boolean flag_ecg,boolean flag_ppg)
	  {
		  db=helper.getWritableDatabase();
		  SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyyMMddHH");       
		  String    date    =    sDateFormat.format(new java.util.Date());    
		  tableNameString="_"+id+"_"+date;
		  if (flag_gy)
		{
			  try
				{
				   db.execSQL("create table  if not exists GY"+tableNameString+"(id INTEGER PRIMARY KEY AUTOINCREMENT ,accx string, accy string, accz string)");
				} catch (Exception e)
				{
					// TODO: handle exception
				} 
		}
		 
		  if (flag_ecg)
		{
			  try
				{
				   db.execSQL("create table if not exists ECG"+tableNameString+"(id INTEGER PRIMARY KEY AUTOINCREMENT ,ecg string)");
				} catch (Exception e)
				{
					// TODO: handle exception
				} 
		}
		 
		 if (flag_ppg)
		{
			 try
				{
				   db.execSQL("create table if not exists PPG"+tableNameString+"(id INTEGER PRIMARY KEY AUTOINCREMENT ,ppg string)");
				} catch (Exception e)
				{
					// TODO: handle exception
				} 
		}
		   
	  }
	  
	  /*
	   * 创建表
	   */
	  public void CreateDATALIST()
	  {
		  db=helper.getWritableDatabase();
		 
		 try
		{
		   db.execSQL("create table if not exists datalist(id INTEGER PRIMARY KEY AUTOINCREMENT ,info_id string, list string)");
		} catch (Exception e)
		{
			// TODO: handle exception
		}     
	  }

	  /*
	   * 创建表
	   */
	  public boolean CheckDataList(String info_id, String list)
	  {
		  db=helper.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from datalist  where info_id = ? and list = ?",new String[]{""+info_id, list}); 
					
			if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.  
				         {  
				            String info_id1=cursor.getString(cursor.getColumnIndex("info_id"));  
			                String list1=cursor.getString(cursor.getColumnIndex("list"));  			            
				            return true;  
			         }  
				         return false;  

	  }
	  
	  
	  public void Insert2List(String info_id, String list)
	{
		  db=helper.getWritableDatabase();
		  try
		{
			db.execSQL("insert into  datalist (info_id, list) values (?,?)", new Object[]
			   		{ info_id, list});
		} catch (Exception e)
		{
			// TODO: handle exception
		}
			
	}
	  /*
	   * 删除表
	   */
	  public void deletetable()
	{
		db=helper.getWritableDatabase();
		try
		{
			db.execSQL("DROP TABLE datalist");
		//	db.execSQL("DROP TABLE DATA");
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	  
	  public String getGYtablename()
	{
		return "GY"+tableNameString;
	}
	  
	  public String getECGtablename()
		{
			return "ECG"+tableNameString;
		}
	  
	  public String getPPGtablename()
		{
			return "PPG"+tableNameString;
		}
	 
	  public ArrayList<String> FindDatalistByID(String info_id)
	{
		  ArrayList<String>data = new ArrayList<String>();
		  boolean flag=false;
		   if (data == null)

	           return null;  
			db = helper.getWritableDatabase();
		   Cursor cursor = db.rawQuery("select * from datalist where info_id = ?", new String[]{""+info_id});
			
			while (cursor.moveToNext())
			{
				flag=true;
				data.add(cursor.getString(cursor.getColumnIndex("list")));
				//return new Info(cursor.getInt(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor.getColumnIndex("age")), cursor.getString(cursor.getColumnIndex("address")),cursor.getString(cursor.getColumnIndex("phone")),cursor.getString(cursor.getColumnIndex("decription")),cursor.getBlob(cursor.getColumnIndex("img")));
			}
			 
			if(flag)
				return data;
			else {
				return null;
			}
	}
	  
	  
	  public ArrayList<String> GetGYdata(String tablename)
	{
		  ArrayList<String>data = new ArrayList<String>();
		  String xString="";
		  String yString="";
		  String zString="";
		  boolean flag=false;
		   if (data == null)
	           return null;  
		   
			db = helper.getWritableDatabase();
		   Cursor cursor = db.rawQuery("select * from "+tablename+"",null);
			
			while (cursor.moveToNext())
			{
				flag=true;
				//data.add(cursor.getString(cursor.getColumnIndex("list")));
				xString=xString+cursor.getString(cursor.getColumnIndex("accx"));
				yString=yString+cursor.getString(cursor.getColumnIndex("accy"));
				zString=zString+cursor.getString(cursor.getColumnIndex("accz"));
				//return new Info(cursor.getInt(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor.getColumnIndex("age")), cursor.getString(cursor.getColumnIndex("address")),cursor.getString(cursor.getColumnIndex("phone")),cursor.getString(cursor.getColumnIndex("decription")),cursor.getBlob(cursor.getColumnIndex("img")));
			}
			
			if (flag)
			{
				data.add(xString);data.add(yString);data.add(zString);
				return data;
			}
			
		return null;
	}
	  
	  public ArrayList<String> GetPPGdata(String tablename)
		{
			  ArrayList<String>data = new ArrayList<String>();
			 
			  String ppgString="";
			  boolean flag=false;
			   if (data == null)
		           return null;  
			   
				db = helper.getWritableDatabase();
			   Cursor cursor = db.rawQuery("select * from "+tablename+"",null);
				
				while (cursor.moveToNext())
				{
					flag=true;
					//data.add(cursor.getString(cursor.getColumnIndex("list")));
					ppgString=ppgString+cursor.getString(cursor.getColumnIndex("ppg"));
					 
					//return new Info(cursor.getInt(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor.getColumnIndex("age")), cursor.getString(cursor.getColumnIndex("address")),cursor.getString(cursor.getColumnIndex("phone")),cursor.getString(cursor.getColumnIndex("decription")),cursor.getBlob(cursor.getColumnIndex("img")));
				}
				
				if (flag)
				{
					data.add(ppgString);
					return data;
				}
				
			return null;
		}
	  
	  public ArrayList<String> GetECGdata(String tablename)
		{
			  ArrayList<String>data = new ArrayList<String>();
			 
			  String ecgString="";
			  boolean flag=false;
			   if (data == null)
		           return null;  
			   
				db = helper.getWritableDatabase();
			   Cursor cursor = db.rawQuery("select * from "+tablename+"",null);
				
				while (cursor.moveToNext())
				{
					flag=true;
					//data.add(cursor.getString(cursor.getColumnIndex("list")));
					ecgString=ecgString+cursor.getString(cursor.getColumnIndex("ecg"));
					 
					//return new Info(cursor.getInt(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor.getColumnIndex("age")), cursor.getString(cursor.getColumnIndex("address")),cursor.getString(cursor.getColumnIndex("phone")),cursor.getString(cursor.getColumnIndex("decription")),cursor.getBlob(cursor.getColumnIndex("img")));
				}
				
				if (flag)
				{
					data.add(ecgString);
					return data;
				}
				
			return null;
		}
}
