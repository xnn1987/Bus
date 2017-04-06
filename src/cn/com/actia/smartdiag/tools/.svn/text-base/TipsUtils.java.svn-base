package cn.com.actia.smartdiag.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.com.actia.smartdiag.beans.Categorie;
import cn.com.actia.smartdiag.beans.TipsSecondBean;

public class TipsUtils {
	public static ArrayList<Categorie> initCategoriesMain(InputStream is)
			throws Exception {
		ArrayList<Categorie> categories = new ArrayList<Categorie>();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(is, new TipsMainHandler(categories));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return categories;
	}

	public static TipsSecondBean initCategoriesSecond(InputStream is)
			throws Exception {
		TipsSecondBean bean = new TipsSecondBean();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(is, new TipsSecondeHandler(bean));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bean;
	}

	public static class TipsSecondeHandler extends DefaultHandler {
		public static final String TITLE = "Title";
		public static final String DATA = "Date";
		public static final String AUTHOR = "Author";
		public static final String BODYCONTENT = "BodyContent";

		boolean isTitle = false;
		boolean isDate = false;
		boolean isAuthor = false;
		boolean isBodycontent = false;

		private TipsSecondBean mSecondBean;

		StringBuffer bodyContent;
		StringBuffer simpleBodyContent;

		public TipsSecondeHandler(TipsSecondBean secondBean) {
			this.mSecondBean = secondBean;
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (localName.equals(TITLE)) {
				isTitle = true;
			}
			if (localName.equals(DATA)) {
				isDate = true;
			}
			if (localName.equals(AUTHOR)) {
				isAuthor = true;
			}
			if (localName.equals(BODYCONTENT)) {
				isBodycontent = true;
			}
			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (localName.equals(TITLE)) {
				isTitle = false;
			}
			if (localName.equals(DATA)) {
				isDate = false;
			}
			if (localName.equals(AUTHOR)) {
				isAuthor = false;
			}
			if (localName.equals(BODYCONTENT)) {
				Pattern pattern = Pattern.compile(".*\\w{1}");
				Matcher matcher = pattern.matcher(simpleBodyContent.toString()
						.trim());
				if (matcher.matches()) {
					simpleBodyContent.append("...");
				}
				mSecondBean.simpleBodycontent = simpleBodyContent.toString();
				mSecondBean.bodyContent = bodyContent.toString();
				isBodycontent = false;
			}
			super.endElement(uri, localName, qName);
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (isTitle) {
				mSecondBean.title = new String(ch, start, length);
			}
			if (isBodycontent) {
				if (null == bodyContent) {
					String str = new String(ch, start, length);
					if (null != str)
						bodyContent = new StringBuffer(str);
				} else {
					String str = new String(ch, start, length);
					if (null != str)
						bodyContent.append(new String(ch, start, length));
				}
				if (null == simpleBodyContent) {
					int end = (length - start) >= 80 ? start + 80 : length;
					simpleBodyContent = new StringBuffer(new String(ch, start,
							end));
				}
			}
			if (isDate) {
				mSecondBean.date = new String(ch, start, length);
			}
			if (isAuthor) {
				mSecondBean.author = new String(ch, start, length);
			}
			super.characters(ch, start, length);
		}

	}

	public static class TipsMainHandler extends DefaultHandler {
		public static final String CATEGORIES_ARRAY = "Categories";
		public static final String CATEGORY = "Category";
		public static final String DISPLAYNAME_ARRAY = "DisplayNames";
		public static final String DISPLAYNAME = "DisplayName";
		public static final String NAME = "name";
		public static final String FOLDERPATH = "folderPath";
		public static final String RELATIVE = "relative";
		public static final String DEFAULTLOCALE = "defaultLocale";
		public static final String LOCALE = "locale";

		boolean isCategory = false;
		boolean isDisplayName = false;

		ArrayList<Categorie> mCategories;
		Categorie categorie;
		String local_text;
		String value_text;

		public TipsMainHandler(ArrayList<Categorie> categories) {
			this.mCategories = categories;
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (localName.equals(CATEGORY)) {
				isCategory = true;
				categorie = new Categorie();
				categorie.name = attributes.getValue(NAME);
				if(attributes
						.getValue(RELATIVE).equals("yes")){
					categorie.relative=true;
				}else{
					categorie.relative=false;
				}
				categorie.folderPath = attributes.getValue(FOLDERPATH);
			}
			if (localName.equals(DISPLAYNAME)) {
				local_text = attributes.getValue(LOCALE);
				isDisplayName = true;
			}
			if (localName.equals(DISPLAYNAME_ARRAY)) {
				categorie.displayNames.defaultLocale = attributes
						.getValue(DEFAULTLOCALE);
			}

			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (isDisplayName) {
				value_text = new String(ch, start, length);
			}
			super.characters(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (localName.equals(CATEGORY)) {
				mCategories.add(categorie);
				categorie = null;
				isCategory = false;
			}
			if (localName.equals(DISPLAYNAME)) {
				isDisplayName = false;
				categorie.displayNames.displayName.put(local_text, value_text);
			}
			super.endElement(uri, localName, qName);
		}

	}
}
