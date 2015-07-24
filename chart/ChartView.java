package com.cubestudio.timeit.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Custom view for printing graph
 * 
 * @author Hyu
 *
 */
public class ChartView extends View{

	Chart mChart;
		
	public ChartView(Context context, Chart chart) {
		super(context);
		mChart = chart;
		this.setOnLongClickListener(mLongClickListener);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mChart.draw(this, canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		super.onTouchEvent(e);
		mChart.onTouch(this, e);
		return true;
	}

	/**
	 * Basic long click listener.
	 * It just call mChart.onLongClick();
	 *
	 * @author Hyu
	 *
	 */
	private OnLongClickListener mLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			mChart.onLongClick(v);
			return true;
		}
	};
}
