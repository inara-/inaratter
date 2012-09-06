package jp.inara.inaratter;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

/**
 * Facebookに非同期で投稿するクラス
 * @author inara
 *
 */
public class AsyncFacebookPostLoader extends AsyncTaskLoader<Boolean> {
    private static final String LOG_TAG = "AsyncFacebookPostLoader";
    private String mPostMessage;
    
    /**
     * コンストラクタ
     * @param context Activityのコンテキスト
     * @param postMessage 投稿するメッセージ
     */
    public AsyncFacebookPostLoader(Context context, String postMessage) {
        super(context);
        mPostMessage = postMessage;
    }

    @Override
    public Boolean loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground");
        return true;
    }
}
