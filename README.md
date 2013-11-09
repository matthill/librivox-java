librivox-java
=============

Librivox Java is an open-source (Apache license) interface for Librivox web service API.  

Getting started is easy, the following code snippet downloads a listing of Librivox books (starting with "Count of") and gets the RSS details for the first in the list.

	LVoxRestClient client = new LVoxRestClient();

	try
	{
		
		List<LVoxBook> books = client.searchByTitle("Count of ");
		
		for (LVoxBook book : books)
		{
			System.out.println(book.toString());
		}

		LVoxRSS rss = client.getBookDetails(books.get(0).getUrlRss());
		
		for (LVoxChapter chapter : rss.getChapters())
		{
			System.out.println(chapter.getTitle() + " -- " + chapter.getFileSize());
			System.out.println("  --  " + chapter.getUrl());
		}

	}
	catch (NDULibraryConnectionException e)
	{
		e.printStackTrace();
	}
	catch (NDULibraryParsingException e)
	{
		e.printStackTrace();
	}
