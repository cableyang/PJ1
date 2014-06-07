package iflab.test;

import iflab.model.Info;
import iflab.model.elder;
import iflab.myinterface.ElderDAO;
import iflab.myinterface.HttpPatientSending;
import iflab.myinterface.InfoDao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
public class PatientID extends Activity
{
	/** Called when the activity is first created. */
	private ImageView imageView;
	private Handler handler;
	public String url="223.3.61.67";
	private Bitmap myBitmap;
	private byte[] mContent;
	Bitmap resizedBitmap;
 
	ImageButton caremabtn,gallerybtn;
	Button backButton;
	ImageButton checkButton,writedb;
	ImageButton remoteButton;
    TextView nameTextView;
    public String nameString;
    EditText idTextView;
    String id;
    EditText ageTextView;
    public int age;
    EditText phoneTextView;
    public String phoneString;
    EditText decrpitontTextView;
    public String des;
    EditText addressTextView;
    String address;
	ElderDAO elderDAO;
	InfoDao infoDao;
	elder elder;
	Info info;
	
 
	@ Override
	public void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patientid);
		
	    nameTextView=(EditText)findViewById(R.id.edit_name);
		idTextView=(EditText)findViewById(R.id.edit_id);
		ageTextView=(EditText)findViewById(R.id.edit_age);
		phoneTextView=(EditText)findViewById(R.id.edit_phone);
		decrpitontTextView=(EditText)findViewById(R.id.histroy_brief);
    	addressTextView=(EditText)findViewById(R.id.edit_addr);
		imageView = (ImageView) findViewById(R.id.info_imageview);
		 
		caremabtn=(ImageButton)findViewById(R.id.camera);
		gallerybtn=(ImageButton)findViewById(R.id.gallery);
		remoteButton=(ImageButton)findViewById(R.id.internet);
		
		elderDAO=new ElderDAO(getBaseContext());
		//elderDAO.creattable();
		//elderDAO.deletetable();
		infoDao = new InfoDao(getBaseContext());
		
		infoDao.creattable();
		
		//infoDao.deletetable();
 	
		/*
		 *
		 */
		handler=new Handler()
		{
            String nameString;
            int age;
            String phone;
            String description;
            String address;
            String id; 

			@Override
			public void handleMessage(Message msg)
			{
				// TODO Auto-generated method stub 
				 
				nameString=PatientID.this.nameString;
				phone=PatientID.this.phoneString;
				age=PatientID.this.age;
				description=PatientID.this.des;
				address=PatientID.this.address;
				id=PatientID.this.id;
				
				switch (msg.what)
				{
				case 1:
					//	elder=new elder(id, nameString, age,address,  phone, description, mContent);	
					//	HttpPatientSending httpPatientSending=new HttpPatientSending(elder);			 
					//    httpPatientSending.httpsending();
					break;
					
				case 2: 
					  // input some ready information	
					if (mContent==null)
					{
					//	Toast.makeText(getBaseContext(), "���ϴ���Ƭ������", Toast.LENGTH_LONG).show();
					}
					else {
				  try
					{	 
				    info=new Info(id, nameString, age,address,  phone, description, mContent);		 
				    infoDao.add(info); 
				   //  Toast.makeText(getBaseContext(), "����ע��ɹ�?, Toast.LENGTH_LONG).show();
					} catch (Exception e)
					{
						// TODO: handle exception
					
					}
					}
				   
					break;
					
				case 3:  
					try
					{
					     refresh();
					     info=  infoDao.findInfobyid(id);
					    if(elder==null)
					    {
				
					    }
					    else {   	
						 mContent=elder.getimg();
					     ageTextView.setText(String.valueOf(info.getage()));
					     Log.i("patientid","the elder age is.."+ info.getage());
					     nameTextView.setText(info.getname().toString());
					     idTextView.setText(String.valueOf(info.getid()));
					     phoneTextView.setText(info.getphone().toString());
					     addressTextView.setText(info.getaddress().toString());
					     decrpitontTextView.setText(info.getdescripiton().toString());
					     Log.i("patientid",info.getdescripiton().toString());        
					     Bitmap bmimage =BitmapFactory.decodeByteArray(mContent, 0, mContent.length);
				         
			             imageView.setImageBitmap(rotateBitmap(bmimage,0));
			            
						}
					   
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					break;
				default:
					break;
				}
			}
 	};
	
		handler=new Handler()
		{
            String nameString;
            int age;
            String phone;
            String description;
            String address;
            String id;

			@Override
			public void handleMessage(Message msg)
			{
				// TODO Auto-generated method stub 
				 
				refresh();
				nameString=PatientID.this.nameString;
				phone=PatientID.this.phoneString;
				age=PatientID.this.age;
				description=PatientID.this.des;
				address=PatientID.this.address;
				id=PatientID.this.id;
				
				switch (msg.what)
				{
				case 1:  //����httppost����Ϣ���ݸ����?hpҳ�����Զ���?���?
						elder=new elder(id, nameString, age,address,  phone, description, mContent);	
						HttpPatientSending httpPatientSending=new HttpPatientSending(elder);			 
					    httpPatientSending.httpsending();
					   // Toast.makeText(getBaseContext(), "Զ��ע��ɹ�?, Toast.LENGTH_LONG).show();
					break;
					
				case 2:
					if (mContent==null)
					{
						//Toast.makeText(getBaseContext(), "���ϴ���Ƭ������", Toast.LENGTH_LONG).show();
					}
					else {
				  try
					{	 
				    info=new Info(id, nameString, age,address,  phone, description, mContent);		 
				    infoDao.add(info); 
				    
				    Toast.makeText(getBaseContext(), "本地保存成功", Toast.LENGTH_LONG).show();
					} catch (Exception e)
					{
						// TODO: handle exception
						Toast.makeText(getBaseContext(), "本地存储失败", Toast.LENGTH_LONG).show();		
					}
					}
				   
					break;
					
				case 3:  //ͨ��������?����м���?
					try
					{
						
					 //elder=new elder(null, null, 0, null, null, null, null);
					 //   elder=elderDAO.findElderbyname(nameString);
					   refresh();
					   info=infoDao.findInfobyid(id);  	
						
					    if(info==null)
					    {
					    	Toast.makeText(getBaseContext(), "你输入的无此人", Toast.LENGTH_LONG).show();
					    }
					    else {
					    	
						 mContent=info.getimg();
					     ageTextView.setText(String.valueOf(info.getage()));
					     Log.i("patientid","the elder age is.."+ info.getage());
					     nameTextView.setText(info.getname().toString());
					     idTextView.setText(String.valueOf(info.getid()));
					     phoneTextView.setText(info.getphone().toString());
					     addressTextView.setText(info.getaddress().toString());
					     decrpitontTextView.setText(info.getdescripiton().toString());
					     Log.i("patientid",info.getdescripiton().toString());
				        
					     Bitmap bmimage =BitmapFactory.decodeByteArray(mContent, 0, mContent.length);
				         bmimage=scaleBitmap(bmimage);
			             imageView.setImageBitmap(rotateBitmap(bmimage, 0));
			             Toast.makeText(getBaseContext(), "欢迎！", Toast.LENGTH_LONG).show();
						}   
					} catch (Exception e)
					{
						// TODO: handle exception
						Toast.makeText(getBaseContext(), "出错了", Toast.LENGTH_LONG).show();
					}
					break;
			  
			case 4:
				try
				{
				myBitmap = getPicFromBytes(mContent, null);
				ByteArrayOutputStream bao1s = new ByteArrayOutputStream();
				
				myBitmap=rotateBitmap(myBitmap, 90);
				myBitmap=scaleBitmap(myBitmap);

				myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao1s);
			
				mContent = bao1s.toByteArray();
			    imageView.setImageBitmap(myBitmap);
				} catch (Exception e)
				{
					// TODO: handle exception
				}
				
				break;
		  
			case 5:
				try
				{

				myBitmap=scaleBitmap(myBitmap);
				ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
				myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
				mContent = baos2.toByteArray();
				imageView.setImageBitmap(myBitmap);
				} catch (Exception e)
				{
					// TODO: handle exception
				}
			
				break;
					
				default:
					break;
				}
			}
 	};
		

		caremabtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
					Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
				    startActivityForResult(getImageByCamera, 1);
	     	}
		});
		

		gallerybtn.setOnClickListener(new OnClickListener()
		{		
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
				getImage.addCategory(Intent.CATEGORY_OPENABLE);
				getImage.setType("image/jpeg");
				startActivityForResult(getImage, 0);			
			}
		});
		
		/*
		 *
		 */
		checkButton =(ImageButton)findViewById(R.id.search);
		checkButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				//elder=new elder(id, name, age, address, phone, des, im)
				try
				{
					nameString=nameTextView.getText().toString();
				if (nameString=="")
				{
					//Toast.makeText(getApplicationContext(), "Toast.LENGTH_LONG).show();
				}
				else {
				Message msg= new Message();
				msg.what=3;
				handler.sendMessage(msg);
				}
				} catch (Exception e)
				{
					// TODO: handle exception
				}
				
			}
		});
 	  /*
 	   * 
 	   * @para input name,age,tele, description img
 	   *    id====name=====age====address======phone=====decription======img=======>>>>>> sqltite
 	   */
		writedb=(ImageButton)findViewById(R.id.local);
		writedb.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{	 
				refresh();
				// TODO Auto-generated method stub
				Message msg= new Message();
				msg.what=2;
				handler.sendMessage(msg);
				 	
			}
		});
 
	/*
	 *
	 */
		remoteButton.setOnClickListener(new OnClickListener()
		{
        
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
		    refresh();
			Message msgMessage=new Message();
			msgMessage.what=1;
			handler.sendMessage(msgMessage);		
			}
		});
		
		/*
		 * =================================================================================
		 * ================================================================================
		 * =================================================================================
		 */
		backButton=(Button)findViewById(R.id.histroy_back);
		backButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
			 try
			{
				Intent intent=new Intent();
				intent.setClass( PatientID.this, firstActivity.class );
				Bundle b = new Bundle();
				 
			 
				Toast.makeText(getBaseContext(), "正在返回", Toast.LENGTH_LONG).show();
				//}
				startActivityForResult(intent, 5);
				 
			} catch (Exception e)
			{
				// TODO: handle exception
			}
				
			}
		});

	}

	
	/*
	 *
	 */
	public void refresh()
	{
		try
		{
		    id=(idTextView.getText().toString());
			nameString=nameTextView.getText().toString();
			age=Integer.parseInt(ageTextView.getText().toString());
			phoneString=phoneTextView.getText().toString();
			address=addressTextView.getText().toString();
			des=decrpitontTextView.getText().toString();

		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	
	
	@ Override
	protected void onActivityResult ( int requestCode , int resultCode , Intent data )
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
    try
	{
    	ContentResolver resolver = getContentResolver();
    	if (requestCode == 0)
		{
			try
			{

				Uri originalUri = data.getData();

				mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));

               Message msg=new Message();
               msg.what=4;
               handler.dispatchMessage(msg);

				
			} catch ( Exception e )
			{
				System.out.println(e.getMessage());
			}

		} else if (requestCode == 1)
		{

			try
			{
				super.onActivityResult(requestCode, resultCode, data);
				Bundle extras = data.getExtras();
				myBitmap = (Bitmap) extras.get("data");
				Message msg=new Message();
	            msg.what=5;
	            handler.dispatchMessage(msg);
 
			} catch ( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		      
		
		}
    	
	} catch (Exception e)
	{
		// TODO: handle exception
	}
		 
		
	}

	
	public static Bitmap getPicFromBytes ( byte[] bytes , BitmapFactory.Options opts )
	{
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	public static byte[] readStream ( InputStream inStream ) throws Exception
	{
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1)
		{
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}
  
	
 

 
	private Bitmap rotateBitmap(Bitmap bm,float angle)
 
	{
		int width = bm.getWidth();  
		int height = bm.getHeight();  
		Matrix matrix = new Matrix();   
 
		 
		matrix.postRotate(angle); 

	   Bitmap   resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	   return resizedBitmap;
		
	}
	
	
	private  Bitmap scaleBitmap(Bitmap bm)
	{
		int width = bm.getWidth();  
		int height = bm.getHeight();                  

		int newWidth = 160;        
		int newHight = 220;      

		float scaleWidth = (float)newWidth/width;     
		float scaleHeight = (float)newHight/height;  

		Matrix matrix = new Matrix();   

		
		matrix.postScale(scaleWidth, scaleHeight);  
		Bitmap   resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return resizedBitmap;
		
	}
	{
		
	}
	
}