package com.cubestudio.timeit.chart;

import java.io.Serializable;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * An abstract graph class
 *  
 * @author Hyu
 *
 */
public abstract class Chart implements Serializable {
	
	public static final int CHART_TYPE_BAR = 0;
	public static final int CHART_TYPE_CIRCLE = 1;
	public static final int CHART_TYPE_PIE = 2;
	public static final int CHART_TYPE_LINE = 3;
	
	protected abstract void draw(View v, Canvas canvas);
	
	protected abstract boolean onTouch(View v, MotionEvent e);

	protected abstract void onLongClick(View v);

	/**
	 * Chart Touch Listener
	 * 
	 * @author Hyu
	 *
	 */
	public interface OnChartItemClickListener {
		/**
		 * Main touch event handler.
		 * I think you don't need an explanation about chartType and chartData.
		 * dataNum shows the order of touched chart data. In case of Pie / Bar
		 * chart, data order needs to be provided to developer. 
		 * If chartType is Circle, which has only one data, dataNum should be 0. 
		 * 
		 * @param chartType
		 * @param chartData
		 * @param dataNum
		 */
		void onChartItemClicked(int chartType, ChartData chartData, int dataNum);
	}

	public void setOnChartItemClickListener(OnChartItemClickListener listener) {
	}

	/**
	 * Chart Long Click Listener
	 *
	 * @author Huejii
	 *
	 */
	public interface OnChartLongClickListener {
		void onChartLongClick();
	}

	public void setOnChartLongClickListener(OnChartLongClickListener listener) {
	}

}
