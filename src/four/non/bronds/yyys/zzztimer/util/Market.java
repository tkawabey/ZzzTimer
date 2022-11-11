package four.non.bronds.yyys.zzztimer.util;



import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.Toast;

/**
 * 
 */
public class Market {
	final static String TAG = "Market";

    public static final String MARKET_PACKAGE_DETAILS_PREFIX = "market://details?id=";
    public static final String MARKET_AUTHOR_SEARCH_PREFIX = "market://search?q=";
    
    public enum INSTALLED_STS {
    	INSTALLED_STS_NOT,
    	INSTALLED_STS_OLD_VER,
    	INSTALLED_STS_EXIST
    }
    
    public static void intentLaunchMarketFallback(Activity context,  String market_search, int req_code) {
    	if( MyLog.isDebugMod()){ MyLog.logf(TAG, "intentLaunchMarketFallback start"); }
    	Uri market_uri = Uri.parse( MARKET_PACKAGE_DETAILS_PREFIX + market_search);
        Intent i = new Intent(Intent.ACTION_VIEW, market_uri);
        try {
            context.startActivityForResult(i, req_code);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Android Market not available.", Toast.LENGTH_LONG).show();
        } finally {
        	if( MyLog.isDebugMod()){ MyLog.logf(TAG, "intentLaunchMarketFallback end"); }
        }
    }
    public static INSTALLED_STS checkInstallPackage(Context context, String strPackageName){
    	if( MyLog.isDebugMod()){ MyLog.logf(TAG, "checkInstallPackage start"); }
    	final PackageManager packageManager = context.getPackageManager();
    	try {
			ApplicationInfo ai = packageManager.getApplicationInfo(strPackageName, 0);
			PackageInfo pc = context.getPackageManager().getPackageInfo(strPackageName, 1);
		} catch (NameNotFoundException e) {

			return INSTALLED_STS.INSTALLED_STS_NOT;
		} finally {
			if( MyLog.isDebugMod()){ MyLog.logf(TAG, "checkInstallPackage end"); }
        }
		return INSTALLED_STS.INSTALLED_STS_EXIST;
    }
}
