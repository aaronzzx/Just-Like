package com.aaron.justlike.main.presenter;

import com.aaron.justlike.main.view.IPreviewView;

public class PreviewPresenter implements IPreviewPresenter {

    private IPreviewView mView;

    public PreviewPresenter(IPreviewView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }
}
