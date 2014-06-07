package iflab.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import iflab.model.*;
import android.util.Log;

public class DataDealing
{
    public int count=0;
    public double gy_stamp=0;
    public double ecg_stamp=0;
	final int GY=3,ECG=2,PPG=1;
	int qrs;
	int hr,spo2;
	List<Integer> indexlist = new LinkedList<Integer>();
	public  String []tarStrings=new String[204800];
	public  String []ConvertedStrings=new String[204800];
	public String[]srcStrings;
	String tempString=new String();
	Node ecg_pres,ppg_pres;
	GYNode gy_pres;
    Node tempNode;
	
	public DataDealing(Node ecg_head, Node ppg_head, GYNode gyNode,String []src)
	{
		// TODO Auto-generated constructor stub
		this.ecg_pres=ecg_head;
		this.ppg_pres=ppg_head;
		this.gy_pres=gyNode;
		this.srcStrings=src;
		hr=qrs=spo2=0;
	}

	
	  
/*
 * input:str[]�ַ������飬len�ַ������ȣ�tarѰ�ҵ��ַ�
 * Output��num ͳ�Ƴ��ֵĸ���	
 */
 public int count(String[] readMessage,int len,String tar)
{
   int num=0;  String tempString=new String();
	 for(int i=0; i<len; i++)	 
	 {
		 if(readMessage[i].equals(tar))
		 {
			 num++;
		 }
	 } 
	return num;
}
 
 
 /*
  *function name: Э�����ת��
  *input:src ��ת�������ݣ�num ���ݳ���
  *output: src ת���������
  */
  public  String[] convert(String []src, int len)
{
	
	for (int i = 0,j=0; i < len; i++,j++)
	{
		tempString=src[i];
		if(src[i].equals("7d"))
		{   
			i++;
			if(src[i].equals("5d"))
			{
				String.format("%s", "5d");
			}else
			{
			   String.format("%s", "7e");	
			}
		}
       ConvertedStrings[j]=tempString;
	}  
	  
  return ConvertedStrings;
}  
 
  
  /*
   * 
   */
  public void  DataSolving(String []str, int num, int len)
  { 
   int index=0;
   
   //����7eλ���б�
	for (int k = 0; k< len; k++)
	{
		if(str[k].equals("7e"))
		{
		  indexlist.add(k);
		}	
	}
	
	
	for(int i=0; i<indexlist.size()/3; i++)
	{
		index++;
	  int j=indexlist.get(3*i+1)-indexlist.get(3*i);
	 
	  //ÿ3��7E���д���
	  int j2=0;
	  if (j>10)
	  {
		 //�ӵ�һ�����ݿ�ʼ���룬���봦��
	     for (int j1 = indexlist.get(3*i); j1 <=indexlist.get(3*i+2); j1++,j2++)
		{
			tarStrings[j2]=str[j1];
		}
	  }
	  else
	  {
		//�ӵڶ������ݿ�ʼ����
		  for (int j1 = indexlist.get(3*i+1); j1 <= indexlist.get(3*i+2); j1++,j2++)
			{
				tarStrings[j2]=str[j1];	    
			}  
	  }
	  //���ݸ�ʽת�� 
	  convert(tarStrings, j2);
	  //ת��������ݽ������� ���� 
	  switch (getNodeType(ConvertedStrings))
	{
	case ECG:
		ECGHandler();
		break;
	case PPG:
		PPGHandler();
		break;
	case GY:
		GYHandler();
		break;
	}
	}
	//Log.i("msg", "index1 "+index+" ");
	index=indexlist.get((index-1)*3+2);

	
	for (int i = index; i < (len-index); i++)
	{
		srcStrings[i-index]=srcStrings[i];
	
	} 
	
	//	Log.i("msg", " srcStings[0] "+ srcStrings[0]+"  srcStrings[index]"+srcStrings[index]);
	
	count=len-index;
	
	//Log.i("msg", "readmess "+srcStrings[0]+" ");
	
	indexlist.clear();
}
  
  /*
   * fun name: getNodeType
   * input:src;
   */
   public int getNodeType(String []src)
   {
	  
    if(src[7].equals("e2"))
    	return ECG;
    if(src[7].equals("e1"))
    	return PPG;
    
    if(src[7].equals("e3")) 
        return GY;
    else {
		return 1000;
	}
   }
 

   /*
    * ECG handler
    */
   public void ECGHandler()
   {
	   int packetnum = Integer.valueOf(ConvertedStrings[8],16);
	   int x,y,z;
	   try
	{
		
		   for (int i = 0; i < packetnum/2	; i++)
			{
				   x=Integer.valueOf(ConvertedStrings[11+i*2],16)*256+ Integer.valueOf(ConvertedStrings[12+i*2],16);
				 
				//   y= Integer.valueOf(ConvertedStrings[13+i*22],16)+ Integer.valueOf(ConvertedStrings[14+i*22],16)*256;
				  
				//   z= Integer.valueOf(ConvertedStrings[15+i*22],16)+ Integer.valueOf(ConvertedStrings[16+i*22],16)*256;
				 
				   if((x&0x8000)>0)
				   {
					   x=((~x)+1)&0xffff;
					   x=0-x;
				   }
				   
				ecg_pres=ecg_pres.next;
				ecg_stamp=(ecg_stamp+1);
				ecg_pres.data=x;ecg_pres.stamp=ecg_stamp/200.0;
				Log.i("ecg_stamp", "datadealingis "+ecg_pres.stamp);		
			}
		   qrs=Integer.valueOf(ConvertedStrings[51],16);
		   Log.i("qrs", "  "+qrs);
	} catch (Exception e)
	{
		// TODO: handle exception
	}
	  
	   //Log.i("msg", "the packetnumis "+packetnum);
   }
 
   public GYNode getGyNode()
{
	return gy_pres;
}
   public Node getPPGNode()
{
	return ppg_pres;
}
   public Node getECGNode()
{
	return ecg_pres;
}

   /*
    * PPG handler
    */
   public void PPGHandler()
{
	   int packetnum = Integer.valueOf(ConvertedStrings[8],16);
	   int x,y,z;
	   try
	{
		
		   for (int i = 0; i < packetnum/2-1	; i++)
			{
				   x=Integer.valueOf(ConvertedStrings[11+i*2],16)*256+ Integer.valueOf(ConvertedStrings[12+i*2],16);
			
				   if((x&0x8000)>0)
				   {
					   x=((~x)+1)&0xffff;
					   x=0-x;
				   }
				ppg_pres=ppg_pres.next;
				ppg_pres.data=x;
				
			}
		   hr=qrs;spo2=99;
		  // qrs=Integer.valueOf(ConvertedStrings[51],16);
		   
	} catch (Exception e)
	{
		// TODO: handle exception
	}
	  
	   //Log.i("msg", "the packetnumis "+packetnum);
}

   /*
    * GY handler
    */
   public void GYHandler()
{
	   int packetnum = Integer.valueOf(ConvertedStrings[8],16);
	   int x,y,z;
	   try
	{
		
		   for (int i = 0; i < packetnum/22	; i++)
			{
				   x=Integer.valueOf(ConvertedStrings[11+i*22],16)+ Integer.valueOf(ConvertedStrings[12+i*22],16)*256;
				 
				   y= Integer.valueOf(ConvertedStrings[13+i*22],16)+ Integer.valueOf(ConvertedStrings[14+i*22],16)*256;
				  
				   z= Integer.valueOf(ConvertedStrings[15+i*22],16)+ Integer.valueOf(ConvertedStrings[16+i*22],16)*256;
				 
				   if((x&0x8000)>0)
				   {
					   x=((~x)+1)&0xffff;
					   x=0-x;
				   }
				   if((y&0x8000)>0)
				   {
					   y=((~y)+1)&0xffff;
					   y=0-y;
					   
				   }
				   if((z&0x8000)>0)
				   {
					   z=((~z)+1)&0xffff;
					   z=0-z;
				   }
				    
				   
				  gy_pres.x=x;
				  gy_pres.y=y;
				  gy_pres.z=z;
				  
				  gy_stamp++;
				  gy_pres.stamp=gy_stamp/50;
				  gy_pres=gy_pres.next;
			}
	} catch (Exception e)
	{
		// TODO: handle exception
	}
	  
	   //Log.i("msg", "the packetnumis "+packetnum);
}
   
   /*
    * 
    */
   public int getCount()
{
	return count;
}
   
  /*
   * 
   */
  public int amcount(Recv head,String var)
{
	Recv temp=head;
	int num=0;
	while(temp.next!=null)
	{
		if(temp.data.equals(var))
		{
			num++;
		}
		temp=temp.next;
	}
	
	return num;
}
  
  
  public Recv freenode(Recv head)
{
     Recv  temp=head;
     Recv temp2=head;
     while(temp.next!=null)
     {
    	 temp=null;
    	 temp2=temp2.next;
    	 temp=temp2;
     }
     return temp;
}
  
  /*
   * input: src Դ�ַ��� ; addr Դ�ַ��� ��des Ŀ���ַ���
   * output: src
   */
  public String[] pushdata(String []src,int addr, String []des)
 {
 	for(int i=0; i<addr; i++)
 	{
 		src[i+addr].replaceAll(null, des[i]);
 	}
 	return src;
 }
  
  
  public void  print(String []src, int len)
 {
 	 String str=new String();
 	 str="";
 	for (int i = 0; i < len; i++)
 	{ 
 		str=str+src[i]+"  ";
 	}
 	Log.i("msg", str);
 }
  
  
  
  public int count1(LinkedList<String>list,String var)
 {
 	int num=0;
 	String msg=new String("");
 	for(Iterator<String> iter = list.iterator(); iter.hasNext();)
 	{
 		msg=msg+iter.toString(); 
 	}
 	 Log.i("iterator", msg);
    msg="";
  
   return num;
 }
  
  
  public int getqrs()
{
	return qrs;
}
  public int  gethr()
{
	return hr;
}
  
  public int  getspo2()
{
	return spo2;
}
}
