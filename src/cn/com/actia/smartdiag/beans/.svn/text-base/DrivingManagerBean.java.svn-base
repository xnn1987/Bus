package cn.com.actia.smartdiag.beans;

import java.io.Serializable;

public class DrivingManagerBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7231218494515022668L;

	public String mDrivAdviceInfo;
	public String mDrivAnaysisInfo;
	public int mDrivingEnvironment;

	@Override
	public boolean equals(Object o) {
		if (null == o)
			return false;
		if (!(o instanceof DrivingManagerBean))
			return false;
		DrivingManagerBean tempO = (DrivingManagerBean) o;
		if (null == mDrivAdviceInfo) {
			if (null != tempO.mDrivAdviceInfo) {
				return false;
			}
		} else {
			if (!mDrivAdviceInfo.equals(tempO.mDrivAdviceInfo)) {
				return false;
			}
		}
		if (null == mDrivAnaysisInfo) {
			if (null != tempO.mDrivAnaysisInfo) {
				return false;
			}
		} else {
			if (!mDrivAnaysisInfo.equals(tempO.mDrivAnaysisInfo))
				return false;
		}

		if (!(mDrivingEnvironment == tempO.mDrivingEnvironment))
			return false;
		return true;
	}
}
