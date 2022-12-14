package four.non.bronds.yyys.zzztimer.widgets.dateslider;

import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;

import four.non.bronds.yyys.zzztimer.R;
import four.non.bronds.yyys.zzztimer.widgets.dateslider.DateSlider.TimeObject;



public class TimeSlider extends DateSlider {

	public TimeSlider(Context context, OnDateSetListener l, Calendar calendar) {
		super(context, l, calendar);
	}
	
	/**
	 * Create the hour and the minutescroller and feed them with their labelers
	 * and place them on the layout.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// this needs to be called before everything else to set up the main layout of the DateSlider  
		super.onCreate(savedInstanceState);		
		
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		
		// create the hour scroller and assign its labeler and add it to the layout
		ScrollLayout mHourScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mHourScroller.setLabeler(hourLabeler, mTime.getTimeInMillis(),90,60);
		mLayout.addView(mHourScroller, 0,lp);
		mScrollerList.add(mHourScroller);
		
		// create the minute scroller and assign its labeler and add it to the layout
		ScrollLayout mMinuteScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mMinuteScroller.setLabeler(minuteLabeler, mTime.getTimeInMillis(),45,60);
		mLayout.addView(mMinuteScroller, 1,lp);
		mScrollerList.add(mMinuteScroller);
		
		// this method _has_ to be called to set the onScrollListeners for all the Scrollers
		// in the mScrollerList.
		setListeners();
	}
	
	// the labeler for the hour scroller
	protected Labeler hourLabeler = new Labeler() {
	
			@Override
			public TimeObject add(long time, int val) {
				Calendar c = Calendar.getInstance(mTimeZone);
				c.setTimeInMillis(time);
				c.add(Calendar.HOUR_OF_DAY, val);
				return timeObjectfromCalendar(c);
			}
			
			@Override
			protected TimeObject timeObjectfromCalendar(Calendar c) {
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				// get the first millisecond of that hour
				c.set(year, month, day, hour, 0, 0);
				c.set(Calendar.MILLISECOND, 0);
				long startTime = c.getTimeInMillis();
				// get the last millisecond of that hour
				c.set(year, month, day, hour, 59, 59);
				c.set(Calendar.MILLISECOND, 999);
				long endTime = c.getTimeInMillis();
				return new TimeObject(String.valueOf(hour), startTime, endTime);
			}
			
		};
		
		// the labeler for the minute scroller
		protected Labeler minuteLabeler = new Labeler() {
	
			@Override
			public TimeObject add(long time, int val) {
				Calendar c = Calendar.getInstance(mTimeZone);
				c.setTimeInMillis(time);
				c.add(Calendar.MINUTE, val);
				return timeObjectfromCalendar(c);
			}
			
			@Override
			protected TimeObject timeObjectfromCalendar(Calendar c) {
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				// get the first millisecond of that minute
				c.set(year, month, day, hour, minute, 0);
				c.set(Calendar.MILLISECOND, 0);
				long startTime = c.getTimeInMillis();
				// get the last millisecond of that minute
				c.set(year, month, day, hour, minute, 59);
				c.set(Calendar.MILLISECOND, 999);
				long endTime = c.getTimeInMillis();
				return new TimeObject(String.valueOf(minute), startTime, endTime);
			}
			
		};
		
		/**
		 * define our own title of the dialog
		 */
		@Override
		protected void setTitle() {
			if (mTitleText != null) {
				mTitleText.setText(String.format("Selected Time: %tR",mTime)); 
			}
		}

}

