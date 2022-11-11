package four.non.bronds.yyys.zzztimer.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import four.non.bronds.yyys.zzztimer.cmn.Constant;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MyLog {
	public static int	DISP_NORMAL	= 0;
	public static int	DISP_LARGE	= 1;
	static int 			mDbgMode = -1;
	static boolean 		mError = false;
	static boolean 		mFunc = false;
	static boolean 		mInfo = false;
	static boolean 		mTrace = false;
	static boolean 		mSequence = false;
	static boolean 		mSQL = false;
	static boolean 		mLogFileWrite = false;
	static boolean 		mLogCatWrite = false;
	static String       mLogFileName = "log.txt";
	static String       mLogDirectory = "";
	static int			mDisp = DISP_NORMAL;

	public static int getDisp() {
		return mDisp;
	}
	public static void setDisp(int mDisp) {
		MyLog.mDisp = mDisp;
	}
	
	
	public static int getDbgMode() {
		return mDbgMode;
	}
	public static void setDbgMode(int dbgMode) {
		mDbgMode = dbgMode;
	}
	public static boolean isError() {
		return mError;
	}
	public static void setError(boolean error) {
		mError = error;
	}
	public static boolean isFunc() {
		return mFunc;
	}
	public static void setFunc(boolean func) {
		mFunc = func;
	}
	public static boolean isInfo() {
		return mInfo;
	}
	public static void setInfo(boolean info) {
		mInfo = info;
	}
	public static boolean isTrace() {
		return mTrace;
	}
	public static void setTrace(boolean trace) {
		mTrace = trace;
	}
	public static boolean isSequence() {
		return mSequence;
	}
	public static void setSequence(boolean sequence) {
		mSequence = sequence;
	}
	public static boolean isSQL() {
		return mSQL;
	}
	public static void setSQL(boolean msql) {
		mSQL = msql;
	}
	public static boolean isLogFileWrite() {
		return mLogFileWrite;
	}
	public static void setLogFileWrite(boolean mLogFileWrite) {
		MyLog.mLogFileWrite = mLogFileWrite;
	}
	public static boolean isLogCatWrite() {
		return mLogCatWrite;
	}
	public static void setLogCatWrite(boolean mLogCatWrite) {
		MyLog.mLogCatWrite = mLogCatWrite;
	}	
	public static String getLogFileName() {
		return mLogFileName;
	}
	public static void setLogFileName(String logFileName) {
		mLogFileName = logFileName;
	}
	public static String getLogDirectory() {
		return mLogDirectory;
	}
	public static void setLogDirectory(String mLogDirectory) {
		MyLog.mLogDirectory = mLogDirectory;
	}

	/**
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void tost(Context context, String text, int duration) {
		if( duration == 0 ) {
			duration = Toast.LENGTH_SHORT;
		} else {
			duration = Toast.LENGTH_LONG;
		}
		Toast.makeText(context, text, duration).show();
	}
	
	
	/**
	 * @return
	 */
	static public boolean isDebugMod()
	{
		return mDbgMode == 1 ? true : false;
	}
	
	static public void loge(String TAG, Exception e) {
		if( isDebugMod() == false ) {
			return ;
		}
		if( mLogCatWrite == true ) {
			if( e.getMessage() != null  ) {
				Log.e(Constant.APP_NAME, TAG + ":" +  e.getMessage());
			} else {
				Log.e(Constant.APP_NAME, TAG + ": message is null");
			}
			Log.e(Constant.APP_NAME, TAG + e.getClass().toString() );
			{
				for( StackTraceElement es : e.getStackTrace() ) {
					Log.e(Constant.APP_NAME, "   " + es.toString());
				}
				
			}
		}
		
	
		if( mError == false ) {
			return;
		}
		if( mLogFileWrite == false ) {
			return ;
		}
		try {
			java.io.FileWriter fs = new java.io.FileWriter(mLogDirectory + "/" + mLogFileName, true);

			fs.write(" E ");
			fs.write(TAG);
			fs.write(" ");
			
			fs.write( e.getMessage() );
			fs.write("\n");
			for( StackTraceElement es : e.getStackTrace() ) {
				fs.write("   ");
				fs.write(es.toString());
				fs.write("\n");
			}
			
			fs.write("\n");
			fs.flush();
			fs.close();
			fs = null;
			
		} catch(Exception eee) {
		}		
	}
	
	static public void logRaw(String tag, String prefix,  String msg) 
	{
		
		if( mLogFileWrite == false ) {
			return ;
		}
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
			java.util.Date date = new java.util.Date();
			java.io.FileWriter fs = new java.io.FileWriter(mLogDirectory + "/" + mLogFileName, true);
			
			fs.write(dateFormat.format(date));
			fs.write(prefix);
			fs.write(" ");
			fs.write(tag);
			fs.write(" ");
			fs.write(msg);
			fs.write("\n");
			fs.flush();
			fs.close();
			fs = null;
			
		} catch(Exception e) {
		}
	}

	static public void logf(String tag, String msg) 
	{
		if( isDebugMod() == false ) {
			return ;
		}
		if( mFunc == false ) {
			return;
		}
		if( mLogCatWrite == true ) {
			Log.v(Constant.APP_NAME, tag + ":" +  msg);
		}
		logRaw(tag, "F", msg);
	}
	
	static public void logi(String tag, String msg) 
	{
		if( isDebugMod() == false ) {
			return ;
		}
		if( mInfo == false ) {
			return;
		}
		if( mLogCatWrite == true ) {
			Log.i(Constant.APP_NAME, tag + ":" +  msg);
		}
		logRaw(tag, "I", msg);
	}

	static public void logt(String tag, String msg) 
	{
		if( isDebugMod() == false ) {
			return ;
		}
		if( mTrace == false ) {
			return;
		}
		if( mLogCatWrite == true ) {
			Log.v(Constant.APP_NAME, tag + ":" +  msg);
		}
		logRaw(tag, "T", msg);
	}

	static public void logs(String tag, String msg) 
	{
		if( isDebugMod() == false ) {
			return ;
		}
		if( mSequence == false ) {
			return;
		}
		if( mLogCatWrite == true ) {
			Log.v(Constant.APP_NAME, tag + ":" +  msg);
		}
		logRaw(tag, "S", msg);
	}
	

	static public void logSQL(String tag, String msg) 
	{
		if( isDebugMod() == false ) {
			return ;
		}
		if( mSQL == false ) {
			return;
		}
		if( mLogCatWrite == true ) {
			Log.v(Constant.APP_NAME, tag + ":" +  msg);
		}
		logRaw(tag, "SQL", msg);
	}
}
