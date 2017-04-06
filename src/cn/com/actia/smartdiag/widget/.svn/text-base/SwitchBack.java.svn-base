package cn.com.actia.smartdiag.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.com.actia.smartdiag.R;

public class SwitchBack extends ImageView {
	public static final int STYLE_NIGHT = 2;
	public static final int STYLE_DAY = 1;

	String[] values;
	Bitmap bitmap_font;
	Bitmap bitmap_little;
	Bitmap bitmap_little_back;

	Paint textPaint;

	int mWidth;
	int mHeight;

	public SwitchBack(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(new String[] { "a", "b", "c" }, STYLE_NIGHT);
	}

	public SwitchBack(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwitchBack(Context context) {
		this(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			this.mWidth = getWidth();
			this.mHeight = getHeight();
		}
	}

	public void init(String[] values, int style) {
		this.values = values;
		initChange(style);
	}

	public void init(ArrayList<String> values, int style) {
		String[] v = new String[values.size()];
		values.toArray(v);
		init(v, style);
	}

	public void initChange(int style) {
		initBitmap(style);
		initPaint(style);
	}

	private void initPaint(int style) {
		textPaint = new Paint();
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(getResources().getDimensionPixelSize(
				R.dimen.small_num_size));
		textPaint.setAntiAlias(true);
		switch (style) {
		case STYLE_NIGHT:
			textPaint.setColor(Color.RED);
			break;

		default:
			textPaint.setColor(Color.WHITE);
			break;
		}
	}

	private void initBitmap(int style) {
		switch (style) {
		case STYLE_NIGHT:
			bitmap_font = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_3_button_night);
			bitmap_little = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_red);
			break;

		default:
			bitmap_font = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_3_button);
			bitmap_little = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_green);
			break;
		}
		bitmap_little_back = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_black);
	}

	private void drawNowBtimap(Canvas canvas, int index) {
		int degree = 360 / values.length * index;
		canvas.save();
		canvas.rotate(degree, mWidth / 2, mHeight / 2);
		canvas.drawBitmap(bitmap_little,
				(mWidth - bitmap_little.getWidth()) / 2, 0, null);
		canvas.drawBitmap(bitmap_font, (mWidth - bitmap_font.getWidth()) / 2,
				(mHeight - bitmap_font.getHeight()) / 2, null);

		canvas.restore();
	}

	/**
	 * 将背景的小点画到背景的画布上面
	 */
	private void drawBackBitmap(Canvas canvas, int num) {
		int degrees = 360 / num;
		canvas.save();
		for (int i = 0; i < num; i++) {
			canvas.drawBitmap(bitmap_little_back,
					(mWidth - bitmap_little_back.getWidth()) / 2, 0, null);
			canvas.rotate(degrees, mWidth / 2, mHeight / 2);
		}
		canvas.restore();
	}

	private void drawText(Canvas canvas, String[] texts) {
		int degrees = 360 / texts.length;
		canvas.save();
		for (int i = 0; i < texts.length; i++) {
			int x = mWidth / 2;
			int y = mHeight / 6;
//			canvas.save();
//			canvas.rotate(-degrees * i, x, y);
			canvas.drawText(texts[i], x, y, textPaint);
//			canvas.restore();
			canvas.rotate(degrees, mWidth / 2, mHeight / 2);
		}
		canvas.restore();

	}

	public void notify(String value) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				index = i;
				postInvalidate();
			}
		}
	}

	public void notify(int index) {
		this.index = index;
		postInvalidate();
	}

	private int index = 2;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mWidth = getWidth();
		mHeight = getHeight();
		drawBackBitmap(canvas, values.length);
		drawNowBtimap(canvas, index);
		drawText(canvas, values);
	}

}
