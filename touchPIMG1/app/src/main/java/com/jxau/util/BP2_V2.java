
package com.jxau.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName: ${BP2}
 * @Description: ${处理数据}
 * @author tugeng
 * @date ${--} ${--}
 * @version 1.0
 * ${tags}
 */
public class BP2_V2 {

	double dw1[][] = new double[8][16];
	double dw2[][] = new double[5][9];
	String configpath = "/data/data/com.jxau.touchpimg/databases/parameters.properties"; //.ini文件路径
	int hidN;//隐含层单元个数  5
	int attN;//输入单元个数 22
	int outN;//输出单元个数 1
	int times;//迭代次数
	double rate;//学习速率
	boolean trained=true;//保证在得结果前，先训练
	BP2_V2(int attN,int outN,int hidN,int times,double rate)
	{
		this.attN=attN;
		this.outN=outN;
		this.hidN=hidN;
			/*dw1=new double[hidN][attN+1];//每行最后一个是阈值w0
			for(int i=0;i<hidN;++i)//每行代表所有输入到i隐藏单元的权值
			{
				for(int j=0;j<=attN;++j)
					dw1[i][j]=Math.random()/2;
			}
			dw2=new double[outN][hidN+1];//输出层权值,每行最后一个是阈值w0
			for(int i=0;i<outN;++i)//每行代表所有隐藏单元到i输出单元的权值
			{
				for(int j=0;j<=hidN;++j)
					dw2[i][j]=Math.random()/2;
			}*/
		this.times=times;
		this.rate=rate;
	}

	public double[] getResault(double samin[]){
		Properties pp = new Properties();
		try {
			FileInputStream fis = new FileInputStream(configpath);
			pp.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}

		for(int i=0;i<8;i++){
			for(int j=0;j<16;j++){
				dw1[i][j] = Double.parseDouble((String) pp.get("dw1["+i+"]["+j+"]"));
			}
		}
		for(int i=0;i<5;i++){
			for(int j=0;j<9;j++){
				dw2[i][j] = Double.parseDouble((String) pp.get("dw2["+i+"]["+j+"]"));
			}
		}

		double temphid[]=new double[hidN];
		double tempout[]=new double[outN];
//			if(trained==false)
//				return null;

		for(int j=0;j<hidN;++j)//计算每个隐含层单元的结果
		{
			temphid[j]=0;
			for(int k=0;k<attN;++k)
				temphid[j]+=dw1[j][k]*samin[k];
			temphid[j]+=dw1[j][attN];//计算阈值产生的隐含层结果
			temphid[j]=1.0/(1+Math.exp(-temphid[j] ));
		}
		for(int j=0;j<outN;++j)//计算每个输出层单元的结果
		{
			tempout[j]=0;
			for(int k=0;k<hidN;++k)
				tempout[j]+=dw2[j][k]*temphid[k];
			tempout[j]+=dw2[j][hidN];//计算阈值产生的输出结果
			tempout[j]=1.0/(1+Math.exp( -tempout[j]));
			//System.out.print(tempout[j]+" ");
		}
		return tempout;
	}
}
