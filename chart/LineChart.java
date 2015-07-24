package com.cubestudio.timeit.chart;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import com.cubestudio.timeit.utility.Utility;

public class LineChart extends Chart {

	private Activity mActivity;
	private ArrayList<ChartData> mChartDataSet;
	
	private int mDataNum;
	private int mMaxValue; 
	private float mLineWidth;
	private int mLineColor;
	private int mLineAlpha;
	private boolean mIsYaxisTextDisplayed;
	private int mYaxisTextColor;
	private float mYaxisTextSize;
	private float mYaxisTextScaleX;
	private float mYaxisTextMarginTop;
	private int mBackGroundColor;
	private int mBackGroundAlpha;
	private boolean mUseBackGroundColor;
	private float mCornerEffectValue;
	private String mFontFamily;
	private int mFontFamilyStyle;
	
	private float mMarginTop;
	private float mMarginLeft;
	private float mMarginRight;
	private float mMarginBottom;
	
	 
	public LineChart(Activity activity, ArrayList<ChartData> chartDataSet) {
		mActivity = activity;
		mChartDataSet = chartDataSet;
		
		Initialize();
		
	}
	
	private void Initialize() {
		mDataNum = mChartDataSet.size();
		
		// set mMaxValue as a largest data value of chartDataSet
		for(int i = 0; i < mDataNum; i++) {
			int categoryValue = (int) mChartDataSet.get(i).getCategoryValue();
			if(categoryValue > mMaxValue) {
				mMaxValue = categoryValue; 
			}
		}
		
		this.setLineWidth(3);
		this.setLineColor(Color.parseColor("#cccccc"));
		this.setLineAlpha(255);
		this.setYaxisTextColor(Color.parseColor("#cccccc"));
		this.setYaxisTextSize(15);
		this.setYaxisTextScaleX((float)0.9);
		this.setYaxisTextMarginTop(3);
		
		this.setUseBackGroundColor(false);
		// default background color realated values
		this.setBackGroundColor(Color.parseColor("#cccccc"));
		this.setBackGroundAlpha(255);
		this.setCornerEffectValue(20.0f);
		
	}
	
	@Override
	protected void draw(View v, Canvas canvas) {

		if(this.isBackGroundColorUsed()) {
			// do something to apply backgroundcolor
		}
		
		float chartSpaceLeftPos = this.getMarginLeft();
		float chartSpaceRightPos = canvas.getWidth() - this.getMarginRight();
		float chartSpaceWidth = chartSpaceRightPos - chartSpaceLeftPos;
		float pointToPointWidth = chartSpaceWidth / (mDataNum - 1);
		float yAxisTextSpaceHeight = 0;
		
		// print y axis text
		if(this.isYaxisTextDisplayed()) {
			Paint yAxisTextPaint = new Paint();
			yAxisTextPaint.setAntiAlias(true);
			yAxisTextPaint.setColor(this.getYaxisTextColor());
			yAxisTextPaint.setTextSize(this.getYaxisTextSize());
			yAxisTextPaint.setTextScaleX(this.getYaxisTextScaleX());
			yAxisTextPaint.setTypeface(Typeface.create(mFontFamily, mFontFamilyStyle));
			
			Rect yAxisTextRect = new Rect();
			chartSpaceLeftPos = this.getMarginLeft() + yAxisTextPaint.measureText(this.mChartDataSet.get(0).getCategoryName())/2;
			chartSpaceRightPos = canvas.getWidth() - yAxisTextPaint.measureText(this.mChartDataSet.get(mDataNum-1).getCategoryName())/2;
			chartSpaceWidth = chartSpaceRightPos - chartSpaceLeftPos;
			pointToPointWidth = chartSpaceWidth / (mDataNum - 1);
			
			// to calculate textspaceheight, call get text bounds with mDataNum-1
			yAxisTextPaint.getTextBounds(this.mChartDataSet.get(mDataNum-1).getCategoryName(),
					0, 
					this.mChartDataSet.get(mDataNum-1).getCategoryName().length(), 
					yAxisTextRect);
			yAxisTextSpaceHeight += yAxisTextRect.height() + this.getYaxisTextMarginTop();
					
			for(int i = 0; i < mDataNum; i++) {
				float xPos = chartSpaceLeftPos + (pointToPointWidth * i) - 
						yAxisTextPaint.measureText(this.mChartDataSet.get(i).getCategoryName())/2;
				float yPos = canvas.getHeight() - this.getMarginBottom();
				
				canvas.drawText(this.mChartDataSet.get(i).getCategoryName(), xPos, yPos, yAxisTextPaint);
				
			}
			
		}
		
		float yAxisPos = canvas.getHeight() - this.getLineWidth()/2 - yAxisTextSpaceHeight;
		float chartSpaceHeight = yAxisPos - this.getMarginTop()- this.getLineWidth()/2;
		
		Path lineChartPath = new Path();

		// start point
		lineChartPath.moveTo(chartSpaceLeftPos, 
							yAxisPos - (chartSpaceHeight * (mChartDataSet.get(0).getCategoryValue() / mMaxValue)));
		
		// draw line chart
		for(int i = 1; i < mDataNum; i++) {
			float pointX = chartSpaceLeftPos + (pointToPointWidth * i);		
			float pointY = yAxisPos - (chartSpaceHeight * (mChartDataSet.get(i).getCategoryValue() / mMaxValue));

			lineChartPath.lineTo(pointX, pointY);
		}

		// draw line
		Paint linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeCap(Paint.Cap.ROUND);
//		linePaint.setStrokeJoin(Paint.Join.ROUND);
		linePaint.setPathEffect(new CornerPathEffect(this.getCornerEffectValue()));
		linePaint.setStrokeWidth(this.getLineWidth());
		linePaint.setColor(this.getLineColor());
		linePaint.setAlpha(this.getLineAlpha());
		canvas.drawPath(lineChartPath, linePaint);
		
	}

	@Override
	protected boolean onTouch(View v, MotionEvent e) {
		return true;
	}

	@Override
	protected void onLongClick(View v) {

	}

	public float getLineWidth() {
		return mLineWidth;
	}

	public void setLineWidth(float dp) {
		this.mLineWidth = Utility.dpToPx(mActivity, dp);
	}

	public int getLineColor() {
		return mLineColor;
	}

	/**
	 * Use Color.parseColor("#RRGGBB")
	 * 
	 * @param mLineColor
	 */
	public void setLineColor(int mLineColor) {
		this.mLineColor = mLineColor;
	}

	public int getLineAlpha() {
		return mLineAlpha;
	}

	public void setLineAlpha(int mLineAlpha) {
		this.mLineAlpha = mLineAlpha;
	}
	
	public int getYaxisTextColor() {
		return mYaxisTextColor;
	}

	public void setYaxisTextColor(int yAxisTextColor) {
		this.mYaxisTextColor = yAxisTextColor;
	}

	public float getYaxisTextSize() {
		return mYaxisTextSize;
	}

	public void setYaxisTextSize(float sp) {
		this.mYaxisTextSize = Utility.dpToPx(mActivity, sp);
	}

	public float getYaxisTextScaleX() {
		return mYaxisTextScaleX;
	}

	public void setYaxisTextScaleX(float mYaxisTextScaleX) {
		this.mYaxisTextScaleX = mYaxisTextScaleX;
	}

	public float getYaxisTextMarginTop() {
		return mYaxisTextMarginTop;
	}

	public void setYaxisTextMarginTop(float dp) {
		this.mYaxisTextMarginTop = Utility.dpToPx(mActivity, dp);
	}

	public int getBackGroundColor() {
		return mBackGroundColor;
	}

	public void setBackGroundColor(int mBackGroundColor) {
		this.mBackGroundColor = mBackGroundColor;
	}

	public int getBackGroundAlpha() {
		return mBackGroundAlpha;
	}

	public void setBackGroundAlpha(int mBackGroundAlpha) {
		this.mBackGroundAlpha = mBackGroundAlpha;
	}

	public boolean isBackGroundColorUsed() {
		return mUseBackGroundColor;
	}
	
	public void setUseBackGroundColor(boolean useBackGroundColor) {
		this.mUseBackGroundColor = useBackGroundColor;
	}

	public float getMarginTop() {
		return mMarginTop;
	}

	public void setMarginTop(float dp) {
		this.mMarginTop = Utility.dpToPx(mActivity, dp);
	}

	public float getMarginLeft() {
		return mMarginLeft;
	}

	public void setMarginLeft(float dp) {
		this.mMarginLeft = Utility.dpToPx(mActivity, dp);
	}

	public float getMarginRight() {
		return mMarginRight;
	}

	public void setMarginRight(float dp) {
		this.mMarginRight = Utility.dpToPx(mActivity, dp);
	}

	public float getMarginBottom() {
		return mMarginBottom;
	}

	public void setMarginBottom(float dp) {
		this.mMarginBottom = Utility.dpToPx(mActivity, dp);
	}

	public boolean isYaxisTextDisplayed() {
		return mIsYaxisTextDisplayed;
	}

	public void setIsYaxisTextDisplayed(boolean isYaxisTextDisplayed) {
		this.mIsYaxisTextDisplayed = isYaxisTextDisplayed;
	}

	public float getCornerEffectValue() {
		return mCornerEffectValue;
	}

	public void setCornerEffectValue(float mCornerEffectValue) {
		this.mCornerEffectValue = mCornerEffectValue;
	}

	public void setYaxisTextTypeface(String fontFamily, int fontFamilyStyle) {
		this.mFontFamily = fontFamily;
		this.mFontFamilyStyle = fontFamilyStyle;
	}


}
