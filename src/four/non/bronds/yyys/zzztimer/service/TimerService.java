package four.non.bronds.yyys.zzztimer.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.activity.ZzzTimerActivity;
import four.non.bronds.yyys.zzztimer.bean.RecFileInf;
import four.non.bronds.yyys.zzztimer.bean.RecTagBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.client.ZzzSvcClient;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.RecDbAccessor;
import four.non.bronds.yyys.zzztimer.db.TimerSettingAccessor;
import four.non.bronds.yyys.zzztimer.util.DTFM;
import four.non.bronds.yyys.zzztimer.util.MusicUtils;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.PCMRecordFile;
import four.non.bronds.yyys.zzztimer.util.PrefereceAcc;
import four.non.bronds.yyys.zzztimer.util.StrageUtil;
import four.non.bronds.yyys.zzztimer.util.WakeOnLan;
import four.non.bronds.yyys.zzztimer.util.WakeOnLan.InvalidMacAddressException;

//import android.R;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

public class TimerService extends Service {
	final static String TAG = "TimerService";

	public static final String ACTION = "four.non.bronds.retimere.action";
	private static final Class<?>[] mSetForegroundSignature = new Class[] {
	    boolean.class};
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
	    int.class, Notification.class};
	private static final Class<?>[] mStopForegroundSignature = new Class[] {
	    boolean.class};

	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	
	
	// private TimerItem timerItem = null;
	private static List<TimerItem> mTimerList = new ArrayList<TimerItem>();

	class TimerItem extends TimerTask {
		final static String TAG = "TimerItem";
		private Timer							timer = null;
		private Timer							timerWOL = null;
		private WifiManager.WifiLock			wifiLock = null;
		private PowerManager.WakeLock			wakeLock = null;
		private TimerSettingBean				mSetting;			// タイマー設定
		private long 							mStartTime = 0;		// タイマー開始時間
		private long 							mDelay = 0;			// 
		private MediaPlayer 					mMediaPlayer = null;	// メディアプレイヤー
		private List<HashMap<String, Object>>	mMusicData;			// 音楽データ
		private int								mMusicCur = 0;		// カレントのMusicのポジション

		private DTFM							mDTFM;
		private PCMRecordFile					mPCMRecord;
		private Thread							mRecThread;
		private Object							mSync = new Object();
		private String							mRecDir = null;
		private String							mRecFile = null;
		private  MediaScannerConnection			mMediaScannerConnection = null;
		private List<RecTagBean>				mRecTags = null;
		private String							mWOLBCastAddr = null;
		private String							mMacAddr = null;
		private WakeOnLan						mWOL = null;
		
		public int getmItemId() {
			return mSetting.getID();
		}
		

		public TimerItem(Context context, TimerSettingBean item, long delay) {
//			this.mItemId = item.getID();
			try {
				if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "TimerItem start");}
				
				this.mSetting = item;
				this.mDelay = delay;
				
				timer = new Timer();
	
				if (item.isWifiLock()) {
					WifiManager wifiManager = (WifiManager) context
							.getSystemService(Context.WIFI_SERVICE);
					wifiLock = wifiManager.createWifiLock("wifilock");
					wifiLock.setReferenceCounted(true);
					wifiLock.acquire();
				}
				if (item.isPoweriLock()) {
					PowerManager powerManager = (PowerManager) context
							.getSystemService(Context.POWER_SERVICE);
					wakeLock = powerManager.newWakeLock(
							PowerManager.FULL_WAKE_LOCK, "wakelock");
					wakeLock.acquire();
	
				} else {
					PowerManager powerManager = (PowerManager) context
							.getSystemService(Context.POWER_SERVICE);
					wakeLock = powerManager.newWakeLock(
							PowerManager.PARTIAL_WAKE_LOCK, "wakelock");
					wakeLock.acquire();
				}
				
				// 音楽の設定
				if( mSetting.isMusic() ) {
					try {
						AudioManager audioManager = (AudioManager)TimerService.this.getSystemService(Context.AUDIO_SERVICE);
						int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
						int curVolume = new PrefereceAcc(TimerService.this).getMusicVolume();
						
						float perVolume = ((float)curVolume/(float)maxVolume);
						
						
						if( StrageUtil.isSDcardExist( TimerService.this ) == false ) {
							throw new Exception( TimerService.this.getString( R.string.errMsgNotExistStrage ) );
						}
						mMusicData = getPlaylistMember( mSetting.getMusicID() );
						if( mMusicData == null ) {
							throw new Exception("fail to load playlist:" + mSetting.getMusicID());
						}
						mMusicData = MusicUtils.shufful(mMusicData, mSetting.isMusicShuffle());
						
						mMediaPlayer = new MediaPlayer();
						mMediaPlayer.setVolume(perVolume, perVolume);
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_SHOW_UI);
						// 再生終了イベント
						mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener (){
							@Override
							public void onCompletion(MediaPlayer arg0) {
								try {
									if( mSetting.isMusicScrib() ) {
										new MusicUtils(TimerService.this).stopScrobbler();
									}
								} catch(Exception e) {}
								// 次の曲を再生
								nextPlay();
							}						
						});
						// 音楽を再生
						playMusic();
					} catch(Exception e) {
						MyLog.loge(TAG, e);
						MyLog.tost(context, e.getMessage() , 1);
					}			
				}
				
				// 録音の設定
				if( MyLog.isDebugMod() ) { MyLog.logt(TAG, "mSetting.isRecord() : " + mSetting.isRecord()); }
				if( mSetting.isRecord() ) {
					java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss");
					recordStart(StrageUtil.getRecDir(TimerService.this), "Timer_" + formatter.format(new Date() ));
				}
			} catch(Exception e) {
				MyLog.loge(TAG, e);
				MyLog.tost(context, e.getMessage() , 1);
			} finally {
				if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "TimerItem end"); }
			}
			
		}

		public void release() {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "release start"); }
			try {
				if (wifiLock != null) {
					wifiLock.release();
					wifiLock = null;
				}
				if (wakeLock != null) {
					wakeLock.release();
					wakeLock = null;
				}
	
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				if( timerWOL != null ) {
					timerWOL.cancel();
					timerWOL = null;
				}
				
				if(mSetting.isRecord()) {
	                Intent intent = new Intent("four.non.bronds.yyys.pipoparoid.Stop");
	                intent.putExtra("COMMAND", "Stop");
	                sendBroadcast(intent);
				}
				
				if( mMediaPlayer != null ) {
					try {
						mMediaPlayer.release();
						mMediaPlayer = null;
					} catch(Exception e) {
						
					}
					try {
						if( mSetting.isMusicScrib() ) {
							new MusicUtils(TimerService.this).stopScrobbler();
						}
					} catch(Exception e) {}
				}
				// 録音の設定
				if( mSetting.isRecord() ) {
					this.recordStop();
				}
			} catch(Exception e) {
				MyLog.loge(TAG, e);
				MyLog.tost(TimerService.this, e.getMessage() , 1);
			} finally {
				if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "release end"); }
			}
		}

		/**
		 * 開始時間の取得
		 * @return	開始時間
		 */
		public long getStartTime() {
			return mStartTime;
		}
		/**
		 * 経過時間の取得
		 * @return	経過時間
		 */
		public long getLeftTime() {
			long elapseTime = System.currentTimeMillis() - mStartTime;
			
			return mDelay - elapseTime;
		}
		
		void schedule(TimerTask timerTask, long delay) {
			try {
				if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "schedule start"); }
				mStartTime = System.currentTimeMillis();
				
				timer.schedule(timerTask, delay);
				
				
				if( mSetting.isEnableWOL() ) {
					if( mSetting.getWOLComputer() != null ) {

						byte bytearry[];
						bytearry = ZzzSvcClient.toByte(mSetting.getWOLComputer().getBroadcast_addr());
						mWOLBCastAddr = "" + ZzzSvcClient.getByte2Int(bytearry[3]);
						mWOLBCastAddr += "." + ZzzSvcClient.getByte2Int(bytearry[2]);
						mWOLBCastAddr += "." + ZzzSvcClient.getByte2Int(bytearry[1]);
						mWOLBCastAddr += "." + ZzzSvcClient.getByte2Int(bytearry[0]);
						mMacAddr = mSetting.getWOLComputer().getMac_addr();
						mWOL = new WakeOnLan(mWOLBCastAddr, mSetting.getWOLComputer().getPort_wol());
					}
					
					// WOL タイマー
					timerWOL = new Timer(true);
					System.out.println("WOL Timer  [" + mSetting.getWolRepeat());
					if( mSetting.getWolRepeat() != 0 ) {
						this.run();
						timerWOL.schedule(this, mSetting.getWolRepeat()*1000, mSetting.getWolRepeat()*1000);
					} else {
						this.run();
					}
				}
			} catch(Exception e) {
				MyLog.loge(TAG, e);
				MyLog.tost(TimerService.this, e.getMessage() , 1);
			} finally {
				if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "schedule end"); }
			}
		}

		/**
		 * 録音開始
		 * @param strSaveDir
		 * @param strFileName
		 * @return
		 */
		public boolean recordStart(String strSaveDir, String strFileName)
		{
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "recordStart start"); }
			boolean ret = false;

			synchronized( mSync ) {
				try {
					PrefereceAcc prefAcc = new PrefereceAcc(TimerService.this);
					int format = prefAcc.getRecFormat();
					if( format == Constant.REC_FORMAT_PCM ) {
						strFileName += ".wav";
					} else
					if( format == Constant.REC_FORMAT_OGG ) {
						strFileName += ".ogg";
					}
					
					
					recordStop();
					if( mDTFM != null ) {
						mDTFM.destroy();
						mDTFM = null;
					}
					mDTFM = DTFM.getInstance(0, getSharedPreferences(Constant.SHARED_PREF, MODE_WORLD_READABLE |MODE_WORLD_WRITEABLE ));
					if( MyLog.isDebugMod() ) { 
						MyLog.logt(TAG, "getSampleBitPerSec:" + mDTFM.getSampleBitPerSec());
						MyLog.logt(TAG, "getSampleBits:" + mDTFM.getSampleBits());
						MyLog.logt(TAG, "getSingnalMsec:" + mDTFM.getSingnalMsec());
						MyLog.logt(TAG, "getChanel:" + mDTFM.getChanel());
					}
					mRecDir = StrageUtil.getTempDir(TimerService.this);
					mRecFile = strFileName;
					mPCMRecord = new PCMRecordFile(TimerService.this, mDTFM, StrageUtil.getRecDir(TimerService.this), strFileName, format);
					mPCMRecord.setOnRecodListener(
							new PCMRecordFile.OnRecodListener() {
								@Override
								public void onRecodeEnd(RecFileInf rcf) {
									// DBに登録
									RecDbAccessor acc = new RecDbAccessor(TimerService.this);
									acc.add(rcf, mRecTags);									
								}
							}
					);
					
					mRecThread = new Thread( mPCMRecord );
					mRecThread.start();
					

					ret = true;					
				} catch(Exception e) {
					MyLog.loge(TAG, e);
					mRecThread = null;
					mPCMRecord = null;
				}
			}
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "recordStart end"); }
			return ret;
		}
		/**
		 * 録音停止
		 */
		public void recordStop() {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "recordStop start"); }
			if( mPCMRecord != null ) {
				synchronized( mSync ) {
					int format = mPCMRecord.getFormat();
		    		mPCMRecord.stop();
		    		
		    		
		    		if( mRecThread != null ) {
		    			try {
		    				mRecThread.join();
		        		} catch (InterruptedException e) {
		    				e.printStackTrace();
		    			}
						mRecThread = null;
		    		}
		    		
		    		mPCMRecord = null;
		    		
		    		
		    		try {
		    			
		    			final String strMIME;
		    			if( format == Constant.REC_FORMAT_PCM ) {
		    				strMIME = "audio/x-wav";
		    			} else
		    			if( format == Constant.REC_FORMAT_OGG ) {
		    				strMIME = "audio/ogg";
		    			} else {
		    				strMIME = "audio/x-wav";
		    			}
		    			if( MyLog.isDebugMod() ) {  
		    				MyLog.logi(TAG, "scanFile : " + mRecDir + "/" + mRecFile);
		    				MyLog.logi(TAG, "MIME :" +  strMIME);
		    			}
			    		// 
//						String paths[] = { mRecDir + "/" + mRecFile };
//						String[] mimeTypes = { strMIME };
		    			if( format == Constant.REC_FORMAT_OGG ) {
							mMediaScannerConnection = new MediaScannerConnection(TimerService.this.getApplicationContext(), 
									new MediaScannerConnection.MediaScannerConnectionClient(){
										@Override
										public void onMediaScannerConnected() {
											if( MyLog.isDebugMod() ) {  MyLog.logi(TAG, "onMediaScannerConnected"); }
											mMediaScannerConnection.scanFile(mRecDir + "/" + mRecFile, strMIME);										
										}
										@Override
										public void onScanCompleted(String path, Uri uri) {
											if( MyLog.isDebugMod() ) {  MyLog.logi(TAG, "onScanCompleted:" + path); }
	//										MyLog.tost(TimerService.this, "regist Record:" + path , 1);
										}
							});
							mMediaScannerConnection.connect();
	/*						
							MediaScannerConnection.scanFile(TimerService.this.getApplicationContext(),
				                    paths,
				                    mimeTypes,
				                    new MediaScannerConnection.OnScanCompletedListener(){
			
										@Override
										public void onScanCompleted(String path, Uri uri) {
											MyLog.tost(TimerService.this, "regist Record:" + path , 1);
										}
									}
							);
						}
*/						
		    			} else
		    			if( format == Constant.REC_FORMAT_PCM ) {
		    				// PCMはScanで情報を拾えないので、ContentProviderで登録する。ことにした。
//		    				ContentResolver cr = getContentResolver();
//		    				ContentValues values = new ContentValues();  
//		    				values.put(MediaStore.Audio.Media.TITLE, "Rec-" + new Date());  
//		    				values.put(MediaStore.Audio.Media.DATA, mRecDir + "/" + mRecFile);
//		    				values.put(MediaStore.Audio.Media.MIME_TYPE, strMIME);
//		    				values.put(MediaStore.Audio.Media.DURATION, strMIME);
		    			}
		    		} catch(Exception e) {
		    			MyLog.loge(TAG, e);
		    		}
				}
			}
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "recordStop end"); }
		}
		public boolean isRecording() {
			if( mPCMRecord == null ) {
				return false;
			}
			return mPCMRecord.isRecording();
		}
		public boolean isPause() {
			if( mPCMRecord == null ) {
				return false;
			}
			return mPCMRecord.isPause();
		}
		public void pauseRec() {
			if( mPCMRecord == null ) {
				return;
			}
			mPCMRecord.pause();
		}
		public void resumeRec() {
			if( mPCMRecord == null ) {
				return;
			}
			mPCMRecord.resume();
		}
		/**
		 * 録音した時間を取得
		 * @return
		 */
		public long getRecordedTime() {
			if( mPCMRecord == null ) {
				return 0;
			}
			return mPCMRecord.getRecordedTime();
		}

		/**
		 * 録音タグを設定
		 * @param tags	録音タグの配列	
		 */
		public void setRecTag(List<RecTagBean> tags) {
			mRecTags = new ArrayList<RecTagBean>();
			for(RecTagBean tag : tags ) {
				mRecTags.add( tag.clone() );
			}			
		}
		
		
		/**
		 * WOLの定期処理
		 */
		@Override
		public void run() {
			try {
				if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "WOL Timer start"); }
				if( mWOL != null ) {
					if( MyLog.isDebugMod() ) {  MyLog.logt(TAG, "MAC      : " + mSetting.getWOLComputer().getMac_addr()); }
					if( MyLog.isDebugMod() ) {  MyLog.logt(TAG, "wakeLock : " + wakeLock); }
					
					
					mWOL.send( mMacAddr );
				}
			} catch(UnknownHostException e) {
				MyLog.loge(TAG, e);
				MyLog.tost(TimerService.this, "unkown host" , 1);
			} catch(InvalidMacAddressException e) {
				MyLog.loge(TAG, e);
				MyLog.tost(TimerService.this, "invalid mac address" , 1);
			} catch(Exception e) {
				MyLog.loge(TAG, e);
				String strErr = e.getMessage();
	    		if( strErr == null ) {
	    			strErr = "unknown error!";
	    		}
	    		MyLog.tost(TimerService.this, "WOL Timer Error. " + strErr , 1);
			} finally {
				if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "WOL Timer end"); }
			}
		}
		


		/**
		 * 次の曲を再生
		 */
		void nextPlay() {
			if( MyLog.isDebugMod() ) { MyLog.logt(TAG, "nextPlay start"); }
			if( mMusicData == null ) {
				return ;
			}
			if( mMusicCur+1 >= mMusicData.size() ) {
				mMusicCur = 0;
			} else {
				mMusicCur++;
			}
			playMusic();
			if( MyLog.isDebugMod() ) { MyLog.logt(TAG, "nextPlay end"); }
		}
		private void playMusic() {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "playMusic start"); }
			try {
				if (mMediaPlayer.isPlaying() ) {
					mMediaPlayer.stop();
				}
				HashMap<String, Object> m = mMusicData.get(mMusicCur);
				try {
					mMediaPlayer.reset();
					mMediaPlayer.setDataSource( (String)m.get(MediaStore.Audio.Media.DATA) );
					mMediaPlayer.prepare();
					mMediaPlayer.start();
					
					if( mSetting.isMusicScrib() ) {
						new MusicUtils(TimerService.this).startScrobbler( (String)m.get(MediaStore.Audio.Media.ARTIST ), 
								(String)m.get(MediaStore.Audio.Media.ALBUM), 
								(String)m.get(MediaStore.Audio.Media.TITLE), 
								Long.parseLong( (String)m.get(MediaStore.Audio.Media.DURATION) ));
					}
				} catch (IllegalArgumentException e) {
					MyLog.loge(TAG, e);
					return ;
				} catch (IllegalStateException e) {
					MyLog.loge(TAG, e);
					return ;
				} catch (IOException e) {
					MyLog.loge(TAG, e);
					return ;
				}
			} catch(Exception e) {
				MyLog.loge(TAG, e);
				String strErr = e.getMessage();
	    		if( strErr == null ) {
	    			strErr = "unknown error!";
	    		}
	    		MyLog.tost(TimerService.this, "Play Music Error. " + strErr , 1);
			} finally {
				if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "playMusic end"); }
			}
		}
	    
	    /**
	     * MusicプレイリストIDから曲情報一覧を
	     * @param id
	     * @return
	     */
	    private List<HashMap<String, Object>> getPlaylistMember(int id) {
	    	if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "getPlaylistMember start"); }
	    	try {
	    	List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	    	
			Cursor cursor = TimerService.this.getContentResolver().query(   
					MediaStore.Audio.Playlists.Members.getContentUri("external", id),
					null,
	                null,
	                null,
	                null);
			while( cursor.moveToNext() ){
				HashMap<String, Object> m = new HashMap<String, Object>();
				for(int i = 0; i < cursor.getColumnCount(); i++ ) {
					m.put(cursor.getColumnName(i), cursor.getString( i ));
				}
				list.add( m );
			}  
			cursor.close();
	        return list;
	    	} finally {
	    		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "getPlaylistMember end"); }
	    	}
	    }
	}

	public class TimerServiceBinder extends Binder {
		public TimerService getService() {
			return TimerService.this;
		}
	}

	@Override
	public void onCreate() {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreate start"); }
		try {
			super.onCreate();
			
		    try {
		        mStartForeground = getClass().getMethod("startForeground",
		                mStartForegroundSignature);
		        mStopForeground = getClass().getMethod("stopForeground",
		                mStopForegroundSignature);
		    } catch (NoSuchMethodException e) {
		        // Running on an older platform.
		        mStartForeground = mStopForeground = null;
		    }
	
		    try {
		        mSetForeground = getClass().getMethod("setForeground",
		                mSetForegroundSignature);
		    } catch (NoSuchMethodException e) {
		    	mSetForeground = null;
		    }
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreate end"); }
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onStart start"); }
		try {
			super.onStart(intent, startId);
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onStart end"); }
		}
	}

	@Override
	public void onDestroy() {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onDestroy start"); }
		try {
			super.onDestroy();
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onDestroy end"); }
		}

		// if (timerItem != null) {
		// timerItem.release();
		// timerItem = null;
		// }
	}

	@Override
	public IBinder onBind(Intent arg0) {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onBind start"); }
		try {
			return new TimerServiceBinder();
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onBind end"); }
		}
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true; // 再度クライアントから接続された際に onRebind を呼び出させる場合は true を返す
	}

	/**
	 * 実行中のタイマーID一覧を取得
	 * @return
	 */
	public List<Integer> getRunningTimerID() {
		List<Integer> ret = new ArrayList<Integer>();
		
		
		synchronized( mTimerList ) {
			for (TimerItem i : mTimerList) {
				
				ret.add( i.getmItemId() );
			}
		}
		return ret;
	}
	
	public void cancelTimer(int timerID) {
		synchronized( mTimerList ) {
			for (TimerItem i : mTimerList) {
				if (i.getmItemId() == timerID) {
					i.release();
//				    // If we have the new stopForeground API, then use it.
//				    if (mStopForeground != null) {
//				        mStopForegroundArgs[0] = Boolean.TRUE;
//				        try {
//				            mStopForeground.invoke(this, mStopForegroundArgs);
//				        } catch (InvocationTargetException e) {
//				            // Should not happen.
//				            Log.w("ApiDemos", "Unable to invoke stopForeground", e);
//				        } catch (IllegalAccessException e) {
//				            // Should not happen.
//				            Log.w("ApiDemos", "Unable to invoke stopForeground", e);
//				        }
//				    } else
//				    if( mSetForegroundArgs != null ) {
//				        mSetForegroundArgs[0] = Boolean.FALSE;
//				        invokeMethod(mSetForeground, mSetForegroundArgs);
//				    }
//
//					// 通知を消去
//					NotificationManager mNM = (NotificationManager) TimerService.this.getSystemService(Context.NOTIFICATION_SERVICE);
//					mNM.cancel(i.getmItemId());
					hideNotify( i.getmItemId() );
					
					mTimerList.remove( i );
					
					return ;
				}
			}
		}
	}

	/**
	 * @param timerID
	 * @return
	 */
	public long getLeftTime(int timerID) {
		synchronized( mTimerList ) {
			for (TimerItem i : mTimerList) {
				if (i.getmItemId() == timerID) {
					return i.getLeftTime();
				}
			}
		}		
		return 0;
	}
	/**
	 * 録音した時間を取得
	 * @param timerID
	 * @return
	 */
	public long getRecordedTime(int timerID) {
		synchronized( mTimerList ) {
			for (TimerItem i : mTimerList) {
				if (i.getmItemId() == timerID) {
					return i.getRecordedTime();
				}
			}
		}		
		return 0;
	}

	/**
	 * 録音タグを設定
	 * @param tags	録音タグの配列	
	 */
	public void setRecTag(int timerID, List<RecTagBean> tags) {
		synchronized( mTimerList ) {
			for (TimerItem i : mTimerList) {
				if (i.getmItemId() == timerID) {
					i.setRecTag(tags);
					break;
				}
			}
		}			
	}
	// クライアントから呼び出されるメソッド
	public void schedule(TimerSettingBean item, long delay) {
		// if (timerItem != null) {
		// timerItem.release();
		// timerItem = null;
		// }
		


		// timer = new Timer();
		TimerItem timerItem = new TimerItem(TimerService.this, item, delay);
		timerItem.schedule(new MyTimerTask(item.getID()), delay);

		mTimerList.add(timerItem);
		
		reRegNotify(item);
		
/*

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"MM/dd HH:mm");
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.HOUR_OF_DAY, item.getHour());
		cal1.add(Calendar.MINUTE, item.getMinite());

		String strSummary;
		strSummary = this.getString(four.non.bronds.yyys.zzztimer.R.string.screen) + " : ";
		if (item.isPoweriLock()) {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.on);
		} else {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.off);
		}
		strSummary += "  " + this.getString(four.non.bronds.yyys.zzztimer.R.string.wifi) + " : ";
		if (item.isWifiLock()) {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.on);
		} else {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.off);
		}

		strSummary += "  " + this.getString(four.non.bronds.yyys.zzztimer.R.string.audio) + " : ";
		if (item.getmAudio() == 1 ) {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.on);
		} else {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.off);
		}
		
		

		// timer = new Timer();
		TimerItem timerItem = new TimerItem(TimerService.this, item);
		timerItem.schedule(new MyTimerTask(item.getID()), delay);

		mTimerList.add(timerItem);

		NotificationManager mNM = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				four.non.bronds.yyys.zzztimer.R.drawable.icon_notify,
				"Running Zzz... Timer!  " + formatter.format(cal1.getTime()), (long) 0);
		Intent intent = new Intent(this, ZzzTimerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent
				.getActivity(
						TimerService.this, 
						0, 
						intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(TimerService.this,
				"Running Zzz... Timer!  " + formatter.format(cal1.getTime()),
				strSummary,
				contentIntent);
		//notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags =  Notification.FLAG_NO_CLEAR         // クリアボタンを表示しない ※ユーザがクリアできない
		          | Notification.FLAG_ONGOING_EVENT;   // 継続的イベント領域に表示 ※「実行中」領域

		
		
		if( mStartForeground != null ) {
			mStartForegroundArgs[0] = Integer.valueOf(item.getID());
	        mStartForegroundArgs[1] = notification;
	        invokeMethod(mStartForeground, mStartForegroundArgs);
		} else
		if( mSetForeground != null ) {
		    // Fall back on the old API.
		    mSetForegroundArgs[0] = Boolean.TRUE;
		    invokeMethod(mSetForeground, mSetForegroundArgs);
		    mNM.notify(item.getID(), notification);
		}
*/
	}
	
	public void reRegNotify(TimerSettingBean item) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"MM/dd HH:mm");
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.HOUR_OF_DAY, item.getHour());
		cal1.add(Calendar.MINUTE, item.getMinite());

		String strSummary;
		strSummary = this.getString(four.non.bronds.yyys.zzztimer.R.string.screen) + " : ";
		if (item.isPoweriLock()) {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.on);
		} else {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.off);
		}
		strSummary += "  " + this.getString(four.non.bronds.yyys.zzztimer.R.string.wifi) + " : ";
		if (item.isWifiLock()) {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.on);
		} else {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.off);
		}

		strSummary += "  " + this.getString(four.non.bronds.yyys.zzztimer.R.string.audio) + " : ";
		if (item.getmAudio() == 1 ) {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.on);
		} else {
			strSummary += this.getString(four.non.bronds.yyys.zzztimer.R.string.off);
		}
		
		
		NotificationManager mNM = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				four.non.bronds.yyys.zzztimer.R.drawable.icon_notify,
				"Running Zzz... Timer!  " + formatter.format(cal1.getTime()), (long) 0);
		Intent intent = new Intent(this, ZzzTimerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent
				.getActivity(
						TimerService.this, 
						0, 
						intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(TimerService.this,
				"Running Zzz... Timer!  " + formatter.format(cal1.getTime()),
				strSummary,
				contentIntent);
		//notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags =  Notification.FLAG_NO_CLEAR         // クリアボタンを表示しない ※ユーザがクリアできない
		          | Notification.FLAG_ONGOING_EVENT;   // 継続的イベント領域に表示 ※「実行中」領域

		
		
		if( mStartForeground != null ) {
			mStartForegroundArgs[0] = Integer.valueOf(item.getID());
	        mStartForegroundArgs[1] = notification;
	        invokeMethod(mStartForeground, mStartForegroundArgs);
		} else
		if( mSetForeground != null ) {
		    // Fall back on the old API.
		    mSetForegroundArgs[0] = Boolean.TRUE;
		    invokeMethod(mSetForeground, mSetForegroundArgs);
		    mNM.notify(item.getID(), notification);
		}
	}
	
	public void hideNotify(int timer_id)
	{
	    // If we have the new stopForeground API, then use it.
	    if (mStopForeground != null) {
	        mStopForegroundArgs[0] = Boolean.TRUE;
	        try {
	            mStopForeground.invoke(this, mStopForegroundArgs);
	        } catch (InvocationTargetException e) {
	            // Should not happen.
	            Log.w("ApiDemos", "Unable to invoke stopForeground", e);
	        } catch (IllegalAccessException e) {
	            // Should not happen.
	            Log.w("ApiDemos", "Unable to invoke stopForeground", e);
	        }
	    } else
	    if( mSetForegroundArgs != null ) {
	        mSetForegroundArgs[0] = Boolean.FALSE;
	        invokeMethod(mSetForeground, mSetForegroundArgs);
	    }

		// 通知を消去
		NotificationManager mNM = (NotificationManager) TimerService.this.getSystemService(Context.NOTIFICATION_SERVICE);
		mNM.cancel(timer_id);
	}

	/**
	 * 録音中かどうか返します。
	 * @param timerID
	 */
	public boolean isPauseRec(int timerID) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "pauseRec start"); }
			synchronized( mTimerList ) {
				for (TimerItem i : mTimerList) {
					if (i.getmItemId() == timerID) {
						return i.isPause();
					}
				}
			}
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "pauseRec end"); }
		}
		return false;
	}
	/**
	 * 録音を一時停止する。
	 * @param timerID
	 */
	public void pauseRec(int timerID) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "pauseRec start"); }
			synchronized( mTimerList ) {
				for (TimerItem i : mTimerList) {
					if (i.getmItemId() == timerID) {
						i.pauseRec();
					}
				}
			}
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "pauseRec end"); }
		}
	}
	/**
	 * 録音を再開する。
	 * @param timerID
	 */
	public void resumeRec(int timerID) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "resumeRec start"); }
			synchronized( mTimerList ) {
				for (TimerItem i : mTimerList) {
					if (i.getmItemId() == timerID) {
						i.resumeRec();
					}
				}
			}
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "resumeRec end"); }
		}
	}
	
	
	class MyTimerTask extends TimerTask {
		private int mTimerID = -1;

		public MyTimerTask(int itemID) {
			mTimerID = itemID;
		}

		@Override
		public void run() {
			TimerItem item = null;

			for (TimerItem i : mTimerList) {
				if (i.getmItemId() == mTimerID) {
					item = i;
				}
			}
			if (item == null) {
				return;
			}

//			NotificationManager mNM = (NotificationManager) TimerService.this.getSystemService(Context.NOTIFICATION_SERVICE);
//			mNM.cancel(item.getmItemId());

			Intent intent = new Intent(ACTION);
			intent.putExtra("ITEM_ID", item.getmItemId());

			sendBroadcast(intent);
			
			
			//
			// ホームスクリーンを表示する。
			//
			/*
			Android 2.1（API Level 7）までは「ActivityManager#restartPackage」で自プロセスを終了することができた。しかし、Android 2.2（API Level 8）以降で新しく定義された「ActivityManager#killBackgroundProcesses」では、たとえ自プロセスであっても『プロセスがフォアグラウンドの時には終了することができない』（メソッドを呼び出しても何も起こらない）。また、API Level 8以降restartPackageはkillBackgroundProcessesのただのラッパーとなったため、API Level 7でrestartPackageによりフォアグラウンドの自プロセスを終了できたのを、API Level 8では期待することができない。

			この挙動に気付いたのは、Android 2.1向けに作った自分自身を終了する処理を持ったアプリを、Android 2.2に対応する作業を行っていたときである。これには本当に困ったのだが、すっきりと解決する方法が思いつかなかった。妥協策として思いついたのは、『自プロセスがフォアグラウンドの時には終了することができない』という点に着目し、「だったらkillBackgroundProcessesを呼ぶ前に自分自身でバックグラウンドプロセスになって、頃合いを見計らってkillBackgroundProcessesを呼べばいいのでは？」という策である。具体的には、
			・ホーム画面を表示するインテント（Action: Intent.ACTION_MAIN / Category: Intent.CATEGORY_HOME）を投げる
			・ホーム画面が表示される程度にThread.sleepで待つ
			・killBackgroundProcesses(getPackageName())を呼ぶ
			*/
	        Intent setIntent = new Intent(Intent.ACTION_MAIN);
	        setIntent.addCategory(Intent.CATEGORY_HOME);
	        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        TimerService.this.getApplicationContext().startActivity(setIntent); 
			
			
	        
	        //
	        // 非同期タスクを実行して、プロセスを削除
	        //
	        new MyAsyncTask(TimerService.this, item.getmItemId()).myDo();
			

	        // 通知を非表示にする
	        TimerService.this.hideNotify( item.getmItemId() );
	        
			item.release();

			mTimerList.remove(item);

			item = null;

		}

	}
	
	
	void invokeMethod(Method method, Object[] args) {
	    try {
	        mStartForeground.invoke(this, mStartForegroundArgs);
	    } catch (InvocationTargetException e) {
	        // Should not happen.
	        //Log.w("ApiDemos", "Unable to invoke method", e);
	    } catch (IllegalAccessException e) {
	        // Should not happen.
	        //Log.w("ApiDemos", "Unable to invoke method", e);
	    }
	}


}







//
//	タイムアップ非同期タスク
//
class MyAsyncTask  {
	final static String TAG = "MyAsyncTask";

	private	Context	mContext = null;
	private int		mItemID = -1;
	
	public MyAsyncTask(Context ctx, int itemID) {
		mContext = ctx;
		mItemID = itemID;
	}
	
	public void myDo() {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "myDo start"); }
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
			// Remote Operation
			if( item.isEnableRemoteOpe() && item.getRemoteOpeComputer() != null ) {
				ZzzSvcClient.COMMAND command = ZzzSvcClient.getCommandIndex( item.getRemoteOpe() );
				try {
					if( MyLog.isDebugMod() ) { 
						MyLog.logt(TAG, "Host Name  : [" + item.getRemoteOpeComputer().getHostname() + "]");
						MyLog.logt(TAG, "Port Number: " + item.getRemoteOpeComputer().getZzz_tcp_port());
						MyLog.logt(TAG, "Command    : " + ZzzSvcClient.COMMAND.CMD_HIBERNATE.toString());
					}
					ZzzSvcClient client = new ZzzSvcClient();
					client.shutdown(
							item.getRemoteOpeComputer().getHostname(), 
							item.getRemoteOpeComputer().getZzz_tcp_port(), 
							command, 
							10, 
							"ZzzTimer Remote Operation", 
							0, 
							0, 
							item.getRemoteOpeComputer().getExec_image(), 
							item.getRemoteOpeComputer().getExec_params(), 
							item.getRemoteOpeComputer().getExec_cur_dir());
				} catch (Exception e) {
					MyLog.loge(TAG, e);
				}
			}
			
			
			// プロセスをキル
			ActivityManager activityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
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
			
//			PackageManager packageManager = getPackageManager();
			if(runningApp != null) {
				for(RunningAppProcessInfo app : runningApp) {			                        
					String strProcName = app.processName;
					int pos = strProcName.indexOf(":");
					if( pos != -1 ) {
						strProcName = strProcName.substring(0, pos);
//						Log.d(TAG, "APP : " + app.processName + "   Remote : " +  strProcName);
					} else {
//						Log.d(TAG, "APP : " + app.processName);
					}
										
					
					if( item.isExistKillApp(strProcName)) {
						killProc(activityManager, app.pid, strProcName);		                
					}
					
				}
			}
			
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "myDo end"); }
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
		
        
//        try {
//	        String [] theArgs = new String [3];
//            
//            theArgs[0] = "su";
//            theArgs[2] = "kill";
//            theArgs[3] = "" + pid;		                
//
//            Log.d(TAG, " kill command : " +  pid);
//        	Process proc =Runtime.getRuntime().exec(theArgs);
////            InputStream is = proc.getInputStream();
////            BufferedReader br = new BufferedReader(new InputStreamReader(is));
////            String line;
////            while ((line = br.readLine()) != null) {
////            	Log.d(TAG, line);
////            }
//        	proc.wait();
//        } catch (Exception e) {
//        	Log.e(TAG, e.getMessage());
//        	e.printStackTrace();
//        }
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