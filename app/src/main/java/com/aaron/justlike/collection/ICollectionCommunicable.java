package com.aaron.justlike.collection;

import android.view.View;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface ICollectionCommunicable {

    void onTap(View v, int pos);

    void onLongTap(View v, int pos);
}
