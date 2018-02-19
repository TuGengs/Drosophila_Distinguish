package com.jxau.touchpimg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.jxau.touchpimg.DragImageView.OnDrawClickListener;
import com.jxau.util.Backpro;
import com.jxau.util.Backpro_V2;
import com.jxau.util.BitmapUtil;
import com.jxau.util.WriteResourcesToPhone;
import com.slidingmenu.app.SlidingMenu;

/**
 * @ClassName: MainActivity
 * @Description: ${程序视图主类}
 * @author DengWu
 * @date
 */
public class MainActivity extends Activity {

	private DragImageView dragImageView;
	private TextView mTextView, textResult;
	private Button buttonClear, buttonsubmit, buttondraw, btnrotate,
			btnpaizhao, btntuku, btnxunlian, btnadd, btnsetting;
	private LinearLayout layout;
	private static final int REQUEST_CAMERA = 0;
	private static final int REQUEST_GALLERY = 1;
	private MyListener myListener = new MyListener();
	Bitmap globlebitmap;
	private Matrix matrix = new Matrix(); // 旋转
	private float progress = 90;
	private int window_width, window_height;
	DisplayMetrics dm = new DisplayMetrics();

	Backpro_V2 backpro = new Backpro_V2();
	private String configName = "/config.properties"; // 相关配置信息
	private String resourcesName = "/fruitfliespoints.txt";// 资源的名字
	private String parametersName = "/parameters.properties";
	private String DATABASE_PATH = "/data/data/com.jxau.touchpimg/databases/";// 资源在手机里的路径
	// private static String
	// DATABASE_PATH=android.os.Environment.getExternalStorageDirectory()+"/weather";
	private boolean isCanWrietResources = false;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		dm = getResources().getDisplayMetrics();
		int screenWidth = (int) (dm.widthPixels / 1); // 屏幕宽（像素，如：480px）
		int screenHight = (int) (dm.heightPixels / 1); // 屏幕高（像素，如：800px）
		/** 获取可見区域高度 **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		layout = (LinearLayout) findViewById(R.id.linerlarout);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		layout.setPadding(10, 10, 10, 10);

		dragImageView = (DragImageView) findViewById(R.id.drawView);
		// mTextView = (TextView)findViewById(R.id.ponitsValue);
		textResult = (TextView) findViewById(R.id.textRes);
		// dragImageView.setLayoutParams(new LayoutParams(screenWidth,
		// screenHight));
		buttonClear = (Button) findViewById(R.id.buttonclear);
		buttonsubmit = (Button) findViewById(R.id.buttonsubmit);
		buttondraw = (Button) findViewById(R.id.buttondraw);
		btnrotate = (Button) findViewById(R.id.btnrotate);

		globlebitmap = BitmapUtil.ReadBitmapById(this,
				R.drawable.ic_bg3_1, window_width, window_height);
		// 设置初始化图片
		dragImageView.setImageBitmap(globlebitmap);
		dragImageView.setmActivity(this);// 注入Activity.

		/*
		 * ----------------------------------------------------------
		 * 以下两条语句不注释掉的话，图片点击缩小会自动弹回限定大小 注释掉以后，图片缩小后会保留，不会弹回
		 */
		// dragImageView.setScreen_W(window_width);
		// dragImageView.setScreen_H(window_height);
		/*------------------------------------------------------------*/

		// 监听点击画点后，把对应的坐标显示出来------------------------------
		dragImageView.setOnDrawChangeListener(new OnDrawClickListener() {
			@Override
			public void onDrawChange(String result) {
				// TODO Auto-generated method stub
				// mTextView.setText(result);
			}
		});

		buttonClear.setOnClickListener(myListener);
		buttondraw.setOnClickListener(myListener);
		btnrotate.setOnClickListener(myListener);
		buttonsubmit.setOnClickListener(myListener);

		// 监听点击切换对图片的操作时间，防止画点与放缩的点击事件冲突-------

		// 监听点击进行图片旋转------------------------------------

		// 把得到的11个点与资源文件进行算法匹配

		isCanWrietResources = isFileExit();
		// raw里的数据写入手机----------------------------------------------
		if (!isCanWrietResources) {
			WriteResourcesToPhone writeResourcesToPhone = new WriteResourcesToPhone();
			// ----写fruitflies.points.txt 文件----------------------
			InputStream is1 = MainActivity.this.getResources().openRawResource(
					R.raw.fruitfliespoints);// 得到数据库文件的数据流
			writeResourcesToPhone.setResourcesFilename(DATABASE_PATH,
					resourcesName);
			writeResourcesToPhone.setInput(is1);
			writeResourcesToPhone.writeIn();
			// ----写Parameters.ini 文件----------------------
			InputStream is2 = MainActivity.this.getResources().openRawResource(
					R.raw.parameters);// 得到数据库文件的数据流
			writeResourcesToPhone.setResourcesFilename(DATABASE_PATH,
					parametersName);
			writeResourcesToPhone.setInput(is2);
			writeResourcesToPhone.writeIn();
			// ----
			writeResourcesToPhone.setResourcesFilename(DATABASE_PATH,
					configName);
			writeResourcesToPhone.createConfigFile();

			try {
				is1.close();
				is2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InitSlidingMenu();
	}

	public class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = null;
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.buttonclear:
					dragImageView.clearPoint();
					if (!("请先点击11个点来确认").equals(textResult.getText())) {
						textResult.setText("请先点击11个点来确认");
					}
					break;

				case R.id.buttonsubmit:
					double testin[] = dragImageView.getTestin();
					if (testin == null) {
						Toast.makeText(getApplicationContext(), "请先点满11个点!",
								Toast.LENGTH_LONG).show();
					} else {
						String databaseFilenames = DATABASE_PATH + resourcesName;

						backpro.setTestin(testin);
						backpro.setFilePath(databaseFilenames);
						String result = backpro.findResult();
						textResult.setText(result);

					}
					break;
				case R.id.btnrotate:
					dragImageView.clearPoint();
					matrix.setRotate(progress);
					Bitmap bmp1 = Bitmap.createBitmap(globlebitmap, 0, 0,
							globlebitmap.getWidth(), globlebitmap.getHeight(),
							matrix, true);
					Bitmap bmp = BitmapUtil.getBitmap(bmp1, window_width,
							window_height);
					dragImageView.setImageBitmap(bmp);
					if (progress < 360) {
						progress = progress + 90;
					} else {
						progress = 90;
					}
					break;
				case R.id.buttondraw:
					if (dragImageView.getDRAW_POINT()) {
						dragImageView.changeDRAW_POINT(false);
						buttondraw.setText("停止放缩");
						dragImageView.clearPoint();
					} else {
						dragImageView.changeDRAW_POINT(true);
						buttondraw.setText("进行放缩");
					}
					break;
				case R.id.btn_paizhao_pic:
					intent = new Intent();
					// 指定开启系统相机的Action
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					// 把文件地址转换成Uri格式
					Uri uri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "syscamera.jpg"));
					// 设置系统相机拍摄照片完成后图片文件的存放地址
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent, REQUEST_CAMERA);
					break;
				case R.id.btn_tuku_pic:
					intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, REQUEST_GALLERY);
					break;
				case R.id.btn_xunlian:
					Backpro backpro = new Backpro();
					backpro.setFilePath(DATABASE_PATH + resourcesName);
					String message = backpro.trainingAlgorithms();
					Toast.makeText(getApplicationContext(), message,
							Toast.LENGTH_LONG).show();
					break;
				case R.id.btn_add:
					double testin2[] = dragImageView.getTestin();
					if (testin2 == null) {
						Toast.makeText(getApplicationContext(), "请先点满11个点!",
								Toast.LENGTH_LONG).show();
					} else {
						chooseSamples(testin2);
					}
					break;
				case R.id.btn_setting: {
				}
				break;

				default:
					break;
			}
		}
	}

	private void InitSlidingMenu() {
		// TODO Auto-generated method stub
		SlidingMenu menu = new SlidingMenu(MainActivity.this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(MainActivity.this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidingmenumain);
		btnadd = (Button) findViewById(R.id.btn_add);
		btnpaizhao = (Button) findViewById(R.id.btn_paizhao_pic);
		btnsetting = (Button) findViewById(R.id.btn_setting);
		btntuku = (Button) findViewById(R.id.btn_tuku_pic);
		btnxunlian = (Button) findViewById(R.id.btn_xunlian);


		btnadd.setOnClickListener(myListener);
		btnpaizhao.setOnClickListener(myListener);
		btnsetting.setOnClickListener(myListener);
		btnxunlian.setOnClickListener(myListener);
		btntuku.setOnClickListener(myListener);
	}

	@Override
	/*
	 * public boolean onCreateOptionsMenu(Menu menu) { // Inflate the menu; this
	 * adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); menu.add(Menu.NONE,
	 * Menu.FIRST + 1, 5, "拍照"); menu.add(Menu.NONE, Menu.FIRST + 2, 5,
	 * "从图库获取"); menu.add(Menu.NONE, Menu.FIRST + 3, 5, "训练分类器");
	 * menu.add(Menu.NONE, Menu.FIRST + 4, 5, "添加当前样本"); menu.add(Menu.NONE,
	 * Menu.FIRST + 5, 5, "设置域值"); return true; }
	 */
	// 菜单项监听事件，相应对应的操作---------------------------
	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item){ Intent
	 * intent = null; int item_id=item.getItemId();//得到当前选中MenuItem的ID
	 * switch(item_id){ case 2:{ ///----拍照------- intent = new Intent(); //
	 * 指定开启系统相机的Action intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
	 * intent.addCategory(Intent.CATEGORY_DEFAULT); // 把文件地址转换成Uri格式 Uri uri =
	 * Uri.fromFile(new
	 * File(Environment.getExternalStorageDirectory(),"syscamera.jpg")); //
	 * 设置系统相机拍摄照片完成后图片文件的存放地址 intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	 * startActivityForResult(intent, REQUEST_CAMERA); } break; case 3:{
	 * ///----图库获取------- intent = new Intent(Intent.ACTION_PICK,
	 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	 * startActivityForResult(intent, REQUEST_GALLERY); } break; case 4:{
	 * ////----训练分类算法------ Backpro backpro = new Backpro();
	 * backpro.setFilePath(DATABASE_PATH+resourcesName); String message =
	 * backpro.trainingAlgorithms(); Toast.makeText(getApplicationContext(),
	 * message, Toast.LENGTH_LONG).show();
	 *
	 * } break; case 5:{ ///-----添加样本------ double testin[] =
	 * dragImageView.getTestin(); if(testin==null){
	 * Toast.makeText(getApplicationContext(), "请先点满11个点!",
	 * Toast.LENGTH_LONG).show(); }else{ chooseSamples(testin); }
	 *
	 * } break; case 6:{ ////---设置域值----- final LinearLayout layout = new
	 * LinearLayout(this); layout.setOrientation(LinearLayout.HORIZONTAL);
	 *
	 * final TextView textServer = new TextView(this);
	 * textServer.setText("输入值："); final EditText inputServer = new
	 * EditText(this); inputServer.setFocusable(true);
	 * inputServer.setWidth(200); layout.addView(textServer);
	 * layout.addView(inputServer);
	 *
	 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 * builder.setTitle("设置域值（当前值："+backpro.getH()+"）").setView(layout);
	 * builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
	 *
	 * @Override public void onClick(DialogInterface arg0, int arg1) {} });
	 * builder.setNeutralButton("提交", new DialogInterface.OnClickListener() {
	 *
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * if(inputServer.getText().toString().length() > 0 &&
	 * isDecimal(inputServer.getText().toString())){
	 * backpro.setH(Double.parseDouble(inputServer.getText().toString()));
	 * Toast.makeText(getApplicationContext(), "设置成功！",
	 * Toast.LENGTH_LONG).show(); }else{ Toast.makeText(getApplicationContext(),
	 * "输入有误！", Toast.LENGTH_LONG).show(); } } });
	 * builder.setPositiveButton("重置(0.01)", new
	 * DialogInterface.OnClickListener() {
	 *
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * backpro.setH(0.01); Toast.makeText(getApplicationContext(), "设置完成",
	 * Toast.LENGTH_LONG).show(); } }); builder.show(); } break; } return true;
	 * }
	 */
	// 当从图库或者相机返回时获取图片并进行处理---------------------------
	// @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		dragImageView.clearPoint();

		if (requestCode == REQUEST_CAMERA) {
			// 将保存在本地的图片取出并缩小后显示在界面上
			globlebitmap = BitmapFactory.decodeFile(Environment
					.getExternalStorageDirectory() + "/syscamera.jpg");
			Bitmap newBitmap = BitmapUtil.getBitmap(globlebitmap, window_width,
					window_height);
			// 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
			globlebitmap.recycle();
			dragImageView.setImageBitmap(newBitmap);

		}
		if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			globlebitmap = BitmapFactory.decodeFile(picturePath);
			Bitmap newBitmap = BitmapUtil.getBitmap(globlebitmap, window_width,
					window_height);
			// 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
			// globlebitmap.recycle();
			dragImageView.setImageBitmap(newBitmap);

		}
	}

	// 弹出选择对话框进行选择-----------------------------------------
	public void chooseSamples(final double t[]) {

		final String[] items = { "桔小实蝇", "南亚果实蝇", "具条实蝇", "瓜实蝇", "番石榴实蝇" };
		new AlertDialog.Builder(MainActivity.this).setTitle("请点击选择")
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						final int number = which + 1;
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("你选择了:" + items[which])
								.setMessage("点击选择操作")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int w) {
												// 这里是你点击确定之后可以进行的操作
												if (addPoints(t, number)) {
													Toast.makeText(
															getApplicationContext(),
															"添加成功",
															Toast.LENGTH_LONG)
															.show();
												} else {
													Toast.makeText(
															getApplicationContext(),
															"配置文件未找到",
															Toast.LENGTH_LONG)
															.show();
												}
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int w) {
												// 这里点击取消之后可以进行的操作
											}
										}).show();
					}
				}).show();

	}

	// ----添加样本到资源文件中---------------------------------
	public boolean addPoints(double t[], int n) {

		StringBuffer buffer = new StringBuffer();
		switch (n) {
			case 1: {
				buffer.append("0 0 0 0 1 ");
			}
			break;
			case 2: {
				buffer.append("0 0 0 1 0 ");
			}
			break;
			case 3: {
				buffer.append("0 0 1 0 0 ");
			}
			break;
			case 4: {
				buffer.append("0 1 0 0 0 ");
			}
			break;
			case 5: {
				buffer.append("1 0 0 0 0 ");
			}
			break;
			default:
				break;
		}
		for (int i = 0; i < t.length; i++) {
			buffer.append(t[i] + " ");
		}

		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(DATABASE_PATH
					+ resourcesName, true));
			out.write(buffer + "\n");
			buffer.delete(0, buffer.length());
		} catch (IOException e) {
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	// /正则判断输入的值是否为浮点型---
	public static boolean isDecimal(String str) {
		if (str == null || "".equals(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
		return pattern.matcher(str).matches();
	}

	public boolean isFileExit() {
		try {
			FileInputStream fis = new FileInputStream(DATABASE_PATH
					+ configName);
			fis.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
