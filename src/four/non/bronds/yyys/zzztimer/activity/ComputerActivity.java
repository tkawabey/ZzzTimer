package four.non.bronds.yyys.zzztimer.activity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.Computer;
import four.non.bronds.yyys.zzztimer.client.ZzzSvcClient;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.db.ComputerAccessor;
import four.non.bronds.yyys.zzztimer.dialog.HelpDialog;
import four.non.bronds.yyys.zzztimer.util.MyLog;
import four.non.bronds.yyys.zzztimer.util.PrefereceAcc;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.DateSlider;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.TimeSlider;

public class ComputerActivity extends MyBaseActivity {
	private static final String TAG = "ComputerActivity";
	private ComputerFragment mFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		Computer comp = (Computer)intent.getSerializableExtra("COMPUTER");
		Bundle args = new Bundle();
		args.putSerializable("COMPUTER", comp);
		// フラグメントインスタンスを作成
		mFragment = new StaticComputerFragment();
		mFragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, mFragment).commit();
		
		
		// コンピュータデータをフラグメントに設定
//		mFragment.setItem(comp);
	}

	public static class StaticComputerFragment extends ComputerFragment {
	}	
}

/**
 * タイマーフラグメント
 */
class ComputerFragment extends MyBaseFragment  implements OnSeekBarChangeListener{
	final static String TAG = "ComputersFragment";
	private Computer	mComputer = null;
	private SeekBar				mSeekBarOnOff = null;
	private Button		mBtnRestartAfterTime = null;
	private Button		mBtnShutdownAfterTime = null;
	private int		 	mRestartAfterTimeHour = 0;
	private int		 	mRestartAfterTimeMinite = 0;
	private int			mShutdownAfterTimeHour = 0;
	private int			mShutdownAfterTimeMinite = 0;
	
	// define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener mDateSetListenerRestartAfterTime =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
           		mRestartAfterTimeHour = selectedDate.get(Calendar.HOUR_OF_DAY);
           		mRestartAfterTimeMinite = selectedDate.get(Calendar.MINUTE);
           		mBtnRestartAfterTime.setText( String.format("%02d:%02d", mRestartAfterTimeHour, mRestartAfterTimeMinite));
              }
    };   
	// define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener mDateSetListenerShutdowAfterTime =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	mShutdownAfterTimeHour = selectedDate.get(Calendar.HOUR_OF_DAY);
            	mShutdownAfterTimeMinite = selectedDate.get(Calendar.MINUTE);
            	mBtnShutdownAfterTime.setText( String.format("%02d:%02d", mShutdownAfterTimeHour, mShutdownAfterTimeMinite));
              }
    }; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {
			if (container == null) {
				return null;
			}
			mActivity = this.getActivity();
			mRootView = inflater.inflate(R.layout.computer, container, false);
			
			Button btn = (Button)mRootView.findViewById(R.id.btnApply);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ComputerFragment.this.onApplyClick(v);
				}
			});
			ImageButton btnImg = (ImageButton)mRootView.findViewById(R.id.btnSearchZZZ);
			btnImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ComputerFragment.this.onClickSearchZzz(v);
				}
			});

    		EditText edt;
    		edt = (EditText)mRootView.findViewById(R.id.edtName);
    		edt.setFocusable(true);

    		edt = (EditText)mRootView.findViewById(R.id.edtIPAddress);
    		edt.setText(getIpAddress());
    		
    		
			mBtnRestartAfterTime = (Button)mRootView.findViewById(R.id.btnRestartAfterTime);
			mBtnRestartAfterTime.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				final Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, mRestartAfterTimeHour);
					c.set(Calendar.MINUTE, mRestartAfterTimeMinite);
    				TimeSlider timeSlider = new TimeSlider(ComputerFragment.this.mActivity,
    						mDateSetListenerRestartAfterTime, c);
					timeSlider.show();
    			}
    		});
			
			mBtnShutdownAfterTime = (Button)mRootView.findViewById(R.id.btnShutdownAfterTime);
			mBtnShutdownAfterTime.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				final Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, mShutdownAfterTimeHour);
					c.set(Calendar.MINUTE, mShutdownAfterTimeMinite);
    				TimeSlider timeSlider = new TimeSlider(ComputerFragment.this.mActivity, 
    						mDateSetListenerShutdowAfterTime, c);
					timeSlider.show();
    			}
    		});
			

			mSeekBarOnOff = (SeekBar)mRootView.findViewById(R.id.seekBarOnOff);
			mSeekBarOnOff.setMax(10);
			mSeekBarOnOff.setProgress(10);
			Bitmap bMap = BitmapFactory.decodeResource(mRootView.getResources(), R.drawable.bgseekbaronoff);
			mSeekBarOnOff.setLayoutParams(new LayoutParams(bMap.getWidth(),bMap.getHeight()));
			mSeekBarOnOff.setProgressDrawable(getResources().getDrawable(R.drawable.bgseekbaronoff));
			mSeekBarOnOff.setThumb(getResources().getDrawable(R.drawable.buttonseekbar));
			mSeekBarOnOff.setThumbOffset(-6);
			mSeekBarOnOff.setOnSeekBarChangeListener(this);
			mSeekBarOnOff.setTag("0");
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView end");}
		}		
		return mRootView;
	}
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	// オプションメニュー
    	setHasOptionsMenu(true);
    	
    	// 画面作成が完了したら、コンピューターのデータを画面に表示
    	mComputer = (Computer)this.getArguments().getSerializable("COMPUTER");
    	this.setItem(mComputer);
    }
    // メニューの作成
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	MenuItem item ;

//    	item = menu.add(Menu.NONE, 0,Menu.NONE, this.getString(R.string.add));
//        item.setIcon(R.drawable.add);
//        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        

        item = menu.add(Menu.NONE, 1,Menu.NONE, this.getActivity().getString(R.string.help));
        item.setIcon( android.R.drawable.ic_menu_help);
        MenuCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM );
    }

    // オプションメニューアイテムが選択された時に呼び出されます
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if( item.getItemId() == 0 ) {
    		this.doApply();
    	} else
    	if( item.getItemId() == 1 ) {

    		PrefereceAcc acc = new PrefereceAcc(this.getActivity());
    		String strURL = "https://sites.google.com/site/zzztimerjp/";
   			strURL = acc.getHelpURL(PrefereceAcc.HELP_URL.HELP_URL_COMPUTER_ADD);
    		HelpDialog.show(this.getActivity(), strURL);
    	}
    	return true;
    }
    
    private String getIpAddress() {
    	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "getIpAddress start");}
    	List<List<Integer>> ips = new ArrayList<List<Integer>>();
        Enumeration<NetworkInterface> netIFs;
        
        try {
        	int findIndex = -1;
        	int i = 0;
            netIFs = NetworkInterface.getNetworkInterfaces();
            while( netIFs.hasMoreElements() ) {
                NetworkInterface netIF = netIFs.nextElement();
                Enumeration<InetAddress> ipAddrs = netIF.getInetAddresses();
                while( ipAddrs.hasMoreElements() ) {
                    InetAddress ip = ipAddrs.nextElement();
                    if( ! ip.isLoopbackAddress() ) {
                    	List<Integer> ipA = new ArrayList<Integer>();
                    	
                    	
                    	byte[] addr = ip.getAddress();
                    	int ip1 = ZzzSvcClient.getByte2Int(addr[0]);
                    	if( ip1 == 192 ) {
                    		findIndex = i;
                    	}
                    	if( MyLog.isDebugMod() ){ MyLog.logt(TAG, "IP : " +
                    			ZzzSvcClient.getByte2Int(addr[0]) + "." 
                    			+ ZzzSvcClient.getByte2Int(addr[1]) + "."
                    			+ ZzzSvcClient.getByte2Int(addr[2]) + "."
                    			+ ZzzSvcClient.getByte2Int(addr[3]) + ".");}
                    	ipA.add(ZzzSvcClient.getByte2Int(addr[0]));
                    	ipA.add(ZzzSvcClient.getByte2Int(addr[1]));
                    	ipA.add(ZzzSvcClient.getByte2Int(addr[2]));
                    	ipA.add(ZzzSvcClient.getByte2Int(addr[3]));
                    	ips.add(ipA);
                    	 i++;
//                    	StringBuilder sb = new StringBuilder();
//    	    			sb.append(ZzzSvcClient.getByte2Int(addr[0]));
//    	    			sb.append(".");
//    	    			sb.append(ZzzSvcClient.getByte2Int(addr[1]));
//    	    			sb.append(".");
//    	    			sb.append(ZzzSvcClient.getByte2Int(addr[2]));
//    	    			sb.append(".");
//    	    			return sb.toString();
//                      return ip.getHostAddress().toString();
                    }
                }
            }
            if( MyLog.isDebugMod() ){ MyLog.logt(TAG, "findIndex:" +  findIndex);}
            if( ips.size() == 0 ) {
            	return null;
            }
            if( findIndex == -1 ) {
            	findIndex = 0;
            }
            List<Integer> ip = ips.get(findIndex);
            StringBuilder sb = new StringBuilder();
    		sb.append( ip.get(0) );
    		sb.append(".");
    		sb.append( ip.get(1) );
    		sb.append(".");
    		sb.append( ip.get(2) );
    		sb.append(".");
    		return sb.toString();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "getIpAddress end");}
        }
        return null;
    }
    /**
     * 指定されたコンピューターの情報で、画面を更新
     * @param comp
     */
    public void setItem(Computer comp) {
    	if(MyLog.isDebugMod()) {MyLog.logf(TAG, "setItem start");}
		try {
    		EditText edt;
    		byte bytearry[];
    		int i = 0;
    		TextView txtV = null;
    		
    		if( mRootView == null ) {
    			if(MyLog.isDebugMod()) {MyLog.logt(TAG, "mRootView is null");}
    			return ;
    		}
    		
    		
    		
			mComputer = comp;
			if( mComputer == null ) {
				txtV = (TextView)mRootView.findViewById(R.id.textTitle);
				txtV.setText(this.getString(R.string.title_add_computer));
				
	    		edt = (EditText)mRootView.findViewById(R.id.edtName);
	    		edt.setText("");
	    		edt = (EditText)mRootView.findViewById(R.id.edtIPAddress);
	    		edt.setText(getIpAddress());
	    		edt = (EditText)mRootView.findViewById(R.id.edtIP1);
	    		edt.setText("255");
	    		edt = (EditText)mRootView.findViewById(R.id.edtIP2);
	    		edt.setText("255");
	    		edt = (EditText)mRootView.findViewById(R.id.edtIP3);
	    		edt.setText("255");
	    		edt = (EditText)mRootView.findViewById(R.id.edtIP4);
	    		edt.setText("255");
	    		edt = (EditText)mRootView.findViewById(R.id.edtPort);
	    		edt = (EditText)mRootView.findViewById(R.id.edtZzzPort);
	    		edt.setText("4949");
	    		edt = (EditText)mRootView.findViewById(R.id.edtMAC1);
	    		edt.setText("00");
	    		edt = (EditText)mRootView.findViewById(R.id.edtMAC2);
	    		edt.setText("00");
	    		edt = (EditText)mRootView.findViewById(R.id.edtMAC3);
	    		edt.setText("00");
	    		edt = (EditText)mRootView.findViewById(R.id.edtMAC4);
	    		edt.setText("00");
	    		edt = (EditText)mRootView.findViewById(R.id.edtMAC5);
	    		edt.setText("00");
	    		edt = (EditText)mRootView.findViewById(R.id.edtMAC6);
	    		edt.setText("00");
	    		edt = (EditText)mRootView.findViewById(R.id.edtExeImage);
	    		edt.setText("");
	    		edt = (EditText)mRootView.findViewById(R.id.edtExeParam);
	    		edt.setText("");
	    		edt = (EditText)mRootView.findViewById(R.id.edtExeCurDir);
	    		edt.setText("");
	    		edt = (EditText)mRootView.findViewById(R.id.edtAlertMessage);
	    		edt.setText("");
				return;
			}
			// Title
			txtV = (TextView)mRootView.findViewById(R.id.textTitle);
			txtV.setText(this.getString(R.string.title_edt_computer));
			
    		//
    		//	Name
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtName);
    		edt.setText(mComputer.getName());
    		//
    		//	IP Address
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtIPAddress);
    		edt.setText(mComputer.getHostname());
			//
    		//	Broad Cast Address
    		//
    		bytearry = ZzzSvcClient.toByte(mComputer.getBroadcast_addr());
    		edt = (EditText)mRootView.findViewById(R.id.edtIP1);
    		edt.setText("" + ZzzSvcClient.getByte2Int(bytearry[3]));
    		edt = (EditText)mRootView.findViewById(R.id.edtIP2);
    		edt.setText("" + ZzzSvcClient.getByte2Int(bytearry[2]));
    		edt = (EditText)mRootView.findViewById(R.id.edtIP3);
    		edt.setText("" + ZzzSvcClient.getByte2Int(bytearry[1]));
    		edt = (EditText)mRootView.findViewById(R.id.edtIP4);
    		edt.setText("" + ZzzSvcClient.getByte2Int(bytearry[0]));
    		//
    		//	WOL Port
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtPort);
    		edt.setText("" + mComputer.getPort_wol());
    		//
    		//	ZzzServer Port
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtZzzPort);
    		edt.setText("" + mComputer.getZzz_tcp_port());
    		
    		//
    		//	MAC Address
    		//
            String[] sbMAC = mComputer.getMac_addr().split("(\\:|\\-|\\.|\\ )");
    		i = 0;
    		int mac_ids[] = {R.id.edtMAC1, R.id.edtMAC2, R.id.edtMAC3, R.id.edtMAC4, R.id.edtMAC5, R.id.edtMAC6};
    		for(String str : sbMAC) {
    			edt = (EditText)mRootView.findViewById(mac_ids[i]);
    			edt.setText( str );
    			i++;
    		}

    		
    		int restartAfterTime = mComputer.getResume_opt();
    		mRestartAfterTimeHour = restartAfterTime / 60;
    		mRestartAfterTimeMinite = restartAfterTime % 60;
    		
    		int shutdownAfterTime = mComputer.getShutdown_after_tm() / 60;
    		mShutdownAfterTimeHour = shutdownAfterTime / 60;
    		mShutdownAfterTimeMinite = shutdownAfterTime % 60;
    		
    		mBtnRestartAfterTime.setText( String.format("%02d:%02d", mRestartAfterTimeHour, mRestartAfterTimeMinite));
    		mBtnShutdownAfterTime.setText( String.format("%02d:%02d", mShutdownAfterTimeHour, mShutdownAfterTimeMinite));
    		

    		//
    		//	Execute Image
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtExeImage);
    		edt.setText(mComputer.getExec_image());
    		
    		//
    		//	Execute Params
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtExeParam);
    		edt.setText(mComputer.getExec_params());
    		
    		//
    		//	Execute Current Directory
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtExeCurDir);
    		edt.setText(mComputer.getExec_cur_dir());

    		if( mComputer.getResume() == 1 ) {
    			mSeekBarOnOff.setProgress(10);
    		} else {
    			mSeekBarOnOff.setProgress(0);
    		}    		
    		// 
    		edt = (EditText)mRootView.findViewById(R.id.edtAlertMessage);
    		edt.setText( mComputer.getShutdown_msg() );
		} catch (Exception e) {
			MyLog.loge(TAG, e);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "setItem end");}
		}
    }
	/**
	 * Applyボタンクリック時の実装
	 * @param v
	 */
	public void onApplyClick(View v) {
		this.doApply();
	}
	
	/**
	 * Applyの実行
	 */
	private void doApply() {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onApplyClick start");	}
		try {
			Computer comp = mComputer;//new Computer();
    		EditText edt;
    		byte ips[] = {0,0,0,0};
			
    		if( comp == null ) {
    			comp = new Computer();
    		}
    		//
    		//	Name
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtName);
    		comp.setName(edt.getEditableText().toString().trim());
    		//
    		//	IP Address
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtIPAddress);
    		comp.setHostname(edt.getEditableText().toString().trim());
			//
    		//	Broad Cast Address
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtIP1);
    		try {
    			ips[3] = (byte)(int)Integer.parseInt(edt.getEditableText().toString());
    			if( ips[3] > 255 ) {
        			showAlert(this.getString(R.string.wol_invalid_ip));
        			return ;
    			}
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
    		}
    		edt = (EditText)mRootView.findViewById(R.id.edtIP2);
    		try {
    			ips[2] = (byte)(int)Integer.parseInt(edt.getEditableText().toString());
    			if( ips[2] > 255 ) {
        			showAlert(this.getString(R.string.wol_invalid_ip));
        			return ;
    			}
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
    		}
    		edt = (EditText)mRootView.findViewById(R.id.edtIP3);
    		try {
    			ips[1] = (byte)(int)Integer.parseInt(edt.getEditableText().toString());
    			if( ips[1] > 255 ) {
        			showAlert(this.getString(R.string.wol_invalid_ip));
        			return ;
    			}
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
    		}
    		edt = (EditText)mRootView.findViewById(R.id.edtIP4);
    		try {
    			ips[0] = (byte)(int)Integer.parseInt(edt.getEditableText().toString());
    			if( ips[0] > 255 ) {
        			showAlert(this.getString(R.string.wol_invalid_ip));
        			return ;
    			}
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
    		}
    		InputStream bais = new ByteArrayInputStream(ips);
    		DataInputStream dins = new DataInputStream(bais);
    		try {
				comp.setBroadcast_addr( dins.readInt() );
			} catch (IOException e1) {
    			showAlert(this.getString(R.string.wol_invalid_ip));
    			return ;
			}
    		//strIP = "" + ips[0] + "." + ips[1] + "." + ips[2] + "." + ips[3];
    		
    		//
    		//	WOL Port
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtPort);
    		try {
    			comp.setPort_wol(Integer.parseInt(edt.getEditableText().toString()));
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_port));
    			return ;
    		}
    		//
    		//	ZzzServer Port
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtZzzPort);
    		try {
    			comp.setZzz_tcp_port(Integer.parseInt(edt.getEditableText().toString()));
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_port));
    			return ;
    		}
    		
    		//
    		//	MAC Address
    		//
    		String strTmp;
    		String strMAC = "";
    		int mac_ids[] = {R.id.edtMAC1, R.id.edtMAC2, R.id.edtMAC3, R.id.edtMAC4, R.id.edtMAC5, R.id.edtMAC6};
    		for(int mac_id : mac_ids) {
    			edt = (EditText)mRootView.findViewById(mac_id);
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
        		if( mac_id != R.id.edtMAC6) {
        			strMAC += ":";
        		}
    		}
    		comp.setMac_addr(strMAC);
    		

    		//
    		//	Execute Image
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtExeImage);
    		comp.setExec_image(edt.getEditableText().toString().trim());
    		
    		//
    		//	Execute Params
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtExeParam);
    		comp.setExec_params(edt.getEditableText().toString().trim());
    		
    		//
    		//	Execute Current Directory
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtExeCurDir);
    		comp.setExec_cur_dir(edt.getEditableText().toString().trim());
    		
    		//
    		//	Resume
    		//
    		comp.setResume(mSeekBarOnOff.getProgress() == 10 ? 1 : 0);

			int restartAfterTime = mRestartAfterTimeHour * 60 + mRestartAfterTimeMinite;
			int shutdownAfterTime = (mShutdownAfterTimeHour * 60 + mShutdownAfterTimeMinite)*60;
			comp.setResume_opt( restartAfterTime );
			comp.setShutdown_after_tm(shutdownAfterTime);
			//
			//	edtAlertMessage
			//
    		edt = (EditText)mRootView.findViewById(R.id.edtAlertMessage);
			comp.setShutdown_msg(edt.getEditableText().toString().trim());
			
			ComputerAccessor acc = new ComputerAccessor(this.getActivity());
			if( mComputer == null ) {
				acc.add(comp);
			} else {
				acc.edit(comp);
			}
    		
    		
    		Activity activity = this.getActivity();
/*    		
    		if( activity instanceof MainActivity ) {
				MainActivity ac = (MainActivity)mActivity;
				ComputersFragment compFragment = (ComputersFragment)ac.mTabFragment.showDetails(TabsFragment.FRAGMENT_INDEX_COMPUTERS);
				if( compFragment != null ) {
					//compFragment.reView();
				}
    		} else {
*/    		
				Intent intent = new Intent();
				intent.putExtra("COMPUTER", comp);
	    		activity.setResult(Constant.RQ_CODE_EDT_COMPUTER, intent);
	    		activity.finish();
/*	    		
    		}
*/
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onApplyClick end");}
		}
	}

	/**
	 * ZzzServerを問い合わせボタンが押下された
	 * @param v
	 */
	public void onClickSearchZzz(View v) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onClickSearchZzz start");	}
		try {
    		EditText edt;
    		String strHostName;
    		int port;
    		//
    		//	IP Address
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtIPAddress);
    		strHostName = edt.getEditableText().toString().trim();
    		
    		// 入力されたアドレスが足しいか確認
			InetAddress.getByName(strHostName);

    		
    		//
    		//	ZzzServer Port
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtZzzPort);
    		try {
    			port = Integer.parseInt(edt.getEditableText().toString());
    		} catch(Exception e) {
    			showAlert(this.getString(R.string.wol_invalid_port));
    			return ;
    		}
    		// 非同期タスクを実行して、問い合わせる
    		AsyncResolve async = new AsyncResolve(this.getActivity(), this, strHostName, port);
    		async.execute("");
    		
		} catch (UnknownHostException e1) {
			MyLog.loge(TAG, e1);
			this.showAlert(this.getString(R.string.err_incorrect_ip));
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onClickSearchZzz end");}
		}
	}
	
	/**
	 * ZzzServerを問い合わせが完了したときにコールされる。画面を再描画する
	 * @param compInf
	 */
	public void reViewResolveCallback(ZzzSvcClient.ZzzCompInf compInf ) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "reViewResolveCallback start");}	
		try {
			
			
			if( compInf.netinfs.size() > 1 ) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
				Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag("wol_dialog");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.addToBackStack(null);
				
				SelectCompDialogFragment dlg = SelectCompDialogFragment.newInstance(this, compInf);
				dlg.show(ft, "select_comp_dialog");
			}
			
			
    		EditText edt;
    		//
    		//	Name
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtName);
    		edt.setText(compInf.computerName);
    		
    		for(ZzzSvcClient.ZzzNetIfInf netInf : compInf.netinfs) {
    			reViewResolveCallback(netInf);
    		}
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "reViewResolveCallback end");}
		}
		
	}
	/**
	 * @param netInf
	 */
	public void reViewResolveCallback(ZzzSvcClient.ZzzNetIfInf netInf ) {
		EditText edt;
		byte bytearry[];
		
		try {
			//
    		//	Name
    		//
    		edt = (EditText)mRootView.findViewById(R.id.edtName);
    		edt.setText(netInf.name);
    		
    		//
    		//	IP Address
    		//
    		bytearry = ZzzSvcClient.toByte(netInf.ipaddress);
			StringBuilder sb = new StringBuilder();
			bytearry = ZzzSvcClient.toByte(netInf.ipaddress);
			sb.append(ZzzSvcClient.getByte2Int(bytearry[3]));
			sb.append(".");
			sb.append(ZzzSvcClient.getByte2Int(bytearry[2]));
			sb.append(".");
			sb.append(ZzzSvcClient.getByte2Int(bytearry[1]));
			sb.append(".");
			sb.append(ZzzSvcClient.getByte2Int(bytearry[0]));
			edt = (EditText)mRootView.findViewById(R.id.edtIPAddress);
			edt.setText(sb.toString());
			//
    		//	Broad Cast Address
    		//
			bytearry = ZzzSvcClient.toByte(netInf.subnetmask);
    		edt = (EditText)mRootView.findViewById(R.id.edtIP1);
    		edt.setText("" + ZzzSvcClient.getByte2Int(bytearry[3]));
    		edt = (EditText)mRootView.findViewById(R.id.edtIP2);
    		edt.setText("" + ZzzSvcClient.getByte2Int(bytearry[2]));
    		edt = (EditText)mRootView.findViewById(R.id.edtIP3);
    		edt.setText("" + ZzzSvcClient.getByte2Int(bytearry[1]));
    		edt = (EditText)mRootView.findViewById(R.id.edtIP4);
    		edt.setText("255");// + ZzzSvcClient.getByte2Int(bytearry[0]));
    		
    		//
    		//	MAC Address
    		//
    		int mac_ids[] = {R.id.edtMAC1, R.id.edtMAC2, R.id.edtMAC3, R.id.edtMAC4, R.id.edtMAC5, R.id.edtMAC6};
    		if( netInf.physicalAddr != null ) {
    			for(int j = 0; j < netInf.physicalAddr.length; j++ ) {
    				String strTmp = "" + Integer.toHexString( ZzzSvcClient.getByte2Int(netInf.physicalAddr[j])  );
    				if( strTmp.length() == 1 ) {
    					strTmp = "0" + Integer.toHexString( ZzzSvcClient.getByte2Int(netInf.physicalAddr[j])  );
    				}
    				edt = (EditText)mRootView.findViewById(mac_ids[j]);
    				edt.setText(strTmp);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 非同期でZzzServerを問い合わせる
	 */
	class AsyncResolve extends AsyncTask<String, Integer, Integer>{
		private Activity			mActivity;
    	private ProgressDialog 		mProgressDialog;
    	private ComputerFragment	mFragment;
    	private String				mHostName;
    	private int					mPort;
    	private Exception			mException = null;
    	private ZzzSvcClient.ZzzCompInf	mCompInf = null;
    	
    	
		public AsyncResolve(Activity activity, ComputerFragment fragment, String strHost, int port) {
			mActivity = activity;
			mFragment = fragment;
			mHostName = strHost;
			mPort = port;
		}
    	@Override
    	protected void onPreExecute() {
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "AsyncResolve.onPreExecute start");	}
			mProgressDialog = new ProgressDialog(mActivity);
			mProgressDialog.setTitle(mActivity.getString(R.string.app_name));
			mProgressDialog.setIcon(R.drawable.icon);
			mProgressDialog.setMessage( "Loading..." );

			mProgressDialog.show();
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "AsyncResolve.onPreExecute end");}
    	}
    	@Override
    	protected Integer doInBackground(String... arg0) {
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "AsyncResolve.doInBackground start");}
    		
    		try {
    			ZzzSvcClient client = new ZzzSvcClient();
    			if(MyLog.isDebugMod()) {
    				MyLog.logf(TAG, " Host Name:" + mHostName);
    				MyLog.logf(TAG, " Port     :" + mPort);
    			}
    			mCompInf = client.tellZzzSvcTCP(mHostName, mPort);
    			
    			
    		} catch(Exception e) {
    			mException = e;
    		}    		
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "AsyncResolve.doInBackground end");}	
    		return 0;
    	}
    	@Override
    	protected void onPostExecute(Integer retval) {
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "AsyncResolve.onPostExecute start");}
    		try {
	    		mProgressDialog.dismiss();
	    		
	    		if( mCompInf != null ) {
	    			mFragment.reViewResolveCallback(mCompInf);
	    		}
    		} catch(Exception e) {
    			
    		}
    		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "AsyncResolve.onPostExecute end");}
    	}
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(seekBar.getProgress()<5){
			seekBar.setProgress(0);
		}else{
			seekBar.setProgress(10);
		}
	}
	
	
	
	/**
	 * コンピューター選択ダイアログ
	 */
	public static class SelectCompDialogFragment extends DialogFragment {
		private ZzzSvcClient.ZzzCompInf mCompInf;
		private Spinner					mSpinnerComputer;
		private ComputerFragment		mParentFragment;
		
		static SelectCompDialogFragment newInstance(ComputerFragment parentFragment, ZzzSvcClient.ZzzCompInf compInf) {
			SelectCompDialogFragment f = new SelectCompDialogFragment();
			f.mParentFragment = parentFragment;
			f.mCompInf = compInf;
			return f;
		}
		
		
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);//R.style.MyLightTheme);
        }
        
        @Override  
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	LayoutInflater inflater = getActivity().getLayoutInflater();  
        	View v = inflater.inflate(R.layout.dialog_select_computer, null, false);
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*, R.style.MyLightTheme*/);  
        	builder.setTitle(R.string.select_computer);
        	builder.setPositiveButton("OK", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						onOkBtnCliced();
						dlg.dismiss();
					}
        	});
        	builder.setNegativeButton("Cancel",  
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dlg, int arg1) {
						// TODO Auto-generated method stub
						dlg.dismiss();
					}
        	});
        	builder.setIcon(R.drawable.wol);
        	builder.setView(v);  
        	
        	mSpinnerComputer = (Spinner)v.findViewById(R.id.spinnerSelectComputer);


        	ListAdapter adapter = new ListAdapter(inflater, this.getActivity(), mCompInf.netinfs);
        	mSpinnerComputer.setAdapter(adapter);
        	adapter.setDropDownViewResource(R.layout.computer_list_row);
        	mSpinnerComputer.setPrompt("Select Computer");
        	mSpinnerComputer.setSelection( 0 );
        	
        	return builder.create();
        }
        
        /**
         * OKボタンをクリックしたときの処理
         */
        private void onOkBtnCliced() {
        	ZzzSvcClient.ZzzNetIfInf netInf = mCompInf.netinfs.get(mSpinnerComputer.getSelectedItemPosition());
        	mParentFragment.reViewResolveCallback(netInf);
        }

    	/**
    	 * コンピューターリストアダプター
    	 */
    	private class ListAdapter extends ArrayAdapter<ZzzSvcClient.ZzzNetIfInf> {
    		private LayoutInflater 	mInflater;
    		
    		public ListAdapter(LayoutInflater inflater, Context context, List<ZzzSvcClient.ZzzNetIfInf> objects) {
    			super(context, R.layout.computer_list_row, R.id.txtName, objects);
        		mInflater = inflater;
    		}
    		
    		@Override
    		public View getDropDownView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.computer_list_row, null, false);
    			}
    			ZzzSvcClient.ZzzNetIfInf comp = this.getItem(position);
    			this.setWiggetContents(view, comp);    			
    			return view;
    		}
    		
    		@Override
    		public View getView(int position, View convertView, ViewGroup parent) {
    			View view = convertView;
    			if (convertView == null) {
    				view =  mInflater.inflate(R.layout.computer_list_row, null, false);
    			}
    			ZzzSvcClient.ZzzNetIfInf comp = this.getItem(position);
    			this.setWiggetContents(view, comp);
    			return view;
    		}
    		
    		private void setWiggetContents(View view, ZzzSvcClient.ZzzNetIfInf netInf) {
    			byte bytearry[];
    			TextView  txtView = null;
    			txtView = (TextView)view.findViewById(R.id.txtName);
    			txtView.setText( netInf.name );
    			try {
	    			StringBuilder sb = new StringBuilder();
	    			sb.append("IPAddress:");
	    			bytearry = ZzzSvcClient.toByte(netInf.ipaddress);
	    			sb.append(ZzzSvcClient.getByte2Int(bytearry[3]));
	    			sb.append(".");
	    			sb.append(ZzzSvcClient.getByte2Int(bytearry[2]));
	    			sb.append(".");
	    			sb.append(ZzzSvcClient.getByte2Int(bytearry[1]));
	    			sb.append(".");
	    			sb.append(ZzzSvcClient.getByte2Int(bytearry[0]));
	    			
	    			sb.append("\nMACAddress:");
	        		if( netInf.physicalAddr != null ) {
	        			for(int j = 0; j < netInf.physicalAddr.length; j++ ) {
	        				String strTmp = "" + Integer.toHexString( ZzzSvcClient.getByte2Int(netInf.physicalAddr[j])  );
	        				if( strTmp.length() == 1 ) {
	        					strTmp = "0" + Integer.toHexString( ZzzSvcClient.getByte2Int(netInf.physicalAddr[j])  );
	        				}
	        				if( j != 0 ) {
	        					sb.append(":");
	        				}
	        				sb.append(strTmp);
	        			}
	        		}
	    			txtView = (TextView)view.findViewById(R.id.textOtherInfo);
	    			txtView.setText( sb.toString() );

    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
	}
	
	
}