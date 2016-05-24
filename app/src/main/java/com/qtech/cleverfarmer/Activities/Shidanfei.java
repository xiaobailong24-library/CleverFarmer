package com.qtech.cleverfarmer.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.qtech.cleverfarmer.LocationApplication;
import com.qtech.cleverfarmer.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Shidanfei extends Activity {

    private static final String TAG = "Shidanfei";

    private TextView locationTextView;
    private TextView tv1, tv2, tv3, tv4, ttv1, ttv2, ttv3;
    @SuppressWarnings("unused")
    private Button btn1, btn2, btn3, btn4, btn5;
    private String text5, text6, text7, NF2, text10, text11, text12, lable4;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Bundle bundle = (Bundle) msg.obj;

                    ttv1.setText(bundle.getString("simpN"));
                    ttv2.setText(bundle.getString("simpP"));
                    ttv3.setText(bundle.getString("simpK"));

                    tv1.setText(bundle.getString("NF1"));
                    tv2.setText(bundle.getString("PF1"));
                    tv3.setText(bundle.getString("KF1"));
                    tv4.setText(bundle.getString("NF2"));

                    text5 = bundle.getString("Text5");
                    text6 = bundle.getString("Text6");
                    text7 = bundle.getString("Text7");
                    NF2 = bundle.getString("NF2");
                    text10 = bundle.getString("Text10");
                    text11 = bundle.getString("Text11");
                    text12 = bundle.getString("Text12");
                    lable4 = bundle.getString("lable4");
                    break;
            }

        }

        ;
    };

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_shidanfei);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        Bundle bundle = getIntent().getExtras();

        String area = bundle.getString("area");
        Log.e(TAG, "onCreate: area->" + area);
        String crop = bundle.getString("crop");

        // TODO: 2016/5/21
        String fullN = bundle.getString("fullN");
        String validP = bundle.getString("validP");
        String fastK = bundle.getString("fastK");
        String organic = bundle.getString("organic");
        Log.e(TAG, "onCreate: " + "fullN:" + fullN + ",validP:" + validP + ",fastK:" + fastK + ",organic:" + organic);

        String NF = bundle.getString("NFer");
        String PF = bundle.getString("PFer");
        String KF = bundle.getString("KFer");
        String t = bundle.getString("targetPro");
        String N = bundle.getString("N");
        String o5p2 = bundle.getString("O5P2");
        String clk = bundle.getString("CLK");
        int way = bundle.getInt("way");

        String lon = ((LocationApplication) getApplication()).Longtitude;
        String lat = ((LocationApplication) getApplication()).Latitude;

        getData(way, area, lon, lat, crop, fullN, validP, fastK, organic, NF, PF, KF, t, N, o5p2, clk);

        locationTextView = (TextView) findViewById(R.id.LocationText);
        locationTextView.setText(((LocationApplication) getApplication()).locationString);

        ttv1 = (TextView) findViewById(R.id.ttv1);
        ttv2 = (TextView) findViewById(R.id.ttv2);
        ttv3 = (TextView) findViewById(R.id.ttv3);

        tv1 = (TextView) findViewById(R.id.tv_danfei);
        tv2 = (TextView) findViewById(R.id.tv_linfei);
        tv3 = (TextView) findViewById(R.id.tv_jiafei);
        tv4 = (TextView) findViewById(R.id.tv_niaosu);

        btn2 = (Button) findViewById(R.id.fuhefeijisuan);
        btn3 = (Button) findViewById(R.id.duanxin);   //绑定发送短信按钮
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent xuanze_intent = new Intent(Shidanfei.this, Fuhefei.class);
                Bundle bundle = new Bundle();
                bundle.putString("Text5", text5);
                bundle.putString("Text6", text6);
                bundle.putString("Text7", text7);
                bundle.putString("NF2", NF2);
                bundle.putString("Text10", text10);
                bundle.putString("Text11", text11);
                bundle.putString("Text12", text12);
                bundle.putString("lable4", lable4);
                xuanze_intent.putExtras(bundle);
                startActivity(xuanze_intent);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse("smsto:");    //给哪个手机号发送短信
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                String body = "[基肥施肥量]" + ttv1.getText().toString() + tv1.getText().toString() + "公斤/亩 "
                        + ttv2.getText().toString() + tv2.getText().toString() + "公斤/亩 "
                        + ttv3.getText().toString() + tv3.getText().toString() + "公斤/亩\n"
                        + "[追肥施肥量]" + "尿素:" + tv4.getText().toString() + "公斤/亩 ";
                intent.putExtra("sms_body", body);
                startActivity(intent);
            }
        });
    }


    public void getData(final int way, final String area, final String lon, final String lat, final String crop,
                        final String fullN, final String validP, final String fastK, final String organic,
                        final String NF, final String PF, final String KF,
                        final String t, final String N, final String o5p2, final String clk){
        Log.e(TAG, "getData: way=" + way);
        String httpUrl="http://115.28.180.110/app_interface/cetuProcess.php";
        final String[] httpArg_area = {""};
        switch(way){
            case 1:
                httpUrl = "http://115.28.180.110/app_interface/cetuProcess.php";
                break;
            case 2:
                httpUrl = "http://115.28.180.110/app_interface/cetuGPS.php";
                break;
            case 3:
                httpUrl = "http://115.28.180.110/app_interface/user_defined.php";
                break;
        }
//        Log.e(TAG, "getData: httpUrl" + httpUrl);
        final String finalHttpUrl = httpUrl;
//        Log.e(TAG, "getData: httpUrl" + finalHttpUrl);
        final String[] path = new String[1];
        Thread thread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                String httpArg_crop = null;
                String httpArg_N = null;
                String httpArg_P = null;
                String httpArg_K = null;
                try {
                    httpArg_area[0] = URLEncoder.encode(area, "UTF-8");
                    httpArg_crop = URLEncoder.encode(crop, "UTF-8");
                    httpArg_N = URLEncoder.encode(NF, "UTF-8");
                    httpArg_P = URLEncoder.encode(PF, "UTF-8");
                    httpArg_K = URLEncoder.encode(KF, "UTF-8");

                } catch (UnsupportedEncodingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                switch(way){
                    case 1:     //1是地区
                        path[0] = finalHttpUrl + "?area=" + httpArg_area[0] + "&targetPro=" + t + "&Crops=" + httpArg_crop
                                + "&N=" + httpArg_N + "&P=" + httpArg_P + "&K=" + httpArg_K
                                + "&N1=" + N + "&P1=" + o5p2 + "&K1=" + clk;
                        break;
                    case 2:     //2是GPS
                        path[0] = finalHttpUrl + "?longtitude=" + lon + "&latitude=" + lat + "&targetPro=" + t + "&Crops=" + httpArg_crop
                                + "&N=" + httpArg_N + "&P=" + httpArg_P + "&K=" + httpArg_K
                                + "&N1=" + N + "&P1=" + o5p2 + "&K1=" + clk;
                        break;
                    case 3:     //3是手动输入
                        path[0] = finalHttpUrl + "?Crops=" + httpArg_crop + "&targetPro=" + t
                                + "&longtitude=" + lon + "&latitude=" + lat
                                + "&jianjiedan=" + fullN + "&fastK=" + fastK + "&validP=" + validP
                                + "&N_kind=" + NF + "&P_kind=" + PF + "&K_kind=" + KF
                                + "&fer_N=" + N + "&fer_P=" + o5p2 + "&fer_K=" +  clk + "&organic=" + organic;
                        break;
                }

                Log.e(TAG, "getData: path[0]->" + path[0].toString());
                try {
                    URL url = new URL(path[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String strRead = null;
                        StringBuffer sbf = new StringBuffer();
                        while ((strRead = reader.readLine()) != null) {
                            sbf.append(strRead);
                            sbf.append("\r\n");
                        }
                        reader.close();
                        Log.e(TAG, "getData: sbf->" + sbf.toString());
                        paresJson(sbf.toString());
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void paresJson(String strResult) {
        try {
            JSONObject root = new JSONObject(strResult);
            JSONObject data = root.getJSONObject("data");

            String NF1 = data.getString("NF1");
            String PF1 = data.getString("PF1");
            String KF1 = data.getString("KF1");
            String NF2 = data.getString("NF2");
            String Text5 = data.getString("Text5");
            String Text6 = data.getString("Text6");
            String Text7 = data.getString("Text7");
            String Text10 = data.getString("Text10");
            String Text11 = data.getString("Text11");
            String Text12 = data.getString("Text12");
            String lable4 = data.getString("lable4");
            String simpN = data.getString("simpN");
            String simpP = data.getString("simpP");
            String simpK = data.getString("simpK");

            Bundle bundle = new Bundle();
            bundle.putString("NF1", NF1);
            bundle.putString("PF1", PF1);
            bundle.putString("KF1", KF1);
            bundle.putString("NF2", NF2);
            bundle.putString("Text5", Text5);
            bundle.putString("Text6", Text6);
            bundle.putString("Text7", Text7);
            bundle.putString("Text10", Text10);
            bundle.putString("Text11", Text11);
            bundle.putString("Text12", Text12);
            bundle.putString("lable4", lable4);
            bundle.putString("simpN", simpN);
            bundle.putString("simpP", simpP);
            bundle.putString("simpK", simpK);

            Message msg = handler.obtainMessage();
            msg.what = 0;
            msg.obj = bundle;
            handler.sendMessage(msg);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
