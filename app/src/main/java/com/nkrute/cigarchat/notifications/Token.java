package com.nkrute.cigarchat.notifications;

import com.google.android.gms.tasks.Task;

public class Token {
    String token;

    public Token(String token) {
        this.token = token;
    }

    public Token(Task<String> tokenRefresh) {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
