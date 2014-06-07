package iflab.test;

import android.util.Log;

    
/*
 * Class GraphicsData is the datahub and 
 * also hub for plot parameter for MyGraphics
 * version 1, 2013-3-25
 */
public class GraphicsData
{
	/*
	 * 定义采样频率，默认为2即500HZ的采样频率
	 */
	public static final int sendnum=5000;
    public static final int RATE250 = 250;
	public static final int RATE500 = 500;
	public static final int RATE1000 = 1000;
	public static int  rate=500;
	public double []data=new double[1000];//定义1000个点进行显示
	public double []datasend=new double[sendnum];  //定义2s数据
    public int num=0;
    public boolean full=false;
    public int datatype;
    
    int remoterate=500-1;
    //===============================
    //====为计算心率做准备=========
    //================================
    public double []window=new double[2000];  //移动窗口，用于进行采样创库设计
    public double []det=new double[2000];   //隔点求差值
    public int []tagMax = new int[200];  //记录曲线变化较大点
    public int []RX = new int[200];// RX为R波的x轴参数
    public double []RY = new double[200];//RY为R波的y轴参数
    double K=0.3;//K为配置参数	
    double K1;//阈值上限
    double K2;//阈值下限
    public double fz=500;  // 参数fz为采样频率500HZ
    public double fmin=20;//tmin为最小阈值  带通 20~200
    public double fmax=200;
    public double tmin=fmin/60;
    public double tmax=fmax/60;
    public int tag_max;
    public int tag_min;
    public boolean thread_qrs=false;
    
    public GraphicsData(int myrate, int type)
    {
    	rate=myrate; //设定速率为myrate
    	datatype=type;
    	 
    }
    
    
  /*
   * 将P指针指向数据显示区域
   */
 public void putdata(int p[])
    {
    	    
      for (int i = 0; i < p.length; i++)
	  {
		 data[i] = p[i];  //装入数据
	  }
      
      for (int i = 0; i < p.length; i++)
	  {
		 datasend[i] = p[i];  //装入数据
	  }
	  	
    }
    
 
 
 /*
  * 添加数据
  */
 public void adddata(double temp)
 {
	 num++;
	for(int i=0; i < RATE500-1; i++ )
	{
		data[i]=data[i+1];	
	}
	 data[RATE500-1]=temp;
	
	 
	if(num==remoterate)
	 {
		 num=0;
		 full=true;
	//	Log.i("QRS COMPUTING", "is plusing");
			 if (datatype==1)
			{
				// QrsAlgorithm(); 
				// thread_qrs=true;
				 Log.i("QRS COMPUTING", "is on going");
			}	
	 }
 }
 
 /*
  * QRS波群检测算法实现
  */
 private int QrsAlgorithm()
 {
	 
	 int qrs =0;
	//装载入窗处理
	for (int b = 0; b < 1500; b++)
	{
		window[b]=window[500+b];
	}
	
	for (int b = 0; b < 500; b++)
	{
		window[1500+b]=data[b];
	}
	//平滑滤波处理

	
	//阈值计算
	for (int b = 2; b < window.length-1; b++)
	{
		det[b-1]=window[b+1]-window[b-1];
	}
	
	K1=K*max(det);
	K2=K*min(det);
	
	int i=1;
	int d=1;
     do
	{
		if ((window[i+1]-window[i])>K1)
		{
			tagMax[d]=i;
			d++;
			i=i+10;
		}
		/*
		if ((window[i+1]-window[i])<K2)
		{
			tagMax[d]=i;
			d++;
			i=i+10;
		}   */
    	 i++; 	 
	} while (i<det.length-1);
    
     RX[1]=tagMax[1];
     RY[1]=window[RX[1]];
     int j=1;
	//RR滤波
     for (int c = 2; c < d-1; c++)
	{
		if((fz*tmin<(tagMax[c]-RX[j]))&&((tagMax[c]-RX[j])<fz*tmax))
		{
			RX[j+1]=tagMax[c];
			RY[j]=window[RX[j+1]];	
			j++;
		}
	}
     
     double []temp=new double[200];
	
	//最邻近值计算法
	int le=20;
	for (int c = 1; c <j ; c++)
	{
		//RY[c]=max(window((RX[i]-le):(RX[i]+le));
		for (int k = 0; k < le*2; k++)
		{
			try
			{
				temp[k]=window[k+RX[i]-le];
			} catch (Exception e)
			{
				// TODO: handle exception
			}
			
		}
		RY[c]=max(temp);
		RX[c]=tag_max+RX[i]-le;
	}
	
	Log.i("QRS COMPUTING", "("+RX[1]+","+RY[1]+")"+"("+RX[2]+","+RY[2]+")"+"("+RX[3]+","+RY[3]+")");
	
	return qrs;
	 
 }
 
 /*
  * 寻找最值
  */
 private double max(double []d)
{
	double max=0.0;
	for (int i = 0; i < d.length; i++)
	{
		if(max<d[i])
		{
	       max=d[i];
		   tag_max=i;		
		}
			
	}
	
	return max;
}
 
 
 /*
  * 寻找最小值
  */
 private double min(double []d)
{
	double min=0.0;
	for (int i = 0; i < d.length; i++)
	{
		if (min>d[i])
		{
			min=d[i];
			tag_min=i;
		}
	}

	return min;
}
 
 
 /*
  * 数据处理
  */
 public void dealwithstring(String string)
{
	 final int size=300;
	 int index1=0;
	 int index2=0;
	 int pack=string.length()/((14*6+4*2));
	 //定义数据包中有14个点
     final int ref_vol=2;  //参考电压
	 final int value=0x7fffff;
     int packsize=14;
     //Log.i("", msg)
     Log.i("length", "string.length="+string.length()+"pack="+pack);
     
	//通过逐一法 求出第一个020a的标记  首先进行020a定位正寻  
     
		 for (int i = 0; i < string.length()-6; i++)
			{
				if(string.charAt(i)=='0')
				{
					if (string.charAt(i+1)=='2')
					{
						if (string.charAt(i+2)=='0')
						{
							if (string.charAt(i+3)=='a')
							{
								index1=i;
								Log.i("INDEX", string.substring(i, i+4)+" index1 "+index1+"  pack="+pack);break;
							}
						}
					}
					
				}
			}
		 
		//逆序 查找020a是否最后一次出现 
		 try
		{
			 for (int i = 1; i < string.length()-14*6-4*2-1; i++)
			{
				if(string.charAt(string.length()-i)=='a')
				{
					if (string.charAt(string.length()-i-1)=='0')
					{
						if (string.charAt(string.length()-i-2)=='2')
						{
							if (string.charAt(string.length()-i-3)=='0')
							{
								index2=i;
								Log.i("INDEX", string.substring(i, i+4)+" index2 "+index2);break;
							}
						}
					}
					
				}
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		
		 //对020a之间的数据进行处理 xx xx xx 030a 020a
		 if (index1>13)
		{ 
			 int before=(index1-4)/6;
			for (int i = 0; i < before; i++)
			{
				int temp = Integer.parseInt(string.substring(index1-4-6*before+i*6, index1-4-6*before+5+1+i*6),16);
				if(temp>0x7fffff)//表明为负数
				 {
					 temp=(~temp+1)&0x7fffff;
				 }
				 int dataadd=size-size*temp/value;
				 adddata(temp);
				
				 if ((dataadd)<0)
					{
						Log.i("tag", "first size-size*temp/value= "+(dataadd));
					}
				
			}
			 Log.i("dealwith data", "deal with 020a之前数据");
		}
		 
		 //计算020a~030a整数个数据包的信息
		 if ((pack>0)&(pack<5))
		{
			 Log.i("dealwith data", "deal  pack之前数据pack="+pack);
			 for(int j=0; j< pack; j++)
		 {
			 for (int i = 0; i < packsize; i++)
				{
				 try
				{
				 int temp = Integer.parseInt(string.substring(index1+4+i*6+j*(14*6+4*2), index1+9+i*6+1+j*(14*6+4*2)),16);
				 if(temp>0x7fffff)//表明为负数
				 {
					 temp=(~temp+1)&0x7fffff;
				 }
			 
				 int dataadd=size-size*temp/value;
				 adddata(temp);
				 if ((dataadd)<0)
					{
						Log.i("tag", "second size-size*temp/value= "+(dataadd));
					}
				} catch (Exception e)
				{
					// TODO: handle exception
				}
			} 	 
		 }
		}
		
		 
		 int lastindex=index1+pack*(14*6+4*2);
		 int left = string.length()-lastindex-4;
		 
		 try
		{
			 if (left>10)
			{
				 Log.i("dealwith data", "deal with 最后数据left="+left);
				 if ((index2-1)>6 )
				{ 
				 for (int i = 0; i < (index2-1)/6; i++)
				{
			     int temp = Integer.parseInt(string.substring(string.length()-index2+i*6+1, string.length()+2-index2+5+i*6),16);
			     Log.i("log_tag", "left"+left+" string is"+ string.substring(string.length()-index2+i*6+1, 2+string.length()-index2+5+i*6));	
			     if(temp>0x7fffff)//表明为负数
				 {
					 temp=(~temp+1)&0x7fffff;
				 } 
				 adddata(temp);
				 
				}
				 
			//	 Log.i("log_tag", "left"+left+" string is"+ string.substring(string.length()-index2, string.length()));	
				
			}
			 
				 
				} 
			
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	
		 
		 
	 }
	
 /*
  * 其它
  */
 
 
 
 
}
