package four.non.bronds.yyys.zzztimer.bean;

import java.io.Serializable;

public class RecTagBean implements Serializable {
	/** Serial ID */
	private static final long serialVersionUID = -2099105061656389014L;
	private String		mTag;
	private long		mTime;
	
	/**
	 * @param tag
	 * @param time
	 */
	public RecTagBean(String tag, long time) {
		mTag = tag;
		mTime = time;
	}
	
	public String getTag() {
		return mTag;
	}
	public void setTag(String mTag) {
		this.mTag = mTag;
	}
	public long getTime() {
		return mTime;
	}
	public void setTime(long mTime) {
		this.mTime = mTime;
	}
	
	public RecTagBean clone() {
		RecTagBean ret = new RecTagBean(mTag, mTime);
		return ret;
	}
	
	
	
	
	
}
