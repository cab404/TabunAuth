package everypony.tabun.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author cab404
 */
public class TabunAccountSelector extends Activity {
    public static final String ACCOUNT = "account";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= 14)
            setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        else
            setTheme(android.R.style.Theme_Light_NoTitleBar);

        setContentView(R.layout.tabun_auth_selector);

        AccountManager manager = AccountManager.get(this);
        assert manager != null;


        LinearLayout list = (LinearLayout) findViewById(R.id.account_list);
        list.removeAllViews();

        for (final Account account : manager.getAccountsByType(TabunAccount.TYPE)) {
            View label = getLayoutInflater().inflate(R.layout.tabun_auth_account_label, list, false);
            assert label != null;
            ((TextView) label.findViewById(R.id.username)).setText(account.name);

            label.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    Intent data = new Intent();
                    data.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
            list.addView(label);
        }

    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

}