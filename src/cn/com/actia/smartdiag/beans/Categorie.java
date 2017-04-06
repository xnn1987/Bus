package cn.com.actia.smartdiag.beans;

import java.util.HashMap;
import java.util.Map;

public class Categorie {
	public String name;
	public String folderPath;
	public boolean relative;

	public DisplayNames displayNames;

	public Categorie() {
		displayNames = new DisplayNames();
	}

	public class DisplayNames {
		public String defaultLocale;
		public Map<String, String> displayName;

		public DisplayNames() {
			displayName = new HashMap<String, String>();
		}
	}
}
