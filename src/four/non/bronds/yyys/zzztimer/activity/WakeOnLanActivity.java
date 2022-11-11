package four.non.bronds.yyys.zzztimer.activity;



import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * @author user
 *
 */
public class WakeOnLanActivity extends Activity {
	final static String TAG = "WakeOnLanActivity";
	private TimerSettingBean 	mItem = null; 

	private int			mRepeatCal = 0;
	/*
		<item>first one</item>
		<item>10 sec</item>
		<item>30 sec</item>
		<item>1 min</item>
		<item>2 min</item>
		<item>5 min</item>
		<item>10 min</item>
		<item>30 min</item>
		<item>1 hour</item>
	 */
	private static int repeat_val[] = {0,10,30,60,120,300, 600,1800,3600};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wol);
        // 自動でソフトキーボードが出るのを防ぐ 
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  
        
        EditText edt;
        CheckBox chk;
        Spinner	 spinner;
        
//        this.getResources().getStringArray(R.array.wol_repeat);
        
        try {
            Intent intent = this.getIntent();
            mItem = (TimerSettingBean)intent.getSerializableExtra(Constant.INTENT_TAG_TEIMER_ITEM);
            if( mItem == null ) {
            	throw new NullPointerException("inner error");
            }
            
            //
            //	Repeat Val
            //
            int iVal = mItem.getWolRepeat();
            int i = 0;
            for(int dispval : repeat_val ) {
            	if( dispval == iVal ) {
            		mRepeatCal=i;
            	}
            	i++;
            }
            
/*            
            //
            //
            //
            String[] sbIP = mItem.getWolIP().split("(\\:|\\-|\\.|\\ )");
            if( sbIP != null ) {
            	
            	edt = (EditText)this.findViewById(R.id.edtIP1);
            	edt.setText(sbIP[0]);
            	
            	edt = (EditText)this.findViewById(R.id.edtIP2);
            	edt.setText(sbIP[1]);
            	
            	edt = (EditText)this.findViewById(R.id.edtIP3);
            	edt.setText(sbIP[2]);
            	
            	edt = (EditText)this.findViewById(R.id.edtIP4);
            	edt.setText(sbIP[3]);
            	
            }
            
            //
            //	Port number
            //
            edt = (EditText)this.findViewById(R.id.edtPort);
            edt.setText("" + mItem.getWolPORT());
            

            //
            //	MAC Address
            //
            String[] sbMAC = mItem.getWolMAC().split("(\\:|\\-|\\.|\\ )");
            if( sbMAC != null ) {
            	edt = (EditText)this.findViewById(R.id.edtMAC1);
            	edt.setText(sbMAC[0]);
            	
            	edt = (EditText)this.findViewById(R.id.edtMAC2);
            	edt.setText(sbMAC[1]);

            	edt = (EditText)this.findViewById(R.id.edtMAC3);
            	edt.setText(sbMAC[2]);

            	edt = (EditText)this.findViewById(R.id.edtMAC4);
            	edt.setText(sbMAC[3]);

            	edt = (EditText)this.findViewById(R.id.edtMAC5);
            	edt.setText(sbMAC[4]);

            	edt = (EditText)this.findViewById(R.id.edtMAC6);
            	edt.setText(sbMAC[5]);
            	
            }
            
            spinner = (Spinner)findViewById(R.id.spinnerWolRepeat);
    		spinner.setSelection(mRepeatCal);

    		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    			@Override
    			public void onItemSelected(AdapterView<?> parent,
    					View view, int position, long id) {
    				mRepeatCal = position;
    			}

    			@Override
    			public void onNothingSelected(AdapterView<?> parent) {
    			}
    		});
            
    		Button btn = (Button)findViewById(R.id.apply);
    		btn.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View arg0) {
    				WakeOnLanActivity.this.onBtnApply();
    			}
    		});
    		
    		
    		chk = (CheckBox)findViewById(R.id.chkWakeOnLan);
    		chk.setChecked( mItem.isEnableWOL() );
    		chk.setFocusable(true);
*/
        }  catch(Exception e) {

    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
    		showAlert( strErr );
        }
    }
    
    /**
     * 
     */
    void onBtnApply() {
    	try {
    		EditText edt;
    		CheckBox chk;
    		String strIP;
    		String strMAC = "";
    		String strTmp = "";
    		int ips[] = {0,0,0,0};
    		int port = 0;
    		
    		
    		
    		//
    		//	IP Address
    		//
    		edt = (EditText)this.findViewById(R.id.edtIP1);
    		try {
    			ips[0] = Integer.parseInt(edt.getEditableText().toString());
    			if( ips[0] > 255 ) {
        			showAlert(this.getString(R.string.wol_invalid_ip));
        			return ;
    			}
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
    		}
    		edt = (EditText)this.findViewById(R.id.edtIP2);
    		try {
    			ips[1] = Integer.parseInt(edt.getEditableText().toString());
    			if( ips[1] > 255 ) {
        			showAlert(this.getString(R.string.wol_invalid_ip));
        			return ;
    			}
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
    		}
    		edt = (EditText)this.findViewById(R.id.edtIP3);
    		try {
    			ips[2] = Integer.parseInt(edt.getEditableText().toString());
    			if( ips[2] > 255 ) {
        			showAlert(this.getString(R.string.wol_invalid_ip));
        			return ;
    			}
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
    		}
    		edt = (EditText)this.findViewById(R.id.edtIP4);
    		try {
    			ips[3] = Integer.parseInt(edt.getEditableText().toString());
    			if( ips[3] > 255 ) {
        			showAlert(this.getString(R.string.wol_invalid_ip));
        			return ;
    			}
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
    		}
    		strIP = "" + ips[0] + "." + ips[1] + "." + ips[2] + "." + ips[3];
    		
    		
    		//
    		//	Port number
    		//
    		edt = (EditText)this.findViewById(R.id.edtPort);
    		try {
    			port = Integer.parseInt(edt.getEditableText().toString());
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_port));
    			return ;
    		}
    		
    		
    		

            //
            //	MAC Address
            //
    		edt = (EditText)this.findViewById(R.id.edtMAC1);
    		try {
    			strTmp = edt.getEditableText().toString();
    			strTmp.trim();
    			Integer.parseInt( strTmp, 16 );
    			if( strTmp.length() != 2 ) {
    				showAlert(this.getString(R.string.wol_invalid_mac));
    				return ;
    			}
    			strMAC += strTmp;
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_mac));
    			return ;
    		}
			strMAC += ":";

    		edt = (EditText)this.findViewById(R.id.edtMAC2);
    		try {
    			strTmp = edt.getEditableText().toString();
    			strTmp.trim();
    			Integer.parseInt( strTmp, 16 );
    			if( strTmp.length() != 2 ) {
    				showAlert(this.getString(R.string.wol_invalid_mac));
    				return ;
    			}
    			strMAC += strTmp;
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_mac));
    			return ;
    		}
			strMAC += ":";

    		edt = (EditText)this.findViewById(R.id.edtMAC3);
    		try {
    			strTmp = edt.getEditableText().toString();
    			strTmp.trim();
    			Integer.parseInt( strTmp, 16 );
    			if( strTmp.length() != 2 ) {
    				showAlert(this.getString(R.string.wol_invalid_mac));
    				return ;
    			}
    			strMAC += strTmp;
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_mac));
    			return ;
    		}
			strMAC += ":";

    		edt = (EditText)this.findViewById(R.id.edtMAC4);
    		try {
    			strTmp = edt.getEditableText().toString();
    			strTmp.trim();
    			Integer.parseInt( strTmp, 16 );
    			if( strTmp.length() != 2 ) {
    				showAlert(this.getString(R.string.wol_invalid_mac));
    				return ;
    			}
    			strMAC += strTmp;
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_mac));
    			return ;
    		}
			strMAC += ":";

    		edt = (EditText)this.findViewById(R.id.edtMAC5);
    		try {
    			strTmp = edt.getEditableText().toString();
    			strTmp.trim();
    			Integer.parseInt( strTmp, 16 );
    			if( strTmp.length() != 2 ) {
    				showAlert(this.getString(R.string.wol_invalid_mac));
    				return ;
    			}
    			strMAC += strTmp;
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_mac));
    			return ;
    		}
			strMAC += ":";


    		edt = (EditText)this.findViewById(R.id.edtMAC6);
    		try {
    			strTmp = edt.getEditableText().toString();
    			strTmp.trim();    			
    			Integer.parseInt( strTmp, 16 );
    			if( strTmp.length() != 2 ) {
    				showAlert(this.getString(R.string.wol_invalid_mac));
    				return ;
    			}
    			strMAC += strTmp;
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_mac));
    			return ;
    		}
    		/*
    		chk = (CheckBox)findViewById(R.id.chkWakeOnLan);
    		mItem.setEnableWOL(chk.isChecked());
    		mItem.setWolIP(strIP);
    		mItem.setWolPORT(port);
    		mItem.setWolMAC(strMAC);
    		mItem.setWolRepeat( repeat_val[mRepeatCal] );
    		*/
    		Intent intent = new Intent();
			intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mItem);
			this.setResult(Constant.RQ_CODE_WOL, intent);
    		this.finish();
        }  catch(Exception e) {

    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
    		showAlert( strErr );
        }
    }
    
    /**
     * @param strMsg
     */
    private void showAlert(String strMsg)
    {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.icon)
		.setTitle(Constant.APP_NAME)
		.setMessage(strMsg)
		.setPositiveButton("OK", null)
		.show();
    }
}
