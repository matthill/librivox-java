package com.ndu.mobile.daisy.providers;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ParsedObject
{
	protected static final int UNDEFINED = -1;

	protected int parseNumVal(XmlPullParser xpp) throws IOException, XmlPullParserException
	{
		
		if (xpp.next() == XmlPullParser.TEXT)
		{
			try
			{
				return Integer.parseInt(xpp.getText());
			}
			catch (NumberFormatException e)
			{
			}
		}

		return UNDEFINED;
	}

	protected String parseStringVal(XmlPullParser xpp) throws IOException, XmlPullParserException
	{
		
		if (xpp.next() == XmlPullParser.TEXT)
		{
			return xpp.getText();

		}

		return "";
	}
}
