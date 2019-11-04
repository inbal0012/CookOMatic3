package com.example.adopy.Notifications;

import android.util.Log;

public class Sender {
    public Data data;
    private String to;

    public Sender(Data data, String to) {
        Log.d("my_Sender", "Sender: to" + to);
        this.data = data;
        this.to = to;
    }
}
