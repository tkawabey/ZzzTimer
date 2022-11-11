package four.non.bronds.yyys.zzztimer.activity;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.Purchased;
import four.non.bronds.yyys.zzztimer.bean.PurchasedHst;
import four.non.bronds.yyys.zzztimer.db.PurchaseAccessor;
import four.non.bronds.yyys.zzztimer.util.MyLog;

public class InAppBillingHstActivity extends MyBaseActivity {
//	private static final String TAG = "InAppBillingHstActivity";
	private InAppBillingHstFragment mFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// フラグメントインスタンスを作成
		mFragment = new StaticInAppBillingHstFragment();
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, mFragment).commit();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static class StaticInAppBillingHstFragment extends InAppBillingHstFragment {
	}
}

/**
 * タイマーフラグメント
 */
class InAppBillingHstFragment extends MyBaseFragment {
	private static final String TAG = "InAppBillingHstFragment";
	private ListView			mListViewProducts = null;
	private ListView			mListViewHistories = null;
	private List<Purchased>		mDataProducts;
	private List<PurchasedHst>	mDataHist;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreateView start"); }
		if (container == null) {
			return null;
		}
		View v = inflater.inflate(R.layout.in_app_billing_hst, container, false);

		mActivity = this.getActivity();
		mRootView = v;
		
		
		mListViewProducts = (ListView)v.findViewById(R.id.listProducts);
		mListViewProducts.setCacheColorHint(Color.TRANSPARENT); 
		mListViewHistories = (ListView)v.findViewById(R.id.listHistories);
		mListViewHistories.setCacheColorHint(Color.TRANSPARENT); 

		PurchaseAccessor acc = new PurchaseAccessor(mActivity);
		mDataProducts = acc.loadPurchased();
		mDataHist = acc.loadPurchasedHistory();
		
		PurchasedListAdapter adapterProducts = new PurchasedListAdapter(mActivity, mDataProducts);
		mListViewProducts.setAdapter(adapterProducts);

		PurchasedHstListAdapter dadapterHst = new PurchasedHstListAdapter(mActivity, mDataHist);
		mListViewHistories.setAdapter(dadapterHst);
		
		
		if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreateView end"); }
		return v;	
	}

	/**
	 * 購入リストアダプター
	 */
	private class PurchasedListAdapter extends ArrayAdapter<Purchased> {
		private LayoutInflater 	mInflater;

		public PurchasedListAdapter(Context context, List<Purchased> objects) {
			super(context, 0, objects);
    		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.in_app_billing_hst_products_row, null);
			}
			TextView txt;
			Purchased item = this.getItem(position);
			
			txt = (TextView)view.findViewById(R.id.textPrID);
			txt.setText(item.getPrID());
			
			txt = (TextView)view.findViewById(R.id.textQuantity);
			txt.setText("" + item.getQuantity());
			return view;
		}		
	}
	


	/**
	 * 購入履歴リストアダプター
	 */
	private class PurchasedHstListAdapter extends ArrayAdapter<PurchasedHst> {
		private LayoutInflater 	mInflater;

		public PurchasedHstListAdapter(Context context, List<PurchasedHst> objects) {
			super(context, 0, objects);
    		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.in_app_billing_hst_product_hst_row, null);
			}
			TextView txt;
			PurchasedHst item = this.getItem(position);
			
			txt = (TextView)view.findViewById(R.id.textOrderID);
			txt.setText(item.getOrderID());
			
			txt = (TextView)view.findViewById(R.id.textSts);
			txt.setText("" + item.getState());
			
			txt = (TextView)view.findViewById(R.id.textPrID);
			txt.setText("" + item.getPrID());
			
			txt = (TextView)view.findViewById(R.id.textDevPayload);
			txt.setText("" + item.getDeveloperPayload());
			
			txt = (TextView)view.findViewById(R.id.textDate);
			txt.setText("" + item.getPurchaseTime());
			return view;
		}		
	}
}