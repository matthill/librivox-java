package com.ndu.mobile.daisy.providers.librivox;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ndu.mobile.daisy.providers.RestClientBase;
import com.ndu.mobile.daisy.providers.exceptions.NDULibraryAuthenticationException;
import com.ndu.mobile.daisy.providers.exceptions.NDULibraryConnectionException;
import com.ndu.mobile.daisy.providers.exceptions.NDULibraryParsingException;

public class LVoxRestClient extends RestClientBase
{

	private static final int MAX_RETRIES = 3;
	private static final int ENTRIES_PER_PAGE = 50;
	

	public List<LVoxBook> getLatest(int pageNumber) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		long curTime = Calendar.getInstance().getTime().getTime() / 1000;
		long oneWeekAgo = curTime - 604800; // One week in Seconds;
		
		int offset = (pageNumber - 1) * ENTRIES_PER_PAGE;
		if (offset < 0)
			offset = 0;
		
		String resp = connect(String.format("http://librivox.org/api/feed/audiobooks?since=%d&limit=%d&offset=%d&extended=1", oneWeekAgo, ENTRIES_PER_PAGE, offset ));

		return parseXML(resp);
	}
	
	public List<LVoxBook> searchByTitle(String title) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		try
		{
			String requestURL = String.format("http://librivox.org/api/feed/audiobooks/title/^%s?extended=1", URLEncoder.encode(title, "UTF-8"));
			requestURL = requestURL.replace("^", "%5E");
			requestURL = requestURL.replace("+", "%20");

			String resp = connect(requestURL);
			
			return parseXML(resp);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}
	}

	public List<LVoxBook> searchByAuthor(String author) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		try
		{
			String requestURL = String.format("http://librivox.org/api/feed/audiobooks/title/^%s?extended=1", URLEncoder.encode(author, "UTF-8"));
			requestURL = requestURL.replace("^", "%5E");
			requestURL = requestURL.replace("+", "%20");
			
			String resp = connect(requestURL);
			
			return parseXML(resp);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}
	}
	
	public LVoxRSS getBookDetails(String rssUrl) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		try
		{
			String resp = connect(rssUrl);
	
			return new LVoxRSS(resp);
		}
		catch (XmlPullParserException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}
		catch (IOException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}		
	}
	
	public List<String> getCategories()
	{
		List<String> categories = new ArrayList<String>();
		
		for(String category : Constants.Categories)
			categories.add(category);

		return categories;
	}
	public List<LVoxBook> getBooksInCategory(String category) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		// TODO: Currently the API doesn't work.  Requesting clarification from Librivox.
		try
		{
			String resp = connect(String.format("https://catalog.librivox.org/search_xml.php?extended=1&genre=%s", URLEncoder.encode(category, "UTF-8") ));
			
			return parseXML(resp);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}
	}
	
//	public void downloadBook(String zipFileURL, String saveLocation) throws NDULibraryConnectionException, NDULibraryParsingException
//	{
//		downloadFile(zipFileURL, saveLocation);
//	}
	
	public URLConnection getDownloadChapterConnection(String url) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		//this.downloadFile(dl_uid, url, filePath);
		return getFileDownloadConnection(url);
	}
	
	private List<LVoxBook> parseXML(String xmlData) throws NDULibraryParsingException
	{
		List<LVoxBook> results = new ArrayList<LVoxBook>(50);
		
		try
		{
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	
	        xpp.setInput( new StringReader ( xmlData ) );
	        int eventType = xpp.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
		         if(eventType == XmlPullParser.START_DOCUMENT) {
		             //System.out.println("Start document");
		         }
	         	 if(eventType == XmlPullParser.START_TAG) {
	         		 if (xpp.getName().equalsIgnoreCase("book"))
	         		 {
	         			LVoxBook book = new LVoxBook(xpp);
	         			
	         			// Don't add the book if the zip file URL isn't filled out
	         			if (book.getId() != LVoxBook.UNKNOWN)
	         				results.add(book);
	         		 }
		         }
				 if(eventType == XmlPullParser.END_TAG) {
		             //System.out.println("End tag "+xpp.getName());
		         }
				 if(eventType == XmlPullParser.TEXT) {
		             //System.out.println("Text "+xpp.getText());
		         }
		         eventType = xpp.next();
	        }
	        //System.out.println("End document");
		}
		catch (XmlPullParserException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}
		catch (IOException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}
		
		return results;
	}
	
	private String connect(String urlString) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		for (int i = 0; i < MAX_RETRIES; i++)
		{
			try
			{
				String retVal = connectOnce(urlString);
				return retVal;
			}
			catch (NDULibraryConnectionException e)
			{
				if (i == MAX_RETRIES - 1)
					throw e;
			}
			catch (NDULibraryAuthenticationException e)
			{
				// Should never happen for Librivox...
				throw new NDULibraryConnectionException(e.getMessage());
			}
		}
		
		return ""; // Should never get here...
	}
	
	
	

	@Override
	protected void setRequestProperties(URLConnection uc)
	{
		// Nothing to do for Librivox here
		uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
		uc.setRequestProperty("Cache-Control", "max-age=0");
		uc.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	}
}
