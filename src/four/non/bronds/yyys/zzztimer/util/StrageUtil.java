package four.non.bronds.yyys.zzztimer.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import four.non.bronds.yyys.zzztimer.cmn.Constant;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;


public class StrageUtil {
	final static String TAG = "StrageUtil";
	
	public static Method getExternalFilesDir;
	
	static public boolean makeDir(String strDir) 
	{
		MyLog.logt(TAG, "makeDir : " + strDir);
		File f = new File( strDir );
    	if( f.isDirectory() == false ) {
    		MyLog.logt(TAG, "----------- mkdir " + strDir);
    		f.mkdir();
    	}		
		return true;
	}
	/**
	 * ストレージの存在を確かめて、アプリケーションディレクトリを作成する
	 * @return
	 */
	public static boolean isSDcardExist(Context ctx)
	{
		String strState = Environment.getExternalStorageState() ;
		if( !strState.equals(Environment.MEDIA_MOUNTED)  ) { 
			return false;		
		}
		
		// ディレクトリの作成
		String strBaseDir = StrageUtil.getBaseDir(ctx);
		if( strBaseDir == null ) {
			return true;
		}
    	File f = new File( strBaseDir );
    	if( f.isDirectory() == false ) {
    		f.mkdir();
    	}
		return true;
	}
	
	/**
	 * @param ctx
	 * @return
	 */
	public static String getBaseDir(Context ctx) {
		String str = "";
        try {
            Class<?> partypes[] = new Class[1];
            partypes[0] = String.class;
            getExternalFilesDir = Context.class.getMethod("getExternalFilesDir", partypes);
            
            Object arglist[] = new Object[1];
            arglist[0] = null;  

			File path = (File)getExternalFilesDir.invoke(ctx, arglist);

			str = path.getPath() + "/" + Constant.FS_APP_DIR_NAME;
		} catch (NoSuchMethodException e) {
			java.io.File file = Environment.getExternalStorageDirectory();
			str = file.getPath() + "/" + Constant.FS_APP_DIR_NAME;
		}  catch (IllegalArgumentException e) {
			java.io.File file = Environment.getExternalStorageDirectory();
			str = file.getPath() + "/" + Constant.FS_APP_DIR_NAME;
		} catch (IllegalAccessException e) {
			java.io.File file = Environment.getExternalStorageDirectory();
			str = file.getPath() + "/" + Constant.FS_APP_DIR_NAME;
		} catch (InvocationTargetException e) {
			java.io.File file = Environment.getExternalStorageDirectory();
			str = file.getPath() + "/" + Constant.FS_APP_DIR_NAME;
		} catch(Exception e) {
			MyLog.loge("getBaseDir", e);
			return null;
		}
		
		
		
		makeDir(str);
		
		return str;
	}
	
	/**
	 * 
	 * 
	 * @param ctx
	 * @return
	 */
	public static long getStrageSize(Context ctx) {
		String str = "";
        try {
            Class<?> partypes[] = new Class[1];
            partypes[0] = String.class;
            getExternalFilesDir = Context.class.getMethod("getExternalFilesDir", partypes);
            
            Object arglist[] = new Object[1];
            arglist[0] = null;  

			File path = (File)getExternalFilesDir.invoke(ctx, arglist);

			str = path.getPath();
		} catch (NoSuchMethodException e) {
			java.io.File file = Environment.getExternalStorageDirectory();
			str = file.getPath();
		}  catch (IllegalArgumentException e) {
			java.io.File file = Environment.getExternalStorageDirectory();
			str = file.getPath();
		} catch (IllegalAccessException e) {
			java.io.File file = Environment.getExternalStorageDirectory();
			str = file.getPath();
		} catch (InvocationTargetException e) {
			java.io.File file = Environment.getExternalStorageDirectory();
			str = file.getPath();
		}
        
        StatFs stat = new StatFs( str );
        long sdAvailSize = (long)stat.getAvailableBlocks() *(long)stat.getBlockSize();
        
        return sdAvailSize;
	}
	
	
	public static String getTempDir(Context ctx) {
		String str = "";
		
		str = getBaseDir(ctx);
		str += "/tmp";
		
		makeDir(str);
		return str;
	}
	
	public static String getRecDir(Context ctx) {
		String str = "";
		
		str = getBaseDir(ctx);
		str += "/rec";
		
		makeDir(str);
		return str;
	}
}

