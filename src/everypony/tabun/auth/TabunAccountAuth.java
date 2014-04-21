package everypony.tabun.auth;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.cab404.libtabun.pages.TabunPage;
import com.cab404.libtabun.util.TabunAccessProfile;
import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.U;

/**
 * @author cab404
 */
public class TabunAccountAuth extends AbstractAccountAuthenticator {

    private Context context;

    public TabunAccountAuth(Context context) {
        super(context);
        this.context = context;
    }

    @Override public Bundle editProperties(AccountAuthenticatorResponse aar, String s) {return null;}

    @Override public Bundle confirmCredentials(AccountAuthenticatorResponse aar, Account account, Bundle bundle)
    throws NetworkErrorException {return null;}

    @Override public Bundle updateCredentials(AccountAuthenticatorResponse aar, Account account, String authTokenType, Bundle options)
    throws NetworkErrorException {return null;}

    @Override public Bundle hasFeatures(AccountAuthenticatorResponse aar, Account account, String[] features)
    throws NetworkErrorException {return null;}

    @Override public Bundle addAccount(AccountAuthenticatorResponse aar,
                                       String accountType,
                                       String authTokenType,
                                       String[] requiredFeatures,
                                       Bundle options)
    throws NetworkErrorException {
        Intent intent = new Intent(context, TabunAuthActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, aar);

        options = options == null ? new Bundle() : options;
        options.putParcelable(AccountManager.KEY_INTENT, intent);

        return options;
    }

    @Override public Bundle getAuthToken(AccountAuthenticatorResponse aar, Account account, String s, Bundle options)
    throws NetworkErrorException {

        AccountManager man = AccountManager.get(context);
        if (man == null)
            throw new RuntimeException("Account manager is unavailable!");

        String token = man.peekAuthToken(account, TabunAuthService.ACCOUNT_TYPE);

        if (!TextUtils.isEmpty(token)) {
            AccessProfile profile;
            try {
                U.v("Checking!");
                profile = AccessProfile.parseString(token);
                TabunPage test = new TabunPage();
                test.fetch(profile);
                if (test.c_inf == null)
                    token = null;

            } catch (Exception ex) { // Если не отпарсился профиль.
                token = null;
            }
        }

        if (TextUtils.isEmpty(token)) {
            String password = man.getPassword(account);
            TabunAccessProfile profile = new TabunAccessProfile();
            if (profile.login(account.name, password))
                token = profile.serialize();
        }

        if (!TextUtils.isEmpty(token)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            return result;
        }

        Intent intent = new Intent(context, TabunAuthActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, aar);

        options = options == null ? new Bundle() : options;
        options.putParcelable(AccountManager.KEY_INTENT, intent);
        return options;
    }

    @Override public String getAuthTokenLabel(String s) {
        return s;
    }
}
