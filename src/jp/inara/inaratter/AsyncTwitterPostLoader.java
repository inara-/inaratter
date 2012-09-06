
package jp.inara.inaratter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Twitter�փo�b�N�O���E���h��Post����
 * 
 * @author inara
 */
public class AsyncTwitterPostLoader extends AsyncTaskLoader<Boolean> {
    private static final String LOG_TAG = "AsyncTwitterPostLoader";
    private String mPostMessage;

    /**
     * �R���X�g���N�^
     * 
     * @param context Activity�̃R���e�L�X�g
     * @param postMessage �|�X�g���郁�b�Z�[�W
     */
    public AsyncTwitterPostLoader(Context context, String postMessage) {
        super(context);
        mPostMessage = postMessage;
    }

    @Override
    public Boolean loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground");
        // Twitter4J�ɑ΂���OAuth����ݒ�
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        ConfigurationBuilder builder = new ConfigurationBuilder();

        // �A�v���ŗL�̏��
        builder.setOAuthConsumerKey(MainActivity.TW_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(MainActivity.TW_CONSUMER_SECRET);
        // �A�v���{���[�U�[�ŗL�̏��
        builder.setOAuthAccessToken(sp.getString(MainActivity.PREF_TW_TOKEN, ""));
        builder.setOAuthAccessTokenSecret(sp.getString(MainActivity.PREF_TW_TOKEN_SECRET, ""));

        // Twitter �� Post ����
        twitter4j.conf.Configuration conf = builder.build();
        TwitterFactory twitterFactory = new TwitterFactory(conf);
        Twitter twitter = twitterFactory.getInstance();
        try {
            twitter.updateStatus(mPostMessage);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return true;
    }
}
