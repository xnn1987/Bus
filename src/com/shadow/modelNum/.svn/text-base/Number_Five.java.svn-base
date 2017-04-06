package com.shadow.modelNum;

import android.graphics.Canvas;

public class Number_Five {
	private int num = 03;
	public int size = 14;
	int space = 2;

	public Number_Five(int size) {
		this.size = size;
	}

	public void setNum(int num) {
		this.num = num;
	}

	private int[] toNumArray(int num) {
		int stringLen = new String(num + "").length();
		int[] tempnums = new int[stringLen <= 5 ? 5 : stringLen];

		int tempNum = num;
		int counter = tempnums.length - 1;
		do {
			tempnums[counter--] = (int) (tempNum % 10);
		} while ((tempNum = tempNum / 10) >= 1);
		return tempnums;
	}

	public void drawSelf(Canvas canvas) {
		int[] nums = toNumArray(num);
		for (int i = 0; i < nums.length; i++) {
			canvas.save();
			canvas.translate(+i * (size + space), 0);
			new BaseNumber(nums[i], size).drawSelf(canvas);
			canvas.restore();
		}
	}
}
