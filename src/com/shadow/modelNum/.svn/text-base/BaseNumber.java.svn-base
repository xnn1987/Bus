package com.shadow.modelNum;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class BaseNumber {
	float size = 0;

	int[] zero = new int[] { 0, 1, 2, 3, 4, 5 };

	int[] one = new int[] { 1, 2 };
	int[] two = new int[] { 0, 1, 3, 4, 6 };
	int[] three = new int[] { 0, 1, 2, 3, 6 };
	int[] four = new int[] { 1, 2, 5, 6 };
	int[] five = new int[] { 0, 2, 3, 5, 6 };
	int[] six = new int[] { 0, 2, 3, 4, 5, 6 };
	int[] seven = new int[] { 0, 1, 2 };
	int[] eight = new int[] { 0, 1, 2, 3, 4, 5, 6 };
	int[] nine = new int[] { 0, 1, 2, 3, 5, 6 };

	int[] zero_grey = new int[] { 6 };
	int[] one_grey = new int[] { 0, 3, 4, 5, 6 };
	int[] two_grey = new int[] { 2, 5 };
	int[] three_grey = new int[] { 4, 5 };
	int[] four_grey = new int[] { 0, 3, 4 };
	int[] five_grey = new int[] { 1, 4 };
	int[] six_grey = new int[] { 1 };
	int[] seven_grey = new int[] { 3, 4, 5, 6 };
	int[] eight_grey = new int[] {};
	int[] nine_grey = new int[] { 4 };

	int[][] all = new int[][] { zero, one, two, three, four, five, six, seven,
			eight, nine };

	int[][] grey = new int[][] { zero_grey, one_grey, two_grey, three_grey,
			four_grey, five_grey, six_grey, seven_grey, eight_grey, nine_grey };

	int num = 8;

	NumStroke[] strokes;
	NumStroke[] greyStrokes;

	PointF location;

	public BaseNumber(int num, float size) {
		this.num = num;
		this.size = size;
		strokes = new NumStroke[] { new StrokeHeng(0, 0, size),
				new StrokeShu(size - (size) / 4, 0, size),
				new StrokeShu(size - (size) / 4, size - (size) / 4, size),
				new StrokeHeng(0, 2 * size - (size) / 2, size),
				new StrokeShu(0, size - (size) / 4, size),
				new StrokeShu(0, 0, size),
				new StrokeHeng(0, size - (size) / 4, size) };

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.argb(255, 55, 55, 55));

		greyStrokes = new NumStroke[] {
				new StrokeHeng(0, 0, size, paint),
				new StrokeShu(size - (size) / 4, 0, size, paint),
				new StrokeShu(size - (size) / 4, size - (size) / 4, size, paint),
				new StrokeHeng(0, 2 * size - (size) / 2, size, paint),
				new StrokeShu(0, size - (size) / 4, size, paint),
				new StrokeShu(0, 0, size, paint),
				new StrokeHeng(0, size - (size) / 4, size, paint) };
	}

	public PointF getLocation() {
		if (null == location)
			location = new PointF();
		return location;
	}

	public void drawSelf(Canvas canvas) {
		canvas.save();
		canvas.translate(getLocation().x, getLocation().y);
		for (int i = 0; i < all[num].length; i++) {
			int k = all[num][i];
			strokes[k].drawSelf(canvas);
		}
		for (int i = 0; i < grey[num].length; i++) {
			int k = grey[num][i];
			greyStrokes[k].drawSelf(canvas);
		}
		canvas.restore();
	}
}
