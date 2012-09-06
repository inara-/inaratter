
package jp.inara.inaratter;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import jp.inara.inaratter.InputFragment.OnFacebookButtonCheckedChangeListener;
import jp.inara.inaratter.InputFragment.OnTwitterButtonCheckedChangeListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CompoundButton;

/**
 * アプリのメイン画面
 * 
 * @author inara
 */
public class MainActivity extends Activity implements
        OnTwitterButtonCheckedChangeListener, OnFacebookButtonCheckedChangeListener {
    private static final String LOG_TAG = "MainActivity";

    // Twitter 認証用
    /** コールバックURL */
    private static final String TW_CALLBACK = "http://blog.inara.jp";

    /** Consumer Key */
    public static final String TW_CONSUMER_KEY = "JbOdaTH7FKi7OOge1iptjw";

    /** Consumer Secret */
    public static final String TW_CONSUMER_SECRET = "8PKqXICuVSt2ivq3SMscA0NDipUrd204Nf4cDd9I";

    /** Twitter 認証リクエスト用の 識別キー */
    private static final int TW_REQUEST_OAUTH = 1;

    // Facebook 認証用
    /** Facebook App ID */
    public static final String FB_APP_ID = "135468956586356";

    /** Twitter トークンのPreferenceキー */
    public static final String PREF_TW_TOKEN = "token";

    /** Twitter トークンシークレットのPreferenceキー */
    public static final String PREF_TW_TOKEN_SECRET = "token_secret";

    /** Facebook アクセストークンのPreferenceキー */
    private static final String PREF_FB_ACCESS_TOKEN = "fb_access_token";

    private static final String PREF_FB_ACCESS_EXPIRES = "fb_access_expires";

    /** Twitter トグルボタンのチェック状態のPreferenceキー */
    public static final String PREF_CHECK_TWITTER = "check_twitter";

    /** Facebook トグルボタンのチェック状態のPreferenceキー */
    public static final String PREF_CHECK_FACEBOOK = "check_facebook";
    
    private SharedPreferences mPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
//        mFacebook.extendAccessTokenIfNeeded(this, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TW_REQUEST_OAUTH) {
            // Twitter認証の場合、SharePreferenceにTwitterトークンを保存
            Editor editor = mPreference.edit();
            editor.putString(PREF_TW_TOKEN, data.getStringExtra(OAuthActivity.TOKEN));
            editor.putString(PREF_TW_TOKEN_SECRET, data.getStringExtra(OAuthActivity.TOKEN_SECRET));
            editor.commit();
        } else {
            // Facebook認証の場合、
            Facebook facebook = FbManager.getFbManager(mPreference).getFacebook();
            facebook.authorizeCallback(requestCode, resultCode, data);
        }
    }

    @Override
    public void onTwitterButtonCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {
        Log.d(LOG_TAG, "onTwitterButtonCheckedChanged");
        // チェック状態を保存
        Editor editor = mPreference.edit();
        editor.putBoolean(PREF_CHECK_TWITTER, isChecked);
        editor.commit();
        if (isChecked) {
            // Twitter認証用のトークンを取得
            String token = mPreference.getString(PREF_TW_TOKEN, "");
            String tokenSecret = mPreference.getString(PREF_TW_TOKEN_SECRET, "");
            if (token.isEmpty() || tokenSecret.isEmpty()) {
                // Twitterの認証を行う
                Intent intent = new Intent(this, OAuthActivity.class);
                intent.putExtra(OAuthActivity.CALLBACK, TW_CALLBACK);
                intent.putExtra(OAuthActivity.CONSUMER_KEY, TW_CONSUMER_KEY);
                intent.putExtra(OAuthActivity.CONSUMER_SECRET, TW_CONSUMER_SECRET);
                startActivityForResult(intent, TW_REQUEST_OAUTH);
            }
        }
    }

    @Override
    public void onFacebookButtonCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(LOG_TAG, "onFacebookButtonCheckedChanged");
        // チェック状態を保存
        final Editor editor = mPreference.edit();
        editor.putBoolean(PREF_CHECK_FACEBOOK, isChecked);
        editor.commit();
        if (isChecked) {
            Facebook facebook = FbManager.getFbManager(mPreference).getFacebook();
            if (!facebook.isSessionValid()) {
                facebook.authorize(this, new String[] {"publish_stream"}, new DialogListener() {
                    @Override
                    public void onFacebookError(FacebookError e) {
                        Log.d(LOG_TAG, "onFacebookError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onError(DialogError e) {
                        Log.d(LOG_TAG, "onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete(Bundle values) {
                        Log.d(LOG_TAG, "onComplete");
                        // Facebook 認証情報を保存
                        Facebook facebook = FbManager.getFbManager(mPreference).getFacebook();
                        editor.putString(PREF_FB_ACCESS_TOKEN, facebook.getAccessToken());
                        editor.putLong(PREF_FB_ACCESS_EXPIRES, facebook.getAccessExpires());
                        editor.commit();
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub
                    }
                });
            }
        }
    }
}
