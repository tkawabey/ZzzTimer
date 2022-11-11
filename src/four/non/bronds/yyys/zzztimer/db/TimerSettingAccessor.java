package four.non.bronds.yyys.zzztimer.db;

import java.util.ArrayList;
import java.util.List;

import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingCloseAppBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingStartApp;
import four.non.bronds.yyys.zzztimer.util.MyLog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;



public class TimerSettingAccessor {
	private static final String TAG = "TimerSettingAccessor";
	private Context		mContext = null;
	
	public TimerSettingAccessor(Context ctx) {
		mContext = ctx;
	}
	
	/**
	 * タイマーアイテムの一覧をロードする。
	 * @return
	 */
	public List<TimerSettingBean> load(){
		List<TimerSettingBean> contaiar = new ArrayList<TimerSettingBean>();
		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		Cursor c = null;
		int length = 0, i;
		
		
		try {
			db = dbOpnner.getReadableDatabase();
			StringBuilder sb = new StringBuilder();
			sb.append("select  item_id "); // 0
			sb.append("  ,  item_name "); // 1
			sb.append("  ,  wifi_lock "); // 2
			sb.append("  ,  power_lock "); // 3
			sb.append("  ,  hour "); // 4
			sb.append("  ,  minite "); // 5
			sb.append("  ,  time_opts "); // 6
			sb.append("  ,  ring "); // 7
			sb.append("  ,  ring_opt "); // 8
			sb.append("  ,  audio "); // 9
			sb.append("  ,  audio_opt "); // 10
			sb.append("  ,  bluetooth "); // 11
			sb.append("  ,  bluetooth_opt "); // 12
			sb.append("  ,  GPS "); // 13
			sb.append("  ,  GPS_opt "); // 14
			sb.append("  ,  pcm_rec "); // 15
			sb.append("  ,  wol_c_id "); // 16
			sb.append("  ,  wol_repeat "); // 17
			sb.append("  ,  remote_ope_c_id "); // 18
			sb.append("  ,  remote_ope "); // 19
			sb.append("  ,  music_play "); // 20
			sb.append("  ,  music_play_id "); // 21
			sb.append("  ,  music_shuffle "); // 22
			sb.append("  ,  music_scrib "); // 23
			sb.append("  ,  music_name "); // 24
			sb.append(" from TIMER_ITEM");
			c = db.rawQuery( sb.toString(), null);
			
			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				TimerSettingBean item = new TimerSettingBean();
				int index = 0;
				item.setID(c.getInt(index++));
				item.setName(c.getString(index++));
				item.setWifiLock(c.getInt(index++)!=0);
				item.setPoweriLock(c.getInt(index++)!=0);
				item.setHour(c.getInt(index++));
				item.setMinite(c.getInt(index++));
				item.setTimeOpt(c.getString(index++));
				item.setmRing(c.getString(index++));
				item.setmRingOpt(c.getString(index++));
				item.setmAudio(c.getInt(index++));
				item.setAudioOpt(c.getString(index++));
				item.setBluetooth(c.getInt(index++));
				item.setBluetoothOpt(c.getString(index++));
				item.setGPS(c.getInt(index++));
				item.setGPSOpt(c.getString(index++));
				item.setRecord(c.getInt(index++)!=0);
				item.setWolComputerID(c.getInt(index++));
				item.setWolRepeat(c.getInt(index++));
				item.setRemoteOpeComputerID(c.getInt(index++));
				item.setRemoteOpe(c.getInt(index++));
				
				
				item.setMusic(c.getInt(index++)!=0);
				item.setMusicID(c.getInt(index++));
				item.setMusicShuffle(c.getInt(index++)!=0);
				item.setMusicScrib(c.getInt(index++)!=0);
				item.setMusicName(c.getString(index++));
				
				MyLog.logt("", "isMusic" + item.isMusic());
				MyLog.logt("", "getMusicID" + item.getMusicID());
				MyLog.logt("", "getMusicName" + item.getMusicName());

				
				
								
				contaiar.add( item );
				
				c.moveToNext();
			}
			c.close();
			c = null;
			
			
			
			for(TimerSettingBean item : contaiar ) {
				// WOLのコンピューターをロード
				if( item.isEnableWOL() ) {
					item.setWOLComputer( ComputerAccessor.getByID(db, item.getWolComputerID()) );
				} else {
					item.setWOLComputer(null);
				}
				// リモートオペレーションのコンピューターをロード
				if( item.isEnableRemoteOpe() ) {
					item.setRemoteOpeComputer( ComputerAccessor.getByID(db, item.getRemoteOpeComputerID()) );
				} else {
					item.setRemoteOpeComputer(null);
				}
			}
		} finally {
			if(c != null ) {
				try { c.close(); } catch(Exception e) {}
			}
			if( db != null ) {
				try { db.close(); } catch(Exception e) {}
			}
		}
		
		
		for(TimerSettingBean item : contaiar) {
			loadDetail(item);
		}
		
		return contaiar;
	}
	
	/**
	 * 詳細のロード
	 * @param item
	 */
	public void loadDetail(TimerSettingBean item) {
		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		Cursor c = null;
		int length = 0, i;
		
		
		try {
			db = dbOpnner.getReadableDatabase();
			item.getListCloseApp().clear();
			item.getListStartApp().clear();
			
			StringBuilder sb = new StringBuilder();
			sb.append("select  item_name "); // 1
			sb.append("  ,  wifi_lock "); // 2
			sb.append("  ,  power_lock "); // 3
			sb.append("  ,  hour "); // 4
			sb.append("  ,  minite "); // 5
			sb.append("  ,  time_opts "); // 6
			sb.append("  ,  ring "); // 7
			sb.append("  ,  ring_opt "); // 8
			sb.append("  ,  audio "); // 9
			sb.append("  ,  audio_opt "); // 10
			sb.append("  ,  bluetooth "); // 11
			sb.append("  ,  bluetooth_opt "); // 12
			sb.append("  ,  GPS "); // 13
			sb.append("  ,  GPS_opt "); // 14
			sb.append("  ,  pcm_rec "); // 15
			sb.append("  ,  wol_c_id "); // 16
			sb.append("  ,  wol_repeat "); // 17
			sb.append("  ,  remote_ope_c_id "); // 18
			sb.append("  ,  remote_ope "); // 19
			sb.append("  ,  music_play "); // 20
			sb.append("  ,  music_play_id "); // 21
			sb.append("  ,  music_shuffle "); // 22
			sb.append("  ,  music_scrib "); // 23
			sb.append("  ,  music_name "); // 24
			sb.append(" from TIMER_ITEM");
			sb.append(" where item_id=").append(item.getID()); // 19
			c = db.rawQuery( sb.toString(), null);
			
			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				int index = 0;
				item.setName(c.getString(index++));
				item.setWifiLock(c.getInt(index++)!=0);
				item.setPoweriLock(c.getInt(index++)!=0);
				item.setHour(c.getInt(index++));
				item.setMinite(c.getInt(index++));
				item.setTimeOpt(c.getString(index++));
				item.setmRing(c.getString(index++));
				item.setmRingOpt(c.getString(index++));
				item.setmAudio(c.getInt(index++));
				item.setAudioOpt(c.getString(index++));
				item.setBluetooth(c.getInt(index++));
				item.setBluetoothOpt(c.getString(index++));
				item.setGPS(c.getInt(index++));
				item.setGPSOpt(c.getString(index++));
				item.setRecord(c.getInt(index++)!=0);
				item.setWolComputerID(c.getInt(index++));
				item.setWolRepeat(c.getInt(index++));
				item.setRemoteOpeComputerID(c.getInt(index++));
				item.setRemoteOpe(c.getInt(index++));
				
				item.setMusic(c.getInt(index++)!=0);
				item.setMusicID(c.getInt(index++));
				item.setMusicShuffle(c.getInt(index++)!=0);
				item.setMusicScrib(c.getInt(index++)!=0);
				item.setMusicName(c.getString(index++));

				c.moveToNext();
			}
			c.close();
			c = null;
			
			
			c = db.rawQuery("select app from TIMER_CLOSE_APP where item_id=" + item.getID(), null);
			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				item.addClosseApp(c.getString(0));
				c.moveToNext();
			}
			c.close();
			c = null;
			
			c = db.rawQuery("select app from START_APP where item_id=" + item.getID(), null);
			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				item.addStartApp(c.getString(0));
				c.moveToNext();
			}
			c.close();
			c = null;
			
			// WOLのコンピューターをロード
			if( item.isEnableWOL() ) {
				item.setWOLComputer( ComputerAccessor.getByID(db, item.getWolComputerID()) );
			} else {
				item.setWOLComputer(null);
			}
			// リモートオペレーションのコンピューターをロード
			if( item.isEnableRemoteOpe() ) {
				item.setRemoteOpeComputer( ComputerAccessor.getByID(db, item.getRemoteOpeComputerID()) );
			} else {
				item.setRemoteOpeComputer(null);
			}
		} finally {
			if(c != null ) {
				try { c.close(); } catch(Exception e) {}
			}
			if( db != null ) {
				try { db.close(); } catch(Exception e) {}
			}
		}
	}
	
	
	public boolean add(TimerSettingBean item) {

		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		Cursor c = null;
		SQLiteStatement stmt = null;
		int length = 0, i;
		int item_id = 0;
		int index = 0;
		
		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();
			
			c = db.rawQuery("select max(item_id) from TIMER_ITEM", null);
			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				item_id = c.getInt(0);
				c.moveToNext();
			}
			c.close();
			
			item_id++;
			item.setID(item_id);
			
			index = 1;
			stmt = db.compileStatement("insert into TIMER_ITEM values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.bindLong(index++, item_id);
			stmt.bindString(index++, item.getName());
			stmt.bindLong(index++, item.isWifiLock() ? 1 : 0);
			stmt.bindLong(index++, item.isPoweriLock() ? 1 : 0);
			stmt.bindLong(index++, item.getHour());
			stmt.bindLong(index++, item.getMinite());
			stmt.bindString(index++, item.getTimeOpt());
			stmt.bindString(index++, item.getRing());
			stmt.bindString(index++, item.getRingOpt());
			stmt.bindLong(index++, item.getmAudio());
			stmt.bindString(index++, item.getAudioOpt());
			stmt.bindLong(index++, item.getBluetooth());
			stmt.bindString(index++, item.getBluetoothOpt());
			stmt.bindLong(index++, item.getGPS());
			stmt.bindString(index++, item.getGPSOpt());
			stmt.bindLong(index++, item.isRecord() ? 1 : 0);
			
			stmt.bindLong(index++, item.getWolComputerID());
			stmt.bindLong(index++, item.getWolRepeat());
			stmt.bindLong(index++, item.getRemoteOpeComputerID());
			stmt.bindLong(index++, item.getRemoteOpe());
			
			stmt.bindLong(index++, item.isMusic() ? 1 : 0);
			stmt.bindLong(index++, item.getMusicID());
			stmt.bindLong(index++, item.isMusicShuffle() ? 1 : 0);
			stmt.bindLong(index++, item.isMusicScrib() ? 1 : 0);
			stmt.bindString(index++, item.getMusicName());
			
			stmt.executeInsert();
			
			
			this.updateKillApps(db, item);
			this.updateStartApps(db, item);
			this.updateWOL(db, item);
			
			db.setTransactionSuccessful();
		} finally {
			if(c != null ) {
				try { c.close(); } catch(Exception e) {}
			}
			if(stmt != null ) {
				try { stmt.close(); } catch(Exception e) {}
			}
			if( db != null ) {
				try { 
					db.endTransaction();
					db.close(); 
				} catch(Exception e) {}
			}
		}
		
		return true;
	}
	
	public boolean delete(TimerSettingBean item) { 

		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;
		int index = 0;
		
		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();
			
			
			index = 1;
			stmt = db.compileStatement("delete from TIMER_ITEM where item_id=?");
			stmt.bindLong(index++, item.getID());
			stmt.executeInsert();
			stmt.close();

			index = 1;
			stmt = db.compileStatement("delete from TIMER_CLOSE_APP where item_id=?");
			stmt.bindLong(index++, item.getID());
			stmt.executeInsert();
			stmt.close();

			index = 1;
			stmt = db.compileStatement("delete from START_APP where item_id=?");
			stmt.bindLong(index++, item.getID());
			stmt.executeInsert();
			stmt.close();
/*
			index = 1;
			stmt = db.compileStatement("delete from TIMER_WOL where item_id=?");
			stmt.bindLong(index++, item.getID());
			stmt.executeInsert();
			stmt.close();
*/
			db.setTransactionSuccessful();
		} finally {
			if( db != null ) {
				try { 
					db.endTransaction();
					db.close(); 
				} catch(Exception e) {}
			}
		}
		
		return true;
	}
	
	public boolean update(TimerSettingBean item) {

		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;

		SQLiteStatement stmt = null;
//		int length = 0, i;
		int index = 0;
		
		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();
			
			StringBuilder sb = new StringBuilder();
			sb.append("update TIMER_ITEM set");
			sb.append("   item_name=?"); //1
			sb.append(" , wifi_lock=?"); //2
			sb.append(" , power_lock=?"); //3
			sb.append(" , hour=?"); //4
			sb.append(" , minite=?"); //5
			sb.append(" , time_opts=?"); //6
			sb.append(" , ring=?"); //7
			sb.append(" , ring_opt=?"); //8
			sb.append(" , audio=?"); //9
			sb.append(" , audio_opt=?"); //10
			sb.append(" , bluetooth=?"); //11
			sb.append(" , bluetooth_opt=?"); //12
			sb.append(" , GPS=?"); //13
			sb.append(" , GPS_opt=?"); //14
			sb.append(" , pcm_rec=?"); //15
			sb.append(" , wol_c_id=?"); //16
			sb.append(" , wol_repeat=?"); //17
			sb.append(" , remote_ope_c_id=?"); //18
			sb.append(" , remote_ope=?"); //19
			sb.append(" , music_play=?"); //20
			sb.append(" , music_play_id=?"); //21
			sb.append(" , music_shuffle=?"); //22
			sb.append(" , music_scrib=?"); //23
			sb.append(" , music_name=?"); //23
			sb.append("where item_id=?");
			
			index = 1;
			stmt = db.compileStatement(sb.toString());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getName:" + item.getName());
			}
			stmt.bindString(index++, item.getName());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "isWifiLock:" + item.isWifiLock());
			}
			stmt.bindLong(index++, item.isWifiLock() ? 1 : 0);
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "isPoweriLock:" + item.isPoweriLock());
			}
			stmt.bindLong(index++, item.isPoweriLock() ? 1 : 0);
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getHour:" + item.getHour());
			}
			stmt.bindLong(index++, item.getHour());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getMinite:" + item.getMinite());
			}
			stmt.bindLong(index++, item.getMinite());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getTimeOpt:" + item.getTimeOpt());
			}
			stmt.bindString(index++, item.getTimeOpt());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getRing:" + item.getRing());
			}
			stmt.bindString(index++, item.getRing());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getRingOpt:" + item.getRingOpt());
			}
			stmt.bindString(index++, item.getRingOpt());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getmAudio:" + item.getmAudio());
			}
			stmt.bindLong(index++, item.getmAudio());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getAudioOpt:" + item.getAudioOpt());
			}
			stmt.bindString(index++, item.getAudioOpt());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getBluetooth:" + item.getBluetooth());
			}
			stmt.bindLong(index++, item.getBluetooth());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getBluetooth:" + item.getBluetooth());
			}
			stmt.bindString(index++, item.getBluetoothOpt());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getBluetoothOpt:" + item.getBluetoothOpt());
			}
			stmt.bindLong(index++, item.getGPS());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getGPSOpt:" + item.getGPSOpt());
			}
			stmt.bindString(index++, item.getGPSOpt());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "isRecord:" + item.isRecord());
			}
			stmt.bindLong(index++, item.isRecord() ? 1 : 0);
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getWolComputerID:" + item.getWolComputerID());
			}
			stmt.bindLong(index++, item.getWolComputerID());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getWolRepeat:" + item.getWolRepeat());
			}
			stmt.bindLong(index++, item.getWolRepeat());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getRemoteOpeComputerID:" + item.getRemoteOpeComputerID());
			}
			stmt.bindLong(index++, item.getRemoteOpeComputerID());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getRemoteOpe:" + item.getRemoteOpe());
			}
			stmt.bindLong(index++, item.getRemoteOpe());	
			
			stmt.bindLong(index++, item.isMusic() ? 1 : 0);
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "isMusic:" + item.isMusic());
			}
			stmt.bindLong(index++, item.getMusicID());
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "getMusicID:" + item.getMusicID());
			}
			stmt.bindLong(index++, item.isMusicShuffle() ? 1 : 0);
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "isMusicShuffle:" + item.isMusicShuffle());
			}
			stmt.bindLong(index++, item.isMusicScrib() ? 1 : 0);
			if( MyLog.isDebugMod() ) {
				MyLog.logSQL(TAG, "MusicName:" + item.getMusicName());
			}
			stmt.bindString(index++, item.getMusicName());
			
			stmt.bindLong(index++, item.getID());
			
			stmt.executeInsert();
			
			this.updateKillApps(db, item);
			this.updateStartApps(db, item);
			this.updateWOL(db, item);
			
			db.setTransactionSuccessful();
		} finally {
			if(stmt != null ) {
				try { stmt.close(); } catch(Exception e) {}
			}
			if( db != null ) {
				try { 
					db.endTransaction();
					db.close(); 
				} catch(Exception e) {}
			}
		}
		
		return true;		
	}
	
	private void updateKillApps(SQLiteDatabase db, TimerSettingBean item) {
		SQLiteStatement stmt = null;
		int index = 0;

		try {
			Log.d("updateKillApps", "  " + item.getListCloseApp().size());
			index = 1;
			stmt = db.compileStatement("delete from TIMER_CLOSE_APP where item_id=?");
			stmt.bindLong(index++, item.getID());
			stmt.executeInsert();
			stmt.close();
			
			
			stmt = db.compileStatement("insert into TIMER_CLOSE_APP values(?, ?)");
			for(TimerSettingCloseAppBean ite : item.getListCloseApp()) {
				
				Log.d("updateKillApps", "  " + ite.getmAppName());
				
				index = 1;
				stmt.clearBindings();
				stmt.bindLong(index++, item.getID());
				stmt.bindString(index++, ite.getmAppName());
				try {
					stmt.executeInsert();
				} catch(Exception e) {
					
				}
			}
			stmt.close();
			stmt = null;
			
		} finally {
			if(stmt != null ) {
				try { stmt.close(); } catch(Exception e) {}
			}
		}
	}
	
	private void updateStartApps(SQLiteDatabase db, TimerSettingBean item) {
		SQLiteStatement stmt = null;
		int index = 0;
		Log.d("updateStartApps", "  " + item.getListStartApp().size());
		try {
			index = 1;
			stmt = db.compileStatement("delete from START_APP where item_id=?");
			stmt.bindLong(index++, item.getID());
			stmt.executeInsert();
			stmt.close();
			
			
			stmt = db.compileStatement("insert into START_APP values(?, ?)");
			for(TimerSettingStartApp ite : item.getListStartApp()) {
				Log.d("updateStartApps", "  " + ite.getmAppName());
				index = 1;
				stmt.clearBindings();
				stmt.bindLong(index++, item.getID());
				stmt.bindString(index++, ite.getmAppName());
				try {
					stmt.executeInsert();
				} catch(Exception e) {
					
				}
			}
			stmt.close();
			stmt = null;
			
		} finally {
			if(stmt != null ) {
				try { stmt.close(); } catch(Exception e) {}
			}
		}
	}
	
	private void updateWOL(SQLiteDatabase db, TimerSettingBean item) {
/*		
		SQLiteStatement stmt = null;
		int index = 0;
		Log.d("updateWOL", "  " + item.getListStartApp().size());
		try {
			index = 1;
			stmt = db.compileStatement("delete from TIMER_WOL where item_id=?");
			stmt.bindLong(index++, item.getID());
			stmt.executeInsert();
			stmt.close();
			
			index = 1;
			stmt = db.compileStatement("insert into TIMER_WOL values(?, ?, ?, ?, ?, ?)");
			stmt.clearBindings();
			stmt.bindLong(index++,  item.getID());
			stmt.bindLong(index++,  item.isEnableWOL() ? 1 : 0);
			stmt.bindString(index++,item.getWolIP());
			stmt.bindLong(index++,  item.getWolPORT());
			stmt.bindString(index++,item.getWolMAC());
			stmt.bindLong(index++,  item.getWolRepeat());
			try {
				stmt.executeInsert();
			} catch(Exception e) {
				
			}
			stmt.close();
			stmt = null;
			
		} finally {
			if(stmt != null ) {
				try { stmt.close(); } catch(Exception e) {}
			}
		}
*/		
	}
}
