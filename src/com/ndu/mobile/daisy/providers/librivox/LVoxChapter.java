package com.ndu.mobile.daisy.providers.librivox;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.ndu.mobile.daisy.providers.ParsedObject;

public class LVoxChapter extends ParsedObject
{
    private int trackNumber;
	private String title;
	private String url = "";
	private long fileSize;
	

	public LVoxChapter(int trackNumber, String title, String url, long filesize)
	{
        this.trackNumber = trackNumber;
        this.title = title;
        this.url = url;
        this.fileSize = filesize;
	}

    public int getTrackNumber() { return trackNumber; }

	public String getTitle()
	{
		return title;
	}


	public String getUrl()
	{
		return url;
	}


	public long getFileSize()
	{
		return fileSize;
	}
}
