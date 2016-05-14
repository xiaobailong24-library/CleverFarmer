package com.qtech.cleverfarmer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.qtech.cleverfarmer.LocationApplication;
import com.qtech.cleverfarmer.R;

public class Fuhefei extends Activity {
    private TextView locationTextView;
    Button btn1,btn2,btn3,btn4;
    private TextView tv1,tv2,tv3,tv4,tv5,tvt1,tvt2,tvt3;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_fuhefei);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        locationTextView = (TextView) findViewById(R.id.LocationText);
        locationTextView.setText(((LocationApplication)getApplication()).locationString);

        tvt1 = (TextView) findViewById(R.id.tvt_1);
        tvt2 = (TextView) findViewById(R.id.tvt_2);
        tvt3 = (TextView) findViewById(R.id.tvt_3);
        tv1 = (TextView) findViewById(R.id.tv_fu_1);
        tv2 = (TextView) findViewById(R.id.tv_fu_2);
        tv3 = (TextView) findViewById(R.id.tv_fu_3);
        tv4 = (TextView) findViewById(R.id.tv_fu_4);
        tv5 = (TextView) findViewById(R.id.tv_fu_5);

        Bundle bundle = getIntent().getExtras();
        tvt1.setText(bundle.getString("Text10"));
        tvt2.setText(bundle.getString("Text11"));
        tvt3.setText(bundle.getString("Text12"));
        tv1.setText(bundle.getString("Text5"));
        tv2.setText(bundle.getString("Text6"));
        tv3.setText(bundle.getString("Text7"));
        tv4.setText(bundle.getString("NF2"));
        if (bundle.getString("lable4").equals("null")) {
            tv5.setText("无");
        }else {
            tv5.setText(bundle.getString("lable4"));
        }


        btn2 = (Button) findViewById(R.id.fasongduanxin);

        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse("smsto:");    //给哪个手机号发送短信
                Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
                String body = "[基肥施肥量]" + tvt1.getText().toString()+ ":" + tv1.getText().toString() + "公斤/亩 "
                        + tvt2.getText().toString()+ ":" + tv2.getText().toString() + "公斤/亩  "
                        + tvt3.getText().toString()+ ":" + tv3.getText().toString() + "公斤/亩\n"
                        + "[追肥施肥量]" +"尿素:" + tv4.getText().toString() + "公斤/亩\n"
                        + "[施肥时期]" + tv5.getText().toString();
                intent.putExtra("sms_body", body);
                startActivity(intent);
            }
        });


    }
}
