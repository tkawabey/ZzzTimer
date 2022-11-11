package four.non.bronds.yyys.zzztimer.widgets;

import four.non.bronds.yyys.zzztimer.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;



public class OnOffBotton extends FrameLayout implements OnSeekBarChangeListener{



	private FrameLayout imgPan;
	private LinearLayout background;
	private LinearLayout featurePan;
	private LinearLayout infoPan;
	private ImageView imgViewIcon;
	private TextView nameDevices;
	private TextView state;
	private SeekBar seekBarOnOff;
	private int		mOnIcon;
	private int		mOffIcon;
	private String state_progress;
	private Handler handler;
	private boolean	mVal;

	public interface OnChangeListener {
		void onItemChage(boolean val);
	}
	OnChangeListener	mOncahge = null;

	public void setOnChangeListener(OnChangeListener listener)
	{
		mOncahge = listener;
	}

	
	public boolean isVal() {
		return mVal;
	}
	public void setVal(boolean val) {
		mVal = val;
		
		imgViewIcon.setImageResource(mVal ? mOnIcon : mOffIcon);
		
		final Message msg = Message.obtain();
		msg.what = !mVal ? 0 : 1;
		handler.sendMessage(msg);
	}



	public OnOffBotton(Context context, String name, int onIcon, int offIcon, boolean initVal) {
		super(context);

		this.mOnIcon = onIcon;
		this.mOffIcon = offIcon;
		this.mVal = initVal;
		
        //graphic agent

		//panel with border
		background = new LinearLayout(context);
		background.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		background.setBackgroundResource(R.drawable.backwidget_white);


		//panel to set img with padding left
		imgPan = new FrameLayout(context);
		imgPan.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
		imgPan.setPadding(15, 8, 10, 10);
		//img
		imgViewIcon = new ImageView(context);
//		img.setBackgroundResource(R.drawable.screen_on);
		imgViewIcon.setImageResource(mVal ? mOnIcon : mOffIcon);
		imgViewIcon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,Gravity.CENTER));
//		img.setBackgroundResource(gAgent.Icones_Agent(usage, 0));


		// info panel
		infoPan = new LinearLayout(context);
		infoPan.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
		infoPan.setOrientation(LinearLayout.VERTICAL);
		infoPan.setGravity(Gravity.CENTER_VERTICAL);

		//name of devices
		nameDevices=new TextView(context);
		nameDevices.setText(name);
		nameDevices.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		nameDevices.setTextColor(Color.BLACK);
		nameDevices.setTextSize(16);
		//state
		state=new TextView(context);
		state.setTextColor(Color.BLACK);
		state.setText("State : ");


		//feature panel
		featurePan=new LinearLayout(context);
		featurePan.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		featurePan.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		featurePan.setPadding(0, 0, 15, 0);

		//first seekbar on/off
		seekBarOnOff=new SeekBar(context);
		seekBarOnOff.setProgress(!mVal ? 0 : 10);
		seekBarOnOff.setMax(10);
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.bgseekbaronoff);
		seekBarOnOff.setLayoutParams(new LayoutParams(bMap.getWidth(),bMap.getHeight()));
		seekBarOnOff.setProgressDrawable(getResources().getDrawable(R.drawable.bgseekbaronoff));
		seekBarOnOff.setThumb(getResources().getDrawable(R.drawable.buttonseekbar));
		seekBarOnOff.setThumbOffset(-6);
		seekBarOnOff.setOnSeekBarChangeListener(this);
		seekBarOnOff.setTag("0");

		featurePan.addView(seekBarOnOff);
		infoPan.addView(nameDevices);
		infoPan.addView(state);
		imgPan.addView(imgViewIcon);
		background.addView(imgPan);
		background.addView(infoPan);
		background.addView(featurePan);

		this.addView(background);


		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0){
					seekBarOnOff.setProgress(0);
				}else if(msg.what == 1){
					seekBarOnOff.setProgress(10);
				}else if(msg.what == 2){
					Toast.makeText(getContext(), "Command Failed", Toast.LENGTH_SHORT).show();
				}
			}	
		};
	}



	@Override
	public void onProgressChanged(SeekBar seekBarOnOff,int progress,boolean fromTouch) {
		switch(progress) {
		case 0:
			state_progress = "Off";
			state.setText("State : "+"Off");
			imgViewIcon.setImageResource(mOffIcon);
			
			mVal = false;
			break;
		case 10:
			state_progress = "On";
			state.setText("State : "+"On");
			imgViewIcon.setImageResource(mOnIcon);
			
			mVal = true;
			break;
		}
		
		if( mOncahge != null ) {
			mOncahge.onItemChage(mVal);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		if(arg0.getProgress()<5){
			arg0.setProgress(0);
		}else{
			arg0.setProgress(10);
		}
		
	}
}
