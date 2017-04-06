package cn.com.actia.smartdiag.range;

/*
 * Author: poper Email:xut@bluemobi.cn
 * Created Date:2013-3-4
 * Copyright @ 2013 BU
 * Description: ¿‡√Ë ˆ
 *
 * History:
 */
public class ODBRangeBean extends RangeBean {
	private String parameterName;
	private String parameterType;

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}
		if (!(o instanceof OtherRangeBean)) {
			return false;
		}
		ODBRangeBean bean = (ODBRangeBean) o;
		if (!parameterName.equals(bean.parameterName)) {
			return false;
		}
		if (!parameterType.equals(bean.parameterType)) {
			return false;
		}
		return true;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
}
