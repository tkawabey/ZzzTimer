package four.non.bronds.yyys.zzztimer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.ImageUtil;
import four.non.bronds.yyys.zzztimer.util.MusicUtils;
import four.non.bronds.yyys.zzztimer.util.MyLog;

public class MusicPlaylistActivity  extends MyBaseActivity {
	private static final String TAG = "MusicAlbumsActivity";
	private StaticMusicPlaylistFragment mFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// フラグメントインスタンスを作成
		mFragment = new StaticMusicPlaylistFragment();
		mFragment.setArguments(this.getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, mFragment).commit();
	}
	public static class StaticMusicPlaylistFragment extends MusicPlaylistFragment {}
}

class MusicPlaylistFragment extends MyBaseFragment
{
	final static String TAG = "MusicPlaylistFragment";
	private ListView		mListView = null;
	private String			mID;
	private String			mName;
	private HashMap<String, Bitmap>	mAlbumArts = new HashMap<String, Bitmap>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {
			if (container == null) {
				return null;
			}
			mActivity = this.getActivity();
			mRootView = inflater.inflate(R.layout.music_playlist, container, false);
			
			
			mListView = (ListView)mRootView.findViewById(R.id.listView);
			mListView.setCacheColorHint(Color.TRANSPARENT);
			mListView.setItemsCanFocus(false);
			mListView.setOnItemClickListener(new OnItemClickListener() {
	    		@Override
	    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					@SuppressWarnings("unchecked")
					HashMap<String, String> data = (HashMap<String, String>)parent.getItemAtPosition(position);

					Intent intent = new Intent(mActivity, MusicPlayActivity.class);

					intent.putExtra("MODE" , "PLAYLIST");
					intent.putExtra(MediaStore.Audio.Playlists.NAME , mName);
					intent.putExtra(MediaStore.Audio.Playlists._ID ,  mID);
					intent.putExtra(MediaStore.Audio.Playlists.Members.TITLE_KEY , data.get(MediaStore.Audio.Playlists.Members.TITLE_KEY));
					
					startActivityForResult(intent, Constant.RQ_CODE_MUSIC_PLAY_LIST_EDIT);
	    		}
			});
			mListView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					@SuppressWarnings("unchecked")
					HashMap<String, String> data = (HashMap<String, String>)parent.getItemAtPosition(position);
					FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
					Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("playlist_dialog");
					if (prev != null) {
						ft.remove(prev);
					}
					PlaylistMemberDelAskDialogFragment dlg = PlaylistMemberDelAskDialogFragment.newInstance( MusicPlaylistFragment.this, data);
			    	dlg.show(ft, "playlist_dialog");
					return false;
				}
				
			});
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView end");}
		}		
		return mRootView;
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
        	mID = args.getString( MediaStore.Audio.Playlists._ID );
        	mName = args.getString( MediaStore.Audio.Playlists.NAME );
        }
	}
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	TextView txtV = (TextView)mRootView.findViewById(R.id.textTitle);
    	txtV.setText(mName);
    	
    	List<HashMap<String, String>> list = getPlaylistMember( Integer.parseInt(mID) );
    	loadAlbumArts(list, mAlbumArts);
    	PlaylistMemberListAdapter adapter = new PlaylistMemberListAdapter(this, mActivity, list);
    	mListView.setAdapter(adapter);
    }
	
	/**
	 * プレイリスト－リストアダプター
	 */
	static class PlaylistMemberListAdapter extends ArrayAdapter<HashMap<String, String>> {
		private LayoutInflater 	mInflater;
		private MusicPlaylistFragment mFragment;
		
		public PlaylistMemberListAdapter(MusicPlaylistFragment fragment, Context context, List<HashMap<String, String>> objects) {
			super(context, 0, objects);
			mFragment = fragment;
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.music_playlist_list_row, null);
			}
			HashMap<String, String> data = this.getItem(position);
			TextView  txtView = null;
			txtView = (TextView)view.findViewById(R.id.txtName);
			
			txtView.setText( data.get( MediaStore.Audio.Playlists.Members.TITLE ) );

			txtView = (TextView)view.findViewById(R.id.txtAtrist);
			txtView.setText( data.get( MediaStore.Audio.Playlists.Members.ARTIST ) );

			txtView = (TextView)view.findViewById(R.id.txtAlbum);
			txtView.setText( data.get( MediaStore.Audio.Playlists.Members.ALBUM )
					+ "(" + data.get( MediaStore.Audio.Playlists.Members.YEAR ) + ")");
			
			txtView = (TextView)view.findViewById(R.id.txtTime);
			long lDuration = Long.parseLong(data.get( MediaStore.Audio.Playlists.Members.DURATION ));
			txtView.setText( Formmater.formatTime(lDuration) );
			
			
			ImageView img = (ImageView)view.findViewById(R.id.imageAlbum);
			Bitmap bitmap = mFragment.mAlbumArts.get( data.get( MediaStore.Audio.Playlists.Members.ALBUM_KEY ) );
			if( bitmap != null ) {
				img.setImageBitmap(bitmap);
			}

			return view;
		}
	}

    /**
     * プレイリストメンバーを削除する
     * @param data
     */
    void deletePlaylistMember( HashMap<String, String> data) {
    	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "deletePlaylistMember start");}
    	try {
    		int member_id = Integer.parseInt( data.get(MediaStore.Audio.Playlists.Members._ID) );
    		
    		new MusicUtils(mActivity).removeMusicFromPlaylists(Integer.parseInt(mID), new int[] {member_id});
    		
    		List<HashMap<String, String>> list = getPlaylistMember( Integer.parseInt(mID) );
        	PlaylistMemberListAdapter adapter = new PlaylistMemberListAdapter(this, mActivity, list);
        	mListView.setAdapter(adapter);
    	} catch(Exception e) {
    		MyLog.loge(TAG, e);
    	} finally {
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "deletePlaylistMember end");}
    	}
    }
    
    List<HashMap<String, String>> getPlaylistMember(int id) {
    	ContentResolver resolver = mActivity.getContentResolver();
    	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	
		Cursor cursor = resolver.query(   
				MediaStore.Audio.Playlists.Members.getContentUri("external", id),
				null,
                null,
                null,
                null);
        if( cursor != null ) {
			while( cursor.moveToNext() ){
				HashMap<String, String> m = new HashMap<String, String>();
				for(int i = 0; i < cursor.getColumnCount(); i++ ) {
					m.put(cursor.getColumnName(i), cursor.getString( i ));
				}
				list.add( m );
			}  
			cursor.close();
        }
        return list;
    }
   
    
    /**
     * @param data
     * @param arts
     */
    private void loadAlbumArts(List<HashMap<String, String>> data, HashMap<String, Bitmap> arts) {
    	ContentResolver resolver = mActivity.getContentResolver();
		for(HashMap<String, String> m : data) {
			String strAlbumKey = m.get( MediaStore.Audio.Playlists.Members.ALBUM_KEY );
			
	        Cursor cursor = resolver.query(
	        		MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI , 
	        		new String[]{
	        				MediaStore.Audio.Albums.ALBUM_ART,
	        		},    // keys for select. null means all
	        		MediaStore.Audio.Albums.ALBUM_KEY + "=?",
	        		new String[]{ strAlbumKey },
	        		null//MediaStore.Audio.Media.ALBUM + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	if( arts.containsKey(strAlbumKey ) == false ) {
		        		String strAlbumArt = cursor.getString( cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART) );
		        		if( strAlbumArt != null ) {
		        			Bitmap bitmap = ImageUtil.LoadImageFile(strAlbumArt, 64, 64);
		        			arts.put(strAlbumKey, bitmap );
		        		}
		        	}
		        }
		        cursor.close();
	        }
		}
	}

	
	/**
	 * プレイリスト削除確認ダイアログを表示
	 */
	public static class PlaylistMemberDelAskDialogFragment extends DialogFragment {
		private MusicPlaylistFragment			mParentFragment;
		private HashMap<String, String>			mData = null;
		
		static PlaylistMemberDelAskDialogFragment newInstance(MusicPlaylistFragment parentFragment, HashMap<String, String> data) {
			PlaylistMemberDelAskDialogFragment f = new PlaylistMemberDelAskDialogFragment();
			f.mParentFragment = parentFragment;
			f.mData = data;
			Bundle args = new Bundle();
			f.setArguments(args);
			return f;
		}
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }

        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	LayoutInflater inflater = getActivity().getLayoutInflater();  
        	View v = inflater.inflate(R.layout.dialog_del_playlist_member_ask, null, false);
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.title_del_playlist_member);
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						dlg.dismiss();
						mParentFragment.deletePlaylistMember(mData);
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

        	TextView txt;
        	txt = (TextView)v.findViewById(R.id.txtTitle);
        	txt.setText(mData.get(MediaStore.Audio.Playlists.Members.TITLE));

        	txt = (TextView)v.findViewById(R.id.txtArtist);
        	txt.setText(mData.get(MediaStore.Audio.Playlists.Members.ARTIST));

        	txt = (TextView)v.findViewById(R.id.txtAlbum);
        	txt.setText(mData.get(MediaStore.Audio.Playlists.Members.ALBUM));

			ImageView img = (ImageView)v.findViewById(R.id.imageAlbum);
			Bitmap bitmap = mParentFragment.mAlbumArts.get( mData.get( MediaStore.Audio.Playlists.Members.ALBUM_KEY ) );
			if( bitmap != null ) {
				img.setImageBitmap(bitmap);
			}
        	
        	builder.setView(v); 
        	
        	
        	return builder.create();
        }
	}


}