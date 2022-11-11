package four.non.bronds.yyys.zzztimer.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import four.non.bronds.yyys.zzztimer.bean.PurchaseInf;
import four.non.bronds.yyys.zzztimer.bean.Purchased;
import four.non.bronds.yyys.zzztimer.bean.PurchasedHst;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.cmn.Constant.PurchaseState;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PurchaseAccessor {
	private static final String TAG = "Dungeons";
	private Context mContext = null;

	public PurchaseAccessor(Context ctx) {
		mContext = ctx;
	}

	/**
	 * ライセンス情報を取得
	 * 
	 * @return
	 */
	public PurchaseInf getLicenseInf() {
		if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "getLicenseInf start");}
		PurchaseInf inf = new PurchaseInf();
		SQLiteDatabase db = null;
		DbOpener dbOpnner = new DbOpener(mContext);
		Cursor c = null;
		int length = 0, i;
		
		
		try {
			StringBuilder sb;
			db = dbOpnner.getReadableDatabase();

			sb = new StringBuilder();
			sb.append(" select   ");
			sb.append("    pr_id  ");
			sb.append("  , quantity  ");
			sb.append(" from PURCHASED  ");
			c = db.rawQuery(sb.toString(), null);

			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				String strPrID = c.getString(0);
				
				if( Constant.PURCHASE_OGG.compareTo(strPrID) == 0 ) {
					inf.setOgg(true);
				}
				
				c.moveToNext();
			}
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (Exception e) {
				}
			}
			if (db != null) {
				try {
					db.close();
				} catch (Exception e) {
				}
			}
			if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "getLicenseInf end");}
		}
		return inf;
	}
	/**
	 * 購入情報を取得
	 * 
	 * @return
	 */
	public List<Purchased> loadPurchased() {
		if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "loadPurchased start");}
		List<Purchased> list = new ArrayList<Purchased>();
		DbOpener dbOpnner = new DbOpener(mContext);
		SQLiteDatabase db = null;
		Cursor c = null;
		int length = 0, i;
		try {
			StringBuilder sb;
			db = dbOpnner.getReadableDatabase();

			sb = new StringBuilder();
			sb.append(" select   ");
			sb.append("    pr_id  ");
			sb.append("  , quantity  ");
			sb.append(" from PURCHASED  ");
			c = db.rawQuery(sb.toString(), null);

			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				Purchased p = new Purchased();
				p.setPrID(c.getString(0));
				p.setQuantity(c.getInt(1));
				list.add(p);
				c.moveToNext();
			}
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (Exception e) {
				}
			}
			if (db != null) {
				try {
					db.close();
				} catch (Exception e) {
				}
			}
			if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "insertOrder end");}
		}
		return list;
	}
	/**
	 * 購入履歴情報を取得
	 * 
	 * @return
	 */
	public List<PurchasedHst> loadPurchasedHistory() {
		if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "loadPurchasedHistory start");}
		List<PurchasedHst> list = new ArrayList<PurchasedHst>();
		DbOpener dbOpnner = new DbOpener(mContext);
		SQLiteDatabase db = null;
		Cursor c = null;
		int length = 0, i;
		try {
			StringBuilder sb;
			db = dbOpnner.getReadableDatabase();

			sb = new StringBuilder();
			sb.append(" select   ");
			sb.append("    order_id  ");
			sb.append("  , state  ");
			sb.append("  , pr_id  ");
			sb.append("  , developerPayload  ");
			sb.append("  , purchaseTime  ");
			sb.append(" from PURCHASED_HST  ");
			c = db.rawQuery(sb.toString(), null);

			c.moveToFirst();
			length = c.getCount();
			for (i = 0; i < length; i++) {
				PurchasedHst p = new PurchasedHst();
				p.setOrderID( c.getString(0) );
				p.setState( c.getInt(1) );
				p.setPrID(c.getString(2));
				p.setDeveloperPayload( c.getString(3) );
				p.setPurchaseTime( new Date(c.getLong(4)) );
				list.add(p);
				c.moveToNext();
			}
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (Exception e) {
				}
			}
			if (db != null) {
				try {
					db.close();
				} catch (Exception e) {
				}
			}
			if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "loadPurchasedHistory end");}
		}
		return list;
	}

	private void insertOrder(String orderId, String productId,
			PurchaseState state, long purchaseTime, String developerPayload) {
		if (MyLog.isDebugMod()) {
			MyLog.logf(TAG, "insertOrder start");
			MyLog.logt(TAG, "       orderId          :" + orderId);
			MyLog.logt(TAG, "       productId        :" + productId);
			MyLog.logt(TAG, "       state            :" + state);
			MyLog.logt(TAG, "       purchaseTime     :" + purchaseTime);
			MyLog.logt(TAG, "       developerPayload :" + developerPayload);
		}
		SQLiteDatabase db = null;
		try {
			DbOpener dbOpnner = new DbOpener(mContext);
			db = dbOpnner.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("order_id", orderId);
			values.put("pr_id", productId);
			values.put("state", state.ordinal());
			values.put("purchaseTime", purchaseTime);
			values.put("developerPayload", developerPayload);
			db.replace("PURCHASED_HST", null /* nullColumnHack */, values);
		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (Exception e) {
				}
			}
			if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "insertOrder end");}
		}
	}

	private void updatePurchasedItem(String productId, int quantity) {
		if (MyLog.isDebugMod()) {
			MyLog.logf(TAG, "updatePurchasedItem start");
			MyLog.logt(TAG, "       productId        :" + productId);
			MyLog.logt(TAG, "       quantity         :" + quantity);
		}
		SQLiteDatabase db = null;
		try {
			DbOpener dbOpnner = new DbOpener(mContext);
			db = dbOpnner.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("pr_id", productId);
			values.put("quantity", quantity);
			db.replace("PURCHASED", null /* nullColumnHack */, values);

		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (Exception e) {
				}
			}
			if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "updatePurchasedItem end");}
		}
	}

	public synchronized int updatePurchase(String orderId, String productId,
			PurchaseState purchaseState, long purchaseTime,
			String developerPayload) {
		if (MyLog.isDebugMod()) {
			MyLog.logf(TAG, "updatePurchase start");
			MyLog.logt(TAG, "       orderId          :" + orderId);
			MyLog.logt(TAG, "       productId        :" + productId);
			MyLog.logt(TAG, "       state            :" + purchaseState);
			MyLog.logt(TAG, "       purchaseTime     :" + purchaseTime);
			MyLog.logt(TAG, "       developerPayload :" + developerPayload);
		}
		int quantity = 0;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String[] clmns = new String[] { "order_id", "pr_id", "state",
				"purchaseTime", "developerPayload" };
		try {
			DbOpener dbOpnner = new DbOpener(mContext);
			db = dbOpnner.getWritableDatabase();

			insertOrder(orderId, productId, purchaseState, purchaseTime,
					developerPayload);
			cursor = db.query("PURCHASED_HST", clmns, "pr_id" + "=?",
					new String[] { productId }, null, null, null, null);
			if (cursor == null) {
				return 0;
			}
			while (cursor.moveToNext()) {

				int stateIndex = cursor.getInt(2);
				PurchaseState state = PurchaseState.valueOf(stateIndex);

				if (state == PurchaseState.PURCHASED || state == PurchaseState.REFUNDED) {
					quantity += 1;
				}
			}
			cursor.close();
			cursor = null;

			// Update the "purchased items" table
			updatePurchasedItem(productId, quantity);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {
				}
			}
			if (db != null) {
				try {
					db.close();
				} catch (Exception e) {
				}
			}
			if( MyLog.isDebugMod() ) {MyLog.logf(TAG, "updatePurchase end");}
		}
		return quantity;
	}
}
