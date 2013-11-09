package com.ndu.mobile.daisy.providers.librivox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.ndu.mobile.daisy.providers.exceptions.NDULibraryParsingException;

public class Daisy2Generator
{
	public static final String NCC_FILE = "ncc.html";
	public static final String MASTER_FILE = "master.smil";
	public static final String SMIL_FILE = "Book.smil";
	
	
	public static final String AUDIO_DIR = "audio/";
	
	public static void Generate(String title, String author, String basedir, LVoxRSS rss) throws NDULibraryParsingException
	{
		writeNCC(title, author, basedir, rss);
		writeMaster(title, basedir);
		writeSmil(title, author, basedir, rss);
		

	}
	
	private static void writeNCC(String title, String author, String basedir, LVoxRSS rss) throws NDULibraryParsingException
	{
		File output = new File(basedir, NCC_FILE);
	    try {
			XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
			XmlSerializer serializer = parserFactory.newSerializer();
			
			FileWriter writer = new FileWriter(output);
		    //StringWriter writer = new StringWriter();
		    
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "html");
	        serializer.attribute("", "xmlns", "http://www.w3.org/1999/xhtml");

		        serializer.startTag("", "head");
		        	serializer.startTag("", "title");
		            serializer.text(title);
			        serializer.endTag("", "title");
			        
			        writeMeta(serializer, "dc:title", title);
			        writeMeta(serializer, "dc:creator", author);
		        serializer.endTag("", "head");

		        serializer.startTag("", "body");
		        
		        for (int i = 0; i < rss.getChapters().size(); i++)
		        {
		        	writeNavEntry(serializer, i, rss.getChapters().get(i));
		        }
		        
		        serializer.endTag("", "body");

		        
	        serializer.endTag("", "html");
	        serializer.endDocument();
	        
	        writer.close();
	        //return true;
	    } catch (Exception e) {
	        throw new NDULibraryParsingException(e.getMessage());
	    } 
	}
	
	private static void writeMeta(XmlSerializer serializer, String name, String content) throws IOException
	{
        serializer.startTag("", "meta" );
        serializer.attribute("", "name", name);
        serializer.attribute("", "content", content);
        serializer.endTag("", "meta");
	}
	private static void writeNavEntry(XmlSerializer serializer, int index, LVoxChapter chapter) throws IOException
	{
		String strIndex = String.valueOf(index);
        serializer.startTag("", "h1" );
        	serializer.attribute("", "id", "entry-" + strIndex);
        	
        	serializer.startTag("", "a" );
	        	serializer.attribute("", "href", "Book.smil#dtb" + strIndex);
	        	serializer.attribute("", "id", "link" + strIndex);
	            serializer.text(chapter.getTitle());
            serializer.endTag("", "a");
            
        serializer.endTag("", "h1");
	}
	

	private static void writeMaster(String title, String basedir) throws NDULibraryParsingException
	{
		File output = new File(basedir, MASTER_FILE);
	    try {
			XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
			XmlSerializer serializer = parserFactory.newSerializer();
			
			FileWriter writer = new FileWriter(output);
		    //StringWriter writer = new StringWriter();
		    
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "smil");

		        serializer.startTag("", "head");
			        
			        writeMeta(serializer, "dc:title", title);
			        writeMeta(serializer, "dc:format", "Daisy 2.02");
			        writeMeta(serializer, "ncc:generator", "Darwin Reader");
		        serializer.endTag("", "head");

		        serializer.startTag("", "body");
		        
			        serializer.startTag("", "ref");
			        	serializer.attribute("", "title", title);
			        	serializer.attribute("", "src", SMIL_FILE);
			        	serializer.attribute("", "id", "masterdtb111");
			        serializer.endTag("", "ref");
		        
		        serializer.endTag("", "body");

		        
	        serializer.endTag("", "smil");
	        serializer.endDocument();
	        
	        writer.close();
	        //return true;
	    } catch (Exception e) {
	        throw new NDULibraryParsingException(e.getMessage());
	    } 
	}
	
	private static void writeSmil(String title, String author, String basedir, LVoxRSS rss) throws NDULibraryParsingException
	{
		File output = new File(basedir, SMIL_FILE);
		
	    try {
			XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
			XmlSerializer serializer = parserFactory.newSerializer();
			
			FileWriter writer = new FileWriter(output);
		    //StringWriter writer = new StringWriter();
		    
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "smil");

		        serializer.startTag("", "head");
		        writeMeta(serializer, "dc:format", "Daisy 2.02");
		        writeMeta(serializer, "generator", "Darwin Reader");
		        serializer.endTag("", "head");

		        serializer.startTag("", "body");
		        serializer.startTag("", "seq");
		        
		        for (int i = 0; i < rss.getChapters().size(); i++)
		        {
		        	writePar(serializer, basedir, i, rss.getChapters().get(i));
		        }

		        serializer.endTag("", "seq");
		        serializer.endTag("", "body");

		        
	        serializer.endTag("", "smil");
	        serializer.endDocument();
	        
	        writer.close();
	        //return true;
	    } catch (Exception e) {
	        throw new NDULibraryParsingException(e.getMessage());
	    } 
	}
	
	private static void writePar(XmlSerializer serializer, String basedir, int index, LVoxChapter chapter) throws IOException
	{
		String indexStr = String.valueOf(index);
		File mp3File = new File(basedir, AUDIO_DIR + indexStr + ".mp3");
		double duration = calculateDuration(mp3File);
		
        serializer.startTag("", "par");
        
	        serializer.startTag("", "audio");
        	serializer.attribute("", "src", "audio/" + indexStr + ".mp3");
        	serializer.attribute("", "clip-begin", "npt=0.00s");
        	
        	serializer.attribute("", "clip-end", String.format(Locale.US, "npt=%.3fs", duration) );
        	serializer.attribute("", "id", "dtb" + index);

	        serializer.endTag("", "audio");
	        
        serializer.endTag("", "par");
	}
	private static double calculateDuration(File mp3File)
	{
		// GetDuration = Int(((LOF(intNumFile) * 8) / intBitrate) / 1000)
		// assuming 64 kbps on all the MP3s from Librivox
		final double BIT_RATE = 64;
		
		double duration = mp3File.length() * 8 / BIT_RATE / 1000;
		
		return duration;
	}
	
}
