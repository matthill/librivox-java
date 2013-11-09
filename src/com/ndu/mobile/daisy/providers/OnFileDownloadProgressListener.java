package com.ndu.mobile.daisy.providers;

public interface OnFileDownloadProgressListener {
	public abstract void onProgressUpdate(long dl_uid, long bytesDownloaded, long total);
}
