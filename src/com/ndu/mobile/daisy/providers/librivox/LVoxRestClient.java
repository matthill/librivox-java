package com.ndu.mobile.daisy.providers.librivox;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ndu.mobile.daisy.providers.RestClientBase;
import com.ndu.mobile.daisy.providers.exceptions.NDULibraryAuthenticationException;
import com.ndu.mobile.daisy.providers.exceptions.NDULibraryConnectionException;
import com.ndu.mobile.daisy.providers.exceptions.NDULibraryParsingException;

public class LVoxRestClient extends RestClientBase
{

    private static final LibrivoxField[] DEFAULT_FIELDS = {
            LibrivoxField.AVG_RATING,
            LibrivoxField.CREATOR,
            LibrivoxField.DESCRIPTION,
            LibrivoxField.DOWNLOADS,
            LibrivoxField.FORMAT,
            LibrivoxField.IDENTIFIER,
            LibrivoxField.MEDIA_TYPE,
            LibrivoxField.PUBLICDATE,
            LibrivoxField.SUBJECT,
            LibrivoxField.TITLE
    };

    private static final String SEARCH_QUERY_PREFIX="collection:librivoxaudio";


    private static final String LV_SEARCH_QUERY = "http://archive.org/advancedsearch.php?";

    private static final String LV_DETAILS_QUERY = "http://archive.org/details/";
    private static final String LV_DETAILS_QUERY_POSTFIX = "?output=json";

	private static final int MAX_RETRIES = 3;
	private static final int ENTRIES_PER_PAGE = 100;

    private String getSearchUrl(String query, SortOption sort, int pageNumber)
    {
        StringBuilder queryString = new StringBuilder();

        queryString.append(LV_SEARCH_QUERY);

        for (LibrivoxField field : DEFAULT_FIELDS)
        {
            queryString.append("fl%5B%5D=");    // fl[]=
            queryString.append(field.toString());
            queryString.append("&");
        }

        queryString.append("sort%5B%5D=");  // sort[]=
        queryString.append(sort.toString());
        queryString.append("&sort%5B%5D=&sort%5B%5D=&");    // &sort[]=&sort[]=&

        queryString.append("rows=");
        queryString.append(ENTRIES_PER_PAGE);

        queryString.append("&page=");
        queryString.append(pageNumber);

        queryString.append("&output=xml");

        queryString.append("&q=");
        queryString.append(query);

        return queryString.toString();

    }

	public List<LVoxBook> getLatest(int pageNumber) throws NDULibraryConnectionException, NDULibraryParsingException
	{

		String resp = connect(getSearchUrl(SEARCH_QUERY_PREFIX, SortOption.DATE, pageNumber));

		return parseXML(resp);
	}
	
	public List<LVoxBook> searchByTitle(String title) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		try
		{
            String query = URLEncoder.encode(SEARCH_QUERY_PREFIX + " AND " + LibrivoxField.TITLE + ":" + title, "UTF-8");

			String resp = connect(getSearchUrl(query, SortOption.POPULARITY, 1));
			
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
            String query = URLEncoder.encode(SEARCH_QUERY_PREFIX + " AND " + LibrivoxField.CREATOR + ":" + author, "UTF-8");

            String resp = connect(getSearchUrl(query, SortOption.POPULARITY, 1));

            return parseXML(resp);

		}
		catch (UnsupportedEncodingException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}
	}
	
	public LVoxRSS getBookDetails(String bookID) throws NDULibraryConnectionException, NDULibraryParsingException
	{
		try
		{
            String url = LV_DETAILS_QUERY + bookID + LV_DETAILS_QUERY_POSTFIX;
			String resp = connect(url);
	
			return new LVoxRSS(bookID, resp);
		}
		catch (ParseException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}
		catch (IOException e)
		{
			throw new NDULibraryParsingException(e.getMessage());
		}		
	}
	

	
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
	         		 if (xpp.getName().equalsIgnoreCase("doc"))
	         		 {
	         			LVoxBook book = new LVoxBook(xpp);
	         			
	         			// Don't add the book if the zip file URL isn't filled out
	         			if (book.getId() != null && book.getId().isEmpty() == false)
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
