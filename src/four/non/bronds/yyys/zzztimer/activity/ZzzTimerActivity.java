package four.non.bronds.yyys.zzztimer.activity;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.R.layout;
import four.non.bronds.yyys.zzztimer.bean.Computer;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingCloseAppBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingStartApp;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.cmn.Constant.PurchaseState;
import four.non.bronds.yyys.zzztimer.cmn.Constant.ResponseCode;
import four.non.bronds.yyys.zzztimer.db.TimerSettingAccessor;
import four.non.bronds.yyys.zzztimer.dialog.HelpDialog;
import four.non.bronds.yyys.zzztimer.service.TimerService;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.Market;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.PrefereceAcc;
import four.non.bronds.yyys.zzztimer.util.StrageUtil;
import four.non.bronds.yyys.zzztimer.vending.billing.BillingService;
import four.non.bronds.yyys.zzztimer.vending.billing.PurchaseObserver;
import four.non.bronds.yyys.zzztimer.vending.billing.ResponseHandler;
import four.non.bronds.yyys.zzztimer.vending.billing.BillingService.RequestPurchase;
import four.non.bronds.yyys.zzztimer.vending.billing.BillingService.RestoreTransactions;
import four.non.bronds.yyys.zzztimer.widgets.OnOffBotton;
import four.non.bronds.yyys.zzztimer.widgets.cover.CoverAdapterView;
import four.non.bronds.yyys.zzztimer.widgets.cover.CoverFlow;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.DateSlider;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.TimeSlider;
import four.non.bronds.yyys.zzztimer.widgets.spinner.CustomSpinner;
import four.non.bronds.yyys.zzztimer.widgets.spinner.CustomSpinner.CustomSpinnerListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView.ScaleType;





public class ZzzTimerActivity extends MyBaseActivity {
	final static String TAG = "MainActivity";
	
	protected MyAdapter		mAdapter;
	protected ViewPager 	mPager;
	
	public TabsFragment	mTabFragment;
	// サービス
	private TimerService 					mTimerService ;
	

	// 課金サービス関連
	private BillingService					mBillingService = null;
	private ZzzTimerPurchaseObserver		mZzzTimerPurchaseObserver = null;
	private Handler 						mHandler = null;
	private boolean							mSuportBiliing = false;
	

	public TimerService getTimerService() {
		return mTimerService;
	}
    public BillingService getBillingService() {
		return mBillingService;
	}
	public boolean isSuportBiliing() {
		return mSuportBiliing;
	}

	/**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class ZzzTimerPurchaseObserver extends PurchaseObserver {
    	final static String TAG = "ZzzTimerPurchaseObserver";
        public ZzzTimerPurchaseObserver(Handler handler) {
            super(ZzzTimerActivity.this, handler);
        }

		@Override
		public void onBillingSupported(boolean supported, String type) {
			if( MyLog.isDebugMod()){
				MyLog.logf(TAG, "::onBillingSupported start");
				MyLog.logf(TAG, "    supported      : " + supported);
				MyLog.logf(TAG, "    type           : " + type);
			}
			mSuportBiliing = supported;
			if( mSuportBiliing ) {
				restoreDatabase();
			}
			// タイマーフラグメントに、サービスの情報をセット。
			if( mAdapter.mTimerFragment != null ) {
//				mAdapter.mTimerFragment.setService( mBillingService, mSuportBiliing );
			}
			if( MyLog.isDebugMod()){MyLog.logf(TAG, "::onBillingSupported end");}
		}
		@Override
		public void onPurchaseStateChange(PurchaseState purchaseState,
				String itemId, int quantity, long purchaseTime,
				String developerPayload) {
			if( MyLog.isDebugMod()){
				MyLog.logf(TAG, "::onPurchaseStateChange start");
				MyLog.logf(TAG, "    purchaseState      : " + purchaseState);
				MyLog.logf(TAG, "    itemId             : " + itemId);
				MyLog.logf(TAG, "    quantity           : " + quantity);
				MyLog.logf(TAG, "    purchaseTime       : " + purchaseTime);
				MyLog.logf(TAG, "    developerPayload   : " + developerPayload);
			}
			
			if (purchaseState == PurchaseState.PURCHASED) {
				
			}
			if( MyLog.isDebugMod()){MyLog.logf(TAG, "::onPurchaseStateChange end");}
		}

		@Override
		public void onRequestPurchaseResponse(RequestPurchase request,
				long requestId, ResponseCode responseCode) {
			if( MyLog.isDebugMod()){
				MyLog.logf(TAG, "::onRequestPurchaseResponse start");
				MyLog.logf(TAG, "    ProductId    : " + request.mProductId);
				MyLog.logf(TAG, "    requestId    : " + requestId);
				MyLog.logf(TAG, "    responseCode : " + responseCode);
			}
			if (responseCode == ResponseCode.RESULT_OK) {
				
			} else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
				
			} else {
				
			}
			if( MyLog.isDebugMod()){MyLog.logf(TAG, "::onRequestPurchaseResponse end");}
		}

		@Override
		public void onRestoreTransactionsResponse(RestoreTransactions request,
				ResponseCode responseCode) {
			if( MyLog.isDebugMod()){
				MyLog.logf(TAG, "::onRestoreTransactionsResponse start");
				MyLog.logf(TAG, "    request      : " + request);
				MyLog.logf(TAG, "    responseCode : " + responseCode);
			}
			if (responseCode == ResponseCode.RESULT_OK) {
				SharedPreferences prefs = ZzzTimerActivity.this.getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putBoolean(Constant.SHARED_PREF_DB_INITIALIZED, true);
				edit.commit();
			} else {
				
			}
			if( MyLog.isDebugMod()){MyLog.logf(TAG, "::onRestoreTransactionsResponse end");}
		}
    }
	
	// サービスコネクション
	private ServiceConnection serviceConnection = new ServiceConnection() {
		final static String TAG = "ServiceConnection";
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			if( MyLog.isDebugMod() ) { 
				MyLog.logf(TAG, "onServiceConnected start");
				MyLog.logt(TAG, "    ClassName:" + className.getClassName());
			}
			mTimerService = ((TimerService.TimerServiceBinder)service).getService();
			if( mAdapter != null ) {
				if( mAdapter.mTimerFragment != null ) {
//					mAdapter.mTimerFragment.setMainActivity( ZzzTimerActivity.this );
//					mAdapter.mTimerFragment.setService(mTimerService);
					mAdapter.mTimerFragment.reView();
				}
			}
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onServiceConnected end"); }
		}
		@Override
		public void onServiceDisconnected(ComponentName className) {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onServiceDisconnected start"); }
			mTimerService = null;
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onServiceDisconnected end"); }
		}
	};
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onActivityResult start"); }
		try {
			if( resultCode == Constant.RQ_CODE_EDT_COMPUTER ) {
				// コンピューター情報編集のActivityから戻った。
				if( mTabFragment != null ) {
					mTabFragment.notifyComputer((Computer)data.getSerializableExtra("COMPUTER"), true);
				}
			} else
			if( resultCode == Constant.RQ_CODE_KILL_APP ||
				resultCode == Constant.RQ_CODE_STARTL_APP) {
				if( mTabFragment != null ) {
					mTabFragment.notifyTimerItem((TimerSettingBean)data.getSerializableExtra(Constant.INTENT_TAG_TEIMER_ITEM), true);
				}
				
			}
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onActivityResult end"); }
		}		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
    	try {
        	PrefereceAcc prefAcc = new PrefereceAcc(this);
        	SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF, MODE_WORLD_READABLE |MODE_WORLD_WRITEABLE );
        	if( sp.contains(Constant.SHARED_PREF_REC_BIT_PER_SEC) == false ) {
        		AudioManager 			mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        		int iMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        		Editor editor = sp.edit();
        		editor.putString(Constant.SHARED_PREF_REC_BIT_PER_SEC, "22050");
        		editor.putString(Constant.SHARED_PREF_REC_FORMAT, "1");
        		editor.putInt(Constant.SHARED_PREF_PLAYMUSIC_VOLUM, iMaxVolume/2);
        		editor.putString(Constant.SHARED_PREF_DBG_PLAYLIST_WHERE, "0");
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_DBGMODE, false);
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_ERR, false);
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_FUNC, false);
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_INF, false);
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_TRACE, false);
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_SEQ, false);
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_SQL, false);
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_W_FILE, false);
        		editor.putBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_W_CAT, false);
        		editor.putString(Constant.SHARED_PREF_DBG_LOG_FILE_NAME, "log.txt");
        		editor.commit();
        	}
			MyLog.setDbgMode( prefAcc.getLogDbgmode() ? 1 : 0);
			MyLog.setError( prefAcc.getLogOptErr() );
			MyLog.setFunc( prefAcc.getLogOptFunc() );
			MyLog.setInfo( prefAcc.getLogOptInf() );
			MyLog.setTrace( prefAcc.getLogOptTrace() );
			MyLog.setSequence( prefAcc.getLogOptSEQ() );
			MyLog.setSQL( prefAcc.getLogOptSQL() );
			MyLog.setLogFileWrite( prefAcc.getLogOptWriteFile() );
			MyLog.setLogCatWrite( prefAcc.getLogOptWriteCat() );
			MyLog.setLogFileName( prefAcc.getLogFileName() );
			try {
				if( StrageUtil.isSDcardExist(this) ) {
					MyLog.setLogDirectory(StrageUtil.getBaseDir(this));
				}
			} catch(Exception e) {
				
			}
    	} catch(Exception e) {
    		MyLog.loge(TAG, e);
    	}
    	

        try {
        	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreate start"); }
        	
        	
            super.onCreate(savedInstanceState);
        	
            setContentView(R.layout.main2);
            TextView v;
            
            

    		mHandler = new Handler();
    		mZzzTimerPurchaseObserver = new ZzzTimerPurchaseObserver(mHandler);
    		mBillingService = new BillingService();
    		mBillingService.setContext(this);
            
            
            
            mPager = (ViewPager)findViewById(R.id.pager);

            
            // Pagerのアダプターを作成
            mAdapter = new MyAdapter(getSupportFragmentManager(), mPager, this);

            
            v = (TextView)this.findViewById(R.id.textNormal);
            if( v != null ) {
            	MyLog.setDisp(MyLog.DISP_NORMAL);
            	MyLog.logi(TAG,	"Normal Display");
            }
            v = (TextView)this.findViewById(R.id.textLarge);
            if( v != null ) {
            	MyLog.setDisp(MyLog.DISP_LARGE);
            	MyLog.logi(TAG,	"Normal Large");
            }
            

			// ウィンドウマネージャのインスタンス取得
			WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
			// ディスプレイのインスタンス生成
			Display disp = wm.getDefaultDisplay();
			disp.getPixelFormat();
			if( MyLog.isDebugMod() ) { 
				MyLog.logi(TAG,	"Width = " + disp.getWidth());
				MyLog.logi(TAG,	"Highht = " + disp.getHeight());
			}
			DisplayMetrics displayMetrics = new DisplayMetrics();
			disp.getMetrics(displayMetrics);

			// 1インチが2.54cmなので約4.0インチは約10.16cm
			//Log
			if( MyLog.isDebugMod() ) { 
				MyLog.logi("xdpi",            String.valueOf(displayMetrics.xdpi)); 
				MyLog.logi("ydpi",            String.valueOf(displayMetrics.ydpi)); 
				MyLog.logi("widthPixels",    String.valueOf(displayMetrics.widthPixels));
				MyLog.logi("heightPixels",    String.valueOf(displayMetrics.heightPixels));
				MyLog.logi("density",        String.valueOf(displayMetrics.density));
				MyLog.logi("scaledDensity",    String.valueOf(displayMetrics.scaledDensity));
	
				MyLog.logi("width",            String.valueOf(disp.getWidth()));
				MyLog.logi("height",            String.valueOf(disp.getHeight()));
				MyLog.logi("orientation",    String.valueOf(disp.getOrientation()));
				MyLog.logi("refreshRate",    String.valueOf(disp.getRefreshRate()));
				MyLog.logi("pixelFormat",    String.valueOf(disp.getPixelFormat()));
			}
            
	        // フラグメントマネージャを取得
	    	FragmentManager fm = getSupportFragmentManager();
	        FragmentTransaction ft = fm.beginTransaction();
	
	        mTabFragment = (TabsFragment)fm.findFragmentById(R.id.navi_panel);
	        if( mTabFragment != null ) {
	        	mTabFragment.setPager(this, mAdapter, mPager);
	        }
	        // メニューフラグメントを登録
	        Fragment menuFrag  = fm.findFragmentByTag("pref");
	        if( menuFrag == null ) {
	        	// ない場合のみ登録させる
	        	menuFrag = new MenuFragment();
	        	((MenuFragment)menuFrag).mTabFragment = mTabFragment; 
	        	((MenuFragment)menuFrag).mActivity = this; 
	        	ft.add(menuFrag, "pref");
	        }
	        ft.commit();
	        
	        
	        
	        
			// サービスを開始
			Intent intent = new Intent(this, TimerService.class);
			startService(intent);
			// サービスにバインド
			bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
			// いったんアンバインドしてから再度バインド
			unbindService(serviceConnection);
			bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
			
			



	        // Check if billing is supported.
	        ResponseHandler.register(mZzzTimerPurchaseObserver);

	        if (!mBillingService.checkBillingSupported()) {
	            //showDialog(DIALOG_CANNOT_CONNECT_ID);
	        }
        } catch(Exception e) {
        	showAlert( e.getMessage() );
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreate end"); }
		}
    }

    
	@Override
	public void onDestroy() {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onDestroy start"); }
		
//		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		Fragment prev = getSupportFragmentManager().findFragmentByTag("pref");
//		if (prev != null) {
//			ft.remove(prev);
//		}
//		ft.commit();
		
		super.onDestroy();
		if( serviceConnection != null ) {
			unbindService(serviceConnection); // バインド解除
		}
		serviceConnection = null;
		if( mBillingService!=null) {
			mBillingService.unbind();
		}
//		unregisterReceiver(receiver); // 登録解除
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onDestroy end"); }
	}

	@Override
	public void onPause()
	{
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onPause start"); }
		super.onPause();
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onPause end"); }
	}

	@Override
	public void onPostResume()
	{
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onPostResume start"); }
		super.onPostResume();
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onPostResume end"); }
	}

	@Override
	public void onResume()
	{
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onResume start"); }
		super.onPostResume();
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onResume end"); }
	}

	@Override
	public void onStart()
	{
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onStart start"); }
		super.onPostResume();
		ResponseHandler.register(mZzzTimerPurchaseObserver);
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onStart end"); }
	}

	@Override
	public void onStop()
	{
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onStop start"); }
		super.onPostResume();
		ResponseHandler.unregister(mZzzTimerPurchaseObserver);
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onStop end"); }
	}

//	public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem item;
//        //SubMenu sub = menu.addSubMenu("Opts");
//        
//        item = menu.add(1, 100, 1, this.getString(R.string.help));
//        item.setIcon( android.R.drawable.ic_menu_help);
//        MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM );
//        
//        item = menu.add(1, 101, 2, this.getString(R.string.preference));
//        item.setIcon( android.R.drawable.ic_menu_preferences );
//        MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM  );
//        
//        item = menu.add(1, 102, 3, this.getString(R.string.recommend));
//        item.setIcon( R.drawable.ic_menu_recommend );
//        MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM  );
//	    return true;
//	}
    /**
     * 購入DBをリストア
     */
    private void restoreDatabase() {
        SharedPreferences prefs = ZzzTimerActivity.this.getPreferences(Context.MODE_PRIVATE);
        boolean initialized = prefs.getBoolean(Constant.SHARED_PREF_DB_INITIALIZED, false);
        if (!initialized) {
            mBillingService.restoreTransactions();
            MyLog.tost(ZzzTimerActivity.this, "ZzzTimer Restoring transactions.", 1);
        }
    }
	
    /**
     * Pagerアダプター
     */
    public static class MyAdapter extends FragmentPagerAdapter {
    	final static String TAG = "MainActivity.MyAdapter";
    	private TimerFragment			mTimerFragment = null;
    	public ComputersFragment		mComputersFragment = null;
    	public StaticMusicFragment		mMusicFragment = null;
    	public StaticListRecFragment	mRecListFragment = null;
        protected Fragment 				mCurFragment = null;
        protected ZzzTimerActivity		mParent = null;
        private final ViewPager			mViewPager;
        

		public MyAdapter(FragmentManager fm, ViewPager pager, ZzzTimerActivity parent) {
            super(fm);
            if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "MyAdapter start"); }
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mParent = parent;
            if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "MyAdapter end"); }
        }
        @Override
        public int getCount() {
        	return 4;
        }
        @Override
        public Fragment getItem(int position) {
        	try {
        		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "getItem start  position:" + position); }
	        	if( mCurFragment == null || mCurFragment.getArguments().getInt("index", 0) != position) {
	            	if( position == 0 ) {
	            		mCurFragment = mTimerFragment = StaticTimerFragment.newInstance(position);
//	            		mTimerFragment.setMainActivity(mParent);
//	            		mTimerFragment.setService( mParent.mTimerService );
	            	} else
	            	if( position == 1 ) {
	           			mCurFragment = mComputersFragment = StaticComputersFlagment.newInstance(position);
	            	} else
	            	if( position == 2 ) {
	           			mCurFragment = mMusicFragment = StaticMusicFragment.newInstance(position);
	            	} else
		            if( position == 3 ) {
		            	mCurFragment = mRecListFragment = StaticListRecFragment.newInstance(position);
	            	}
	        	}
	        	if( position == 0 ) {
	        		if( MyLog.isDebugMod() ) {
	        			MyLog.logf(TAG, "getItem  mTimerFragment=" + mTimerFragment); 
	        		}
	        		if( mTimerFragment != null ) {
//	        			mTimerFragment.setMainActivity(mParent);
//	        			mTimerFragment.setService( mParent.mTimerService );
//	        			mTimerFragment.setService( mParent.mBillingService, mParent.mSuportBiliing );
	        		}
	        	}
	        	return mCurFragment;
        	} finally {
        		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "getItem end"); }
        	}
        }
    }

    /**
     * ナビゲーションパネルのフラグメント
     */
    public static class TabsFragment  
    		extends Fragment 
    		implements View.OnClickListener, ViewPager.OnPageChangeListener {
    	private static final String TAG = "MainActivity.TabsFragment";
        protected int 					mCurCheckPosition = 0;
        protected ZzzTimerActivity			mParent;
        
        private Button					mBtnNaviTimer;	
        private Button					mBtnNaviComputer;
        private Button					mBtnNaviMusic;
        private Button					mBtnNaviRec;
    	
        protected MyAdapter		mAdapter;
    	protected ViewPager 	mPager;
        
    	public void setPager(ZzzTimerActivity parent, MyAdapter adapter, ViewPager pager) { 
        	mParent = parent;
    		mAdapter = adapter;
    		mPager = pager;
    		mPager.setOnPageChangeListener(this);
    	}

        /**
         * コンピューター情報を再表示
         * @param comp
         */
        public void notifyComputer(Computer comp, boolean bActivityResult) {
        	if( mAdapter != null ) {
        		mAdapter.mComputersFragment.reView();
        	}
        }
        public void notifyTimerItem(TimerSettingBean timerItem, boolean bActivityResult) {
        	if( mAdapter != null ) {
        		mAdapter.mTimerFragment.updateTimerItem(timerItem);
        	}
        }

 
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
        	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "TabsFragment.onActivityCreated start"); }
        	super.onActivityCreated(savedInstanceState);
/*        	
        	// 右のパネルのビューを取得
        	View detailsFrame = getActivity().findViewById(R.id.work_panel);
            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }
        	
            showDetails( mCurCheckPosition );
*/
        	
        	this.setButtonActive(0);
        	
        	
        	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "TabsFragment.onActivityCreated end"); }
        }
        
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    			Bundle savedInstanceState) {
    		try {
    			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreateView start"); }
	        	final Context context = getActivity();
	
	            FrameLayout root = new FrameLayout(context);
	    		View v = inflater.inflate(R.layout.main_navi_pane, root, false);
	            root.addView(v, new FrameLayout.LayoutParams(
	                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
	
	            mBtnNaviTimer = (Button)v.findViewById(R.id.btnNaviTimer);
	            mBtnNaviTimer.setOnClickListener(this);
	            mBtnNaviComputer = (Button)v.findViewById(R.id.btnNaviComputer);
	            mBtnNaviComputer.setOnClickListener(this);
	            mBtnNaviMusic = (Button)v.findViewById(R.id.btnNaviMusic);
	            mBtnNaviMusic.setOnClickListener(this);
	            mBtnNaviRec = (Button)v.findViewById(R.id.btnNaviRec);
	            mBtnNaviRec.setOnClickListener(this);
	    		return root;
	    	} finally {
	    		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreateView end"); }
	    	}
    	}
        @Override
        public void onDestroyView(){
        	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onDestroyView start"); }
        	try {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.work_panel, null);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
        	} catch(Exception e ) {}
            super.onDestroyView();
            if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onDestroyView end"); }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
			// 設定情報保存
			super.onSaveInstanceState(outState);
			outState.putInt("curChoice", mCurCheckPosition);
        }
    	
		@Override
		public void onClick(View v) {
    		int id = v.getId();
    		int index = v.getId();
    		
    		switch(id) {
    		case R.id.btnNaviTimer:
    			index = 0;
    			break;
    		case R.id.btnNaviComputer:
    			index = 1;
    			break;
    		case R.id.btnNaviMusic:
    			index = 2;
    			break;
    		case R.id.btnNaviRec:
    			index = 3;
    			break;
    		}
    		mCurCheckPosition = index;
    		setButtonActive( index );
    		mPager.setCurrentItem( index );
		}
    	

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			setButtonActive( position );			
		}

    	/**
    	 * 詳細画面を表示
    	 * @param index
    	 */
		private void setButtonActive(int index) {
    		mBtnNaviTimer.setBackgroundColor(0xAAAAAAAA);
    		mBtnNaviComputer.setBackgroundColor(0xAAAAAAAA);
    		mBtnNaviMusic.setBackgroundColor(0xAAAAAAAA);
    		mBtnNaviRec.setBackgroundColor(0xAAAAAAAA);
    		switch(index) {
    		case 0:
    			mBtnNaviTimer.setBackgroundColor(0x00FFFFFF);
    			break;
    		case 1:
    			mBtnNaviComputer.setBackgroundColor(0x00FFFFFF);
    			break;
    		case 2:
    			mBtnNaviMusic.setBackgroundColor(0x00FFFFFF);
    			break;
    		case 3:
    			mBtnNaviRec.setBackgroundColor(0x00FFFFFF);
    			break;
    		}
		}
    }
    
    /**
     * タイマーフラグメント
     */
    public static class StaticTimerFragment extends TimerFragment {
    	public static StaticTimerFragment newInstance(int index) {
    		StaticTimerFragment f = new StaticTimerFragment();
    		
            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
    	}
    }
    /**
     * コンピューターラグメント
     */
    public static class StaticComputersFlagment extends ComputersFragment {
    	public static StaticComputersFlagment newInstance(int index) {

    		StaticComputersFlagment f = new StaticComputersFlagment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
    	}
    }
    
    public static class StaticComputerFragment extends ComputerFragment {
    	public static StaticComputerFragment newInstance(int index) {
    		StaticComputerFragment f = new StaticComputerFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
    	}
    }
    
    public static class StaticMusicFragment extends MusicPlayListEditFragment {
    	public static StaticMusicFragment newInstance(int index) {
    		StaticMusicFragment f = new StaticMusicFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
    	}
    }
    
    public static class StaticListRecFragment extends RecListFragment {
    	public static StaticListRecFragment newInstance(int index) {
    		StaticListRecFragment f = new StaticListRecFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
    	}
    }

    /**
     * メニューのフラグメント
     */
    public static class MenuFragment extends Fragment {
    	final static String TAG = "ZzzTimerActivity.MenuFragment";
    	public TabsFragment			mTabFragment;
    	public ZzzTimerActivity		mActivity;
    	
//    	public MenuFragment(TabsFragment t) {
//    		super();
//    		mTabFragment = t;
//    	}
    	
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

		//@Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            MenuItem item;
            //SubMenu sub = menu.addSubMenu("Opts");
            
            item = menu.add(1, 100, 1, this.getActivity().getString(R.string.help));
            item.setIcon( android.R.drawable.ic_menu_help);
            MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM );
            
            item = menu.add(1, 101, 2, this.getActivity().getString(R.string.preference));
            item.setIcon( android.R.drawable.ic_menu_preferences );
            MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM  );
            
            item = menu.add(1, 102, 3, this.getActivity().getString(R.string.recommend));
            item.setIcon( R.drawable.ic_menu_recommend );
            MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM  );
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onOptionsItemSelected : " + item.getItemId()); }
        	if( item.getItemId() == 100 ) {
        		PrefereceAcc acc = new PrefereceAcc(this.getActivity());
        		String strURL = "https://sites.google.com/site/zzztimerjp/";
        		int pos = mActivity.mPager.getCurrentItem();
        		switch(pos) {
        		case 0:
        			strURL = acc.getHelpURL(PrefereceAcc.HELP_URL.HELP_URL_TIMER);
        			break;
        		case 1:
        			strURL = acc.getHelpURL(PrefereceAcc.HELP_URL.HELP_URL_COMPUTER);
        			break;
        		case 2:
        			strURL = acc.getHelpURL(PrefereceAcc.HELP_URL.HELP_URL_PLAYLIST);
        			break;
        		case 3:
        			strURL = acc.getHelpURL(PrefereceAcc.HELP_URL.HELP_URL_RECLIST);
        			break;
        		}
        		HelpDialog.show(this.getActivity(), strURL);
        	} else
        	if( item.getItemId() == 101 ) {
	            Intent intent = new Intent();
	            intent.setClass(getActivity(), PrefActivity.class);
	            startActivity(intent);
	    	} else
	    	if( item.getItemId() == 102 ) {
	    		Market.intentLaunchMarketFallback(this.getActivity(), Constant.PACKAGE_ME, 0);
	    	}
        	return super.onContextItemSelected(item);
        }
        
        
        
        

//        @Override
//        public void onAttach(Activity act){
//            super.onAttach(act);
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onAttach"); }
//        }
//        @Override
//        public void onActivityCreated(Bundle bundle){
//            super.onActivityCreated(bundle);
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onActivityCreated"); }
//        }
//
//        @Override
//        public void onStart(){
//            super.onStart();
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onStart"); }
//        }
//
//        @Override
//        public void onResume(){
//            super.onResume();
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onResume"); }
//        }
//
//        @Override
//        public void onPause(){
//            super.onPause();
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onPause"); }
//        }
//
//        @Override
//        public void onStop(){
//            super.onStop();
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onStop"); }
//        }
//
//        @Override
//        public void onDestroyView(){
//            super.onDestroyView();
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onDestroyView"); }
//        }
//
//        @Override
//        public void onDestroy(){
//            super.onDestroy();
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onDestroy"); }
//        }
//
//        @Override
//        public void onDetach(){
//            super.onDetach();
//            if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onDetach"); }
//        }
    }
}