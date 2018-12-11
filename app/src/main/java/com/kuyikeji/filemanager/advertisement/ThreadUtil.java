package com.kuyikeji.filemanager.advertisement;

import android.os.AsyncTask;

/**
 * Created by DuJie on 2018/9/17 0017.
 * <pre>
 * </pre>
 */
public class ThreadUtil {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepUI(long millis, Runnable runnable) {
        new SleepAsync(runnable).execute(millis);
    }


    private static class SleepAsync extends AsyncTask<Long, Void, Void> {
        Runnable mRunnable;

        public SleepAsync(Runnable runnable) {
            this.mRunnable = runnable;
        }

        @Override
        protected Void doInBackground(Long... params) {
            sleep(params == null || params.length == 0 ? 0 : params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                mRunnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
