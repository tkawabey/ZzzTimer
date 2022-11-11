package four.non.bronds.yyys.zzztimer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import four.non.bronds.yyys.zzztimer.bean.SongOtherInf;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;



public class MusicUtils {
	private static final String TAG = "MusicUtils";
	private Context				mContext;
	private ContentResolver		mContentResolver;
	public static String		SPECIAL_KEY_WHERE	= "WHERE";
	public static String		SPECIAL_VAL_WHERE_EX	= "EX";
	public static String		SPECIAL_VAL_WHERE_IN	= "IN";
	

	public MusicUtils(Context ctx) {
		mContext = ctx;
		mContentResolver = mContext.getContentResolver();
	}
	
	public void startScrobbler(String strArtist, String strAlbum, String strTrakName, long duration) {
		try {
			if( MyLog.isDebugMod()) { 
				MyLog.logt(TAG, "Start Scrobbler");
				MyLog.logt(TAG, "  artist   :" + strArtist);
				MyLog.logt(TAG, "  album    :" + strAlbum);
				MyLog.logt(TAG, "  track    :" + strTrakName);
				MyLog.logt(TAG, "  duration :" + duration);
			}
			Intent i = new Intent("fm.last.android.metachanged");
			i.putExtra("artist", strArtist);
			i.putExtra("album", strAlbum);
			i.putExtra("track", strTrakName);
			i.putExtra("duration", duration);
			mContext.sendBroadcast(i);
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		}
	}
	public void stopScrobbler() {
		try {
			if( MyLog.isDebugMod()) { 
				MyLog.logt(TAG, "Stop Scrobbler");
			}
			Intent i = new Intent("fm.last.android.playbackcomplete");
			mContext.sendBroadcast(i);
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		}
	}
	public void pauseScrobbler() {
		try {
			Intent i = new Intent("fm.last.android.playbackcomplete");
			mContext.sendBroadcast(i);
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		}
	}
	public void resumeScrobbler() {
		try {
			Intent i = new Intent("fm.last.android.metachanged");
			mContext.sendBroadcast(i);
		} catch(Exception e) {
			MyLog.loge(TAG, e);
		}
	}
/*	
	public PlaylistUtils(ContentResolver resolve) {
		mContentResolver = resolve;
	}
*/
	public boolean isGalaxy(){
	    return false;
	}
	 
	/**
	 * プレイリストを新規作成する
	 * @param name プレイリストの名前
	 * @param images_uri サムネイル画像のURI（Galaxyのみ）
	 * @param thumb サムネイル画像のURIの文字列（Galaxyのみ）
	 *
	 * 基本的に名前を指定してクエリー実行でOKです。
	 * Galaxyの場合は、サムネイル画像を指定する事ができます。
	 *
	 */
	public void addPlaylist(String name, Uri images_uri, String thumb){
	    ContentValues contentvalues = null;
	    Uri result_uri = null;
	    Uri playlist_uri = null;
	    int result = -1;
	     
	    if(isGalaxy()){
	        //Galaxyの場合の必要データ作成
	         
	        //URI
	        playlist_uri = Uri.parse("content://media/external/audio/music_playlists");
	        //データ作成
	        int image_index = -1;
	        if(images_uri != null){
	            image_index = (int)ContentUris.parseId(images_uri);
	        }
	        contentvalues = new ContentValues();
	        contentvalues.put("name", name);
	        contentvalues.put("images_id", image_index);
	        contentvalues.put("thumbnail_uri", "");
	    }else{
	        //標準的な端末の必要データ作成
	         
	        //URI
	        playlist_uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	        //データ作成
	        contentvalues = new ContentValues();
	        contentvalues.put("name", name);
	    }
	     
		//追加
	    result_uri = mContentResolver.insert(playlist_uri, contentvalues);
	     
	    if(result_uri == null){
	        //NG
	        Log.d("test", "fail add playlist : " + name + ", is null");
	    }else if((result = (int)ContentUris.parseId(result_uri)) == -1){
	        //NG
	        Log.d("test", "fail add playlist : " + name + ", " + result_uri.toString());
	    }else{
	        //OK
	        Log.d("test", "add playlist : " + name + "," + result);
	    }
	}
	 
	/**
	 * プレイリストの名前を変更する
	 * @param id プレイリストのID
	 * @param name 新しい名前
	 */
	public void updatePlaylist(int id, String name){
	    ContentValues contentvalues = null;
	    Uri uri = null;
	    String as[];
	    int result = -1;
	     
	    //URIの作成
	    if(isGalaxy()){
	        uri = Uri.parse("content://media/external/audio/music_playlists");
	    }else{
	        uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	    }
	    //登録データの作成
	    contentvalues = new ContentValues(1);
	    contentvalues.put("name", name);
	    as = new String[1];
	    as[0] = Integer.toString(id);
	    //変更
	    result = mContentResolver.update(uri, contentvalues, "_id = ?", as);
	    if(result != 1){
	        Log.d("test", "fail update playlist : " + name + ", " + result);
	    }else{
	        Log.d("test", "update playlist : " + name + ", " + result);
	    }
	}
	 
	/**
	 * プレイリストの最大のplay_orderを取得する
	 * @param playlist_id
	 * @return
	 */
	public int getUserListMaxPlayOrder(int playlist_id){
	    int ret = -1;
	    Uri uri = null;
	     
	    if(isGalaxy()){
	        uri = Uri.parse("content://media/external/audio/music_playlists/" + playlist_id + "/members");;
	    }else{
	        uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
	    }
	    String as[] = new String[1];
	    as[0] = " max(play_order)";
	    String s = "playlist_id = " + playlist_id;
	    Cursor cursor = mContentResolver.query(uri, as, s, null, null);
	    if(cursor == null){
	    }else{
	        cursor.moveToFirst();
	        ret = cursor.getInt(0);
	        cursor.close();
	    }
	    return ret;
	}
	 
	 
	/**
	 * プレイリストへ曲を追加する
	 * @param playlist_id 追加対象のプレイリストのID（標準端末・Galaxyの共通で使用します）
	 * @param audio_id 追加する曲のID（標準端末で使用します）
	 * @param data 追加する曲のフルパスの文字列（Galaxyで使用します）
	 * @param data_hash 追加する曲のハッシュコード（Galaxyで使用します）
	 */
	public void addMusicToPlaylist(int playlist_id, int audio_id, String data, int data_hash){
	    ContentValues contentvalues = new ContentValues();
	    Uri uri = null;
	     
	    if(mContentResolver == null){
	    }else{
	 
	        Uri result_uri = null;
	        contentvalues.put("play_order", getUserListMaxPlayOrder(playlist_id) + 1);
	         
	        if(isGalaxy()){
	            uri = Uri.parse("content://media/external/audio/music_playlists/" + playlist_id + "/members");;
	            contentvalues.put("audio_data", data);
	            contentvalues.put("audio_data_hashcode", data_hash);
	        }else{
	            uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
	            contentvalues.put("audio_id", Integer.valueOf(audio_id));
	        }
	 
	        //追加
	        result_uri = mContentResolver.insert(uri, contentvalues);
	        if(result_uri == null){
	            //NG
	            Log.d("test", "fail add music : " + playlist_id + ", " + audio_id + ", is null");
	        }else if(((int)ContentUris.parseId(result_uri)) == -1){
	            //NG
	            Log.d("test", "fail add music : " + playlist_id + ", " + audio_id + ", " + result_uri.toString());
	        }else{
	            //OK
	            Log.d("test", "add music : " + playlist_id + ", " + audio_id + ", " + result_uri.toString());
	        }
	    }
	}
	 
	 
	/**
	 * プレイリストから曲を削除する
	 * @param ids 削除対象のIDの配列
	 */
	public void removeMusicFromPlaylists(int playlist_id, int[] ids){
	    Uri uri = null;
	    if(isGalaxy()){
	        uri = Uri.parse("content://media/external/audio/music_playlists/" + playlist_id + "/members");
	    }else{
	        uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
	    }
	    removeItems(uri, ids);
	}
	 
	/**
	 * プレイリストを削除する
	 * @param ids 削除対象のIDの配列
	 */
	public void removePlaylists(int[] ids){
	    Uri uri = null;
	    if(isGalaxy()){
	        uri = Uri.parse("content://media/external/audio/music_playlists");
	    }else{
	        uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	    }
	    removeItems(uri, ids);
	}
	 
	 
	/**
	 * 登録されているアイテムを削除する
	 * @param uri 削除URI
	 * @param ids 削除するIDの配列
	 */
	protected void removeItems(Uri uri, int[] ids){
	     
	    if(uri == null || ids == null){
	    }else{
	        String where = "_id IN(";
	        for(int i=0; i<ids.length; i++){
	            where += Integer.valueOf(ids[i]);
	            if(i < (ids.length -1)){
	                where += ", ";
	            }
	        }
	        where += ")";
	        //削除
	        mContentResolver.delete(uri, where, null);
	    }      
	}




    /**
     * アーティスト一覧を取得
     * @return
     */
	public List<HashMap<String, String>> getArtists(List<HashMap<String, String>> list, String strWhereStrage)
    {
		Cursor cursor;

    	try {
    		if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhereStrage) == 0 ) {
		        cursor = mContentResolver.query(
		        		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , 
		        		new String[]{
		        				"distinct " + MediaStore.Audio.Media.ARTIST ,
		        				MediaStore.Audio.Media.ARTIST_KEY,
		        				MediaStore.Audio.Media.ARTIST_ID,
		        				
		        		},
		        		null,
		        		null,
		        		null
		        );
		        if( cursor != null ) {
			        while( cursor.moveToNext() ){
			        	HashMap<String, String> m = new HashMap<String, String>();
		            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_EX);
			        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
			        		m.put(cursor.getColumnName(i), cursor.getString( i ));
			        	}
			        	list.add( m );
			        }
			        cursor.close();
		        }
    		}
        } catch(Exception e) {        	
        }

    	try {
    		if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_IN.compareTo(strWhereStrage) == 0 ) {
		        cursor = mContentResolver.query(
		        		MediaStore.Audio.Media.INTERNAL_CONTENT_URI , 
		        		new String[]{
		        				"distinct " + MediaStore.Audio.Media.ARTIST ,
		        				MediaStore.Audio.Media.ARTIST_KEY,
		        				MediaStore.Audio.Media.ARTIST_ID,
		        				
		        		},
		        		null,
		        		null,
		        		null
		        );
		        if( cursor != null ) {
			        while( cursor.moveToNext() ){
			        	HashMap<String, String> m = new HashMap<String, String>();
		            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_IN);
			        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
			        		m.put(cursor.getColumnName(i), cursor.getString( i ));
			        	}
			        	list.add( m );
			        }
			        cursor.close();
		        }
    		}
        } catch(Exception e) {        	
        }
        return list;
    }

    /**
     * プレーリスト一覧を取得
     * 
     * @param list
     * @return
     */
    public List<HashMap<String, String>> getPlaylist(List<HashMap<String, String>> list, String strWhereStrage)
    {
    	Cursor cursor;
    	
    	try {
    		if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhereStrage) == 0 ) {
    			MyLog.logf(TAG, "SPECIAL_VAL_WHERE_EX");
		        cursor = mContentResolver.query(
		        		MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI , 
		        		null , //取得する項目 nullは全部
		        		null , //フィルター条件 nullはフィルタリング無し 
		        		null , //フィルター用のパラメータ
		        		null   //並べ替え
		        );
		        if( cursor != null ) {
		            while( cursor.moveToNext() ){
		            	HashMap<String, String> m = new HashMap<String, String>();
		            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_EX);
		            	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
		            		m.put(cursor.getColumnName(i), cursor.getString( i ));
		            	}
		            	list.add( m );
		            }
		            cursor.close();
		        }
		        
		        for(HashMap<String, String> data : list) {
		        	SongOtherInf inf = this.getPlaylistOtherInfo(Integer.parseInt( data.get( MediaStore.Audio.Playlists._ID ) ), SPECIAL_VAL_WHERE_EX);
		        	if( inf != null ) {
		        		data.put(MediaStore.Audio.Media.DURATION, "" + inf.mDuration);
		        		data.put(MediaStore.Audio.Albums.NUMBER_OF_SONGS, "" + inf.mNumOfSong);
		        	} else {
		        		data.put(MediaStore.Audio.Media.DURATION, "0");
		        		data.put(MediaStore.Audio.Albums.NUMBER_OF_SONGS, "0");
		        	}
		        }
    		}
        } catch(Exception e) { 	
        	MyLog.loge("", e);
        }

        try {
        	if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_IN.compareTo(strWhereStrage) == 0 ) {
        		MyLog.logf(TAG, "SPECIAL_VAL_WHERE_IN");
		        cursor = mContentResolver.query(
		        		MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI , 
		        		null , //取得する項目 nullは全部
		        		null , //フィルター条件 nullはフィルタリング無し 
		        		null , //フィルター用のパラメータ
		        		null   //並べ替え
		        );
		        if( cursor != null ) {
		            while( cursor.moveToNext() ){
		            	HashMap<String, String> m = new HashMap<String, String>();
		            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_IN);
		            	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
		            		m.put(cursor.getColumnName(i), cursor.getString( i ));
		            	}
		            	list.add( m );
		            }
		            cursor.close();
		        }
		        
		        for(HashMap<String, String> data : list) {
		        	SongOtherInf inf = this.getPlaylistOtherInfo(Integer.parseInt( data.get( MediaStore.Audio.Playlists._ID ) ), SPECIAL_VAL_WHERE_EX);
		        	if( inf != null ) {
		        		data.put(MediaStore.Audio.Media.DURATION, "" + inf.mDuration);
		        		data.put(MediaStore.Audio.Albums.NUMBER_OF_SONGS, "" + inf.mNumOfSong);
		        	} else {
		        		data.put(MediaStore.Audio.Media.DURATION, "0");
		        		data.put(MediaStore.Audio.Albums.NUMBER_OF_SONGS, "0");
		        	}
		        }
        	}
        } catch(Exception e) {
        	MyLog.loge("", e);
        }
        return list;
    }
    
    public SongOtherInf getPlaylistOtherInfo(int id, String strWhereStrage) {
    	Cursor cursor;
    	SongOtherInf inf = new SongOtherInf();
    	long numOfSongs = 0;
    	long duration = 0;
    	long maxYear = 0;
    	long minYear = 99999;
    	String strWhereVal = null;
    	if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhereStrage) == 0 ) {
    		strWhereVal = "external";
    	} else {
    		strWhereVal = "internal";
    	}
		cursor = mContentResolver.query(
				MediaStore.Audio.Playlists.Members.getContentUri(strWhereVal, id),
				null,
                null,
                null,
                null);
		if( cursor != null ) {
			while( cursor.moveToNext() ){
	        	numOfSongs++;
	        	duration += cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.DURATION) );
	        	
	        	long l = cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.YEAR) );
	        	if( l > maxYear ) {
	        		maxYear = l;
	        	}
	        	if( l < minYear ) {
	        		minYear = l;
	        	}
			}  
			cursor.close();
		}
        inf.mNumOfSong = numOfSongs;
        inf.mDuration = duration;
        inf.mMaxYear = maxYear;
        inf.mMinYear = minYear;
    	return inf;
    }

    /**
     * @param strArtistKey
     * @param strAlbumKey
     * @param strWhereStrage
     * @return
     */
    public SongOtherInf getAllSongInfo(String strArtistKey, String strAlbumKey, String strWhereStrage)
    {
    	Cursor cursor;
    	SongOtherInf inf = new SongOtherInf();
    	long numOfSongs = 0;
    	long duration = 0;
    	long maxYear = 0;
    	long minYear = 99999;
    	String whereArgs[];
    	String strWhere = MediaStore.Audio.Media.ARTIST_KEY + "=?";
    	if( strAlbumKey != null && strAlbumKey.length() != 0 ) {
    		strWhere += " AND " + MediaStore.Audio.Media.ALBUM_KEY + "=?";
    		whereArgs = new String[] {strArtistKey, strAlbumKey};
    	} else {
    		whereArgs = new String[] {strArtistKey};
    	}
    	
    	
    	
    	
    	if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhereStrage) == 0  ) {
	        cursor = mContentResolver.query(
	        		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , 
	        		new String[]{
	        				MediaStore.Audio.Media.TITLE_KEY,
	        				MediaStore.Audio.Media.DURATION,
	        				MediaStore.Audio.Media.YEAR,
	        				MediaStore.Audio.Media.SIZE,
	        				MediaStore.Audio.Media._ID,
	        		},    // keys for select. null means all
	        		strWhere,
	        		whereArgs,
	        		MediaStore.Audio.Media.TRACK + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	numOfSongs++;
		        	duration += cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.DURATION) );
		        	
		        	long l = cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.YEAR) );
		        	if( l > maxYear ) {
		        		maxYear = l;
		        	}
		        	if( l < minYear ) {
		        		minYear = l;
		        	}
		        }
		        cursor.close();
	        }
        }
    	

    	if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_IN.compareTo(strWhereStrage) == 0  ) {
	        cursor = mContentResolver.query(
	        		MediaStore.Audio.Media.INTERNAL_CONTENT_URI , 
	        		new String[]{
	        				MediaStore.Audio.Media.TITLE_KEY,
	        				MediaStore.Audio.Media.DURATION,
	        				MediaStore.Audio.Media.YEAR,
	        				MediaStore.Audio.Media.SIZE,
	        				MediaStore.Audio.Media._ID,
	        		},    // keys for select. null means all
	        		strWhere,
	        		whereArgs,
	        		MediaStore.Audio.Media.TRACK + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	numOfSongs++;
		        	duration += cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.DURATION) );
		        	
		        	long l = cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.YEAR) );
		        	if( l > maxYear ) {
		        		maxYear = l;
		        	}
		        	if( l < minYear ) {
		        		minYear = l;
		        	}
		        }
		        cursor.close();
	        }
        }
        inf.mNumOfSong = numOfSongs;
        inf.mDuration = duration;
        inf.mMaxYear = maxYear;
        inf.mMinYear = minYear;
        return inf;
    }

    /**
     * @param strArtistKey
     * @param strAlbumKey
     * @param strWhereStrage
     * @return
     */
    public SongOtherInf getAllSongInfoByVal(String strArtist, String strAlbum, String strWhereStrage)
    {
    	Cursor cursor;
    	SongOtherInf inf = new SongOtherInf();
    	long numOfSongs = 0;
    	long duration = 0;
    	long maxYear = 0;
    	long minYear = 99999;
    	String whereArgs[];
    	String strWhere = MediaStore.Audio.Media.ARTIST + "=?";
    	if( strAlbum != null && strAlbum.length() != 0 ) {
    		strWhere += " AND " + MediaStore.Audio.Media.ALBUM + "=?";
    		whereArgs = new String[] {strArtist, strAlbum};
    	} else {
    		whereArgs = new String[] {strArtist};
    	}
    	
    	
    	
    	
    	if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhereStrage) == 0  ) {
	        cursor = mContentResolver.query(
	        		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , 
	        		new String[]{
	        				MediaStore.Audio.Media.TITLE_KEY,
	        				MediaStore.Audio.Media.DURATION,
	        				MediaStore.Audio.Media.YEAR,
	        				MediaStore.Audio.Media.SIZE,
	        				MediaStore.Audio.Media._ID,
	        		},    // keys for select. null means all
	        		strWhere,
	        		whereArgs,
	        		MediaStore.Audio.Media.TRACK + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	numOfSongs++;
		        	duration += cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.DURATION) );
		        	
		        	long l = cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.YEAR) );
		        	if( l > maxYear ) {
		        		maxYear = l;
		        	}
		        	if( l < minYear ) {
		        		minYear = l;
		        	}
		        }
		        cursor.close();
	        }
        }
    	

    	if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_IN.compareTo(strWhereStrage) == 0  ) {
	        cursor = mContentResolver.query(
	        		MediaStore.Audio.Media.INTERNAL_CONTENT_URI , 
	        		new String[]{
	        				MediaStore.Audio.Media.TITLE_KEY,
	        				MediaStore.Audio.Media.DURATION,
	        				MediaStore.Audio.Media.YEAR,
	        				MediaStore.Audio.Media.SIZE,
	        				MediaStore.Audio.Media._ID,
	        		},    // keys for select. null means all
	        		strWhere,
	        		whereArgs,
	        		MediaStore.Audio.Media.TRACK + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	numOfSongs++;
		        	duration += cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.DURATION) );
		        	
		        	long l = cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.YEAR) );
		        	if( l > maxYear ) {
		        		maxYear = l;
		        	}
		        	if( l < minYear ) {
		        		minYear = l;
		        	}
		        }
		        cursor.close();
	        }
        }
        inf.mNumOfSong = numOfSongs;
        inf.mDuration = duration;
        inf.mMaxYear = maxYear;
        inf.mMinYear = minYear;
        return inf;
    }
    /**
     * アーティスト一覧を取得
     * 
     * @param strArtistKey
     * @param list
     * @return
     */
    public List<HashMap<String, String>> getAlbum(String strArtistKey, String strWhere, List<HashMap<String, String>> list)
    {
    	Cursor cursor;

        try {
        	if( strWhere == null || SPECIAL_VAL_WHERE_EX.compareTo(strWhere) == 0 ) {
		        cursor = mContentResolver.query(
		        		MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI , 
		        		new String[]{
		        				"distinct " + MediaStore.Audio.Media.ALBUM ,
		        				MediaStore.Audio.Albums.ALBUM_KEY ,
		        				MediaStore.Audio.Albums._ID,
		        				MediaStore.Audio.Albums.ALBUM_ART,
		        				MediaStore.Audio.Albums.NUMBER_OF_SONGS,
		        				MediaStore.Audio.Albums.FIRST_YEAR,
		        				MediaStore.Audio.Albums.LAST_YEAR,
		        				//MediaStore.Audio.Media.TITLE
		        		},    // keys for select. null means all
		        		MediaStore.Audio.Media.ARTIST_KEY + "=?",
		        		new String[]{ strArtistKey },
		        		MediaStore.Audio.Media.ALBUM + " ASC"
		        );
		        if( cursor != null ) {
			        while( cursor.moveToNext() ){
			        	HashMap<String, String> m = new HashMap<String, String>();
		            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_EX);
			        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
			        		m.put(cursor.getColumnName(i), cursor.getString( i ));
			        	}
			        	list.add( m );
			        }
		        }
		        cursor.close();
		        for( HashMap<String, String> data : list ) {
		        	SongOtherInf inf = this.getAllSongInfo(strArtistKey, data.get(MediaStore.Audio.Albums.ALBUM_KEY), strWhere );
		        	if( inf != null ) {
		        		data.put(MediaStore.Audio.Media.DURATION, "" + inf.mDuration);
		        	} else {
		        		data.put(MediaStore.Audio.Media.DURATION, "0");
		        	}
		        }
        	}
        } catch(Exception e) {
        	MyLog.loge("", e);
        }
        try {
        	if( strWhere == null || SPECIAL_VAL_WHERE_IN.compareTo(strWhere) == 0 ) {
		        cursor = mContentResolver.query(
		        		MediaStore.Audio.Albums.INTERNAL_CONTENT_URI , 
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
		        if( cursor != null ) {
			        while( cursor.moveToNext() ){
			        	HashMap<String, String> m = new HashMap<String, String>();
		            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_IN);
			        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
			        		m.put(cursor.getColumnName(i), cursor.getString( i ));
			        	}
			        	list.add( m );
			        }
		        }
		        cursor.close();
		        for( HashMap<String, String> data : list ) {
		        	SongOtherInf inf = this.getAllSongInfo(strArtistKey, data.get(MediaStore.Audio.Albums.ALBUM_KEY), strWhere );
		        	if( inf != null ) {
		        		data.put(MediaStore.Audio.Media.DURATION, "" + inf.mDuration);
		        	} else {
		        		data.put(MediaStore.Audio.Media.DURATION, "0");
		        	}
		        }
        	}
        } catch(Exception e) {
        	MyLog.loge("", e);
        }
        return list;
    }
    
    /**
     * 曲一覧を取得
     * @param strArtistKey	アーティストキー
     * @param strWhereStrage	ストレージの場所
     * @param strAlbumKey	アルバムキー
     * @return
     */
    public List<HashMap<String, Object>> getSongs(String strArtistKey, String strWhereStrage, String strAlbumKey, List<HashMap<String, Object>> list)
    {
    	MyLog.logf(TAG, "strArtistKey:" + strArtistKey + "      strAlbumKey:"+strAlbumKey);
        Cursor cursor;
    	String whereArgs[];
    	String strWhere = MediaStore.Audio.Media.ARTIST_KEY + "=?";
    	if( strAlbumKey != null && strAlbumKey.length() != 0 ) {
    		strWhere += " AND " + MediaStore.Audio.Media.ALBUM_KEY + "=?";
    		whereArgs = new String[] {strArtistKey, strAlbumKey};
    	} else {
    		whereArgs = new String[] {strArtistKey};
    	}

        
        if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhereStrage) == 0  ) {
	        cursor = mContentResolver.query(
	        		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , 
	        		new String[]{
	        				"distinct " + MediaStore.Audio.Media.TITLE ,
	        				MediaStore.Audio.Media.TITLE_KEY,
	        				MediaStore.Audio.Media.DATA,
	        				MediaStore.Audio.Media.MIME_TYPE,
	        				MediaStore.Audio.Media.DURATION,
	        				MediaStore.Audio.Media.YEAR,
	        				MediaStore.Audio.Media.TRACK,
	        				MediaStore.Audio.Media.SIZE,
	        				MediaStore.Audio.Media.COMPOSER,
	        				MediaStore.Audio.Media._ID,
	        				MediaStore.Audio.Media.ALBUM_KEY,
	        				MediaStore.Audio.Media.ALBUM,
	        				MediaStore.Audio.Media.ALBUM_ID,
	        				MediaStore.Audio.Media.ARTIST,
	        				MediaStore.Audio.Media.ARTIST_ID,
	        				MediaStore.Audio.Media.ARTIST_KEY
	        		},    // keys for select. null means all
	        		strWhere,
	        		whereArgs,
	        		MediaStore.Audio.Media.TRACK + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	HashMap<String, Object> m = new HashMap<String, Object>();
	            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_EX);
		        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
		        		m.put(cursor.getColumnName(i), cursor.getString( i ));
		        	}
		        	m.put("CHK", new Boolean(false));
		        	
		        	list.add( m );
		        }
		        cursor.close();
	        }
        }
        

        if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_IN.compareTo(strWhereStrage) == 0 ) {
	        cursor = mContentResolver.query(
	        		MediaStore.Audio.Media.INTERNAL_CONTENT_URI , 
	        		new String[]{
	        				"distinct " + MediaStore.Audio.Media.TITLE ,
	        				MediaStore.Audio.Media.TITLE_KEY,
	        				MediaStore.Audio.Media.DATA,
	        				MediaStore.Audio.Media.MIME_TYPE,
	        				MediaStore.Audio.Media.DURATION,
	        				MediaStore.Audio.Media.YEAR,
	        				MediaStore.Audio.Media.TRACK,
	        				MediaStore.Audio.Media.SIZE,
	        				MediaStore.Audio.Media.COMPOSER,
	        				MediaStore.Audio.Media._ID
	        		},    // keys for select. null means all
	        		strWhere,
	        		whereArgs,
	        		MediaStore.Audio.Media.TRACK + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	HashMap<String, Object> m = new HashMap<String, Object>();
	            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_IN);
		        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
		        		m.put(cursor.getColumnName(i), cursor.getString( i ));
		        	}
		        	m.put("CHK", new Boolean(false));
		        	
		        	list.add( m );
		        }
		        cursor.close();
	        }
        }
        return list;
    }
    

    /**
     * プレイリストの設定された曲情報を取得
     * @param id
     * @return
     */
	public List<HashMap<String, Object>> getPlaylistMember(int id, String strWhere, List<HashMap<String, Object>> list) {
    	MyLog.logf(TAG, "getPlaylistMember start");
    	String strWhereVal = null;
    	if( strWhere == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhere) == 0 ) {
    		strWhereVal = "external";
    	} else {
    		strWhereVal = "internal";
    	}
		Cursor cursor = mContentResolver.query(
				MediaStore.Audio.Playlists.Members.getContentUri(strWhereVal, id),
				null,
                null,
                null,
                null);
		if( cursor != null ) {
			while( cursor.moveToNext() ){
				HashMap<String, Object> m = new HashMap<String, Object>();
				for(int i = 0; i < cursor.getColumnCount(); i++ ) {
					m.put(cursor.getColumnName(i), cursor.getString( i ));
				}
				list.add( m );
			}  
			cursor.close();
		}
    	MyLog.logf(TAG, "getPlaylistMember end");
        return list;
    }
	
	/**
	 * データファイルをキーに情報を取得
	 * 
	 * @param strData
	 * @param strWhere
	 * @param list
	 * @return
	 */
	public List<HashMap<String, Object>> getSongByDataKey(String strData, String strWhereStrage, List<HashMap<String, Object>> list) {
		MyLog.logf(TAG, "getPlaylistMember start");
        Cursor cursor;
    	String strWhereVal = null;
    	String whereArgs[];
    	String strWhere = MediaStore.Audio.Media.DATA + "=?";

    	whereArgs = new String[] {strData};
    	
    	if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhereStrage) == 0 ) {
    		strWhereVal = "external";
    	} else {
    		strWhereVal = "internal";
    	}
    	
        if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_EX.compareTo(strWhereStrage) == 0  ) {
	        cursor = mContentResolver.query(
	        		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , 
	        		new String[]{
	        				"distinct " + MediaStore.Audio.Media.TITLE ,
	        				MediaStore.Audio.Media.TITLE_KEY,
	        				MediaStore.Audio.Media.DATA,
	        				MediaStore.Audio.Media.MIME_TYPE,
	        				MediaStore.Audio.Media.DURATION,
	        				MediaStore.Audio.Media.YEAR,
	        				MediaStore.Audio.Media.TRACK,
	        				MediaStore.Audio.Media.SIZE,
	        				MediaStore.Audio.Media.COMPOSER,
	        				MediaStore.Audio.Media._ID,
	        				MediaStore.Audio.Media.ALBUM_KEY,
	        				MediaStore.Audio.Media.ALBUM,
	        				MediaStore.Audio.Media.ALBUM_ID,
	        				MediaStore.Audio.Media.ARTIST,
	        				MediaStore.Audio.Media.ARTIST_ID,
	        				MediaStore.Audio.Media.ARTIST_KEY
	        		},    // keys for select. null means all
	        		strWhere,
	        		whereArgs,
	        		MediaStore.Audio.Media.TRACK + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	HashMap<String, Object> m = new HashMap<String, Object>();
	            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_EX);
		        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
		        		m.put(cursor.getColumnName(i), cursor.getString( i ));
		        	}
		        	m.put("CHK", new Boolean(false));
		        	
		        	list.add( m );
		        }
		        cursor.close();
	        }
        }
        

        if( strWhereStrage == null || MusicUtils.SPECIAL_VAL_WHERE_IN.compareTo(strWhereStrage) == 0 ) {
	        cursor = mContentResolver.query(
	        		MediaStore.Audio.Media.INTERNAL_CONTENT_URI , 
	        		new String[]{
	        				"distinct " + MediaStore.Audio.Media.TITLE ,
	        				MediaStore.Audio.Media.TITLE_KEY,
	        				MediaStore.Audio.Media.DATA,
	        				MediaStore.Audio.Media.MIME_TYPE,
	        				MediaStore.Audio.Media.DURATION,
	        				MediaStore.Audio.Media.YEAR,
	        				MediaStore.Audio.Media.TRACK,
	        				MediaStore.Audio.Media.SIZE,
	        				MediaStore.Audio.Media.COMPOSER,
	        				MediaStore.Audio.Media._ID
	        		},    // keys for select. null means all
	        		strWhere,
	        		whereArgs,
	        		MediaStore.Audio.Media.TRACK + " ASC"
	        );
	        if( cursor != null ) {
		        while( cursor.moveToNext() ){
		        	HashMap<String, Object> m = new HashMap<String, Object>();
	            	m.put(SPECIAL_KEY_WHERE, SPECIAL_VAL_WHERE_IN);
		        	for(int i = 0; i < cursor.getColumnCount(); i++ ) {
		        		m.put(cursor.getColumnName(i), cursor.getString( i ));
		        	}
		        	m.put("CHK", new Boolean(false));
		        	
		        	list.add( m );
		        }
		        cursor.close();
	        }
        }
    	
    	MyLog.logf(TAG, "getPlaylistMember end");
        return list;
	}
	/**
	 * シャッフルする
	 * 
	 * @param data	シャッフル元のデータ
	 * @return
	 */
	public static List<HashMap<String, Object>> shufful(List<HashMap<String, Object>> data, boolean bShuffle) 
	{
		List<HashMap<String, Object>> dst = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> src = null;
		int iDeleteCnt = data.size();
		
		if( bShuffle == false ) {
			for(HashMap<String, Object> q : data) {
				dst.add(q);
			}
			return dst;
		}
		
		src = new ArrayList<HashMap<String, Object>>();
		for(HashMap<String, Object> q : data) {
			src.add(q);
		}
		
		
		while( iDeleteCnt != 0 ) {
			HashMap<String, Object> q = null;
			
			Random rnd = new Random();
			int iVal = Math.abs( rnd.nextInt() );
			iVal = (iVal+100) % src.size();
			
			q = src.get(iVal);
			if( q != null ) {
				dst.add(q);
				src.remove(q);
			}
			
			iDeleteCnt--;
		}	
		
		return dst;
	}
}
