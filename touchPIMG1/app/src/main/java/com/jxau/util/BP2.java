
package com.jxau.util;

/**
 * @ClassName: ${BP2}
 * @Description: ${处理数据}
 * @author lmx
 * @date ${--} ${--}
 * @version 1.0
 * ${tags}
 */
public class BP2 {

	double dw1[][],dw2[][];
	int hidN;//隐含层单元个数  5
	int samN;//学习样例个数 201
	int attN;//输入单元个数 22
	int outN;//输出单元个数 1
	int times;//迭代次数q
	double rate;//学习速率
	boolean trained=false;//保证在得结果前，先训练
	BP2(int attN,int outN,int hidN,int samN,int times,double rate)
	{
		this.attN=attN;
		this.outN=outN;
		this.hidN=hidN;
		this.samN=samN;
		dw1=new double[hidN][attN+1];//每行最后一个是阈值w0
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
		}
		this.times=times;
		this.rate=rate;
	}
	public void train(double samin[][],double samout[][])
	{
		double dis=0;//总体误差
		int count=times;
		double temphid[]=new double[hidN];
		double tempout[]=new double[outN];
		double wcout[]=new double[outN];
		double wchid[]=new double[hidN];
		while((count--)>0)//迭代训练
		{

			//attN = 15;

			//att2N = 22;

			//outN = 5;    --> 十五个输入层

			//hidN = 8;    --> 八个隐含层

			dis=0;
			for(int i=0;i<samN;++i)//遍历每个样例 samin[i]
			{
				for(int j=0;j<hidN;++j)//计算每个隐含层单元的结果
				{
					temphid[j]=0;
					for(int k=0;k<attN;++k)
						temphid[j]+=dw1[j][k]*samin[i][k];
					temphid[j]+=dw1[j][attN];//计算阈值产生的隐含层结果
					temphid[j]=1.0/(1+Math.exp(-temphid[j] ));
				}
				for(int j=0;j<outN;++j)//计算每个输出层单元的结果
				{
					tempout[j]=0;
					for(int k=0;k<hidN;++k)
						tempout[j]+=dw2[j][k]*temphid[k];
					tempout[j]+=dw2[j][hidN];//计算阈值产生的输出结果
					tempout[j]=1.0/(1+Math.exp( -tempout[j] ));
				}
				//计算每个输出单元的误差项

				for(int j=0;j<outN;++j)  //5
				{
					wcout[j]=tempout[j]*(1-tempout[j])*(samout[i][j]-tempout[j]);
					dis+=1/2 * Math.pow((samout[i][j]-tempout[j]),2);
				}
				//计算每个隐藏单元的误差项

				for(int j=0;j<hidN;++j)    //8
				{
					double wche=0;
					for(int k=0;k<outN;++k)//计算输出项误差和
					{
						wche+=wcout[k]*dw2[k][j];
					}
					wchid[j]=temphid[j]*(1-temphid[j])*wche;
				}
				//改变输出层的权值
				for(int j=0;j<outN;++j)			//5
				{
					for(int k=0;k<hidN;++k)
					{
						dw2[j][k]+=rate*wcout[j]*temphid[k];
					}
					dw2[j][hidN]=rate*wcout[j];
				}
				//改变隐含层的权值
				for(int j=0;j<hidN;++j)				//8
				{
					for(int k=0;k<attN;++k)
					{
						dw1[j][k]+=rate*wchid[j]*samin[i][k];
					}
					dw1[j][attN]=rate*wchid[j];
				}

			}
			if(dis<0.003)
				break;
		}
		trained=true;
	}

}
