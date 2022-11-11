package four.non.bronds.yyys.zzztimer.activity;


import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.util.Market;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.SeekBar;

public class PrefActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "PrefActivity";
	private PreferenceScreen		mInAppBillingPref = null;
	private PreferenceScreen		mMusicVolumPref = null;
	private int 					mMaxVolume = 0;
	private AudioManager 			mAudioManager;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreate start"); }
			super.onCreate(savedInstanceState);
			this.getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_WRITEABLE);
			this.getPreferenceManager().setSharedPreferencesName(Constant.SHARED_PREF);
			
			if( Market.checkInstallPackage(this, Constant.PACKAGE_DEBUG) == Market.INSTALLED_STS.INSTALLED_STS_EXIST ) {
				addPreferencesFromResource(R.xml.preferences_dbg);
			} else {
				addPreferencesFromResource(R.xml.preferences);
			}
			
			
			
			mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			
			// 購入情報のアクティビティーを表示
			mInAppBillingPref = (PreferenceScreen)findPreference("in_app_billing_hst");
			if( mInAppBillingPref != null ) {
				mInAppBillingPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
					@Override
					public boolean onPreferenceClick(Preference pref) {
						// 
						Intent intent = new Intent(PrefActivity.this, InAppBillingHstActivity.class);
						startActivityForResult(intent, 0);
						return false;
					}				
				});
			}
			
			mMusicVolumPref = (PreferenceScreen)findPreference("play_music_volum");
			if( mMusicVolumPref != null ) {
				mMusicVolumPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
					@Override
					public boolean onPreferenceClick(Preference pref) {
						onMusicVolume(pref);
						return false;
					}				
				});
				SharedPreferences pref = this.getSharedPreferences(Constant.SHARED_PREF, MODE_WORLD_WRITEABLE );
				this.setVolumeSummary(mMusicVolumPref , pref.getInt(Constant.SHARED_PREF_PLAYMUSIC_VOLUM, 0));
			}
			
			
			SharedPreferences pref = this.getSharedPreferences(Constant.SHARED_PREF, MODE_WORLD_WRITEABLE );
			this.changeSummary(R.array.sampling_rate_value, R.array.sampling_rate_titles, pref, Constant.SHARED_PREF_REC_BIT_PER_SEC);
			this.changeSummary(R.array.rec_format_val, R.array.rec_format_disp, pref, Constant.SHARED_PREF_REC_FORMAT);

		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onCreate end"); }
		}
	}
	
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences spref, String key) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onSharedPreferenceChanged start"); }

			if( key.equals(Constant.SHARED_PREF_REC_BIT_PER_SEC)) {
				changeSummary(R.array.sampling_rate_value, R.array.sampling_rate_titles, spref, key);
			} else
			if( key.equals(Constant.SHARED_PREF_REC_FORMAT)) {
				changeSummary(R.array.rec_format_val, R.array.rec_format_disp, spref, key);
			}
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onSharedPreferenceChanged end"); }
		}
	}

	
	/**
	 * @param resid_val
	 * @param resid_title
	 * @param pref
	 * @param key
	 */
	private void changeSummary(int resid_val, int resid_title, SharedPreferences pref, String key) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "changeSummary start"); }

			int i = 0, find = -1;
			String values[] = this.getResources().getStringArray(resid_val);
			String val = pref.getString(key, "0");
			for(String s : values) {
				if( s.equals(val)) {
					find = i;
					break;
				}
				i++;
			}
			String summarys[] = this.getResources().getStringArray(resid_title);
			if( find != -1 ) {
				val = summarys[find];
			}
			
			findPreference(key).setSummary( val );
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "changeSummary end"); }
		}
	}
	/**
	 * Call Phone  Volume
	 * 
	 * @param pref
	 * @return
	 */
	private boolean onMusicVolume(Preference prefArg) {
		try {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onMusicVolume start"); }
			final SeekBar seekBar = new SeekBar(this);
			final SharedPreferences pref = this.getSharedPreferences(Constant.SHARED_PREF, Context.MODE_WORLD_READABLE |Context.MODE_WORLD_WRITEABLE );
			final Preference pregArgS = prefArg;
			seekBar.setMax(mMaxVolume);
			seekBar.setProgress((int)pref.getInt(Constant.SHARED_PREF_PLAYMUSIC_VOLUM, 0));
			
	        // Show Dialog
	        new AlertDialog.Builder(this)
	        .setIcon(R.drawable.icon)
	        .setTitle( this.getString(R.string.title_select_volum) )
	        .setView(seekBar)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        		
	        		int val = seekBar.getProgress();
	        		Editor editor = pref.edit();
	        		editor.putInt(Constant.SHARED_PREF_PLAYMUSIC_VOLUM, val);
	        		editor.commit();
	        		
	        		PrefActivity.this.setVolumeSummary(pregArgS, val);

	        	}
	        })
	        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        	}
	        })
	        .show();
			
		} catch (Exception e) {
		} finally {
			if( MyLog.isDebugMod() ) { MyLog.logf(TAG, "onMusicVolume end"); }
		}
		return true;  
	}
	/**
	 * @param prefArg
	 * @param val
	 */
	private void setVolumeSummary(Preference prefArg, int val ) {
		double per = ((double)val/(double)mMaxVolume) * 100;
		prefArg.setSummary("" + (int)per + " % ");
	}
	
}
