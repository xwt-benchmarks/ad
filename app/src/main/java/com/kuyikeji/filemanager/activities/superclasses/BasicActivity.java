package com.kuyikeji.filemanager.activities.superclasses;

import android.support.v7.app.AppCompatActivity;

import com.kuyikeji.filemanager.ui.colors.ColorPreferenceHelper;
import com.kuyikeji.filemanager.utils.application.AppConfig;
import com.kuyikeji.filemanager.utils.provider.UtilitiesProvider;
import com.kuyikeji.filemanager.utils.theme.AppTheme;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by rpiotaix on 17/10/16.
 */
public class BasicActivity extends AppCompatActivity {

    protected AppConfig getAppConfig() {
        return (AppConfig) getApplication();
    }

    public ColorPreferenceHelper getColorPreference() {
        return getAppConfig().getUtilsProvider().getColorPreference();
    }

    public AppTheme getAppTheme() {
        return getAppConfig().getUtilsProvider().getAppTheme();
    }

    public UtilitiesProvider getUtilsProvider() {
        return getAppConfig().getUtilsProvider();
    }

    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
