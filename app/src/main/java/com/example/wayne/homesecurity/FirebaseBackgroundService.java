package com.example.wayne.homesecurity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import static android.content.ContentValues.TAG;

public class FirebaseBackgroundService extends Service {

    private DatabaseReference mDatabase;
    private static int counter;
    private static boolean running = false;

    public FirebaseBackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        counter = 0;
        mDatabase = FirebaseDatabase.getInstance().getReference(HomeSecurityMainActivity.DATABASE_PATH_UPLOADS);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                if(counter==0||counter==1) {
                    postNotif();
                    counter++;
                } else {
                    if(!running) {
                        running = new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                counter = 0;
                                running = false;
                            }
                        }, 30000);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void postNotif() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "Home Security";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, ViewImageActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        //Toast.makeText(getApplicationContext(), "Motion is detected at home...", Toast.LENGTH_SHORT).show();
        Notification n  = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            n = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("New Firebase Image")
                    .setContentText("Motion is detected at home...")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .setStyle(new Notification.BigTextStyle().bigText("")).build();
        } else {
            n = new Notification.Builder(this)
                    .setContentTitle("New Firebase Image")
                    .setContentText("Motion is detected at home...")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigTextStyle().bigText("")).build();
        }
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(0, n);
    }
}
