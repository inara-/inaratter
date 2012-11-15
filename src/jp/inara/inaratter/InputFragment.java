
package jp.inara.inaratter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

/**
 * �e�L�X�g��͗p��UI��������Fragment
 * 
 * @author inara
 */
public class InputFragment extends Fragment implements LoaderCallbacks<Boolean> {
    private static final String POST_MESSAGE_KEY = "post_message";
    private static final String LOG_TAG = "InputFragment";
    private static final int TWEET_LENGTH = 140;

    private OnTwitterButtonCheckedChangeListener mTwitterListener;
    private OnFacebookButtonCheckedChangeListener mFacebookListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.input, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Listener��Activity�Ɏ�������Ă��邩�m�F
        try {
            mTwitterListener = (OnTwitterButtonCheckedChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement OnTwitterButtonCheckedChangeListener");
        }
        try {
            mFacebookListener = (OnFacebookButtonCheckedChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement OnFacebookButtonCheckedChangeListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Twitter
        final ToggleButton twitterButton = (ToggleButton) getActivity().findViewById(
                R.id.twitter_button);
        // ButtonStatus��ݒ�
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        twitterButton.setChecked(sp.getBoolean(MainActivity.PREF_CHECK_TWITTER, false));

        // Twitter�g�O���{�^���Ƀ��X�i�[��o�^
        twitterButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTwitterListener.onTwitterButtonCheckedChanged(buttonView, isChecked);
            }
        });

        // Facebook
        final ToggleButton facebookButton = (ToggleButton) getActivity().findViewById(
                R.id.facebokk_button);
        // ButtonStatus��ݒ�
        facebookButton.setChecked(sp.getBoolean(MainActivity.PREF_CHECK_FACEBOOK, false));

        // Facebook�g�O���{�^���Ƀ��X�i�[��o�^
        facebookButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFacebookListener.onFacebookButtonCheckedChanged(buttonView, isChecked);
            }
        });

        // Post �{�^���Ƀ��X�i�[��o�^
        Button postButton = (Button) getActivity().findViewById(R.id.post_button);
        postButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Post Button Clicked");
                EditText editText = (EditText) getActivity().findViewById(R.id.input_text);
                String message = editText.getText().toString();
                if (twitterButton.isChecked()) {
                    Log.d(LOG_TAG, "Twitter Posting");
                    Bundle args = new Bundle(1);
                    args.putString(POST_MESSAGE_KEY, message);
                    getLoaderManager().initLoader(0, args, InputFragment.this);
                }
                if (facebookButton.isChecked()) {
                    Log.d(LOG_TAG, "Facebook Posting");
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    Facebook facebook = FbManager.getFbManager(sp).getFacebook();
                    String method = "POST";
                    Bundle param = new Bundle();
                    param.putString("message", message);
                    AsyncFacebookRunner asyncFbRunner = new AsyncFacebookRunner(facebook);
                    asyncFbRunner.request("me/feed", param, method, new FBPostRequestListner(),
                            null);
                }
            }
        });

        // EditText�Ƀ��X�i�[��o�^
        EditText editText = (EditText) getActivity().findViewById(R.id.input_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView countText = (TextView) getActivity().findViewById(R.id.count_text);
                int textLength = TWEET_LENGTH - s.length();
                countText.setText(String.valueOf(textLength));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Twitter ToggleButton �� �`�F�b�N�ύX����Activity�ɒʒm���邽�߂̃��X�i�[
     * 
     * @author inara
     */
    public interface OnTwitterButtonCheckedChangeListener {
        /**
         * Twitter�g�O���{�^���̃��X�i�[
         * 
         * @param buttonView CompoundButton�I�u�W�F�N�g
         * @param isChecked �`�F�b�N����Ă��邩�ǂ���
         */
        public void onTwitterButtonCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }

    /**
     * Facebook ToggleButton�̃`�F�b�N�ύX����Activity�ɒʒm���邽�߂̃��X�i�[
     * 
     * @author inara
     */
    public interface OnFacebookButtonCheckedChangeListener {
        /**
         * Facebook�g�O���{�^���̃��X�i�[
         * 
         * @param buttonView {@link CompoundButton}�I�u�W�F�N�g
         * @param isChecked �`�F�b�N����Ă��邩�ǂ���
         */
        public void onFacebookButtonCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");
        String message = args.getString(POST_MESSAGE_KEY);
        AsyncTwitterPostLoader loader = new AsyncTwitterPostLoader(getActivity(), message);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoaderReset(Loader<Boolean> arg0) {
    }

    @Override
    public void onLoadFinished(Loader<Boolean> arg0, Boolean arg1) {
        Log.d(LOG_TAG, "onLoadFinished");
        Toast.makeText(getActivity(), "Post���܂����B", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    /**
     * Facebook�̃E�H�[���֓��e���邽�߂̃��X�i�[
     * 
     * @author inara
     */
    private class FBPostRequestListner implements RequestListener {

        @Override
        public void onComplete(String response, Object state) {
            Log.d(LOG_TAG, "onComplete");
            Log.d(LOG_TAG, response);
        }

        @Override
        public void onIOException(IOException e, Object state) {
            Log.d(LOG_TAG, "onIOException");
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e, Object state) {
            Log.d(LOG_TAG, "onFileNotFoundException");
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            Log.d(LOG_TAG, "onMalformedURLException");
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            Log.d(LOG_TAG, "onFacebookError");
        }

    }
}
