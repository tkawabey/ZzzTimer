package four.non.bronds.yyys.zzztimer.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class TimerSettingBean implements Serializable {


	/** Serial ID */
	private static final long serialVersionUID = 3922600389566487397L;

	private int mID = 0;
	private String mName = "";
	private boolean	mWifiLock = true; 
	private boolean	mPoweriLock = true;
	private int		mHour = 0;
	private int		mMinite = 1;
	private String 	mTimeOpt = "";
	private String 	mRing = "";
	private String 	mRingOpt = "";
	private List<TimerSettingCloseAppBean>	mListCloseApp = null;
	private List<TimerSettingStartApp>	mListStartApp = null;
	private List<RecTagBean>			mRecTags = null;
	private int		mAudio = 0;
	private String 	mAudioOpt = "";
	private int		mBluetooth = 1;
	private String 	mBluetoothOpt = "";
	private int		mGPS = 1;
	private String 	mGPSOpt = "";
	private boolean mRecord = false;
	private boolean mRunning = false;
	

	// WOL
	private int     mWolComputerID = -1;
	private int		mWolRepeat = 0;
	private Computer	mWOLComputer = null;
	// Remote Operation
	private int     mRemoteOpeComputerID = -1;
	private int		mRemoteOpe = 0;
	private Computer	mRemoteOpeComputer = null;
	// Music
	private boolean mMusic = false;
	private int     mMusicID = 0;
	private boolean mMusicShuffle = false;
	private boolean mMusicScrib = false;
	private String  mMusicName = "";
	

//	private boolean mEnableWOL = false;
//	private String  mWolIP = "255.255.255.255";
//	private int     mWolPORT = 2304;
//	private String  mWolMAC = "00:00:00:00:00:00";
	







	public TimerSettingBean()
	{
		mListCloseApp = new ArrayList<TimerSettingCloseAppBean>();
		mListStartApp = new ArrayList<TimerSettingStartApp>();
		mRecTags = new ArrayList<RecTagBean>();
	}
	
	
	public int getID() {
		return mID;
	}
	public void setID(int mID) {
		this.mID = mID;
	}
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public boolean isWifiLock() {
		return mWifiLock;
	}
	public void setWifiLock(boolean mWifiLock) {
		this.mWifiLock = mWifiLock;
	}
	public boolean isPoweriLock() {
		return mPoweriLock;
	}
	public void setPoweriLock(boolean mPoweriLock) {
		this.mPoweriLock = mPoweriLock;
	}
	public int getHour() {
		return mHour;
	}

	public void setHour(int mHouer) {
		this.mHour = mHouer;
	}


	public int getMinite() {
		return mMinite;
	}
	public void setMinite(int mMinite) {
		this.mMinite = mMinite;
	}


	public String getTimeOpt() {
		return mTimeOpt;
	}
	public void setTimeOpt(String mTimeOpt) {
		this.mTimeOpt = mTimeOpt;
	}


	public String getRing() {
		return mRing;
	}
	public void setmRing(String mRing) {
		this.mRing = mRing;
	}


	public String getRingOpt() {
		return mRingOpt;
	}
	public void setmRingOpt(String mRingOpt) {
		this.mRingOpt = mRingOpt;
	}

	
	public int getmAudio() {
		return mAudio;
	}
	public void setmAudio(int mAudio) {
		this.mAudio = mAudio;
	}


	public String getAudioOpt() {
		return mAudioOpt;
	}
	public void setAudioOpt(String mAudioOpt) {
		this.mAudioOpt = mAudioOpt;
	}


	public int getBluetooth() {
		return mBluetooth;
	}
	public void setBluetooth(int mBluetoth) {
		this.mBluetooth = mBluetoth;
	}


	public String getBluetoothOpt() {
		return mBluetoothOpt;
	}
	public void setBluetoothOpt(String mBluetothOpt) {
		this.mBluetoothOpt = mBluetothOpt;
	}


	public int getGPS() {
		return mGPS;
	}
	public void setGPS(int mGPS) {
		this.mGPS = mGPS;
	}


	public String getGPSOpt() {
		return mGPSOpt;
	}
	public void setGPSOpt(String mGPSOpt) {
		this.mGPSOpt = mGPSOpt;
	}

	

	public boolean isRunning() {
		return mRunning;
	}
	public void setRunning(boolean mRunning) {
		this.mRunning = mRunning;
	}	
	
	
	
	
	
	public List<TimerSettingCloseAppBean> getListCloseApp() {
		return mListCloseApp;
	}
	public void setListCloseApp(List<TimerSettingCloseAppBean> mListCloseApp) {
		this.mListCloseApp = mListCloseApp;
	}
	public TimerSettingCloseAppBean addClosseApp(String strAPP) {
		
		for(TimerSettingCloseAppBean ite : mListCloseApp) {
			if( ite.getmAppName().compareTo(strAPP) == 0 ) {
				return ite;
			}
		}
		
		TimerSettingCloseAppBean app = new TimerSettingCloseAppBean();
		app.setmAppName( strAPP );
		mListCloseApp.add(app);
		return app;
	}
	public boolean isExistKillApp(String strApp) {
		for(TimerSettingCloseAppBean ite : mListCloseApp) {
			if( ite.getmAppName().compareTo(strApp) == 0 ) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public List<TimerSettingStartApp> getListStartApp() {
		return mListStartApp;
	}


	public void setListStartApp(List<TimerSettingStartApp> mListStartApp) {
		this.mListStartApp = mListStartApp;
	}
	public TimerSettingStartApp addStartApp(String strAPP) {
		
		for(TimerSettingStartApp ite : mListStartApp) {
			if( ite.getmAppName().compareTo(strAPP) == 0 ) {
				return ite;
			}
		}
		
		TimerSettingStartApp app = new TimerSettingStartApp();
		app.setmAppName( strAPP );
		mListStartApp.add(app);
		return app;
	}
	public boolean isExistStartApp(String strApp) {
		for(TimerSettingStartApp ite : mListStartApp) {
			if( ite.getmAppName().compareTo(strApp) == 0 ) {
				return true;
			}
		}
		return false;
	}
	

	public List<RecTagBean> getmRecTags() {
		return mRecTags;
	}

	
	
	public boolean isEnableWOL() 
	{
		return mWolComputerID != -1;
		//return mEnableWOL;
	}
	
	
	public int getWolRepeat() {
		return mWolRepeat;
	}
	public void setWolRepeat(int mWolRepeat) {
		this.mWolRepeat = mWolRepeat;
	}

	public boolean isRecord() {
		return mRecord;
	}
	public void setRecord(boolean mRecord) {
		this.mRecord = mRecord;
	}


	public int getWolComputerID() {
		return mWolComputerID;
	}


	public void setWolComputerID(int mWolComputerID) {
		this.mWolComputerID = mWolComputerID;
	}

	public boolean isEnableRemoteOpe() {
		return mRemoteOpeComputerID != -1;
	}
	
	public int getRemoteOpeComputerID() {
		return mRemoteOpeComputerID;
	}


	public void setRemoteOpeComputerID(int mRemoteOpeComputerID) {
		this.mRemoteOpeComputerID = mRemoteOpeComputerID;
	}


	public int getRemoteOpe() {
		return mRemoteOpe;
	}


	public void setRemoteOpe(int mRemoteOpe) {
		this.mRemoteOpe = mRemoteOpe;
	}


	public Computer getWOLComputer() {
		return mWOLComputer;
	}


	public void setWOLComputer(Computer mWOLComputer) {
		this.mWOLComputer = mWOLComputer;
	}


	public Computer getRemoteOpeComputer() {
		return mRemoteOpeComputer;
	}


	public void setRemoteOpeComputer(Computer mRemoteOpeComputer) {
		this.mRemoteOpeComputer = mRemoteOpeComputer;
	}


	public boolean isMusic() {
		return mMusic;
	}
	public void setMusic(boolean mMusic) {
		this.mMusic = mMusic;
	}
	
	public int getMusicID() {
		return mMusicID;
	}
	public void setMusicID(int mMusicID) {
		this.mMusicID = mMusicID;
	}
	
	public boolean isMusicShuffle() {
		return mMusicShuffle;
	}
	public void setMusicShuffle(boolean mMusicShuffle) {
		this.mMusicShuffle = mMusicShuffle;
	}


	public boolean isMusicScrib() {
		return mMusicScrib;
	}
	public void setMusicScrib(boolean mMusicScrib) {
		this.mMusicScrib = mMusicScrib;
	}


	public String getMusicName() {
		if( mMusicName == null ) {
			return "";
		}
		return mMusicName;
	}
	public void setMusicName(String mMusicName) {
		this.mMusicName = mMusicName;
	}

/*	
	public void setEnableWOL(boolean mEnableWOL) {
		this.mEnableWOL = mEnableWOL;
	}
	public String getWolIP() {
		return mWolIP;
	}
	public void setWolIP(String mWolIP) {
		this.mWolIP = mWolIP;
	}


	public int getWolPORT() {
		return mWolPORT;
	}
	public void setWolPORT(int mWolPORT) {
		this.mWolPORT = mWolPORT;
	}


	public String getWolMAC() {
		return mWolMAC;
	}
	public void setWolMAC(String mWolMAC) {
		this.mWolMAC = mWolMAC;
	}
*/
}

