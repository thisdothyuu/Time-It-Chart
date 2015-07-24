package com.cubestudio.timeit.chart;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import com.cubestudio.timeit.utility.Utility;

public class BarChart extends Chart {

	private Activity mActivity;
	private ArrayList<ChartData> mChartDataSet;
	
	private int mDataNum;
	private int mDisplayedDataNum;
	private float mMarginTop;
	private float mMarginLeft;
	private float mMarginRight;
	private float mMarginBottom;
	private float mMarginBetweenBars;
	private boolean mIsTextDisplayed;
//	private String[] mDisplayedBarText;
	private int mBarTextColor;
	private float mBarTextSize;
	private float mBarTextScaleX;
	private float mBarTextMarginTop;
	private String mFontFamily;
	private int mFontFamilyStyle;
	private int mMaxValue;
	private int mBackGroundBarColor;
	private int mBackGroundBarAlpha;
	private int mDataBarAlpha;
	private float mRoundBarRadius;
	
	public BarChart(Activity activity, ChartData chartData) {
		mActivity = activity;
		mChartDataSet.add(chartData);
		mDataNum = 1;
		Initialize();
	}
	
	public BarChart(Activity activity, ArrayList<ChartData> chartDataSet) {
		mActivity = activity;
		mChartDataSet = chartDataSet;
		mDataNum = chartDataSet.size();
		
		Initialize();
	}
	
	private void Initialize() {
		// set mMaxValue as a largest data value of chartDataSet
		mMaxValue = 0;
		for(int i = 0; i < mDataNum; i++) {
			int categoryValue = (int) mChartDataSet.get(i).getCategoryValue();
			if(categoryValue > mMaxValue) {
				mMaxValue = categoryValue; 
			}
		}
//		this.setDisplayedDataNum(7);
		this.setMarginTop(0);
		this.setMarginLeft(0);
		this.setMarginRight(0);
		this.setMarginBottom(0);
		this.setMarginBetweenBars(5);
//		this.setMaxValue(100);
		this.setBackgroundBarColor(Color.parseColor("#cccccc"));
		this.setBackgroundBarAlpha(100);
		this.setDataBarAlpha(100);
		this.setRoundBarRadius(10);
		this.setBarTextDisplayed(false);
//		String[] displayedBarText = {"M","T","W","T","F","S","S"};
//		this.setDisplayedBarText(displayedBarText);
		this.setBarTextColor(Color.parseColor("#cccccc"));
		this.setBarTextScaleX((float)0.9);
		this.setBarTextSize(15);
		this.setBarTextMarginTop(3);
	}
	
	@Override
	protected void draw(View v, Canvas canvas) {
		
		float barWidth = (canvas.getWidth() - 
				(this.mMarginLeft + this.mMarginLeft + this.mMarginBetweenBars * (this.mDataNum - 1)))
				/ this.mDataNum;
		
		for(int i = 0; i < this.mDataNum; i++) {
			Paint barPaint = new Paint();
			barPaint.setAntiAlias(true);
			barPaint.setColor(this.mBackGroundBarColor);
			barPaint.setAlpha(this.mBackGroundBarAlpha);
			RectF barRect = new RectF();
			float barLeftPos = this.mMarginLeft + (barWidth + this.mMarginBetweenBars) * i;
			float barRightPos = barLeftPos + barWidth;
			float barTopPos = this.mMarginTop;
			float barBottomPos;
			
			if(this.mIsTextDisplayed) {
				Paint barTextPaint = new Paint();
				barTextPaint.setAntiAlias(true);
				barTextPaint.setColor(this.mBarTextColor);
				barTextPaint.setTextScaleX(this.mBarTextScaleX);
				barTextPaint.setTextSize(this.mBarTextSize);
				barTextPaint.setTypeface(Typeface.create(mFontFamily, mFontFamilyStyle));
				Rect barTextRect = new Rect();
				barTextPaint.getTextBounds(this.mChartDataSet.get(i).getCategoryName(), 0, this.mChartDataSet.get(i).getCategoryName().length(), barTextRect);
				barBottomPos = barTopPos + canvas.getHeight() - 
						(this.mMarginTop + this.mMarginBottom + this.mBarTextMarginTop + barTextRect.height()); 
				float barTextXPos = barLeftPos + ((barRightPos - barLeftPos) - barTextRect.width()) / 2;
				float barTextYPos = barBottomPos + this.mBarTextMarginTop + barTextRect.height();
				canvas.drawText(this.mChartDataSet.get(i).getCategoryName(), barTextXPos, barTextYPos, barTextPaint);
			}
			else {
				barBottomPos = canvas.getHeight() - this.mMarginBottom;
			}
			
			// draw background bar
			barRect.set(barLeftPos, barTopPos, barRightPos, barBottomPos);
			canvas.drawRoundRect(barRect, this.mRoundBarRadius, this.mRoundBarRadius, barPaint);
			
			// draw chart data bar 
			if(i < this.mDataNum) {
				ChartData chartData = this.mChartDataSet.get(i);
				barPaint.setColor(chartData.getCategoryColor());
				barPaint.setAlpha(this.mDataBarAlpha);
				// highest barTopPos will be 90% of background bar height
				barTopPos = (float) (barBottomPos - (barBottomPos - barTopPos)*(chartData.getCategoryValue() / this.mMaxValue)*0.9);
				barRect.set(barLeftPos, barTopPos, barRightPos, barBottomPos);
				canvas.drawRoundRect(barRect, this.mRoundBarRadius, this.mRoundBarRadius, barPaint);
			}
		}			
	}
	
	@Override
	protected boolean onTouch(View v, MotionEvent e) {
		return true;
	}

	@Override
	protected void onLongClick(View v) {

	}

	public int getDisplayedDataNum() {
		return mDisplayedDataNum;
	}
	 
	/**
	 * displayedDataNum value defines the number of bars in bargraph. 
	 * @param mDisplayedDataNum
	 */
//	public void setDisplayedDataNum(int displayedDataNum) {
//		this.mDisplayedDataNum = displayedDataNum;
//		this.mDisplayedBarText = new String[this.mDisplayedDataNum];
//	}

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

	public float getMarginBetweenBars() {
		return mMarginBetweenBars;
	}

	public void setMarginBetweenBars(float dp) {
		this.mMarginBetweenBars = Utility.dpToPx(mActivity, dp);
	}

	public int getMaxValue() {
		return mMaxValue;
	}

	public void setMaxValue(int maxValue) {
		this.mMaxValue = maxValue;
	}

	public int getBackgroundBarAlpha() {
		return mBackGroundBarAlpha;
	}

	public void setBackgroundBarAlpha(int backGroundBarAlpha) {
		this.mBackGroundBarAlpha = backGroundBarAlpha;
	}

	public int getDataBarAlpha() {
		return mDataBarAlpha;
	}

	public void setDataBarAlpha(int dataBarAlpha) {
		this.mDataBarAlpha = dataBarAlpha;
	}

	public int getBackgroundBarColor() {
		return mBackGroundBarColor;
	}

	public void setBackgroundBarColor(int backGroundBarColor) {
		this.mBackGroundBarColor = backGroundBarColor;
	}

	public float getRoundBarRadius() {
		return mRoundBarRadius;
	}

	public void setRoundBarRadius(float roundBarRadius) {
		this.mRoundBarRadius = roundBarRadius;
	}

//	public String[] getDisplayedBarTexts() {
//		return mDisplayedBarText;
//	}

//	public void setDisplayedBarText(String displayedBarText, int barTextIndex) {
//		this.mDisplayedBarText[barTextIndex] = displayedBarText;
//	}
	
//	public void setDisplayedBarText(String[] displayedBarText) {
//		this.mDisplayedBarText = displayedBarText;
//	}

	public boolean isTextDisplayed() {
		return mIsTextDisplayed;
	}

	public void setBarTextDisplayed(boolean isTextDisplayed) {
		this.mIsTextDisplayed = isTextDisplayed;
	}

	public int getBarTextColor() {
		return mBarTextColor;
	}

	public void setBarTextColor(int barTextColor) {
		this.mBarTextColor = barTextColor;
	}

	public float getBarTextSize() {
		return mBarTextSize;
	}

	public void setBarTextSize(float sp) {
		this.mBarTextSize = Utility.dpToPx(mActivity, sp);
	}

	public void setBarTextTypeface(String fontFamily, int fontFamilyStyle) {
		this.mFontFamily = fontFamily;
		this.mFontFamilyStyle = fontFamilyStyle;
	}

	public float getBarTextScaleX() {
		return mBarTextScaleX;
	}

	public void setBarTextScaleX(float barTextScaleX) {
		this.mBarTextScaleX = barTextScaleX;
	}

	public float getBarTextMarginTop() {
		return mBarTextMarginTop;
	}

	/**
	 * BarTextMarginTop means margin between bargraph and bartext.
	 * If you want to set bottom margin of bartext, use setMarginBottom method.
	 */
	public void setBarTextMarginTop(float dp) {
		this.mBarTextMarginTop = Utility.dpToPx(mActivity, dp);
	}
	
}
