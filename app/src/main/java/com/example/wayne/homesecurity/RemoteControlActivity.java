package com.example.wayne.homesecurity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class RemoteControlActivity extends AppCompatActivity {
    private SeekBar camSeekbar;
    private TextView camTextview;
    private ToggleButton alarmToggle;

    static String servoTopic = "csw/iot/servo";
    static String buzzerTopic = "csw/iot/buzzer";

    static String MQTTHOST = "tcp://iot.eclipse.org:1883";
    MqttAndroidClient client;
    IMqttToken token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camSeekbar = findViewById(R.id.cameraSeekbar);
        camTextview = findViewById(R.id.cameraMsg);
        alarmToggle = findViewById(R.id.alarmToggle);

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


    private class seekBarListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
            // Log the progress
            Log.d("DEBUG", "Progress is: "+value);
            //set textView's text
            camTextview.setText("Camera angle : "+value);

            publishMQTT(servoTopic, Integer.toString(value));

        }
        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    private void publishMQTT(final String topic, final String message) {
        try {
            client.publish(topic, message.getBytes(), 0, false);
            Toast.makeText(RemoteControlActivity.this, "Topic: " + topic + "  || Msg: " + message, Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
