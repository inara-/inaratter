package jp.inara.inaratter;

import android.content.SharedPreferences;

import com.facebook.android.Facebook;

/**
 * Facebook �֘A�̏����������N���X
 * @author inara
 *
 */
public class FbManager {

    /** Twitter �g�[�N����Preference�L�[ */
    public static final String PREF_TW_TOKEN = "token";

    /** Twitter �g�[�N���V�[�N���b�g��Preference�L�[ */
    public static final String PREF_TW_TOKEN_SECRET = "token_secret";

    /** Facebook �A�N�Z�X�g�[�N����Preference�L�[ */
    private static final String PREF_FB_ACCESS_TOKEN = "fb_access_token";

    private static final String PREF_FB_ACCESS_EXPIRES = "fb_access_expires";
    
    private Facebook mFacebook;
    private static FbManager mFbManager;

    /**
     * �R���X�g���N�^
     * @param sp �v���t�@�����X
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
     * {@link FbManager} �̃C���X�^���X���擾����
     * @param sp �v���t�@�����X
     * @return {@link FbManager} �̃C���X�^���X
     */
    public static FbManager getFbManager(SharedPreferences sp){
        if(mFbManager == null){
            mFbManager = new FbManager(sp);
        }
        return mFbManager;
    }
    
    /**
     * Facebook�I�u�W�F�N�g���擾����
     * @return �A�N�Z�X�g�[�N�����ݒ肳�ꂽ {@link Facebook} �I�u�W�F�N�g
     */
    public Facebook getFacebook() {
        return mFacebook;
    }
}
