package com.aaron.base.image.test;

import android.content.Context;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public interface ImageEngine<T extends ImageOption> {

    void init(Context context);

    void load(Context context, T config);
}
