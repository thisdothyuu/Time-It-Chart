package com.cubestudio.timeit.chart;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cubestudio.timeit.utility.Utility;

public class PieChart extends Chart{

	private Activity mActivity;
	private Canvas mCanvas;
	private ArrayList<ChartData> mChartDataSet;
	
	private int mDataNum;
	private float mCategoryValueSum;
	private float mFirstStartAngle;
	private float[] mStartAngles;
	private float[] mSweepAngles;
	private float mOuterStrokeWidth;
	private float mMarginBetweenOuterStrokes;
	private int mOuterStrokeAlpha;
	private int mInnerCircleColor;
	private int mInnerCircleAlpha;
	private float mInnerCircleCenterX;
	private float mInnerCircleCenterY;
	private float mInnerCircleRad;
	
	private boolean mIsClockHourShown;
	private int mClockHoursTextNum;
	private float mClockHoursTextSize;
	private float mClockHoursTextScaleX;
	private int mClockHoursTextColor;
	private Typeface mClockHoursTypeface;
	private int mClockLineNum;
	private float mClockLineLength;
	private float mClockLineMargin;
	private float mClockLineWidth;
	private int mClockLineColor;

	private boolean mIsAnimationEnabled;

	private float mFirstTouchPointAngle;
	private boolean mIsDoughnutAreaTouched;

	private OnChartItemClickListener mPieChartTouchListener = null;
	private OnChartLongClickListener mPieChartLongClickListener = null;

	
	public PieChart(Activity activity, ChartData chartData) {
		mActivity = activity;
		mChartDataSet.add(chartData);
		mDataNum = 1;
		initialize();
	}
	
	public PieChart(Activity activity, ArrayList<ChartData> chartDataSet) {
		mActivity = activity;
		mChartDataSet = chartDataSet;
		mDataNum = chartDataSet.size();
		initialize();
	}
	
	private void initialize() {
		this.setCategoryValueSum();
		this.setFirstStartAngle(270);
		this.mStartAngles = new float[mDataNum];
		this.mSweepAngles = new float[mDataNum];
		this.setOuterStrokeWidth(30);
		this.setMarginBetweenOuterStrokes(1);
		this.setOuterStrokeAlpha(255);
		this.setInnerCircleColor(Color.parseColor("#FFFFFF"));
		this.setInnerCircleAlpha(255);
		this.setClockHourShown(false);
		this.setClockHoursTextNum(12);
		this.setClockHoursTextSize(10);
		this.setClockHoursTextScaleX(1);
		this.setClockHoursTextColor(Color.parseColor("#dddddd"));
		setClockHoursTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
		this.setClockLineNum(24);
		this.setClockLineColor(Color.parseColor("#dddddd"));
		this.setClockLineMargin(3);
		this.setClockLineLength(4);
		this.setClockLineWidth(1);
	}
	
	@Override
	protected void draw(View v, Canvas canvas) {
		mCanvas = canvas;
		
		// if canvas is not a square, a half of length of the shorter side becomes graph radius.
		int centerX = canvas.getWidth()/2;
		int centerY = canvas.getHeight()/2;
		int radius;
		if(centerX > centerY) {
			radius = centerY;
		}
		else {
			radius = centerX;
		}
		
		float clockHourSpace = 0;
		if(this.mIsClockHourShown) {
			
			// clockHourText is the hour number which is displayed along the graph.
			Paint clockHourTextPaint = new Paint();
			clockHourTextPaint.setAntiAlias(true);
			clockHourTextPaint.setColor(this.mClockHoursTextColor);
			clockHourTextPaint.setTextSize(this.mClockHoursTextSize);
			clockHourTextPaint.setTextScaleX(this.mClockHoursTextScaleX);
			clockHourTextPaint.setTypeface(mClockHoursTypeface);
			
			// clockHourLine is the short line displayed with the hour number.
			Paint clockHourLinePaint = new Paint();
			clockHourLinePaint.setAntiAlias(true);
			clockHourLinePaint.setStyle(Style.STROKE);
			clockHourLinePaint.setColor(this.mClockLineColor);
			clockHourLinePaint.setStrokeWidth(this.mClockLineWidth);
			
			// clockHourSpace is the margin for displaying clockHourText and clockHourLine.
			// It is the sum of clockLineLength, clockLineMargin*2 and clockHourTextSpace.
			clockHourSpace = this.mClockLineLength + this.mClockLineMargin*2;
			
			// To calculate width of clockhourText, make text bound using "23",
			// which may have the largest width among 0 - 24.
			Rect clockHourTextRect = new Rect();
			float clockHourTextSpace = clockHourTextPaint.measureText("23");
			clockHourSpace += clockHourTextSpace;
			
			// print clockhourtext
			for(int i = 1; i <= 24; i++) {
				
				// In java, degree starts from 90. In other words, 0 degree location in Korean high school curriculum
				// is the same as 90 degree location is java. 
				// Its direction is also opposite against high school curriculum. If you add degree, point moves 
				// counterclockwise. So I add -15 degree in each iteration and the point moves clockwise
				double degree = (-15 * i + 90);
				double radian = degree*Math.PI/180;
												
				// Calculate the number of hour numbers to be displayed. 
				// mClockHoursTextNum should be a divisor of 24 such as 24, 12, 8, 6, ...
				// For example, if mClockHoursTextNum is 12, hour text is displayed in every 2 iterations.
				if(i % (24 / this.mClockHoursTextNum) == 0) {			
					
					// there are two ways to calculate text width
					// Paint.measureText() and Paint.getBounds(RECT) & RECT.width().
					// measureText() value contains little margin which is actually shown
					// when text is displayed on a screen. 
					// Eventually, measureText() is more similar to real text width. 
					float clockHourTextWidth = clockHourTextPaint.measureText(String.valueOf(i%24));	
					
					clockHourTextPaint.getTextBounds(String.valueOf(i%24), 0, String.valueOf(i%24).length(), clockHourTextRect);
					float clockHourTextHeight = clockHourTextRect.height();
					
					float clockNumX = 0;
					
					if(degree == -90 || degree == -270) {
						// clock number is 0 , 12
						clockNumX = (float)((centerX+
								-clockHourTextWidth/2));
					}
					else if(degree < -90 && degree > -270) {
						// Left part of the circle. Math.cos(radian) is minus value.
						clockNumX = (float)(centerX
								+(radius-clockHourTextSpace)*Math.cos(radian)) - clockHourTextWidth;							
					}
					else {
						clockNumX = (float)(centerX+
								(radius-clockHourTextSpace)*Math.cos(radian));
					}
					
					float clockNumY = 0;
					// Bottom part of the circle. It means that value Y is minus.
					if(degree <= 0 && degree >= -180) {
						clockNumY = (float)((centerY-
								(radius-clockHourTextSpace)*Math.sin(radian))
								-clockHourTextHeight*Math.sin((radian-45*Math.PI/180)*2/3));
					}
					else {
						clockNumY = (float)(centerY-
								((radius-clockHourTextSpace)*Math.sin(radian)));
					}					
					canvas.drawText(String.valueOf(i%24), clockNumX, clockNumY, clockHourTextPaint);
				}
				
				if(i % (24 / this.mClockLineNum) == 0) {
					float lineStartX = (float)(centerX+ 
							(radius-clockHourSpace+this.mClockLineMargin) 
							* Math.cos(radian));
					float lineStartY = (float)(centerY- 
							(radius-clockHourSpace+this.mClockLineMargin) 
							* Math.sin(radian));
					float lineStopX = (float)(centerX+ 
							(radius-clockHourSpace+this.mClockLineMargin+this.mClockLineLength) 
							* Math.cos(radian));
					float lineStopY = (float)(centerY- 
							(radius-clockHourSpace+this.mClockLineMargin+this.mClockLineLength) 
							* Math.sin(radian));
					canvas.drawLine(lineStartX, lineStartY, lineStopX, lineStopY, clockHourLinePaint);
				}
					
			}
		}
		
		Paint outerStrokePaint = new Paint();
		outerStrokePaint.setAntiAlias(true);
		outerStrokePaint.setStyle(Style.STROKE);
		outerStrokePaint.setStrokeWidth(mOuterStrokeWidth);
		outerStrokePaint.setAlpha(mOuterStrokeAlpha);
		RectF outerStrokeRect = new RectF();
		outerStrokeRect.set(centerX - radius + mOuterStrokeWidth / 2 + clockHourSpace, mOuterStrokeWidth / 2 + clockHourSpace,
				centerX + radius - mOuterStrokeWidth / 2 - clockHourSpace, centerY + radius - mOuterStrokeWidth / 2 - clockHourSpace);
		for(int i = 0, n = mDataNum; i < n; i++) {
			ChartData graphData = mChartDataSet.get(i);
			outerStrokePaint.setColor(graphData.getCategoryColor());
			mSweepAngles[i] = graphData.getCategoryValue()/this.mCategoryValueSum*360;
			mStartAngles[i] = mFirstStartAngle;
			for(int j = 0; j < i; j++) {
				mStartAngles[i] += mSweepAngles[j];
			}

			Path outerStrokePath = new Path();
			if (mDataNum == 1) {
				// if there is only one data, margin between outer stroke is not needed
				outerStrokePath.addArc(outerStrokeRect, mStartAngles[i], mSweepAngles[i]);
			} else {
				outerStrokePath.addArc(outerStrokeRect, mStartAngles[i], mSweepAngles[i] - mMarginBetweenOuterStrokes);
			}
			canvas.drawPath(outerStrokePath, outerStrokePaint);
		}
		
		Paint innerCirclePaint = new Paint();
		innerCirclePaint.setAntiAlias(true);
		innerCirclePaint.setColor(mInnerCircleColor);
		innerCirclePaint.setAlpha(mInnerCircleAlpha);
		mInnerCircleCenterX = centerX;
		mInnerCircleCenterY = centerY;
		mInnerCircleRad = radius-mOuterStrokeWidth-clockHourSpace;
		canvas.drawCircle(mInnerCircleCenterX, mInnerCircleCenterY, mInnerCircleRad, innerCirclePaint);
	}
	
	@Override
	protected boolean onTouch(View v, MotionEvent e) {
		int action = e.getAction();

		float dX = e.getX() - mInnerCircleCenterX;
		float dY = e.getY() - mInnerCircleCenterY;
		double touchPointRad = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

		if (action == MotionEvent.ACTION_DOWN) {

			mFirstTouchPointAngle = (float)Math.toDegrees(Math.atan2(dY, dX));
			if(mFirstTouchPointAngle < 0) {
				mFirstTouchPointAngle += 360;
			}

			double outerCircleRad = mInnerCircleRad + mOuterStrokeWidth;

			if (touchPointRad >= mInnerCircleRad && touchPointRad <= outerCircleRad) {
				this.mIsDoughnutAreaTouched = true;
			}

		} else if (action == MotionEvent.ACTION_UP) {

			if (touchPointRad < mInnerCircleRad) {
				return true;
			}

			if (mFirstTouchPointAngle < mFirstStartAngle) {
				mFirstTouchPointAngle += 360;
			}

			for (int i = 0; i < mDataNum; i++) {
				if (mFirstTouchPointAngle >= mStartAngles[i] && mFirstTouchPointAngle <= mStartAngles[i] + mSweepAngles[i]) {
					if (this.mPieChartTouchListener != null) {
						this.mPieChartTouchListener.onChartItemClicked(CHART_TYPE_PIE, this.mChartDataSet.get(i), i);
					}
				}
			}
		}

		return true;
	}

	@Override
	public void setOnChartItemClickListener(OnChartItemClickListener listener) {
		this.mPieChartTouchListener = listener;
	}

	@Override
	protected void onLongClick(View v) {
		Log.v("chartlongclick", "piechart onLongClick");
		this.mPieChartLongClickListener.onChartLongClick();
	}

	@Override
	public void setOnChartLongClickListener(OnChartLongClickListener listener) {
		this.mPieChartLongClickListener = listener;
	}

	public void changeChartData(ArrayList<ChartData> chartDataSet) {
		mChartDataSet = chartDataSet;
		mDataNum = chartDataSet.size();
	}
	
	public void setCategoryValueSum() {
		float categoryValueSum = 0;
		for(ChartData i : mChartDataSet) {
			categoryValueSum += i.getCategoryValue();
		}
		this.mCategoryValueSum = categoryValueSum;
	}

	public ArrayList<ChartData> getChartDataSet() {
		return this.mChartDataSet;
	}
	
	public float getCategoryValueSum() {
		
		return mCategoryValueSum;
	}

	public float getFirstStartAngle() {
		return mFirstStartAngle;
	}

	public void setFirstStartAngle(float firstStartAngle) {
		this.mFirstStartAngle = firstStartAngle;
	}

	public float[] getStartAngles() {
		return this.mStartAngles;
	}

	public float[] getSweepAngles() {
		return this.mSweepAngles;
	}

	public float getOuterStrokeWidth() {
		return mOuterStrokeWidth;
	}

	public void setOuterStrokeWidth(float dp) {
		this.mOuterStrokeWidth = Utility.dpToPx(mActivity, dp);
	}

	public float getMarginBetweenOuterStrokes() {
		return mMarginBetweenOuterStrokes;
	}

	/**
	 * MarginBetweenOuterStrokes defines the width of white space between pies in piegraph
	 */
	public void setMarginBetweenOuterStrokes(float degree) {
		this.mMarginBetweenOuterStrokes = degree;
	}

	public int getOuterStrokeAlpha() {
		return mOuterStrokeAlpha;
	}

	public void setOuterStrokeAlpha(int outerStrokeAlpha) {
		this.mOuterStrokeAlpha = outerStrokeAlpha;
	}

	public int getInnerCircleColor() {
		return mInnerCircleColor;
	}

	public void setInnerCircleColor(int innerCircleColor) {
		this.mInnerCircleColor = innerCircleColor;
	}

	public int getInnerCircleAlpha() {
		return mInnerCircleAlpha;
	}

	public void setInnerCircleAlpha(int innerCircleAlpha) {
		this.mInnerCircleAlpha = innerCircleAlpha;
	}

	public float getInnerCircleRad() {
		return this.mInnerCircleRad;
	}
	
	public boolean isAnimationEnabled() {
		return mIsAnimationEnabled;
	}

	public void enableAnimation() {
		this.mIsAnimationEnabled = true;
	}
	
	public void disableAnimation() {
		this.mIsAnimationEnabled = false;
	}

	public void setClockHourShown(boolean isClockHourShown) {
		this.mIsClockHourShown = isClockHourShown;
	}
	
	public int getClockHoursTextNum() {
		return mClockHoursTextNum;
	}

	/**
	 * mClocHoursTextNum should be divisor of 24 such as 24, 12, 8, ...
	 */
	public void setClockHoursTextNum(int clockHoursNum) {
		this.mClockHoursTextNum = clockHoursNum;
	}

	public float getClockHoursTextSize() {
		return mClockHoursTextSize;
	}

	public void setClockHoursTextSize(float sp) {
		this.mClockHoursTextSize = Utility.dpToPx(mActivity, sp);
	}

	public float getClockHoursTextScaleX() {
		return mClockHoursTextScaleX;
	}

	public void setClockHoursTextScaleX(float clockHoursTextScaleX) {
		this.mClockHoursTextScaleX = clockHoursTextScaleX;
	}
	
	public int getClockHoursTextColor() {
		return mClockHoursTextColor;
	}

	public void setClockHoursTextColor(int clockHoursTextColor) {
		this.mClockHoursTextColor = clockHoursTextColor;
	}
	
	public void setClockHoursTypeface(Typeface typeface) {
		mClockHoursTypeface = typeface;
	}
	
	public Typeface getClockHoursTypeface() {
		return mClockHoursTypeface;
	}

	public int getClockLineNum() {
		return mClockLineNum;
	}

	public void setClockLineNum(int clockLineNum) {
		this.mClockLineNum = clockLineNum;
	}

	public float getClockLineLength() {
		return mClockLineLength;
	}

	public void setClockLineLength(float dp) {
		this.mClockLineLength = Utility.dpToPx(mActivity, dp);;
	}

	public float getClockLineMargin() {
		return mClockLineMargin;
	}

	public void setClockLineMargin(float dp) {
		this.mClockLineMargin = Utility.dpToPx(mActivity, dp);
	}

	public int getClockLineColor() {
		return mClockLineColor;
	}

	public void setClockLineColor(int clockLineColor) {
		this.mClockLineColor = clockLineColor;
	}

	public float getClockLineWidth() {
		return mClockLineWidth;
	}

	public void setClockLineWidth(float dp) {
		this.mClockLineWidth = Utility.dpToPx(mActivity, dp);
	}

	public boolean isDoughnutAreaTouched() {
		return this.mIsDoughnutAreaTouched;
	}

	public void setIsDoughnutAreaTouched(boolean isDoughnutAreaTouched) {
		this.mIsDoughnutAreaTouched = isDoughnutAreaTouched;
	}

	public float getFirstTouchPointAngle() {
		return this.mFirstTouchPointAngle;
	}

	public float getInnerCircleCenterX() {
		return mInnerCircleCenterX;
	}

	public float getInnerCircleCenterY() {
		return mInnerCircleCenterY;
	}


}
