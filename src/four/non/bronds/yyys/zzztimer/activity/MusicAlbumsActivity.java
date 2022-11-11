package four.non.bronds.yyys.zzztimer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.SongOtherInf;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.util.Formmater;
import four.non.bronds.yyys.zzztimer.util.ImageUtil;
import four.non.bronds.yyys.zzztimer.util.MusicUtils;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.StrageUtil;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MusicAlbumsActivity  extends MyBaseActivity {
	private static final String TAG = "MusicAlbumsActivity";
	private StaticMusicAlbumsFragmentFragment mFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// フラグメントインスタンスを作成
		mFragment = new StaticMusicAlbumsFragmentFragment();
		mFragment.setArguments(this.getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, mFragment).commit();
		
	}
	
	public static class StaticMusicAlbumsFragmentFragment extends MusicAlbumsFragment {}

}


class MusicAlbumsFragment extends MyBaseFragment
{
	final static String TAG = "MusicAlbumsFragment";
	private ListView		mListView = null;

	private String			mArtistID;
	private String			mArtist;
	private String			mArtistKey;
	private String			mWhere;
	private List<HashMap<String, String>>	mData;
	private ProgressBar		mProgressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {
			if (container == null) {
				return null;
			}
			mActivity = this.getActivity();
			mRootView = inflater.inflate(R.layout.music_albums, container, false);

			
			mProgressBar = (ProgressBar)mRootView.findViewById(R.id.progressBar);
			
			mListView = (ListView)mRootView.findViewById(R.id.listView);
			mListView.setCacheColorHint(Color.TRANSPARENT);
			mListView.setItemsCanFocus(false);
			mListView.setOnItemClickListener(new OnItemClickListener() {
	    		@Override
	    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					@SuppressWarnings("unchecked")
					HashMap<String, String> data = (HashMap<String, String>)parent.getItemAtPosition(position);

					Intent intent = new Intent(mActivity, MusicSongsActivity.class);
					intent.putExtra(MediaStore.Audio.Media.ARTIST_ID , mArtistID);
					intent.putExtra(MediaStore.Audio.Media.ARTIST , mArtist);
					intent.putExtra(MediaStore.Audio.Media.ARTIST_KEY , mArtistKey);

					intent.putExtra(MediaStore.Audio.Media.ALBUM , data.get(MediaStore.Audio.Media.ALBUM));
					intent.putExtra(MediaStore.Audio.Media.ALBUM_KEY , data.get(MediaStore.Audio.Media.ALBUM_KEY));
					intent.putExtra(MediaStore.Audio.Media.ALBUM_ID , data.get(MediaStore.Audio.Albums._ID));
					intent.putExtra(MediaStore.Audio.Media.ALBUM_ART , data.get(MediaStore.Audio.Albums.ALBUM_ART));
					intent.putExtra(MediaStore.Audio.Albums.NUMBER_OF_SONGS , data.get(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
					intent.putExtra(MusicUtils.SPECIAL_KEY_WHERE , data.get(MusicUtils.SPECIAL_KEY_WHERE ));
					
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
    	
    	//List<HashMap<String, String>> list = getAlbum(mArtistKey);
    	//AlbumListAdapter adapter = new AlbumListAdapter(mActivity, list);
    	//mListView.setAdapter(adapter);
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
    		mWhere = args.getString(MusicUtils.SPECIAL_KEY_WHERE);
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "mWhere:" + mWhere);}
        }
	}


    /**
     * @throws Exception
     */
    void innerLoad() throws Exception {

		if( StrageUtil.isSDcardExist( mActivity ) == false ) {
			throw new Exception( mActivity.getString( R.string.errMsgNotExistStrage ) );
		}
		
		MusicUtils musicUtil = new MusicUtils(mActivity);
		
    	mData =  new ArrayList<HashMap<String, String>>();
    	SongOtherInf songInf = musicUtil.getAllSongInfo(mArtistKey, null, mWhere);
    	
    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put(MediaStore.Audio.Albums.ALBUM, "<<All Song>>");
    	map.put(MediaStore.Audio.Albums.ALBUM_KEY, "");
    	map.put(MediaStore.Audio.Albums._ID, "");
    	map.put(MediaStore.Audio.Albums.ALBUM_ART, null);
    	if( songInf != null ) {
	    	map.put(MediaStore.Audio.Albums.NUMBER_OF_SONGS, "" + songInf.mNumOfSong);
	    	map.put(MediaStore.Audio.Albums.FIRST_YEAR, "" + songInf.mMinYear);
	    	map.put(MediaStore.Audio.Albums.LAST_YEAR, "" + songInf.mMaxYear);    		
    	} else {
	    	map.put(MediaStore.Audio.Albums.NUMBER_OF_SONGS, "0");
	    	map.put(MediaStore.Audio.Albums.FIRST_YEAR, "0");
	    	map.put(MediaStore.Audio.Albums.LAST_YEAR, "0");
    	}
    	map.put(MusicUtils.SPECIAL_KEY_WHERE, mWhere);
    	mData.add( map );
    	
    	
    	
    	
    	musicUtil.getAlbum(mArtistKey, mWhere, mData);    	
    }
    /**
     * 
     */
    void reView() {

		TextView txt = (TextView)mRootView.findViewById(R.id.textTitle);
		txt.setText( mArtist );
		
    	AlbumListAdapter adapter = new AlbumListAdapter(mActivity, mData);
    	mListView.setAdapter(adapter);
    }
	
	
	/**
	 * プレイリスト－リストアダプター
	 */
	private class AlbumListAdapter extends ArrayAdapter<HashMap<String, String>> {
		private LayoutInflater 	mInflater;
		
		public AlbumListAdapter(Context context, List<HashMap<String, String>> objects) {
			super(context, 0, objects);
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.music_album_list_row, null);
			}
			HashMap<String, String> data = this.getItem(position);
			TextView  txtView = null;
			txtView = (TextView)view.findViewById(R.id.txtName);
			
			txtView.setText( data.get( MediaStore.Audio.Albums.ALBUM ) );
			
			String strYear = "";
			try {
				long firstYear = Long.parseLong( data.get( MediaStore.Audio.Albums.FIRST_YEAR ) );
				long lastYear = Long.parseLong( data.get( MediaStore.Audio.Albums.LAST_YEAR ) );
				if( firstYear == lastYear ) {
					strYear = "" + firstYear;
				} else {
					strYear = "" + firstYear + "-" + lastYear;
				}
				strYear += ", ";
			} catch(NumberFormatException ex ) {
			}
			
			txtView = (TextView)view.findViewById(R.id.txtNumberOfMusic);
			txtView.setText( strYear
					+ data.get( MediaStore.Audio.Albums.NUMBER_OF_SONGS )+" TRACK" );

			txtView = (TextView)view.findViewById(R.id.txtTime);
			long lDuration = 0;
			try {
				lDuration = Long.parseLong((String)data.get(MediaStore.Audio.Media.DURATION));
			} catch(NumberFormatException ex ) {
			}
			txtView.setText( Formmater.formatTime(lDuration) );
			
			ImageView img = (ImageView)view.findViewById(R.id.imageAlbum);
			if(MyLog.isDebugMod()) {MyLog.logt(TAG, "ART :" + data.get( MediaStore.Audio.Albums.ALBUM_ART ));}
			if( data.get( MediaStore.Audio.Albums.ALBUM_ART ) != null ) {
				Bitmap bitmap = ImageUtil.LoadImageFile(data.get( MediaStore.Audio.Albums.ALBUM_ART ), 64, 64);
				//Uri sArtworkUri = Uri.parse( data.get( MediaStore.Audio.Albums.ALBUM_ART ) );
				img.setImageBitmap(bitmap);
			}
			
			return view;
		}
	}

    List<HashMap<String, String>> getAlbum(String strArtistKey)
    {
    	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        ContentResolver resolver = mActivity.getContentResolver();
        Cursor cursor = resolver.query(
        		MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI , 
        		new String[]{
        				"distinct " + MediaStore.Audio.Media.ALBUM ,
        				MediaStore.Audio.Albums.ALBUM_KEY ,
        				MediaStore.Audio.Albums._ID,
        				MediaStore.Audio.Albums.ALBUM_ART,
        				MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        				MediaStore.Audio.Albums.FIRST_YEAR,
        				MediaStore.Audio.Albums.LAST_YEAR
        				//MediaStore.Audio.Media.TITLE
        		},    // keys for select. null means all
        		MediaStore.Audio.Media.ARTIST_KEY + "=?",
        		new String[]{ strArtistKey },
        		MediaStore.Audio.Media.ALBUM + " ASC"
        );
        


        while( cursor.moveToNext() ){
        	HashMap<String, String> m = new HashMap<String, String>();
        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
        		m.put(cursor.getColumnName(i), cursor.getString( i ));
        	}
        	list.add( m );
        }
        return list;
    }


	/**
	 * 非同期データローダー
	 */
	class MyLoader extends AsyncTask<String, Integer, Integer> {
		private MusicAlbumsFragment	mFragment;
		private Exception					mError;
		
		
		public MyLoader(MusicAlbumsFragment fragment) {
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
}