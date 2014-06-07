package iflab.test;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
 
import java.util.Timer;
import java.util.TimerTask;

public class Welcome extends Activity
{
private final int TIME_UP = 1;
public int mode=1;
Timer newTimer;
TimerTask welcomeTask;

EditText w_b, w_s, w_n, w_string ;
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			
			switch (msg.what)
			{
			case 2:
				w_b.setText("B");
				break;
             
			case 3:
				w_s.setText("S");
				break;
				
			case 4:
				w_n.setText("N");
				break;
				
			case 5:
				w_string.setText("身体传感器网络");
				break;
				
				default:
				   newTimer.cancel();
				   Intent intent = new Intent();
				intent.setClass(Welcome.this, PatientID.class);
				startActivity(intent);
		     //	overridePendingTransition(R.anim.splash_screen_fade, R.anim.splash_screen_hold);
				Welcome.this.finish();
					break;
			}
			}
	 
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		w_b=new EditText(getBaseContext());
		w_n=new EditText(getBaseContext());
		w_s=new EditText(getBaseContext());
		
		w_b=(EditText)findViewById(R.id.w_b);
		w_s=(EditText)findViewById(R.id.w_s);
		w_n=(EditText)findViewById(R.id.w_n);
		w_string=(EditText)findViewById(R.id.w_string);

		newTimer = new Timer();
		
		welcomeTask= new TimerTask()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				mode++;
				Message msg = new Message();
				msg.what = mode;
				handler.sendMessage(msg);		
			}
		};
		newTimer.schedule(welcomeTask, 1000, 1500);
	}
}
