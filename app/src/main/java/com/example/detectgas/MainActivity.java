package com.example.detectgas;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        đoạn code test thông báo




//        đoạn code test thông báo

        EditText editText = findViewById(R.id.editText);
        editText.setText("100");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference gasRef = database.getReference("/Gas");
        DatabaseReference tempRef = database.getReference("/DHT11/temp");
        DatabaseReference humiRef = database.getReference("/DHT11/humi");


        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Float temp = dataSnapshot.getValue(Float.class);
                // Hiển thị message trong TextView của bạn
                TextView textView = findViewById(R.id.temperature_value);
                textView.setText(temp.toString() + "°C");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

        humiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Float humi = dataSnapshot.getValue(Float.class);
                // Hiển thị message trong TextView của bạn
                TextView textView = findViewById(R.id.humidity_value);
                textView.setText(humi.toString() + "%");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

        LinearLayout linearLayout = findViewById(R.id.linearlayout1);

        gasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer gas = dataSnapshot.getValue(int.class);
                if (gas > 200 && gas < 400) {
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.pastel_xam));
                } else if (gas > 400 && gas < 600) {
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.pastel_xam1));
                }else if (gas > 600 ) {
                    addNotification();
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.pastel_xam2));
                }else{
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.hong));
                }

                TextView textView = findViewById(R.id.gas_value);
                textView.setText(gas.toString() + "ppm");
                // Hiển thị message trong TextView của bạn


            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error);
            }
        });


        Switch switch_Manual = findViewById(R.id.manual_switch);
        Switch switch_Auto = findViewById(R.id.auto_switch);
        Switch switchWindow = findViewById(R.id.door_switch);
        Switch switchFan = findViewById(R.id.fan_switch);

        TextView textView = findViewById(R.id.manual);
        TextView textViewAuto = findViewById(R.id.auto);

        ImageView imageFan = findViewById(R.id.fan);
        ImageView imageWin = findViewById(R.id.door);

        switchWindow.setEnabled(false);
        switchFan.setEnabled(false);

        switch_Manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    switch_Auto.setEnabled(false);
                    switchWindow.setEnabled(true);
                    switchFan.setEnabled(true);
                    textViewAuto.setTextColor(Color.GRAY);

                    //thực hiện window bên trong manual.
                    switchWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference window_Switch = database.getReference("window");
                                window_Switch.setValue(1);
                            }
                            else {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference window_Switch = database.getReference("window");
                                window_Switch.setValue(0);
                            }
                        }
                    });

                    switchFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference fan_Switch = database.getReference("quat");
                                fan_Switch.setValue(1);
                            }
                            else {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference fan_Switch = database.getReference("quat");
                                fan_Switch.setValue(0);
                            }
                        }
                    });
                }
                else {
                    // Công tắc được tắt

                    switchWindow.setChecked(false);
                    switchFan.setChecked(false);

                    switchWindow.setEnabled(false);
                    switchFan.setEnabled(false);

                    textView.setTextColor(Color.BLACK);
                    switch_Auto.setEnabled(true);
                    textViewAuto.setTextColor(Color.BLACK);
                }
            }
        });

        //Kiểu tra số liệu của editText
        EditText myEditText = findViewById(R.id.editText);
        myEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra giá trị vừa được nhập vào EditText
                String input = s.toString();
                if (!input.isEmpty()) {
                    int value = Integer.parseInt(input);
                    if (value < 1 || value > 1000) {
                        // Nếu giá trị nhập vào không nằm trong giới hạn 1 đến 1000, hiển thị thông báo lỗi
                        myEditText.setError("Giá trị phải nằm trong khoảng từ 1 đến 1000");
                    } else {
                        // Nếu giá trị nhập vào hợp lệ, xử lý dữ liệu
                        // ...
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });

        
        //EditText editText = findViewById(R.id.editText);
        switch_Auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    int gasData = Integer.parseInt(editText.getText().toString());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference Gas_autoRef = database.getReference("Gas");

                    showNotification();

                    Gas_autoRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int GasFirebase = dataSnapshot.getValue(Integer.class);
                            if(GasFirebase >= gasData){
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference fan_Switch = database.getReference().child("/quat");
                                fan_Switch.setValue(1);
                                switchWindow.setChecked(true);

                                DatabaseReference window_Switch = database.getReference().child("/window");
                                window_Switch.setValue(1);
                                switchFan.setChecked(true);
                            }
                            // Các trường hợp khác tắt hết
                            else{
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference fan_Switch = database.getReference().child("/quat");
                                fan_Switch.setValue(0);
                                switchWindow.setChecked(false);

                                DatabaseReference window_Switch = database.getReference().child("/window");
                                window_Switch.setValue(0);
                                switchFan.setChecked(false);

                                DatabaseReference coi_signal = database.getReference().child("/coi");
                                coi_signal.setValue(0);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Xử lý khi có lỗi xảy ra
                        }
                    });

                }
                else{
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference fan_Switch = database.getReference().child("/quat");
                    fan_Switch.setValue(0);
                    switchWindow.setChecked(false);

                    DatabaseReference window_Switch = database.getReference().child("/window");
                    window_Switch.setValue(0);
                    switchFan.setChecked(false);
                }
            }
        });





    }
    private static final String CHANNEL_ID = "my_channel_id";
    private static final String CHANNEL_NAME = "my_channel_name";
    private static final String CHANNEL_DESCRIPTION = "my_notification_channel_description";

    private void addNotification() {
        // Tạo NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo NotificationChannel cho phiên bản Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.gas)
                .setContentTitle("Thông báo mới")
                .setContentText("Nồng độ gas đã quá ngưỡng, nguy cơ cháy nổ.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Hiển thị thông báo
        notificationManager.notify(0, builder.build());
    }
    private void showNotification() {
        Toast.makeText(this, "Bạn đã bật chế độ tự động", Toast.LENGTH_SHORT).show();
    }
}