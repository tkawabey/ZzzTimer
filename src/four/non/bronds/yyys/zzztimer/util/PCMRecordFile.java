package four.non.bronds.yyys.zzztimer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import four.non.bronds.yyys.zzztimer.bean.MusicTagBean;
import four.non.bronds.yyys.zzztimer.bean.PurchaseInf;
import four.non.bronds.yyys.zzztimer.bean.RecFileInf;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.PurchaseAccessor;



/**
 * Voice Recorder
 * @author user
 *
 */
public class PCMRecordFile extends PCMRecord {
    private String				mStrageDir;
    private String				mFileName;
    private FileOutputStream	mFos = null;
    private int					mTotalSize = 0;
	protected Date				mStateDate = null;
	protected OnRecodListener	mOnRecodListener = null;
    private Context				mContext;
    private int					mFormat = 0;
    private boolean				mBuyOGGLicense = false;
    final static public long	NO_LICENSE_REC_OGG_TIME = 1000*60*3;



	public interface OnRecodListener {
    	public void onRecodeEnd(RecFileInf rcf);
    }
	
	
    /**
     * コンストラクタ
     * 
     * @param ctx
     * @param dtfm
     * @param strageDir
     * @param strFileName
     * @param format
     * @throws Exception
     */
    public PCMRecordFile(Context ctx, DTFM dtfm, String strageDir, String strFileName, int format)
			throws Exception {
		super(dtfm);

		mContext = ctx;
		mStrageDir = strageDir;
		mFileName = strFileName;
		mFormat = format;
		
		//　ライセンス情報を取得
		PurchaseInf license = new PurchaseAccessor(ctx).getLicenseInf();
		if( license != null ) {
			mBuyOGGLicense = license.isOgg();
		}
	}



 // プロパティ
 	public String getStrageDir() {
 		return mStrageDir;
 	}
 	public String getFileName() {
 		return mFileName;
 	}
    public OnRecodListener getOnRecodListener() {
		return mOnRecodListener;
	}
	public void setOnRecodListener(OnRecodListener mOnRecodListener) {
		this.mOnRecodListener = mOnRecodListener;
	}	
	public int getFormat() {
		return mFormat;
	}
	
	
	
	/* (non-Javadoc)
	 * @see four.non.bronds.yyys.zzztimer.util.PCMRecord#recodStart()
	 */
	public void recodStart()
	{
		mTotalSize = 0;
		mStateDate = new Date();
		File fnTmp = new File(mStrageDir + "/" + mFileName);
				
		try {
			if( mFormat == Constant.REC_FORMAT_PCM ) {
				mFos = new FileOutputStream(fnTmp);
				byte [] headerByte = mDTFM.getWaveFileHeader(mTotalSize);
				mFos.write(headerByte);
			} else
			if( mFormat == Constant.REC_FORMAT_OGG ) {
				MusicTagBean[] t = { 
						new MusicTagBean("ENCODER", "ZzzTimer Encode"), 
						new MusicTagBean("ARTIST", "ZzzTimer"),
						new MusicTagBean("ALBUM", Formmater.getYYYYMMDD(mStateDate)),
						new MusicTagBean("TITLE", "Rec-" + Formmater.getYYYYMMDDhhmmss(mStateDate))
				};
				mDTFM.openWriteOgg( fnTmp.getAbsolutePath(), t);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			try {
				fnTmp = new File(mStrageDir + "/" + mFileName);
				fnTmp.delete();
			} catch(Exception e2) {
				// ignore
			}
		} catch (IOException e) {
			e.printStackTrace();
			errorOnDeleteDile();
		} catch (Exception e) {
			MyLog.loge(TAG, e);
			e.printStackTrace();
			errorOnDeleteDile();
		}
	}	
	/* (non-Javadoc)
	 * @see four.non.bronds.yyys.zzztimer.util.PCMRecord#receiveBuff(int, byte[])
	 */
	public void receiveBuff(int size, byte []buf)
	{
		try {
			if( mFormat == Constant.REC_FORMAT_PCM ) {
				mFos.write(buf, 0, size);
				mTotalSize += size;	
			} else
			if( mFormat == Constant.REC_FORMAT_OGG ) {
				mDTFM.writeOgg(buf, size);
				mTotalSize += size;	
				if( mBuyOGGLicense == false ) {
					if( this.getRecordedTime() > NO_LICENSE_REC_OGG_TIME ) {
						this.stop();
					}
				}
			}		
		} catch (IOException e) {
			e.printStackTrace();
			errorOnDeleteDile();
		} catch (Exception e) {
			MyLog.loge(TAG, e);
			e.printStackTrace();
			errorOnDeleteDile();
		}
	}
	
	/* (non-Javadoc)
	 * @see four.non.bronds.yyys.zzztimer.util.PCMRecord#recodEnd(long)
	 */
	public void recodEnd(long elapseTime)
	{
		try {
			if( mFormat == Constant.REC_FORMAT_PCM ) {
				mFos.close();
				String strTitle = "Rec-" + Formmater.getYYYYMMDDhhmmss(mStateDate);
				MusicTagBean[] t = { 
						new MusicTagBean("IART", "ZzzTimer"),	// IART(アーティスト:artist name)
						new MusicTagBean("IPRD", Formmater.getYYYYMMDD(mStateDate)),	// IPRD(アルバム:製品:associated product)
						new MusicTagBean("INAM", strTitle),		// INAM(名前:Name)
						new MusicTagBean("ISBJ", strTitle),		// ISBJ(タイトル:subject description)
						new MusicTagBean("ICMT", "ZzzTimer Recorded"),	// ICMT(コメント:comments)
						new MusicTagBean("ICOP", "copyright ZzzTimer"),	// ICOP(著作権:copyright)
						new MusicTagBean("ICRD", Formmater.getYYYYMMDDhhmmss(mStateDate)),	// ICRD(作成日:creation date)
						new MusicTagBean("IENG", "ZzzTimer"),	// IENG(ファイルを作った人:engineer)
						new MusicTagBean("ISFT", "ZzzTimer"),	// ISFT(ソフトウェア:software used)
						
				};
				
				int ret = mDTFM.updateWaveFileHeaderFile(mTotalSize, mStrageDir + "/" + mFileName, t);
				if( ret != 0 ) {
					// Error
				}
				
				
				ContentResolver cr = mContext.getContentResolver();
				ContentValues values = new ContentValues();  
				values.put(MediaStore.Audio.Media.TITLE, "Rec-" + Formmater.getYYYYMMDDhhmmss(mStateDate));  
				values.put(MediaStore.Audio.Media.DATA, mStrageDir + "/" + mFileName);
				values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/x-wav");
				values.put(MediaStore.Audio.Media.DURATION, elapseTime);
				values.put(MediaStore.Audio.Media.YEAR, mStateDate.getYear());
				values.put(MediaStore.Audio.Media.SIZE, mTotalSize);
				values.put(MediaStore.Audio.Media.ALBUM, Formmater.getYYYYMMDD(mStateDate));
				values.put(MediaStore.Audio.Media.ARTIST, "ZzzTimer");
				values.put(MediaStore.Audio.Media.TRACK, (long)0);
				cr.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
				
				try {
					int albumID = -1;
					int numOfSongs = 0;
					Cursor cursor;
					cursor = mContext.getContentResolver().query(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,
							new String[] {
									MediaStore.Audio.Media.ALBUM_ID
							},
							MediaStore.Audio.Media.ARTIST + "=? AND " + MediaStore.Audio.Media.ALBUM + "=?" ,
							new String[] {"ZzzTimer", Formmater.getYYYYMMDD(mStateDate)},
							null);
					if( cursor != null ) {
						while( cursor.moveToNext() ){
							albumID = cursor.getInt(0);
							MyLog.logt(TAG, "albumID:" + albumID);
							numOfSongs++;
						}
						cursor.close();
					}
					if( albumID != -1) {
						values = new ContentValues(); 
						values.put(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS, numOfSongs);
						cr.update(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
								values, 
								MediaStore.Audio.AlbumColumns.ALBUM_ID + "=?", 
								new String[]{""+albumID});
					}
					
					
				} catch(Exception e) {
					MyLog.loge(TAG, e);
				}
				//cr.update(uri, values, where, selectionArgs)
				
			} else
			if( mFormat == Constant.REC_FORMAT_OGG ) {
				mDTFM.closeWriteOgg();
			}
			
			// 実際のファイルのサイズを取得
			try {
				File file = new File(mStrageDir + "/" + mFileName );
				mTotalSize = (int)file.length();
			} catch(Exception e) {
				MyLog.loge(TAG, e);
			}
			
			// コールバックに通知			
	        if( mOnRecodListener != null ) {
	        	RecFileInf rcf = new RecFileInf();
	        	rcf.setFName(  mStrageDir + "/" + mFileName );
	        	rcf.setDisp( mFileName );
	        	rcf.setRecDate(mStateDate);
	        	rcf.setTime( elapseTime );
	        	rcf.setSize( mTotalSize );
	        	rcf.setBitsPer( mDTFM.getSampleBitPerSec() );
	        	rcf.setBit( mDTFM.getSampleBits() );
	        	rcf.setChanel( mDTFM.getChanel() );
	        	rcf.setType( mFormat );
	        	mOnRecodListener.onRecodeEnd(rcf);
	        }
		} catch (IOException e) {
			e.printStackTrace();
			errorOnDeleteDile();
		} catch (Exception e) {
			MyLog.loge(TAG, e);
			e.printStackTrace();
			errorOnDeleteDile();
		}
	}
    

	
	/**
	 * エラー発生時にレコードファイルを削除する。
	 */
	private void errorOnDeleteDile() {
		if( mFormat == Constant.REC_FORMAT_PCM ) {
			if( mFos != null ) {
				try {
					mFos.close();
				} catch(Exception e2) {
					// ignore
				}
			}
		} else
		if( mFormat == Constant.REC_FORMAT_OGG ) {
			try {
				mDTFM.closeWriteOgg();
			} catch(Exception e2) {
				// ignore
			}
		}
		try {
			File fnTmp = new File(mStrageDir + "/" + mFileName);
			fnTmp.delete();
		} catch(Exception e2) {
			// ignore
		}
	}
}
