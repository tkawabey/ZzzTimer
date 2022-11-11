package four.non.bronds.yyys.zzztimer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingBean;
import four.non.bronds.yyys.zzztimer.bean.TimerSettingStartApp;
import four.non.bronds.yyys.zzztimer.cmn.Constant;
import four.non.bronds.yyys.zzztimer.util.MyLog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


class PkgItem {
	String		mAppName;
	String		mPkgName;
	Drawable 	mIcon;
	boolean		mCheck;
	CheckBox	mCheckBox;
}


public class SelAppActivity extends Activity {
	final static String TAG = "SelAppActivity";
	private ListView			mListView = null;
	private TextView			mTxtTitle = null;
	private TimerSettingBean 	mItem = null; 
	private int					mMode = Constant.SEL_APP_MODE_KILL;
	List<PkgItem>	mPkgItemList = new ArrayList<PkgItem>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sel_app);
        
        try {
        	

            Intent intent = this.getIntent();
            mItem = (TimerSettingBean)intent.getSerializableExtra(Constant.INTENT_TAG_TEIMER_ITEM);
            if( mItem == null ) {
            	throw new NullPointerException("inner error");
            }
            mMode = intent.getIntExtra(Constant.INTENT_TAG_SEL_APP_MODE, -1);
            if( mMode == -1 ) {
            	throw new NullPointerException("inner error");
            }
	        // hold UI Controls
			mListView = (ListView) findViewById(R.id.listView);
			if (mListView == null) {
				throw new Exception("List View");
			}
			mListView.setCacheColorHint(Color.TRANSPARENT); 
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				}
			});
			mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
					
//					SelAppActivity.this.listLongTouch(parent, view, position, id);
					
					
					return true;
				}
			} );
			mTxtTitle = (TextView)findViewById(R.id.textTitle);
			if(mMode == Constant.SEL_APP_MODE_KILL ) {
				mTxtTitle.setText( this.getString(R.string.select_kill_apps) );
			}
			if(mMode == Constant.SEL_APP_MODE_STAT ) {
				mTxtTitle.setText( this.getString(R.string.select_start_apps) );
			}
			
			
			
//			// フォーカスが当たらないよう設定  
//			mListView.setItemsCanFocus(false);  
//		  
//		    // 選択の方式の設定  
//			mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
		   
			
			
			((Button)findViewById(R.id.apply)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SelAppActivity.this.onApply();
				}
			} );
			

			AsyncLoader task = new AsyncLoader(this);
			task.execute("");
			
			
			
			
        }  catch(Exception e) {

    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon)
			.setTitle(Constant.APP_NAME)
			.setMessage(strErr)
			.setPositiveButton("OK", null)
			.show();
        }
    }

    
    
    int mSelectItem = 0;
    
    private boolean listLongTouch(AdapterView<?> parent, View view, int position, long id) {
    	String []strSelectItem = new String[2] ;
    	strSelectItem[0] = "Run";
    	strSelectItem[1] = "Delete";
    	
    	mSelectItem = 0;
    	

    	final PkgItem item = mPkgItemList.get(position);
    	
    	new AlertDialog.Builder(this)
    	.setTitle(Constant.APP_NAME)
    	.setSingleChoiceItems(strSelectItem, 0, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				mSelectItem = which;
			}
    	})
    	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if( mSelectItem == 0 ) {
					run3PartyApp(item.mPkgName);
				} else
				if( mSelectItem == 1 ) {
					

				}
			}
    	})
    	.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
    		
    	})
		.show();
    	
    	return true;
    }
    
    
    private void run3PartyApp( String strPackageName ) 
    {
    	try {
    		
    		Log.d(TAG, "run3PartyApp : " + strPackageName);
    		
    		Context foreignContext = createPackageContext(strPackageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
//    		Class<?> yourClass = foreignContext.getClassLoader().loadClass("com.yourdomain.yourapp.YourClass");

    		
    	} catch(Exception e ) {
    		e.printStackTrace();
    	}
    	
    	
    }
    
    
    /**
     * 
     */
    private void onApply() {
    	int cnt = 0, i;
    	try {

    		if(mMode == Constant.SEL_APP_MODE_KILL ) {
    			mItem.getListCloseApp().clear();
    		} else
   			if(mMode == Constant.SEL_APP_MODE_STAT ) {
   				mItem.getListStartApp().clear();
   			}
    		cnt = mPkgItemList.size();
    		for(i = 0; i < cnt; i++ ) {
    			PkgItem item = mPkgItemList.get(i);
    			if( item.mCheckBox == null ) {
    			} else {
        			if( item.mCheck ) {
        				if(mMode == Constant.SEL_APP_MODE_KILL ) {
        					mItem.addClosseApp( item.mPkgName );
        	    		} else
    	       			if(mMode == Constant.SEL_APP_MODE_STAT ) {
    	       				mItem.addStartApp( item.mPkgName );
    	       			}
        			}
    			}
    		}
    		
    		if(mMode == Constant.SEL_APP_MODE_STAT ) {
	    		new AlertDialog.Builder(this)
	    		.setIcon(R.drawable.icon)
	    		.setTitle( this.getString(R.string.app_name) )
	    		.setMessage(  this.getString(R.string.are_you_add_kill_app_on_start_app) )
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	SelAppActivity.this.onApplyStartApp(true);
				    }
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	SelAppActivity.this.onApplyStartApp(false);
				    }
				})
				.show();
    		} else {
    			Intent intent = new Intent();
    			intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mItem);
    			if(mMode == Constant.SEL_APP_MODE_KILL ) {
    				this.setResult(Constant.RQ_CODE_KILL_APP, intent);
    			}
    			if(mMode == Constant.SEL_APP_MODE_STAT ) {
    				this.setResult(Constant.RQ_CODE_STARTL_APP, intent);
    			}
    			this.finish();
    		}
    		
    		

    	} catch(Exception e) {

    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon)
			.setTitle(Constant.APP_NAME)
			.setMessage(strErr)
			.setPositiveButton("OK", null)
			.show();
        }
    }
    
    /**
     * @param addKillApp
     */
    private void onApplyStartApp(boolean addKillApp)
    {
    	if( addKillApp == true ) {
    		for(TimerSettingStartApp ite : mItem.getListStartApp() ) {
    			mItem.addClosseApp(ite.getmAppName());
    			
    		}
    	}
		Intent intent = new Intent();
		intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mItem);
		this.setResult(Constant.RQ_CODE_STARTL_APP, intent);
		this.finish();
    }
    
    /**
     * リストを更新。
     */
    public void reView() {

		// フォーカスが当たらないよう設定  
		mListView.setItemsCanFocus(false);  
	  
	    // 選択の方式の設定  
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
		
		PkgItemAdapter adapter = new PkgItemAdapter(this, mPkgItemList);
		mListView.setAdapter(adapter);
    }
    
    /**
     * アプリケーション情報をロード
     */
    public void loadAppInfo() {
    	try {
			PackageManager pm = this.getPackageManager();
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
			
	
			Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));
			int count = appList.size();
			for(int i = 0; i < count; i++ ) {
				ResolveInfo rinf = appList.get(i);
				
				ActivityInfo atvInf = rinf.activityInfo;
				ApplicationInfo appInf = atvInf.applicationInfo;
	
	//			Log.i("Main", "packageName:" + appInf.packageName);
	//			Log.i("    ", "className:" + appInf.className);
	//			Log.i("    ", "manageSpaceActivityName:" + appInf.manageSpaceActivityName);
	//			Log.i("    ", "name:" + appInf.name);
	//			Log.i("    ", "permission:" + appInf.permission);
	//			Log.i("    ", "processName:" + appInf.processName);
	//			Log.i("    ", "loadLabel:" + atvInf.loadLabel(pm));
	
	
				PkgItem pkgItem = new PkgItem();
				pkgItem.mAppName = atvInf.loadLabel(pm).toString();
				pkgItem.mIcon = resizeIcon( atvInf.loadIcon(pm) );
				pkgItem.mPkgName = appInf.packageName;
				
				if(mMode == Constant.SEL_APP_MODE_KILL ) {
					if( mItem.isExistKillApp( pkgItem.mPkgName ) ) {
						pkgItem.mCheck = true;
					}
				} else
				if(mMode == Constant.SEL_APP_MODE_STAT ) {
					if( mItem.isExistStartApp( pkgItem.mPkgName ) ) {
						pkgItem.mCheck = true;
					}
				}
				
				mPkgItemList.add(pkgItem);
				
			}
		
        }  catch(Exception e) {

    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
    		showAlert( strErr );
        }
    }
    
    
    
    /**
     * アイコンサイズの変更
     * @param icon
     * @return
     */
    private Drawable resizeIcon(Drawable icon) {
        //標準アイコンサイズの取得
        Resources res=getResources();
        int width =(int)res.getDimension(android.R.dimen.app_icon_size);
        int height=(int)res.getDimension(android.R.dimen.app_icon_size);
        
        //現在のアイコンサイズの取得
        int iconWidth =icon.getIntrinsicWidth();
        int iconHeight=icon.getIntrinsicHeight();

        //アイコンサイズの変更
        if (width>0 && height>0 && 
            (width<iconWidth || height<iconHeight)) {
            
            //変換後のアイコンサイズの計算
            float ratio=(float)iconWidth/(float)iconHeight;
            if (iconWidth>iconHeight) {
                height=(int)(width/ratio);
            } else if (iconHeight>iconWidth) {
                width=(int)(height*ratio);
            }

            //動的キャンバスの生成
            Bitmap.Config c=(icon.getOpacity()!=PixelFormat.OPAQUE)?
                Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;
            Bitmap thumb=Bitmap.createBitmap(width,height,c);
            Canvas canvas=new Canvas(thumb);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,0));

            //動的キャンバスへのアイコン描画
            Rect oldBounds=new Rect();
            oldBounds.set(icon.getBounds());
            icon.setBounds(0,0,width,height);
            icon.draw(canvas);
            icon.setBounds(oldBounds);
            
            //キャンバスをDrawableオブジェクトに変換
            icon=new BitmapDrawable(thumb);
        }        
        return icon;
    }
    
    
    /**
     * アプリケーション一覧ローダー 
     * 
     * @author user
     */
    class AsyncLoader extends AsyncTask<String, Integer, Integer>{
    	private SelAppActivity mActivity;
    	private ProgressDialog 		mProgressDialog;
    	
    	AsyncLoader(SelAppActivity activity) {
    		mActivity = activity;
    	}
    	@Override
    	protected void onPreExecute() {
			// Show Progress Dialog
			mProgressDialog = new ProgressDialog(mActivity);
			mProgressDialog.setTitle(mActivity.getString(R.string.app_name));
			mProgressDialog.setIcon(R.drawable.icon);
			mProgressDialog.setMessage( "Loading..." );

			mProgressDialog.show();
    	}
    	@Override
    	protected Integer doInBackground(String... arg0) {
    		mActivity.loadAppInfo();
    		return 0;
    	}
    	@Override
    	protected void onPostExecute(Integer retval) {
    		try {
	    		mProgressDialog.dismiss();
	    		
	    		mActivity.reView();
    		} catch(Exception e) {
    			
    		}
    	}
    }
    /**
     * リストビューのアダプター
     * @author user
     *
     */
    private class PkgItemAdapter extends ArrayAdapter<PkgItem> {
    	private LayoutInflater 	mInflater;
    	
    	public PkgItemAdapter(Context context, List<PkgItem> objects) {
    		super(context, 0, objects);
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		
    		
    	}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.sel_app_item_row, null);
			}
			PkgItem pkgItem = this.getItem(position);

			ImageView imgView = null;
			CheckBox  chkBox = null;
			TextView  txtView = null;
			
			chkBox  = (CheckBox)view.findViewById(R.id.chk_package);
			imgView = (ImageView)view.findViewById(R.id.image_package);
			txtView = (TextView)view.findViewById(R.id.txt_package);
			// Text	@+id/txt_package
			
			chkBox.setText("");
			txtView.setText( pkgItem.mAppName );
			chkBox.setTag(pkgItem);
			
			imgView.setImageDrawable(pkgItem.mIcon);
			
/*			chkBox.setOnCheckedChangeListener( new android.widget.CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton btn, boolean val) {
					for(PkgItem item : SelectPackageActivity.this.mPkgItemList ) {
						if( item.mCheckBox == btn ) {
							MyLog.logi("", "FIND : " + item.mAppName);
							item.mCheck = val;
							return ;
						} 
					}
				}					
			});
			*/
			chkBox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					PkgItem pkgItem = (PkgItem)v.getTag();
					if( pkgItem != null ) {
						CheckBox chk = (CheckBox)v;
						pkgItem.mCheck = chk.isChecked();
					}
				}
			});
			chkBox.setChecked( pkgItem.mCheck );
			
			pkgItem.mCheckBox = chkBox;
			
			return view;
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

class SelectAppFragment extends MyBaseFragment
{
	final static String TAG = "ComputerOpeFragment";
	private ListView			mListView = null;
	private TextView			mTxtTitle = null;
	private TimerSettingBean 	mItem = null; 
	private int					mMode = Constant.SEL_APP_MODE_KILL;
	List<PkgItem>	mPkgItemList = new ArrayList<PkgItem>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView start");}
		try {
			if (container == null) {
				return null;
			}
			
			mActivity = this.getActivity();
			mRootView = inflater.inflate(R.layout.sel_app, container, false);
			
            if( mItem == null ) {
            	throw new NullPointerException("inner error");
            }
            if( mMode == -1 ) {
            	throw new NullPointerException("inner error");
            }
			

	        // hold UI Controls
			mListView = (ListView)mActivity.findViewById(R.id.listView);
			if (mListView == null) {
				throw new Exception("List View");
			}
			mListView.setCacheColorHint(Color.TRANSPARENT); 
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				}
			});
			mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
					
//					SelAppActivity.this.listLongTouch(parent, view, position, id);
					
					
					return true;
				}
			} );
			mTxtTitle = (TextView)mActivity.findViewById(R.id.textTitle);
			if(mMode == Constant.SEL_APP_MODE_KILL ) {
				mTxtTitle.setText( this.getString(R.string.select_kill_apps) );
			}
			if(mMode == Constant.SEL_APP_MODE_STAT ) {
				mTxtTitle.setText( this.getString(R.string.select_start_apps) );
			}
			
			
			
//			// フォーカスが当たらないよう設定  
//			mListView.setItemsCanFocus(false);  
//		  
//		    // 選択の方式の設定  
//			mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
		   
			
			
			((Button)mActivity.findViewById(R.id.apply)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SelectAppFragment.this.onApply();
				}
			} );
			

			AsyncLoader task = new AsyncLoader(this);
			task.execute("");
			// showAlert
		}  catch(Exception e) {

    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
    		showAlert(strErr);
		} finally {
			if(MyLog.isDebugMod()) {MyLog.logf(TAG, "onCreateView end");}
		}		
		return mRootView;
	}
	
	

    
    
    int mSelectItem = 0;
    
    private boolean listLongTouch(AdapterView<?> parent, View view, int position, long id) {
    	String []strSelectItem = new String[2] ;
    	strSelectItem[0] = "Run";
    	strSelectItem[1] = "Delete";
    	
    	mSelectItem = 0;
    	

    	final PkgItem item = mPkgItemList.get(position);
    	
    	new AlertDialog.Builder(mActivity)
    	.setTitle(Constant.APP_NAME)
    	.setSingleChoiceItems(strSelectItem, 0, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				mSelectItem = which;
			}
    	})
    	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if( mSelectItem == 0 ) {
					run3PartyApp(item.mPkgName);
				} else
				if( mSelectItem == 1 ) {
					

				}
			}
    	})
    	.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
    		
    	})
		.show();
    	
    	return true;
    }
    
    
    private void run3PartyApp( String strPackageName ) 
    {
    	try {
    		
    		Log.d(TAG, "run3PartyApp : " + strPackageName);
    		
    		Context foreignContext = mActivity.createPackageContext(strPackageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
//    		Class<?> yourClass = foreignContext.getClassLoader().loadClass("com.yourdomain.yourapp.YourClass");

    		
    	} catch(Exception e ) {
    		e.printStackTrace();
    	}
    	
    	
    }
    
    
    /**
     * 
     */
    private void onApply() {
    	int cnt = 0, i;
    	try {

    		if(mMode == Constant.SEL_APP_MODE_KILL ) {
    			mItem.getListCloseApp().clear();
    		} else
   			if(mMode == Constant.SEL_APP_MODE_STAT ) {
   				mItem.getListStartApp().clear();
   			}
    		cnt = mPkgItemList.size();
    		for(i = 0; i < cnt; i++ ) {
    			PkgItem item = mPkgItemList.get(i);
    			if( item.mCheckBox == null ) {
    			} else {
        			if( item.mCheck ) {
        				if(mMode == Constant.SEL_APP_MODE_KILL ) {
        					mItem.addClosseApp( item.mPkgName );
        	    		} else
    	       			if(mMode == Constant.SEL_APP_MODE_STAT ) {
    	       				mItem.addStartApp( item.mPkgName );
    	       			}
        			}
    			}
    		}
    		
    		if(mMode == Constant.SEL_APP_MODE_STAT ) {
	    		new AlertDialog.Builder(mActivity)
	    		.setIcon(R.drawable.icon)
	    		.setTitle( this.getString(R.string.app_name) )
	    		.setMessage(  this.getString(R.string.are_you_add_kill_app_on_start_app) )
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	SelectAppFragment.this.onApplyStartApp(true);
				    }
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	SelectAppFragment.this.onApplyStartApp(false);
				    }
				})
				.show();
    		} else {
    			Intent intent = new Intent();
    			intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mItem);
    			if(mMode == Constant.SEL_APP_MODE_KILL ) {
    				mActivity.setResult(Constant.RQ_CODE_KILL_APP, intent);
    			}
    			if(mMode == Constant.SEL_APP_MODE_STAT ) {
    				mActivity.setResult(Constant.RQ_CODE_STARTL_APP, intent);
    			}
    			mActivity.finish();
    		}
    		
    		

    	} catch(Exception e) {

    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
			new AlertDialog.Builder(mActivity)
			.setIcon(R.drawable.icon)
			.setTitle(Constant.APP_NAME)
			.setMessage(strErr)
			.setPositiveButton("OK", null)
			.show();
        }
    }
    
    /**
     * @param addKillApp
     */
    private void onApplyStartApp(boolean addKillApp)
    {
    	if( addKillApp == true ) {
    		for(TimerSettingStartApp ite : mItem.getListStartApp() ) {
    			mItem.addClosseApp(ite.getmAppName());
    			
    		}
    	}
		Intent intent = new Intent();
		intent.putExtra(Constant.INTENT_TAG_TEIMER_ITEM, mItem);
		mActivity.setResult(Constant.RQ_CODE_STARTL_APP, intent);
		mActivity.finish();
    }
    
    /**
     * リストを更新。
     */
    public void reView() {

		// フォーカスが当たらないよう設定  
		mListView.setItemsCanFocus(false);  
	  
	    // 選択の方式の設定  
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
		
		PkgItemAdapter adapter = new PkgItemAdapter(mActivity, mPkgItemList);
		mListView.setAdapter(adapter);
    }
    
    /**
     * アプリケーション情報をロード
     */
    public void loadAppInfo() {
    	try {
			PackageManager pm = mActivity.getPackageManager();
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
			
	
			Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));
			int count = appList.size();
			for(int i = 0; i < count; i++ ) {
				ResolveInfo rinf = appList.get(i);
				
				ActivityInfo atvInf = rinf.activityInfo;
				ApplicationInfo appInf = atvInf.applicationInfo;
	
	//			Log.i("Main", "packageName:" + appInf.packageName);
	//			Log.i("    ", "className:" + appInf.className);
	//			Log.i("    ", "manageSpaceActivityName:" + appInf.manageSpaceActivityName);
	//			Log.i("    ", "name:" + appInf.name);
	//			Log.i("    ", "permission:" + appInf.permission);
	//			Log.i("    ", "processName:" + appInf.processName);
	//			Log.i("    ", "loadLabel:" + atvInf.loadLabel(pm));
	
	
				PkgItem pkgItem = new PkgItem();
				pkgItem.mAppName = atvInf.loadLabel(pm).toString();
				pkgItem.mIcon = resizeIcon( atvInf.loadIcon(pm) );
				pkgItem.mPkgName = appInf.packageName;
				
				if(mMode == Constant.SEL_APP_MODE_KILL ) {
					if( mItem.isExistKillApp( pkgItem.mPkgName ) ) {
						pkgItem.mCheck = true;
					}
				} else
				if(mMode == Constant.SEL_APP_MODE_STAT ) {
					if( mItem.isExistStartApp( pkgItem.mPkgName ) ) {
						pkgItem.mCheck = true;
					}
				}
				
				mPkgItemList.add(pkgItem);
				
			}
		
        }  catch(Exception e) {

    		String strErr = e.getMessage();
    		if( strErr == null ) {
    			strErr = "unknown error!";
    		}
    		showAlert( strErr );
        }
    }
    
    
    
    /**
     * アイコンサイズの変更
     * @param icon
     * @return
     */
    private Drawable resizeIcon(Drawable icon) {
        //標準アイコンサイズの取得
        Resources res=getResources();
        int width =(int)res.getDimension(android.R.dimen.app_icon_size);
        int height=(int)res.getDimension(android.R.dimen.app_icon_size);
        
        //現在のアイコンサイズの取得
        int iconWidth =icon.getIntrinsicWidth();
        int iconHeight=icon.getIntrinsicHeight();

        //アイコンサイズの変更
        if (width>0 && height>0 && 
            (width<iconWidth || height<iconHeight)) {
            
            //変換後のアイコンサイズの計算
            float ratio=(float)iconWidth/(float)iconHeight;
            if (iconWidth>iconHeight) {
                height=(int)(width/ratio);
            } else if (iconHeight>iconWidth) {
                width=(int)(height*ratio);
            }

            //動的キャンバスの生成
            Bitmap.Config c=(icon.getOpacity()!=PixelFormat.OPAQUE)?
                Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;
            Bitmap thumb=Bitmap.createBitmap(width,height,c);
            Canvas canvas=new Canvas(thumb);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,0));

            //動的キャンバスへのアイコン描画
            Rect oldBounds=new Rect();
            oldBounds.set(icon.getBounds());
            icon.setBounds(0,0,width,height);
            icon.draw(canvas);
            icon.setBounds(oldBounds);
            
            //キャンバスをDrawableオブジェクトに変換
            icon=new BitmapDrawable(thumb);
        }        
        return icon;
    }
    
    
	

    
    /**
     * アプリケーション一覧ローダー 
     * 
     * @author user
     */
    class AsyncLoader extends AsyncTask<String, Integer, Integer>{
    	private SelectAppFragment	mFragment;
    	private ProgressDialog 		mProgressDialog;
    	
    	AsyncLoader(SelectAppFragment fragment) {
    		mFragment = fragment;
    	}
    	@Override
    	protected void onPreExecute() {
			// Show Progress Dialog
			mProgressDialog = new ProgressDialog(mActivity);
			mProgressDialog.setTitle(mActivity.getString(R.string.app_name));
			mProgressDialog.setIcon(R.drawable.icon);
			mProgressDialog.setMessage( "Loading..." );

			mProgressDialog.show();
    	}
    	@Override
    	protected Integer doInBackground(String... arg0) {
    		mFragment.loadAppInfo();
    		return 0;
    	}
    	@Override
    	protected void onPostExecute(Integer retval) {
    		try {
	    		mProgressDialog.dismiss();
	    		
	    		mFragment.reView();
    		} catch(Exception e) {
    			
    		}
    	}
    }
    /**
     * リストビューのアダプター
     * @author user
     *
     */
    private class PkgItemAdapter extends ArrayAdapter<PkgItem> {
    	private LayoutInflater 	mInflater;
    	
    	public PkgItemAdapter(Context context, List<PkgItem> objects) {
    		super(context, 0, objects);
    		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		
    		
    	}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view =  mInflater.inflate(R.layout.sel_app_item_row, null);
			}
			PkgItem pkgItem = this.getItem(position);

			ImageView imgView = null;
			CheckBox  chkBox = null;
			TextView  txtView = null;
			
			chkBox  = (CheckBox)view.findViewById(R.id.chk_package);
			imgView = (ImageView)view.findViewById(R.id.image_package);
			txtView = (TextView)view.findViewById(R.id.txt_package);
			// Text	@+id/txt_package
			
			chkBox.setText("");
			txtView.setText( pkgItem.mAppName );
			chkBox.setTag(pkgItem);
			
			imgView.setImageDrawable(pkgItem.mIcon);
			
/*			chkBox.setOnCheckedChangeListener( new android.widget.CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton btn, boolean val) {
					for(PkgItem item : SelectPackageActivity.this.mPkgItemList ) {
						if( item.mCheckBox == btn ) {
							MyLog.logi("", "FIND : " + item.mAppName);
							item.mCheck = val;
							return ;
						} 
					}
				}					
			});
			*/
			chkBox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					PkgItem pkgItem = (PkgItem)v.getTag();
					if( pkgItem != null ) {
						CheckBox chk = (CheckBox)v;
						pkgItem.mCheck = chk.isChecked();
					}
				}
			});
			chkBox.setChecked( pkgItem.mCheck );
			
			pkgItem.mCheckBox = chkBox;
			
			return view;
		}
    }
	
}


