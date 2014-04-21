package everypony.tabun.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.cab404.libtabun.pages.TabunPage;
import com.cab404.libtabun.requests.LoginRequest;
import com.cab404.libtabun.util.TabunAccessProfile;
import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.U;

/**
 * @author cab404
 */
public class TabunAuthActivity extends Activity {

    public static final String LOG_TAG = "Tabun.Auth";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 14)
            setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        else
            setTheme(android.R.style.Theme_Light_NoTitleBar);

        setContentView(R.layout.auth);

        ((EditText) findViewById(R.id.password)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView password, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    EditText login = (EditText) findViewById(R.id.login);

                    setLoading(true);
                    login(String.valueOf(login.getText()), String.valueOf(password.getText()));
                }

                return false;
            }
        });
    }

    protected void login(String login, String password) {
        new AuthTask().execute(new AuthData(login, password));
    }

    protected void setLoading(boolean loading) {
        findViewById(R.id.login).setEnabled(!loading);
        findViewById(R.id.password).setEnabled(!loading);
        findViewById(R.id.indicator).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    protected void throwError() {
        String[] messages = getResources().getStringArray(R.array.error);
        String message = messages[((int) (messages.length * Math.random()))];
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void throwSuccess() {
        String[] messages = getResources().getStringArray(R.array.success);
        String message = messages[((int) (messages.length * Math.random()))];
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private class AuthData {
        String login, password;
        AccessProfile profile;

        public AuthData(String login, String password) {
            this.login = login;
            this.password = password;
        }
    }

    private class AuthTask extends AsyncTask<AuthData, Void, AccessProfile> {

        private AuthData data;
        @Override protected AccessProfile doInBackground(AuthData... authData) {
            data = authData[0];

            if (data.login == null || data.login.length() == 0)
                return null;
            if (data.password == null || data.password.length() == 0)
                return null;

            data.profile = new TabunAccessProfile();

            TabunPage page = new TabunPage();
            page.fetch(data.profile);

            LoginRequest login = new LoginRequest(data.login, data.password);
            try {
                login.exec(data.profile, page);
            } catch (Exception e) {
                U.w(e);
                return null;
            }

            if (login.success())
                return data.profile;
            else
                return null;
        }

        @Override protected void onPostExecute(AccessProfile profile) {
            if (profile == null) {
                setLoading(false);
                throwError();
            } else {
                throwSuccess();
                createAccount(data);
                setResult(RESULT_OK);
                finish();
            }
            super.onPostExecute(profile);
        }

    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    private void createAccount(AuthData data) {
        AccountManager man = AccountManager.get(getBaseContext());
        if (man == null)
            throw new RuntimeException("Account manager is unavailable!");

        Account account = new Account(data.login, TabunAuthService.ACCOUNT_TYPE);
        man.addAccountExplicitly(account, data.password, null);
        man.setAuthToken(account, TabunAuthService.COOKIE_TOKEN_TYPE, data.profile.serialize());
    }
}