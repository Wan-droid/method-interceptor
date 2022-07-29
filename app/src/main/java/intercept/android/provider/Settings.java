package intercept.android.provider;

import android.content.ContentResolver;

public class Settings {

    public static final class Secure {
        public static String getString(ContentResolver resolver, String name) {
            return "Intercept-Settings.Secure." + name;
        }
    }
}
