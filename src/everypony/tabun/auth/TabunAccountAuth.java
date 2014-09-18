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
		LogBus.log("create()");
		this.context = context;
	}

	@Override public Bundle editProperties(AccountAuthenticatorResponse aar, String s) {
		LogBus.log("edit()");
		return null;
	}

	@Override public Bundle confirmCredentials(AccountAuthenticatorResponse aar, Account account, Bundle bundle)
	throws NetworkErrorException {
		LogBus.log("confirm()");
		return null;
	}

	@Override public Bundle updateCredentials(AccountAuthenticatorResponse aar, Account account, String authTokenType, Bundle options)
	throws NetworkErrorException {
		LogBus.log("update()");
		return null;
	}

	@Override public Bundle hasFeatures(AccountAuthenticatorResponse aar, Account account, String[] features)
	throws NetworkErrorException {
		LogBus.log("hasFeatures()");
		return null;
	}

	@Override public Bundle addAccount(AccountAuthenticatorResponse aar,
	                                   String accountType,
	                                   String authTokenType,
	                                   String[] requiredFeatures,
	                                   Bundle options)
	throws NetworkErrorException {
		LogBus.log("add()");

		U.v(options);

		Intent intent = new Intent(context, TabunAuthActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, aar);

		options = options == null ? new Bundle() : options;
		options.putParcelable(AccountManager.KEY_INTENT, intent);

		return options;
	}

	@Override public Bundle getAuthToken(AccountAuthenticatorResponse aar, Account account, String authTokenType, Bundle options)
	throws NetworkErrorException {
		LogBus.log("getAuthToken()");

		AccountManager man = AccountManager.get(context);
		if (man == null)
			throw new RuntimeException("Account manager is unavailable!");


		String token = man.getUserData(account, TabunAccount.COOKIE_TOKEN_TYPE);

		if (!TextUtils.isEmpty(token)) {
			AccessProfile profile;
			LogBus.log("Нашла токен авторизации, проверяю...");
			try {
				profile = AccessProfile.parseString(token);
				TabunPage test = new TabunPage();
				test.fetch(profile);
				if (test.c_inf == null) {
					LogBus.log("Токен сломан...");
					token = null;
				} else
					LogBus.log("Токен работает!");

			} catch (Exception ex) { // Если не отпарсился токен.
				LogBus.log("Токен совсем сломан, такого не должно быть...");
				token = null;
			}
		}

		if (TextUtils.isEmpty(token)) {
			LogBus.log("Рабочего токена нету, делаю новый...");
			String password = man.getPassword(account);

			TabunAccessProfile profile = new TabunAccessProfile();

			TabunPage page = new TabunPage();
			page.fetch(profile);

			boolean success;
			try {
				success = profile.login(account.name, password);
			} catch (Exception e) {
				success = false;
			}

			if (success) {
				token = profile.serialize();
				LogBus.log("Зашла в Табун, данные аутентификации работают. Сохранила его, как основной.");
				LogBus.log("Привет, " + account.name + "!");
				man.setUserData(account, TabunAccount.COOKIE_TOKEN_TYPE, token);

			} else {

				LogBus.log("ВНЕЗАПНО в AccountManager лежит сломанный аккаунт! Надо удалить!");
				Bundle result = new Bundle();

				result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_REMOTE_EXCEPTION);
				String[] array = context.getResources().getStringArray(R.array.error);
				String message = array[((int) (array.length * Math.random()))];
				result.putString(AccountManager.KEY_ERROR_MESSAGE, message);

				man.removeAccount(account, null, null);

				return result;

			}

		}

		if (!TextUtils.isEmpty(token)) {
			LogBus.log("Токен есть, сейчас отдам.");
			Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, token);
			return result;
		}

		LogBus.log("Хм. Я ничего не нашла, произошло что-то не то.");

		Intent intent = new Intent(context, TabunAuthActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, aar);
		options = options == null ? new Bundle() : options;
		options.putParcelable(AccountManager.KEY_INTENT, intent);
		return options;
	}

	@Override public String getAuthTokenLabel(String s) {
		LogBus.log("label()");
		return s;
	}
}
