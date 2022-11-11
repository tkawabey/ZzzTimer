package four.non.bronds.yyys.zzztimer.widgets;

import four.non.bronds.yyys.zzztimer.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class OnOffSimpleBtn extends SeekBar implements OnSeekBarChangeListener{

	public OnOffSimpleBtn(Context context, AttributeSet attrs) {
		super(context);//, attrs);
		
		this.setProgress(10);
		this.setMax(10);
		Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.bgseekbaronoff);
		this.setLayoutParams(new LayoutParams(bMap.getWidth(),bMap.getHeight()));
		this.setProgressDrawable(getResources().getDrawable(R.drawable.bgseekbaronoff));
		this.setThumb(getResources().getDrawable(R.drawable.buttonseekbar));
		this.setThumbOffset(-6);
		this.setOnSeekBarChangeListener(this);
		this.setTag("0");
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch(progress) {
		case 0:
//			mVal = false;
			break;
		case 10:
//			mVal = true;
			break;
		}
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

}
