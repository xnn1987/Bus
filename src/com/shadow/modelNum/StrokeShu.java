package com.shadow.modelNum;

import cn.com.actia.smartdiag.constants.Constants;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

/** »­ÊúÏß */
public class StrokeShu extends NumStroke {

	public StrokeShu(float size) {
		super(size);
	}

	public StrokeShu(float x, float y, float size) {
		super(x, y, size);
	}

	public StrokeShu(PointF location, float size) {
		super(location, size);
	}
	
	public StrokeShu(float x, float y, float size, Paint paint) {
		this(x, y, size);
		this.paint = paint;
	}

	PointF[] style_half = new PointF[] { new PointF(getSize(), getSize()),
			new PointF(2 * getSize(), 2 * getSize()),
			new PointF(2 * getSize(), 12 * getSize()),
			new PointF(getSize(), 13 * getSize()), new PointF(0, 12 * getSize()),
			new PointF(0, 2 * getSize()) };
	
	private Paint paint;
	

	@Override
	public void drawSelf(Canvas canvas) {
		Path path = new Path();
		if (null == paint) {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Constants.COLOR);
		}
		for (int i = 0; i < style_half.length; i++) {
			if (i == 0) {
				path.moveTo(getLocation().x + style_half[i].x, getLocation().y
						+ style_half[i].y + 1);
			} else {
				path.lineTo(getLocation().x + style_half[i].x, getLocation().y
						+ style_half[i].y + 1);
			}
		}
		
		canvas.drawPath(path, paint);
	}

}