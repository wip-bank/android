package de.fhdw.wipbank.android;


import de.fhdw.wipbank.android.model.Account;

public class AccountService {

    private static Account account;

    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account pAccount) {
        account = pAccount;
    }

}
