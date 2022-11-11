package four.non.bronds.yyys.zzztimer.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.RecFileInf;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.RecDbAccessor;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;



public class RecListActivity {

}

/**
 *	録音データリストフラグメント
 */
class RecListFragment extends MyBaseFragment {
	final static String TAG = "RecListFragment";
	private ListView		mListRecFiles;
	

    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		View v = null;
		try {
			if (container == null) {
				return null;
			}
			v = inflater.inflate(R.layout.rec_list, container, false);
			mActivity = this.getActivity();
			mRootView = v;
			
			mListRecFiles = (ListView)mRootView.findViewById(R.id.listRecFiles);
	        // リスト
	        mListRecFiles.setCacheColorHint(Color.TRANSPARENT); 
	        mListRecFiles.setOnItemClickListener(new OnItemClickListener() {
	    		@Override
	    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    			// タップで再生
	    			RecListFragment.this.playRecFileInf( (RecFileInf)parent.getItemAtPosition(position));
	    		}
	    	});
	        mListRecFiles.setOnItemLongClickListener(new OnItemLongClickListener() {
	    		@Override
	    		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	    			// ロングタップで編集
	    			return RecListFragment.this.onListLongClick(parent, view, position, id);
	    		}
	    	} );
			


		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView end");}
		}		
		return v;
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

    	
    	// オプションメニュー
    	//setHasOptionsMenu(true);
	}
    // メニューの作成
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	
    }
    // オプションメニューアイテムが選択された時に呼び出されます
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	return true;
    }
    @Override
    public void onResume() {
    	super.onResume();
    	
    	this.reView();
    }
	/**
	 * 再表示する
	 */
	public void reView() {

		if( mListRecFiles == null ) {
			return ;
		}

		
		// リストを再表示
        RecFileItemAdapter adapter = new RecFileItemAdapter(mActivity, new RecDbAccessor(mActivity).load());
        mListRecFiles.setAdapter(adapter);
	}
	

    /**
     * リストメニューのロングタッチの動作
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    private boolean onListLongClick(AdapterView<?> parent, View view, int position, long id) {
		final RecFileInf rcf = (RecFileInf)parent.getItemAtPosition(position);
		
		String titles[] = mActivity.getResources().getStringArray(R.array.rec_file_long_click_menu);
		new AlertDialog.Builder(mActivity)
		.setIcon(R.drawable.icon)
		.setTitle(this.getActivity().getString(R.string.menu))
		.setItems(titles,  new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				
				try {
					switch( which ) {
					case 0:
						playRecFileInf(rcf);
						break;
					case 1:
						editRecFileTitle(rcf);
						break;
					case 2:
						deleteRecFileTitle(rcf);
						break;
					case 3:
						mailRecFileTitle(rcf);
						break;
					case 4:
						shareRecFileTitle(rcf);
						break;
					}
					dialog.dismiss();
				} catch(Exception e) {
					
				}
			}
		})
		.show();
		
    	return true;
    }

    /**
     * WAVファイルを再生する
     * @param rcf
     */
    private void playRecFileInf(RecFileInf rcf) {
    	
    	Intent intent = new Intent(mActivity, RecPlayActivity.class);
    	intent.putExtra("MODE" , "ZZZ");
    	intent.putExtra("FNAME" , rcf.getFName());
    	intent.putExtra("RecFileInf", rcf);
    	startActivityForResult(intent, Constant.RQ_CODE_MUSIC_PLAY_LIST_EDIT);
//		Uri uri = Uri.fromFile(new File(rcf.getFName() ));
//		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
//		intent.setAction(android.content.Intent.ACTION_VIEW);
//		intent.setDataAndType(uri, "audio/x-wav");
//		mActivity.startActivity(Intent.createChooser(
//		            intent, this.getString(R.string.select_action)));
    }    
    /**
     * WAVファイルのタイトルを変更する。
     * @param rcf
     */
    private void editRecFileTitle(RecFileInf rcf) {
    	final RecFileInf frcf = rcf;
    	final EditText edtInput;
    	edtInput = new EditText(mActivity);
    	
    	new AlertDialog.Builder(mActivity)
        .setIcon(R.drawable.icon)
        .setTitle( this.getString(R.string.please_input_title) )
        .setView(edtInput)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        		
        		String strDisp = edtInput.getEditableText().toString();
        		if( strDisp.length() == 0 ) {
        			RecListFragment.this.showAlert( RecListFragment.this.mActivity.getString(R.string.no_specify_title) );
        			return ;
        		}
        		
        		
        		frcf.setDisp(
        				edtInput.getEditableText().toString()
        		);
        		
        		new RecDbAccessor(RecListFragment.this.mActivity).update(frcf);
        		RecListFragment.this.reView();
        	}
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {

        	}
        })
        .show();
    	
    }
    /**
     * WAVファイルを削除。
     * @param rcf
     */
    private void deleteRecFileTitle(RecFileInf rcf) {
    	final RecFileInf frcf = rcf;
    	new AlertDialog.Builder(mActivity)
    	.setIcon(R.drawable.icon)
    	.setTitle( this.getString(R.string.confirm) )
    	.setMessage( this.getString(R.string.are_you_del_file) )
    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
        		new RecDbAccessor(RecListFragment.this.mActivity).del( frcf.getFName() );
        		RecListFragment.this.reView();
    	    }
    	})
    	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	    }
    	})
    	.show();
    }
    /**
     * WAVファイルのタイトルを変更する。
     * @param rcf
     */
    private void mailRecFileTitle(RecFileInf rcf) {
    	Uri uri = Uri.fromFile(new File(rcf.getFName() ));

    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.putExtra(Intent.EXTRA_SUBJECT,"ZzzTimer Record file. " + rcf.getDateStr());
    	intent.putExtra(Intent.EXTRA_STREAM, uri );
    	intent.putExtra(Intent.EXTRA_TEXT, "ZzzTimer Record file.\n"
    			+ "Title : " + rcf.getDisp() + "\n"
    			+ "Bitrate : " + rcf.getBitsPer() + "Hz\n"
    			+ "Bits: " + rcf.getBit() + "\n"
    			+ "Chanel : Monoral" + "\n"
    			+ "Date :" + rcf.getDateStr() + "\n"
    			+ "Play time : " + rcf.getPlayTime()
    			
    			);
    	intent.setType("audio/wav");
    	mActivity.startActivity(Intent.createChooser(intent, "Choose Email Client")); 
    }
    /**
     * WAVファイルを共有
     * @param rcf
     */
    private void shareRecFileTitle(RecFileInf rcf) {
    	Uri uri = Uri.fromFile(new File(rcf.getFName() ));

    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.putExtra(Intent.EXTRA_SUBJECT,"ZzzTimer Record file " + rcf.getDateStr());
    	intent.putExtra(Intent.EXTRA_STREAM, uri );
    	intent.putExtra(Intent.EXTRA_TEXT, "ZzzTimer Record file.\n"
    			+ "Title : " + rcf.getDisp() + "\n"
    			+ "Bitrate : " + rcf.getBitsPer() + "Hz\n"
    			+ "Bits: " + rcf.getBit() + "\n"
    			+ "Chanel : Monoral" + "\n"
    			+ "Date :" + rcf.getDateStr() + "\n"
    			+ "Play time : " + rcf.getPlayTime()
    			
    			);
    	intent.setType("audio/wav");
    	mActivity.startActivity(Intent.createChooser(intent, "Choose Share Client")); 
    }

    /**
     * リストビューのアダプター
     * @author user
     *
     */
    private class RecFileItemAdapter extends ArrayAdapter<RecFileInf> {
    	private LayoutInflater 	mInflater;
    	private java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"MM/dd HH:mm:ss");
    	public RecFileItemAdapter(Context context, List<RecFileInf> objects) {
    		super(context, 0, objects);
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		
    		
    	}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.rec_list_row, null);
			}
			RecFileInf rcf = this.getItem(position);

			
			TextView  txtView = null;
			
			
			// textItemName
			txtView = (TextView)view.findViewById(R.id.textItemName);
			txtView.setText( rcf.getDisp() );
			
			// textOther
			txtView = (TextView)view.findViewById(R.id.textOther);
			long lVal = rcf.getTime() / 1000;	// 秒にする
			long lHour   = lVal / (60*60);
			long lMinite = (lVal - ( lHour * (60*60) ))/60;
			long lSec    = lVal % 60;
			double dSize = (double)rcf.getSize();
			DecimalFormat df1 = new DecimalFormat("###.###");
			txtView.setText(String.format("%02d:%02d:%02d", lHour, lMinite, lSec)
					+ "       " 
					+ formatter.format(rcf.getRecDate())
					+ "       "
					+ df1.format(dSize / (1024*1024)) + " MB");
			return view;
		}
    }
}