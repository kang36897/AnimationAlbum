package com.gulu.album;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class LastestReceivedSmsActivity extends Activity {

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "new message !", Toast.LENGTH_SHORT).show();
            SmsMessage[] sms = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            StringBuilder body = new StringBuilder();
            for (SmsMessage msg : sms) {
                body.append(msg.getMessageBody());
            }

            TextView mLatestRecievedMessage = (TextView) findViewById(R.id.latest_received_message);
            mLatestRecievedMessage.setText(body.toString());
        }
    };

    void extractVerfiyCode(String messageBody) {
        if (TextUtils.isEmpty(messageBody)) {
            return;
        }

        Pattern p = Pattern.compile("([Nnavi]{4}[168]{3})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(messageBody);
        if (matcher.find() == false) {
            return;
        }

        String app = matcher.group(0);
        if ("Navi168".equalsIgnoreCase(app) == false) {
            return;
        }

        p = Pattern.compile("(\\d{6})");
        matcher = p.matcher(messageBody);
        if (matcher.find()) {

            String code = matcher.group(0);
            if (TextUtils.isEmpty(code)) {
                return;
            }

            TextView mAuthentiCodeView = (TextView) findViewById(R.id.latest_received_message);
            mAuthentiCodeView.setText(code);
        }
    }

    //@formatter:off
    private String[] projection = new String[]{
            Telephony.Sms.Inbox.THREAD_ID,
            Telephony.Sms.Inbox.BODY,
            Telephony.Sms.Inbox.DATE,
            Telephony.Sms.Inbox.PERSON,
            Telephony.Sms.Inbox.SERVICE_CENTER
    };
    //@formatter:on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_received_sms);

        StringBuilder sBuilder = new StringBuilder();
        TextView textView = (TextView) findViewById(R.id.message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            onReceivedNewTextMessage(sBuilder);
        } else {
            sBuilder.append("Ops,Your android sdk version is below  ").append(Build.VERSION_CODES.KITKAT);
        }
        textView.setText(sBuilder.toString());
    }

    void onReceivedNewTextMessage(StringBuilder sBuilder) {

        Cursor cursor = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, projection, null, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        int idIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.THREAD_ID);
        int bodyIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.BODY);
        int dateIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.DATE);
        int personIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.PERSON);
        int scIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.SERVICE_CENTER);

        if (cursor.moveToNext()) {
            sBuilder.append(Telephony.Sms.Inbox.THREAD_ID).append(":").append(idIndex == -1 ? "" : cursor.getString(idIndex));
            sBuilder.append("_").append(Telephony.Sms.Inbox.BODY).append(":").append(bodyIndex == -1 ? "" : cursor.getString(bodyIndex));
            sBuilder.append("_").append(Telephony.Sms.Inbox.DATE).append(":").append(dateIndex == -1 ? "" : cursor.getString(dateIndex));
            sBuilder.append("_").append(Telephony.Sms.Inbox.PERSON).append(":").append(personIndex == -1 ? "" : cursor.getString(personIndex));
            sBuilder.append("_").append(Telephony.Sms.Inbox.SERVICE_CENTER).append(":").append(scIndex == -1 ? "" : cursor.getString(scIndex));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(mBroadcastReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
