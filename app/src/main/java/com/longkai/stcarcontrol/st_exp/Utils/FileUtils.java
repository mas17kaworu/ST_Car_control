package com.longkai.stcarcontrol.st_exp.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DCIM;


/**
 * SlideUtil
 *
 * @author rui on 2017/05/11.
 * @version 1.0
 * @since 1.0
 */

public class FileUtils {

//	private static String INTERNAL_PATH = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM) + "/";
	public static String INTERNAL_PATH = Environment.getExternalStorageDirectory() + "/";

	public static String DIAGRAM_PIC = "ST_DIAGRAM";

	public static String getInternalPath()
	{
		return INTERNAL_PATH;
	}


	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static File createSDFile(String fileName) throws IOException
	{
		File file = new File(INTERNAL_PATH + fileName);
		file.createNewFile();
		return file;
	}

	/*
	 * 在SD卡上创建目录
	 * */
	public static File createSDDir(String dirName) {
		File dir = new File(INTERNAL_PATH + dirName);

		if (dir.mkdir()){
            Log.i("File", "createSDDir success");
        }else {
            Log.i("File", "createSDDir failed");
        }
		return dir;
	}

	/*
	 * 判断SD卡上文件夹是否存在
	 *
	 * */
	public static boolean isFileExist(String fileName)
	{
		File file = new File(INTERNAL_PATH + fileName);
		return file.exists();
	}

	/*
	 * 将一个InputStream里面的数据写入到SD卡中
	 * */
	public static File write2SDFromInput(String path, String fileName, InputStream input)
	{
		File file = null;
		OutputStream output = null;
		try{
			byte[] arr = new byte[1];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(baos);
			int n = input.read(arr);

			while (n > 0) {
				bos.write(arr);
				n = input.read(arr);
			}
			bos.close();
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			output.write(baos.toByteArray());

			output.flush();
			baos.close();
			/*
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[10 * 1024*1024];
			while((input.read(buffer)) != -1)
			{
				output.write(buffer);
			}
			output.flush();
			*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{

				output.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return file;
	}

	public static void copyDiagram2SDCard(Context context, String fileName){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.d("File", "copyDiagram2SDCard: sdcard exist");
		}

		if (!isFileExist(DIAGRAM_PIC)){
			createSDDir(DIAGRAM_PIC);
		}
		if (!isFileExist(DIAGRAM_PIC + "/" + fileName)){
			try{
				InputStream is = context.getAssets().open(fileName);
				FileOutputStream fos = new FileOutputStream(new File(INTERNAL_PATH + DIAGRAM_PIC + "/" + fileName));
				byte[] buffer = new byte[1024];
				int byteCount = 0;
				while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
					// buffer字节
					fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
				}
				fos.flush();// 刷新缓冲区
				is.close();
				fos.close();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static Uri getResUri(int res, Context context) {
		return Uri.parse(String.format(
				Locale.getDefault(), "android.resource://%s/%s", context.getPackageName(), res));
	}



	/**
	 * 复制asset文件到指定目录
	 * @param oldPath  asset下的路径
	 * @param newPath  SD卡下保存路径
	 */
	public static void CopyAssets(Context context, String oldPath, String newPath) {
		try {
			String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
			if (fileNames.length > 0) {// 如果是目录
				File file = new File(newPath);
				file.mkdirs();// 如果文件夹不存在，则递归
				for (String fileName : fileNames) {
					CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
				}
			} else {// 如果是文件
				InputStream is = context.getAssets().open(oldPath);
				FileOutputStream fos = new FileOutputStream(new File(newPath));
				byte[] buffer = new byte[1024];
				int byteCount = 0;
				while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
					// buffer字节
					fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
				}
				fos.flush();// 刷新缓冲区
				is.close();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//private void openFile(Uri pickerInitialUri) {
	//	Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
	//	intent.addCategory(Intent.CATEGORY_OPENABLE);
	//	intent.setType("application/pdf");
	//
	//	// Optionally, specify a URI for the file that should appear in the
	//	// system file picker when it loads.
	//	intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
	//
	//	startActivityForResult(intent, PICK_PDF_FILE);
	//}


}
