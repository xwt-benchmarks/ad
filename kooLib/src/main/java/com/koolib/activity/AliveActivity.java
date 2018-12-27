package com.koolib.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.Gravity;
import android.app.Activity;
import android.content.Intent;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import com.koolib.adconfigaction.ProcessService;

public class AliveActivity extends Activity
{
    public static final String IS_START_PROCESS_SERVICE = "is_start_process_service";

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
        startProcessService();
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        ALiveManager.getInstance().setAlive(this);
    }

    private void startProcessService()
    {
        if(getIntent().getBooleanExtra(IS_START_PROCESS_SERVICE,false))
            startService(new Intent(this,ProcessService.class));
    }

    protected void onDestroy()
    {
        super.onDestroy();
        ALiveManager.getInstance().setAlive(null);
    }
}