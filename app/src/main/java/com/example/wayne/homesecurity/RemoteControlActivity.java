package com.example.wayne.homesecurity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class RemoteControlActivity extends AppCompatActivity {
    static String servoTopic = "csw/iot/servo";
    static String buzzerTopic = "csw/iot/buzzer";
    static String MQTTHOST = "tcp://iot.eclipse.org:1883";
    MqttAndroidClient client;
    IMqttToken token;
    private SeekBar camSeekbar;
    private TextView camTextview;
    private ToggleButton alarmToggle;
    private Button button0;
    private Button button45;
    private Button button90;
    private Button button135;
    private Button button180;
    private EditText address;
    private Button connect;
    private VideoView streamView;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camSeekbar = findViewById(R.id.cameraSeekbar);
        camTextview = findViewById(R.id.cameraMsg);
        alarmToggle = findViewById(R.id.alarmToggle);
        button0 = findViewById(R.id.button0);
        button45 = findViewById(R.id.button45);
        button90 = findViewById(R.id.button90);
        button135 = findViewById(R.id.button135);
        button180 = findViewById(R.id.button180);
        address = findViewById(R.id.address);
        connect = findViewById(R.id.connect);
        streamView = findViewById(R.id.videoView);

        camSeekbar.setOnSeekBarChangeListener(new seekBarListener());

        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publishMQTT(buzzerTopic, "1");
                } else {
                    publishMQTT(buzzerTopic, "0");
                }
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = address.getText().toString();
            }
        });

        button0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (camSeekbar.getProgress() != 0) {
                    camTextview.setText("Camera angle : 0\u00B0");
                    camSeekbar.setProgress(0);
                    publishMQTT(servoTopic, Integer.toString(0));
                }
            }
        });

        button45.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (camSeekbar.getProgress() != 45) {
                    camTextview.setText("Camera angle : 45\u00B0");
                    camSeekbar.setProgress(45);
                    publishMQTT(servoTopic, Integer.toString(45));
                }
            }
        });

        button90.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (camSeekbar.getProgress() != 90) {
                    camTextview.setText("Camera angle : 90\u00B0");
                    camSeekbar.setProgress(90);
                    publishMQTT(servoTopic, Integer.toString(90));
                }
            }
        });

        button135.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (camSeekbar.getProgress() != 135) {
                    camTextview.setText("Camera angle : 135\u00B0");
                    camSeekbar.setProgress(135);
                    publishMQTT(servoTopic, Integer.toString(135));
                }
            }
        });

        button180.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (camSeekbar.getProgress() != 180) {
                    camTextview.setText("Camera angle : 180\u00B0");
                    camSeekbar.setProgress(180);
                    publishMQTT(servoTopic, Integer.toString(180));
                }
            }
        });


        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        try{
            token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(RemoteControlActivity.this, "MQTT Connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(RemoteControlActivity.this, "MQTT Connection Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }catch(MqttException e){
            e.printStackTrace();
        }

    }

    private void playStream(String src) {
        Uri UriSrc = Uri.parse(src);
        if (UriSrc == null) {
            Toast.makeText(RemoteControlActivity.this,
                    "UriSrc == null", Toast.LENGTH_LONG).show();
        } else {
            streamView.setVideoURI(UriSrc);
            mediaController = new MediaController(this);
            streamView.setMediaController(mediaController);
            streamView.start();

            Toast.makeText(RemoteControlActivity.this, "Connect: " + src, Toast.LENGTH_LONG).show();
        }
    }

    private void publishMQTT(final String topic, final String message) {
        try {
            client.publish(topic, message.getBytes(), 0, false);
            //Toast.makeText(MainActivity.this, "Topic: " + topic + "  || Msg: " + message, Toast.LENGTH_SHORT).show();
            if (topic.contains("servo")) {
                Toast.makeText(RemoteControlActivity.this, "Servo set to " + message + "\u00B0", Toast.LENGTH_SHORT).show();
            } else if (topic.contains("buzzer")) {
                if (message.equals("1")) {
                    Toast.makeText(RemoteControlActivity.this, "Alarm: Activated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RemoteControlActivity.this, "Alarm: Deactivated", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        streamView.stopPlayback();
    }

    private class seekBarListener implements SeekBar.OnSeekBarChangeListener {

        int angle = 0;

        public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
            // Log the progress
            Log.d("DEBUG", "Progress is: "+value);
            //set textView's text
            camTextview.setText("Camera angle : " + value + "\u00B0");
            angle = value;

        }
        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {
            publishMQTT(servoTopic, Integer.toString(angle));
        }

    }
}
