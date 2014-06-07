package iflab.myinterface;

 
import iflab.model.Info;
import iflab.model.elder;
 
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class InfoDao 
{
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private String tablename="INFO";
	
	public InfoDao(Context context)
	{
		helper= new DBOpenHelper(context);
	}
	
	/*
	 * 创建表
	 */
	public void creattable()
	{
		db=helper.getWritableDatabase();
		try
		{
			db.execSQL("create table  if not exists "+tablename+"(id INTEGER PRIMARY KEY AUTOINCREMENT,info_id varchar(32),name varchar(20),age integer, address varchar(32), phone varchar(15), decription text, img blob)");
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	
	public void deletetable()
	{
		db=helper.getWritableDatabase();
		try
		{
			db.execSQL("DROP table "+tablename);
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	
	public void add(Info info)
	{
		db=helper.getWritableDatabase();
		//db.execSQL("set NAMES gb2312");
		db.execSQL("insert into "+tablename+" (info_id,name,age,address,phone,decription,img) values (?,?,?,?,?,?,?)", new Object[]
		{ info.getid(), info.getname(), info.getage(), info.getaddress(), info.getphone(),info.getdescripiton(),info.getimg()});
	}
	
	public void update(Info info)
	{
		db=helper.getWritableDatabase();
		db.execSQL("update "+tablename+"  set name = ?,age = ?,address = ? , phone = ?,decription=?,img=? where id = ?", new Object[]
         {info.getname(), info.getage(), info.getaddress(), info.getphone(),info.getdescripiton(),info.getimg(),info.getid()});
	}
	
	
	/*
	 * 通过id进行查询
	 */
	public Info findInfobyid(String id)
	{
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select info_id,name,age,address,phone,decription,img from "+tablename+"  where info_id = ?", new String[]
		{ String.valueOf(id)});
		
		if (cursor.moveToNext())
		{
			return new Info(cursor.getString(cursor.getColumnIndex("info_id")),cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor.getColumnIndex("age")), cursor.getString(cursor.getColumnIndex("address")),cursor.getString(cursor.getColumnIndex("phone")),cursor.getString(cursor.getColumnIndex("decription")),cursor.getBlob(cursor.getColumnIndex("img")));
		}
		return null;
	}
	
	
	public elder findbyname(String name)
	{
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select id,name,age,address,phone,decription,img from "+tablename+"  where name = ? ", new String[]
		{ name});
		
		if (cursor.moveToNext())
		{                                                                                                                                                                                                                           //select id,name,age,address,phone,decription,img
			return new elder(cursor.getString(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor.getColumnIndex("age")), cursor.getString(cursor.getColumnIndex("address")),cursor.getString(cursor.getColumnIndex("phone")),cursor.getString(cursor.getColumnIndex("decription")),cursor.getBlob(cursor.getColumnIndex("img")));
		}
		return null;
	}
	
	/*
	 * 删除id号的
	 */
	public void delete(int id)
	{
		db= helper.getWritableDatabase();
		db.execSQL("delete from "+tablename+"  where id="+String.valueOf(id));
	}
	
	
	
}
