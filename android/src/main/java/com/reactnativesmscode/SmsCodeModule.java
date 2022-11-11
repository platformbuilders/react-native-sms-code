package com.reactnativesmscode;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ReactModule(name = SmsCodeModule.NAME)
public class SmsCodeModule extends ReactContextBaseJavaModule implements LifecycleEventListener, ActivityEventListener {
    public static final String NAME = "SmsCode";
    public static final String LOG = "Here";
    public static final String LOG_LISTENER = "Listener";
    public static final String LOG_BROADCAST_SUCCESS = "BroadcastReceiver Success";
    public static final int REQ_USER_CONSENT = 200;

    final ReactApplicationContext reactContext = getReactApplicationContext();

    SmsBroadcastReceiver smsBroadcastReceiver;
    String code;
    String codeLength = "6";


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(LOG, LOG_LISTENER);

        if (requestCode == REQ_USER_CONSENT){

            if ((resultCode == RESULT_OK) && (data != null)){
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }

    }

    public SmsCodeModule(ReactApplicationContext reactContext) {
        super(reactContext);

        reactContext.addActivityEventListener(this);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


    private void startSmartUserConsent(){
        SmsRetrieverClient client = SmsRetriever.getClient(this.getReactApplicationContext());
        client.startSmsUserConsent(null);
    }

    @ReactMethod
    public void registerBroadcastReceiver(){

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);

        startSmartUserConsent();

        smsBroadcastReceiver = new SmsBroadcastReceiver();

        smsBroadcastReceiver.smsBroadCastReceiverListener = new SmsBroadcastReceiver.SmsBroadCastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                Log.d(LOG, LOG_BROADCAST_SUCCESS);
                reactContext.startActivityForResult(intent, REQ_USER_CONSENT, Bundle.EMPTY);
            }

            @Override
            public void onFailure() {
            }
        };


        reactContext.registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable String params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void codeLength(String codeLength){
        this.codeLength = codeLength;
    }

    public String getOtpFromMessage(String message) {
        Pattern otpPattern = Pattern.compile("(|^)\\d{" + Integer.parseInt(this.codeLength) + "}");
        Matcher matcher = otpPattern.matcher(message);

        if (matcher.find()){
          code = matcher.group(0);
          sendEvent(reactContext, "code", code);
          Log.d(LOG, code);
        } else {
          sendEvent(reactContext, "code", "");
        }

        return null;
    }

    @ReactMethod
    public void unRegisterBroadcastService(){
        reactContext.unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    public void onHostResume() {}

    @Override
    public void onHostPause() {}

    @Override
    public void onHostDestroy() {}

    @Override
    public void onNewIntent(Intent intent) {}

}
