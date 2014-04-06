package com.ndu.mobile.daisy.providers.librivox;

import com.ndu.mobile.daisy.providers.ParsedObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LVoxBook extends ParsedObject implements Serializable 
{
	
	private static final long serialVersionUID = -5021054789409657400L;

	
	private String title;
	private List<String> authors = new ArrayList<String>(5);
	private String description;
	private String id = "";




	public LVoxBook(XmlPullParser xpp) throws IOException, XmlPullParserException
	{
        int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

	        eventType = xpp.getEventType();
        	 if(eventType == XmlPullParser.START_TAG) {

	        	 if (xpp.getName().equalsIgnoreCase("str") && xpp.getAttributeCount() > 0&& xpp.getAttributeValue(0).equals("title"))
	        	 {
	        		 title = parseStringVal(xpp);	        		 
	        		 
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("arr") && xpp.getAttributeCount() > 0&& xpp.getAttributeValue(0).equals("creator"))
	        	 {
	        		 parseAuthors(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("str") && xpp.getAttributeCount() > 0 && xpp.getAttributeValue(0).equalsIgnoreCase("description"))
	        	 {
	        		 description = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("str") && xpp.getAttributeCount() > 0 && xpp.getAttributeValue(0).equalsIgnoreCase("identifier"))
	        	 {
	        		 id = parseStringVal(xpp);
	        	 }
	         }
			 if(eventType == XmlPullParser.END_TAG) {
	             if (xpp.getName().equalsIgnoreCase("doc"))
	             {
	            	 break;
	             }
	         }

	         eventType = xpp.next();
       }
	}

	private void parseAuthors(XmlPullParser xpp) throws IOException, XmlPullParserException
	{
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

	        eventType = xpp.getEventType();
        	 if(eventType == XmlPullParser.START_TAG) {

	        	 if (xpp.getName().equalsIgnoreCase("str"))
	        	 {
	        		 authors.add(xpp.getText());
	        	 }
	         }
			 if(eventType == XmlPullParser.END_TAG) {
	             if (xpp.getName().equalsIgnoreCase("arr"))
	             {
	            	 break;
	             }
	         }

	         eventType = xpp.next();
       }
	}
	
	@Override
	public String toString()
	{
		return "(" + getId() + ") " + getTitle();
	}
	

	public String getTitle()
	{
		return title;
	}
	public List<String> getAuthors()
	{
		return authors;
	}
	public String getDescription()
	{
		return description;
	}
	public String getId()
	{
		return id;
	}



}
