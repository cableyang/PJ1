package iflab.test;									//包名

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;							//导入类文件
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
public class MyGraphics extends View implements Runnable{	//自定义View
	final int plotwidth=300;  //定义显示区域长度
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
	//声明画笔对象
	static int a=0;
	public MyGraphics(Context context, int rate, GraphicsData pointGraphicsData) {
		super(context);
		// TODO Auto-generated constructor stub
		graphicsData= pointGraphicsData;  //由指针指向传过来的数据
		paint=new Paint();							//构建对象
		new Thread(this).start();					//开启线程
		
		
		//此处进行QRS波心率计算公式及滤波
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
	 * function:  找最小值
	 * @input data[] 为被查找的数组； size为数组大小
	 * @output 输出
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
	 * function: 找最大值
	 * @Input data[]为被查找数组； size为数组大小
	 * @Output 最大值
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
		
		paint.setAntiAlias(true);	//设置画笔为无锯齿
		paint.setColor(Color.RED);	//设置画笔颜色
		canvas.drawColor(Color.DKGRAY);				//白色背景
		paint.setStrokeWidth((float) 4.0);				//线宽
		paint.setStyle(Style.STROKE);
		Path path = new Path();						//Path对象

		/*
		 * LP-HP-descision tree方法
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

	
      //数据读入		
		int max=(int) findmax(graphicsData.data, GraphicsData.rate);
		int min=(int) findmin(graphicsData.data, GraphicsData.rate);
		
		//path.moveTo(0, (float) graphicsData.data[0]);						//起始点
		for(int i=0; i<1000; i++)
		{
			float y = (float) (plotwidth-plotwidth*((graphicsData.data[i]-min)/(max-min)));
			path.lineTo(i, y);
		}
		    canvas.drawPath(path, paint);					//绘制任意多边形
		    
		    //绘制纵向珊格
		    paint.setAntiAlias(true);	//设置画笔为无锯齿
			paint.setColor(Color.BLACK);	//设置画笔颜色 
			paint.setStrokeWidth((float) 1.0);				//线宽
			paint.setStyle(Style.STROKE);
			Path gridpath = new Path();						//Path对象
			
			//***********绘制纵向坐标 数据读入***********************
			for(int i=0; i<10; i++)
			{
				gridpath.moveTo(i*50, 0);	
				gridpath.lineTo(i*50, 500);
				canvas.drawText(""+i*50, i*50, 300, paint);
			}
			    canvas.drawPath(gridpath, paint);					//绘制任意多边形  
			    
			  //绘制横向珊格
			    paint.setAntiAlias(true);	//设置画笔为无锯齿
				paint.setColor(Color.BLACK);	//设置画笔颜色 
				paint.setStrokeWidth((float) 1.0);				//线宽
				paint.setStyle(Style.STROKE);
				Path gridpath2 = new Path();						//Path对象
				
			    //**************************************************
				//*************绘制纵向坐标数据读入******************
				//**************************************************
				for(int i=0; i<10; i++)
				{
					gridpath2.moveTo(0,i*50 );	
					gridpath2.lineTo(500, i*50);
					canvas.drawText(""+(300-i*50), 0, i*50, paint);
				}
				    canvas.drawPath(gridpath2, paint);					//绘制任意多边形  
		    
				    
				    Paint TextPaint=new Paint();
					TextPaint.setTextSize(30);
					TextPaint.setColor(Color.RED);
				 
					canvas.drawText("心率："+R+"bpm", 30, 30, TextPaint);
	}

	@Override
	public void run() {								//重载run方法
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

			postInvalidate();						//更新界面
		}
	}
	
	public int QRSalgorithm()
	{
		int qrs = 0;

		return qrs;
	}
}
