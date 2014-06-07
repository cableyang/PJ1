package iflab.test;									//����

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;							//�������ļ�
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.View;


/*
 * Function  MyGraphics use canvas and paint to draw a line with 250 to 1000 points
 * @Author YangHua cabelyang@126.com 
 * @Parameter 
 */
public class MyGraphics extends View implements Runnable{	//�Զ���View
	final int plotwidth=300;  //������ʾ���򳤶�
	GraphicsData graphicsData;
	private Paint paint=null;

	private TimerTask stimerTask;
	private Timer timer= new Timer();
	public int R_pos1=0;
	public int R_pos2=0;
	public int R_pos3=0;
	public int R_pos=0;
	public int R=0;

	//QRS qrs;
	//�������ʶ���
	static int a=0;
	public MyGraphics(Context context, int rate, GraphicsData pointGraphicsData) {
		super(context);
		// TODO Auto-generated constructor stub
		graphicsData= pointGraphicsData;  //��ָ��ָ�򴫹���������
		paint=new Paint();							//��������
		new Thread(this).start();					//�����߳�
		
		
		//�˴�����QRS�����ʼ��㹫ʽ���˲�
		stimerTask= new TimerTask()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				findmax(graphicsData.data, 500);
				R_pos1=R_pos2;
				R_pos2=R_pos3;
				R_pos3=R_pos;
				R=(R_pos3+R_pos1-2*R_pos2+1000)/2;
				R=60*R/500;
			}
		};
		// every 1s do this
		timer.schedule(stimerTask, 100, 1000);		
	}
	
	/*
	 * function:  ����Сֵ
	 * @input data[] Ϊ�����ҵ����飻 sizeΪ�����С
	 * @output ���
	 */
	protected double findmin(double data[], int size)
	{
		double min=data[0];
		for (int i = 0; i < size; i++)
		{
			if(min>data[i])
			{
				min=data[i];	
			}
		}
		return min;
	}
	
	
	
	/*
	 * function: �����ֵ
	 * @Input data[]Ϊ���������飻 sizeΪ�����С
	 * @Output ���ֵ
	 */
	protected double findmax(double data[], int size)
	{
		double max=data[0];
		for (int i = 0; i < size; i++)
		{
			if (max<data[i])
			{
				max=data[i];
				R_pos=i;
			}
		}
		return max;
		
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		paint.setAntiAlias(true);	//���û���Ϊ�޾��
		paint.setColor(Color.RED);	//���û�����ɫ
		canvas.drawColor(Color.DKGRAY);				//��ɫ����
		paint.setStrokeWidth((float) 4.0);				//�߿�
		paint.setStyle(Style.STROKE);
		Path path = new Path();						//Path����

		/*
		 * LP-HP-descision tree����
            HEAD
		 */
		try
		{
		//	qrs=new QRS(graphicsData.data);
		  //  graphicsData.data=qrs.filter(); 
		} catch (Exception e)
		{
			// TODO: handle exception

		}

	
      //���ݶ���		
		int max=(int) findmax(graphicsData.data, GraphicsData.rate);
		int min=(int) findmin(graphicsData.data, GraphicsData.rate);
		
		//path.moveTo(0, (float) graphicsData.data[0]);						//��ʼ��
		for(int i=0; i<1000; i++)
		{
			float y = (float) (plotwidth-plotwidth*((graphicsData.data[i]-min)/(max-min)));
			path.lineTo(i, y);
		}
		    canvas.drawPath(path, paint);					//������������
		    
		    //��������ɺ��
		    paint.setAntiAlias(true);	//���û���Ϊ�޾��
			paint.setColor(Color.BLACK);	//���û�����ɫ 
			paint.setStrokeWidth((float) 1.0);				//�߿�
			paint.setStyle(Style.STROKE);
			Path gridpath = new Path();						//Path����
			
			//***********������������ ���ݶ���***********************
			for(int i=0; i<10; i++)
			{
				gridpath.moveTo(i*50, 0);	
				gridpath.lineTo(i*50, 500);
				canvas.drawText(""+i*50, i*50, 300, paint);
			}
			    canvas.drawPath(gridpath, paint);					//������������  
			    
			  //���ƺ���ɺ��
			    paint.setAntiAlias(true);	//���û���Ϊ�޾��
				paint.setColor(Color.BLACK);	//���û�����ɫ 
				paint.setStrokeWidth((float) 1.0);				//�߿�
				paint.setStyle(Style.STROKE);
				Path gridpath2 = new Path();						//Path����
				
			    //**************************************************
				//*************���������������ݶ���******************
				//**************************************************
				for(int i=0; i<10; i++)
				{
					gridpath2.moveTo(0,i*50 );	
					gridpath2.lineTo(500, i*50);
					canvas.drawText(""+(300-i*50), 0, i*50, paint);
				}
				    canvas.drawPath(gridpath2, paint);					//������������  
		    
				    
				    Paint TextPaint=new Paint();
					TextPaint.setTextSize(30);
					TextPaint.setColor(Color.RED);
				 
					canvas.drawText("���ʣ�"+R+"bpm", 30, 30, TextPaint);
	}

	@Override
	public void run() {								//����run����
		// TODO Auto-generated method stub
	//	while(!Thread.currentThread().isInterrupted())
		if(false)
		{
			 
			try
			{
				Thread.sleep(35);
			} catch (Exception e)
			{
				// TODO: handle exception
			}

			postInvalidate();						//���½���
		}
	}
	
	public int QRSalgorithm()
	{
		int qrs = 0;

		return qrs;
	}
}
