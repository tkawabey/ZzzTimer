package four.non.bronds.yyys.zzztimer.activity;

import java.util.Calendar;



import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingCloseAppBean;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.TimerSettingAccessor;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.Market;
import four.non.bronds.yyys.zzztimer.widgets.OnOffBotton;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.DateSlider;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.TimeSlider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TimerItemActivity  extends Activity {
	final static String TAG = "TimerItemActivity";
	private	boolean				mAddMode = false;
	private TimerSettingBean 	mItem = null; 
	private OnOffBotton			mScreenOn=null;
	private OnOffBotton			mWifiOn=null;
	private OnOffBotton			mAudioOn=null;
	private OnOffBotton			mRecordingOn=null;
	private TextView			mTimeDesc = null;
	private TextView			mTextVAppKillDesc = null; // txtKillAppDesc
	private TextView			mTextVAppStartDesc = null; // txtKillAppDesc
	
	
	
	
	// define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener mDateSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	// update the dateText view with the corresponding date
            	
            	mItem.setHour(selectedDate.get(Calendar.HOUR_OF_DAY));
            	mItem.setMinite(selectedDate.get(Calendar.MINUTE));
            	
            	
            	mTimeDesc.setText( Formmater.getMiniteAfer(TimerItemActivity.this, mItem) );
            }
    };   
    
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_item);
        
        Intent intent = this.getIntent();
        mItem = (TimerSettingBean)intent.getSerializableExtra("TEIMER_ITEM");
        if( mItem == null ) {
        	mItem = new TimerSettingBean();
        	mAddMode = true;
        } else {
        	// 詳細をロード
        	new TimerSettingAccessor(this).loadDetail( mItem );
        	

        	TextView txtV = (TextView)this.findViewById(R.id.textTitle);
        	txtV.setText( mItem.getName() );
        	
        	EditText edt = (EditText)this.findViewById(R.id.editTextItemName);
        	edt.setVisibility(View.GONE);
        }
        
        mTimeDesc = (TextView)this.findViewById(R.id.txtTimeDesc);
        mTimeDesc.setText( Formmater.getMiniteAfer(this, mItem) );
        
        mTextVAppKillDesc =  (TextView)this.findViewById(R.id.txtKillAppDesc);
        mTextVAppKillDesc.setText( Formmater.getKillApp(TimerItemActivity.this, mItem) );
        
        mTextVAppStartDesc =  (TextView)this.findViewById(R.id.txtStartAppDesc);
        mTextVAppStartDesc.setText( Formmater.getStartApp(TimerItemActivity.this, mItem) );

        
        LinearLayout ll = (LinearLayout)this.findViewById(R.id.layoutWidgetItemScreen);
        mScreenOn = new OnOffBotton(this, this.getString(R.string.screen), R.drawable.screen_on, R.drawable.screen_off, mItem.isPoweriLock());
        ll.addView(mScreenOn);
        
        
        ll = (LinearLayout)this.findViewById(R.id.layoutWidgetItemWifi);// 
        mWifiOn = new OnOffBotton(this, this.getString(R.string.wifi),  R.drawable.wifi_on, R.drawable.wifi_off, mItem.isWifiLock());
        ll.addView(mWifiOn);

        ll = (LinearLayout)this.findViewById(R.id.layoutWidgetItemAudio);// 
        mAudioOn = new OnOffBotton(this, this.getString(R.string.audio),  R.drawable.audio_on, R.drawable.audio_off, mItem.getmAudio() == 1);
        ll.addView(mAudioOn);

        ll = (LinearLayout)this.findViewById(R.id.layoutWidgetItemRecord);// 
        mRecordingOn = new OnOffBotton(this,  this.getString(R.string.mic_recod),  R.drawable.rec_enable, R.drawable.rec_disable, false);
        mRecordingOn.setOnChangeListener(new four.non.bronds.yyys.zzztimer.widgets.OnOffBotton.OnChangeListener() {
			@Override
			public void onItemChage(boolean val) {
				// 
				TimerItemActivity.this.onChangeRecordOption(val);
			}
		});
        ll.addView(mRecordingOn);
        
        
        
        
        

		Button button = (Button)findViewById(R.id.btnApply);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TimerItemActivity.this.onApply();
			}
			
		});
		
		
		
		// Id	@+id/btnSetTimer
		button = (Button)findViewById(R.id.btnSetTimer);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, mItem.getHour());
				c.set(Calendar.MINUTE, mItem.getMinite());
				
				TimeSlider timeSlider = new TimeSlider(TimerItemActivity.this, mDateSetListener, c);
				timeSlider.show();
				

//				Intent intent = new Intent(TimerItemActivity.this, TimeInputActivity.class);
//				
//				intent.putExtra("HOUR", mItem.getHour());
//				intent.putExtra("MINITE", mItem.getMinite());
//				
//				
////				intent.putExtra("TEIMER_ITEM", mItem);
//				startActivityForResult(intent, 500);
			}
			
		});
		
		
		
		// btnKillApp
		button = (Button)findViewById(R.id.btnKillApp);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimerItemActivity.this, SelAppActivity.class);
				intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mItem);
				intent.putExtra(Constant.INTENT_TAG_SEL_APP_MODE, (int)Constant.SEL_APP_MODE_KILL);
				startActivityForResult(intent, Constant.RQ_CODE_KILL_APP);
			}
			
		});
		// btnStartApp
		button = (Button)findViewById(R.id.btnStartApp);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimerItemActivity.this, SelAppActivity.class);
				intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mItem);
				intent.putExtra(Constant.INTENT_TAG_SEL_APP_MODE, (int)Constant.SEL_APP_MODE_STAT);
				startActivityForResult(intent, Constant.RQ_CODE_STARTL_APP);
			}
			
		});	
		// btnWOL
		button = (Button)findViewById(R.id.btnWOL);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimerItemActivity.this, WakeOnLanActivity.class);
				intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mItem);
				intent.putExtra(Constant.INTENT_TAG_SEL_APP_MODE, (int)Constant.SEL_APP_MODE_STAT);
				startActivityForResult(intent, Constant.RQ_CODE_WOL);
			}
			
		});
		
		updateWolInf();
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null ) {
			TimerSettingBean item = (TimerSettingBean)data.getSerializableExtra("TEIMER_ITEM");
			if( item != null ) {
				mItem = item;
		        mTextVAppKillDesc.setText( Formmater.getKillApp(this, mItem) );
		        mTextVAppStartDesc.setText( Formmater.getStartApp(TimerItemActivity.this, mItem) );
		        updateWolInf();
			}
		}
	}
	private void updateWolInf()
	{
		ImageView img = (ImageView)this.findViewById(R.id.imgWOL);
		TextView txt = (TextView)this.findViewById(R.id.txtWolDesc);
		
		txt.setText( Formmater.getWOL(this,mItem )  );
		if( mItem.isEnableWOL() ) {
			img.setImageResource(R.drawable.wol);
		} else {
			img.setImageResource(R.drawable.wol_disable);
		}
	}
    
    void onApply() {
    	try {
	    	
	    	if( mAddMode == true ) {
	    		// 追加モードの場合は、名前の入力チェック
		    	String strName = null;
	    		EditText edt = (EditText)this.findViewById(R.id.editTextItemName);
	    		strName = edt.getText().toString();
	    		if( strName.length() == 0 ) {	    			
	    			new AlertDialog.Builder(this)
		    			.setIcon(R.drawable.icon)
		    			.setTitle(Constant.APP_NAME)
		    			.setMessage(this.getString(R.string.not_spcify_name))
		    			.setPositiveButton(this.getString(R.string.ok), null)
		    			.show();
	    			return ;
	    		}

				
	    	    mItem.setName(strName);
	    	}
	    	

    		mItem.setPoweriLock( mScreenOn.isVal() );
    		mItem.setWifiLock( mWifiOn.isVal() );
    		mItem.setmAudio(mAudioOn.isVal() ? 1 : 0);
    		mItem.setRecord(mRecordingOn.isVal());
    		
	    	if( mAddMode == true ) {
	    		new TimerSettingAccessor(this).add(mItem);
	    		
	    	} else {
	    		new TimerSettingAccessor(this).update(mItem);
	    	}
    	
    	} catch( Exception e) {
    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon)
			.setTitle(Constant.APP_NAME)
			.setMessage(strErr)
			.setPositiveButton(this.getString(R.string.ok), null)
			.show();
			return ;
    	}    	
    	this.finish();
    }
    
	private void onChangeRecordOption(boolean val) {
		
		if( val == true ) {
			if( Market.checkInstallPackage(this, Constant.PACKAGE_PIPOPA_ROID) != Market.INSTALLED_STS.INSTALLED_STS_EXIST ) {
				
				mRecordingOn.setVal(false);
				
				new AlertDialog.Builder(this)
				.setIcon(R.drawable.icon)
				.setTitle("Confirm")
				.setMessage(this.getString(R.string.non_install_pipopadroid))
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	Market.intentLaunchMarketFallback(TimerItemActivity.this, Constant.PACKAGE_PIPOPA_ROID, 9000);
				    }
				})
				.show();			
				return ;
			}
		}

	}
}
