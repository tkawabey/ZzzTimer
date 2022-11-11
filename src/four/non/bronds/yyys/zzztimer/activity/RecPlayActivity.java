package four.non.bronds.yyys.zzztimer.activity;

import java.io.IOException;
import java.util.List;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.activity.MusicPlayFragment.MyLoader;
import four.non.bronds.yyys.zzztimer.bean.RecFileInf;
import four.non.bronds.yyys.zzztimer.bean.RecTagBean;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.RecDbAccessor;
import four.non.bronds.yyys.zzztimer.dialog.HelpDialog;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.MusicUtils;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.PrefereceAcc;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecPlayActivity extends MyBaseActivity {
	private static final String TAG = "RecPlayActivity";
	private StaticRecPlayFragment mFragment;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreate start");}
		try {
			super.onCreate(savedInstanceState);
			// フラグメントインスタンスを作成
			mFragment = new StaticRecPlayFragment();
			mFragment.setArguments(this.getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, mFragment).commit();
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreate end");}
		}
	}
	public static class StaticRecPlayFragment extends RecPlayFragment {}
}

/**
 * 
 */
class RecPlayFragment extends MyBaseFragment implements OnClickListener {
	final static String TAG = "RecPlayFragment";
	private ListView			mListView = null;
	private ProgressBar			mProgressBar;
	private SeekBar				mSeekBar;
	private Chronometer 		mChronometer;
	private ImageButton			mBtnPlay;
	private MediaPlayer 		mediaPlayer = null;
	private	boolean				mPause = false;
	private	boolean				mSeekbarTraking = false;
	private RecFileInf			mFileInf;
	private List<RecTagBean>	mRecTags = null;
	private long				mDuration = 0;
	private long				mPaseTime = 0;
	private boolean				mPrepared = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {			
			if (container == null) {
				return null;
			}
			mActivity = this.getActivity();
			mRootView = inflater.inflate(R.layout.rec_play, container, false);

			mProgressBar = (ProgressBar)mRootView.findViewById(R.id.progressBar);
			mChronometer = (Chronometer)mRootView.findViewById(R.id.chronometer);
			mSeekBar = (SeekBar)mRootView.findViewById(R.id.seekBar);
			mListView = (ListView)mRootView.findViewById(R.id.listViewRecTag);
			mListView.setCacheColorHint(Color.TRANSPARENT);
			mListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onListItemClick(parent, view, position, id);
				}
			});

			
			mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
				@Override
				public void onChronometerTick(Chronometer chronometer) {
					if( mSeekbarTraking == false && mPrepared == true ) {
						int pos = mediaPlayer.getCurrentPosition();
						pos /= 1000;
						mSeekBar.setProgress(pos);
					}
				}
			});
			
			mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

				// トラッキング中に呼び出されます
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
					//MyLog.logt(TAG, "onProgressChanged");
					
				}
				// トラッキング開始時に呼び出されます
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					mSeekbarTraking = true;
					
				}
				// トラッキング終了時に呼び出されます
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					if(MyLog.isDebugMod()) {MyLog.logt(TAG, "onStopTrackingTouch");}
					int pos = seekBar.getProgress();
					mChronometer.setBase(SystemClock.elapsedRealtime() - (long)(pos*1000));
					if( mediaPlayer.isPlaying() ) {
						mediaPlayer.seekTo(pos * 1000);
					}
					mSeekbarTraking = false;
				}
				
			});


			// メディアプレイヤーの作成
			mediaPlayer = new MediaPlayer();
			
			// 再生終了イベント
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if(MyLog.isDebugMod()) {MyLog.logt(TAG, "called setOnCompletionListener"); }
					mPrepared = false;
					mPause = false;
					mChronometer.stop();
					mSeekBar.setProgress( 0 );
					mBtnPlay.setImageResource(R.drawable.play);
				}
			});
			// 準備完了
			mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					if(MyLog.isDebugMod()) {MyLog.logt(TAG, "called onPrepared");}
					mPrepared = true;
					mediaPlayer.start();
					int lPos = mSeekBar.getProgress() * 1000;
					mediaPlayer.seekTo(lPos);
					mChronometer.setBase(SystemClock.elapsedRealtime() - (lPos));
					mChronometer.start();
					mBtnPlay.setImageResource(R.drawable.pause);
				}
			} );
			// シーク完了イベント
			mediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
				public void onSeekComplete(MediaPlayer mp) {
					if(MyLog.isDebugMod()) {MyLog.logt(TAG, "called setOnSeekCompleteListener");}
				}
			});
			//
			mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					if(MyLog.isDebugMod()) {MyLog.logt(TAG, "called setOnInfoListener what:" + what + "  extra:" + extra);}
					return false;
				}
			});
			mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					if(MyLog.isDebugMod()) {MyLog.logt(TAG, "called setOnBufferingUpdateListener percent:" + percent);}
					
				}
			} );
			
			ImageButton btn;
			mBtnPlay = (ImageButton)mRootView.findViewById(R.id.btnPlay);
			mBtnPlay.setOnClickListener(this);
			
			
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView end");}
		}		
		return mRootView;
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
    		mFileInf = (RecFileInf)args.getSerializable("RecFileInf");
        }
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onActivityCreated start");}
    	try {
			mActivity = this.getActivity();
			mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			// データをロード
			new MyLoader(this).execute("");
	    	
	    	// オプションメニュー
	    	setHasOptionsMenu(true);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onActivityCreated end");}
		}		
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
    		String strURL = acc.getHelpURL(PrefereceAcc.HELP_URL.HELP_URL_REC_PALY);
    		HelpDialog.show(this.getActivity(), strURL);
    	}
    	return true;	
    }
    
	@Override
    public void onDestroy(){
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onDestroy start");}
    	try {
	        super.onDestroy();
	        if( mediaPlayer != null ) {
	        	if( mediaPlayer.isPlaying() ) {
	        		mediaPlayer.stop();
	        	}
	        	mediaPlayer.release();
	        	mediaPlayer = null;
	        }
    	} finally {
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onDestroy end");}
    	}
    }
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.btnPlay ) {
			// 再生してなかったら
			if (!mediaPlayer.isPlaying()) {
				if(MyLog.isDebugMod()) {MyLog.logt(TAG, "Not Playing : " + mPause + " ");}
				if( mPause == false ) {
					try {
						mediaPlayer.reset();
						mediaPlayer.setDataSource( mFileInf.getFName() );
						mediaPlayer.prepare();
					} catch (IllegalArgumentException e) {
						MyLog.loge(TAG, e);
						this.showAlert(e.getMessage());
						return ;
					} catch (IllegalStateException e) {
						MyLog.loge(TAG, e);
						this.showAlert(e.getMessage());
						return ;
					} catch (IOException e) {
						MyLog.loge(TAG, e);
						this.showAlert(e.getMessage());
						return ;
					}
				} else {
					int seekPos = mSeekBar.getProgress();
					mediaPlayer.start();
					mediaPlayer.seekTo(seekPos*1000);
					mChronometer.setBase(SystemClock.elapsedRealtime() - (seekPos*1000));
					mChronometer.start();
					mBtnPlay.setImageResource(R.drawable.pause);					
				}
			} else {
				if(MyLog.isDebugMod()) {MyLog.logt(TAG, "Playing");}
				mediaPlayer.pause();
				mChronometer.stop();
				mPause = true;
				mBtnPlay.setImageResource(R.drawable.play);				
			}
		}
	}
	/**
	 * タグリストクリック時、指定されたタグの時間から、再生する。
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	void onListItemClick(AdapterView<?> parent, View view,
				int position, long id) {
		RecTagBean tag = (RecTagBean)parent.getItemAtPosition(position);
		int duration = (int)tag.getTime();
		if( mPrepared == true) {
			mediaPlayer.seekTo(duration);
		}
		duration /= 1000;
		mSeekBar.setProgress(duration);
		mChronometer.setBase(SystemClock.elapsedRealtime() - tag.getTime());
	}
    /**
     * データのロード
     * @throws Exception 
     */
    void innerLoad() throws Exception {
    	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "innerLoad start"); }
    	try {
    		RecDbAccessor acc = new RecDbAccessor(mActivity);
    		mRecTags = acc.lodTags(mFileInf.getFName());
    	} catch(Exception e) {
    		MyLog.loge(TAG, e);
    		throw e;
    	} finally {
    		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "innerLoad end"); }
    	}
    }
    /**
     * 画面を再描画
     */
    void reView() {
    	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "reView start"); }
    	try {
        	TextView txtV;
    		if( mRecTags != null ) {
    			mDuration = mFileInf.getTime();
    			// リストを再表示
    			RecTagAdapter adapter = new RecTagAdapter(mActivity, mRecTags);
    			mListView.setAdapter(adapter);
    			
    			txtV = (TextView)mRootView.findViewById(R.id.textTitle);
    			txtV.setText( mFileInf.getDisp() );

    			txtV = (TextView)mRootView.findViewById(R.id.txtArtist);
    			txtV.setText( mFileInf.getDateStr() );
    			

    			txtV = (TextView)mRootView.findViewById(R.id.txtTime);
    			txtV.setText( Formmater.formatTime(mFileInf.getTime()) );
    			

    			mDuration /= 1000;	// ミリ秒→秒
    			mSeekBar.setMax( (int)mDuration );
    			
    			
    		}    		
    	} catch(Exception e) {
    		MyLog.loge(TAG, e);
    		this.showAlert(e.getMessage());
    	} finally {
    		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "reView end"); }
    	}
    }
	

	
    /**
     * 録音タグアダプター
     */
    private class RecTagAdapter extends ArrayAdapter<RecTagBean> {
    	private LayoutInflater 	mInflater;
    	public RecTagAdapter(Context context, List<RecTagBean> objects) {
    		super(context, 0, objects);
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.rec_tag_row, null);
			}
			RecTagBean tag = this.getItem(position);
			TextView  txtView = null;
			
			txtView = (TextView)view.findViewById(R.id.textViewRecTagName);
			txtView.setText( tag.getTag() );
			
			txtView = (TextView)view.findViewById(R.id.textViewRecTagTime);
			txtView.setText( Formmater.formatTime( tag.getTime() ) );

			return view;
		}
    }
    

	/**
	 * 非同期データローダー
	 */
	private class MyLoader extends AsyncTask<String, Integer, Integer> {
		private RecPlayFragment	mFragment;
		private Exception		mException = null;
		
		
		public MyLoader(RecPlayFragment fragment) {
			mFragment = fragment;
		}
		@Override
		protected void onPreExecute() {
			if( mFragment.mProgressBar != null ) {
				mFragment.mProgressBar.setVisibility(View.VISIBLE);
			}
		}
		@Override
		protected Integer doInBackground(String... arg0) {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "doInBackground start"); }
			try {
				mFragment.innerLoad();
			} catch(Exception e) {
				mException = e;
			} finally {
				if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "doInBackground end"); }
			}
			return null;
		}
		@Override
		protected void onPostExecute(Integer retval) {
			if( mFragment.mProgressBar != null ) {
				mFragment.mProgressBar.setVisibility(View.GONE);
			}
			if( mException == null ) {
				mFragment.reView();
			} else {
				mFragment.showAlert( mException.getMessage() );
			}
		}
	}
}