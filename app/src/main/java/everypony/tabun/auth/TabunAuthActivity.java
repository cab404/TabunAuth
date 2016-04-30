package everypony.tabun.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cab404.libtabun.pages.TabunPage;
import com.cab404.libtabun.requests.LSRequest;
import com.cab404.libtabun.requests.LoginRequest;
import com.cab404.libtabun.util.TabunAccessProfile;
import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.U;

/**
 * @author cab404
 */
public class TabunAuthActivity extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 19)
            setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
        else
            setTheme(android.R.style.Theme_Translucent_NoTitleBar);


        setContentView(R.layout.tabun_auth_auth);

        ((EditText) findViewById(R.id.password)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView password, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
                    enter(null);
                return false;
            }
        });
    }

    public void enter(View view) {
        EditText login = (EditText) findViewById(R.id.login);
        EditText password = (EditText) findViewById(R.id.password);

        setLoading(true);
        login(String.valueOf(login.getText()), String.valueOf(password.getText()));
    }

    public void show_pwd(View view) {
        EditText password = (EditText) findViewById(R.id.password);
        if (((CheckBox) view).isChecked())
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_CLASS_TEXT);
        else
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
    }

    protected void login(String login, String password) {
        System.out.println("logging in as " + login);
        new Thread(new AuthTask(new AuthData(login, password))).start();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void createAccount(AuthData data) {
        AccountManager man = AccountManager.get(getBaseContext());
        if (man == null)
            throw new RuntimeException("Account manager is unavailable!");

        Account account = new Account(data.login, TabunAccount.TYPE);
        man.addAccountExplicitly(account, data.password, null);
        man.setAuthToken(account, TabunAccount.COOKIE_TOKEN_TYPE, data.profile.serialize());
    }

    private class AuthData {
        String login, password;
        TabunAccessProfile profile;

        public AuthData(String login, String password) {
            this.login = login;
            this.password = password;
        }

    }

    private class AuthTask implements Runnable {

        private AuthData data;

        public AuthTask(AuthData data) {
            this.data = data;
        }

        protected AccessProfile access() {

            if (data.login == null || data.login.length() == 0)
                return null;
            if (data.password == null || data.password.length() == 0)
                return null;

            data.profile = new TabunAccessProfile();

            boolean success;
            try {
                System.out.println("logging in");
                success = data.profile.login(data.login, data.password);
                System.out.println("logging in: " + success);
            } catch (Exception e) {
                return null;
            }

            if (success) {
                return data.profile;
            } else {
                Log.v("err", "asdf");
                return null;
            }
        }

        @Override
        public void run() {
            final AccessProfile profile = access();
            new Handler(Looper.getMainLooper()).post(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (profile == null) {
                                setLoading(false);
                                throwError();
                            } else {
                                throwSuccess();
                                createAccount(data);
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                    }
            );
        }
    }
}