package cn.com.actia.smartdiag.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class NeedBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1560557346927361819L;
	public String EcuFamily;
	public ArrayList<String> Categories;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Family:" + EcuFamily + "\n\r");
		for (String str : Categories) {
			sb.append("Categories:" + str).append("\n\r");
		}
		return sb.toString();
	}
}
