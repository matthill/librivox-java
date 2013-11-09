package com.ndu.mobile.daisy.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.ndu.mobile.daisy.providers.exceptions.NDULibraryAuthenticationException;
import com.ndu.mobile.daisy.providers.exceptions.NDULibraryConnectionException;
import com.ndu.mobile.daisy.providers.exceptions.NDULibraryParsingException;

public abstract class RestClientBase
{

	protected OnFileDownloadProgressListener onFileDownloadProgressListener = null;
	protected int zipDownloadPercentComplete = 0;
	

	protected abstract void setRequestProperties(URLConnection uc);
	
	public void setOnFileDownloadProgressListener(OnFileDownloadProgressListener listener)
	{
		onFileDownloadProgressListener = listener;
	}
	

	protected String connectOnce(String urlString) throws NDULibraryAuthenticationException, NDULibraryConnectionException, NDULibraryParsingException
	{
		StringBuilder response = new StringBuilder();
		try
		{
//			URL url = new URL (urlString);
		
			
			InputStream stream = getStreamNoValidation(urlString);

			
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    		stream));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) 
			{
				response.append(inputLine);
				//System.out.println(inputLine);
			}
			in.close();
			
			if (response.length() == 0)
				throw new NDULibraryConnectionException("No data returned from content provider");
		}
		catch (MalformedURLException e)
		{
			// Bad URL sent in
			//System.out.println(e.getMessage());
			throw new NDULibraryParsingException(e.getMessage());
		}
		catch (IOException e)
		{
			// Couldn't connect to URL
			//System.out.println(e.getMessage());
			throw new NDULibraryConnectionException(e.getMessage());
		}

		
		return response.toString();
	}
	
	

	protected URLConnection getFileDownloadConnection(String urlString) throws NDULibraryConnectionException, NDULibraryParsingException
	{

	    try {
	    	//urlString = String.format("%s?api_key=%s", urlString, apikey);
			URL url = new URL (urlString);

			URLConnection uc;// = url.openConnection();
	        if (urlString.toLowerCase().startsWith("https://"))
	        {
	        	uc = (HttpsURLConnection) url.openConnection();
	        	setRequestProperties(uc);
	        }
	        else
	        {
	        	uc =  url.openConnection();
	        	setRequestProperties(uc);
	        }
			//setRequestProperties(uc);
			
			return uc;
		}
		catch (MalformedURLException e)
		{
			// Bad URL sent in
			//System.out.println(e.getMessage());
			throw new NDULibraryParsingException(e.getMessage());
		}
		catch (IOException e)
		{
			// Couldn't connect to URL
			//System.out.println(e.getMessage());
			throw new NDULibraryConnectionException(e.getMessage());
		}
	}
	
	
	// Connecting to an AL appliance throws an exception since the cert is
	// not validated.  This function overrides that behavior.
	protected InputStream getStreamNoValidation(String _URL) throws IOException, NDULibraryAuthenticationException
	{
		final int timeoutMS = 35000;
		
        URL ArxURL = new URL(_URL);
        //_FakeX509TrustManager.allowAllSSL(); 
        
        URLConnection con;
        if (_URL.toLowerCase().startsWith("https://"))
        {
        	con = (HttpsURLConnection) ArxURL.openConnection();
        	setRequestProperties(con);
        }
        else
        {
			con =  ArxURL.openConnection();
        	setRequestProperties(con);
        }

		//con.setRequestMethod("GET"); 
	     con.setConnectTimeout(timeoutMS);
	     con.setReadTimeout(timeoutMS);
		con.setUseCaches (false); 

		InputStream newconn = con.getInputStream();

		int statusCode = ((HttpURLConnection) con).getResponseCode(); 
		
		if ((statusCode == 403) || (statusCode == 500)) // 500 errors also show up when the user is unauthorized... weird.
		{
			throw new NDULibraryAuthenticationException("This user is not authorized");
		}

		return newconn;


	}
}
