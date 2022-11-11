package four.non.bronds.yyys.zzztimer.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingCloseAppBean;
import four.non.bronds.yyys.zzztimer.db.TimerSettingAccessor;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class TimeupActivity extends Activity {
	final static String TAG = "ZzzTimer.TimeupActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.timeup);
//        
//        
//        Log.d(TAG, "onCreate");
//        
//        AudioManager _audioManager = (AudioManager) this
//                .getSystemService(Context.AUDIO_SERVICE);
//        _audioManager
//        	.setStreamMute(AudioManager.MODE_NORMAL, true);
//        _audioManager
//    		.setStreamMute(AudioManager.MODE_RINGTONE, true);
//        _audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
//        
//        
//        
//        
//
//		Bundle bundle=this.getIntent().getExtras();
//		Log.d(TAG, "ITEM_ID : " + this.getIntent().getIntExtra("ITEM_ID", -1));
//		
//		int item_id = this.getIntent().getIntExtra("ITEM_ID", -1);
//		
//		if( item_id == -1 ) {
//			Log.d(TAG, "bundle is null");
//			return ;
//		}
//		
//	
//		try {
//			Thread.sleep(5*1000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//		
//		TimerSettingBean item = new TimerSettingBean();
//		item.setID(item_id);
//		new TimerSettingAccessor(this).loadDetail(item);
//		
//
//		// プロセスをキル
//		ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
//		List<RunningAppProcessInfo> runningApp = activityManager.getRunningAppProcesses();
//		List<ActivityManager.RunningServiceInfo> serviese = activityManager.getRunningServices(100);
//		
//
//		Log.d(TAG, "Setting Count : " + item.getListCloseApp().size());
//		for(TimerSettingCloseAppBean o : item.getListCloseApp() ) {
//			Log.d(TAG, "Setting APP : " + o.getmAppName());
//		}
//		
//		
//		if( serviese != null ) {
//			for(ActivityManager.RunningServiceInfo service : serviese) {
//				Log.d(TAG, "Service : " + service.service.getPackageName() + " Class: " + service.service.getClassName());
//
//				try {
//					
//					
//					if( item.isExistKillApp(service.service.getPackageName())) {
//						Log.d(TAG, "  Stop Service : " + service.service.getPackageName() + " Class: " + service.service.getClassName());
//						Intent ii = new Intent(this, service.service.getClass());
//						this.stopService(ii);
//						
//						killProc(activityManager, service.pid, service.service.getPackageName());
//					}
//				} catch( Exception e) {
//                	Log.e(TAG, e.getMessage());
//                	e.printStackTrace();
//				}
//			
//				try {
//					if( item.isExistKillApp( service.service.getPackageName() )) {
//						Log.d(TAG, "load foreign package : ");
//						Context foreignContext = createPackageContext(
//								service.service.getPackageName(), 
//								Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
//						Intent ii = new Intent(foreignContext, service.service.getClass());		
//						
//						foreignContext.stopService( ii );
//					
//					}
//
//				} catch( Exception e) {
//                	Log.e(TAG, e.getMessage());
//                	e.printStackTrace();
//				}
//		}
//		}
//		
//		
//		
////		PackageManager packageManager = getPackageManager();
//		if(runningApp != null) {
//
//
//			
//			for(RunningAppProcessInfo app : runningApp) {
//				
//				
//	
//				                        
//				String strProcName = app.processName;
//				int pos = strProcName.indexOf(":");
//				if( pos != -1 ) {
//					strProcName = strProcName.substring(0, pos);
////					Log.d(TAG, "APP : " + app.processName + "   Remote : " +  strProcName);
//				} else {
////					Log.d(TAG, "APP : " + app.processName);
//				}
//									
//				
//				if( item.isExistKillApp(strProcName)) {
//					killProc(activityManager, app.pid, strProcName);		                
//				}
//				
//			}
//		}
        
    }
//    
//	private void killProc(ActivityManager activityManager, int pid, String strPackageName)
//	{
//		Log.d(TAG, " KILL APP : " + strPackageName + "  PID : " + pid);
//		// プロセスをKill
//		android.os.Process.killProcess(pid);
//		// プロセスをKill
//		activityManager.restartPackage(  strPackageName );
//
//		if( Build.VERSION.SDK_INT >= 8) {
//			killTaskFroyo(strPackageName);
//		}
//
//		try {
//			Class<?> partypes[] = new Class[1];
//            partypes[0] = String.class;
//			Method killBackgroundProcesses;
//			killBackgroundProcesses = ActivityManager.class.getMethod("killBackgroundProcesses", partypes);
//
//			Log.d(TAG, " killBackgroundProcesses : " +  strPackageName);
//			Object arglist[] = new Object[1];
//			arglist[0] = strPackageName;  
//			killBackgroundProcesses.invoke(activityManager, arglist);
//		} catch( Exception e) {
//        	Log.e(TAG, e.getMessage());
//        	e.printStackTrace();
//		}
//		
//        
////        try {
////	        String [] theArgs = new String [3];
////            
////            theArgs[0] = "su";
////            theArgs[2] = "kill";
////            theArgs[3] = "" + pid;		                
////
////            Log.d(TAG, " kill command : " +  pid);
////        	Process proc =Runtime.getRuntime().exec(theArgs);
//////            InputStream is = proc.getInputStream();
//////            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//////            String line;
//////            while ((line = br.readLine()) != null) {
//////            	Log.d(TAG, line);
//////            }
////        	proc.wait();
////        } catch (Exception e) {
////        	Log.e(TAG, e.getMessage());
////        	e.printStackTrace();
////        }
//	}
//
//    public void killTaskFroyo(String pkgName) {
//        try {
//        		Log.d(TAG, " killTaskFroyo - " + pkgName);
//        		
//        		
//                Class<?> c = Class.forName("android.app.ActivityManagerNative");
//                Method getDefaultMethod = c.getMethod("getDefault");
//                getDefaultMethod.setAccessible(true);
//                Object nativeManager = getDefaultMethod.invoke(null);
//                c = nativeManager.getClass();
//                Method forceStopPackageMethod = c.getMethod("forceStopPackage",
//                                String.class);
//                forceStopPackageMethod.setAccessible(true);
//                forceStopPackageMethod.invoke(nativeManager, pkgName);
//        } catch (Exception e) {
//                ;
//        }
//}
}
