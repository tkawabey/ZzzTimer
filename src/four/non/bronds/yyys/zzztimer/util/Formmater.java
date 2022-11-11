package four.non.bronds.yyys.zzztimer.util;

import java.util.Date;

import android.content.Context;
import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.activity.TimerItemActivity;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.cmn.Constant;

public class Formmater {

	/**
	 * カウントダウン用の文字列作成
	 * 
	 * @param millis
	 * @return
	 */
	static public String formatTime(long millis) {
		String output = "00:00:00";
		long seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;

		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 60;

		String secondsD = String.valueOf(seconds);
		String minutesD = String.valueOf(minutes);
		String hoursD = String.valueOf(hours);

		if (seconds < 10)
			secondsD = "0" + seconds;
		if (minutes < 10)
			minutesD = "0" + minutes;
		if (hours < 10)
			hoursD = "0" + hours;
		
		if( hours != 0 ) {
			output = hoursD + ":" + minutesD + ":" + secondsD;
		} else {
			output = minutesD + ":" + secondsD;
		}
		return output;
	}
	static public String getMiniteAfer(Context ctx, TimerSettingBean item) {
		return "" + (item.getHour() * 60 + item.getMinite()) + " " + ctx.getString(R.string.minite_after);
	}
	

	static public String getKillApp(Context ctx, TimerSettingBean item) {
		return ctx.getString(R.string.kill_app_count) + " : " +  item.getListCloseApp().size();
	}
	

	static public String getStartApp(Context ctx, TimerSettingBean item) {
		return ctx.getString(R.string.start_app_count) + " : " +  item.getListStartApp().size();
	}
	

	static public String getWOL(Context ctx, TimerSettingBean item) {
		
		if( item.isEnableWOL() ) {
			String repeatDis[] = ctx.getResources().getStringArray(R.array.wol_repeat);
			int index = 0;
			int i = 0;
			for(int iTargetVal : Constant.repeat_val ) {
				if( iTargetVal == item.getWolRepeat() ) {
					index = i;
					break;
				}
				i++;
			}
			
			String strRet = "";
			if( item.getWOLComputer() != null ) {
				strRet = item.getWOLComputer().getName();
				strRet += "\n";
			}
			strRet += ctx.getString(R.string.repeat) + " : " +  repeatDis[index];
			
			return strRet;
		} else {
			return ctx.getString(R.string.disable);
		}
	}
	static public String getRemoteOpe(Context ctx, TimerSettingBean item) {
		if( item.isEnableRemoteOpe() ) {
			String remoteOpes[] = ctx.getResources().getStringArray(R.array.computer_remote_operations);
			String strRet = "";
			if( item.getRemoteOpeComputer() != null ) {
				strRet = item.getRemoteOpeComputer().getName();
				strRet += "\n";
			}
			strRet += remoteOpes[ item.getRemoteOpe() ];
			return strRet;
		} else {
			return ctx.getString(R.string.disable);
		}
	}
	static public String getPlayMusic(Context ctx, TimerSettingBean item) {
		if( item.isMusic() ) {
			String strRet = item.getMusicName();
			return strRet;
		} else {
			return ctx.getString(R.string.disable);
		}
	}
	
	static public String getYYYYMMDD(Date date) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd");
		return formatter.format(date);
	}
	
	static public String getYYYYMMDDhhmmss(Date date) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return formatter.format(date);
	}
}
