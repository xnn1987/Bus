package cn.com.actia.smartdiag.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.com.actia.smartdiag.R;

public class Histogram extends ImageView {
	/** 这里是 留的尾巴长度 的两倍 */
	private final int tail = 20;
	/** 这里是箭头的线条的长度 */
	private final int tip_long = 10;

	/** 纵轴最小值对应的坐标 */
	private double yMin_y;

	/** 纵轴最大值 */
	private double yMax = 1000;

	/** 纵轴最小值 */
	private double yMin = 0;

	/** x、y轴的间距 */
	private double x_Space;
	private double y_Space;

	/** 存放直方图中的点 */
	private ArrayList<DataPoint> mDatas;

	/** 这个是用来画坐标轴，和箭头的的 */
	private Paint linePaint;
	/** 这个是用来画背景的黑色线条的 */
	private Paint bgMarkPaint;
	/** 这个是用来画刻度线的 */
	private Paint marksPaint;

	/** 这个是用来画数据 */
	private Paint dataPaint;

	/** 这个是用来写字的 */
	private Paint textPaint;

	private Rect mViewRect;

	/** O点对应的t */
	private double O_t = 0;
	/** 时间轴一格对应的时间 */
	private double t_space = 1;

	/** 每秒刷新次数 */
	private double flashTime = 10;

	float mMax;
	float mMin;
	String mUnit;

	private IGraphDataChangeListener gdcl;

	/**
	 * 设置刷新次数
	 */
	public void setFlashTime(double flashTime) {
		this.flashTime = flashTime;
	}

	/** 纵轴分成多少段 */
	private int sectionY = 10;
	/** 横轴分成为多少段 */
	private int sectionX = 10;

	public static enum DrawStatus {
		STATUS_NONE, // 未开始
		STATUS_STARTING, // 正在开始状态
		STATUS_WORKING;// 运行状态
	}

	public Histogram(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(2);

		dataPaint = new Paint();
		dataPaint.setAntiAlias(true);
		dataPaint.setStrokeWidth(2);

		bgMarkPaint = new Paint();
		bgMarkPaint.setColor(Color.GRAY);

		marksPaint = new Paint();
		marksPaint.setAntiAlias(true);
		marksPaint.setColor(Color.CYAN);
		marksPaint.setStrokeWidth(1.5f);

		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(13);
		textPaint.setTextAlign(Align.LEFT);
		textPaint.setAntiAlias(true);

		linePaint.setColor(getResources().getColor(R.color.graph_color));

		dataPaint.setColor(getResources().getColor(R.color.white));
		this.mDatas = new ArrayList<DataPoint>();
		// for (int i = 0; i < 10; i++) {
		// DataPoint dataPoint = new DataPoint();
		// dataPoint.hen = i * 1;
		// dataPoint.shu = i * 100;
		// mDatas.add(dataPoint);
		// }
	}

	public Histogram(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Histogram(Context context) {
		this(context, null);
	}

	public void addDatas(DataPoint dp) {
		mDatas.add(dp);
		if (null != gdcl) {
			gdcl.onAddData(dp);
		}
	}

	/** 设置O点对应的时间 */
	public void setO_t(double ot) {
		this.O_t = ot;
		if (null != gdcl) {
			gdcl.onSetOT(ot);
		}
	}

	public void setMax(int max) {
		this.mMax = max;
	}

	public void setUnit(String unit) {
		this.mUnit = unit;
	}

	public void setMin(int min) {
		this.mMin = min;
	}

	public void initData(float max, float min, String unit) {
		this.mMax = max;
		this.mMin = min;
		this.mUnit = unit;
		if (null != gdcl) {
			gdcl.onInitData(mDatas, max, min, unit);
		}
	}

	/** 画上背景的线条 */
	public void drawBgline(Canvas canvas) {
		float[] MarkYs = new float[sectionY];
		float Y = mViewRect.top + tail * 2;
		float O;
		if (yMin == 0) {
			O = mViewRect.bottom - tail;
		} else {
			O = mViewRect.bottom - tail - tip_long;
		}
		yMin_y = O;
		float len = (O - Y) / sectionY;
		y_Space = len;
		for (int i = 0; i < MarkYs.length; i++) {
			MarkYs[i] = i * len + Y;
			canvas.drawLine(mViewRect.left + tail, MarkYs[i], mViewRect.right
					- tail - tip_long, MarkYs[i], bgMarkPaint);
		}
	}

	private boolean isLittle = true;

	/** 画上背景的线条(竖线) */
	public void drawBglineShu(Canvas canvas) {
		float[] MarkXs = new float[sectionX + 2];
		float Y = mViewRect.bottom - tail;
		float X = mViewRect.right - tail * 2;
		float O = mViewRect.left + tail;
		int n = (int) ((O_t * flashTime) % flashTime);
		float len = (X - O) / sectionX;
		x_Space = len;
		int shu_line_space = isLittle ? 2 : 1;
		for (int i = 0; i < MarkXs.length - 1; i += shu_line_space) {
			MarkXs[i] = (float) i * len + O - (float) n * len / 10f;
			canvas.drawLine(MarkXs[i], mViewRect.top + tail + tip_long,
					MarkXs[i], Y, bgMarkPaint);
		}

	}

	public void drawText(Canvas canvas) {

	}

	/** 画X、Y轴 */
	public void drawXYAxis(Canvas canvas) {
		getWidth();
		/* 纵-轴的线 */
		canvas.drawLine(mViewRect.left + tail, mViewRect.top + tail,
				mViewRect.left + tail, mViewRect.bottom - tip_long, linePaint);
		/* 横-轴的线 */
		canvas.drawLine(mViewRect.left + tip_long, mViewRect.bottom - tail,
				mViewRect.right - tail, mViewRect.bottom - tail, linePaint);

		/* 纵-轴的箭头 */
		canvas.save();
		canvas.rotate(15, mViewRect.left + tail, mViewRect.top + tail);
		canvas.drawLine(mViewRect.left + tail, mViewRect.top + tail,
				mViewRect.left + tail, mViewRect.top + (15 + tail), linePaint);
		canvas.rotate(-30, mViewRect.left + tail, mViewRect.top + tail);
		canvas.drawLine(mViewRect.left + tail, mViewRect.top + tail,
				mViewRect.left + tail, mViewRect.top + (15 + tail), linePaint);
		canvas.restore();

		/* 横-轴的箭头 */
		canvas.save();
		canvas.rotate(15, mViewRect.right - tail, mViewRect.bottom - tail);
		canvas.drawLine(mViewRect.right - (15 + tail), mViewRect.bottom - tail,
				mViewRect.right - tail, mViewRect.bottom - tail, linePaint);
		canvas.rotate(-30, mViewRect.right - tail, mViewRect.bottom - tail);
		canvas.drawLine(mViewRect.right - (15 + tail), mViewRect.bottom - tail,
				mViewRect.right - tail, mViewRect.bottom - tail, linePaint);
		canvas.restore();
	}

	/** 纵轴的刻度 */
	public void drawMarkY(Canvas canvas) {
		float[] MarkYs = new float[sectionY];
		float X = mViewRect.left + tail;
		float Y = mViewRect.top + tail * 2;
		float O;
		if (yMin == 0) {
			O = mViewRect.bottom - tail;
		} else {
			O = mViewRect.bottom - tail - tip_long;
		}
		yMin_y = O;
		float len = (O - Y) / sectionY;
		y_Space = len;
		for (int i = 0; i < MarkYs.length; i++) {
			MarkYs[i] = i * len + Y;
			canvas.drawLine(X + tip_long / 2, MarkYs[i], X, MarkYs[i],
					linePaint);
		}
	}

	/** 横轴的刻度 */
	public void drawMarkX(Canvas canvas) {
		float[] MarkXs = new float[sectionX + 2];
		float Y = mViewRect.bottom - tail;
		float X = mViewRect.right - tail * 2;
		float O = mViewRect.left + tail;
		int n = (int) ((O_t * flashTime) % flashTime);
		float len = (X - O) / sectionX;
		x_Space = len;
		for (int i = 0; i < MarkXs.length - 1; i++) {
			MarkXs[i] = (float) i * len + O - (float) n * len / 10f;
			canvas.drawLine(MarkXs[i], Y - tip_long / 2, MarkXs[i], Y,
					linePaint);
		}

	}

	public void drawDataX(Canvas canvas) {
		// int[] times = new int[sectionX + 2];
		// for (int i = 0; i < times.length; i++) {
		// times[i] = times.length - i;
		// }

		float[] MarkXs = new float[sectionX + 2];
		float Y = mViewRect.bottom - tip_long / 2;
		float X = mViewRect.right - tail * 2;
		float O = mViewRect.left + tail;
		int n = (int) ((O_t * flashTime) % flashTime);
		float len = (X - O) / sectionX;
		x_Space = len;
		textPaint.setTextSize(getWidth() / 14);
		Paint textPaintNum = new Paint(textPaint);
		textPaintNum.setTextAlign(Align.CENTER);
		int max = MarkXs.length;

		float x = (MarkXs.length) * len + 0 - (float) n * len / 10f;
		canvas.drawText("S", x, Y, textPaintNum);

		for (int i = max - 1; i > 0; i--) {
			MarkXs[i] = (float) i * len + O - (float) n * len / 10f;
			if (i == max - 1)
				continue;
			canvas.drawText(max - 1 - i + "", MarkXs[i], Y, textPaintNum);
		}

	}

	public void drawDataY(Canvas canvas) {
		float[] MarkYs = new float[sectionY];
		float X = mViewRect.left - 0;
		float Y = mViewRect.top + tail * 2;
		float O;
		if (yMin == 0) {
			O = mViewRect.bottom - tail;
		} else {
			O = mViewRect.bottom - tail - tip_long;
		}
		yMin_y = O;
		float len = (O - Y) / sectionY;
		y_Space = len;

		int k = 1;
		double tempMax = mMax;
		if (mMax > 1000 && mMin == 0) {// 如果最大值是1000,最小值是0
			while (tempMax > 1000) {
				k *= 10;
				tempMax = mMax / k;
			}
		}

		float partData = ((mMax - mMin) / (sectionY + 1));
		for (int i = 1; i < MarkYs.length; i++) {
			MarkYs[i] = i * len + Y;
			if (i % 2 == 0) {
				canvas.drawText(Math.round((mMax - partData * (i)) / k * 10)
						/ 10f + "", X, MarkYs[i], textPaint);
			}
		}

	}

	public void drawMaxY(Canvas canvas) {
		StringBuffer sb = new StringBuffer();
		int k = 1;
		double tempMax = mMax;
		if (mMax > 1000 && mMin == 0) {// 如果最大值是1000,最小值是0
			while (tempMax > 1000) {
				k *= 10;
				tempMax = mMax / k;
			}
			if (tempMax == mMax) {
				sb.append(mMax);
			} else {
				sb.append(Math.round(mMax / k * 10) / 10f).append(" X" + k);
			}
		} else {
			sb.append(mMax);
		}

		if (null != this.mUnit && !"".equals(this.mUnit)) {
			sb.append("(");
			sb.append(this.mUnit);
			sb.append(")");
		}
		canvas.drawText(sb.toString(), mViewRect.left, mViewRect.top + tail,
				textPaint);
	}

	public void drawMinY(Canvas canvas) {
		canvas.drawText(mMin + "", mViewRect.left, mViewRect.bottom - tail,
				textPaint);
	}

	/** 将数据转换为坐标点 */
	public PointF getPointfromData(DataPoint dataPoint) {
		PointF p = new PointF();
		float X = (float) (((dataPoint.hen - O_t) / t_space) * x_Space
				+ mViewRect.left + tail);
		float Y = (float) (yMin_y - ((dataPoint.shu - mMin) / (mMax - mMin))
				* sectionY * y_Space);

		if (mMin < 0) {
			System.out.println(mMin);
		}
		if (Y > yMin_y) {
			System.out.println(Y);
		}

		p.x = X;
		p.y = Y;
		return p;
	}

	/** 画出数据曲线 */
	public void drawDatas(Canvas canvas) {
		for (int i = 0; i < mDatas.size(); i++) {
			PointF p1 = getPointfromData(mDatas.get(i));
			if (i == 0) {
				// canvas.drawCircle(p1.x, p1.y, 5, linePaint);
			} else {
				PointF p0 = getPointfromData(mDatas.get(i - 1));
				// canvas.drawCircle(p1.x, p1.y, 5, linePaint);
				if (p0.x >= mViewRect.left + tail) {
					canvas.drawLine(p0.x, p0.y, p1.x, p1.y, dataPaint);
				}
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		mViewRect = new Rect(0, 0, getWidth(), getHeight());
		super.draw(canvas);
		canvas.drawColor(Color.BLACK);

		drawBgline(canvas);
		drawBglineShu(canvas);
		drawMarkX(canvas);
		drawMarkY(canvas);

		drawXYAxis(canvas);

		drawDataX(canvas);
		drawDataY(canvas);
		drawDatas(canvas);
		drawMaxY(canvas);
		drawMinY(canvas);
	}

	public IGraphDataChangeListener getGdcl() {
		return gdcl;
	}

	public void setGdcl(IGraphDataChangeListener gdcl) {
		this.gdcl = gdcl;
		gdcl.onInitData(mDatas, mMax, mMin, mUnit);
	}

	public ArrayList<DataPoint> getmDatas() {
		return mDatas;
	}

	public void setmDatas(ArrayList<DataPoint> mDatas) {
		this.mDatas = mDatas;
	}

	public boolean isLittle() {
		return isLittle;
	}

	public void setLittle(boolean isLittle) {
		this.isLittle = isLittle;
	}
}
