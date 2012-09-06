
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
 * TwitterへバックグラウンドでPostする
 * 
 * @author inara
 */
public class AsyncTwitterPostLoader extends AsyncTaskLoader<Boolean> {
    private static final String LOG_TAG = "AsyncTwitterPostLoader";
    private String mPostMessage;

    /**
     * コンストラクタ
     * 
     * @param context Activityのコンテキスト
     * @param postMessage ポストするメッセージ
     */
    public AsyncTwitterPostLoader(Context context, String postMessage) {
        super(context);
        mPostMessage = postMessage;
    }

    @Override
    public Boolean loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground");
        // Twitter4Jに対してOAuth情報を設定
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        ConfigurationBuilder builder = new ConfigurationBuilder();

        // アプリ固有の情報
        builder.setOAuthConsumerKey(MainActivity.TW_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(MainActivity.TW_CONSUMER_SECRET);
        // アプリ＋ユーザー固有の情報
        builder.setOAuthAccessToken(sp.getString(MainActivity.PREF_TW_TOKEN, ""));
        builder.setOAuthAccessTokenSecret(sp.getString(MainActivity.PREF_TW_TOKEN_SECRET, ""));

        // Twitter に Post する
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
