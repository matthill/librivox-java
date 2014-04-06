package com.ndu.mobile.daisy.providers.librivox;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ndu.mobile.daisy.providers.ParsedObject;

public class LVoxRSS extends ParsedObject
{
    private static final String BASE_DOWNLOAD_URL = "https://archive.org/download/";

	
	private List<LVoxChapter> chapters = new ArrayList<LVoxChapter>(30);
	
	public LVoxRSS(String bookID, String jsonData) throws IOException, ParseException
	{
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(jsonData);

        JSONObject files = (JSONObject) root.get("files");

        for (Object obj : files.keySet().toArray())
        {
            String key = (String) obj;

            JSONObject file = (JSONObject) files.get(key);

            if (file.containsKey("source") && ((String)file.get("source")).equalsIgnoreCase("original") &&
                file.containsKey("title") && file.containsKey("track") && file.containsKey("size"))
            {
                String track = (String) file.get("track");
                if (track.contains("/"))
                    track = track.substring(0, track.indexOf("/"));

                String title = (String) file.get("title");
                String url =  BASE_DOWNLOAD_URL + bookID + key;
                String fileSize = (String) file.get("size");
                LVoxChapter chapter = new LVoxChapter(Integer.valueOf(track), title, url, Long.valueOf(fileSize));

                chapters.add(chapter);
            }
        }

        Comparator<LVoxChapter> chapterSorter = new Comparator<LVoxChapter>() {
            @Override
            public int compare(LVoxChapter lVoxChapter, LVoxChapter lVoxChapter2) {
                if (lVoxChapter.getTrackNumber() < lVoxChapter2.getTrackNumber())
                    return -1;
                else if (lVoxChapter.getTrackNumber() < lVoxChapter2.getTrackNumber())
                    return 0;
                else
                    return 1;
            }
        };

        Collections.sort(chapters, chapterSorter);

	}
	


	public List<LVoxChapter> getChapters()
	{
		return chapters;
	}
}
