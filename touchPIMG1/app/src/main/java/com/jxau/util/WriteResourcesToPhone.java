/**
 *
 */
package com.jxau.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName:
 * @Description: TODO
 * @author
 * @date
 *
 */
public class WriteResourcesToPhone {

    public  String resourcesName;//资源的名字
    private  String DATABASE_PATH;//资源在手机里的路径
    InputStream is;

    //判断资源是否存在
    boolean resourcesExist = checkResourcesBase();

    public void writeIn(){
        if( ! resourcesExist){ //不存在就把raw里的数据写入手机
            try{
                copyResourcesBase();
            }catch(IOException e){
                throw new Error("Error copying database");
            }
        }
    }


    /**
     * 判断资源是否存在
     * @return false or true
     */
    public boolean checkResourcesBase(){
        try{
            String resourcesFilename = DATABASE_PATH+resourcesName;
            FileInputStream fileInputStream = new FileInputStream(resourcesFilename);
            return true;
        }catch(IOException e){
            return false;
        }
    }

    /**
     * 复制资源到手机指定文件夹下s
     * @throws IOException
     */
    public void copyResourcesBase() throws IOException{
        String databaseFilenames =DATABASE_PATH+resourcesName;
        File dir = new File(DATABASE_PATH);
        if(!dir.exists())//判断文件夹是否存在，不存在就新建一个
            dir.mkdir();
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(databaseFilenames);//得到资源文件的写入流
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
//        InputStream is = myActivity.this.getResources().openRawResource(R.raw.fruitflies);//得到数据库文件的数据流
//        AssetManager am = null;  
//        am = getAssets();  
//        InputStream is = am.open(resourcesName);
        byte[] buffer = new byte[8192];
        int count = 0;
        try{
            while((count=is.read(buffer))>0){
                os.write(buffer, 0, count);
                os.flush();
            }
        }catch(IOException e){

        }
        try{
            is.close();
            os.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void createConfigFile(){
        File newxmlfile = new File(DATABASE_PATH,resourcesName);
        try{
            if(!newxmlfile.exists())
                newxmlfile.createNewFile();
        }catch(IOException e){
            return;
        }
    }


    public void setInput(InputStream i) {
        this.is = i;
    }

    public void setResourcesFilename(String path,String name){
        this.DATABASE_PATH = path;
        this.resourcesName = name;
    }

}
