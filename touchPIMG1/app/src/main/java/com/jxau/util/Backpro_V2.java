package com.jxau.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * @ClassName: ${Backpro}
 * @Description: ${获取11个坐标点，并与数据匹配返回结果}
 * @author tugeng
 * @version 1.0.1
 * ${tags}
 */
public class Backpro_V2 {

	private  double testin[] = new double[22];//传入数据二维数组
	private String filePath;//数据库路径
	private double h=0.01; //默认值

	public double[] test2(double[] line){
		double max = 450;
		double min = 0;

		for(int j=0;j<15;j++){
			//System.out.print(line[j]+",");
			line[j] = (line[j]-min)/(max-min);
			//System.out.print(line[j]+",");
		}
		//System.out.println();
		return line;

	}

	public double test1(double x1,double y1,double x2,double y2){
		double d = Math.sqrt(Math.pow(x1-x2,2)+ Math.pow(y1-y2,2));
		return d;
	}
	public String findResult() {
		//String filename=new String("/storage/sdcard/Download/fruitflies_1.points.txt");

		StringBuffer result = new StringBuffer();

		String filename=new String(filePath);
		try {
			FileInputStream fileInputStream=new FileInputStream(filename);
			Scanner sinScanner=new Scanner(fileInputStream);
			int attN,hidN,outN,att2N;
			//attN=sinScanner.nextInt();//11
			attN = 15;

			att2N = 22;
			//outN=sinScanner.nextInt();//1
			outN = 5;
			//hidN=sinScanner.nextInt();  //4
			hidN = 8;

			int times=50000;
			double rate=0.5;
			BP2_V2 bp2=new BP2_V2(attN,outN,hidN,times,rate);

			/*for(int i=0;i<hidN;++i)
			{
				for(int j=0;j<attN;++j)
					System.out.print(bp2.dw1[i][j]+" ");
				System.out.println();
			}
			for(int i=0;i<outN;++i)
			{
				for(int j=0;j<hidN;++j)
					System.out.print(bp2.dw2[i][j]+" ");
				System.out.println();
			}*/
			//while(true)
			//{
			double testout[]=new double[outN];
			double testin2[] = new double[15];
			testin2[0] = test1(testin[0],testin[1],testin[2],testin[3]);
			testin2[1] = test1(testin[0],testin[1],testin[6],testin[7]);
			testin2[2] = test1(testin[6],testin[7],testin[8],testin[9]);

			testin2[3] = test1(testin[8],testin[9],testin[10],testin[11]);

			testin2[4] = test1(testin[2],testin[3],testin[10],testin[11]);

			testin2[5] = test1(testin[4],testin[5],testin[12],testin[13]);

			testin2[6] = test1(testin[4],testin[5],testin[6],testin[7]);

			testin2[7] = test1(testin[8],testin[9],testin[16],testin[17]);

			testin2[8] = test1(testin[10],testin[11],testin[18],testin[19]);

			testin2[9] = test1(testin[14],testin[15],testin[16],testin[17]);

			testin2[10] = test1(testin[16],testin[17],testin[18],testin[19]);

			testin2[11] = test1(testin[18],testin[19],testin[20],testin[21]);

			testin2[12] = test1(testin[12],testin[13],testin[20],testin[21]);

			testin2[13] = test1(testin[12],testin[13],testin[14],testin[15]);

			testin2[14] = test1(testin[4],testin[5],testin[14],testin[15]);
			testin2 = test2(testin2);

			testout=bp2.getResault(testin2);

			for(int i=0;i<outN;i++){  ////开始与域值作比较，并作替换
				System.out.println("--before>>>>"+testout[i]);
				if( Math.abs(1-testout[i]) < h){
					testout[i] = 1.0;
				}else if(Math.abs(0-testout[i]) < h){
					testout[i] = 0.0;
				}else{
					testout[i] = -1.0;    ////都不满足条件，属于未知，用-1代替
				}
				System.out.println("--after>>>>"+testout[i]);
			}
			int max_index = 0;   ////记录1出现的位置
			int count4one = 0;    /////记录1出现的次数
			boolean haveUnknown  = false;   ////是否有未知的
			for(int i=0;i<outN;++i){
				if( 1.0 == testout[i]){
					count4one++;
					max_index = i;
				}else if(-1.0 == testout[i]){  ////出现-1 则表示未知
					haveUnknown  = true;
					break;
				}
				if(count4one > 1) {
					haveUnknown  = true;
					break;
				}
			}
			System.out.println(count4one+">>>>>>>>count4one");
			System.out.println(max_index+">>>>>>>>max_index");
			if( !haveUnknown  && count4one == 1){
				if(max_index+1 == 1){
					result.append("桔小实蝇\n");
				}else if(max_index+1 == 2){
					result.append("南亚果实蝇\n");
				}else if(max_index+1 == 3){
					result.append("具条实蝇\n");
				}else if(max_index+1 == 4){
					result.append("瓜实蝇\n");
				}else{
					result.append("番石榴实蝇\n");
				}
			}else{
				result.append("未知果蝇\n");
			}
			return result.toString();
			//	System.out.print(max_index+1+"号果蝇");
			//	System.out.println(outN);
			//}
		} catch (IOException e) {
			e.printStackTrace();
			return "抱歉，没有找到数据文件！";
		}
		//return "抱歉，没有找到相应的结果！";
	}

	public void setTestin(double testin[]){
		this.testin = testin;
	}
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	public void setH(double h){
		this.h = h;
	}
	public double getH(){
		return h;
	}
}
