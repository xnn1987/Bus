package cn.com.actia.smartdiag.range;

/*
 * Author: poper Email:xut@bluemobi.cn
 * Created Date:2013-3-4
 * Copyright @ 2013 BU
 * Description: ÀàÃèÊö
 *
 * History:
 */
public class RangeBean {
	private int min;
	private int max;

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	@Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof RangeBean)) {
			return false;
		}
		RangeBean bean = (RangeBean) o;
		if (bean.max != this.max)
			return false;
		if (bean.min != this.min)
			return false;
		return true;
	}
}
