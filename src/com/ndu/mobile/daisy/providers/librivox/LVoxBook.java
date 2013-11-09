package com.ndu.mobile.daisy.providers.librivox;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.ndu.mobile.daisy.providers.ParsedObject;

public class LVoxBook extends ParsedObject implements Serializable 
{
	public static final int UNKNOWN = -1;
	
	private static final long serialVersionUID = -5021054789409657400L;

	
	private String title;
	private List<LVoxAuthor> authors = new ArrayList<LVoxAuthor>(5);

	private String description;
	private String archiveOrgURL;
	private String zipfile;
	private int NumberOfSections = UNKNOWN;
	private String url;
	private String urlRss;
	private int id = UNKNOWN;
	private String totaltime;
	private long totaltimeseconds;




	public LVoxBook(XmlPullParser xpp) throws IOException, XmlPullParserException
	{
        int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

	        eventType = xpp.getEventType();
        	 if(eventType == XmlPullParser.START_TAG) {

	        	 if (xpp.getName().equalsIgnoreCase("title"))
	        	 {
	        		 title = parseStringVal(xpp);	        		 
	        		 
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("authors"))
	        	 {
	        		 parseAuthors(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("description"))
	        	 {
	        		 description = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("url_iarchive"))
	        	 {
	        		 archiveOrgURL = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("sections"))
	        	 {
	        		 NumberOfSections = parseNumVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("url_librivox"))
	        	 {
	        		 url = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("url_rss"))
	        	 {
	        		 urlRss = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("url_zip_file"))
	        	 {
	        		 zipfile = parseStringVal(xpp).trim();
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("id"))
	        	 {
	        		 id = parseNumVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("totaltime"))
	        	 {
	        		 totaltime = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("totaltimesecs"))
	        	 {
	        		 totaltimeseconds = parseNumVal(xpp);
	        	 }
	         }
			 if(eventType == XmlPullParser.END_TAG) {
	             if (xpp.getName().equalsIgnoreCase("book"))
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

	private void parseAuthors(XmlPullParser xpp) throws IOException, XmlPullParserException
	{
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

	        eventType = xpp.getEventType();
        	 if(eventType == XmlPullParser.START_TAG) {

	        	 if (xpp.getName().equalsIgnoreCase("author"))
	        	 {
	        		 authors.add(new LVoxAuthor(xpp));       		 
	        	 }
	         }
			 if(eventType == XmlPullParser.END_TAG) {
	             if (xpp.getName().equalsIgnoreCase("authors"))
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
	public List<LVoxAuthor> getAuthors()
	{
		return authors;
	}



	public String getDescription()
	{
		return description;
	}



	public String getArchiveOrgURL()
	{
		return archiveOrgURL;
	}



	public String getZipfile()
	{
		return zipfile;
	}





	public int getNumberOfSections()
	{
		return NumberOfSections;
	}



	public String getUrl()
	{
		return url;
	}

	
	public int getId()
	{
		return id;
	}


	public String getTotaltime()
	{
		return totaltime;
	}



	public long getTotaltimeseconds()
	{
		return totaltimeseconds;
	}

	public String getUrlRss()
	{
		return urlRss;
	}


}
