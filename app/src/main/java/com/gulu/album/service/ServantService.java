package com.gulu.album.service;

import android.app.IntentService;
import android.content.Intent;

public class ServantService extends IntentService {

    public final static String ACTION_SHUT_DOWN_DEVICE = "com.gulu.album.action_shut_down_device";


    public ServantService(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public ServantService() {
        super("servant_service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_SHUT_DOWN_DEVICE.equals(intent.getAction())) {

        }

    }
}
