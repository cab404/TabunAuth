package everypony.tabun.auth;

import java.util.ArrayList;

/**
 * @author cab404
 */
public class LogBus {

	public static interface Handler {
		void handle(String str);
	}

	private final static ArrayList<Handler> handlers = new ArrayList<>();

	public static void reg(Handler handler) {
		synchronized (handlers) {
			handlers.add(handler);
		}
	}

	public static void unreg(Handler handler) {
		synchronized (handlers) {
			handlers.remove(handler);
		}
	}

	public static void log(Object msg) {
		String msg_text = String.valueOf(msg);
		synchronized (handlers) {
			for (Handler handler : handlers)
				handler.handle(msg_text);
		}
	}


}
