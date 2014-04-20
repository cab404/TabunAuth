package everypony.tabun.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * @author cab404
 */
public class TabunAccount extends AbstractAccountAuthenticator {

    public TabunAccount(Context context) {
        super(context);
    }

    @Override public Bundle editProperties(AccountAuthenticatorResponse aar, String s) {
        return null;
    }

    @Override public Bundle addAccount(AccountAuthenticatorResponse aar,
                                       String accountType,
                                       String authTokenType,
                                       String[] requiredFeatures,
                                       Bundle options)
    throws NetworkErrorException {

        return null;
    }

    @Override public Bundle confirmCredentials(AccountAuthenticatorResponse aar, Account account, Bundle bundle)
    throws NetworkErrorException {
        return null;
    }

    @Override public Bundle getAuthToken(AccountAuthenticatorResponse aar, Account account, String s, Bundle bundle)
    throws NetworkErrorException {
        return null;
    }

    @Override public String getAuthTokenLabel(String s) {
        return s;
    }

    @Override public Bundle updateCredentials(AccountAuthenticatorResponse aar, Account account, String authTokenType, Bundle options)
    throws NetworkErrorException {
        return null;
    }

    @Override public Bundle hasFeatures(AccountAuthenticatorResponse aar, Account account, String[] features)
    throws NetworkErrorException {
        return null;
    }
}
