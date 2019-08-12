package com.aaron.justlike.common.http.download;

public interface IDownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailure();

    void onPause();
}
