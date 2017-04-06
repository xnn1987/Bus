package cn.com.actia.smartdiag.widget;

import cn.com.actia.smartdiag.constants.Constants;

import com.shadow.modelNum.Number_Five;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.ImageView;

public class E_Odometer extends ImageView {
	public Number_Five mNumber;
	int mSize;
	public float number;
	public String unit="hhh";
	private Paint mTextPaint;

	public E_Odometer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public E_Odometer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public E_Odometer(Context context) {
		super(context);
		init();
	}

	public void init() {
		mTextPaint = new Paint();
		mTextPaint.setTextSize(20);mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.WHITE);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		int height = getHeight();
		int width = getWidth();
		mSize = height / 9;
		mNumber = new Number_Five(mSize);
		mNumber.setNum((int) number);
		mNumber.setNum((int) number);
		canvas.save();
		canvas.translate(width * 11 / 48, height * 5 / 15);
		mNumber.drawSelf(canvas);
		canvas.restore();
		if (null != unit && !"".equals(unit)) {
			mTextPaint.setColor(Constants.COLOR);
			canvas.drawText(unit, width/2, height *4/ 5f, mTextPaint);
		}
	}

}
