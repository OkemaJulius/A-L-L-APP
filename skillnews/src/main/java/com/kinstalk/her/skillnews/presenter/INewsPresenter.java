package com.kinstalk.her.skillnews.presenter;

import com.kinstalk.her.skillnews.view.INewsView;

public interface INewsPresenter {
    void clickPlayBtn();

    void clickPauseBtn();

    void clickPreviousBtn();

    void clickNextBtn();

    void touchSeekBar(int position);

    void detachView();

    void setCurrentControlPanel(INewsView cp);
}
