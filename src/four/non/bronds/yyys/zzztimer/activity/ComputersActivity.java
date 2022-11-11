package four.non.bronds.yyys.zzztimer.activity;

import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.Computer;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.ComputerAccessor;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.DateSlider;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.TimeSlider;

public class ComputersActivity {
	final static String TAG = "ComputersActivity";

}

/**
 * タイマーフラグメント
 */
class ComputersFragment extends MyBaseFragment {
	final static String TAG = "ComputersFragment";
	private ListView			mListView = null;
	private LinearLayout		mNoItemLayout = null;
	

    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		View v = null;
		try {
			if (container == null) {
				return null;
			}
			v = inflater.inflate(R.layout.computers, container, false);
			mActivity = this.getActivity();
			mRootView = v;
			
			mNoItemLayout = (LinearLayout)v.findViewById(R.id.layoutNoItem);
			/*
			// 追加ボタン 
			ImageButton btn = (ImageButton)v.findViewById(R.id.btnAddComputer);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ComputersFragment.this.onAddBtnClick(v);
				}
			});
			btn.setBackgroundColor(Color.TRANSPARENT);
			*/
			mListView = (ListView)v.findViewById(R.id.listComputers);
			mListView.setCacheColorHint(Color.TRANSPARENT); 
			// フォーカスが当たらないよう設定  
			mListView.setItemsCanFocus(false);
			ComputerAccessor acc = new ComputerAccessor(mActivity);
			ListAdapter adapter = new ListAdapter(mActivity, acc.load());
			if( adapter.getCount() == 0 ) {
				mNoItemLayout.setVisibility(View.VISIBLE);
			} else {
				mNoItemLayout.setVisibility(View.GONE);
			}
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {
	    		@Override
	    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    			ComputersFragment.this.onListItemClick(parent, view, position, id);
	    		}
			});
			mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
	    		@Override
	    		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	    		
	    			return ComputersFragment.this.onListItemLongClick(parent, view, position, id);
	    		}
			});
			


		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView end");}
		}		
		return v;
	}
    
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	// オプションメニュー
    	setHasOptionsMenu(true);
    }
	
    // メニューの作成
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add(Menu.NONE, 0,Menu.NONE, this.getString(R.string.add));
        item.setIcon(R.drawable.add24);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM | MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
    }
    // オプションメニューアイテムが選択された時に呼び出されます
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if( item.getItemId() == 0 ) {
    		this.doAddComputer();	
    	}
    	return true;	
    }

	
	/**
	 * 再表示する
	 */
	public void reView() {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "reView start");}
		try {
			ComputerAccessor acc = new ComputerAccessor(mActivity);
			ListAdapter adapter = new ListAdapter(mActivity, acc.load());
			if( adapter.getCount() == 0 ) {
				mNoItemLayout.setVisibility(View.VISIBLE);
			} else {
				mNoItemLayout.setVisibility(View.GONE);
			}
			mListView.setAdapter(adapter);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "reView end");}
		}		
	}
	
	/**
	 * 追加ボタンクリック
	 * @param v
	 */
	public void onAddBtnClick(View v) {
		this.doAddComputer();	
	}
	
	/**
	 * リストアイテムがクリックされた
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	public void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Computer comp = (Computer)parent.getItemAtPosition(position);
		doRemoteOperationComputer(comp);
	}
	/**
	 * リストアイテムがロングタップされた
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 * @return
	 */
	public boolean onListItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onListItemLongClick start");}
		try {
			final Computer comp = (Computer)parent.getItemAtPosition(position);
			String titles[] = mActivity.getResources().getStringArray(R.array.computer_long_click_menu);
			new AlertDialog.Builder(mActivity)
			.setIcon(R.drawable.icon)
			.setTitle( mActivity.getResources().getString(R.string.dlg_title_computer_ope) )
			.setItems(titles,  new android.content.DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						switch( which ) {
						case 0:
							doRemoteOperationComputer(comp);
							break;
						case 1:
							doEditComputer(comp);
							break;
						case 2:
							doDeleteComputer(comp);
							break;
						}
						dialog.dismiss();
					} catch(Exception e) {
						
					}
				}})
			.show();	
		} catch(Exception e) {
			showAlert(e.getMessage());
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onListItemLongClick end");}
		}
		return true;
	}

	/**
	 * コンピュータの追加
	 */
	public void doAddComputer() {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "doAddComputer start");}
		try {
			if( mActivity instanceof ZzzTimerActivity ) {
				if(MyLog.isDebugMod()) {MyLog.logt(TAG, "DisplayMode : " + MyLog.getDisp());}
/*
				if( MyLog.getDisp() == MyLog.DISP_NORMAL) {
*/
					// Normal画面の時は、Activityを起動
					Intent intent = new Intent(mActivity, ComputerActivity.class);
					startActivityForResult(intent, Constant.RQ_CODE_ADD_COMPUTER);
/*					
				} else {
					// Large画面の時は、Fragmentの切り替え
					MainActivity ac = (MainActivity)mActivity;
					ComputerFragment compFragment = (ComputerFragment)ac.mTabFragment.showDetails(TabsFragment.FRAGMENT_INDEX_COMPUTER_EDT);
					if( compFragment != null ) {
						compFragment.setItem(null);				
					}
				}
*/				
			} else {
				/*
	            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
	            ComputerFragment newFragment = new ComputerFragment();
	            ft.add(100, newFragment).commit();
	            */
				Intent intent = new Intent(mActivity, ComputerActivity.class);
				startActivityForResult(intent, Constant.RQ_CODE_ADD_COMPUTER);
			}
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "doAddComputer end");}
		}
	}
	
	/**
	 * リモートオペレーション
	 * @param comp
	 */
	public void doRemoteOperationComputer(Computer comp) { 
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "doRemoteOperationComputer start");}
		try {
			Intent intent = new Intent(this.mActivity, ComputerOpeActivity.class);
			intent.putExtra("COMPUTER", comp);
			startActivityForResult(intent, 0);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "doRemoteOperationComputer end");}
		}
	}
	/**
	 * 編集
	 * @param comp
	 */
	public void doEditComputer(Computer comp) { 
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "doEditComputer start");}
		try {
			
			if( mActivity instanceof ZzzTimerActivity ) {
				if(MyLog.isDebugMod()) {MyLog.logt(TAG, "DisplayMode : " + MyLog.getDisp());}
/*				
				if( MyLog.getDisp() == MyLog.DISP_NORMAL) {
*/				
					// Normal画面の時は、Activityを起動
					Intent intent = new Intent(this.mActivity, ComputerActivity.class);
					intent.putExtra("COMPUTER", comp);
					startActivityForResult(intent, 0);
/*					
				} else {
					// Large画面の時は、Fragmentの切り替え
					MainActivity ac = (MainActivity)mActivity;
					ComputerFragment compFragment = (ComputerFragment)ac.mTabFragment.showDetails(TabsFragment.FRAGMENT_INDEX_COMPUTER_EDT);
					if( compFragment != null ) {
						compFragment.setItem(comp);				
					}
				}
*/				
			} else {
				Intent intent = new Intent(this.mActivity, ComputerActivity.class);
				intent.putExtra("COMPUTER", comp);
				startActivityForResult(intent, 0);
			}
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "doEditComputer end");}
		}
		
	}
	/**
	 * 削除
	 * @param comp
	 */
	public void doDeleteComputer(Computer comp) { 
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "doDeleteComputer start");}
		try {
			final Computer compF = comp;

	    	new AlertDialog.Builder(mActivity)
	    	.setIcon(R.drawable.icon)
	    	.setTitle( this.getString(R.string.confirm) )
	    	.setMessage( this.getString(R.string.dlg_message_deleete_computer) )
	    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int whichButton) {
	    	    	ComputersFragment.this.doDeleteComputer2(compF);
	    	    }
	    	})
	    	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int whichButton) {
	    	    }
	    	})
	    	.show();
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "doDeleteComputer end");}
		}		
	}
	public void  doDeleteComputer2(Computer comp) { 
		new ComputerAccessor(mActivity).remote(comp);
		

		ComputerAccessor acc = new ComputerAccessor(mActivity);
		ListAdapter adapter = new ListAdapter(mActivity, acc.load());
		if( adapter.getCount() == 0 ) {
			mNoItemLayout.setVisibility(View.VISIBLE);
		} else {
			mNoItemLayout.setVisibility(View.GONE);
		}
		mListView.setAdapter(adapter);
	}


	/**
	 * コンピューターリストアダプター
	 */
	private class ListAdapter extends ArrayAdapter<Computer> {
		private LayoutInflater 	mInflater;
		
		public ListAdapter(Context context, List<Computer> objects) {
			super(context, 0, objects);
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.computer_list_row, null);
			}
			Computer comp = this.getItem(position);
			TextView  txtView = null;
			txtView = (TextView)view.findViewById(R.id.txtName);
			
			txtView.setText( comp.getName() );

			
			
			
			
			try {
    			StringBuilder sb = new StringBuilder();
    			sb.append("Host:");
    			sb.append(comp.getHostname());
    			sb.append("\nMAC Address:");
    			sb.append(comp.getMac_addr());
				txtView = (TextView)view.findViewById(R.id.textOtherInfo);
				txtView.setText( sb.toString() );
			} catch (Exception e) {
				e.printStackTrace();
			}
			return view;
		}
	}



}