package jp.inara.inaratter;

import android.content.SharedPreferences;

import com.facebook.android.Facebook;

/**
 * Facebook 関連の処理を扱うクラス
 * @author inara
 *
 */
public class FbManager {

    /** Twitter トークンのPreferenceキー */
    public static final String PREF_TW_TOKEN = "token";

    /** Twitter トークンシークレットのPreferenceキー */
    public static final String PREF_TW_TOKEN_SECRET = "token_secret";

    /** Facebook アクセストークンのPreferenceキー */
    private static final String PREF_FB_ACCESS_TOKEN = "fb_access_token";

    private static final String PREF_FB_ACCESS_EXPIRES = "fb_access_expires";
    
    private Facebook mFacebook;
    private static FbManager mFbManager;

    /**
     * コンストラクタ
     * @param sp プリファレンス
     */
    private FbManager(SharedPreferences sp){
        mFacebook = new Facebook(MainActivity.FB_APP_ID);
        String fbToken = sp.getString(PREF_FB_ACCESS_TOKEN, null);
        long fbExpires = sp.getLong(PREF_FB_ACCESS_EXPIRES, 0);
        if (fbToken != null) {
            mFacebook.setAccessToken(fbToken);
        }
        if (fbExpires != 0) {
            mFacebook.setAccessExpires(fbExpires);
        }
    }
    
    /**
     * {@link FbManager} のインスタンスを取得する
     * @param sp プリファレンス
     * @return {@link FbManager} のインスタンス
     */
    public static FbManager getFbManager(SharedPreferences sp){
        if(mFbManager == null){
            mFbManager = new FbManager(sp);
        }
        return mFbManager;
    }
    
    /**
     * Facebookオブジェクトを取得する
     * @return アクセストークンが設定された {@link Facebook} オブジェクト
     */
    public Facebook getFacebook() {
        return mFacebook;
    }
}
