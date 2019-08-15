package com.aaron.justlike.main.preview;

import android.view.View;

/**
 * PreviewAdapter 的宿主类必须实现此接口与适配器进行通信
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IPreviewCommunicable {

    void onTap(View v);
}
