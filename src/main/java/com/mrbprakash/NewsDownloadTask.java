package com.mrbprakash;

public class NewsDownloadTask implements Runnable {
    private final NewsDownloader downloader;

    public NewsDownloadTask(NewsDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void run() {
        downloader.download();
    }
}
