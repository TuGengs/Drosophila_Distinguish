package com.jxau.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;

import com.jxau.touchpimg.MainActivity;

import android.content.Context;
import android.util.Log;

/**
 * @ClassName: ${Backpro}
 * @Description: ${获取11个坐标点，并与数据匹配返回结果}
 * @author lmx
 * @date ${2014/9/8} ${10:09}
 * @version 1.0.1
 * ${tags}
 */
public class Backpro {

	private String filePath;
	String configpath = "/data/data/com.jxau.touchpimg/databases/parameters.properties"; //.ini文件路径

	public double[] test2(double[] line){
		double max = 450;
		double min = 0;

		for(int j=0;j<15;j++){
			//System.out.print(line[j]+",");
			line[j] = (line[j]-min)/(max-min);
		}
		//System.out.println();
		return line;
	}

	public double test1(double x1,double y1,double x2,double y2){
		double d = Math.sqrt(Math.pow(x1-x2,2)+ Math.pow(y1-y2,2));
		return d;
	}
	public String trainingAlgorithms() {
		String filename=new String(filePath);
		try {
			FileInputStream fileInputStream=new FileInputStream(filename);
			Scanner sinScanner=new Scanner(fileInputStream);
			int attN,hidN,outN,samN=0,tempN,att2N;
			//attN=sinScanner.nextInt();//11
			attN = 15;

			att2N = 22;
			//outN=sinScanner.nextInt();//1
			outN = 5;
			//hidN=sinScanner.nextInt();  //4
			hidN = 8;
			//samN=sinScanner.nextInt(); //200
			//samN = 200;
			BufferedReader b = new BufferedReader(new FileReader(filePath));
			String line = "";
			while((line = b.readLine()) != null){
				samN++;
			}
			b.close();
			tempN = samN;

			//System.out.println(attN+" "+outN+" "+hidN+" "+samN);
			double samin[][]=new double[samN][att2N];
			double osamin[][]=new double[samN][att2N]; //原始数据
			double samout[][]=new double[samN][outN];
			for(int i=0;i<samN;++i)
			{
				for(int j=0;j<outN;++j){
					samout[i][j]=sinScanner.nextDouble();	//前5个标识符
				}
				for(int j=0;j<att2N;++j){
					osamin[i][j]=sinScanner.nextDouble();	//输入层22
				}

			}
			//转成距离 200行 15列
			for(int i=0;i<samN;i++){
				samin[i][0] = test1(osamin[i][0],osamin[i][1],osamin[i][2],osamin[i][3]);
				samin[i][1]= test1(osamin[i][0],osamin[i][1],osamin[i][6],osamin[i][7]);
				samin[i][2]= test1(osamin[i][6],osamin[i][7],osamin[i][8],osamin[i][9]);

				samin[i][3]= test1(osamin[i][8],osamin[i][9],osamin[i][10],osamin[i][11]);

				samin[i][4]= test1(osamin[i][2],osamin[i][3],osamin[i][10],osamin[i][11]);

				samin[i][5]= test1(osamin[i][4],osamin[i][5],osamin[i][12],osamin[i][13]);

				samin[i][6]= test1(osamin[i][4],osamin[i][5],osamin[i][6],osamin[i][7]);

				samin[i][7]= test1(osamin[i][8],osamin[i][9],osamin[i][16],osamin[i][17]);

				samin[i][8] = test1(osamin[i][10],osamin[i][11],osamin[i][18],osamin[i][19]);

				samin[i][9] = test1(osamin[i][14],osamin[i][15],osamin[i][16],osamin[i][17]);

				samin[i][10] = test1(osamin[i][16],osamin[i][17],osamin[i][18],osamin[i][19]);

				samin[i][11]= test1(osamin[i][18],osamin[i][19],osamin[i][20],osamin[i][21]);

				samin[i][12]= test1(osamin[i][12],osamin[i][13],osamin[i][20],osamin[i][21]);

				samin[i][13] = test1(osamin[i][12],osamin[i][13],osamin[i][14],osamin[i][15]);

				samin[i][14]= test1(osamin[i][4],osamin[i][5],osamin[i][14],osamin[i][15]);

			}

			//把 距离转成0-1之间的小数
			for(int i=0;i<samin.length;i++){
				samin[i] = test2(samin[i]);
			}

			int times=50000;
			double rate=0.5;
			BP2 bp2=new BP2(attN,outN,hidN,samN,times,rate);
			bp2.train(samin, samout);

//			for(int i=0;i<hidN;++i)
//			{
//				for(int j=0;j<attN;++j)
//					System.out.print(bp2.dw1[i][j]+" ");
//				System.out.println();
//			}
//			for(int i=0;i<outN;++i)
//			{
//				for(int j=0;j<hidN;++j)
//					System.out.print(bp2.dw2[i][j]+" ");
//				System.out.println();
//			}

			OutputStream fos = null;
			Properties pp= new Properties();
			try {
				fos = new FileOutputStream(configpath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			for(int i=0;i<8;i++){
				for(int j=0;j<16;j++){
					pp.setProperty("dw1["+i+"]["+j+"]", bp2.dw1[i][j]+"");// 修改值
				}
			}
			for(int i=0;i<5;i++){
				for(int j=0;j<9;j++){
					pp.setProperty("dw2["+i+"]["+j+"]", bp2.dw2[i][j]+"");// 修改值
					//Log.e("1",String.valueOf(bp2.dw1[i][j]));
				}
			}

			try {
				pp.store(fos, null);
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "训练完成！";
		} catch (IOException e) {
			e.printStackTrace();
			return "抱歉，没有找到数据文件！";
		}
	}

	public void setFilePath(String filePath){
		this.filePath = filePath;
	}


}
