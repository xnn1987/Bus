package com.shadow.modelNum;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

public abstract class NumStroke {
	PointF[] Style_half;
	/** ������̵ĵı�����4��1 */
	private float size;

	int color = Color.WHITE;

	PointF location;
	State_Position position;


	public NumStroke(float size) {
		setSize(size);
	}

	public NumStroke(float x, float y, float size) {
		location = new PointF(x, y);
		setSize(size);
	}

	public NumStroke(PointF location, float size) {
		this.location = location;
		setSize(size);
	}

	public abstract void drawSelf(Canvas canvas);

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = (size - 2) / 16;
	}

	public PointF getLocation() {
		if (null == location)
			location = new PointF(0, 0);
		return location;
	}

	public void setLocation(PointF location) {
		this.location = location;
	}

	public enum State_Position {
		/** ����λ�� */
		STATE_LOCATION_TOP,
		/** �м�λ�� */
		STATE_LOCATION_CENTER,
		/** �²�λ�� */
		STATE_LOCATION_BOTTOM,
		/** ����λ�� */
		STATE_LOCATION_LEFT,
		/** �м�λ�� */
		STATE_LOCATION_RIGHT;
	}

}
