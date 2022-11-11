package four.non.bronds.yyys.zzztimer.activity;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;

public class MyBaseActivity extends FragmentActivity {
	
	
	protected void setCurrentTheme() {
		this.setTheme(R.style.MyLightTheme);
	}
	
	
    /**
     * @param strMsg
     */
    protected void showAlert(String strMsg)
    {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.icon)
		.setTitle(Constant.APP_NAME)
		.setMessage(strMsg)
		.setPositiveButton("OK", null)
		.show();
    }

}
