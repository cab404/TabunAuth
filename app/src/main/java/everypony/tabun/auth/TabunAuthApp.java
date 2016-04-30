package everypony.tabun.auth;

import android.app.Application;
import android.util.Log;

/**
 * @author cab404
 */
public class TabunAuthApp extends Application {

	@Override public void onCreate() {
		super.onCreate();
		LogBus.reg(new LogBus.Handler() {
			@Override public void handle(String str) {
				Log.v("LogBus", str);
			}
		});
	}
}
