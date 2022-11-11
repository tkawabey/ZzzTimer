package four.non.bronds.yyys.zzztimer.db;

import four.non.bronds.yyys.zzztimer.bean.Computer;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpener extends SQLiteOpenHelper {
	private static final String TAG = "DbOpener";
	public DbOpener(Context context) {
		super(context, "laa.db", null, 100);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sb;
		//TIMER_ITEM
		sb = new StringBuilder();
		sb.append("CREATE TABLE TIMER_ITEM (");
		sb.append("     item_id Integer NOT NULL ");
		sb.append("   , item_name varchar(256) NOT NULL ");
		sb.append("   , wifi_lock Integer NOT NULL ");
		sb.append("   , power_lock Integer NOT NULL ");
		sb.append("   , hour Integer NOT NULL ");
		sb.append("   , minite Integer NOT NULL ");
		sb.append("   , time_opts varchar(256) NOT NULL ");
		sb.append("   , ring varchar(256) NOT NULL ");
		sb.append("   , ring_opt varchar(256) NOT NULL ");
		sb.append("   , audio Integer NOT NULL ");
		sb.append("   , audio_opt varchar(256) NOT NULL ");
		sb.append("   , bluetooth Integer NOT NULL ");
		sb.append("   , bluetooth_opt varchar(256) NOT NULL ");
		sb.append("   , GPS Integer NOT NULL ");
		sb.append("   , GPS_opt varchar(256) NOT NULL ");
		sb.append("   , pcm_rec Integer NOT NULL ");
		sb.append("   , wol_c_id Integer NOT NULL ");
		sb.append("   , wol_repeat Integer NOT NULL ");
		sb.append("   , remote_ope_c_id Integer NOT NULL ");
		sb.append("   , remote_ope Integer NOT NULL ");
		sb.append("   , music_play Integer");
		sb.append("   , music_play_id Integer");
		sb.append("   , music_shuffle Integer");
		sb.append("   , music_scrib Integer");
		sb.append("   , music_name varchar(256) NOT NULL ");
		sb.append("   ,  CONSTRAINT PK_TIMER_ITEM  PRIMARY KEY  (item_id)");
		sb.append(")");
		db.execSQL(sb.toString());
		//TIMER_CLOSE_APP
		sb = new StringBuilder();
		sb.append("CREATE TABLE TIMER_CLOSE_APP (");
		sb.append("     item_id Integer NOT NULL ");
		sb.append("   , app varchar(256) NOT NULL ");
		sb.append("   ,  CONSTRAINT PK_TIMER_CLOSE_APP  PRIMARY KEY  (item_id, app)");
		sb.append(")");
		db.execSQL(sb.toString());
		//START_APP
		sb = new StringBuilder();
		sb.append("CREATE TABLE START_APP (");
		sb.append("     item_id Integer NOT NULL ");
		sb.append("   , app varchar(256) NOT NULL ");
		sb.append("   ,  CONSTRAINT PK_START_APP  PRIMARY KEY  (item_id, app)");
		sb.append(")");
		db.execSQL(sb.toString());
		//TIMER_WOL
		sb = new StringBuilder();
		sb.append("CREATE TABLE TIMER_WOL (");
		sb.append("     item_id Integer NOT NULL ");
		sb.append("   , enable_wol Integer NOT NULL ");
		sb.append("   , IP varchar(255) NOT NULL ");
		sb.append("   , PORT Integer NOT NULL ");
		sb.append("   , MAC varchar(255) NOT NULL ");
		sb.append("   , REPEAT Integer NOT NULL ");
		sb.append("   ,  CONSTRAINT PK_TIMER_WOL  PRIMARY KEY  (item_id)");
		sb.append(")");
		db.execSQL(sb.toString());

		//COMPUTER
		sb = new StringBuilder();
		sb.append("CREATE TABLE COMPUTER (");
		sb.append("     computer_id integer NOT NULL ");
		sb.append("   , mac_addr varchar(64) NOT NULL ");
		sb.append("   , hostname integer NOT NULL ");
		sb.append("   , name varchar(255) NOT NULL ");
		sb.append("   , broadcast_addr integer NOT NULL ");
		sb.append("   , OS integer NOT NULL ");
		sb.append("   , OSver varchar(255) NOT NULL ");
		sb.append("   , username varchar(255) NOT NULL ");
		sb.append("   , passwd blob NOT NULL ");
		sb.append("   , exec_image varchar(255) NOT NULL ");
		sb.append("   , exec_params varchar(255) NOT NULL ");
		sb.append("   , exec_cur_dir varchar(255) NOT NULL ");
		sb.append("   , zzz_tcp_port integer NOT NULL ");
		sb.append("   , port_wol integer NOT NULL ");
		sb.append("   , resume integer NOT NULL ");
		sb.append("   , resume_opt integer NOT NULL ");
		sb.append("   , shutdown_msg varchar(255) NOT NULL ");
		sb.append("   , shutdown_after_tm integer NOT NULL ");
		sb.append("   ,  CONSTRAINT PK_COMPUTER  PRIMARY KEY  (computer_id)");
		sb.append(")");
		db.execSQL(sb.toString());
		
		
		//REC_FILE
		sb = new StringBuilder();
		sb.append("CREATE TABLE REC_FILE (");
		sb.append("     fname varchar(255) NOT NULL ");
		sb.append("   , disp varchar(256) NOT NULL ");
		sb.append("   , rec_date Integer");
		sb.append("   , time Integer");
		sb.append("   , data_size Integer");
		sb.append("   , bits_per Integer");
		sb.append("   , bit Integer");
		sb.append("   , chanel Integer");
		sb.append("   , type Integer");
		sb.append("   ,  CONSTRAINT PK_REC_FILE  PRIMARY KEY  (fname)");
		sb.append(")");
		db.execSQL(sb.toString());
		
		//REC_TAG
		sb = new StringBuilder();
		sb.append("CREATE TABLE REC_TAG (");
		sb.append("     fname varchar(255) NOT NULL ");
		sb.append("   , tag varchar(255)");
		sb.append("   , tag_time Integer");
		sb.append("   ,  CONSTRAINT PK_REC_TAG  PRIMARY KEY  (fname, tag_time)");
		sb.append(")");
		db.execSQL(sb.toString());

		
		//PURCHASED
		sb = new StringBuilder();
		sb.append("CREATE TABLE PURCHASED (");
		sb.append("     pr_id varchar(255) NOT NULL ");
		sb.append("   , quantity Integer NOT NULL ");
		sb.append("   ,  CONSTRAINT PK_PURCHASED  PRIMARY KEY  (pr_id)");
		sb.append(")");
		db.execSQL(sb.toString());
		//PURCHASED_HST
		sb = new StringBuilder();
		sb.append("CREATE TABLE PURCHASED_HST (");
		sb.append("     order_id varchar(255) NOT NULL ");
		sb.append("   , state Integer NOT NULL ");
		sb.append("   , pr_id varchar(255) NOT NULL ");
		sb.append("   , developerPayload varchar(255)");
		sb.append("   , purchaseTime Integer NOT NULL ");
		sb.append("   ,  CONSTRAINT PK_PURCHASED_HST  PRIMARY KEY  (order_id)");
		sb.append(")");
		db.execSQL(sb.toString());

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		StringBuilder sb;
		Cursor c = null;
		MyLog.logf(TAG, "onUpgrade start");
		MyLog.logt(TAG, " oldVersion=" + oldVersion + "  newVersion=" + newVersion);
		try {
			try {
				sb = new StringBuilder();
				sb.append("CREATE TABLE START_APP (");
				sb.append("     item_id Integer NOT NULL ");
				sb.append("   , app varchar(256) NOT NULL ");
				sb.append("   ,  CONSTRAINT PK_START_APP  PRIMARY KEY  (item_id, app)");
				sb.append(")");
				db.execSQL(sb.toString());
			} catch(Exception e) {
				MyLog.loge(TAG, e);
			}
			try {
				if( oldVersion <= 3 ) {
					MyLog.logt(TAG, " Create TABLE TIMER_WOL");
					//TIMER_WOL
					sb = new StringBuilder();
					sb.append("CREATE TABLE TIMER_WOL (");
					sb.append("     item_id Integer NOT NULL ");
					sb.append("   , enable_wol Integer NOT NULL ");
					sb.append("   , IP varchar(255) NOT NULL ");
					sb.append("   , PORT Integer NOT NULL ");
					sb.append("   , MAC varchar(255) NOT NULL ");
					sb.append("   , REPEAT Integer NOT NULL ");
					sb.append("   ,  CONSTRAINT PK_TIMER_WOL  PRIMARY KEY  (item_id)");
					sb.append(")");
					db.execSQL(sb.toString());
				}
			} catch(Exception e) {
				MyLog.loge(TAG, e);
			}
				
			try {
				if( oldVersion <= 7 ) {
					//COMPUTER
					try {
						sb = new StringBuilder();
						sb.append("CREATE TABLE COMPUTER (");
						sb.append("     computer_id integer NOT NULL ");
						sb.append("   , mac_addr varchar(64) NOT NULL ");
						sb.append("   , hostname integer NOT NULL ");
						sb.append("   , name varchar(255) NOT NULL ");
						sb.append("   , broadcast_addr integer NOT NULL ");
						sb.append("   , OS integer NOT NULL ");
						sb.append("   , OSver varchar(255) NOT NULL ");
						sb.append("   , username varchar(255) NOT NULL ");
						sb.append("   , passwd blob NOT NULL ");
						sb.append("   , exec_image varchar(255) NOT NULL ");
						sb.append("   , exec_params varchar(255) NOT NULL ");
						sb.append("   , exec_cur_dir varchar(255) NOT NULL ");
						sb.append("   , zzz_tcp_port integer NOT NULL ");
						sb.append("   , port_wol integer NOT NULL ");
						sb.append("   , resume integer NOT NULL ");
						sb.append("   , resume_opt integer NOT NULL ");
						sb.append("   , shutdown_msg varchar(255) NOT NULL ");
						sb.append("   , shutdown_after_tm integer NOT NULL ");
						sb.append("   ,  CONSTRAINT PK_COMPUTER  PRIMARY KEY  (computer_id)");
						sb.append(")");
						db.execSQL(sb.toString());
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					
					//REC_FILE
					try {

						sb = new StringBuilder();
						sb.append("CREATE TABLE REC_FILE (");
						sb.append("     fname varchar(255) NOT NULL ");
						sb.append("   , disp varchar(256) NOT NULL ");
						sb.append("   , rec_date Integer");
						sb.append("   , time Integer");
						sb.append("   , data_size Integer");
						sb.append("   , bits_per Integer");
						sb.append("   , bit Integer");
						sb.append("   , chanel Integer");
						sb.append("   , type Integer");
						sb.append("   ,  CONSTRAINT PK_REC_FILE  PRIMARY KEY  (fname)");
						sb.append(")");
						db.execSQL(sb.toString());
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					
					//REC_TAG
					try {
						sb = new StringBuilder();
						sb.append("CREATE TABLE REC_TAG (");
						sb.append("     fname varchar(255) NOT NULL ");
						sb.append("   , tag varchar(255)");
						sb.append("   , tag_time Integer");
						sb.append("   ,  CONSTRAINT PK_REC_TAG  PRIMARY KEY  (fname, tag_time)");
						sb.append(")");
						db.execSQL(sb.toString());
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}

					
					//PURCHASED
					try {
						sb = new StringBuilder();
						sb.append("CREATE TABLE PURCHASED (");
						sb.append("     pr_id varchar(255) NOT NULL ");
						sb.append("   , quantity Integer NOT NULL ");
						sb.append("   ,  CONSTRAINT PK_PURCHASED  PRIMARY KEY  (pr_id)");
						sb.append(")");
						db.execSQL(sb.toString());
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					//PURCHASED_HST
					try {
						sb = new StringBuilder();
						sb.append("CREATE TABLE PURCHASED_HST (");
						sb.append("     order_id varchar(255) NOT NULL ");
						sb.append("   , state Integer NOT NULL ");
						sb.append("   , pr_id varchar(255) NOT NULL ");
						sb.append("   , developerPayload varchar(255)");
						sb.append("   , purchaseTime Integer NOT NULL ");
						sb.append("   ,  CONSTRAINT PK_PURCHASED_HST  PRIMARY KEY  (order_id)");
						sb.append(")");
						db.execSQL(sb.toString());
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}


					try {
						sb = new StringBuilder();
						sb.append("ALTER  TABLE TIMER_ITEM add pcm_rec Integer");
						db.execSQL(sb.toString());
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					

					try {
						db.execSQL("ALTER table TIMER_ITEM add wol_c_id Integer");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					try {
						db.execSQL("ALTER table TIMER_ITEM add wol_repeat Integer");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}

					try {
						db.execSQL("ALTER table TIMER_ITEM add remote_ope_c_id Integer");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					try {
						db.execSQL("ALTER table TIMER_ITEM add remote_ope Integer");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					try {
						db.execSQL("ALTER table TIMER_ITEM add music_play Integer");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					try {
						db.execSQL("ALTER table TIMER_ITEM add music_play_id Integer");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					try {
						db.execSQL("ALTER table TIMER_ITEM add music_shuffle Integer");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					try {
						db.execSQL("ALTER table TIMER_ITEM add music_scrib Integer");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					try {
						db.execSQL("ALTER table TIMER_ITEM add music_name varchar(255)");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					try {
						db.execSQL("update TIMER_ITEM set pcm_rec=0");
						db.execSQL("update TIMER_ITEM set wol_c_id=-1");
						db.execSQL("update TIMER_ITEM set wol_repeat=0");
						db.execSQL("update TIMER_ITEM set remote_ope_c_id=-1");
						db.execSQL("update TIMER_ITEM set remote_ope=0");
						db.execSQL("update TIMER_ITEM set music_play=0");
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					}
					
					
					// 旧データの移行
					sb = new StringBuilder();
					sb.append("select item_id"); //
					sb.append(" , enable_wol"); //
					sb.append(" , IP"); //
					sb.append(" , PORT"); //
					sb.append(" , MAC"); //
					sb.append(" , REPEAT"); //
					sb.append(" from TIMER_WOL"); //
					c = db.rawQuery(sb.toString(), null);
					c.moveToFirst();
					int length = c.getCount();
					int i;
					for(i = 0; i < length; i++) {
						int item_id = c.getInt(0);
						int enable_wol = c.getInt(1);
						String IP = c.getString(2);
						int PORT = c.getInt(3);
						String MAC = c.getString(4);
						int REPEAT = c.getInt(5);
						
						MyLog.logt(TAG, " Add Computer ID:" + "Computer-" + (i+1));

						// コンピュータの追加
						Computer comp = new Computer();
						comp.setName("Computer-" + (i+1));
						comp.setHostname(IP);
						comp.setZzz_tcp_port(PORT);
						comp.setMac_addr(MAC);						
						ComputerAccessor.add(db, comp);
						
						MyLog.logt(TAG, "Pass1");
						
						// タイマーアイテムの更新
						if( enable_wol == 1 ) {
							sb = new StringBuilder();
							sb.append("update TIMER_ITEM set");
							sb.append("  wol_c_id=").append(item_id);
							sb.append(", wol_repeat=").append(REPEAT);
							sb.append(" where item_id=").append(comp.getId());
							MyLog.logt(TAG, "[" + sb.toString() + "]");
							db.execSQL(sb.toString());
							MyLog.logt(TAG, "Pass2");
						}
						c.moveToNext();
					}
					c.close();
					c = null;
										
					MyLog.logt(TAG, "Pass3");
				}
			} catch(Exception e) {
				MyLog.loge(TAG, e);
			}
		} finally {
			if( c != null ) {
				c.close();
			}
			MyLog.logf(TAG, "onUpgrade end");
		}
	}

	
}
