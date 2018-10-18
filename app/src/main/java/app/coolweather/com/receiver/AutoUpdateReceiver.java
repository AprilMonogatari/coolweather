package app.coolweather.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.coolweather.com.service.AutoUpdateService;

/**
 * Created by tg on 2018/10/18.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startService(intent);
    }
}
