package com.aaron.justlike.http.download_manager;

public interface IDownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailure();

    void onPause();
}
