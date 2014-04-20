package everypony.tabun.auth;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.cab404.libtabun.data.LivestreetKey;
import com.cab404.libtabun.pages.TabunPage;
import com.cab404.libtabun.requests.LoginRequest;
import com.cab404.libtabun.util.TabunAccessProfile;
import com.cab404.moonlight.framework.AccessProfile;

/**
 * @author cab404
 */
public class AuthActivity extends Activity {

    public static final String LOG_TAG = "Tabun.Auth";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 14)
            setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        else
            setTheme(android.R.style.Theme_Light_NoTitleBar);

        setContentView(R.layout.auth);

        ((EditText) findViewById(R.id.login)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.v(LOG_TAG, String.valueOf(textView.getText()));
                }
                return false;
            }
        });

        ((EditText) findViewById(R.id.password)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.v(LOG_TAG, String.valueOf(textView.getText()));
                }
                return false;
            }
        });
    }

    protected void login(String login, String password) {
        new AuthTask().execute(new AuthData(login, password));
    }

    private LivestreetKey cached_key = null;

    private class AuthData {
        String login, password;
        public AuthData(String login, String password) {
            this.login = login;
            this.password = password;
        }
    }

    private class AuthTask extends AsyncTask<AuthData, Void, AccessProfile> {

        @Override protected AccessProfile doInBackground(AuthData... authData) {
            AuthData data = authData[0];

            if (data.login == null || data.login.length() == 0)
                return null;
            if (data.password == null || data.password.length() == 0)
                return null;

            TabunAccessProfile profile = new TabunAccessProfile();

            if (cached_key == null) {
                TabunPage page = new TabunPage();
                page.fetch(profile);
                cached_key = page.key;
            }

            LoginRequest login = new LoginRequest(data.login, data.password);
            login.exec(profile, cached_key);

            if (login.success())
                return profile;
            else
                return null;
        }

        @Override protected void onPostExecute(AccessProfile profile) {
            super.onPostExecute(profile);



        }

    }
}