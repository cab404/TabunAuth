package everypony.tabun.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author cab404
 */
public class TabunTokenGetterActivity extends Activity {
	AccountManager man;

	private static final int
			SELECTION_REQ = 14411,
			CREATION_REQ = 15123;

	private void log(String str) {
		TextView log_text = (TextView) findViewById(R.id.log);
		StringBuilder text = new StringBuilder(log_text.getText());
		text.append("\n").append(str);
		log_text.setText(text);
	}

	private final LogBus.Handler log_handler = new LogBus.Handler() {
		@Override public void handle(final String str) {
			new Handler(getMainLooper()).post(new Runnable() {
				@Override public void run() {
					log(str);
				}
			});
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogBus.reg(log_handler);

		if (Build.VERSION.SDK_INT >= 19)
			setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor);
		else
			setTheme(android.R.style.Theme_Translucent_NoTitleBar);

		setContentView(R.layout.tabun_auth_loading_layout);

		ConnectivityManager activeNetworkInfo = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (activeNetworkInfo.getActiveNetworkInfo() == null || !activeNetworkInfo.getActiveNetworkInfo().isAvailable()) {
			Toast
					.makeText(this, "Нет подключения к Сети!", Toast.LENGTH_LONG)
					.show();
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		man = AccountManager.get(this);

		valve();
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		LogBus.unreg(log_handler);
	}
	/**
	 * Запускается для проверки готовности данных к отправке.
	 * Если аккаунтов нет, то запускает создание, если один, то отдает, если больше, то включает селектор.
	 */
	public void valve() {
		Account[] accounts = man.getAccountsByType(TabunAccount.TYPE);

		if (accounts.length == 0) {
			Intent create_user = new Intent(this, TabunAuthActivity.class);
			startActivityForResult(create_user, CREATION_REQ);
			return;
		}

		if (accounts.length == 1) {
			new TokenGetter().execute(accounts[0]);
			return;
		}

		if (accounts.length > 1) {
			Intent select = new Intent(this, TabunAccountSelectorActivity.class);
			startActivityForResult(select, SELECTION_REQ);
		}


	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED) {
			setResult(RESULT_CANCELED);
			finish();
		}

		if (resultCode == RESULT_OK) {
			if (requestCode == CREATION_REQ)
				valve();

			if (requestCode == SELECTION_REQ) {
				String account_name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

				for (Account acc : man.getAccountsByType(TabunAccount.TYPE))
					if (acc.name.equals(account_name)) {
						new TokenGetter().execute(acc);
						return;
					}

			}

		}

	}

	@Override public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		finish();
	}

	private class TokenGetter extends AsyncTask<Account, Void, String> {

		@Override protected String doInBackground(Account... accounts) {
			Account account = accounts[0];

			Bundle bundle;
			try {
				TabunAccountAuth auth = new TabunAccountAuth(TabunTokenGetterActivity.this);

				bundle = auth.getAuthToken(
						null,
						account,
						TabunAccount.COOKIE_TOKEN_TYPE,
						Bundle.EMPTY
				);

			} catch (NetworkErrorException e) {
				throw new RuntimeException(e);
			}

			return bundle.getString(AccountManager.KEY_AUTHTOKEN);
		}

		@Override protected void onPostExecute(String s) {
			super.onPostExecute(s);

			if (s != null) {
				Intent data = new Intent();
				data.putExtra(TabunAccount.COOKIE_TOKEN_TYPE, s);
				setResult(RESULT_OK, data);
				finish();
			}

		}
	}
}