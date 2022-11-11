package four.non.bronds.yyys.zzztimer.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import four.non.bronds.yyys.zzztimer.bean.RecFileInf;
import four.non.bronds.yyys.zzztimer.bean.RecTagBean;
import four.non.bronds.yyys.zzztimer.util.MyLog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


public class RecDbAccessor {
	private static final String TAG = "RecDbAccessor";
	private Context		mContext = null;

	public RecDbAccessor(Context ctx) {
		mContext = ctx;
	}
	
	/**
	 * 録音リストをロード
	 * @return	録音リスト
	 */
	public List<RecFileInf> load() {
		List<RecFileInf> container = new ArrayList<RecFileInf>();
		DbOpener dbOpnner = new DbOpener(mContext);
		SQLiteDatabase db = null;
		Cursor c = null;
		int length = 0, i;
		
		
		try {
			db = dbOpnner.getReadableDatabase();
			StringBuilder sb;
			
			sb = new StringBuilder();
			sb.append(" select   ");
			sb.append("    fname  ");
			sb.append("  , disp  ");
			sb.append("  , rec_date  ");
			sb.append("  , time  ");
			sb.append("  , data_size  ");
			sb.append("  , bits_per  ");
			sb.append("  , bit  ");
			sb.append("  , chanel  ");
			sb.append("  , type  ");
			sb.append(" from REC_FILE  ");
			c = db.rawQuery(sb.toString(), null);

			
			
			
			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				RecFileInf rcf = new RecFileInf();
				rcf.setFName( c.getString(0) );
				rcf.setDisp(c.getString(1) );
				rcf.setRecDate( new Date(c.getLong(2)) );
				rcf.setTime(c.getLong(3));
				rcf.setSize(c.getLong(4));
				rcf.setBitsPer(c.getInt(5));
				rcf.setBit(c.getInt(6));
				rcf.setChanel(c.getInt(7));
				rcf.setType(c.getInt(8));
				
				container.add(rcf);
				
				c.moveToNext();
			}

		} finally {
			if(c != null ) {
				try { c.close(); } catch(Exception e) {}
			}
			if( db != null ) {
				try { db.close(); } catch(Exception e) {}
			}
		}
		return container;
	}
	/**
	 * 録音タグリストをロード
	 * @param fname	ファイル名
	 * @return	録音タグリスト
	 */
	public List<RecTagBean> lodTags(String fname) {
		List<RecTagBean> continar = new ArrayList<RecTagBean>();
		DbOpener dbOpnner = new DbOpener(mContext);
		SQLiteDatabase db = null;
		Cursor c = null;
		int length = 0, i;
		
		try {
			db = dbOpnner.getReadableDatabase();
			StringBuilder sb;
			
			sb = new StringBuilder();
			sb.append(" select   ");
			sb.append("    tag  ");
			sb.append("  , tag_time  ");
			sb.append(" from REC_TAG where fname=?  ");
			c = db.rawQuery(sb.toString(), new String[]{fname});

			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				RecTagBean tag = new RecTagBean(c.getString(0), c.getLong(1));
				
				continar.add(tag);
				
				c.moveToNext();
			}
		} finally {
			if(c != null ) {
				try { c.close(); } catch(Exception e) {}
			}
			if( db != null ) {
				try { db.close(); } catch(Exception e) {}
			}
		}
		return continar;
	}
	public void add(RecFileInf rcf, List<RecTagBean> tags) {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "add start"); }
		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;
		int index = 0;
		
		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();
			if( MyLog.isDebugMod() ) {
				MyLog.logt(TAG, "  INS REC_FILE"); 
				MyLog.logt(TAG, "    FNAME     :" + rcf.getFName()); 
				MyLog.logt(TAG, "    DISP      :" + rcf.getDisp());
				MyLog.logt(TAG, "    Date      :" + rcf.getRecDate());
				MyLog.logt(TAG, "    Time      :" + rcf.getTime());
				MyLog.logt(TAG, "    Size      :" + rcf.getSize());
				MyLog.logt(TAG, "    BitsPer   :" + rcf.getBitsPer());
				MyLog.logt(TAG, "    Bit       :" + rcf.getBit());
				MyLog.logt(TAG, "    Chanel    :" + rcf.getChanel());
				MyLog.logt(TAG, "    Type      :" + rcf.getType());
			}
			index = 1;
			stmt = db.compileStatement("insert into REC_FILE values(?, ?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.bindString(index++, rcf.getFName());
			stmt.bindString(index++, rcf.getDisp());
			stmt.bindLong(index++, rcf.getRecDate().getTime());
			stmt.bindLong(index++, rcf.getTime());
			stmt.bindLong(index++, rcf.getSize());
			stmt.bindLong(index++, rcf.getBitsPer());
			stmt.bindLong(index++, rcf.getBit());
			stmt.bindLong(index++, rcf.getChanel());
			stmt.bindLong(index++, rcf.getType());
			stmt.executeInsert();
			
			if( tags != null ) {
				stmt = db.compileStatement("insert into REC_TAG values(?, ?, ?)");
				for(RecTagBean tag : tags ) {
					if( MyLog.isDebugMod() ) {
						MyLog.logt(TAG, "  INS REC_TAG"); 
						MyLog.logt(TAG, "    FNAME     :" + rcf.getFName()); 
						MyLog.logt(TAG, "    DISP      :" + tag.getTag());
						MyLog.logt(TAG, "    Date      :" + tag.getTime());
					}				
					index = 1;
					stmt.bindString(index++, rcf.getFName());
					stmt.bindString(index++, tag.getTag());
					stmt.bindLong(index++, tag.getTime());
					stmt.executeInsert();
					stmt.clearBindings();
				}
			}
			
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
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "add end"); }
		}
	}
	
	public void del(String strFName) {
		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;
		int index = 0;
		
		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();
			
			try { 
				File fName = new File(strFName);
				if( fName.exists() ) {
					fName.delete();
				}
			} catch(Exception e ) {
				// noting
			}
			
			
			index = 1;
			stmt = db.compileStatement("delete from REC_FILE where fname=?");
			stmt.bindString(index++, strFName);
			stmt.executeInsert();
			
			
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
	}

	public void update(RecFileInf rcf) {
		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;
		int index = 0;
		
		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();
			
			index = 1;
			stmt = db.compileStatement("update REC_FILE set disp=?  where fname=?");
			
			stmt.bindString(index++, rcf.getDisp());
			stmt.bindString(index++, rcf.getFName());
			stmt.executeInsert();
			
			
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
	}
}
