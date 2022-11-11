package four.non.bronds.yyys.zzztimer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.activity.TimerFragment.TimerItemImputDialogFragment;
import four.non.bronds.yyys.zzztimer.bean.Computer;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.TimerSettingAccessor;
import four.non.bronds.yyys.zzztimer.dialog.MyAlertDialog;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.MusicUtils;
import four.non.bronds.yyys.zzztimer.util.PrefereceAcc;
import four.non.bronds.yyys.zzztimer.util.StrageUtil;

public class MusicPlayListEditActivity extends MyBaseActivity {
	private static final String TAG = "MusicPlayListEditActivity";
	private StaticCMusicPlayListEditFragment mFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// フラグメントインスタンスを作成
		mFragment = new StaticCMusicPlayListEditFragment();
		getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, mFragment).commit();
		
	}
	public static class StaticCMusicPlayListEditFragment extends MusicPlayListEditFragment {}
}

/**
 * @author user
 *
 */
class MusicPlayListEditFragment extends MyBaseFragment
{
	final static String TAG = "MusicPlayListEditFragment";	
	private ListView		mListPlayList = null;
	private ListView		mListArtists = null;
	private ProgressBar		mProgressBar;
	private List<HashMap<String, String>>	mPlaylistData;
	private List<HashMap<String, String>>	mArtistData;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {
			if (container == null) {
				return null;
			}
			mActivity = this.getActivity();
			mRootView = inflater.inflate(R.layout.music_play_list_edit, container, false);

			mProgressBar = (ProgressBar)mRootView.findViewById(R.id.progressBar);
			
			
			mListPlayList = (ListView)mRootView.findViewById(R.id.listViewPlayList);
			mListPlayList.setCacheColorHint(Color.TRANSPARENT); 
			mListPlayList.setItemsCanFocus(false);
			mListPlayList.setOnItemClickListener(new OnItemClickListener() {
	    		@Override
	    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					@SuppressWarnings("unchecked")
					HashMap<String, String> data = (HashMap<String, String>)parent.getItemAtPosition(position);
	    			
	    			Intent intent = new Intent(mActivity, MusicPlaylistActivity.class);
	    			Set<String> keys = data.keySet();
	    			for(String key : keys) {
	    				intent.putExtra(key, data.get(key));
	    			}
	    			startActivityForResult(intent, Constant.RQ_CODE_MUSIC_PLAY_LIST_EDIT);
	    		}
			});
			mListPlayList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					HashMap<String, String> data = (HashMap<String, String>)parent.getItemAtPosition(position);
					int playlist_id = Integer.parseInt( data.get(MediaStore.Audio.Playlists._ID) );
					String name = data.get(MediaStore.Audio.Playlists.NAME);
					FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
					Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("playlist_dialog");
					if (prev != null) {
						ft.remove(prev);
					}
					//ft.addToBackStack(null);
					PlaylistSelectOpeDialogFragment dlg = PlaylistSelectOpeDialogFragment.newInstance(MusicPlayListEditFragment.this, playlist_id, name);
			    	dlg.show(ft, "playlist_dialog");

					return false;
				}
				
			});
			mListArtists = (ListView)mRootView.findViewById(R.id.listViewMusic);
			mListArtists.setCacheColorHint(Color.TRANSPARENT);
			mListArtists.setItemsCanFocus(false);
			mListArtists.setOnItemClickListener(new OnItemClickListener() {
	    		@Override
	    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					@SuppressWarnings("unchecked")
					HashMap<String, String> data = (HashMap<String, String>)parent.getItemAtPosition(position);
	    			
	    			
	    			Intent intent = new Intent(mActivity, MusicAlbumsActivity.class);
	    			Set<String> keys = data.keySet();
	    			for(String key : keys) {
	    				intent.putExtra(key, data.get(key));
	    			}
	    			startActivityForResult(intent, Constant.RQ_CODE_MUSIC_PLAY_LIST_EDIT);
	    		}
			});
			
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView end");}
		}		
		return mRootView;
	}
    
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	// オプションメニュー
    	setHasOptionsMenu(true);
    	
    	new MyLoader(this).execute("");
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
    		// プレイリストを追加
    		this.showPlaylistNameDlg();
    	}
    	return true;	
    }
    
    /**
     * プレイリスト名入力ダイアログを表示する
     * 
     */
    void showPlaylistNameDlg() {
		FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
		Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("playlist_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		
		PlaylistNameInputDialogFragment dlg = PlaylistNameInputDialogFragment.newInstance(this, -1, null);
		dlg.show(ft, "playlist_dialog");
    }
    /**
     * プレイリストを追加
     * @param strName
     */
    void doAddPlaylist(String strName) {
    	MusicUtils utl = new MusicUtils(mActivity);
    	utl.addPlaylist(strName, null, null);
    	
    	new MyLoader(this).execute("");
    }
    
	/**
	 * プレイリスト名変更ダイアログを表示
	 * @param playlist_id
	 * @param name
	 */
	void showPlaylistNameDlg(int playlist_id, String name) {
		FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
		Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("playlist_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		PlaylistNameInputDialogFragment dlg = PlaylistNameInputDialogFragment.newInstance(this, playlist_id, name);
		dlg.show(ft, "playlist_dialog");
    }
    
    /**
     * プレイリスト名変更
     * @param playlist_id
     * @param name
     */
    void doModPlaylistName(int playlist_id, String name) {
    	if(MyLog.isDebugMod()) {MyLog.logt(TAG, "doModPlaylistName playlist_id:" + playlist_id + "  " + name);}
    	MusicUtils utl = new MusicUtils(mActivity);
    	utl.updatePlaylist(playlist_id, name);

    	new MyLoader(this).execute("");
    }
    
    /**
     * プレイリスト削除確認画面を表示
     * @param playlist_id
     */
    void showPlaylistDeleteAskDlg(int playlist_id) {
		FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
		Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("playlist_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
    	PlaylistDelAskDialogFragment dlg = PlaylistDelAskDialogFragment.newInstance(this, playlist_id);
    	dlg.show(ft, "playlist_dialog");
    }
    /**
     * プレイリストを削除
     * @param playlist_id
     */
    void doDeletePlaylist(int playlist_id) {
    	MusicUtils utl = new MusicUtils(mActivity);
    	int[] ids = {playlist_id};
    	utl.removePlaylists(ids);

    	new MyLoader(this).execute("");
    }
    

    /**
     * データのロード
     * @throws Exception 
     */
    void innerLoad() throws Exception {

		if( StrageUtil.isSDcardExist( mActivity ) == false ) {
			throw new Exception( mActivity.getString( R.string.errMsgNotExistStrage ) );
		}
		// Playlistや、ArtistをIN-Strageから検索するか？
		String strWhere = new PrefereceAcc(mActivity).getMusicDBSearchLoc();

		MusicUtils musicUtil = new MusicUtils(mActivity);
    	mPlaylistData = new ArrayList<HashMap<String, String>>();
    	mArtistData = new ArrayList<HashMap<String, String>>();
    	
    	musicUtil.getPlaylist( mPlaylistData, strWhere );
    	musicUtil.getArtists( mArtistData, strWhere );
    }
    /**
     * 再描画
     */
	void reView() {
		PlatListListAdapter listPlayList = new PlatListListAdapter(mActivity, mPlaylistData);
		mListPlayList.setAdapter(listPlayList);
		
		ArtistListAdapter listArtists = new ArtistListAdapter(mActivity, mArtistData);
		mListArtists.setAdapter(listArtists);
    }

	/**
	 * プレイリスト－リストアダプター
	 */
	private class PlatListListAdapter extends ArrayAdapter<HashMap<String, String>> {
		private LayoutInflater 	mInflater;
		
		public PlatListListAdapter(Context context, List<HashMap<String, String>> objects) {
			super(context, 0, objects);
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.play_list_row, null);
			}
			HashMap<String, String> data = this.getItem(position);
			TextView  txtView = null;
			txtView = (TextView)view.findViewById(R.id.txtName);
			
			txtView.setText( data.get( MediaStore.Audio.Playlists.NAME ) );
			

			txtView = (TextView)view.findViewById(R.id.txtOther);
			txtView.setText( "" + data.get( MediaStore.Audio.Albums.NUMBER_OF_SONGS )+" TRACK" );
			

			txtView = (TextView)view.findViewById(R.id.txtTime);
			long lDuration = 0;
			try {
				lDuration = Long.parseLong((String)data.get(MediaStore.Audio.Media.DURATION));
			} catch(NumberFormatException ex ) {
			}
			txtView.setText( Formmater.formatTime(lDuration) );
			
			return view;
		}
	}
	
	/**
	 * アーティスト－リストアダプター
	 */
	private class ArtistListAdapter extends ArrayAdapter<HashMap<String, String>> {
		private LayoutInflater 	mInflater;
		
		public ArtistListAdapter(Context context, List<HashMap<String, String>> objects) {
			super(context, 0, objects);
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.music_artist_list_row, null);
			}
			HashMap<String, String> data = this.getItem(position);
			TextView  txtView = null;
			txtView = (TextView)view.findViewById(R.id.txtName);
			
			txtView.setText( data.get( MediaStore.Audio.Media.ARTIST ) );
			
			return view;
		}
	}

	/**
	 * 非同期データローダー
	 */
	class MyLoader extends AsyncTask<String, Integer, Integer> {
		private MusicPlayListEditFragment	mFragment;
		private Exception					mError;
		
		
		public MyLoader(MusicPlayListEditFragment fragment) {
			mFragment = fragment;
		}
		@Override
		protected void onPreExecute() {
			if( mFragment.mProgressBar != null ) {
				mFragment.mProgressBar.setVisibility(View.VISIBLE);
			}
		}
		@Override
		protected Integer doInBackground(String... arg0) {
			try {
				mError = null;
				mFragment.innerLoad();
			} catch(Exception e) {
				MyLog.loge(TAG, e);
				mError = e;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Integer retval) {
			if( mFragment.mProgressBar != null ) {
				mFragment.mProgressBar.setVisibility(View.GONE);
			}
			if( mError != null ) {
				mFragment.showAlert("" + mError.getMessage());
			} else {
				mFragment.reView();
			}
			
		}				
	}
		
	/**
	 * プレイリスト名入力ダイアログ
	 */
	public static class PlaylistNameInputDialogFragment extends DialogFragment {
		private MusicPlayListEditFragment		mParentFragment;
		private EditText						mEditItemName;
		private int								mPlaylistID = -1;
		private String							mName;
		
		static PlaylistNameInputDialogFragment newInstance(MusicPlayListEditFragment parentFragment, int playlist_id, String name) {
			PlaylistNameInputDialogFragment f = new PlaylistNameInputDialogFragment();
			f.mParentFragment = parentFragment;
			f.mPlaylistID = playlist_id;
			f.mName = name;
			Bundle args = new Bundle();
			f.setArguments(args);
			return f;
		}
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }

        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	
        	LayoutInflater inflater = getActivity().getLayoutInflater();  
        	View v = inflater.inflate(R.layout.dialog_enter_playlist_name, null, false);
        	//AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);
        	MyAlertDialog builder = new MyAlertDialog(getActivity()/*, R.style.MyLightTheme*/);
        	if( mPlaylistID == -1 ) {
        		builder.setTitle(R.string.title_add_playlist);
        	} else {
        		builder.setTitle(R.string.title_edt_playlist);
        	}
        	builder.setButton("OK",
//        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						onOkBtnCliced(dlg);
					}
        	});
        	builder.setButton2("Cancel",
//        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.playlist);
        	builder.setView(v);  
        	
        	mEditItemName = (EditText)v.findViewById(R.id.editItemName);
        	if( mName != null ) {
        		mEditItemName.setText(mName);
        	}
        	return builder;//.create();
        }
        
        /**
         * OKボタンをクリックしたときの処理
         */
        private void onOkBtnCliced(DialogInterface dlg) {
        	String strName  = mEditItemName.getEditableText().toString().trim();
        	
        	if( strName.length() == 0 ) {
        		dlg.cancel();
        		mParentFragment.showAlert(this.getString(R.string.not_spcify_name));
        		return ;
        	}
        	dlg.dismiss();
        	if( mPlaylistID == -1 ) {
        		mParentFragment.doAddPlaylist(strName);
        	} else {
        		mParentFragment.doModPlaylistName(mPlaylistID, strName);
        	}
        }
	}
	
	/**
	 * プレイリスト操作選択ダイアログ
	 */
	public static class PlaylistSelectOpeDialogFragment extends DialogFragment {
		private MusicPlayListEditFragment		mParentFragment;
		private int								mSelectItem = 0;
		private int								mPlaylistID = -1;
		private String							mName;
		
		static PlaylistSelectOpeDialogFragment newInstance(MusicPlayListEditFragment parentFragment, int playlist_id, String name) {
			PlaylistSelectOpeDialogFragment f = new PlaylistSelectOpeDialogFragment();
			f.mParentFragment = parentFragment;
			f.mPlaylistID = playlist_id;
			f.mName = name;
			Bundle args = new Bundle();
			f.setArguments(args);
			return f;
		}
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }

        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle( this.getString(R.string.title_ope_playlist) + " : " + mName );
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						if( mSelectItem == 0 ) {
							mParentFragment.showPlaylistNameDlg(mPlaylistID, mName);
						} else
						if( mSelectItem == 1 ) { 
							mParentFragment.showPlaylistDeleteAskDlg(mPlaylistID);
						}
						dlg.dismiss();
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.playlist);
        	builder.setSingleChoiceItems(R.array.playlist_ope, mPlaylistID, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectItem = which;
				}
        	});
        	return builder.create();
        }
	}	
		
	/**
	 * プレイリスト削除確認ダイアログを表示
	 */
	public static class PlaylistDelAskDialogFragment extends DialogFragment {
		private MusicPlayListEditFragment		mParentFragment;
		private int								mPlaylistID = -1;
		
		static PlaylistDelAskDialogFragment newInstance(MusicPlayListEditFragment parentFragment, int playlist_id) {
			PlaylistDelAskDialogFragment f = new PlaylistDelAskDialogFragment();
			f.mParentFragment = parentFragment;
			f.mPlaylistID = playlist_id;
			Bundle args = new Bundle();
			f.setArguments(args);
			return f;
		}
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }

        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.title_del_playlist);
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						dlg.dismiss();
						mParentFragment.doDeletePlaylist( mPlaylistID );
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.playlist);
        	builder.setMessage(R.string.msg_del_playlist);
        	return builder.create();
        }
	}
}