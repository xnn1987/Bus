package cn.com.actia.smartdiag.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.com.actia.smartdiag.R;

public class E_Thermograph extends ImageView {
	public static final int STYLE_NIGHT = 2;
	public static final int STYLE_DAY = 1;

	Bitmap bar_back;
	Bitmap bar_image;
	Bitmap back;
	Bitmap text_back;
	Bitmap left;
	private Paint textPaint;
	private Paint textsmallPaint;
	private Paint barbackPaint;

	private int mWidth;
	private int mHeight;

	int style = STYLE_NIGHT;

	float max;
	float min;
	private int num;
	private float value;

	public E_Thermograph(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(STYLE_NIGHT, 100, -100, 0);
	}

	public E_Thermograph(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public E_Thermograph(Context context) {
		this(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			this.mWidth = getWidth();
			this.mHeight = getHeight();
			initsize();
		}
	}

	public void init(int style, float max, float min, float value) {
		initFinalBitmap();
		initChangeBitmap(style);
		initPaint();
		initPaint(style);
		this.max = max;
		this.min = min;
		setValue(value);
	}

	private void initChangeBitmap(int style) {
		switch (style) {
		case STYLE_NIGHT:
			bar_image = BitmapFactory.decodeResource(getResources(),
					R.drawable.thermograph_bar_night);
			left = BitmapFactory.decodeResource(getResources(),
					R.drawable.thermograph_left_night);
			break;
		default:
			bar_image = BitmapFactory.decodeResource(getResources(),
					R.drawable.thermograph_bar);
			left = BitmapFactory.decodeResource(getResources(),
					R.drawable.thermograph_left);
			break;
		}
	}

	private void initPaint(int style) {
		switch (style) {
		case STYLE_NIGHT:
			textPaint.setColor(Color.RED);
			textsmallPaint.setColor(Color.RED);
			break;
		default:
			textPaint.setColor(Color.BLACK);
			textsmallPaint.setColor(Color.WHITE);
			break;
		}

	}

	/**
	 * 若返回值为1表示现有数据超出了上限<br>
	 * 若返回值为0表示现有数据在范围内<br>
	 * 若是范围值为-1表示现有数据超出了下限
	 */
	public int notify(float value, int style) {
		int result = 0;
		if (value > max) {
			result = 1;
			value = max;
		}
		if (value < min) {
			value = min;
			result = -1;
		}
		initPaint(style);
		initChangeBitmap(style);
		setValue(value);
		postInvalidate(left_pixel, 0, mWidth, mHeight);
		return result;
	}

	private void initPaint() {

		textsmallPaint = new Paint();
		textsmallPaint.setTextSize(left.getWidth() * 2);
		textsmallPaint.setTextAlign(Align.CENTER);
		textsmallPaint.setAntiAlias(true);

		textPaint = new Paint();
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(text_back.getHeight() / 2);
		textPaint.setAntiAlias(true);

		barbackPaint = new Paint();
		barbackPaint.setColor(Color.BLACK);

	}

	private void initFinalBitmap() {
		bar_back = BitmapFactory.decodeResource(getResources(),
				R.drawable.thermograph_bar_back);
		back = BitmapFactory.decodeResource(getResources(),
				R.drawable.thermograph_back);
		text_back = BitmapFactory.decodeResource(getResources(),
				R.drawable.thermograph_text_back);
	}

	private void drawBack(Canvas canvas) {
		canvas.drawBitmap(back, mWidth / 2 - back.getWidth() / 2, mHeight / 2
				- back.getHeight() / 2, null);
		for (int i = 0; i < 7; i++) {
			canvas.drawBitmap(bar_back, left_pixel, bottom_pixel - move_pixel
					* (i + 1), null);
		}
	}

	private void drawLeft(Canvas canvas) {
		FontMetrics textMetrics = textsmallPaint.getFontMetrics();

		canvas.drawBitmap(left, left_pixel - left.getWidth() * 4, mHeight / 2
				- left.getHeight() / 2, null);
		float k = ((max - 0) / (max - min));

		int x = left_pixel - left.getWidth() * 4;
		if (k != 1) {

			int h = left.getHeight() / 7;
			int y = (int) (left.getHeight() * k + mHeight / 2
					- left.getHeight() / 2 - h / 2);
			int w = left.getWidth();

			Rect r = new Rect(x, y, x + w, y + h);
			canvas.drawRect(r, barbackPaint);
			canvas.drawText("0", r.left + w / 2, r.top + h / 2
					+ textMetrics.descent, textsmallPaint);
		}

		canvas.drawText(Math.round(min * 10) / 10f + "", x,
				mHeight / 2 - left.getHeight() / 2 + left.getHeight()
						+ (textMetrics.bottom - textMetrics.top),
				textsmallPaint);
		canvas.drawText(Math.round(max * 10) / 10f + "", x,
				mHeight / 2 - left.getHeight() / 2 - textMetrics.bottom,
				textsmallPaint);

		canvas.drawText("°C", x, mHeight / 2 - left.getHeight() / 2
				- textMetrics.bottom + textMetrics.ascent, textsmallPaint);

	}

	int move_pixel;
	int left_pixel;
	int bottom_pixel;

	private void initsize() {
		move_pixel = bar_back.getHeight();
		left_pixel = mWidth / 2 - bar_back.getWidth() / 2;
		bottom_pixel = mHeight / 2 - back.getHeight() / 2 + back.getHeight()
				- (back.getHeight() - move_pixel * 7) / 2;

	}

	/* 画出在手机上 显示的小块的个数 */
	public void drawBar(Canvas canvas, int num) {
		for (int i = 0; i < num; i++) {
			canvas.drawBitmap(bar_image, left_pixel, bottom_pixel - move_pixel
					* (i + 1), null);
		}
	}

	public void drawRight(Canvas canvas, float value) {
		FontMetrics textMetrics = textPaint.getFontMetrics();
		int y = (int) (bottom_pixel
				- ((value - min) / (max - min) * move_pixel * 7) - text_back
				.getHeight() / 2);
		int x = left_pixel + back.getWidth();
		canvas.drawBitmap(text_back, x, y, null);
		canvas.drawText(Math.round(value * 10) / 10f + "",
				x + text_back.getWidth() / 2 + text_back.getWidth() / 15, y
						+ text_back.getHeight() / 2 + textMetrics.descent,
				textPaint);
	}

	public void setValue(float value) {
		num = Math.round((value - min) / (max - min) * 7);
		this.value = value;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBack(canvas);
		drawBar(canvas, num);
		drawLeft(canvas);
		drawRight(canvas, value);
	}
}
