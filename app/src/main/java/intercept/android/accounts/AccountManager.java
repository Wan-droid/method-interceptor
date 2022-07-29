package intercept.android.accounts;

import android.accounts.Account;

public class AccountManager {

    public static Account[] getAccounts(android.accounts.AccountManager manager) {
        return new Account[]{new Account("InterceptName", "InterceptType")};
    }

    public static Account[] getAccountsByType(android.accounts.AccountManager manager, String type) {
        return new Account[]{new Account("InterceptName2-" + type, "InterceptType2" + type)};
    }
}
