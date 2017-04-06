package cn.com.actia.smartdiag.widget;

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
import cn.com.actia.smartdiag.tools.LightSensor;

import com.shadow.modelNum.Number_Five;

public class E_Disk extends ImageView {
	public Number_Five mNumber;
	int size;
	private float number;
	public String unit = "text";
	private Paint mTextPaint;
	private Bitmap cubeBitmap;

	public E_Disk(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(LightSensor.STATE_NIGHT, 100, 0, 50);
	}

	public E_Disk(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public E_Disk(Context context) {
		this(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			mWidth = getWidth();
			mHeight = getHeight();
		}
	}

	int mWidth;
	int mHeight;
	float mMax;
	float mMin;

	int all_show_num_cubes = 18;
	int show_num_cubes;

	public void init(int style, float max, float min, float value) {
		this.mMax = max;
		this.mMin = min;
		mTextPaint = new Paint();
		mTextPaint.setTextSize(20);
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setAntiAlias(true);
		initPaint(style);
		initBitmap(style);
		setValue(value);
	}

	void initPaint(int style) {
		if (style == LightSensor.STATE_NIGHT) {
			mTextPaint.setColor(Color.RED);
		} else {
			mTextPaint.setColor(Color.WHITE);
		}
	}

	void initBitmap(int style) {
		if (style == LightSensor.STATE_NIGHT) {
			cubeBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.disk_cube_night);
			setImageResource(R.drawable.e_widget_view_disk_night);
		} else {
			cubeBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.disk_cube);
			setImageResource(R.drawable.e_widget_view_disk);
		}
	}

	public void setValue(float value) {
		this.number = value;
		if (value > mMax) {
			value = mMax;
		}
		if (value < mMin) {
			value = mMin;
		}
		show_num_cubes = Math.round((value - mMin) / (mMax - mMin)
				* all_show_num_cubes);

	}

	void drawCube(Canvas canvas, int num) {
		int mindegree = -118;
		int moveDegree = 15;
		canvas.save();
		canvas.rotate(mindegree, mWidth / 2, mHeight / 2);
		for (int i = 0; i < num; i++) {
			canvas.rotate(moveDegree, mWidth / 2, mHeight / 2);
			canvas.save();
			canvas.scale(getWidth() / 200f, getHeight() / 200f, getWidth() / 2,
					getHeight() / 10);
			canvas.drawBitmap(cubeBitmap, mWidth / 2 - cubeBitmap.getWidth()
					/ 2, getHeight() / 10, null);
			canvas.restore();

		}
		canvas.restore();
	}

	/**
	 * 若返回值为1表示现有数据超出了上限<br>
	 * 若返回值为0表示现有数据在范围内<br>
	 * 若是范围值为-1表示现有数据超出了下限
	 */
	public void notify(int style, float value) {
		initBitmap(style);
		initPaint(style);
		setValue(value);
		postInvalidate();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		int height = getHeight();
		int width = getWidth();
		size = height / 9;
		mNumber = new Number_Five(size);
		mNumber.setNum((int) number);
		canvas.save();
		canvas.translate(width * 11 / 48, height * 5 / 14);
		mNumber.drawSelf(canvas);
		canvas.restore();
		if (null != unit && !"".equals(unit)) {
			canvas.drawText(unit, width / 2, height * 3 / 4f, mTextPaint);
		}
		drawCube(canvas, show_num_cubes);
	}

}
