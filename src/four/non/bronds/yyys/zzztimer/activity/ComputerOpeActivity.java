package four.non.bronds.yyys.zzztimer.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;//.FrameLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.Computer;
import four.non.bronds.yyys.zzztimer.client.ZzzSvcClient;
import four.non.bronds.yyys.zzztimer.client.ZzzSvcClient.COMMAND;
import four.non.bronds.yyys.zzztimer.dialog.HelpDialog;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.PrefereceAcc;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.DateSlider;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.TimeSlider;

public class ComputerOpeActivity  extends MyBaseActivity {
	private static final String TAG = "ComputerOpeActivity";
	private ComputerOpeFragment mFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// フラグメントインスタンスを作成
		mFragment = new StaticComputerOpeFragment();
		getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, mFragment).commit();
		
		
		Intent intent = this.getIntent();
		Computer comp = (Computer)intent.getSerializableExtra("COMPUTER");
		// コンピュータデータをフラグメントに設定
		mFragment.setItem(comp);
	}

	public static class StaticComputerOpeFragment extends ComputerOpeFragment {
	}	
}

/**
 * タイマーフラグメント
 */
class ComputerOpeFragment extends MyBaseFragment  implements OnSeekBarChangeListener{
	final static String TAG = "ComputerOpeFragment";
	private Computer	mComputer;
	private int		 	mRestartAfterTimeHour = 0;
	private int		 	mRestartAfterTimeMinite = 0;
	private int			mShutdownAfterTimeHour = 0;
	private int			mShutdownAfterTimeMinite = 0;
	private Button		mBtnRestartAfterTime = null;
	private Button		mBtnShutdownAfterTime = null;
	private ImageButton		mBtnVoiceInput= null;
//	private ToggleButton	mResume;
	private LinearLayout	mLayoutExec = null;
	private LinearLayout	mLayoutSuspend = null;
	private LinearLayout	mLayoutShutdown = null;
	private SeekBar			mSeekBarOnOff = null;
	
	
	private static final int REQUEST_CODE_VOICEINPUT = 10;
	
	
	
	
	// define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener mDateSetListenerRestartAfterTime =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
           		mRestartAfterTimeHour = selectedDate.get(Calendar.HOUR_OF_DAY);
           		mRestartAfterTimeMinite = selectedDate.get(Calendar.MINUTE);
           		mBtnRestartAfterTime.setText( String.format("%02d:%02d", mRestartAfterTimeHour, mRestartAfterTimeMinite));
              }
    };   
	// define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener mDateSetListenerShutdowAfterTime =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	mShutdownAfterTimeHour = selectedDate.get(Calendar.HOUR_OF_DAY);
            	mShutdownAfterTimeMinite = selectedDate.get(Calendar.MINUTE);
            	mBtnShutdownAfterTime.setText( String.format("%02d:%02d", mShutdownAfterTimeHour, mShutdownAfterTimeMinite));
              }
    };   
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {
			if (container == null) {
				return null;
			}
			
			mActivity = this.getActivity();
			mRootView = inflater.inflate(R.layout.computer_ope, container, false);
			
			mLayoutExec = (LinearLayout)mRootView.findViewById(R.id.layoutExec);
			mLayoutSuspend = (LinearLayout)mRootView.findViewById(R.id.layoutSuspendOpt);
			mLayoutShutdown = (LinearLayout)mRootView.findViewById(R.id.layOutShutdownOpt);
/*			
			mResume = (ToggleButton)mRootView.findViewById(R.id.toggleBtnAfterExec);
			mResume.setTextOn("");
			mResume.setTextOff("");
			mResume.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					mResume.setBackgroundResource(isChecked ? R.drawable.toggle_on : R.drawable.toggle_off);
				}
			});
			mResume.setChecked(false);
			mResume.setBackgroundResource( R.drawable.toggle_off);
*/			
			int position = 0;
			mLayoutExec.setVisibility(View.GONE);
			mLayoutSuspend.setVisibility(View.GONE);
			mLayoutShutdown.setVisibility(View.GONE);
			switch(position) {
			case 0:
			case 1:
				mLayoutShutdown.setVisibility(View.VISIBLE);
				break;
			case 2:
			case 3:
				mLayoutExec.setVisibility(View.VISIBLE);
				mLayoutSuspend.setVisibility(View.VISIBLE);
				break;
			case 7:
				mLayoutExec.setVisibility(View.VISIBLE);
				break;
			}

			Button btn = (Button)mRootView.findViewById(R.id.btnExecute);
			btn.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				ComputerOpeFragment.this.onBtnExecuteClick(v);
    			}
    		});
			
			mBtnRestartAfterTime = (Button)mRootView.findViewById(R.id.btnRestartAfterTime);
			mBtnRestartAfterTime.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				final Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, mRestartAfterTimeHour);
					c.set(Calendar.MINUTE, mRestartAfterTimeMinite);
    				TimeSlider timeSlider = new TimeSlider(ComputerOpeFragment.this.mActivity,
    						mDateSetListenerRestartAfterTime, c);
					timeSlider.show();
    			}
    		});
			
			mBtnShutdownAfterTime = (Button)mRootView.findViewById(R.id.btnShutdownAfterTime);
			mBtnShutdownAfterTime.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				final Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, mShutdownAfterTimeHour);
					c.set(Calendar.MINUTE, mShutdownAfterTimeMinite);
    				TimeSlider timeSlider = new TimeSlider(ComputerOpeFragment.this.mActivity, 
    						mDateSetListenerShutdowAfterTime, c);
					timeSlider.show();
    			}
    		});

			Spinner spinner = (Spinner)mRootView.findViewById(R.id.spinnerRemoteOpe);
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent,
						View view, int position, long id) {
//					Spinner spinner = (Spinner) parent;
					//String item = (String) spinner.getSelectedItem();
					
			    	/*
					0 : Shutdown
					1 : Reboot
					2 : Stand by
					3 : Hibernate
					4 : Log off
					5 : Lock Computer
					6 : Screen OFF
					7 : Test Remote Execute
			    	 */					
					mLayoutExec.setVisibility(View.GONE);
					mLayoutSuspend.setVisibility(View.GONE);
					mLayoutShutdown.setVisibility(View.GONE);
					switch(position) {
					case 0:
					case 1:
						mLayoutShutdown.setVisibility(View.VISIBLE);
						break;
					case 2:
					case 3:
						mLayoutExec.setVisibility(View.VISIBLE);
						mLayoutSuspend.setVisibility(View.VISIBLE);
						break;
					case 7:
						mLayoutExec.setVisibility(View.VISIBLE);
						break;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			mSeekBarOnOff = (SeekBar)mRootView.findViewById(R.id.seekBarOnOff);
			mSeekBarOnOff.setMax(10);
			mSeekBarOnOff.setProgress(10);
			Bitmap bMap = BitmapFactory.decodeResource(mRootView.getResources(), R.drawable.bgseekbaronoff);
			mSeekBarOnOff.setLayoutParams(new LayoutParams(bMap.getWidth(),bMap.getHeight()));
			mSeekBarOnOff.setProgressDrawable(getResources().getDrawable(R.drawable.bgseekbaronoff));
			mSeekBarOnOff.setThumb(getResources().getDrawable(R.drawable.buttonseekbar));
			mSeekBarOnOff.setThumbOffset(-6);
			mSeekBarOnOff.setOnSeekBarChangeListener(this);
			mSeekBarOnOff.setTag("0");

			mBtnVoiceInput = (ImageButton)mRootView.findViewById(R.id.imgBtnVoiceInput);
			mBtnVoiceInput.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				doVoiceInput();
    			}
    		});
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView end");}
		}		
		return mRootView;
	}
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	// 画面作成が完了したら、コンピューターのデータを画面に表示
    	this.setItem(mComputer);
    	// オプションメニュー
    	setHasOptionsMenu(true);
    }
    // メニューの作成
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item;
        //SubMenu sub = menu.addSubMenu("Opts");
        item = menu.add(1, 100, 1, this.getActivity().getString(R.string.help));
        item.setIcon( android.R.drawable.ic_menu_help);
        MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM );
    }
    
    // オプションメニューアイテムが選択された時に呼び出されます
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if( item.getItemId() == 100 ) {
    		PrefereceAcc acc = new PrefereceAcc(this.getActivity());
    		String strURL = acc.getHelpURL(PrefereceAcc.HELP_URL.HELP_URL_COMPUTER_OPE);
    		HelpDialog.show(this.getActivity(), strURL);
    	}
    	return true;	
    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onActivityResult start"); }
		if( requestCode== REQUEST_CODE_VOICEINPUT) {
			
			int index = -1;
			int j;
			String cmds[] = this.getResources().getStringArray(R.array.computer_remote_operations);
			String resultsString = "";
			if( data != null ) {
	            ArrayList<String> results = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	            for (int i = 0; i< results.size(); i++) {
	            	String strSel = results.get(i);
	            	strSel = strSel.replace(" ", "");
	            	resultsString += "\n";
	            	resultsString += strSel;
	            	MyLog.logt(TAG, "[" + strSel + "]");
	            	j = 0;
	            	for(String cmd : cmds) {
	            		if( cmd.compareToIgnoreCase(strSel) == 0 ) {
	            			index = j;
	            			break;
	            		}
	            		j++;
	            	}
	                if( index == -1 ) {
	                	if( "Shutdown".compareToIgnoreCase(strSel) == 0 ) {
	                		index = 0;
	                	} else
	                	if( "Reboot".compareToIgnoreCase(strSel) == 0 ) {
	                		index = 1;
	                	} else
	                	if( "Standby".compareToIgnoreCase(strSel) == 0 ) {
	                		index = 2;
	                	} else
	                	if( "Hibernate".compareToIgnoreCase(strSel) == 0 ) {
	                		index = 3;
	                	} else
	                	if( "Logoff".compareToIgnoreCase(strSel) == 0 ) {
	                		index = 4;
	                	} else
	                	if( "LockComputer".compareToIgnoreCase(strSel) == 0 ) {
	                		index = 5;
	                	} else
	                	if( "ScreenOFF".compareToIgnoreCase(strSel) == 0 ) {
	                		index = 6;
	                	} else
	                	if( "TestRemoteExecute".compareToIgnoreCase(strSel) == 0 ||
	                		"RemoteExecute".compareToIgnoreCase(strSel) == 0 ||
	                		"コマンド実行".compareToIgnoreCase(strSel) == 0) {
	                		index = 7;
	                	}
	                }
	                if( index != -1 ) {
	                	break;
	                }
	            }
			}
            
            if( index != -1 ) {
            	MyLog.tost(mActivity, cmds[index], 1);
            	
            	ZzzSvcClient.COMMAND command;
            	command = ZzzSvcClient.getCommandIndex(index);
            	
            	executeCommand( command );
            	
            } else {
            	MyLog.tost(mActivity, this.getString(R.string.err_reconsnize_voice_input_command) + resultsString, 1);
            }
                   

	    	
		}
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onActivityResult end"); }
	}
	
	
    /**
     * リモートコマンドを実行
     * @param v
     */
    public void onBtnExecuteClick(View v) {
		try {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onBtnExecuteClick start");}
			EditText edt;
			Spinner spinner = (Spinner)mRootView.findViewById(R.id.spinnerRemoteOpe);
	    	ZzzSvcClient.COMMAND command;
	    	int pos = spinner.getSelectedItemPosition();
	    	/*
			<item>Shutdown
			<item>Reboot
			<item>Stand by
			<item>Hibernate
			<item>Log off
			<item>Lock Computer
			<item>Screen OFF
			<item>Test Remote Execute
	    	 */
	    	command = ZzzSvcClient.getCommandIndex(pos);
	    	
	    	executeCommand( command );
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onBtnExecuteClick end");}
		}
    }

	
	/**
	 * コマンドを実行します。
	 * @param command
	 */
	private void executeCommand(ZzzSvcClient.COMMAND command) {
		try {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "executeCommand start");}
			EditText edt;
	    	
	    	// Exe image 
			edt = (EditText)mRootView.findViewById(R.id.edtExeImage);
			String strExeImage = edt.getEditableText().toString().trim();
	    	// Exe image 
			edt = (EditText)mRootView.findViewById(R.id.edtExeParam);
			String strExeParam = edt.getEditableText().toString().trim();
	    	// Exe Current Directory
			edt = (EditText)mRootView.findViewById(R.id.edtExeCurDir);
			String strExeCurDir = edt.getEditableText().toString().trim();
			// Message
			edt = (EditText)mRootView.findViewById(R.id.edtAlertMessage);
			String strMessage = edt.getEditableText().toString().trim();


			int restartAfterTime = mRestartAfterTimeHour * 60 + mRestartAfterTimeMinite;
			int shutdownAfterTime = (mShutdownAfterTimeHour * 60 + mShutdownAfterTimeMinite)*60;
			if(MyLog.isDebugMod()) {
				MyLog.logt(TAG, "Host Name  : [" + mComputer.getHostname() + "]");
				MyLog.logt(TAG, "Port Number: " + mComputer.getZzz_tcp_port());
				MyLog.logt(TAG, "Command    : " + ZzzSvcClient.COMMAND.CMD_HIBERNATE.toString());
			}
	    	boolean bResume = mSeekBarOnOff.getProgress() == 10;
	    	

	    	if( command == ZzzSvcClient.COMMAND.CMD_HIBERNATE ||
    			command == ZzzSvcClient.COMMAND.CMD_STAND_BY) {
	    		if( bResume == true && restartAfterTime == 0 ) {
	    			
	    			this.showAlert(this.getString(R.string.err_remote_ope_restart_time));
	    			return ;
	    		}
	    	}
	    	if( command == ZzzSvcClient.COMMAND.CMD_REMOTE_EXEC ) {
	    		if( strExeImage.length() == 0 ) {
	    			this.showAlert(this.getString(R.string.err_remote_ope_remote_command));
	    			return ;
	    		}    			
	    	}
	    	
	    	AsyncRequester async = new AsyncRequester(this.mActivity,
	    			mComputer.getHostname(), mComputer.getZzz_tcp_port(), command,
	    			shutdownAfterTime, strMessage,
	    			bResume ? 1 : 0,
	    			restartAfterTime,
	    			strExeImage,
	    			strExeParam
	    			,strExeCurDir);
	    	async.execute("");
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "executeCommand end");}
		}
	}
    /**
     * 音声入力
     */
    private void doVoiceInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); 
		
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak  Command...");
        startActivityForResult(intent, REQUEST_CODE_VOICEINPUT);
    }
    
    /**
     * 指定されたコンピューターの情報で、画面を更新
     * @param comp
     */
	public void setItem(Computer comp) {
		try {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "setItem start");}	
			
			EditText edt;
			TextView txtV;
			
			mComputer = comp;
			if( mComputer == null ) {
				return;
			}
			if( mRootView == null ) {
				return ;
			}

    		int restartAfterTime = mComputer.getResume_opt();
    		mRestartAfterTimeHour = restartAfterTime / 60;
    		mRestartAfterTimeMinite = restartAfterTime % 60;
    		
    		int shutdownAfterTime = mComputer.getShutdown_after_tm() / 60;
    		mShutdownAfterTimeHour = shutdownAfterTime / 60;
    		mShutdownAfterTimeMinite = shutdownAfterTime % 60;

    		mBtnRestartAfterTime.setText( String.format("%02d:%02d", mRestartAfterTimeHour, mRestartAfterTimeMinite));
    		mBtnShutdownAfterTime.setText( String.format("%02d:%02d", mShutdownAfterTimeHour, mShutdownAfterTimeMinite));
    		
			
			
			txtV = (TextView)mRootView.findViewById(R.id.txtName);
			if( txtV != null ) {
				txtV.setText(mComputer.getName());
			}
			// Exe image 
			edt = (EditText)mRootView.findViewById(R.id.edtExeImage);
			edt.setText(comp.getExec_image());
			// Exe param 
			edt = (EditText)mRootView.findViewById(R.id.edtExeParam);
			edt.setText(comp.getExec_params());
			// Exe current dir
			edt = (EditText)mRootView.findViewById(R.id.edtExeCurDir);
			edt.setText(comp.getExec_params());
			

    		if( mComputer.getResume() == 1 ) {
    			mSeekBarOnOff.setProgress(10);
    		} else {
    			mSeekBarOnOff.setProgress(0);
    		}
    		// 
    		edt = (EditText)mRootView.findViewById(R.id.edtAlertMessage);
    		edt.setText( mComputer.getShutdown_msg() );
			
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "setItem end");}
		}
	}
	
	/**
	 * リモートオペレーション結果を画面に更新
	 * @param e	エラーが発生した場合の、エラーインスタンス。エラーがなかった場合は、null。
	 */
	private void showRemoteOpeResult(Exception e) {
		if( e != null ) {
			this.showAlert(e.getMessage());
		} else {
			MyLog.tost(mActivity, this.getString(R.string.msg_remote_ope_complete), 1);
		}
	}

	/**
	 * 非同期で、リモートコマンドを実行
	 */
	class AsyncRequester extends AsyncTask<String, Integer, Integer> {
		final static String TAG = "AsyncRequester";
		private ProgressDialog 		mProgressDialog;
		private Context				mContext;
		private String				mHostname;
		private int					mPort;
		private COMMAND				mCommand;
		private int					mAfterTime;
		private String				mMessage;
		private	int					mResume;
		private	int					mResumeOpt;
		private String				mExeImage;
		private String				mExeParam;
		private String				mExeCurDir;
		private Exception			mException = null;
		
		
		public AsyncRequester(
				Context context,
				String host, 
				int port, 
				COMMAND command,
				int after_time,
				String strMsg,
				int resume,
				int resume_opt,
				String strExeImage,
				String strExeParams,
				String strExeCurDir)  {
			mContext = context;
			mHostname = host;
			mPort = port;
			mCommand = command;
			mAfterTime = after_time;
			mMessage = strMsg;
			mResume = resume;
			mResumeOpt = resume_opt;
			mExeImage = strExeImage;
			mExeParam = strExeParams;
			mExeCurDir = strExeCurDir;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			try {
				// Show Progress Dialog
				mProgressDialog = new ProgressDialog(mContext);
				mProgressDialog.setTitle(mContext.getString(R.string.app_name));
				mProgressDialog.setIcon(R.drawable.icon);
				mProgressDialog.setMessage("Progressing");
				

				// 画面の回転を禁止
				if( mActivity instanceof Activity ) {
					Configuration config = mContext.getResources().getConfiguration();
					((Activity)mContext).setRequestedOrientation(config.orientation);
				}

				mProgressDialog.show();
			} catch (Exception e) {
				MyLog.loge(TAG, e);
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			try {
				ZzzSvcClient client = new ZzzSvcClient();
				int ret = client.shutdown(mHostname, mPort, mCommand,
						mAfterTime, mMessage,
						mResume,
						mResumeOpt,
						mExeImage,
						mExeParam
		    			,mExeCurDir);
				if(MyLog.isDebugMod()) {MyLog.logt(TAG, " Remote Ope Result:" + ret);}
				switch(ret) {
				case 0:
					break;
				case 1:
					throw new Exception( mContext.getString(R.string.err_remote_ope_connect_server) );
				case 5:
					throw new Exception( mContext.getString(R.string.err_remote_ope_no_logon) );
				case 6:
					throw new Exception( mContext.getString(R.string.err_remote_ope_execute_proc) );
				case 7:
					throw new Exception( mContext.getString(R.string.err_remote_ope_not_found_user) );
				case 8:
					throw new Exception( mContext.getString(R.string.err_remote_ope_timeout) );
				default:
					throw new Exception( mContext.getString(R.string.err_remote_ope_internal_error) );
				}
			} catch (Exception e) {
				MyLog.loge(TAG, e);
				mException = e;
			}
			return 0;
		}
		@Override
		protected void onPostExecute(Integer retval) {
			try {
				try {
					mProgressDialog.dismiss();
				} catch (Exception e) {
					// noting
				}
				
				showRemoteOpeResult(mException);
				
				// 画面の回転を禁止を元に戻す
				if( mActivity instanceof Activity ) {
					((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
				}
			} catch (Exception e) {
				MyLog.loge(TAG, e);
			}
		}
		
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(seekBar.getProgress()<5){
			seekBar.setProgress(0);
		}else{
			seekBar.setProgress(10);
		}
		
	}
}