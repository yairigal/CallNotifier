package yairigal.com.telegramcallnotifier;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.ajts.androidmads.telegrambotlibrary.Telegram;
import com.ajts.androidmads.telegrambotlibrary.Utils.TelegramCallback;
import com.ajts.androidmads.telegrambotlibrary.models.Message;

import okhttp3.Call;

import static yairigal.com.telegramcallnotifier.Credentials.TOKEN;
import static yairigal.com.telegramcallnotifier.Credentials.YAIR_ID;

/**
 * Created by Yair Yigal on 2018-09-03.
 */

public class CustomPhoneStateListener extends PhoneStateListener {



    //private static final String TAG = "PhoneStateChanged";
    private Context context; //Context to make Toast if required
    private Telegram telegram;

    public CustomPhoneStateListener(Context context) {
        super();
        this.context = context;
        this.telegram = new Telegram(TOKEN);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);


        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                //when Idle i.e no call
                Toast.makeText(context, "Phone state Idle", Toast.LENGTH_LONG).show();
                System.out.println("Phone state Idle");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //when Off hook i.e in call
                //Make intent and start your service here
                Toast.makeText(context, "Phone state Off hook", Toast.LENGTH_LONG).show();
                System.out.println("Phone state Off hook");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                //when Ringing
                Toast.makeText(context, "Phone state Ringing", Toast.LENGTH_LONG).show();
                System.out.println("Phone state Ringing");
                String name = getContactName(incomingNumber);
                sendMessage(name + " is calling");
                break;
            default:
                break;
        }
    }

    private void sendMessage(String msg) {
        telegram.sendMessage(YAIR_ID, msg, new TelegramCallback<Message>() {
            @Override
            public void onResponse(Call call, Message response) {
            }

            @Override
            public void onFailure(Call call, Exception e) {
                System.out.println("Failed to send telegram message");
            }
        });
    }

    private String getContactName(final String phoneNumber) {
        if (phoneNumber.equals("")) {
            return "Private Number";
        }
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

            String contactName = "";
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(0);
                }
                cursor.close();
            }

            if (contactName.equals("")){
                return phoneNumber;
            }
            return contactName;
        } catch (Exception e) {
            return phoneNumber;
        }
    }


}