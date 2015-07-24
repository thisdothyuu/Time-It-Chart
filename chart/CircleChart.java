package com.cubestudio.timeit.chart;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.cubestudio.timeit.utility.Utility;

/**
 * Create CirclrGraph. Used with single category
 * 
 * @author Huejii
 *
 */
public class CircleChart extends Chart {
	
	private Activity mActivity;
	private Canvas mCanvas;
	private ChartData mGraphData;
	private String mCategoryName;
	private float mCategoryValue;
	private int mCategoryColor;
	
	private float mStartAngle;
	private float mSweepAngle;
	private float mOuterStrokeWidth;
	private int mOuterStrokeAlpha;
	private int mInnerCircleAlpha;
	private float mMainValueTextSize;
	private float mMainValueTextScaleX;
	private float mMainValueTextPosX;
	private boolean mIsMainValueTextPosXSet;
	private float mMainValueTextPosY;
	private boolean mIsMainValueTextPosYSet;
	private float mPercentTextSize;
	private float mPercentTextScaleX;
	private float mPercentTextPosX;
	private boolean mIsPercentTextPosXSet;
	private float mPercentTextPosY;
	private boolean mIsPercentTextPosYSet;
	private float mLabelTextSize;
	private float mLabelTextScaleX;
	private float mLabelTextPosX;
	private boolean mIsLabelTextPosXSet;
	private float mLabelTextPosY;
	private boolean mIsLabelTextPosYSet;
	
	private boolean mIsAnimationEnabled;
	private float mTotalDuration;
	private int mTotalFrameNum;
	private float mFrameDuration;
	private int mCurrentFrameNum;
	
	/**
	 * ToDo : Using overloading, support various types of input in addition to % value
	 * @param activity
	 * @param graphData
	 */
	public CircleChart(Activity activity, ChartData graphData) {
		mActivity = activity;
		mGraphData = graphData;
		mCategoryName = mGraphData.getCategoryName();
		mCategoryValue = mGraphData.getCategoryValue();
		mCategoryColor = mGraphData.getCategoryColor();
		Initialize();
	}
	
	private void Initialize() {
		this.setStartAngle(270);
		this.setSweepAngle(mCategoryValue);
		this.setOuterStrokeWidth(3);
		this.setOuterStrokeAlpha(255);
		this.setInnerCircleAlpha(22);
		this.setMainValueTextSize(80);
		this.setMainValueTextScaleX((float)0.8);
		this.setPercentTextSize(35);
		this.setPercentTextScaleX(1);
		this.setLabelTextSize(15);
		this.setLabelTextScaleX(1);
		this.enableAnimation();
		this.setTotalDuration(500);
		this.setFrameDuration(10);
		this.mTotalFrameNum = (int) (mTotalDuration / mFrameDuration);
		this.mCurrentFrameNum = 0;
	}
	
	@Override
	protected void draw(View v, Canvas canvas) {
		mCanvas = canvas;
		
		Paint innerCirclePaint = new Paint();
		innerCirclePaint.setAntiAlias(true);
		innerCirclePaint.setColor(mCategoryColor);
		innerCirclePaint.setAlpha(mInnerCircleAlpha);
		canvas.drawCircle(canvas.getWidth()/2, canvas.getWidth()/2, canvas.getWidth()/2 - mOuterStrokeWidth, innerCirclePaint);
		
		Paint textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(mMainValueTextSize);
		textPaint.setColor(mCategoryColor);
		textPaint.setTextScaleX(mMainValueTextScaleX);
		Rect mainTextRect = new Rect();
		textPaint.getTextBounds(String.valueOf((int)mCategoryValue), 0, String.valueOf((int)mCategoryValue).length(), mainTextRect);
		if(!this.mIsMainValueTextPosXSet) {
			float MainValueTextWidth = textPaint.measureText(String.valueOf((int)mCategoryValue));
			this.setMainValueTextPosX((float)((mCanvas.getWidth() - MainValueTextWidth)/2*0.85));
		}
		if(!this.mIsMainValueTextPosYSet) {
			this.setMainValueTextPosY((float)(canvas.getHeight()*0.625));	
		}
		canvas.drawText(String.valueOf((int)mCategoryValue), mMainValueTextPosX, mMainValueTextPosY, textPaint);
		
		textPaint.setTextSize(mPercentTextSize);
		textPaint.setTextScaleX(mPercentTextScaleX);
		Rect percetTextRect = new Rect();
		textPaint.getTextBounds("%", 0, "%".length(), percetTextRect);
		if(!this.mIsPercentTextPosXSet) {
			this.setPercentTextPosX(this.mMainValueTextPosX + mainTextRect.width() + Utility.dpToPx(mActivity, 5));
		}
		if(!this.mIsPercentTextPosYSet) {
			this.setPercentTextPosY(this.mMainValueTextPosY - mainTextRect.height() + percetTextRect.height());
		}
		canvas.drawText("%", this.mPercentTextPosX, this.mPercentTextPosY, textPaint);
		
		textPaint.setTextSize(mLabelTextSize);
		textPaint.setTextScaleX(mLabelTextScaleX);
		Rect labelTextRect = new Rect();
		textPaint.getTextBounds(String.valueOf(mCategoryValue), 0, String.valueOf(mCategoryValue).length(), labelTextRect);
		if(!this.mIsLabelTextPosXSet) {
			this.setLabelTextSPosX((canvas.getWidth() - textPaint.measureText(mCategoryName))/2);
		}
		if(!this.mIsLabelTextPosYSet) {
			this.setLabelTextSPosY(mMainValueTextPosY + Utility.dpToPx(mActivity, 20));
		}
		canvas.drawText(mCategoryName, mLabelTextPosX, mLabelTextPosY, textPaint);
		
		if(this.isAnimationEnabled()) {	
			Paint outerStrokePaint = new Paint();
			outerStrokePaint.setAntiAlias(true);
			outerStrokePaint.setStyle(Style.STROKE);
			outerStrokePaint.setStrokeWidth(mOuterStrokeWidth);
			outerStrokePaint.setColor(mCategoryColor);
			RectF outerStrokeRect = new RectF();
			outerStrokeRect.set(mOuterStrokeWidth/2, mOuterStrokeWidth/2, canvas.getWidth() - mOuterStrokeWidth/2, canvas.getWidth() - mOuterStrokeWidth/2);
			Path outerStrokePath = new Path();
			outerStrokePath.addArc(outerStrokeRect, mStartAngle, mSweepAngle * mCurrentFrameNum / mTotalFrameNum);
			canvas.drawPath(outerStrokePath, outerStrokePaint);	
					
			if(mCurrentFrameNum++ < mTotalFrameNum) {
				v.postInvalidateDelayed((long) this.mFrameDuration);
			}
			else {
				return ;
			}
		}
		else {
			Paint outerStrokePaint = new Paint();
			outerStrokePaint.setAntiAlias(true);
			outerStrokePaint.setStyle(Style.STROKE);
			outerStrokePaint.setStrokeWidth(mOuterStrokeWidth);
			outerStrokePaint.setColor(mCategoryColor);
			RectF outerStrokeRect = new RectF();
			outerStrokeRect.set(mOuterStrokeWidth/2, mOuterStrokeWidth/2, canvas.getWidth() - mOuterStrokeWidth/2, canvas.getWidth() - mOuterStrokeWidth/2);
			Path outerStrokePath = new Path();
			outerStrokePath.addArc(outerStrokeRect, mStartAngle, mSweepAngle);
			canvas.drawPath(outerStrokePath, outerStrokePaint);	
		}		
	}
	
	@Override
	protected boolean onTouch(View v, MotionEvent e) {
		return true;
	}

	@Override
	protected void onLongClick(View v) {

	}

	public float getStartAngle() {
		return mStartAngle;
	}

	public void setStartAngle(float startAngle) {
		this.mStartAngle = startAngle;
	}
	
	public void setSweepAngle(float categoryValue) {
		this.mSweepAngle = categoryValue/100*360;
	}
	
	public float getOuterStrokeWidth() {
		return this.mOuterStrokeWidth;
	}
	
	public void setOuterStrokeWidth(float dp) {
		this.mOuterStrokeWidth = Utility.dpToPx(mActivity, dp);
	}

	public int getOuterStrokeAlpha() {
		return mOuterStrokeAlpha;
	}

	public void setOuterStrokeAlpha(int outerStrokeAlpha) {
		this.mOuterStrokeAlpha = outerStrokeAlpha;
	}

	public int getInnerCircleAlpha() {
		return mInnerCircleAlpha;
	}

	public void setInnerCircleAlpha(int innerCircleAlpha) {
		this.mInnerCircleAlpha = innerCircleAlpha;
	}
	
	public float getMainValueTextSize() {
		return mMainValueTextSize;
	}

	public void setMainValueTextSize(float sp) {
		this.mMainValueTextSize = Utility.dpToPx(mActivity, sp);
	}

	public float getMainValueTextScaleX() {
		return mMainValueTextScaleX;
	}

	public void setMainValueTextScaleX(float mainValueTextScaleX) {
		this.mMainValueTextScaleX = mainValueTextScaleX;
	}

	public float getMainValueTextPosX() {
		return mMainValueTextPosX;
	}

	public void setMainValueTextPosX(float mainValueTextPosX) {
		this.mMainValueTextPosX = mainValueTextPosX;
		this.mIsMainValueTextPosXSet = true;
	}

	public float getMainValueTextPosY() {
		return mMainValueTextPosY;
	}

	public void setMainValueTextPosY(float mainValueTextPosY) {
		this.mMainValueTextPosY = mainValueTextPosY;
		this.mIsMainValueTextPosYSet = true;
	}

	public float getPercentTextSize() {
		return mPercentTextSize;
	}

	public void setPercentTextSize(float sp) {
		this.mPercentTextSize = Utility.dpToPx(mActivity, sp);
	}

	public float getPercentTextScaleX() {
		return mPercentTextScaleX;
	}

	public void setPercentTextScaleX(float percentTextScaleX) {
		this.mPercentTextScaleX = percentTextScaleX;
	}

	public float getPercentTextPosX() {
		return mPercentTextPosX;
	}

	public void setPercentTextPosX(float percentTextPosX) {
		this.mPercentTextPosX = percentTextPosX;
		this.mIsPercentTextPosXSet = true;
	}

	public float getPercentTextPosY() {
		return mPercentTextPosY;
	}

	public void setPercentTextPosY(float percentTextPosY) {
		this.mPercentTextPosY = percentTextPosY;
		this.mIsPercentTextPosYSet = true;
	}

	public float getmLabelTextSize() {
		return mLabelTextSize;
	}

	public void setLabelTextSize(float sp) {
		this.mLabelTextSize = Utility.dpToPx(mActivity, sp);
	}

	public float getLabelTextScaleX() {
		return mLabelTextScaleX;
	}

	public void setLabelTextScaleX(float labelTextScaleX) {
		this.mLabelTextScaleX = labelTextScaleX;
	}

	public float getLabelTextPosX() {
		return this.mLabelTextPosX;
	}

	public void setLabelTextSPosX(float labelTextSPosX) {
		this.mLabelTextPosX = labelTextSPosX;
		this.mIsLabelTextPosXSet = true;
	}

	public float getLabelTextSPosY() {
		return this.mLabelTextPosY;
	}

	public void setLabelTextSPosY(float labelTextSPosY) {
		this.mLabelTextPosY = labelTextSPosY;
		this.mIsLabelTextPosYSet = true;
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

	public float getTotalDuration() {
		return mTotalDuration;
	}

	public void setTotalDuration(float totalDuration) {
		this.mTotalDuration = totalDuration;
	}

	public float getFrameDuration() {
		return mFrameDuration;
	}

	public void setFrameDuration(float frameDuration) {
		this.mFrameDuration = frameDuration;
	}
}
