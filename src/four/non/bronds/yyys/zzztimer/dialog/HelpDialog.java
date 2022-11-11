package four.non.bronds.yyys.zzztimer.dialog;

import four.non.bronds.yyys.zzztimer.R;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class HelpDialog {
	public static void show(Activity activity, String strURL){
		
		LayoutInflater inflater = activity.getLayoutInflater();  
    	View v = inflater.inflate(R.layout.help, null, false);
    	ProgressBar progress = (ProgressBar)v.findViewById(R.id.progressBar);
		WebView w = (WebView)v.findViewById(R.id.webV);//new WebView(v);
		w.loadUrl(strURL);
		w.getSettings().setJavaScriptEnabled(true);
		w.setWebChromeClient(new WebChromeClient() {
			   public void onProgressChanged(WebView view, int progress) {
				   if( progress == 100 ) {
					   ProgressBar progressBar = (ProgressBar)view.getTag();
					   progressBar.setVisibility(View.GONE);
				   }
			   }
		});
		w.setTag(progress);

		Builder builder = new Builder(activity);
		builder.setTitle( activity.getString(R.string.app_name) );
		builder.setView(v);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener (){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}}
		);
		builder.show();
	}
}
