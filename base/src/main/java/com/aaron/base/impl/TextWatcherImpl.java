package com.aaron.base.impl;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * TextWatcher 空实现
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class TextWatcherImpl implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
