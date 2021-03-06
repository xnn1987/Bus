package cn.com.actia.smartdiag.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.VehicleDataBean;
import cn.com.actia.smartdiag.constants.Constants;

public class PiezometerBack extends ImageView {
	// private float min;
	// private float max;
	private float nowValue;

	public void setNowValue(float value) {
		this.nowValue = value;
	}

	// private String lable = "%";

	private Paint numPaint;
	private Paint labelpaint;
	private Paint beipaint;
	private Paint markPaint;

	private int markNum = 8;

	private static Path path = new Path();

	// 刻度盘上没格跳动的距离
	private int mDistance;
	// 刻度盘上显示的 倍数
	private float k;

	public VehicleDataBean dataBean;

	public PiezometerBack(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// setMinMax(-64, 64);
		initDatas(4, 9488, "%");
	}

	public PiezometerBack(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PiezometerBack(Context context) {
		this(context, null);
	}

	public void initDatas(float min, float max, String unit) {
		dataBean = new VehicleDataBean(min, max,unit);
		dataBean.unit = unit;
	}

	// public void setMinMax(float min, float max) {
	// int[] values = new int[2];
	// DataFormatUtils.formatMinMaxData(values, min, max);
	//
	// mDistance = (values[1] - values[0]) / 8;
	// k = (float) Math.pow(10, DataFormatUtils.getZeroNum(mDistance));
	// mDistance = (int) (mDistance / k);
	// this.max = values[1] / k;
	// this.min = values[0] / k;
	//
	// System.out.println(mDistance);
	// System.out.println(this.max);
	// System.out.println(this.min);
	// System.out.println(this.k);
	// }

	private void init() {
		numPaint = new Paint();
		numPaint.setColor(getResources().getColor(R.color.clock_green));
		// 圆滑处理
		numPaint.setAntiAlias(true);
		numPaint.setTextAlign(Align.CENTER);
		// numPaint.setTextSize(13);
		numPaint.setTextSize(11);

		labelpaint = new Paint();
		labelpaint.setAntiAlias(true);
		labelpaint.setColor(getResources().getColor(R.color.clock_textcolor));
		labelpaint.setTextAlign(Align.CENTER);
		// labelpaint.setTextSize(29);
		labelpaint.setTextSize(27);

		beipaint = new Paint();
		beipaint.setAntiAlias(true);
		beipaint.setColor(getResources().getColor(R.color.clock_textcolor));
		beipaint.setTextAlign(Align.CENTER);
		// beipaint.setTextSize(19);
		beipaint.setTextSize(17);

		markPaint = new Paint();
		markPaint.setAntiAlias(true);
		markPaint.setColor(getResources().getColor(R.color.clock_green));
		markPaint.setTextAlign(Align.CENTER);
		// markPaint.setTextSize(19);
		markPaint.setTextSize(17);

		path.moveTo(centerPoint.x - 1, 20);
		path.lineTo(centerPoint.x + 1, 20);
		path.lineTo(centerPoint.x + 0, 26);
		path.lineTo(centerPoint.x - 0, 26);
		path.lineTo(centerPoint.x - 1, 20);
	}

	PointF centerPoint = new PointF();

	@Override
	protected void onDraw(Canvas canvas) {
		centerPoint.x = getWidth() / 2;
		centerPoint.y = getHeight() * 3 / 7;
		super.onDraw(canvas);
		if (dataBean == null)
			return;

		// System.out.println(centerPoint.x);
		// System.out.println(centerPoint.y);
		int saveCount = canvas.getSaveCount();
		canvas.save();
		canvas.rotate(-120, centerPoint.x, centerPoint.y);
		markPaint.setColor(Constants.COLOR);
		beipaint.setColor(Constants.COLOR);
		labelpaint.setColor(Constants.COLOR);
		numPaint.setColor(Constants.COLOR);
		for (int i = 0; i <= markNum * 2; i++) {
			if (i % 2 == 0) {

				canvas.drawLine(centerPoint.x, centerPoint.x / 8,
						centerPoint.x, centerPoint.x / 8 + 6, markPaint);
				canvas.drawText((int) (dataBean.datas[i / 2]) + "",
						centerPoint.x, centerPoint.x / 8 + 15, numPaint);
				canvas.rotate(240f / markNum / 2, centerPoint.x, centerPoint.y);
			} else {
				canvas.drawLine(centerPoint.x, centerPoint.x / 8,
						centerPoint.x, centerPoint.x / 8 + 3, markPaint);
				canvas.rotate(240f / markNum / 2, centerPoint.x, centerPoint.y);
			}
		}

		// for (int i = 0; i <= markNum * 2; i++) {
		// if (i % 2 == 0) {
		// canvas.drawLine(centerPoint.x, 20, centerPoint.x, 28, markPaint);
		// canvas.drawText((int) (min + mDistance * (i / 2)) + "",
		// centerPoint.x, 40, numPaint);
		// canvas.rotate(240f / markNum / 2, centerPoint.x, centerPoint.y);
		// } else {
		// canvas.drawLine(centerPoint.x, 20, centerPoint.x, 24, markPaint);
		// canvas.rotate(240f / markNum / 2, centerPoint.x, centerPoint.y);
		// }
		// }
		canvas.restoreToCount(saveCount);
		if (null != dataBean.unit && !dataBean.unit.equals("")) {
			canvas.drawText(dataBean.unit, centerPoint.x,
					centerPoint.y * 3 / 2+labelpaint.descent(), labelpaint);
		}

		if (dataBean.ratio != 1) {
			canvas.drawText("X" + dataBean.ratio, centerPoint.x,
					centerPoint.y * 4 / 3, beipaint);
		}

		canvas.drawText(nowValue / dataBean.ratio + "", centerPoint.x,
				centerPoint.y * 3 / 4, beipaint);

		// canvas.drawText("X" + dataBean.getActualMax() + "``" +
		// dataBean.getActualMin(), centerPoint.x, 180, beipaint);
		// canvas.drawText(value + "", centerPoint.x, 75, numPaint);

	}

	/*
	 * String value;
	 * 
	 * public void setValue(String value) { this.value = value; }
	 */

	/*
	 * public float getMin() { return min; }
	 * 
	 * public void setMin(float min) { this.min = min; }
	 * 
	 * public float getMax() { return max; }
	 * 
	 * public void setMax(float max) { this.max = max; }
	 * 
	 * public float getNowValue() { return nowValue; }
	 * 
	 * public void setNowValue(float nowValue) { this.nowValue = nowValue; }
	 */
	//
	// public String getLable() {
	// return lable;
	// }
	//
	// public void setLable(String lable) {
	// this.lable = lable;
	// }

}
