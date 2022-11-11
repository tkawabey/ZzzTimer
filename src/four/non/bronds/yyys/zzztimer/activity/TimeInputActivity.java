package four.non.bronds.yyys.zzztimer.activity;

import java.util.ArrayList;
import java.util.List;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.widgets.spinner.CustomSpinner;
import four.non.bronds.yyys.zzztimer.widgets.spinner.CustomSpinner.CustomSpinnerListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TimeInputActivity extends Activity {
	final static String TAG = "ZzzTimer.TimeInputActivity";

	private CustomSpinner					mSpinnerHour = null;
	private CustomSpinner					mSpinnerMinite= null;
	private int								mHour = 0;
	private int								mMinite = 0;
	private TextView						mTxtTitle;
	
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_input);
        
        mTxtTitle = (TextView)this.findViewById(R.id.txtTitle);
        
        //
        //	時間入力コントロールの初期化
        //
        mSpinnerHour = (CustomSpinner)this.findViewById(R.id.custom_spinner_hour);
        mSpinnerMinite = (CustomSpinner)this.findViewById(R.id.custom_spinner_minite);
        
        
        List<TextView> viewsHour = new ArrayList<TextView>();
		for (int i = 0; i <= 23; i++) {
			TextView element = new TextView(this);
			element.setText("" + i);
			element.setTextSize(40);
			element.setTextColor(Color.DKGRAY);
			element.setGravity(Gravity.CENTER);
			viewsHour.add(element);
		}
		mSpinnerHour.setViews(viewsHour, this);
		mSpinnerHour.setCurrentChildChangedListener(new CustomSpinnerListener() {
			@Override
			public void onScrollChanged(int currentChild) {
				mHour = currentChild;
				
				
				mTxtTitle.setText("" + mHour + " : " + mMinite);
			}
		});
		
		List<TextView> viewsMinite = new ArrayList<TextView>();
		for (int i = 0; i <= 59; i++) {
			TextView element = new TextView(this);
			element.setText("" + i);
			element.setTextSize(40);
			element.setTextColor(Color.DKGRAY);
			element.setGravity(Gravity.CENTER);
			viewsMinite.add(element);
		}
		mSpinnerMinite.setViews(viewsMinite, this);
		mSpinnerMinite.setCurrentChildChangedListener(new CustomSpinnerListener() {
			@Override
			public void onScrollChanged(int currentChild) {
				mMinite = currentChild;

				mTxtTitle.setText("" + mHour + " : " + mMinite);
			}
		});
        
		
		Button btn;
		
		btn = (Button)this.findViewById(R.id.btnOK);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				

				Intent intent = new Intent();
				intent.putExtra("HOUR", mHour);
				intent.putExtra("MINITE", mMinite);
				TimeInputActivity.this.setResult(500, intent);
				
				TimeInputActivity.this.finish();
			}
		});

		btn = (Button)this.findViewById(R.id.btnCancel);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				TimeInputActivity.this.finish();
			}
		});
		
    }
    

	@Override
	protected void onStop() {
		super.onStop();
     	mSpinnerHour.stopController();
     	mSpinnerMinite.stopController();
	}
	
	private	Handler	mHander = new Handler();
	
    public void onResume() {
    	Log.d(TAG, "onResume");
     	super.onResume();

     	mSpinnerHour.startController();
     	mSpinnerMinite.startController();
     	
        

        
    	new Thread(new Runnable() {
    	    public void run() {
    	    	
    	    	try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
    	    	mHander.post(new Runnable() {
		            public void run() {

		                Intent intent = TimeInputActivity.this.getIntent();
		                mHour = intent.getIntExtra("HOUR", 0);
		                mMinite = intent.getIntExtra("MINITE", 0);
		                
		                
		                mSpinnerHour.setCurrent(mHour);
		                mSpinnerMinite.setCurrent(mMinite);
		            }
		    	});
    	    }
        }).start();
    }
}
