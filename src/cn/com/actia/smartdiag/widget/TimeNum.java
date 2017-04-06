package cn.com.actia.smartdiag.widget;

import cn.com.actia.smartdiag.constants.Constants;

import com.shadow.modelNum.Number;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TimeNum extends ImageView {
	Number mNumberHour;
	Number mNumberMin;
	Number mNumberSec;

	Paint paint;

	public void setSize(int size) {
		mNumberHour.size = size;
		mNumberMin.size = size;
		mNumberSec.size = size;
	}

	int min;
	int sec;
	int hour;

	public TimeNum(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();

		paint.setAntiAlias(true);
		paint.setColor(Constants.COLOR);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(19);

		mNumberHour = new Number(20);
		mNumberMin = new Number(20);
		mNumberSec = new Number(20);
		setTime(0);
	}

	public void setTime(long time) {
		long tempTime = time;
		sec = (int) (tempTime % 60);
		tempTime /= 60;
		min = (int) (tempTime % 60);
		tempTime /= 60;
		hour = (int) (tempTime % 60);
		mNumberHour.setNum(hour);
		mNumberMin.setNum(min);
		mNumberSec.setNum(sec);
		path.moveTo(100, 110);
		path.lineTo(100, 100);
	}

	Path path = new Path();

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		int size = width / 10;
		setSize(size);

		canvas.drawCircle(size * 3+size/2, height / 2 - size / 2, 2, paint);
		canvas.drawCircle(size * 3+size/2, height / 2 + size / 2, 2, paint);
		paint.setColor(Constants.COLOR);
		canvas.drawCircle(size * 6+size/2, width / 2 - size / 2, 2, paint);
		canvas.drawCircle(size * 6+size/2, width / 2 + size / 2, 2, paint);
		canvas.save();

		canvas.translate(size, width / 2 - mNumberHour.size / 2 - 6);
		mNumberHour.drawSelf(canvas);
		canvas.translate(size * 3, 0);
		mNumberMin.drawSelf(canvas);
		canvas.translate(size * 3, 0);
		mNumberSec.drawSelf(canvas);
		canvas.restore();

	}

}
