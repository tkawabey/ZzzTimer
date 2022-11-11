package four.non.bronds.yyys.zzztimer.db;

import java.util.ArrayList;
import java.util.List;

import four.non.bronds.yyys.zzztimer.bean.Computer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class ComputerAccessor {
	private Context		mContext = null;
	
	public ComputerAccessor(Context ctx) {
		mContext = ctx;
	}
	
	/**
	 * Computer一覧を取得
	 * @return
	 */
	public List<Computer> load() {
		List<Computer> contaiar = new ArrayList<Computer>();

		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		Cursor c = null;
		int length = 0, i;

		try {
			db = dbOpnner.getReadableDatabase();
			

			c = db.rawQuery("select * from COMPUTER", null);
			
			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				Computer comp = new Computer();
				/*
					computer_id
					mac_addr
					hostname
					name
					broadcast_addr
					OS
					OSver
					username
					passwd
					exec_image
					exec_params
					exec_cur_dir
					zzz_tcp_port
					port_wol
					resume
					resume_opt
					shutdown_msg
					shutdown_after_tm
				 */
				comp.setId(c.getInt(0));
				comp.setMac_addr(c.getString(1));
				comp.setHostname(c.getString(2));
				comp.setName(c.getString(3));
				comp.setBroadcast_addr(c.getInt(4));
				comp.setOS(c.getInt(5));
				comp.setOSver(c.getString(6));
				comp.setUsername(c.getString(7));
				comp.setPasswd(c.getString(8));
				comp.setExec_image(c.getString(9));
				comp.setExec_params(c.getString(10));
				comp.setExec_cur_dir(c.getString(11));
				comp.setZzz_tcp_port(c.getInt(12));
				comp.setPort_wol(c.getInt(13));
				comp.setResume(c.getInt(14));
				comp.setResume_opt(c.getInt(15));
				comp.setShutdown_msg(c.getString(16));
				comp.setShutdown_after_tm(c.getInt(17));
				
				contaiar.add(comp);
				c.moveToNext();
			}
			c.close();
			c = null;
		} finally {
			if(c != null ) {
				try { c.close(); } catch(Exception e) {}
			}
			if( db != null ) {
				try { db.close(); } catch(Exception e) {}
			}
		}
		return contaiar;
	}

	public Computer getByID(int computer_id) {
		DbOpener dbOpnner = new DbOpener(mContext);
		SQLiteDatabase db = null;
		Computer comp = null;
		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();
			
			comp = ComputerAccessor.getByID(db, computer_id);

			db.setTransactionSuccessful();
		} finally {
			if( db != null ) {
				try { 
					db.endTransaction();
					db.close(); 
				} catch(Exception e) {}
			}
		}
		return comp;
	}
	/**
	 * 指定したcomputer_idのコンピューターデータを取得
	 * @param db
	 * @param computer_id
	 * @return
	 */
	public static Computer getByID(SQLiteDatabase db, int computer_id) {
		Cursor c = null;
		int length = 0, i;
		Computer comp = null;
		try {
			c = db.rawQuery("select * from COMPUTER where computer_id=" + computer_id, null);

			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				comp = new Computer();

				comp.setId(c.getInt(0));
				comp.setMac_addr(c.getString(1));
				comp.setHostname(c.getString(2));
				comp.setName(c.getString(3));
				comp.setBroadcast_addr(c.getInt(4));
				comp.setOS(c.getInt(5));
				comp.setOSver(c.getString(6));
				comp.setUsername(c.getString(7));
				comp.setPasswd(c.getString(8));
				comp.setExec_image(c.getString(9));
				comp.setExec_params(c.getString(10));
				comp.setExec_cur_dir(c.getString(11));
				comp.setZzz_tcp_port(c.getInt(12));
				comp.setPort_wol(c.getInt(13));
				comp.setResume(c.getInt(14));
				comp.setResume_opt(c.getInt(15));
				comp.setShutdown_msg(c.getString(16));
				comp.setShutdown_after_tm(c.getInt(17));
				
				c.moveToNext();
			}
			c.close();
			c = null;
			
			
		} finally {
			if(c != null ) {
				try { c.close(); } catch(Exception e) {}
			}
		}
		return comp;
	}
	
	/**
	 * Computerを新規作成
	 * @param comp
	 */
	static public void add(SQLiteDatabase db, Computer comp) {
		Cursor c = null;
		SQLiteStatement stmt = null;
		int length = 0, i;
		int item_id = 0;
		int index = 0;
		try {
			c = db.rawQuery("select max(computer_id) from COMPUTER", null);
			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				item_id = c.getInt(0);
				c.moveToNext();
			}
			c.close();
			c = null;
			item_id++;
			

			index = 1;
			stmt = db.compileStatement("insert into COMPUTER values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			/*
				computer_id
				mac_addr
				hostname
				name
				broadcast_addr
				OS
				OSver
				username
				passwd
				exec_image
				exec_params
				exec_cur_dir
				zzz_tcp_port
				port_wol
			 */			
			stmt.bindLong(index++, item_id);
			stmt.bindString(index++, comp.getMac_addr());
			stmt.bindString(index++, comp.getHostname());
			stmt.bindString(index++, comp.getName());
			stmt.bindLong(index++, comp.getBroadcast_addr());
			stmt.bindLong(index++, comp.getOS());
			stmt.bindString(index++, comp.getOSver());
			stmt.bindString(index++, comp.getUsername());
			stmt.bindString(index++, comp.getPasswd());
			stmt.bindString(index++, comp.getExec_image());
			stmt.bindString(index++, comp.getExec_params());
			stmt.bindString(index++, comp.getExec_cur_dir());
			stmt.bindLong(index++, comp.getZzz_tcp_port());
			stmt.bindLong(index++, comp.getPort_wol());
			stmt.bindLong(index++, comp.getResume());
			stmt.bindLong(index++, comp.getResume_opt());
			stmt.bindString(index++, comp.getShutdown_msg());
			stmt.bindLong(index++, comp.getShutdown_after_tm());
			stmt.executeInsert();

			comp.setId( item_id );
		} finally {
			if(c != null ) {
				try { c.close(); } catch(Exception e) {}
			}
			if(stmt != null ) {
				try { stmt.close(); } catch(Exception e) {}
			}
		}	
	}
	/**
	 * Computerを新規作成
	 * @param comp
	 */
	public void add(Computer comp) {
		DbOpener dbOpnner = new DbOpener(mContext);
		SQLiteDatabase db = null;
		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();
			
			ComputerAccessor.add(db, comp);

			db.setTransactionSuccessful();
		} finally {
			if( db != null ) {
				try { 
					db.endTransaction();
					db.close(); 
				} catch(Exception e) {}
			}
		}
	}
	
	/**
	 * 指定したコンピュータを削除する
	 * @param comp
	 */
	public void remote(Computer comp) {

		DbOpener dbOpnner = new DbOpener(mContext);
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();

			stmt = db.compileStatement("delete from COMPUTER where computer_id=" + comp.getId());
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

	public void edit(Computer comp) {

		DbOpener dbOpnner = new DbOpener(mContext);
		
		SQLiteDatabase db = null;
		Cursor c = null;
		SQLiteStatement stmt = null;
		int index = 0;
		try {
			StringBuilder sb;
			db = dbOpnner.getReadableDatabase();
			db.beginTransaction();

			
			sb = new StringBuilder();
			sb.append(" update COMPUTER ");
			sb.append(" set  ");
			sb.append(" mac_addr=?, ");
			sb.append(" hostname=?, ");
			sb.append(" name=?, ");
			sb.append(" broadcast_addr=?, ");
			sb.append(" OS=?, ");
			sb.append(" OSver=?, ");
			sb.append(" username=?, ");
			sb.append(" passwd=?, ");
			sb.append(" exec_image=?, ");
			sb.append(" exec_params=?, ");
			sb.append(" exec_cur_dir=?, ");
			sb.append(" zzz_tcp_port=?, ");
			sb.append(" port_wol=?, ");
			sb.append(" resume=?, ");
			sb.append(" resume_opt=?, ");
			sb.append(" shutdown_msg=?, ");
			sb.append(" shutdown_after_tm=? ");
			sb.append(" where computer_id=? ");
			index = 1;
			stmt = db.compileStatement(sb.toString());
			stmt.bindString(index++, comp.getMac_addr());
			stmt.bindString(index++, comp.getHostname());
			stmt.bindString(index++, comp.getName());
			stmt.bindLong(index++, comp.getBroadcast_addr());
			stmt.bindLong(index++, comp.getOS());
			stmt.bindString(index++, comp.getOSver());
			stmt.bindString(index++, comp.getUsername());
			stmt.bindString(index++, comp.getPasswd());
			stmt.bindString(index++, comp.getExec_image());
			stmt.bindString(index++, comp.getExec_params());
			stmt.bindString(index++, comp.getExec_cur_dir());
			stmt.bindLong(index++, comp.getZzz_tcp_port());
			stmt.bindLong(index++, comp.getPort_wol());
			stmt.bindLong(index++, comp.getResume());
			stmt.bindLong(index++, comp.getResume_opt());
			stmt.bindString(index++, comp.getShutdown_msg());
			stmt.bindLong(index++, comp.getShutdown_after_tm());
			stmt.bindLong(index++, comp.getId());

			stmt.executeInsert();

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
	}
}
