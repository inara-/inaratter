package jp.inara.inaratter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * TwitterのOAuth認証を行う
 * 
 * @author inara
 */
public class OAuthActivity extends Activity {
    /** コールバックキー */
    public static final String CALLBACK = "callback";

    /** CONSUMER KEY */
    public static final String CONSUMER_KEY = "consumer_key";

    /** CONSUMER SECRET KEY */
    public static final String CONSUMER_SECRET = "consumer_secret";

    /** Twitter ユーザーID */
    public static final String USER_ID = "user_id";

    /** スクリーンネーム */
    public static final String SCREEN_NAME = "screen_name";

    /** リクエストトークン */
    public static final String TOKEN = "token";

    /** リクエストトークン シークレットキー */
    public static final String TOKEN_SECRET = "token_secret";

    /** ? */
    private static final String OAUTH_VERIFIER = "oauth_verifier";

    /** OAuth認証ページ表示用の WebView */
    private WebView mWebView;

    /** コールバック先 */
    private String mCallback;

    /** Twitterオブジェクト */
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_CANCELED);

        // プログレス表示
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // Viewのセットアップ
        mWebView = new WebView(this);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        setContentView(mWebView);

        // TwitterのOAuth認証画面で毎回ユーザ名、アカウントを入力させるために必要
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(false);

        Intent intent = getIntent();
        mCallback = intent.getStringExtra(CALLBACK);
        String consumerKey = intent.getStringExtra(CONSUMER_KEY);
        String consumerSecret = intent.getStringExtra(CONSUMER_SECRET);
        if ((mCallback == null) || (consumerKey == null)
                || (consumerSecret == null)) {
            finish();
        }

        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(consumerKey, consumerSecret);

        PreTask preTask = new PreTask();
        preTask.execute();
    }

    /**
     * 認証前処理用の AsyncTask
     * 
     * @author inara
     */
    public class PreTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String authorizationUrl = null;
            try {
                // 非同期処理が必要なメソッドの呼び出し
                RequestToken requestToken = mTwitter.getOAuthRequestToken();
                if (requestToken != null) {
                    authorizationUrl = requestToken.getAuthorizationURL();
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return authorizationUrl;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);
            if (result != null) {
                mWebView.loadUrl(result);
            } else {
                finish();
            }
        }
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            setProgress(newProgress * 100);
        }
    };

    private WebViewClient mWebViewClient = new WebViewClient() {

        // 特定のページをフック
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean result = true;
            if ((url != null) && (url.startsWith(mCallback))) {
                Uri uri = Uri.parse(url);
                String oAuthVerifier = uri.getQueryParameter(OAUTH_VERIFIER);
                PostTask postTask = new PostTask();
                postTask.execute(oAuthVerifier);
            } else {
                result = super.shouldOverrideUrlLoading(view, url);
            }
            return result;
        }
    };

    /**
     * 後処理用の AsyncTask
     * 
     * @author inara
     */
    public class PostTask extends AsyncTask<String, Void, AccessToken> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected AccessToken doInBackground(String... params) {
            AccessToken accessToken = null;
            if (params != null) {
                try {
                    // 非同期処理が必要なメソッドの呼び出し
                    accessToken = mTwitter.getOAuthAccessToken(params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute(AccessToken result) {
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);
            if (result != null) {
                long userId = result.getUserId();
                String screenName = result.getScreenName();
                String token = result.getToken();
                String tokenSecret = result.getTokenSecret();
                Intent intent = new Intent();
                intent.putExtra(USER_ID, userId);
                intent.putExtra(SCREEN_NAME, screenName);
                intent.putExtra(TOKEN, token);
                intent.putExtra(TOKEN_SECRET, tokenSecret);
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    }
}
