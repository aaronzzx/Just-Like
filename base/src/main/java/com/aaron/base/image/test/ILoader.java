package com.aaron.base.image.test;

import android.content.Context;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public interface ILoader {

    <T extends ImageOption> void load(Context context, T config);

    void setImageEngine(ImageEngine engine);

    ImageEngine getImageEngine();
}
