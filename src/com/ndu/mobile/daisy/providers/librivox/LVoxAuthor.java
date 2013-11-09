package com.ndu.mobile.daisy.providers.librivox;

import java.io.IOException;
import java.io.Serializable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.ndu.mobile.daisy.providers.ParsedObject;

public class LVoxAuthor extends ParsedObject implements Serializable 
{

	private static final long serialVersionUID = 2967115273723222618L;

	
	private int id;
	private String firstName;
	private String lastName;
	private int yearBirth;
	private int yearDeath;

	public LVoxAuthor(XmlPullParser xpp) throws IOException, XmlPullParserException
	{
        int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

	        eventType = xpp.getEventType();
        	 if(eventType == XmlPullParser.START_TAG) {

	        	 if (xpp.getName().equalsIgnoreCase("id"))
	        	 {
	        		 id = parseNumVal(xpp);	        		 
	        		 
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("first_name"))
	        	 {
	        		 firstName = parseStringVal(xpp).trim();
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("last_name"))
	        	 {
	        		 lastName = parseStringVal(xpp);
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("dob"))
	        	 {
	        		 yearBirth = parseNumVal(xpp);	      
	        	 }
	        	 else if (xpp.getName().equalsIgnoreCase("dod"))
	        	 {
	        		 yearDeath = parseNumVal(xpp);	      
	        	 }
	        	 
	         }
			 if(eventType == XmlPullParser.END_TAG) {
	             if (xpp.getName().equalsIgnoreCase("author"))
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
		return "(" + id + ") " + firstName + " " + lastName;
	}


	public int getId()
	{
		return id;
	}


	public String getFirstName()
	{
		return firstName;
	}


	public String getLastName()
	{
		return lastName;
	}


	public int getYearBirth()
	{
		return yearBirth;
	}


	public int getYearDeath()
	{
		return yearDeath;
	}
	
	
}
