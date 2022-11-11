package four.non.bronds.yyys.zzztimer.util;

import java.util.Locale;

import four.non.bronds.yyys.zzztimer.cmn.Constant;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefereceAcc {
	private static final String TAG = "PrefereceAcc";
	private SharedPreferences	mSp;
	private String				mLocal = "en";
	
	
	public enum HELP_URL
	{
		HELP_URL_MAIN,
		HELP_URL_TIMER,
		HELP_URL_COMPUTER,
		HELP_URL_COMPUTER_ADD,
		HELP_URL_COMPUTER_OPE,
		HELP_URL_PLAYLIST,
		HELP_URL_RECLIST,
		HELP_URL_REC_PALY,
		
	}




	public PrefereceAcc(Context ctx) {
		mSp = ctx.getSharedPreferences(Constant.SHARED_PREF, android.content.Context.MODE_WORLD_READABLE | android.content.Context.MODE_WORLD_WRITEABLE );
		Locale loc = Locale.getDefault();
		if( loc.getLanguage().equals("ja") ) {
			mLocal = loc.getLanguage(); 
		}
	}
	
	public String getLocal() {
		return mLocal;
	}
	
	public String getHelpURL(HELP_URL h) {
		String strURL = "https://sites.google.com/site/zzztimerjp/";
		if( mLocal.compareTo("ja") == 0 ) {
			switch(h) {
			case HELP_URL_MAIN:
				strURL = "https://sites.google.com/site/zzztimerjp/";
				break;
			case HELP_URL_TIMER:
				strURL = "https://sites.google.com/site/zzztimerjp/home/timer/";
				break;
			case HELP_URL_COMPUTER:
				strURL = "https://sites.google.com/site/zzztimerjp/home/computer";
				break;
			case HELP_URL_COMPUTER_ADD:
				strURL = "https://sites.google.com/site/zzztimerjp/home/computer/add_computer";
				break;
			case HELP_URL_COMPUTER_OPE:
				strURL = "https://sites.google.com/site/zzztimerjp/home/computer/remoteope";
				break;
			case HELP_URL_PLAYLIST:
				strURL = "https://sites.google.com/site/zzztimerjp/home/music-playlist";
				break;
			case HELP_URL_RECLIST:
				strURL = "https://sites.google.com/site/zzztimerjp/home/record-list";
				break;
			case HELP_URL_REC_PALY:
				strURL = "https://sites.google.com/site/zzztimerjp/home/record-list/pray_recfile";
				break;
			}
		} else {
			switch(h) {
			case HELP_URL_MAIN:
				strURL = "https://sites.google.com/site/zzztimeren";
				break;
			case HELP_URL_TIMER:
				strURL = "https://sites.google.com/site/zzztimeren/home/timer/";
				break;
			case HELP_URL_COMPUTER:
				strURL = "https://sites.google.com/site/zzztimeren/home/computer";
				break;
			case HELP_URL_COMPUTER_ADD:
				strURL = "https://sites.google.com/site/zzztimeren/home/computer/add_computer";
				break;
			case HELP_URL_COMPUTER_OPE:
				strURL = "https://sites.google.com/site/zzztimeren/home/computer/remoteope";
				break;
			case HELP_URL_PLAYLIST:
				strURL = "https://sites.google.com/site/zzztimeren/home/music-playlist";
				break;
			case HELP_URL_RECLIST:
				strURL = "https://sites.google.com/site/zzztimeren/home/record-list";
				break;
			case HELP_URL_REC_PALY:
				strURL = "https://sites.google.com/site/zzztimeren/home/record-list/pray_recfile";
				break;
			}			
		}
		return strURL;
	}
	
	
	
	/**
	 * Playlistや、ArtistをIN-Strageから検索するか？
	 * @return
	 */
	public String getMusicDBSearchLoc() {
		String strWhere = mSp.getString(Constant.SHARED_PREF_DBG_PLAYLIST_WHERE, "0");
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "" + Constant.SHARED_PREF_DBG_PLAYLIST_WHERE + " : " + strWhere ); }
		if( strWhere != null ) {
			if( strWhere.compareTo("0") == 0 ) {
				strWhere = MusicUtils.SPECIAL_VAL_WHERE_EX;
			} else 
			if( strWhere.compareTo("1") == 0 ) {
				strWhere = MusicUtils.SPECIAL_VAL_WHERE_EX;
			} else {
				strWhere = null;
			}
		}
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "" + Constant.SHARED_PREF_DBG_PLAYLIST_WHERE + " : " + strWhere ); }
		return strWhere;
	}
	
	public int getRecFormat() {
		int ret = Constant.REC_FORMAT_PCM;
		String strRecFormat = mSp.getString(Constant.SHARED_PREF_REC_FORMAT, "0");
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "" + Constant.SHARED_PREF_REC_FORMAT + " : " + strRecFormat ); }
		if( strRecFormat != null ) {
			if( strRecFormat.compareTo("0") == 0 ) {
				ret = Constant.REC_FORMAT_PCM;
			} else 
			if( strRecFormat.compareTo("1") == 0 ) {
				ret = Constant.REC_FORMAT_OGG;
			}
		}
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "" + Constant.SHARED_PREF_REC_FORMAT + " : " + ret ); }
		return ret;
	}

	public int getMusicVolume() {
		int b = mSp.getInt(Constant.SHARED_PREF_PLAYMUSIC_VOLUM, 10);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_PLAYMUSIC_VOLUM + " : " + b ); }
		return b;
	}

	public boolean getLogDbgmode() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_DBGMODE, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_DBGMODE + " : " + b ); }
		return b;
	}
	public boolean getLogOptErr() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_ERR, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_OPT_ERR + " : " + b ); }
		return b;
	}
	public boolean getLogOptFunc() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_FUNC, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_OPT_FUNC + " : " + b ); }
		return b;
	}
	public boolean getLogOptInf() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_INF, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_OPT_INF + " : " + b ); }
		return b;
	}
	public boolean getLogOptTrace() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_TRACE, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_OPT_TRACE + " : " + b ); }
		return b;
	}
	public boolean getLogOptSEQ() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_SEQ, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_OPT_SEQ + " : " + b ); }
		return b;
	}
	public boolean getLogOptSQL() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_SQL, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_OPT_SQL + " : " + b ); }
		return b;
	}
	public boolean getLogOptWriteFile() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_W_FILE, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_OPT_W_FILE + " : " + b ); }
		return b;
	}
	public boolean getLogOptWriteCat() {
		boolean b = mSp.getBoolean(Constant.SHARED_PREF_DBG_LOG_OPT_W_CAT, false);
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_OPT_W_CAT + " : " + b ); }
		return b;
	}
	public String getLogFileName() {
		String str = mSp.getString(Constant.SHARED_PREF_DBG_LOG_FILE_NAME, "log.log");
		if( MyLog.isDebugMod()) { MyLog.logt(TAG, "SharedPref." + Constant.SHARED_PREF_DBG_LOG_FILE_NAME + " : " + str ); }
		return str;
	}


}
