package com.koolib.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.Gravity;
import android.app.Activity;
import android.content.Intent;
import android.view.WindowManager;
import android.graphics.PixelFormat;

public class AliveActivity extends Activity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        params.format = PixelFormat.TRANSPARENT;
        params.windowAnimations = android.R.style.Animation_Translucent;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        window.setAttributes(params);
        ALiveManager.getInstance().setAlive(this);
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        ALiveManager.getInstance().setAlive(this);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        ALiveManager.getInstance().setAlive(null);
    }
}