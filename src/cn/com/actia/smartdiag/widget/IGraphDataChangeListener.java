package cn.com.actia.smartdiag.widget;

import java.util.ArrayList;

/*
 * Author: poper Email:xut@bluemobi.cn
 * Created Date:2013-3-18
 * Copyright @ 2013 BU
 * Description: ¿‡√Ë ˆ
 *
 * History:
 */
public interface IGraphDataChangeListener {
	public void onInitData(ArrayList<DataPoint> datas, float max,
			float min, String unit);

	public void onAddData(DataPoint dp);
	
	public void onSetOT(double ot);
}
