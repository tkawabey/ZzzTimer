package four.non.bronds.yyys.zzztimer.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;


public abstract class PCMRecord implements Runnable{
	static final String TAG = "PCMRecord";
	// DTFMオブジェクト
	protected DTFM	mDTFM;
    // 録音中フラグ
	protected boolean isRecoding = false;
	protected int		mBuffSize = 0;
	protected long		mStartTime = 0;
	protected long		mPausedDelta = 0;
	protected long		mPausedStart = 0;
	protected boolean	mPause = false;


	public abstract void recodStart();
	public abstract void receiveBuff(int size, byte []buf);
	public abstract void recodEnd(long elapseTime);
	

	public PCMRecord(DTFM dtfm) throws Exception
	{
		mDTFM = dtfm;
		
		
        // 必要なバッファ数
		mBuffSize = AudioRecord.getMinBufferSize(
        		mDTFM.getSampleBitPerSec(),
        		mDTFM.getChanel() == 1 ?  AudioFormat.CHANNEL_CONFIGURATION_MONO  : AudioFormat.CHANNEL_CONFIGURATION_STEREO,
        		mDTFM.getSampleBits() == 8 ?  AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT);
		
		if( mBuffSize < 0 ) {
			throw new Exception("");
		}
	}
// プロパティ
	public int getBuffSize() {
		return mBuffSize;
	}	
	public long getStartTime() {
		return mStartTime;
	}
	/**
	 * 録音した時間
	 * @return
	 */
	public long getRecordedTime() {
		if( mPause == true ) {
			return mPausedStart - mStartTime - mPausedDelta;
		} else {
			return SystemClock.elapsedRealtime() - mStartTime - mPausedDelta;
		}
	}
	public boolean isPause() {
		return mPause;
	}
	public boolean pause() {
		if( mPause == true ) {
			return false;
		}
		mPause = true;
		mPausedStart = SystemClock.elapsedRealtime();
		return true;
	}
	public boolean resume() {
		if( mPause == false ) {
			return false;
		}
		mPausedDelta += (SystemClock.elapsedRealtime()-mPausedStart);
		mPause = false;
		return true;
	}
	
	
// メソッド
    // 録音を停止
    public void stop() {
    	isRecoding = false;
    }
    public boolean isRecording() {
    	return isRecoding;
    }
	@Override
	public void run() {
		try{
	    	android.os.Process.setThreadPriority(
	                android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

	    	if( MyLog.isDebugMod() ) {
				MyLog.logt(TAG, "getSampleBitPerSec:" + mDTFM.getSampleBitPerSec());
				MyLog.logt(TAG, "getSampleBits:" + mDTFM.getSampleBits());
				MyLog.logt(TAG, "getSingnalMsec:" + mDTFM.getSingnalMsec());
				MyLog.logt(TAG, "getChanel:" + mDTFM.getChanel());
				MyLog.logt(TAG, "mBuffSize:" + mBuffSize);
	    	}
			
	        AudioRecord audioRecord = new AudioRecord(
	                MediaRecorder.AudioSource.MIC,
	                mDTFM.getSampleBitPerSec(),
	                mDTFM.getChanel() == 1 ?  AudioFormat.CHANNEL_CONFIGURATION_MONO  : AudioFormat.CHANNEL_CONFIGURATION_STEREO,
	                mDTFM.getSampleBits() == 8 ?  AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT,
	                		mBuffSize);
	        
	        
	        byte[] buffer = new byte[mBuffSize];
	        
	        audioRecord.startRecording();
	        mStartTime = SystemClock.elapsedRealtime();
	        recodStart();
	        isRecoding = true;
				
	        while(isRecoding) {
	            int readed = audioRecord.read(buffer, 0, mBuffSize);
	            if( readed > 0 && mPause == false ) {
	            	receiveBuff(readed, buffer);
	            }
	        }
	        if( mPause == true ) {
	        	resume();
	        }
	        recodEnd( SystemClock.elapsedRealtime() - mStartTime - mPausedDelta );
	        

	        audioRecord.stop();
	        audioRecord.release();
		} catch(IllegalArgumentException ex) {
			MyLog.loge(TAG, ex);
		} catch(SecurityException ex) {
			MyLog.loge(TAG, ex);
		} catch(IllegalStateException ex) {
			MyLog.loge(TAG, ex);
		}
	}	
}

