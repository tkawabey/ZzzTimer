package four.non.bronds.yyys.zzztimer.bean;

import java.io.Serializable;

public class TimerSettingCloseAppBean implements Serializable {


	/** Serial ID */
	private static final long serialVersionUID = 4054426342552596874L;

	private String mAppName = "";


	
	public TimerSettingCloseAppBean()
	{
		
	}
	
	public String getmAppName() {
		return mAppName;
	}

	public void setmAppName(String mAppName) {
		this.mAppName = mAppName;
	}

}