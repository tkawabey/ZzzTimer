package four.non.bronds.yyys.zzztimer.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.Computer;
import four.non.bronds.yyys.zzztimer.bean.PurchaseInf;
import four.non.bronds.yyys.zzztimer.bean.RecTagBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingStartApp;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.cmn.Constant.PurchaseState;
import four.non.bronds.yyys.zzztimer.cmn.Constant.ResponseCode;
import four.non.bronds.yyys.zzztimer.db.ComputerAccessor;
import four.non.bronds.yyys.zzztimer.db.PurchaseAccessor;
import four.non.bronds.yyys.zzztimer.db.TimerSettingAccessor;
import four.non.bronds.yyys.zzztimer.dialog.HelpDialog;
import four.non.bronds.yyys.zzztimer.service.TimerService;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.Market;
import four.non.bronds.yyys.zzztimer.util.MusicUtils;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.PrefereceAcc;
import four.non.bronds.yyys.zzztimer.util.StrageUtil;
import four.non.bronds.yyys.zzztimer.vending.billing.BillingService;
import four.non.bronds.yyys.zzztimer.vending.billing.PurchaseObserver;
import four.non.bronds.yyys.zzztimer.vending.billing.BillingService.RequestPurchase;
import four.non.bronds.yyys.zzztimer.vending.billing.BillingService.RestoreTransactions;
import four.non.bronds.yyys.zzztimer.vending.billing.ResponseHandler;
import four.non.bronds.yyys.zzztimer.widgets.OnOffBotton;
import four.non.bronds.yyys.zzztimer.widgets.cover.CoverAdapterView;
import four.non.bronds.yyys.zzztimer.widgets.cover.CoverFlow;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.DateSlider;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.TimeSlider;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.ToggleButton;

public class TimerActivity extends MyBaseActivity {
	private static final String TAG = "TimerActivity";
	private TimerFragment mFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// フラグメントインスタンスを作成
		mFragment = new StaticTimerFragment();
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, mFragment).commit();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static class StaticTimerFragment extends TimerFragment {
	}
}

/**
 * タイマーフラグメント
 */
class TimerFragment extends MyBaseFragment {
	final static String TAG = "TimerFragment";
	private boolean mReviewing = false;
	private List<TimerSettingBean> mItems = null;
	private TimerSettingBean mCurItem = null;
	private CoverFlow mCoverFlow = null;
	private MyAdapter mCoverFlowAdapter = null;
	private OnOffBotton mScreenOn = null;
	private OnOffBotton mWifiOn = null;
	private OnOffBotton mAudioOn = null;
	private OnOffBotton mRecordingOn = null;
	private TextView mTxtTitle = null;
	private TextView mTxtTime = null;
	private TextView mTxtTimeRunning = null;
	private TextView mTxtKillApp = null;
	private TextView mTxtStartApp = null;
	private TextView mTxtNoTime = null;
	private TextView mTxtCountdown = null;
	private Button mBtnSetTimer = null;
	private LinearLayout mLayoutCountDown = null;
	private LinearLayout mLayoutRecOpe = null;
	private LinearLayout mLayoutOptionWidgets = null;
	private int mSelectItem = 0;
	private int mSelectChoice = 0;
	private MyCount mCountdownCounter = null;
	private ToggleButton	mRecOpeBtn = null;
	private ImageButton		mRecTagBtn = null;
	// サービス
	// 
	private PurchaseInf						mLicense = new PurchaseInf();
//	private boolean							mSuportBiliing = false;
//	private ZzzTimerPurchaseObserver		mZzzTimerPurchaseObserver = null;
	private Handler 						mHandler = null;
//	private BillingService					mBillingService = null;

	
	//
	//	サービスからのタイムアップレシーバー
	//
	private class TimerServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
		}
	}

	public void countDownTimerCtrl(int mode) {

	}
	
	/**
	 * カレントのタイマーアイテムを、DBに更新
	 */
	private void updateCurrentItemToDB() {
		if( mCurItem == null ) {
			return ;
		}		
		try {
			new TimerSettingAccessor(mActivity).update(mCurItem);
		} catch(Exception e) {
			MyLog.loge(TAG, e);
			this.showAlert("Internal Error");
		}
		
	}
	
	// define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener mDateSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
	

            	if( mCurItem != null ) {
            		mCurItem.setHour(selectedDate.get(Calendar.HOUR_OF_DAY));
            		mCurItem.setMinite(selectedDate.get(Calendar.MINUTE));
            		
            		mTxtTime.setText( Formmater.getMiniteAfer(TimerFragment.this.mActivity, mCurItem) );
            		
            		TimerFragment.this.updateCurrentItemToDB();
            	}
            }
    };   
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreateView start"); }
		if (container == null) {
			return null;
		}
		View v = inflater.inflate(R.layout.timer_activity, container, false);
		LinearLayout lineLayout;
		mActivity = this.getActivity();
		mRootView = v;
		MyLog.logt(TAG, "mActivity:"+mActivity);
		
//		mHandler = new Handler();
//		mZzzTimerPurchaseObserver = new ZzzTimerPurchaseObserver(mHandler);
//		mBillingService = new BillingService();
//		mBillingService.setContext(mActivity);
		
		
		
		
		
		// カウントダウン用のレイアウト
		mLayoutCountDown = (LinearLayout)mRootView.findViewById(R.id.layoutCountdown);
		// スクリーン設定
		lineLayout = (LinearLayout)mRootView.findViewById(R.id.layoutWidgetItemScreen);
        mScreenOn = new OnOffBotton(mActivity, this.getString(R.string.screen), R.drawable.screen_on, R.drawable.screen_off, false);
        mScreenOn.setOnChangeListener(new four.non.bronds.yyys.zzztimer.widgets.OnOffBotton.OnChangeListener() {
			@Override
			public void onItemChage(boolean val) {
				// 
				if( mCurItem != null ) {
					mCurItem.setPoweriLock(val);
				}				
			}
		});
        lineLayout.addView(mScreenOn);
        
        // 録音オペレーションのレイアウト
        mLayoutRecOpe = (LinearLayout)mRootView.findViewById(R.id.layoutRecOpe);
        mRecOpeBtn = (ToggleButton)mRootView.findViewById(R.id.toggleRecOpe);
        mRecOpeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked ) {
					mRecOpeBtn.setBackgroundResource(R.drawable.rec_72);
					onPauseRec();
				} else {
					mRecOpeBtn.setBackgroundResource(R.drawable.pause_enable_72);
					onResumeRec();
				}
			}
        });
        mRecTagBtn = (ImageButton)mRootView.findViewById(R.id.imgBtnRecTag);
        mRecTagBtn.setOnClickListener(new CompoundButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				onRecTag();
			}
        });

        // Wifi設定
        lineLayout = (LinearLayout)mRootView.findViewById(R.id.layoutWidgetItemWifi);// 
        mWifiOn = new OnOffBotton(mActivity,  this.getString(R.string.wifi),  R.drawable.wifi_on, R.drawable.wifi_off, false);
        mWifiOn.setOnChangeListener(new four.non.bronds.yyys.zzztimer.widgets.OnOffBotton.OnChangeListener() {
			@Override
			public void onItemChage(boolean val) {
				// 
				if( mCurItem != null ) {
					mCurItem.setWifiLock(val);
				}
			}
		});
        lineLayout.addView(mWifiOn);
       
        

        // Audio設定
        lineLayout = (LinearLayout)mRootView.findViewById(R.id.layoutWidgetItemAudio); 
        mAudioOn = new OnOffBotton(mActivity,  this.getString(R.string.audio),  R.drawable.audio_on, R.drawable.audio_off, false);
        mAudioOn.setOnChangeListener(new four.non.bronds.yyys.zzztimer.widgets.OnOffBotton.OnChangeListener() {
			@Override
			public void onItemChage(boolean val) {
				// 
				if( mCurItem != null ) {
					mCurItem.setmAudio(val == true ? 1 : 0);
				}
			}
		});
        lineLayout.addView(mAudioOn);        
        
        // Recording
        lineLayout  = (LinearLayout)mRootView.findViewById(R.id.layoutWidgetItemRecord); 
        mRecordingOn = new OnOffBotton(mActivity,  this.getString(R.string.mic_recod),  R.drawable.rec_enable, R.drawable.rec_disable, false);
        mRecordingOn.setOnChangeListener(new four.non.bronds.yyys.zzztimer.widgets.OnOffBotton.OnChangeListener() {
			@Override
			public void onItemChage(boolean val) {
				// 
//				TimerFragment.this.onChangeRecordOption(val);
				if( mCurItem != null ) {
					mCurItem.setRecord(val);
				}
			}
		});
        lineLayout.addView(mRecordingOn);
        
        
        
        
        mLayoutOptionWidgets = (LinearLayout)mRootView.findViewById(R.id.layoutOptionWindgets); //
        //
        // カバーフローの初期化
        //
		mCoverFlow = (CoverFlow)mRootView.findViewById(R.id.coverflow);
		if( mCoverFlow == null ) {
			throw new NullPointerException("coverflow");
		}    		
		mCoverFlow.setOnItemClickListener(new CoverAdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(CoverAdapterView<?> parent, View view, int position, long id) {
				TimerFragment.this.onCoverItemClick(parent, view, position, id);
			}
		});
		mCoverFlow.setOnItemLongClickListener(new four.non.bronds.yyys.zzztimer.widgets.cover.CoverAdapterView.OnItemLongClickListener(){
			// Long-Click 時の処理
			@Override
			public boolean onItemLongClick(CoverFlow coverFlow, View view, int position, long id) {
				return TimerFragment.this.onCoverItemLongClick(coverFlow, view, position, id);
			}
		});
		mCoverFlow.setOnItemSelectedListener( new  four.non.bronds.yyys.zzztimer.widgets.cover.CoverAdapterView.OnItemSelectedListener() {
			// 選択したアイテムのポジションを記憶
			@Override
			public void onItemSelected(CoverAdapterView<?> parent, View view, int position, long id) {
				TimerFragment.this.onCoverItemSelected(parent, view, position, id);
			}
			@Override
			public void onNothingSelected(CoverAdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
        



		
		// タイトル
		mTxtTitle = (TextView)mRootView.findViewById(R.id.textTitle);
		// Time
		mTxtTime = (TextView)mRootView.findViewById(R.id.txtTimeDesc);
		mTxtTimeRunning = (TextView)mRootView.findViewById(R.id.txtTimeRunning);
		// Kill App
		mTxtKillApp = (TextView)mRootView.findViewById(R.id.txtKillAppDesc);
		// Start APP
		mTxtStartApp = (TextView)mRootView.findViewById(R.id.txtStartAppDesc);
		// txtNoItem
		mTxtNoTime = (TextView)mRootView.findViewById(R.id.txtNoItem);
		// textChronometer
		mTxtCountdown = (TextView)mRootView.findViewById(R.id.textChronometer);
		
		

		
		Button btn;
		btn = (Button)mRootView.findViewById(R.id.btnKillApp);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(TimerFragment.this.mActivity, SelAppActivity.class);
				intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mCurItem);
				intent.putExtra(Constant.INTENT_TAG_SEL_APP_MODE, (int)Constant.SEL_APP_MODE_KILL);
				startActivityForResult(intent, Constant.RQ_CODE_KILL_APP);
				
			}
		});
		
		btn = (Button)mRootView.findViewById(R.id.btnStartApp);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(TimerFragment.this.mActivity, SelAppActivity.class);
				intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mCurItem);
				intent.putExtra(Constant.INTENT_TAG_SEL_APP_MODE, (int)Constant.SEL_APP_MODE_STAT);
				startActivityForResult(intent, Constant.RQ_CODE_STARTL_APP);
				
			}
		});
		
		// Wake On LAN
		btn = (Button)mRootView.findViewById(R.id.btnWOL);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickBtnWOL(v);
			}
		});
		
		// Remote Operation
		btn = (Button)mRootView.findViewById(R.id.btnRemoteOpe);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickBtnRemoteOpe(v);
			}
		});
		// Play Music Setting
		btn = (Button)mRootView.findViewById(R.id.btnMusic);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickBtnPlayMusic(v);
			}
		});
		
		// btnSetTimer
		mBtnSetTimer = (Button)mRootView.findViewById(R.id.btnSetTimer);
		mBtnSetTimer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mCurItem.isRunning() ) {
					//　タイマーをキャンセル
					TimerFragment.this.cancelTimer(mCurItem);
				} else {
//					Intent intent = new Intent(TimerFragment.this, TimeInputActivity.class);
//					
//					intent.putExtra("HOUR", mCurItem.getHour());
//					intent.putExtra("MINITE", mCurItem.getMinite());
//					
//					
//	//				intent.putExtra("TEIMER_ITEM", mItem);
//					startActivityForResult(intent, 500);
					
					final Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, mCurItem.getHour());
					c.set(Calendar.MINUTE, mCurItem.getMinite());
					
					TimeSlider timeSlider = new TimeSlider(TimerFragment.this.mActivity, mDateSetListener, c);
					timeSlider.show();					
				}
			}
			
		});


        // Check if billing is supported.
//        ResponseHandler.register(mZzzTimerPurchaseObserver);

//        if (!mBillingService.checkBillingSupported()) {
//            //showDialog(DIALOG_CANNOT_CONNECT_ID);
//        }

		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreateView end"); }
		return v;
	}
    
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	// オプションメニュー
    	setHasOptionsMenu(true);
    	

    }
	
    // メニューの作成
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add(Menu.NONE, 0,Menu.NONE, this.getString(R.string.add));
        item.setIcon(R.drawable.add24);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM | MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
    }
    /**
     * Called when this activity becomes visible.
     */
    @Override
    public void onStart(){
    	if (MyLog.isDebugMod()) {MyLog.logf(TAG, "onStart start");}
		super.onStart();
//		ResponseHandler.register(mZzzTimerPurchaseObserver);
//		initializeOwnedItems();
		

        if (MyLog.isDebugMod()) {MyLog.logf(TAG, "onStart end");}
    }

    /**
     * Called when this activity is no longer visible.
     */
    @Override
    public void onStop(){
		super.onStop();
//		ResponseHandler.unregister(mZzzTimerPurchaseObserver);
    }
    
    
    @Override
    public void onDestroy(){
    	try {
    		if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onDestroy start"); }
    		super.onDestroy();
    		
//    		mBillingService.unbind();
    		
    	} finally {
    		if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"onDestroy end"); }
    	}
    }
    
    
    /**
     * 購入DBをリストア
     */
//    private void restoreDatabase() {
//        SharedPreferences prefs = TimerFragment.this.getActivity().getPreferences(Context.MODE_PRIVATE);
//        boolean initialized = prefs.getBoolean(Constant.SHARED_PREF_DB_INITIALIZED, false);
//        if (!initialized) {
//            mBillingService.restoreTransactions();
//            MyLog.tost(TimerFragment.this.getActivity(), "ZzzTimer Restoring transactions.", 1);
//        }
//    }
    
    private ZzzTimerActivity getMainAc() {
    	if(  !(this.getActivity() instanceof ZzzTimerActivity) ) {
			return null;
		}
    	return (ZzzTimerActivity)this.getActivity(); 
    }
    /**
     * OGGライセンス購入を実行
     */
    private void buyOGGLicence() {
    	if( MyLog.isDebugMod()) {MyLog.logf(TAG, "buyOGGLicence start");}
    	try {
    		ZzzTimerActivity ac = getMainAc();
    		if( ac == null) {
    			return ;
    		}
    		if( ac.getBillingService() == null) {
    			return ;
    		}
    		boolean b = ac.getBillingService().requestPurchase(Constant.PURCHASE_OGG, null, null);
    		if( b == false ) {
    			this.showAlert(this.getString(R.string.err_billing_not_supported_message));
    			return ;
    		}
    	} finally {
    		if( MyLog.isDebugMod()) {MyLog.logf(TAG, "buyOGGLicence end");}
    	}
    }
    
    // オプションメニューアイテムが選択された時に呼び出されます
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if( item.getItemId() == 0 ) {
    		this.doAddTimerItem();
    	}
    	return true;	
    }
	/**
	 * カバーフローのアイテムをクリック時の実装
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	public void onCoverItemClick(CoverAdapterView<?> parent, View view, int position, long id) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCoverItemClick start"); }
			// タイマー開始
			if( mCurItem == null ) {
				return ;
			}
			if(mCurItem.isRunning() ) {
				
				new AlertDialog.Builder(this.mActivity)
				.setMessage( this.getString(R.string.are_you_cancel_timer) ) 
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						TimerFragment.this.cancelTimer(mCurItem);
						dialog.cancel();
				      }
				  })
				  .setNegativeButton("No", new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int id) {
				           dialog.cancel();
				      }
				  })
				  .show();
			} else {
				TimerSettingBean timerItem = mItems.get(position);
				new TimerSettingAccessor(mActivity).update(timerItem);

				PrefereceAcc prefAcc = new PrefereceAcc(mActivity);
				int format = prefAcc.getRecFormat();
				mLicense = new PurchaseAccessor(mActivity).getLicenseInf();
				
				MyLog.logt(TAG, "    isRecord  :" + mCurItem.isRecord());
				MyLog.logt(TAG, "    format    :" + (format == Constant.REC_FORMAT_OGG));
				MyLog.logt(TAG, "    isOgg     :" + mLicense.isOgg());
				
				if( mCurItem.isRecord() && format == Constant.REC_FORMAT_OGG && mLicense.isOgg() == false) {
					// OGGのライセンスを購入していない
					
					FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
					Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("buy_ogg_dialog");
					if (prev != null) {
						ft.remove(prev);
					}
					ft.addToBackStack(null);
					
					BuyOggDialogFragment dlg = BuyOggDialogFragment.newInstance(mCurItem, this);
					dlg.show(ft, "buy_ogg_dialog");
					
				} else {
					this.startTimer( timerItem );
				}
			}
		} catch(Exception e) {
			MyLog.loge(TAG, e);
			this.showAlert(e.getMessage());
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCoverItemClick end"); }
		}
	}
	
	/**
	 * カバーフローのアイテムをロングクリック時の実装
	 * @param coverFlow
	 * @param view
	 * @param position
	 * @param id
	 * @return
	 */
	public boolean onCoverItemLongClick(CoverFlow coverFlow, View view, int position, long id) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCoverItemLongClick start"); }
		
	    	String []strSelectItem = new String[2] ;
	    	strSelectItem[0] = this.getString(R.string.edit);
	    	strSelectItem[1] = this.getString(R.string.delete);
	    	
	    	mSelectChoice = 0;
	    	
	    	final TimerSettingBean item = mItems.get(position);
	
	    	
	    	new AlertDialog.Builder(this.mActivity)
	    	.setIcon(R.drawable.icon)
	    	.setTitle(Constant.APP_NAME)
	    	.setSingleChoiceItems(strSelectItem, 0, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					mSelectChoice = which;
				}
	    	})
	    	.setPositiveButton(TimerFragment.this.getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
	
					if( mSelectChoice == 0 ) {
						Intent intent = new Intent(TimerFragment.this.mActivity, TimerItemActivity.class);
						intent.putExtra("TEIMER_ITEM", item);
						startActivityForResult(intent, 0);
					} else
					if( mSelectChoice == 1 ) {
	//					final Runtime runtime = Runtime.getRuntime();
						
						try {
	
							new TimerSettingAccessor(TimerFragment.this.mActivity).delete(item);
							
							TimerFragment.this.reView();
							
	//						Process superUserProcess = runtime.exec("su");
	//						Thread.sleep(3000);
	//						// 端末画面でsuを許可する必要があるので、  
	//			            // 3秒間待つ（その間にsuを許可する）  
	//			            // キチンと実装するのであれば別スレッドを立ち上げるなど  
	//			            // suが完了するまで待つような実装すべき  
	//						
	//						
	//						 DataOutputStream os = new DataOutputStream(superUserProcess.getOutputStream());  
	//			            os.writeBytes("kill 100" + "\n");  
	//			            os.flush();  
						} catch (Exception e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
	
					}
				}
	    	})
	    	.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
	    		
	    	})
			.show();

		} catch(Exception e) {
			MyLog.loge(TAG, e);
			this.showAlert(e.getMessage());
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCoverItemLongClick end"); }
		}
		return false;
	}

	/**
	 * カバーフローのアイテムが選択された
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	public void onCoverItemSelected(CoverAdapterView<?> parent, View view, int position, long id) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCoverItemSelected start"); }
			mSelectItem = position;
			
			mCurItem = mItems.get(position);
			
			ZzzTimerActivity  ac =  getMainAc();
	
			if(mCurItem.isRunning() ) {
				mTxtTime.setVisibility(View.GONE);
				mTxtTimeRunning.setVisibility(View.VISIBLE);
				mLayoutOptionWidgets.setVisibility(View.GONE);
				mLayoutCountDown.setVisibility(View.VISIBLE);
				if( mCurItem.isRecord() ) {
					mLayoutRecOpe.setVisibility(View.VISIBLE);
				
					if( ac != null && ac.getTimerService() != null) {
						if( MyLog.isDebugMod()) {
							MyLog.logi(TAG, "   Recording isPause : " + ac.getTimerService().isPauseRec(mCurItem.getID()) );
						}
						if( ac.getTimerService().isPauseRec(mCurItem.getID()) ) {
							mRecOpeBtn.setBackgroundResource(R.drawable.rec_72);
							mRecOpeBtn.setChecked(true);
						} else {
							mRecOpeBtn.setBackgroundResource(R.drawable.pause_enable_72);
							mRecOpeBtn.setChecked(false);
						}
					} 
					
				} else {
					mLayoutRecOpe.setVisibility(View.GONE);
				}
				mTxtTitle.setText(mCurItem.getName());
				
				
				mTxtTime.setText( Formmater.getMiniteAfer(this.mActivity, mCurItem) );
				
				mBtnSetTimer.setText( this.getString(R.string.cancel) );
				
				if( ac != null && ac.getTimerService() != null) {
					// カウントダウンを表示
					long l = ac.getTimerService().getLeftTime( mCurItem.getID() );
					if( mCountdownCounter != null ) {
						mCountdownCounter.cancel();
					}
					mCountdownCounter = new MyCount (l, 1000);
					mCountdownCounter.start();
				}
				
			} else {
				mTxtTime.setVisibility(View.VISIBLE);
				mTxtTimeRunning.setVisibility(View.GONE);
				mLayoutOptionWidgets.setVisibility(View.VISIBLE);
				mLayoutCountDown.setVisibility(View.GONE);
				mLayoutRecOpe.setVisibility(View.GONE);
	
				mTxtTitle.setText(mCurItem.getName());
				mTxtTime.setText( Formmater.getMiniteAfer(this.mActivity, mCurItem) );
				
				mBtnSetTimer.setText( this.getString(R.string.set) );
				
				
			}
			
			TimerFragment.this.updateWolInf();
			TimerFragment.this.updateRemoteOpeInf();
			TimerFragment.this.updatePlayMusicInf();
			mTxtKillApp.setText(Formmater.getKillApp(this.mActivity, mCurItem));
			mTxtStartApp.setText(Formmater.getStartApp(this.mActivity, mCurItem));
			
			mScreenOn.setVal( mCurItem.isPoweriLock() );
			mWifiOn.setVal( mCurItem.isWifiLock());
			mAudioOn.setVal( mCurItem.getmAudio() == 1 );
			mRecordingOn.setVal( mCurItem.isRecord() );

		} catch(Exception e) {
			MyLog.loge(TAG, e);
			this.showAlert(e.getMessage());
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCoverItemSelected end"); }
		}
	}
	
	/**
	 * WOLの設定ボタンがクリックされた
	 * @param v
	 */
	private void onClickBtnWOL(View v) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onClickBtnWOL start"); }
			// コンピューターが登録されているか？
			ComputerAccessor acc = new ComputerAccessor(mActivity);
			if( acc.load().size() == 0 ) {
				this.showAlert( mActivity.getString(R.string.msg_reg_not_computer) );
				return ;
			}
			
			
			
			
			FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
			Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("wol_dialog");
			if (prev != null) {
				ft.remove(prev);
			}
			ft.addToBackStack(null);
			
	
			WOLDialogFragment dlg = WOLDialogFragment.newInstance(mCurItem, this);
			dlg.show(ft, "wol_dialog");
			/*
			Intent intent = new Intent(TimerFragment.this.mActivity, WakeOnLanActivity.class);
			intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mCurItem);
			intent.putExtra(Constant.INTENT_TAG_SEL_APP_MODE, (int)Constant.SEL_APP_MODE_STAT);
			startActivityForResult(intent, Constant.RQ_CODE_WOL);
			*/
		} catch(Exception e) {
			MyLog.loge(TAG, e);
			this.showAlert(e.getMessage());
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onClickBtnWOL end"); }
		}
	}
	
	/**
	 * Remote Operationボタンがクリックされた
	 * @param v
	 */
	private void onClickBtnRemoteOpe(View v) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onClickBtnRemoteOpe start"); }
			// コンピューターが登録されているか？
			ComputerAccessor acc = new ComputerAccessor(mActivity);
			if( acc.load().size() == 0 ) {
				this.showAlert( mActivity.getString(R.string.msg_reg_not_computer) );
				return ;
			}		
			FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
			Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("remote_ope_dialog");
			if (prev != null) {
				ft.remove(prev);
			}
			ft.addToBackStack(null);
			
	
			RemoteOpeDialogFragment dlg = RemoteOpeDialogFragment.newInstance(mCurItem, this);
			dlg.show(ft, "remote_ope_dialog");
		} catch(Exception e) {
			MyLog.loge(TAG, e);
			this.showAlert(e.getMessage());
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onClickBtnRemoteOpe end"); }
		}
	}
	


	/**
	 * Play Musicボタンがクリックされた
	 * @param v
	 */
	private void onClickBtnPlayMusic(View v) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onClickBtnPlayMusic start"); }
			
			// SDカードを使用するケースは、SDカードの有無をチェックする
			if( StrageUtil.isSDcardExist(mActivity) == false ) {
				throw new Exception( this.getString(R.string.errMsgNotExistStrage) );
			}
			
			
			FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
			Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("play_music_dialog");
			if (prev != null) {
				ft.remove(prev);
			}
			ft.addToBackStack(null);
			
			PlayMusicSelDialogFragment dlg = PlayMusicSelDialogFragment.newInstance(mCurItem, this);
			dlg.show(ft, "play_music_dialog");
		} catch(Exception e ) {
			MyLog.loge(TAG, e);
			this.showAlert("" + e.getMessage());
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onClickBtnPlayMusic end"); }
		}
	}
	
	@Override
	public void onResume() {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onResume start"); }
			super.onResume();
			mReviewing = true;
			this.reView();
			mReviewing = false;	
		} catch(Exception e ) {
			MyLog.loge(TAG, e);
			this.showAlert("" + e.getMessage());
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onResume end"); }
		}		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 画面の再描画
	 */
	public void reView() {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "reView start"); }
			mReviewing = true;
			ZzzTimerActivity  ac =  getMainAc();
	    	int i = 0;
	        mItems = new TimerSettingAccessor(mActivity).load();
	        
	        if( ac != null && ac.getTimerService() != null) {
		        List<Integer> runningTimerIDs = ac.getTimerService().getRunningTimerID();
		        
		        for(TimerSettingBean item : mItems ) {
		        	for(Integer id : runningTimerIDs ) {
		        		if( item.getID() == id ) {
		        			ac.getTimerService().reRegNotify(item);
		        			item.setRunning(true);
		        			mSelectItem = i;
		        		}
		        	}
		        	i++;
		        }
	        }
	        
	        LinearLayout ll = (LinearLayout)mRootView.findViewById(R.id.layoutSetItemPane);
	        
	        if(mItems.size() == 0 ) {
	        	mTxtNoTime.setVisibility(View.VISIBLE);
	        	mCoverFlow.setVisibility(View.GONE);
	        	ll.setVisibility(View.GONE);
	        } else {
	        	mTxtNoTime.setVisibility(View.GONE);
	        	mCoverFlow.setVisibility(View.VISIBLE);
	        	ll.setVisibility(View.VISIBLE);
	        }
			mCoverFlowAdapter = new MyAdapter(this.mActivity, mItems);
			mCoverFlowAdapter.initRC();	// リソースの初期化
			mCoverFlow.setAdapter(mCoverFlowAdapter);
	
			mCoverFlow.setSpacing(-5);
			if( mItems.size() != 0 ) {
				if( mSelectItem >= mItems.size() ) {
					mSelectItem = 0;
				}			
				mCoverFlow.setSelection( mSelectItem );
			}
		} catch(Exception e) {
			MyLog.loge(getTag(), e);
		} catch(java.lang.OutOfMemoryError e ) {
			this.showAlert(this.getString(R.string.err_app_init));
			mActivity.finish();
		}
		finally {
			mReviewing = false;
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "reView end"); }
		}
	}
	
	/**
	 * 指定されたタイマーアイテムで、内部の配列情報を更新で、カレントデータの場合は、画面の表示を更新
	 * @param timerItem	タイマーアイテム
	 */
	public void updateTimerItem(TimerSettingBean timerItem) {
		if( MyLog.isDebugMod() ) { MyLog.logt(TAG, "updateTimerItem start"); }
		try {
			// DBの内容を更新
			new TimerSettingAccessor(mActivity).update(timerItem);
			
			this.reView();
			
		} catch(Exception e) {
			MyLog.loge(getTag(), e);
		} finally {
			mReviewing = false;
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updateTimerItem end"); }
		}
	}

    /**
     * タイマーアイテムを追加
     */
    private void doAddTimerItem() {
//		Intent intent = new Intent(TimerFragment.this.mActivity, TimerItemActivity.class);
//		startActivityForResult(intent, 0);
		FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
		Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("timer_item_input_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		
		TimerItemImputDialogFragment dlg = TimerItemImputDialogFragment.newInstance(this);
		dlg.show(ft, "timer_item_input_dialog");
    }
    
    /**
     * タイマーアイテムが更新された。
     * 
     * @param index
     */
    private void updateTimerItem(int index) {
    	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updateTimerItem start"); }
    	mSelectItem = mItems.size();
		this.reView();
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updateTimerItem end"); }
    }
    
	/**
	 * WOL の設定が変更された時の画面更新
	 */
	private void updateWolInf()
	{
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updateWolInf start"); }
		ImageView img = (ImageView)mRootView.findViewById(R.id.imgWOL);
		TextView txt = (TextView)mRootView.findViewById(R.id.txtWolDesc);
		
		txt.setText( Formmater.getWOL(this.mActivity,mCurItem )  );
		if( mCurItem.isEnableWOL() ) {
			img.setImageResource(R.drawable.wol);
		} else {
			img.setImageResource(R.drawable.wol_disable);
		}
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updateWolInf end"); }
	}
	
	/**
	 * Remote Operation の設定が変更された時の画面更新
	 */
	private void updateRemoteOpeInf()
	{
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updateRemoteOpeInf start"); }
		try {
			ImageView img = (ImageView)mRootView.findViewById(R.id.imgRemoteOpe);
			TextView txt = (TextView)mRootView.findViewById(R.id.txtRemoteOpeDesc);
			
			txt.setText( Formmater.getRemoteOpe(this.mActivity,mCurItem )  );
			if( mCurItem.isEnableRemoteOpe() ) {
				img.setImageResource(R.drawable.remote_on);
			} else {
				img.setImageResource(R.drawable.remote_off);
			}
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updateRemoteOpeInf end"); }
		}
	}

	/**
	 * 音楽再生情報を更新する
	 */
	private void updatePlayMusicInf() {

		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updatePlayMusicInf start"); }
		try {
			ImageView img = (ImageView)mRootView.findViewById(R.id.imgMusic);
			TextView txt = (TextView)mRootView.findViewById(R.id.txtMusicDesc);
			
			txt.setText( Formmater.getPlayMusic(this.mActivity,mCurItem )  );
			if( mCurItem.isMusic() ) {
				img.setImageResource(R.drawable.play_music_on);
				
				img = (ImageView)mRootView.findViewById(R.id.imgMusicShufful);
				if( mCurItem.isMusicShuffle()) {
					img.setVisibility(View.VISIBLE);
				} else {
					img.setVisibility(View.GONE);
				}
				
				img = (ImageView)mRootView.findViewById(R.id.imgMusicScrobbiling);
				if( mCurItem.isMusicScrib()) {
					img.setVisibility(View.VISIBLE);
				} else {
					img.setVisibility(View.GONE);
				}
			} else {
				img.setImageResource(R.drawable.play_music_off);
				img = (ImageView)mRootView.findViewById(R.id.imgMusicShufful);
				img.setVisibility(View.GONE);
				img = (ImageView)mRootView.findViewById(R.id.imgMusicScrobbiling);
				img.setVisibility(View.GONE);
			}
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "updatePlayMusicInf end"); }
		}
	
	}
    
    /**
     * タイマー開始
     * @param item
     */
    private void startTimer(TimerSettingBean item) {
    	try {
    		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "startTimer start"); }
	    	long hour = item.getHour();
			long min = item.getMinite();
			ZzzTimerActivity  ac =  getMainAc();
			
			// SDカードを使用するケースは、SDカードの有無をチェックする
			if( item.isMusic() ) {
				if( StrageUtil.isSDcardExist(mActivity) == false ) {
					throw new Exception( this.getString(R.string.errMsgNotExistStrage) );
				}
			}
			if( MyLog.isDebugMod() ) { 
				MyLog.logf(TAG, "  ac:" + ac); 
				if( ac != null ) {
					MyLog.logf(TAG, "  getTimerService:" + ac.getTimerService());
				}
			}
			if( ac == null || ac.getTimerService() == null) {
				throw new Exception("Not ready for service");
			}
			ac.getTimerService().schedule(item, (hour * 60 + min) * 60 * 1000);
			mActivity.moveTaskToBack(true);
			
			
			// アプリを開始する
			for(TimerSettingStartApp ite : item.getListStartApp() ) {
				
				
		        Intent intentQuery = new Intent(Intent.ACTION_MAIN,null);
		        intentQuery.addCategory(Intent.CATEGORY_LAUNCHER); 			
				PackageManager pm = mActivity.getPackageManager();
	//			PackageInfo activityInfo = null;
				
				try {
					List<ResolveInfo> apps = pm.queryIntentActivities(intentQuery, 0);
					
	
					
					for (int i=0;i<apps.size();i++) {
						ResolveInfo info=apps.get(i);
						ActivityInfo activity = info.activityInfo;
						if( ite.getmAppName().compareToIgnoreCase( activity.packageName ) == 0 ) {
	//						Log.i(TAG,"name:"+activity.name);
	//						Log.i(TAG,"   packageName:"+activity.packageName);
	//						Log.i(TAG,"  permission:"+activity.permission);
	//						Log.i(TAG,"  processName:"+activity.processName);
	//						Log.i(TAG,"  targetActivity:"+activity.targetActivity);
	//						Log.i(TAG,"  flags:"+activity.flags);
	//						Log.i(TAG,"  launchMode:"+activity.launchMode);
							Intent intent = new Intent(Intent.ACTION_MAIN); 
							intent.setClassName(ite.getmAppName(), activity.name);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							this.startActivity(intent); 
						}
					}
				} catch (Exception/*NameNotFoundException*/ e) {
					MyLog.loge(TAG, e);
					MyLog.tost(mActivity, e.getMessage(), 1);
				}
			}
    	} catch(Exception e) {
    		MyLog.loge(TAG, e);
    		this.showAlert(""+ e.getMessage());
    	} finally {
    		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "startTimer end"); }
    	}
    }
    
    /**
     * タイマーキャンセル
     * @param item
     */
    private void cancelTimer(TimerSettingBean item) {
    	try {
    		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "cancelTimer start"); }
    		ZzzTimerActivity  ac =  getMainAc();
    		if( ac != null && ac.getTimerService() != null) {
    			ac.getTimerService().cancelTimer(item.getID());
				item.setRunning(false);
				
				mCountdownCounter.cancel();
				mLayoutCountDown.setVisibility(View.GONE);
				mLayoutRecOpe.setVisibility(View.GONE);
				mTxtTimeRunning.setVisibility(View.GONE);
				
				mTxtTime.setVisibility(View.VISIBLE);
				mLayoutOptionWidgets.setVisibility(View.VISIBLE);
		
				mScreenOn.setVal( item.isPoweriLock() );
				mWifiOn.setVal( item.isWifiLock());
				mAudioOn.setVal( item.getmAudio() == 1 );
				mRecordingOn.setVal( item.isRecord() );
				
				mTxtTitle.setText(item.getName());
				
				mTxtTime.setText( Formmater.getMiniteAfer(this.mActivity, item) );
				
				mBtnSetTimer.setText( this.getString(R.string.set) );
    		}
		} catch(Exception e) {
			MyLog.loge(TAG, e);
			this.showAlert(""+ e.getMessage());
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "cancelTimer end"); }
		}
    }
	
    /**
     * 録音一時停止
     * @throws Exception 
     */
    private void onPauseRec() {
    	try {
    		if( MyLog.isDebugMod() ){ MyLog.logf(TAG, "onPauseRec start");}
	    	if( mReviewing == true ) {
	    		return ;
	    	}
	    	if( mCurItem == null ) {
	    		return ;
	    	}
	    	ZzzTimerActivity  ac =  getMainAc();
	    	if( ac == null || ac.getTimerService() == null) {
	    		this.showAlert("Service is not start.");
	    		return ;
	    	}
	    	ac.getTimerService().pauseRec( mCurItem.getID() );
    	} finally {
    		if( MyLog.isDebugMod() ){ MyLog.logf(TAG, "onPauseRec end");}
    	}
    	
    }
    
    /**
     * 録音再開
     * @throws Exception 
     */
    private void onResumeRec() {
    	try {
    		if( MyLog.isDebugMod() ){ MyLog.logf(TAG, "onPauseRec start");}
	    	if( mReviewing == true ) {
	    		return ;
	    	}
	    	if( mCurItem == null ) {
	    		return ;
	    	}
	    	ZzzTimerActivity  ac =  getMainAc();
	    	if( ac == null || ac.getTimerService() == null) {
	    		this.showAlert("Service is not start.");
	    		return ;
	    	}
	    	ac.getTimerService().resumeRec( mCurItem.getID() );
    	} finally {
    		if( MyLog.isDebugMod() ){ MyLog.logf(TAG, "onPauseRec end");}
    	}
    }
	
    /**
     * 録音タグの設定
     */
    private void onRecTag() {
    	try {
    		if( MyLog.isDebugMod() ){ MyLog.logf(TAG, "onRecTag start");}
	    	if( mReviewing == true ) {
	    		return ;
	    	}
	    	if( mCurItem == null ) {
	    		return ;
	    	}
	    	ZzzTimerActivity  ac =  getMainAc();
	    	if( ac == null || ac.getTimerService() == null) {
	    		this.showAlert("Service is not start.");
	    		return ;
	    	}
			FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
			Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("rec_tag_dialog");
			if (prev != null) {
				ft.remove(prev);
			}
			ft.addToBackStack(null);
			
			RecTagDialogFragment dlg = RecTagDialogFragment.newInstance(mCurItem, this, ac.getTimerService());
			dlg.show(ft, "rec_tag_dialog");
    	} finally {
    		if( MyLog.isDebugMod() ){ MyLog.logf(TAG, "onRecTag end");}
    	}
    }
	
	//
	//	タイムアップ非同期タスク
	//
	class MyAsyncTask extends AsyncTask<String, Integer, Integer> {
	
    	
    	private	Context	mContext = null;
    	private int		mItemID = -1;
		
		public MyAsyncTask(Context ctx, int itemID) {
			mContext = ctx;
			mItemID = itemID;
		}
		
		@Override
    	protected void onPreExecute() {
			
		}
		@Override
		protected Integer doInBackground(String... arg0) {
			try {
				// Sleep
				Thread.sleep(1*1000);
				
				
				TimerSettingBean item = new TimerSettingBean();
				item.setID(mItemID);
				new TimerSettingAccessor(mContext).loadDetail(item);
				
				
				if( item.getmAudio() == 0 ) {
			        AudioManager _audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			        //_audioManager.setStreamMute(AudioManager.MODE_NORMAL, true);
			        //_audioManager.setStreamMute(AudioManager.MODE_RINGTONE, true);
			        _audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
				}
				
				// プロセスをキル
				ActivityManager activityManager = (ActivityManager)mContext.getSystemService(Activity.ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> runningApp = activityManager.getRunningAppProcesses();
				List<ActivityManager.RunningServiceInfo> serviese = activityManager.getRunningServices(100);
				


				
				
				if( serviese != null ) {
					for(ActivityManager.RunningServiceInfo service : serviese) {

						try {
							
							
							if( item.isExistKillApp(service.service.getPackageName())) {
								Intent ii = new Intent(mContext, service.service.getClass());
								mContext.stopService(ii);
								
								killProc(activityManager, service.pid, service.service.getPackageName());
							}
						} catch( Exception e) {
		                	Log.e(TAG, e.getMessage());
		                	e.printStackTrace();
						}
					
						try {
							if( item.isExistKillApp( service.service.getPackageName() )) {
								Context foreignContext = mContext.createPackageContext(
										service.service.getPackageName(), 
										Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
								Intent ii = new Intent(foreignContext, service.service.getClass());		
								
								foreignContext.stopService( ii );
							
							}

						} catch( Exception e) {
		                	e.printStackTrace();
						}
				}
				}
				
				
				
//				PackageManager packageManager = getPackageManager();
				if(runningApp != null) {


					
					for(RunningAppProcessInfo app : runningApp) {
						
						
			
						                        
						String strProcName = app.processName;
						int pos = strProcName.indexOf(":");
						if( pos != -1 ) {
							strProcName = strProcName.substring(0, pos);
//							Log.d(TAG, "APP : " + app.processName + "   Remote : " +  strProcName);
						} else {
//							Log.d(TAG, "APP : " + app.processName);
						}
											
						
						if( item.isExistKillApp(strProcName)) {
							killProc(activityManager, app.pid, strProcName);		                
						}
						
					}
				}
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Integer retval) {
			
			if( mContext instanceof Activity ) {
				Activity activity = (Activity)mContext;
				activity.finish();
			}
		}

		private void killProc(ActivityManager activityManager, int pid, String strPackageName)
		{
			// プロセスをKill
			android.os.Process.killProcess(pid);
			// プロセスをKill
			activityManager.restartPackage(  strPackageName );

			if( Build.VERSION.SDK_INT >= 8) {
				killTaskFroyo(strPackageName);
			}

			try {
				Class<?> partypes[] = new Class[1];
	            partypes[0] = String.class;
				Method killBackgroundProcesses;
				killBackgroundProcesses = ActivityManager.class.getMethod("killBackgroundProcesses", partypes);

				Log.d(TAG, " killBackgroundProcesses : " +  strPackageName);
				Object arglist[] = new Object[1];
				arglist[0] = strPackageName;  
				killBackgroundProcesses.invoke(activityManager, arglist);
			} catch( Exception e) {
	        	Log.e(TAG, e.getMessage());
	        	e.printStackTrace();
			}
			
	        
//	        try {
//		        String [] theArgs = new String [3];
//	            
//	            theArgs[0] = "su";
//	            theArgs[2] = "kill";
//	            theArgs[3] = "" + pid;		                
	//
//	            Log.d(TAG, " kill command : " +  pid);
//	        	Process proc =Runtime.getRuntime().exec(theArgs);
////	            InputStream is = proc.getInputStream();
////	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
////	            String line;
////	            while ((line = br.readLine()) != null) {
////	            	Log.d(TAG, line);
////	            }
//	        	proc.wait();
//	        } catch (Exception e) {
//	        	Log.e(TAG, e.getMessage());
//	        	e.printStackTrace();
//	        }
		}

	    public void killTaskFroyo(String pkgName) {
	        try {
	        		Log.d(TAG, " killTaskFroyo - " + pkgName);
	        		
	        		
	                Class<?> c = Class.forName("android.app.ActivityManagerNative");
	                Method getDefaultMethod = c.getMethod("getDefault");
	                getDefaultMethod.setAccessible(true);
	                Object nativeManager = getDefaultMethod.invoke(null);
	                c = nativeManager.getClass();
	                Method forceStopPackageMethod = c.getMethod("forceStopPackage",
	                                String.class);
	                forceStopPackageMethod.setAccessible(true);
	                forceStopPackageMethod.invoke(nativeManager, pkgName);
	        } catch (Exception e) {
	                ;
	        }
	}
	}
	
	
	/**
	 * タイマー名入力ダイアログ
	 */
	public static class TimerItemImputDialogFragment extends DialogFragment {
		private TimerFragment		mParentFragment;
		private EditText			mEditItemName;
		
		static TimerItemImputDialogFragment newInstance(TimerFragment parentFragment) {
			TimerItemImputDialogFragment f = new TimerItemImputDialogFragment();
			f.mParentFragment = parentFragment;
			Bundle args = new Bundle();
			//args.putSerializable("item", setting);
			f.setArguments(args);
			return f;
		}
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }

        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	
        	LayoutInflater inflater = getActivity().getLayoutInflater();  
        	View v = inflater.inflate(R.layout.dialog_enter_item_name, null, false);
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.title_add_timer);
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						onOkBtnCliced(dlg);
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.icon);
        	builder.setView(v);  
        	
        	mEditItemName = (EditText)v.findViewById(R.id.editItemName);
        	
        	return builder.create();
        }
        
        /**
         * OKボタンをクリックしたときの処理
         */
        private void onOkBtnCliced(DialogInterface dlg) {
        	String strName  = mEditItemName.getEditableText().toString().trim();
        	
        	if( strName.length() == 0 ) {
        		dlg.cancel();
        		mParentFragment.showAlert(this.getString(R.string.not_spcify_name));
        		return ;
        	}
        	
        	TimerSettingBean t = new TimerSettingBean();
        	t.setName(strName);
        	
        	try {
        		new TimerSettingAccessor(mParentFragment.mActivity).add(t);
        	} catch(Exception e) {
        		dlg.cancel();
        		MyLog.loge(TAG, e);
        		mParentFragment.showAlert("Internal error.");
        		return ;
        	}
        	
        	
        	dlg.dismiss();
        	
        	mParentFragment.updateTimerItem(-1);
        }

	}

	/**
	 * 音楽を再生するアイテムを選択
	 */
	public static class PlayMusicSelDialogFragment extends DialogFragment {
		private TimerSettingBean	mSetting;
		private TimerFragment		mParentFragment;
		private Spinner				mSpinnerMusic;
		private List<HashMap<String, String>> mData;
		private CheckBox			mChkEnablePlayMusic;
		private CheckBox			mChkShuffle;
		private CheckBox			mChkScrobbiling;
		
		static PlayMusicSelDialogFragment newInstance(TimerSettingBean setting, TimerFragment parentFragment) {
			PlayMusicSelDialogFragment f = new PlayMusicSelDialogFragment();
			f.mParentFragment = parentFragment;
			Bundle args = new Bundle();
			args.putSerializable("item", setting);
			f.setArguments(args);			
			return f;
		}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }
        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	mSetting = (TimerSettingBean)getArguments().getSerializable("item");

        	LayoutInflater inflater = getActivity().getLayoutInflater(); 

        	View v = inflater.inflate(R.layout.dialog_select_play_music, null, false);
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.play_music_select);
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						onOkBtnCliced();
						dlg.dismiss();
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						// TODO Auto-generated method stub
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.play_music_on);
        	builder.setView(v);  
        	
        	mData = new ArrayList<HashMap<String, String>>();
        	HashMap<String, String> m = new HashMap<String, String>();
        	m.put(MediaStore.Audio.Playlists._ID, "-1");
        	m.put(MediaStore.Audio.Playlists.NAME, "<<Edit Play List>>");
        	mData.add( m );
        	// プレイリストを取得
			// Playlistや、ArtistをIN-Strageから検索するか？
        	String strWhere = new PrefereceAcc(mParentFragment.mActivity).getMusicDBSearchLoc();
        	new MusicUtils(mParentFragment.mActivity).getPlaylist(mData, strWhere);
        	
        	mSpinnerMusic = (Spinner)v.findViewById(R.id.spinnerSelectMusic);
        	ListAdapter adapter = new ListAdapter(inflater, this.getActivity(), mData);
        	mSpinnerMusic.setAdapter(adapter);
        	adapter.setDropDownViewResource(R.layout.spinner_list_music_row);
        	mSpinnerMusic.setPrompt("Select Playlist");
        	int index = 0;
        	int i = 0;
        	for(HashMap<String, String> data : mData ) {
        		int id = Integer.parseInt( data.get(MediaStore.Audio.Playlists._ID) );
        		if( id == mSetting.getMusicID() ) {
        			index = i;
        			break;
        		}
        		i++;
        	}
        	mSpinnerMusic.setSelection(index);
        	
        	mChkEnablePlayMusic = (CheckBox)v.findViewById(R.id.checkEnablePlayMusic);
        	mChkEnablePlayMusic.setChecked( mSetting.isMusic() );
        	mChkShuffle = (CheckBox)v.findViewById(R.id.checkShuffle);
        	mChkShuffle.setChecked( mSetting.isMusicShuffle() );
        	mChkScrobbiling = (CheckBox)v.findViewById(R.id.checkScrobbiling);
        	mChkScrobbiling.setChecked( mSetting.isMusicScrib() );
        	mChkScrobbiling.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener (){
				@Override
				public void onCheckedChanged(CompoundButton btn, boolean arg1) {
					if( isLastFmInstalled() == false ) {
				    	Uri market_uri = Uri.parse( "market://details?id=fm.last.android");
						Intent intent = new Intent(Intent.ACTION_VIEW, market_uri);
						mParentFragment.mActivity.startActivityForResult(intent, 0);
						
						btn.setChecked(false);
					}					
				}
        	} );
        	
        	return builder.create();
        }
        private boolean isLastFmInstalled() {
        	PackageManager pm = mParentFragment.mActivity.getPackageManager();
        	boolean result = false;
        	try {
        		pm.getPackageInfo("fm.last.android", PackageManager.GET_ACTIVITIES);
        		result = true;
        	} catch (Exception e) {
        		result = false;
        	}
        	return result;
        }
        /**
         * OKボタンをクリックしたときの処理
         */
        private void onOkBtnCliced() {
        	HashMap<String, String> data = mData.get( mSpinnerMusic.getSelectedItemPosition() );
        	int id = Integer.parseInt( data.get(MediaStore.Audio.Playlists._ID) );
        	
        	if( id == -1 ) {
        		Intent intent = new Intent(mParentFragment.mActivity, MusicPlayListEditActivity.class); 
        		startActivityForResult(intent, Constant.RQ_CODE_MUSIC_PLAY_LIST_EDIT);
        	} else {
        		
        		mSetting.setMusic( mChkEnablePlayMusic.isChecked() );
        		mSetting.setMusicID(id);
        		mSetting.setMusicShuffle( mChkShuffle.isChecked() );
        		mSetting.setMusicScrib( mChkScrobbiling.isChecked() );
        		mSetting.setMusicName( data.get( MediaStore.Audio.Playlists.NAME ) );
        		
        		new TimerSettingAccessor(this.getActivity()).update(mSetting);
        		
        		mParentFragment.updatePlayMusicInf();
        	}
        }
        private class ListAdapter extends ArrayAdapter<HashMap<String, String>>
        {
    		private LayoutInflater 	mInflater;
    		
    		
        	public ListAdapter(LayoutInflater inflater, Context context, List<HashMap<String, String>> objects) {
        		super(context, R.layout.spinner_list_music_row, R.id.txtName, objects);
        		mInflater = inflater;
        	}
    		@Override
    		public View getDropDownView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.spinner_list_music_row, null, false);
    			}
    			this.setWiggetContents(view, this.getItem(position));
    			return view;
    		}
    		@Override
    		public View getView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.spinner_list_music_row, null, false);
    			}
    			this.setWiggetContents(view, this.getItem(position));
    			return view;
    		}
    		private void setWiggetContents(View view, HashMap<String, String> data) {
    			TextView  txtView = null;
    			txtView = (TextView)view.findViewById(R.id.txtName);
    			txtView.setText( data.get( MediaStore.Audio.Playlists.NAME ) );
    		}
        }
	}
	
	
	/**
	 * WOLのコンピューターを設定するダイアログ
	 */
	public static class RemoteOpeDialogFragment extends DialogFragment {
		private TimerSettingBean	mSetting;
		private CheckBox			mChkEnableRemoteOpe;
		private Spinner				mSpinnerComputer;
		private Spinner				mSpinnerRemoteOpe;
		private List<Computer>		mComputers;
		private TimerFragment		mParentFragment;


		
		static RemoteOpeDialogFragment newInstance(TimerSettingBean setting, TimerFragment parentFragment) {
			RemoteOpeDialogFragment f = new RemoteOpeDialogFragment();
			f.mParentFragment = parentFragment;
			Bundle args = new Bundle();
			args.putSerializable("item", setting);
			f.setArguments(args);
			return f;
		}
		
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }
        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	mSetting = (TimerSettingBean)getArguments().getSerializable("item");
        	  	
        	
        	LayoutInflater inflater = getActivity().getLayoutInflater();  
        	View v = inflater.inflate(R.layout.dialog_remote_ope_setting, null, false);
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.remote_ope);
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						onOkBtnCliced();
						dlg.dismiss();
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						// TODO Auto-generated method stub
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.remote_on);
        	builder.setView(v);  
        	
        	mChkEnableRemoteOpe = (CheckBox)v.findViewById(R.id.checkEnableRemoteOpe);
        	mSpinnerComputer = (Spinner)v.findViewById(R.id.spinnerRemoteOpeComputer);
        	mSpinnerRemoteOpe = (Spinner)v.findViewById(R.id.spinnerRemoteOperation);
        	
        	mChkEnableRemoteOpe.setChecked( mSetting.getRemoteOpeComputerID() != -1 );
        	ComputerAccessor acc = new ComputerAccessor(this.getActivity());
        	mComputers = acc.load();
        	int indexRemoteOpeComputer = 0;
        	int i = 0;
        	if( mSetting.getWolComputerID() != -1 ) {
        		for(Computer comp : mComputers ) {
        			if( comp.getId() == mSetting.getWolComputerID() ) {
        				indexRemoteOpeComputer = i;
        			}
        		}
        		i++;
        	}
        	mSpinnerComputer.setSelection( indexRemoteOpeComputer );
        	ListAdapter adapter = new ListAdapter(inflater, this.getActivity(), mComputers);
        	mSpinnerComputer.setAdapter(adapter);
        	adapter.setDropDownViewResource(R.layout.computer_list_row);
        	mSpinnerComputer.setPrompt("Select Computer");
        	
        	mSpinnerRemoteOpe.setSelection( mSetting.getRemoteOpe() );
        	
        	return builder.create();
        }
        
        /**
         * OKボタンをクリックしたときの処理
         */
        private void onOkBtnCliced() {
        	if( mChkEnableRemoteOpe.isChecked() ) {
        		Computer comp = mComputers.get(mSpinnerComputer.getSelectedItemPosition());
        		mSetting.setRemoteOpeComputerID( comp.getId() );
        		mSetting.setRemoteOpeComputer(comp);
        	} else {
        		mSetting.setRemoteOpeComputerID(-1);
        		mSetting.setRemoteOpeComputer(null);
        	}
        	mSetting.setRemoteOpe( mSpinnerRemoteOpe.getSelectedItemPosition() );
        	
        	
        	new TimerSettingAccessor(this.getActivity()).update(mSetting);
        	
        	mParentFragment.updateRemoteOpeInf();
        }

    	/**
    	 * コンピューターリストアダプター
    	 */
    	private class ListAdapter extends ArrayAdapter<Computer> {
    		private LayoutInflater 	mInflater;
    		
    		public ListAdapter(LayoutInflater inflater, Context context, List<Computer> objects) {
    			super(context, R.layout.computer_spinner_row, R.id.txtName, objects);
        		mInflater = inflater;
    		}
    		
    		@Override
    		public View getDropDownView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.computer_spinner_row, null, false);
    			}
    			Computer comp = this.getItem(position);
    			this.setWiggetContents(view, comp);    			
    			return view;
    		}
    		
    		@Override
    		public View getView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.computer_spinner_row, null, false);
    			}
    			Computer comp = this.getItem(position);
    			this.setWiggetContents(view, comp);
    			return view;
    		}
    		
    		private void setWiggetContents(View view, Computer comp) {
    			TextView  txtView = null;
    			txtView = (TextView)view.findViewById(R.id.txtName);
    			txtView.setText( comp.getName() );
    			
    			txtView = (TextView)view.findViewById(R.id.textOtherInfo);
    			txtView.setVisibility(View.GONE);    			
    		}
    	}

	}
	

	/**
	 * WOLのコンピューターを設定するダイアログ
	 */
	public static class WOLDialogFragment extends DialogFragment {
		private TimerSettingBean	mSetting;
		private CheckBox			mChkWnableWOL;
		private Spinner				mSpinnerComputer;
		private Spinner				mSpinnerRepeat;
		private List<Computer>		mComputers;
		private TimerFragment		mParentFragment;
		/*
			<item>first one</item>
			<item>10 sec</item>
			<item>30 sec</item>
			<item>1 min</item>
			<item>2 min</item>
			<item>5 min</item>
			<item>10 min</item>
			<item>30 min</item>
			<item>1 hour</item>
		 */
		private static int repeat_val[] = {0,10,30,60,120,300, 600,1800,3600};
		
		static WOLDialogFragment newInstance(TimerSettingBean setting, TimerFragment parentFragment) {
			WOLDialogFragment f = new WOLDialogFragment();
			f.mParentFragment = parentFragment;
			Bundle args = new Bundle();
			args.putSerializable("item", setting);
			f.setArguments(args);
			return f;
		}
		
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }
        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	mSetting = (TimerSettingBean)getArguments().getSerializable("item");
        	  	
        	
        	LayoutInflater inflater = getActivity().getLayoutInflater();  
        	View v = inflater.inflate(R.layout.dialog_wol_setting, null, false);
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.wake_on_lan);
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						onOkBtnCliced();
						dlg.dismiss();
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						// TODO Auto-generated method stub
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.wol);
        	builder.setView(v);  
        	
        	mChkWnableWOL = (CheckBox)v.findViewById(R.id.checkEnablrWOL);
        	mSpinnerComputer = (Spinner)v.findViewById(R.id.spinnerWOLComputer);
        	mSpinnerRepeat = (Spinner)v.findViewById(R.id.spinnerWOLRepeat);
        	
        	mChkWnableWOL.setChecked( mSetting.getWolComputerID() != -1 );
        	ComputerAccessor acc = new ComputerAccessor(this.getActivity());
        	mComputers = acc.load();
        	int indexWOLComputer = 0;
        	int i = 0;
        	if( mSetting.getWolComputerID() != -1 ) {
        		for(Computer comp : mComputers ) {
        			if( comp.getId() == mSetting.getWolComputerID() ) {
        				indexWOLComputer = i;
        			}
        		}
        		i++;
        	}
        	mSpinnerComputer.setSelection( indexWOLComputer );
        	ListAdapter adapter = new ListAdapter(inflater, this.getActivity(), mComputers);
        	mSpinnerComputer.setAdapter(adapter);
        	adapter.setDropDownViewResource(R.layout.computer_list_row);
        	mSpinnerComputer.setPrompt("Select Computer");
        	
        	int indexWOLRepeat = 0;
            int iVal = mSetting.getWolRepeat();
            i = 0;
            for(int dispval : repeat_val ) {
            	if( dispval == iVal ) {
            		indexWOLRepeat=i;
            	}
            	i++;
            }
        	mSpinnerRepeat.setSelection( indexWOLRepeat );
        	
        	return builder.create();
        }
        
        /**
         * OKボタンをクリックしたときの処理
         */
        private void onOkBtnCliced() {
        	if( mChkWnableWOL.isChecked() ) {
        		Computer comp = mComputers.get(mSpinnerComputer.getSelectedItemPosition());
        		mSetting.setWolComputerID( comp.getId() );
        		mSetting.setWOLComputer(comp);
        	} else {
        		mSetting.setWolComputerID(-1);
        		mSetting.setWOLComputer(null);
        	}
        	mSetting.setWolRepeat(repeat_val[mSpinnerRepeat.getSelectedItemPosition()]);
        	
        	
        	new TimerSettingAccessor(this.getActivity()).update(mSetting);
        	
        	mParentFragment.updateWolInf();
        }

    	/**
    	 * コンピューターリストアダプター
    	 */
    	private class ListAdapter extends ArrayAdapter<Computer> {
    		private LayoutInflater 	mInflater;
    		
    		public ListAdapter(LayoutInflater inflater, Context context, List<Computer> objects) {
    			super(context, R.layout.computer_spinner_row, R.id.txtName, objects);
        		mInflater = inflater;
    		}
    		
    		@Override
    		public View getDropDownView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.computer_spinner_row, null, false);
    			}
    			Computer comp = this.getItem(position);
    			this.setWiggetContents(view, comp);    			
    			return view;
    		}
    		
    		@Override
    		public View getView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.computer_spinner_row, null, false);
    			}
    			Computer comp = this.getItem(position);
    			this.setWiggetContents(view, comp);
    			return view;
    		}
    		
    		private void setWiggetContents(View view, Computer comp) {
    			TextView  txtView = null;
    			txtView = (TextView)view.findViewById(R.id.txtName);
    			txtView.setText( comp.getName() );
    			
    			txtView = (TextView)view.findViewById(R.id.textOtherInfo);
    			txtView.setText("MAC Address:" + comp.getMac_addr() + " Port:" + comp.getPort_wol());
    		}
    	}

	}
	
	/**
	 * OGGライセンス購入ダイアログ
	 */
	public static class BuyOggDialogFragment extends DialogFragment {
		private TimerSettingBean	mSetting;
		private TimerFragment		mParentFragment;
		static BuyOggDialogFragment newInstance(TimerSettingBean setting, TimerFragment parentFragment) {
			BuyOggDialogFragment f = new BuyOggDialogFragment();

			Bundle args = new Bundle();
			args.putSerializable("item", setting);
			f.setArguments(args);
			f.mParentFragment = parentFragment;
			return f;
		}
        @Override
		public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
		}
        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	mSetting = (TimerSettingBean)getArguments().getSerializable("item");
        	LayoutInflater inflater = getActivity().getLayoutInflater();  
        	View v = inflater.inflate(R.layout.dialog_purchase_ogg, null, false);
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.title_purchase_ogg);
        	builder.setPositiveButton("Buy", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						mParentFragment.buyOGGLicence();
						dlg.dismiss();
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						mParentFragment.startTimer(mSetting);
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.wol);
        	builder.setView(v);  
        	return builder.create();
        }
	}
	
	/**
	 * Record Tag追加ダイアログ
	 */
	public static class RecTagDialogFragment extends DialogFragment {
		private TimerSettingBean	mSetting;
		private TimerFragment		mParentFragment;
		private TimerService 		mTimerService;
		
		private EditText			mEditTagVal;
		private ListView			mListView;
		
		static RecTagDialogFragment newInstance(TimerSettingBean setting, TimerFragment parentFragment, TimerService timerService) {
			RecTagDialogFragment f = new RecTagDialogFragment();

			Bundle args = new Bundle();
			args.putSerializable("item", setting);
			f.setArguments(args);
			f.mParentFragment = parentFragment;
			f.mTimerService = timerService;
			return f;
		}
        @Override
		public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
		}
        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	mSetting = (TimerSettingBean)getArguments().getSerializable("item");
        	LayoutInflater inflater = getActivity().getLayoutInflater();  
        	View v = inflater.inflate(R.layout.dialog_rec_tag, null, false);
        	
        	mEditTagVal = (EditText)v.findViewById(R.id.editTextRecTag);
        	mListView = (ListView)v.findViewById(R.id.listRecTag);

        	ListAdapter adapter = new ListAdapter(inflater, this.getActivity(), mSetting.getmRecTags());
        	mListView.setAdapter(adapter);
        	
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.title_record_tag);
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						onOkBtnClicked();
						dlg.dismiss();
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {

						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.rec_tag_48);
        	builder.setView(v);  
        	return builder.create();
        }
        
        private void onOkBtnClicked() {
        	try {
        		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onOkBtnClicked start");}
	        	String strVal = mEditTagVal.getText().toString();
	        	if( strVal.length() == 0 ) {
	        		MyLog.tost(getActivity(), "no specified TAG.", 1);
	        		return ;
	        	}
	        	long time = mTimerService.getRecordedTime(mSetting.getID());
	        	RecTagBean recTag = new RecTagBean(strVal, time);
	        	mSetting.getmRecTags().add(recTag);
	
	        	// サービスのアイテムに設定
	        	mTimerService.setRecTag(mSetting.getID(), mSetting.getmRecTags() );
        	} catch(Exception e) {
        		MyLog.loge(TAG, e);
        		mParentFragment.showAlert(e.getMessage());
        	} finally {
        		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onOkBtnClicked end");}
        	}
        }
        
        private class ListAdapter extends ArrayAdapter<RecTagBean> {
    		private LayoutInflater 	mInflater;
    		
    		public ListAdapter(LayoutInflater inflater, Context context, List<RecTagBean> objects) {
    			super(context, R.layout.rec_tag_row, R.id.txtName, objects);
        		mInflater = inflater;
    		}
    		@Override
    		public View getView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.rec_tag_row, null, false);
    			}
    			RecTagBean comp = this.getItem(position);
    			this.setWiggetContents(view, comp);
    			return view;
    		}
    		
    		private void setWiggetContents(View view, RecTagBean recTag) {
    			TextView  txtView = null;
    			txtView = (TextView)view.findViewById(R.id.textViewRecTagName);
    			txtView.setText( recTag.getTag() );
    			
    			txtView = (TextView)view.findViewById(R.id.textViewRecTagTime);
    			txtView.setText( Formmater.formatTime( recTag.getTime() ) );
    		}
        	
        }
        
        
	}
	
	//
	//	カバーフローアダプター
	//
	class MyAdapter extends ArrayAdapter<TimerSettingBean> {

    	private Bitmap originalImage;
    	private int		org_width;
    	private int		org_height;
 //   	private List<Bitmap>	mReflectionImages = new ArrayList<Bitmap>();
    	private List<Bitmap>	mBitmapWithReflections = new ArrayList<Bitmap>();
    	final static int MAX_ARRAY = 4;
    	
    	
		public MyAdapter(Context context, List<TimerSettingBean> objects) {
			super(context, 0, objects);
			// TODO 自動生成されたコンストラクター・スタブ
		}
		
		public void initRC() throws Exception {
			if(MyLog.isDebugMod()) { MyLog.logt(TAG, "initRC start"); }
			try {
				createImage(R.drawable.image0001);
				createImage(R.drawable.image0002);
				createImage(R.drawable.image0003);
				createImage(R.drawable.image0004);
			} catch(Exception e) {
				MyLog.loge(TAG, e);
				throw e;
			} finally {
				if(MyLog.isDebugMod()) { MyLog.logt(TAG, "initRC end"); }
			}
		}
		
		private void createImage(int rc) {
			originalImage = BitmapFactory.decodeResource(getResources(), rc);
			

     		final int reflectionGap = 4;

    		org_width = originalImage.getWidth();
    		org_height = originalImage.getHeight();
    		
     		Matrix matrix = new Matrix();
            matrix.preScale(1, -1);
            
            
        	Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, org_height/2, org_width, org_height/2, matrix, false);
     		
            Bitmap bitmapWithReflection = Bitmap.createBitmap(org_width 
                    , (org_height + org_height/2), Config.ARGB_8888);
            
            Canvas canvas = new Canvas(bitmapWithReflection);
            
            
            canvas.drawBitmap(originalImage, 0, 0, null);
            
            Paint deafaultPaint = new Paint();
            canvas.drawRect(0, org_height, org_width, org_height + reflectionGap, deafaultPaint);
            //Draw in the reflection
            canvas.drawBitmap(reflectionImage,0, org_height + reflectionGap, null);
            //Draw in the reflection
            
            //Create a shader that is a linear gradient that covers the reflection
            Paint paint = new Paint(); 
            
            paint.setAntiAlias(true);
            paint.setTextSize(48);
            paint.setColor(0xFF001111);
            canvas.drawText("", 170, org_height/2, paint);
            
            LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, 
              bitmapWithReflection.getHeight() + reflectionGap, 
              0x70ffffff, 0x00ffffff, 
              //0x70ffff00, 0x00ff00ff,
              TileMode.CLAMP); 
            
            //Set the paint to use this shader (linear gradient)
            paint.setShader(shader); 
            //Set the Transfer mode to be porter duff and destination in
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); 
            //Draw a rectangle using the paint with our linear gradient
            canvas.drawRect(0, org_height, org_width, 
              bitmapWithReflection.getHeight() + reflectionGap, paint); 
            canvas.drawBitmap(reflectionImage,0, org_height + reflectionGap, paint);
            

            
//            mReflectionImages.add(reflectionImage);
            mBitmapWithReflections.add(bitmapWithReflection);
		}

     	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
     		ImageView imageView = null;
            if( convertView == null ) {
	            imageView = new ImageView(this.getContext());
	            imageView.setLayoutParams(new CoverFlow.LayoutParams(360, 240));
	            imageView.setScaleType(ScaleType.MATRIX);
            
	            convertView = imageView;
            } else {
            	imageView = (ImageView)convertView;
            }
            
            
            TimerSettingBean item = (TimerSettingBean)this.getItem(position);
         

            int img_pos = item.getID() % MAX_ARRAY;

            Bitmap bitmapWithReflection = mBitmapWithReflections.get( img_pos );
            
            imageView.setImageBitmap(bitmapWithReflection);
            
            return imageView;
     	}
	}
	
	

	
	// カウントダウンタイマー
	class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mTxtCountdown.setText("done!");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mTxtCountdown.setText(Formmater.formatTime(millisUntilFinished));
		}

	}
	
	
	
	
	
	
	
	
	

    /**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
//    private class ZzzTimerPurchaseObserver extends PurchaseObserver {
//        public ZzzTimerPurchaseObserver(Handler handler) {
//            super(TimerFragment.this.getActivity(), handler);
//        }
//
//		@Override
//		public void onBillingSupported(boolean supported, String type) {
//			if( MyLog.isDebugMod()){
//				MyLog.logf(TAG, "::onBillingSupported start");
//				MyLog.logf(TAG, "    supported      : " + supported);
//				MyLog.logf(TAG, "    type           : " + type);
//			}
//			mSuportBiliing = supported;
//			if( mSuportBiliing ) {
////				restoreDatabase();
//			}
//			if( MyLog.isDebugMod()){MyLog.logf(TAG, "::onBillingSupported end");}
//		}
//		@Override
//		public void onPurchaseStateChange(PurchaseState purchaseState,
//				String itemId, int quantity, long purchaseTime,
//				String developerPayload) {
//			if( MyLog.isDebugMod()){
//				MyLog.logf(TAG, "::onPurchaseStateChange start");
//				MyLog.logf(TAG, "    purchaseState      : " + purchaseState);
//				MyLog.logf(TAG, "    itemId             : " + itemId);
//				MyLog.logf(TAG, "    quantity           : " + quantity);
//				MyLog.logf(TAG, "    purchaseTime       : " + purchaseTime);
//				MyLog.logf(TAG, "    developerPayload   : " + developerPayload);
//			}
//			
//			if (purchaseState == PurchaseState.PURCHASED) {
//				
//			}
//
//			
//			
//			if( MyLog.isDebugMod()){MyLog.logf(TAG, "::onPurchaseStateChange end");}
//		}
//
//		@Override
//		public void onRequestPurchaseResponse(RequestPurchase request,
//				ResponseCode responseCode) {
//			if( MyLog.isDebugMod()){
//				MyLog.logf(TAG, "::onRequestPurchaseResponse start");
//				MyLog.logf(TAG, "    ProductId    : " + request.mProductId);
//				MyLog.logf(TAG, "    responseCode : " + responseCode);
//			}
//			if (responseCode == ResponseCode.RESULT_OK) {
//				
//			} else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
//				
//			} else {
//				
//			}
//			if( MyLog.isDebugMod()){MyLog.logf(TAG, "::onRequestPurchaseResponse end");}
//		}
//
//		@Override
//		public void onRestoreTransactionsResponse(RestoreTransactions request,
//				ResponseCode responseCode) {
//			if( MyLog.isDebugMod()){
//				MyLog.logf(TAG, "::onRestoreTransactionsResponse start");
//				MyLog.logf(TAG, "    request      : " + request);
//				MyLog.logf(TAG, "    responseCode : " + responseCode);
//			}
//			if (responseCode == ResponseCode.RESULT_OK) {
//				SharedPreferences prefs = TimerFragment.this.getActivity().getPreferences(Context.MODE_PRIVATE);
//				SharedPreferences.Editor edit = prefs.edit();
//				edit.putBoolean(Constant.SHARED_PREF_DB_INITIALIZED, true);
//				edit.commit();
//			} else {
//				
//			}
//			if( MyLog.isDebugMod()){MyLog.logf(TAG, "::onRestoreTransactionsResponse end");}
//		}
//    }
}