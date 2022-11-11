package four.non.bronds.yyys.zzztimer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.dialog.HelpDialog;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.ImageUtil;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.MusicUtils;
import four.non.bronds.yyys.zzztimer.util.PrefereceAcc;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MusicSongsActivity extends MyBaseActivity {
	private static final String TAG = "MusicAlbumsActivity";
	private StaticMusicSongsFragment mFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreate start");}
		try {

			super.onCreate(savedInstanceState);
			// フラグメントインスタンスを作成
			mFragment = new StaticMusicSongsFragment();
			mFragment.setArguments(this.getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, mFragment).commit();
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreate end");}
		}

	}

	public static class StaticMusicSongsFragment extends MusicSongsFragment {
	}
}

class MusicSongsFragment extends MyBaseFragment {
	final static String TAG = "MusicSongsFragment";
	private ListView mListView = null;

	private String mArtistID;
	private String mArtist;
	private String mArtistKey;
	private String mAlbum;
	private String mAlbumKey;
	private String mAlbumID;
	private String mAlbumArt;
	private String mNumberOfSongs;
	private String mWhere;
	private ProgressBar mProgressBar;
	private List<HashMap<String, Object>> mData;
	private List<HashMap<String, String>> mPlaylist;
	private Handler	mOptMenuHander = null;
	private Runnable mOptMenuRunnable = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {
			if (container == null) {
				return null;
			}
			mActivity = this.getActivity();
			mRootView = inflater
					.inflate(R.layout.music_songs, container, false);

			mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);

			mListView = (ListView) mRootView.findViewById(R.id.listView);
			mListView.setCacheColorHint(Color.TRANSPARENT);
			mListView.setItemsCanFocus(false);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onItemClick start");}
					try {
						@SuppressWarnings("unchecked")
						HashMap<String, String> data = (HashMap<String, String>) parent.getItemAtPosition(position);
	
						Intent intent = new Intent(mActivity,MusicPlayActivity.class);
	
						intent.putExtra(MediaStore.Audio.Media.ARTIST_ID, mArtistID);
						intent.putExtra(MediaStore.Audio.Media.ARTIST, mArtist);
						intent.putExtra(MediaStore.Audio.Media.ARTIST_KEY,mArtistKey);
	
						intent.putExtra(MediaStore.Audio.Media.ALBUM, mAlbum);
						intent.putExtra(MediaStore.Audio.Media.ALBUM_KEY, mAlbumKey);
						intent.putExtra(MediaStore.Audio.Media.ALBUM_ID, mAlbumID);
						intent.putExtra(MediaStore.Audio.Media.ALBUM_ART, mAlbumArt);
						intent.putExtra(MediaStore.Audio.Albums.NUMBER_OF_SONGS,mNumberOfSongs);
	
						intent.putExtra(MediaStore.Audio.Media.TITLE_KEY,data.get(MediaStore.Audio.Media.TITLE_KEY));
	
						intent.putExtra("MODE", "ALBUM");
	
						startActivityForResult(intent,
								Constant.RQ_CODE_MUSIC_PLAY_LIST_EDIT);
					} catch(Exception e) {
						MyLog.loge(TAG, e);
					} finally {
						if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onItemClick end2");}
					}
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

		ImageView img;
		TextView txt;

		img = (ImageView) mRootView.findViewById(R.id.imageIcon);
		if (mAlbumArt != null) {
			Bitmap bitmap = ImageUtil.LoadImageFile(mAlbumArt, 64, 64);
			img.setImageBitmap(bitmap);
		}

		txt = (TextView) mRootView.findViewById(R.id.textTitle);
		txt.setText(mAlbum);
		txt = (TextView) mRootView.findViewById(R.id.txtArtist);
		txt.setText(mArtist);

		new MyLoader(this).execute("");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mArtistID = args.getString(MediaStore.Audio.Media.ARTIST_ID);
			mArtist = args.getString(MediaStore.Audio.Media.ARTIST);
			mArtistKey = args.getString(MediaStore.Audio.Media.ARTIST_KEY);

			mAlbum = args.getString(MediaStore.Audio.Media.ALBUM);
			mAlbumKey = args.getString(MediaStore.Audio.Media.ALBUM_KEY);
			mAlbumID = args.getString(MediaStore.Audio.Media.ALBUM_ID);
			mAlbumArt = args.getString(MediaStore.Audio.Media.ALBUM_ART);
			mNumberOfSongs = args
					.getString(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
			mWhere = args.getString(MusicUtils.SPECIAL_KEY_WHERE);
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "mWhere:" + mWhere);}
		}
	}

	// メニューの作成
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.add(Menu.NONE, 0, Menu.NONE,
				this.getString(R.string.add));
		item.setIcon(R.drawable.add24);
		MenuItemCompat.setShowAsAction(item,
				MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

		item = menu.add(Menu.NONE, 1, Menu.NONE, this.getString(R.string.select_all));
		item.setIcon(R.drawable.select_all);
		MenuItemCompat.setShowAsAction(item,
				MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

		item = menu.add(Menu.NONE, 2, Menu.NONE, this.getString(R.string.clear_all));
		item.setIcon(R.drawable.deselect_all);
		MenuItemCompat.setShowAsAction(item,
				MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		

        item = menu.add(1, 100, 1, this.getActivity().getString(R.string.help));
        item.setIcon( android.R.drawable.ic_menu_help);
        MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM );
	}

	// オプションメニューアイテムが選択された時に呼び出されます
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			
			if( mPlaylist.size() == 0 ) {
				showAlert( this.getString(R.string.playlist_no_exist) );
				return true;
			}
			
			this.showAddPlaylistItemDlg();
		} else
		if (item.getItemId() == 1) {
			for(HashMap<String, Object> data : mData) {
				data.put("CHK", new Boolean(true));
			}
			AlbumListAdapter adapter = new AlbumListAdapter(this, mActivity, mData);
			mListView.setAdapter(adapter);
		} else
		if (item.getItemId() == 2) {
			for(HashMap<String, Object> data : mData) {
				data.put("CHK", new Boolean(false));
			}
			AlbumListAdapter adapter = new AlbumListAdapter(this, mActivity, mData);
			mListView.setAdapter(adapter);
		} else
		if (item.getItemId() == 100) {
    		PrefereceAcc acc = new PrefereceAcc(this.getActivity());
    		String strURL = acc.getHelpURL(PrefereceAcc.HELP_URL.HELP_URL_PLAYLIST);
    		HelpDialog.show(this.getActivity(), strURL);
		}
		return true;
	}

	/**
	 * チェックボックスをチェックした時に、オプションメニューを表示する。
	 */
	void onCheckItem() {
		if( mOptMenuHander != null ){
			try {
				mOptMenuHander.removeCallbacks(mOptMenuRunnable);
			} catch(Exception e) {}
		}
		try {
			mOptMenuHander = new Handler();
			mOptMenuRunnable = new Runnable() {
				public void run() {
					MusicSongsFragment.this.mActivity.openOptionsMenu();
				}
			};
			mOptMenuHander.postDelayed(mOptMenuRunnable, 1000);
		} catch(Exception e) {}
	}

	/**
	 * リストアイテムのクリック処理
	 * @param data
	 */
	void onListClickOnItem(HashMap<String, Object> data) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onListClickOnItem start");}
		try {
			@SuppressWarnings("unchecked")
			Intent intent = new Intent(mActivity, MusicPlayActivity.class);
	
			intent.putExtra(MediaStore.Audio.Media.ARTIST_ID, mArtistID);
			intent.putExtra(MediaStore.Audio.Media.ARTIST, mArtist);
			intent.putExtra(MediaStore.Audio.Media.ARTIST_KEY, mArtistKey);
	
			intent.putExtra(MediaStore.Audio.Media.ALBUM, mAlbum);
			intent.putExtra(MediaStore.Audio.Media.ALBUM_KEY, mAlbumKey);
			intent.putExtra(MediaStore.Audio.Media.ALBUM_ID, mAlbumID);
			intent.putExtra(MediaStore.Audio.Media.ALBUM_ART, mAlbumArt);
			intent.putExtra(MediaStore.Audio.Albums.NUMBER_OF_SONGS, mNumberOfSongs);
	
			intent.putExtra(MediaStore.Audio.Media.TITLE_KEY,
					(String) data.get(MediaStore.Audio.Media.TITLE_KEY));
	
			intent.putExtra("MODE", "ALBUM");
	
			startActivityForResult(intent, Constant.RQ_CODE_MUSIC_PLAY_LIST_EDIT);
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onListClickOnItem end");}
		}
	}

	/**
	 * Playlistにアイテムを追加するための、Playlist選択画面の表示
	 */
	void showAddPlaylistItemDlg() {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "showAddPlaylistItemDlg start");}
		try {
			FragmentTransaction ft = mActivity.getSupportFragmentManager()
					.beginTransaction();
			Fragment prev = mActivity.getSupportFragmentManager()
					.findFragmentByTag("playlist_input_dialog");
			if (prev != null) {
				ft.remove(prev);
			}
			ft.addToBackStack(null);
			PlaylistNameInputDialogFragment dlg = PlaylistNameInputDialogFragment
					.newInstance(this, mPlaylist);
			dlg.show(ft, "playlist_input_dialog");
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "showAddPlaylistItemDlg end");}
		}
	}

	/**
	 * プレイリストのアイテムを追加
	 * @param index
	 */
	void addPlaylistItem(int index) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "addPlaylistItem start");}
		try {
			if(MyLog.isDebugMod()) {
				MyLog.logt(TAG, "mPlaylist.size:" + mPlaylist.size());
				MyLog.logt(TAG, " index:" + index);
			}
	
			HashMap<String, String> playList = mPlaylist.get(index);
			int playlist_id = Integer.parseInt(playList
					.get(MediaStore.Audio.Playlists._ID));
			if(MyLog.isDebugMod()) {MyLog.logt(TAG, " playlist_id:" + playlist_id);}
			MusicUtils utl = new MusicUtils(mActivity);
	
			for (HashMap<String, Object> data : mData) {
				boolean b = (Boolean) data.get("CHK");
				if (b) {
					int audio_id = Integer.parseInt((String) data
							.get(MediaStore.Audio.Media._ID));
					if(MyLog.isDebugMod()) {MyLog.logt(TAG, " audio_id:" + audio_id);}
					utl.addMusicToPlaylist(playlist_id, audio_id, null, 0);
				}
			}
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "addPlaylistItem end");}
		}
	}

	/**
	 * データのロード
	 */
	void innerLoad() {
		try {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "innerLoad start");}
			MusicUtils musicUtil = new MusicUtils(mActivity);

			mData = new ArrayList<HashMap<String, Object>>();
			mPlaylist = new ArrayList<HashMap<String, String>>();

			// Playlistや、ArtistをIN-Strageから検索するか？
			String strWhere = new PrefereceAcc(mActivity).getMusicDBSearchLoc();
			
			musicUtil.getSongs(mArtistKey, mWhere, mAlbumKey, mData);
			musicUtil.getPlaylist(mPlaylist, strWhere);
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "innerLoad end");}
		}
	}

	/**
	 * 再表示
	 */
	void reView() {
		AlbumListAdapter adapter = new AlbumListAdapter(this, mActivity, mData);
		mListView.setAdapter(adapter);
	}

	/**
	 * プレイリスト－リストアダプター
	 */
	private class AlbumListAdapter extends
			ArrayAdapter<HashMap<String, Object>> implements
			View.OnTouchListener {
		private LayoutInflater mInflater;
		private MusicSongsFragment mFragment;

		public AlbumListAdapter(MusicSongsFragment fragment, Context context,
				List<HashMap<String, Object>> objects) {
			super(context, 0, objects);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mFragment = fragment;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.music_songs_list_row, null);
			}
			HashMap<String, Object> data = this.getItem(position);
			TextView txtView = null;
			txtView = (TextView) view.findViewById(R.id.txtName);
			txtView.setTag(data);
			txtView.setOnTouchListener(this);
			txtView.setText((String) data.get(MediaStore.Audio.Media.TITLE));

			CheckBox chk = (CheckBox) view.findViewById(R.id.checkBox);
			// chk.setText( (String)data.get( MediaStore.Audio.Media.TITLE) );
			chk.setTag(data);
			chk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					HashMap<String, Object> data = (HashMap<String, Object>) v.getTag();
					if (data != null) {
						CheckBox chk = (CheckBox) v;
						data.put("CHK", new Boolean(chk.isChecked()));
						mFragment.onCheckItem();
					}
				}

			});
			chk.setChecked((Boolean) data.get("CHK"));

			txtView = (TextView) view.findViewById(R.id.txtOther);
			txtView.setTag(data);
			txtView.setOnTouchListener(this);
			long trak = 0;
			if( data.get(MediaStore.Audio.Media.TRACK) != null ) {
				trak = Long.parseLong((String) data.get(MediaStore.Audio.Media.TRACK));
			}
			if (trak > 1000) {
				trak -= 1000;
			}
			String str = "";
			boolean bSetConma = false;
			if( trak != 0 ) {
				str += trak;
				bSetConma = true;
			}
			if( data.get(MediaStore.Audio.Media.YEAR) != null ) {
				if( bSetConma == true ) {
					str += " ";
				}
				str += "(";
				str += data.get(MediaStore.Audio.Media.YEAR);
				str += ")";
				bSetConma = true;
			}
			if( data.get(MediaStore.Audio.Media.MIME_TYPE) != null ) {
				if( bSetConma == true ) {
					str += " ";
				}
				String strMIME = (String)data.get(MediaStore.Audio.Media.MIME_TYPE);
				if( strMIME.indexOf("audio/") != -1 ) {
					strMIME = strMIME.substring(6);
				}
				str += strMIME;
				bSetConma = true;
			}

			txtView.setText( str );

			txtView = (TextView) view.findViewById(R.id.txtTime);
			long lDuration = Long.parseLong((String) data
					.get(MediaStore.Audio.Media.DURATION));
			txtView.setText(Formmater.formatTime(lDuration));
			return view;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				HashMap<String, Object> data = (HashMap<String, Object>) v.getTag();
				if (data != null) {
					MusicSongsFragment.this.onListClickOnItem(data);
				}
			}
			return true;
		}
	}

	/**
	 * 非同期データローダー
	 */
	class MyLoader extends AsyncTask<String, Integer, Integer> {
		private MusicSongsFragment mFragment;
		private ProgressBar mProgressBar = null;

		public MyLoader(MusicSongsFragment fragment) {
			mFragment = fragment;
		}

		@Override
		protected void onPreExecute() {
			if (mFragment.mProgressBar != null) {
				mFragment.mProgressBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected Integer doInBackground(String... arg0) {
			mFragment.innerLoad();
			return null;
		}

		@Override
		protected void onPostExecute(Integer retval) {
			if (mFragment.mProgressBar != null) {
				mFragment.mProgressBar.setVisibility(View.GONE);
			}
			mFragment.reView();
		}
	}

	/**
	 * Playlistにアイテムを追加するための、Playlist選択画面
	 */
	public static class PlaylistNameInputDialogFragment extends DialogFragment {
		private MusicSongsFragment mParentFragment;
		private String[] mPlaylistNames;
		private int mSelectItem = 0;

		static PlaylistNameInputDialogFragment newInstance(
				MusicSongsFragment parentFragment,
				List<HashMap<String, String>> playlist) {
			int index = 0;
			PlaylistNameInputDialogFragment f = new PlaylistNameInputDialogFragment();
			f.mParentFragment = parentFragment;

			f.mPlaylistNames = new String[playlist.size()];

			for (HashMap<String, String> data : playlist) {
				String str = data.get(MediaStore.Audio.Playlists.NAME);
				f.mPlaylistNames[index] = str;
				index++;
			}

			Bundle args = new Bundle();

			f.setArguments(args);
			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);// R.style.MyLightTheme);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*
																				 * ,R.style.MyLightTheme
																				 */);
			
			builder.setTitle(R.string.title_add_playlist_member);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dlg, int item) {
							mParentFragment.addPlaylistItem(mSelectItem);
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dlg, int arg1) {
							dlg.dismiss();
						}
					});
			builder.setSingleChoiceItems(mPlaylistNames, mSelectItem,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mSelectItem = which;
						}
					});
			builder.setIcon(R.drawable.playlist);
			return builder.create();
		}

	}
}