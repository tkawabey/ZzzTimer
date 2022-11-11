package four.non.bronds.yyys.zzztimer.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import four.non.bronds.yyys.zzztimer.R;

import four.non.bronds.yyys.zzztimer.bean.RecFileInf;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.RecDbAccessor;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.ImageUtil;
import four.non.bronds.yyys.zzztimer.util.MusicUtils;
import four.non.bronds.yyys.zzztimer.util.MyLog;



public class MusicPlayActivity extends MyBaseActivity {
	private static final String TAG = "MusicPlayActivity";
	private StaticMusicPlayFragment mFragment;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreate start");}
		try {
			super.onCreate(savedInstanceState);
			// フラグメントインスタンスを作成
			mFragment = new StaticMusicPlayFragment();
			mFragment.setArguments(this.getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, mFragment).commit();
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreate end");}
		}
	}
	public static class StaticMusicPlayFragment extends MusicPlayFragment {}
}



class MusicPlayFragment extends MyBaseFragment implements OnClickListener {
	final static String TAG = "MusicPlayFragment";

	private String			mMode;
	private String			mArtistID;
	private String			mArtist;
	private String			mArtistKey;
	private String			mAlbum;
	private String			mAlbumKey;
	private String			mAlbumID;
	private String			mAlbumArt;
	private String			mNumberOfSongs;
	private String			mTitleKey;
	private String			mWhere;
	private List<HashMap<String, Object>>	mData;
	private List<HashMap<String, Object>>	mNowPlayList = null;
	private int				mCurPos = 0;
	private HashMap<String, String>	mAlbumArts = new HashMap<String, String>();
	private ProgressBar		mProgressBar;

	private SeekBar			mSeekBar;
	private Chronometer 	mChronometer;
	private long			mDuration = 0;
	private MediaPlayer 	mediaPlayer = null;
	private	boolean			mPause = false;
	private	boolean			mSeekbarTraking = false;
	private ImageButton		mBtnPlay;
	private boolean			mShuffle = false;
	private boolean			mLoop = false;
	private boolean			mScrobbiling = false;
	private boolean			mPrepared = false;
	

	void startScrob() {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "startScrob start:" + mScrobbiling);}
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, " -------" + (mCurPos+1) + "/" + mNowPlayList.size());}
		if( mCurPos+1 > mNowPlayList.size() ) {
			return ;
		}
		
		HashMap<String, Object> m = mNowPlayList.get(mCurPos);
		
		if( mScrobbiling == true ) {
			MusicUtils mU = new MusicUtils(mActivity);
			mU.startScrobbler((String)m.get(MediaStore.Audio.Media.ARTIST), 
						(String)m.get(MediaStore.Audio.Media.ALBUM), 
						(String)m.get(MediaStore.Audio.Media.TITLE), 
						Long.parseLong( (String)m.get(MediaStore.Audio.Media.DURATION) ));
		}
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "startScrob end");}
	}
	void stopScrob() {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "stopScrob start:" + mScrobbiling);}
		MusicUtils mU = new MusicUtils(mActivity);
		mU.stopScrobbler();
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "stopScrob end");}
	}
	/**
	 * 次の曲を再生
	 */
	void nextPlay() {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "nextPlay start");}
		if( mNowPlayList == null ) {
			return ;
		}
		if(MyLog.isDebugMod()) {MyLog.logt(TAG, "mCurPos Cur: " + mCurPos);}
		if( mCurPos+1 >= mNowPlayList.size() ) {
			mCurPos = 0;
			this.reView();
			if( mLoop == true ) {
				playMusic();
			}
			return ;
		}
		if(MyLog.isDebugMod()) {MyLog.logt(TAG, "mCurPos Next: " + mCurPos);}
		mCurPos++;
		playMusic();
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "nextPlay end");}
	}
	/**
	 * 前の曲を再生
	 */
	void previousPlay() {
		if(MyLog.isDebugMod()) {MyLog.logt(TAG, "previousPlay start");}
		if( mNowPlayList == null ) {
			return ;
		}
		if( mCurPos == 0 || mNowPlayList.size() == 0 ) {
			return ;
		}
		mCurPos--;
		playMusic();
		if(MyLog.isDebugMod()) {MyLog.logt(TAG, "previousPlay end");}
	}
	/**
	 * カレントの音楽を再生する。
	 */
	void playMusic() {
		if(MyLog.isDebugMod()) {MyLog.logt(TAG, "playMusic start");}
    	try {
			if (mediaPlayer.isPlaying() ) {
				mediaPlayer.stop();
			}
			HashMap<String, Object> m = mNowPlayList.get(mCurPos);
			
			reView();			
			
			
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource( (String)m.get(MediaStore.Audio.Media.DATA) );
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
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logt(TAG, "playMusic end");}
		}		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {
			
			if (container == null) {
				return null;
			}
			mActivity = this.getActivity();
			mRootView = inflater.inflate(R.layout.music_play, container, false);

			mProgressBar = (ProgressBar)mRootView.findViewById(R.id.progressBar);
			
			mChronometer = (Chronometer)mRootView.findViewById(R.id.chronometer);
			mSeekBar = (SeekBar)mRootView.findViewById(R.id.seekBar);
			
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
				public void onProgressChanged(SeekBar arg0, int arg1,
						boolean arg2) {
					//MyLog.logt(TAG, "onProgressChanged");
					
				}
				// トラッキング開始時に呼び出されます
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
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
					mPause = false;
					mPrepared = false;
					mChronometer.stop();
					mSeekBar.setProgress(0);
					mChronometer.setBase(SystemClock.elapsedRealtime());
					nextPlay();
					mBtnPlay.setImageResource(R.drawable.play);
					
					stopScrob();
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
					
					startScrob();
					
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
			btn = (ImageButton)mRootView.findViewById(R.id.btnPrev);
			btn.setOnClickListener(this);
			btn = (ImageButton)mRootView.findViewById(R.id.btnNext);
			btn.setOnClickListener(this);
			
			btn = (ImageButton)mRootView.findViewById(R.id.btnShuffle);
			btn.setOnClickListener(this);
			btn = (ImageButton)mRootView.findViewById(R.id.btnLoop);
			btn.setOnClickListener(this);
			btn = (ImageButton)mRootView.findViewById(R.id.btnScrobbilling);
			btn.setOnClickListener(this);
			
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
        	mMode = args.getString("MODE");
    		if( "ALBUM".compareTo(mMode) == 0 ) {
    			mArtistID = args.getString(MediaStore.Audio.Media.ARTIST_ID);
    			
    			mArtistKey = args.getString(MediaStore.Audio.Media.ARTIST_KEY);
    			
    			mAlbum = args.getString(MediaStore.Audio.Media.ALBUM);
    			mAlbumKey = args.getString(MediaStore.Audio.Media.ALBUM_KEY);
    			mAlbumID = args.getString(MediaStore.Audio.Media.ALBUM_ID);
    			mAlbumArt = args.getString(MediaStore.Audio.Media.ALBUM_ART);
    			mNumberOfSongs = args.getString(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
    			
    			mTitleKey = args.getString(MediaStore.Audio.Media.TITLE_KEY);
    			
        		mWhere = args.getString(MusicUtils.SPECIAL_KEY_WHERE);
        		
        		if(MyLog.isDebugMod()) {MyLog.logt(TAG, "mWhere:" + mWhere);}
    		} else
    		if( "PLAYLIST".compareTo(mMode) == 0 ) {
    			mArtistKey = args.getString(MediaStore.Audio.Playlists._ID);
    			mArtist = args.getString(MediaStore.Audio.Playlists.NAME);
    			mTitleKey = args.getString(MediaStore.Audio.Playlists.Members.TITLE_KEY);
        		
    			mWhere = args.getString(MusicUtils.SPECIAL_KEY_WHERE);
        		
    			if(MyLog.isDebugMod()) {MyLog.logt(TAG, "mWhere:" + mWhere);}
    		} else
        	if( "ZZZ".compareTo(mMode) == 0 ) {
        		mTitleKey = mArtistKey = args.getString("FNAME");
        		mArtist = Constant.APP_NAME;
        		mWhere = MusicUtils.SPECIAL_VAL_WHERE_EX;
        		
        		if(MyLog.isDebugMod()) {MyLog.logt(TAG, "mWhere:" + mWhere);}
        	}
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
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onActivityCreated end");}
		}		
    }

    @Override
    public void onPause(){
    	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onPause start");}
        try {
        	super.onPause();
        } finally {
        	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onPause end");}
        }
    }

    @Override
    public void onStop(){
    	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onStop start");}
        try {
        	super.onStop();
        } finally {
        	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onStop end");}
        }
    }

    @Override
    public void onDestroyView(){
    	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onDestroyView start");}
        try {
        	super.onDestroyView();
        } finally {
        	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onDestroyView end");}
        }
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
	        	
	        	this.stopScrob();
	        }
    	} finally {
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onDestroy end");}
    	}
    }
	
    private void loadAlbumArts(List<HashMap<String, Object>> data, HashMap<String, String> arts) {
    	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "loadAlbumArts start");}
    	ContentResolver resolver = mActivity.getContentResolver();
		for(HashMap<String, Object> m : data) {
			String strAlbumKey = (String)m.get(MediaStore.Audio.Media.ALBUM_KEY);
			//MyLog.logt(TAG, "ALBUM_KEY:" + strAlbumKey);
			if( strAlbumKey == null ) {
				return ;
			}
	        Cursor cursor = resolver.query(
	        		MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI , 
	        		new String[]{
	        				MediaStore.Audio.Albums.ALBUM_ART,
	        		},    // keys for select. null means all
	        		MediaStore.Audio.Albums.ALBUM_KEY + "=?",
	        		new String[]{ strAlbumKey },
	        		null//MediaStore.Audio.Media.ALBUM + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	MyLog.logt(TAG, " - " + cursor.getString( cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART) ));
		        	arts.put(strAlbumKey, cursor.getString( cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART) ));
		        }
		        cursor.close();
	        }
		}
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "loadAlbumArts end");}
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.btnPlay ) {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "btnPlay:" + mScrobbiling);}
			
			// 再生してなかったら
			if (!mediaPlayer.isPlaying()) {
				if(MyLog.isDebugMod()) {MyLog.logt(TAG, "Not Playing : " + mPause + "  mCurPos:" + mCurPos);}
				if( mPause == false ) {
					HashMap<String, Object> m = mNowPlayList.get(mCurPos);
					
					try {
						mediaPlayer.reset();
						mediaPlayer.setDataSource( (String)m.get(MediaStore.Audio.Media.DATA) );
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
					if(MyLog.isDebugMod()) {MyLog.logf(TAG, "1111:" + mScrobbiling);}
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
		} else
		if( v.getId() == R.id.btnNext ) {
			nextPlay();			
		} else
		if( v.getId() == R.id.btnPrev ) {
			previousPlay();			
		} else
		if( v.getId() == R.id.btnShuffle ) {
			ImageButton btn = (ImageButton)mRootView.findViewById(v.getId());
			if( mShuffle == false ) {
				mShuffle = true;
				btn.setImageResource(R.drawable.shuffle_on);
			} else {
				mShuffle = false;
				btn.setImageResource(R.drawable.shuffle_off);
			}

			mNowPlayList = MusicUtils.shufful(mData, mShuffle);
			
			if( mediaPlayer.isPlaying() == false ) {
				// データをロード
				new MyLoader(this).execute("");
			}
		} else
		if( v.getId() == R.id.btnLoop ) {
			ImageButton btn = (ImageButton)mRootView.findViewById(v.getId());
			if( mLoop == false ) {
				mLoop = true;
				btn.setImageResource(R.drawable.loop_on);
			} else {
				mLoop = false;
				btn.setImageResource(R.drawable.loop_off);
			}
		} else
		if( v.getId() == R.id.btnScrobbilling ) {
			ImageButton btn = (ImageButton)mRootView.findViewById(v.getId());
			if( mScrobbiling == false ) {
				mScrobbiling = true;
				btn.setImageResource(R.drawable.lastfm_on);
			} else {
				mScrobbiling = false;
				btn.setImageResource(R.drawable.lastfm_off);
			}
		}
	
	}



    /**
     * データのロード
     */
    void innerLoad() {

    	MusicUtils musicUtil = new MusicUtils(mActivity);
		mCurPos = -1;
		mData =  new ArrayList<HashMap<String, Object>>();
		mNowPlayList =  new ArrayList<HashMap<String, Object>>();
		if(MyLog.isDebugMod()) {MyLog.logt(TAG, "MODE:" + mMode);}
		if( "ALBUM".compareTo(mMode) == 0 ) {
			musicUtil.getSongs(mArtistKey, mWhere, mAlbumKey, mData);
		} else
		if( "PLAYLIST".compareTo(mMode) == 0 ) {
			musicUtil.getPlaylistMember( Integer.parseInt(mArtistKey), mWhere, mData );
		} else
		if( "ZZZ".compareTo(mMode) == 0 ) {
			RecDbAccessor acc = new RecDbAccessor(mActivity);
			List<RecFileInf> listRecFile = acc.load();
			for(RecFileInf recF : listRecFile) {
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put(MediaStore.Audio.Media.TITLE_KEY, recF.getFName());
				data.put(MediaStore.Audio.Media.DATA, recF.getFName());
				data.put(MediaStore.Audio.Media.MIME_TYPE, "audio/ogg");
				data.put(MediaStore.Audio.Media.DURATION, "" + recF.getTime());
				data.put(MediaStore.Audio.Media.ALBUM, Constant.APP_NAME);
				data.put(MediaStore.Audio.Media.ARTIST, Constant.APP_NAME);
				data.put(MediaStore.Audio.Media.TITLE, recF.getDisp());
				
				mData.add(data);
			}
		}
		loadAlbumArts(mData, mAlbumArts);
		int index = 0;
		for(HashMap<String, Object> m : mData) {
			String strTmp = (String)m.get(MediaStore.Audio.Media.TITLE_KEY);
			if( mTitleKey.compareTo( strTmp )  == 0  ) {
				mCurPos = index;
				break;
			}
			index++;
		}
		mNowPlayList = MusicUtils.shufful(mData, mShuffle);
    }
    void reView() {
    	TextView txtV;
		if( mCurPos  != -1 ) {
			HashMap<String, Object> m = mNowPlayList.get(mCurPos);
			mDuration = Long.parseLong( (String)m.get(MediaStore.Audio.Media.DURATION) );

			
			txtV = (TextView)mRootView.findViewById(R.id.textTitle);
			txtV.setText( (String)m.get(MediaStore.Audio.Media.TITLE) );
			
			txtV = (TextView)mRootView.findViewById(R.id.txtArtist);
			txtV.setText( (String)m.get(MediaStore.Audio.Media.ARTIST) );
			
			txtV = (TextView)mRootView.findViewById(R.id.txtAlbum);
			txtV.setText( (String)m.get(MediaStore.Audio.Media.ALBUM) );
			

			txtV = (TextView)mRootView.findViewById(R.id.txtTime);
			txtV.setText( Formmater.formatTime(mDuration) );
			
			txtV = (TextView)mRootView.findViewById(R.id.txtNum);
			txtV.setText( "" + (mCurPos+1) + "/" +  mNowPlayList.size() );
			
			mDuration /= 1000;	// ミリ秒→秒
			mSeekBar.setMax( (int)mDuration );
			
			//画面サイズ取得
			WindowManager	wmWindowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
			Display			display = wmWindowManager.getDefaultDisplay();
			int widthDisp	= display.getWidth();
			int heightDisp	= display.getHeight();
			
			ImageView img = (ImageView)mRootView.findViewById(R.id.imageAlbumArt);
			if(MyLog.isDebugMod()) {MyLog.logt(TAG, "ALBUM_KEY 2 :" + m.get(MediaStore.Audio.Media.ALBUM_KEY));}
			String strAlbumArt = mAlbumArts.get( m.get(MediaStore.Audio.Media.ALBUM_KEY) );
			if(MyLog.isDebugMod()) {MyLog.logt(TAG, " strAlbumArt :" + strAlbumArt);}
			if( strAlbumArt != null ) {
				//Uri sArtworkUri = Uri.parse(strAlbumArt);
				Bitmap bm = ImageUtil.LoadImageFile(strAlbumArt, widthDisp, heightDisp);
				img.setImageBitmap(bm);
			} else {
				img.setImageResource(R.drawable.play_music_on);				
			}
		}	
    }


	/**
	 * 非同期データローダー
	 */
	class MyLoader extends AsyncTask<String, Integer, Integer> {
		private MusicPlayFragment	mFragment;
		
		
		public MyLoader(MusicPlayFragment fragment) {
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
			mFragment.innerLoad();
			return null;
		}
		@Override
		protected void onPostExecute(Integer retval) {
			if( mFragment.mProgressBar != null ) {
				mFragment.mProgressBar.setVisibility(View.GONE);
			}			
			mFragment.reView();
		}
	}
}