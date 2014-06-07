package iflab.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
 

public class setting extends Activity
{
	public final int SETTING_BACK=20;
	
   Button backButton;
   TextView serverEditText;
   TextView rateEditText;
   CheckBox smsBox;
   CheckBox careBox;
   CheckBox call120Box;
   CheckBox callrelativeBox;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings);
	    
	    backButton=new Button(getBaseContext());
	    backButton= (Button)findViewById(R.id.setting_back1);
	    backButton.setOnClickListener(listener);
	    serverEditText= new TextView(getBaseContext());
	    serverEditText=(TextView)findViewById(R.id.setting_ip);
	    
	 //   rateEditText= new TextView(getBaseContext());
	   // rateEditText=(TextView)findViewById(R.id.setting_relatives);
	    
	   
	    smsBox=new CheckBox(getBaseContext());
	    smsBox=(CheckBox)findViewById(R.id.setting_sms1);
	    
	    careBox= new CheckBox(getBaseContext());
	    careBox = (CheckBox)findViewById(R.id.setting_center1);
	    
	    call120Box= new CheckBox(getBaseContext());
	    call120Box=(CheckBox)findViewById(R.id.setting_1201);
	    
	     callrelativeBox= new CheckBox(getBaseContext());
	    callrelativeBox= (CheckBox)findViewById(R.id.setting_relatives1);
	    
	}

	public OnClickListener listener= new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			Button btn= (Button)v;
			switch (btn.getId())
			{
			//·µ»ØÊý¾Ý
			case R.id.setting_back1:
				Intent intent=new Intent();
				Bundle extras= new Bundle();
				extras.putString("server", serverEditText.getText().toString());
			//	extras.putString("rate", rateEditText.getText().toString());
				extras.putBoolean("smsbox", smsBox.isChecked());
				extras.putBoolean("carebox", careBox.isChecked());
				extras.putBoolean("call120box", call120Box.isChecked());
				extras.putBoolean("callrelativeBox", callrelativeBox.isChecked()); 
				intent.putExtras(extras);
				setResult(SETTING_BACK, intent);
				finish();
				break;

			default:
				break;
			}
			
			
		}
	};
	
	
	
	
}
