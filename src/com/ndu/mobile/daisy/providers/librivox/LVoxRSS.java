package com.ndu.mobile.daisy.providers.librivox;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ndu.mobile.daisy.providers.ParsedObject;

public class LVoxRSS extends ParsedObject
{
	private String title;

	private String description;
	private String genre;
	private String language;
	
	private List<LVoxChapter> chapters;
	
	public LVoxRSS(String xmlData) throws IOException, XmlPullParserException
	{
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader ( xmlData ) );
        
		chapters = new ArrayList<LVoxChapter>();
		
        int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

	        eventType = xpp.getEventType();
        	 if(eventType == XmlPullParser.START_TAG) {

	        	 if (xpp.getName().equalsIgnoreCase("title"))
	        	 {
	        		 title = parseStringVal(xpp).trim();
 
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("genre"))
	        	 {
	        		 genre = parseStringVal(xpp).trim();
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("description"))
	        	 {
	        		 description = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("language"))
	        	 {
	        		 language = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("item"))
	        	 {
	        		 LVoxChapter chapter = new LVoxChapter(xpp);
	        		 chapters.add(chapter);
	        	 }
	         }
			 if(eventType == XmlPullParser.END_TAG) {
	             if (xpp.getName().equalsIgnoreCase("channel"))
	             {
	            	 break;
	             } 
	             else if (xpp.getName().equalsIgnoreCase("rss"))
	             {
	            	 break;
	             }
	         }
//			 if(eventType == XmlPullParser.TEXT) {
//	             //System.out.println("Text "+xpp.getText());
//	         }
	         eventType = xpp.next();
       }
	}
	

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public String getGenre()
	{
		return genre;
	}

	public String getLanguage()
	{
		return language;
	}

	public List<LVoxChapter> getChapters()
	{
		return chapters;
	}
}
