package com.cubestudio.timeit.chart;

import java.io.Serializable;

public class ChartData implements Serializable {
	
	private String mCategoryName;
	private float mCategoryValue;
	private int mCategoryColor;
	
	public ChartData(String categoryName, float categoryValue, int categoryColor) {
		mCategoryName = categoryName;
		mCategoryValue = categoryValue;
		mCategoryColor = categoryColor;
	}

	public String getCategoryName() {
		return mCategoryName;
	}

	public void setCategoryName(String categoryName) {
		this.mCategoryName = categoryName;
	}

	public float getCategoryValue() {
		return mCategoryValue;
	}

	public void setCategoryValue(int categoryValue) {
		this.mCategoryValue = categoryValue;
	}

	public int getCategoryColor() {
		return mCategoryColor;
	}

	public void setCategoryColor(int categoryColor) {
		this.mCategoryColor = categoryColor;
	}
	
}
