package app.coolweather.com.util;

/**
 * Created by tg on 2018/10/17.
 */

public interface HttpCallbackListener {

    void onFinish(String response);
    void onError(Exception e);
}
