package four.non.bronds.yyys.zzztimer.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import four.non.bronds.yyys.zzztimer.bean.MusicTagBean;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import android.content.SharedPreferences;



public class DTFM {
	
//	public DTFMChanel	mChanel;
	public long		chanelHandle;
	
	public DTFM()
	{
		chanelHandle = 0;
//		mChanel = new DTFMChanel();
		
	}
	public void init() {
		initDefaultChanel(this);
	}
	public void destroy() {
		destroyDefaultChanel(this);
	}

	public static DTFM getInstance(int mode, SharedPreferences pref) {
		DTFM inst = new DTFM();
		
		initDefaultChanel(inst);
		inst.setSampleBits((short)16);
		if( mode == 0 ) {
			inst.setSampleBitPerSec(Integer.valueOf(pref.getString(Constant.SHARED_PREF_REC_BIT_PER_SEC, "22050")));
		} else
		if( mode == 1 ) {
			inst.setSampleBitPerSec(22050);//Integer.valueOf(pref.getString(Constant.SHARED_PREF_DTFM_BIT_PER_SEC, "22050")));
			inst.setSingnalMsec(50);//Integer.valueOf(pref.getString(Constant.SHARED_PREF_DTFM_SIGNAL_MSEC, "50")));
			inst.setBlankMsec(30);//Integer.valueOf(pref.getString(Constant.SHARED_PREF_DTFM_PUASE_MSEC, "30")));
			inst.setJudgeMsec(17);//Integer.valueOf(pref.getString(Constant.SHARED_PREF_DTFM_JUDGE_MSEC, "17")));
		}
		resetChanel(inst);

		return inst;
	}
	
    
	static {
        System.loadLibrary("zzztimer");
    }
    
	public byte[] getWaveFileHeader(int datalen)
	{
		return getWaveFileHeader(chanelHandle, datalen);
	}
	
	public byte[] string2PCM(String txt)
	{
		return string2PCM(chanelHandle, txt);
	}
	public String analyzeDTFMFile(String strFilePath)
	{
		return analyzeDTFMFile(chanelHandle, strFilePath);
	}
	
	


	
	public int getSampleBitPerSec()
	{
		return DTFM.getSampleBitPerSec(chanelHandle);
	}
	void setSampleBitPerSec(int val)
	{
		DTFM.setSampleBitPerSec(chanelHandle, val);
	}
			
	public short getSampleBits()
	{
		return DTFM.getSampleBits(chanelHandle);
	}
	public void setSampleBits(short val)
	{
		DTFM.setSampleBits(chanelHandle, val);
	}
	
	
	public short getChanel()
	{
		return DTFM.getChanel(chanelHandle);
	}
	public void setChanel(short val)
	{
		DTFM.setChanel(chanelHandle, val);
	}
	
	
	public int getSingnalMsec()
	{
		return DTFM.getSingnalMsec(chanelHandle);
	}
	public void setSingnalMsec(int val)
	{
		DTFM.setSingnalMsec(chanelHandle, val);
	}
	
	
	public int getJudgeMsec()
	{
		return DTFM.getJudgeMsec(chanelHandle);
	}
	public void setJudgeMsec(int val)
	{
		DTFM.setJudgeMsec(chanelHandle, val);
	}
	
	public int getBlankMsec()
	{
		return DTFM.getBlankMsec(chanelHandle);
	}
	public void setBlankMsec(int val)
	{
		DTFM.setBlankMsec(chanelHandle, val);
	}
	
	public int updateWaveFileHeaderFile(int datalen, String filePath, MusicTagBean[] tags)
	{
		return DTFM.updateWaveFileHeaderFile(chanelHandle, datalen, filePath, tags);
	}
	

	public void analyzeDTFMBuffring(byte[] datas, int arry_size, PCMRecordBuffring cb)
	{
		analyzeDTFMBuffring(chanelHandle, datas, arry_size, cb);
	}
	public String encExtString(String str)
	{
		return nEncExtString(str);
	}
	public String decExtString(String str)
	{
		return nDecExtString(str);
	}
	
	
	
	public boolean openWriteOgg(String fileParh, MusicTagBean[] tags)
	{
		return openWriteOgg(chanelHandle, fileParh, tags);
	}
	public boolean closeWriteOgg()
	{
		return closeWriteOgg(chanelHandle);
	}
	public boolean writeOgg(byte[] datas, int arry_size)
	{
		return writeOgg(chanelHandle, datas, arry_size);
	}
	
	
	
	native static void initDefaultChanel(DTFM chanel);
	native static void resetChanel(DTFM chanel);
	native static void destroyDefaultChanel(DTFM chanel);
	
	public native static int getSampleBitPerSec(long handle);
	public native static void setSampleBitPerSec(long handle, int val);
	
	public native static short getSampleBits(long handle);
	public native static void setSampleBits(long handle, short val);
	
	public native static short getChanel(long handle);
	public native static void setChanel(long handle, short val);
	
	public native static int getSingnalMsec(long handle);
	public native static void setSingnalMsec(long handle, int val);
	
	public native static int getJudgeMsec(long handle);
	public native static void setJudgeMsec(long handle, int val);
	
	public native static int getBlankMsec(long handle);
	public native static void setBlankMsec(long handle, int val);
	
	
	public native static byte[] getWaveFileHeader(long handle, int datalen);
	public native static int updateWaveFileHeaderFile(long handle, int datalen, String filePath, MusicTagBean[] tags);
	public native static byte[] string2PCM(long handle, String txt);
	public native static String analyzeDTFMFile(long handle, String strFilePath);
	public native static void analyzeDTFMBuffring(long handle, byte[] datas, int arry_size, PCMRecordBuffring cb);
	
	public native static String nEncExtString(String str);
	public native static String nDecExtString(String str);
	
	
	public native static boolean openWriteOgg(long handle, String fileParh, MusicTagBean[] tags);
	public native static boolean closeWriteOgg(long handle);
	public native static boolean writeOgg(long handle, byte[] datas, int arry_size);
	public native static boolean convertOgg(String fileIn, String strOut);
}
