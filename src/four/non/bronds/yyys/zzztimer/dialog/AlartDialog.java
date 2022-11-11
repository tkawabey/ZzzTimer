package four.non.bronds.yyys.zzztimer.dialog;

import four.non.bronds.yyys.zzztimer.R;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;


public class AlartDialog {
	public static void show(Context v, String msg){
		Builder builder = new Builder(v);
		builder.setTitle( v.getString(R.string.app_name) );
		builder.setMessage(msg == null ? "msg is null" : msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener (){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}}
		);
		builder.show();
	}
}
