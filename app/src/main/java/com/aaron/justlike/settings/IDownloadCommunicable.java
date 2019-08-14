package com.aaron.justlike.settings;

import android.view.View;

/**
 * DownloadAdapter 的宿主类必须实现此接口与适配器通信
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IDownloadCommunicable {

    void onTap(View v, String path);
}
