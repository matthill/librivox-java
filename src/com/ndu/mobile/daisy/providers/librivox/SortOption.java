package com.ndu.mobile.daisy.providers.librivox;

public enum SortOption {
    TITLE       { @Override  public String toString() {  return "titleSorter+asc";  }  },
    DATE        { @Override  public String toString() {  return "date+asc";  }  },
    POPULARITY  { @Override  public String toString() {  return "downloads+desc";  }  }
}
