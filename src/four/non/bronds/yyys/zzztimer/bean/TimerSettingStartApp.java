package four.non.bronds.yyys.zzztimer.bean;

import java.io.Serializable;

public class TimerSettingStartApp implements Serializable {


	/** Serial ID */
	private static final long serialVersionUID = 2273484578603574094L;

	private String mAppName = "";
	
	
	public TimerSettingStartApp()
	{
		
	}
	
	public String getmAppName() {
		return mAppName;
	}

	public void setmAppName(String mAppName) {
		this.mAppName = mAppName;
	}

}
