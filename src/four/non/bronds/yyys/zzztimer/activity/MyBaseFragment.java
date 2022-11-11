package four.non.bronds.yyys.zzztimer.activity;


import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

public class MyBaseFragment extends Fragment {
	private static final String TAG = "MyBaseFragment";
	protected FragmentActivity		mActivity;
	protected View					mRootView;


    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }
    @Override
    public void onAttach(Activity act){
        super.onAttach(act);
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onAttach"); }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onCreate"); }
    }
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onActivityCreated"); }
    }

    @Override
    public void onStart(){
        super.onStart();
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onStart"); }
    }

    @Override
    public void onResume(){
        super.onResume();
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onResume"); }
    }

    @Override
    public void onPause(){
        super.onPause();
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onPause"); }
    }

    @Override
    public void onStop(){
        super.onStop();
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onStop"); }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onDestroyView"); }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onDestroy"); }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        if( MyLog.isDebugMod() ) { MyLog.logt(TAG,"Fragment-onDetach"); }
    }
    /**
     * @param strMsg
     */
    protected void showAlert(String strMsg)
    {
    	if( strMsg == null ) {
    		strMsg = "Internal Error.";
    	}
		new AlertDialog.Builder(this.getActivity())
		.setIcon(R.drawable.icon)
		.setTitle(Constant.APP_NAME)
		.setMessage(strMsg)
		.setPositiveButton("OK", null)
		.show();
    }
}
