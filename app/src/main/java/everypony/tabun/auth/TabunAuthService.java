package everypony.tabun.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author cab404
 */
public class TabunAuthService extends Service {

    public IBinder onBind(Intent intent) {
        TabunAccountAuth auth = new TabunAccountAuth(this);
        return auth.getIBinder();
    }
}
