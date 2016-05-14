package com.qtech.cleverfarmer.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qtech.cleverfarmer.LocationApplication;
import com.qtech.cleverfarmer.R;
import com.qtech.cleverfarmer.Weather.WeatherInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Nongzuowu extends Activity {
    private TextView locationTextView;
    private TextView weatherTextView;
    private WeatherInfo weatherInfo = new WeatherInfo();

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    weatherTextView.setText("天气获取失败\n请检查网络是否连接");
                    break;
                case 1:
                    weatherInfo.parseJson((String) msg.obj);
                    UpdateText();
                    break;
            }
        }

        ;
    };

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_nongzuowu);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        locationTextView = (TextView) findViewById(R.id.LocationText);
        locationTextView.setText(((LocationApplication) getApplication()).locationString);


        ImageButton btn1, btn2, btn3, btn4;

        @SuppressWarnings("unused")
        final TextView t1, t2, t3, t4;

        t1 = (TextView) findViewById(R.id.yumi1);
        t2 = (TextView) findViewById(R.id.huasheng1);
        t3 = (TextView) findViewById(R.id.xiaomai1);
        t4 = (TextView) findViewById(R.id.dadou1);
        weatherTextView = (TextView) findViewById(R.id.tianqi);


        btn1 = (ImageButton) findViewById(R.id.yumi);
        btn2 = (ImageButton) findViewById(R.id.huasheng);
        btn3 = (ImageButton) findViewById(R.id.xiaomai);
        btn4 = (ImageButton) findViewById(R.id.dadou);

        //////////////////////////天气显示/////////////////////////////
        GetJson();


        btn1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent a = new Intent();
                String n1 = t1.getText().toString();
                a.putExtra("n", n1);
                a.setClass(Nongzuowu.this, Turang.class);
                Nongzuowu.this.startActivity(a);
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent();
                String n1 = t2.getText().toString();
                a.putExtra("n", n1);
                a.setClass(Nongzuowu.this, Turang.class);
                Nongzuowu.this.startActivity(a);
            }

        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent();
                String n1 = t3.getText().toString();
                a.putExtra("n", n1);
                a.setClass(Nongzuowu.this, Turang.class);
                Nongzuowu.this.startActivity(a);
            }

        });

        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });

    }


    //更新天气textView
    public void UpdateText() {
        if (weatherInfo.city != null) {
            weatherTextView.setText(weatherInfo.city + "当前气温是:" + weatherInfo.tmp + "℃");
            weatherTextView.append("\n" + weatherInfo.txt + " " + weatherInfo.wind_dir + " " + weatherInfo.wind_level + "级");
            weatherTextView.append("\n湿度:" + weatherInfo.hum + "%");
        } else {
            weatherTextView.setText("天气获取失败");
        }
    }

    //从网站上获取JSON数据，并发消息给主线程
    private void GetJson() {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                String httpUrl = "http://apis.baidu.com/heweather/weather/free";
                String httpArg = "city=weihai";
                BufferedReader reader = null;
                String result = null;
                StringBuffer sbf = new StringBuffer();
                httpUrl = httpUrl + "?" + httpArg;

                try {
                    URL url = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("apikey", "f1375c3a44007cdc331966524783f3ab");
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String strRead = null;
                    while ((strRead = reader.readLine()) != null) {
                        sbf.append(strRead);
                        sbf.append("\r\n");
                    }
                    reader.close();
                    result = sbf.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Message msg = handler.obtainMessage();
                msg.obj = result;
                //0获取失败  1获取成功
                if (sbf.toString().isEmpty()) {
                    msg.what = 0;
                } else {
                    msg.what = 1;
                }
                handler.sendMessage(msg);

            }
        }.start();
    }
}
