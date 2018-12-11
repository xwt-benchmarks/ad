package com.koolib.adListener;

public interface InAdListener
{
    void onAdStarted(String venderName);
    void onAdLoaded(String venderName);
    void onAdShowing(String venderName);
    void onAdClicked(String venderName);
    void onAdClosed(String venderName);
    void onAdError(String venderName,int code,String description);
    void onAdErrorWithOpenNextAd(String venderName,int code,String description,boolean isExtinguishingScreen,int residualRetryNumberOfVender);
}