package jp.inara.inaratter;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

/**
 * Facebook�ɔ񓯊��œ��e����N���X
 * @author inara
 *
 */
public class AsyncFacebookPostLoader extends AsyncTaskLoader<Boolean> {
    private static final String LOG_TAG = "AsyncFacebookPostLoader";
    private String mPostMessage;
    
    /**
     * �R���X�g���N�^
     * @param context Activity�̃R���e�L�X�g
     * @param postMessage ���e���郁�b�Z�[�W
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
