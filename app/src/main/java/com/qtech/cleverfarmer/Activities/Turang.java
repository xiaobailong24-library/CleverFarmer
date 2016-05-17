package com.qtech.cleverfarmer.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qtech.cleverfarmer.CustomView.CustomDialog;
import com.qtech.cleverfarmer.CustomView.SpinnerAdapter;
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

public class Turang extends Activity implements SpinnerAdapter {

    private static final String TAG = "Turang";

    CustomDialog customDialog;
    View layout;
    ArrayAdapter<String> adapter1 = null;    //市级适配器
    ArrayAdapter<String> adapter2 = null;    //县级适配器
    ArrayAdapter<String> adapter3 = null;    //乡级适配器
    ArrayAdapter<String> adapter4 = null;    //村级适配器
    int spinner1Position = 0;
    int spinner2Position = 0;
    int spinner3Position = 0;

    private TextView locationTextView;
    private String latitude, longitude; // 经度和纬度
    private Spinner s1, s2, s3;
    private TextView title;
    private Button selectAddress, gpsButton, submit;
    public EditText et1;
    public static EditText et2;
    public static EditText et3;
    public static EditText et4;
    public EditText et5;
    public EditText et6;
    public EditText et7;

    private static int way = 0; // 获取土地信息的方式, 0是还没获取，1是地区 2是GPS
    private static String area; // 地区
    private String crop; // 农作物
    private String NFer; // 氮肥
    private String PFer; // 磷肥
    private String KFer; // 钾肥

    private static Activity turaung_ac;

    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0: // 更新textView
                    Bundle bundle = (Bundle) msg.obj;
                    et2.setText(bundle.getString("fullN"));
                    et3.setText(bundle.getString("validP"));
                    et4.setText(bundle.getString("fastK"));
                    break;
                case 1: // 根据地区获取数据
                    area = (String) msg.obj;
                    getDataByArea(area);
                    way = 1;
                    break;
                case 2: // 根据GPS获取数据
                    way = 2;
                    break;
                case 3: //不在威海市时，位置信息没有数据
                    Toast.makeText(turaung_ac, "位置信息有误，不在威海市", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        ;
    };

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_turang);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        turaung_ac = Turang.this;

        title = (TextView) findViewById(R.id.biaoti);
        Intent a = getIntent();
        crop = a.getStringExtra("n");
        title.setText(crop + "土壤化验数据输入");

        locationTextView = (TextView) findViewById(R.id.LocationText);
        locationTextView
                .setText(((LocationApplication) getApplication()).locationString);
        latitude = ((LocationApplication) getApplication()).Latitude;
        longitude = ((LocationApplication) getApplication()).Longtitude;

        et1 = (EditText) findViewById(R.id.et_mubiao);
        et2 = (EditText) findViewById(R.id.et_quandan);
        et3 = (EditText) findViewById(R.id.et_youxiaolin);
        et4 = (EditText) findViewById(R.id.et_suxiaojia);
        et5 = (EditText) findViewById(R.id.et_chundan);
        et6 = (EditText) findViewById(R.id.et_wuyanghuaerlin);
        et7 = (EditText) findViewById(R.id.et_yanghuajia);

        if (crop.equals("小麦") || crop.equals("玉米")) {
            et1.setHint("取值范围:350-650");
        } else if (crop.equals("花生")) {
            et1.setHint("取值范围:200-500");
        }

        // ////////////////////////////////////跳转/////////////////////////////////////////////////
        submit = (Button) findViewById(R.id.tuijianshifei);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent xuanze_intent = new Intent(Turang.this, Shidanfei.class);
                Bundle bundle = new Bundle();
                bundle.putString("area", area);
                bundle.putString("crop", crop);
                bundle.putString("NFer", NFer);
                bundle.putString("PFer", PFer);
                bundle.putString("KFer", KFer);
                bundle.putString("targetPro", et1.getText().toString());
                bundle.putString("N", et5.getText().toString());
                bundle.putString("O5P2", et6.getText().toString());
                bundle.putString("CLK", et7.getText().toString());
                bundle.putInt("way", way);
                xuanze_intent.putExtras(bundle);
                if (!et1.getText().toString().isEmpty()
                        && !et5.getText().toString().isEmpty()
                        && !et6.getText().toString().isEmpty()
                        && !et7.getText().toString().isEmpty()) {
                    // 目标产量满足取值范围
                    if (crop.equals("小麦") || crop.equals("玉米")) {
                        if (Integer.parseInt(et1.getText().toString()) >= 350
                                && Integer.parseInt(et1.getText().toString()) <= 650) {
                            if (area != null) {
                                if ((Integer.parseInt(et5.getText().toString())
                                        + Integer.parseInt(et6.getText()
                                        .toString()) + Integer
                                        .parseInt(et7.getText().toString())) <= 100
                                        && (Integer.parseInt(et5.getText()
                                        .toString())
                                        + Integer.parseInt(et6
                                        .getText().toString()) + Integer
                                        .parseInt(et7.getText()
                                                .toString())) >= 0) {
                                    Turang.this.startActivity(xuanze_intent);
                                } else {
                                    Toast.makeText(Turang.this, "复合肥料不能超过100",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Turang.this, "请选择地区",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Turang.this, "目标产量要在350-650之间",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else if (crop.equals("花生")) {
                        if (Integer.parseInt(et1.getText().toString()) >= 200
                                && Integer.parseInt(et1.getText().toString()) <= 500) {
                            if (way != 0) {
                                if ((Integer.parseInt(et5.getText().toString())
                                        + Integer.parseInt(et6.getText()
                                        .toString()) + Integer
                                        .parseInt(et7.getText().toString())) <= 100
                                        && (Integer.parseInt(et5.getText()
                                        .toString())
                                        + Integer.parseInt(et6
                                        .getText().toString()) + Integer
                                        .parseInt(et7.getText()
                                                .toString())) >= 0) {
                                    Turang.this.startActivity(xuanze_intent);
                                } else {
                                    Toast.makeText(Turang.this, "复合肥料不能超过100",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Turang.this, "请先获取土地信息",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Turang.this, "目标产量要在200-500之间",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(Turang.this, "有数据未填，请填写数据",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ////////////////////////////////////按地区获取数据//////////////////////////////////
        initSpinner();
        customDialog = new CustomDialog(this);
        customDialog.setView(layout);
        selectAddress = (Button) findViewById(R.id.villageButton);
        selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 选择地址
                Log.e("selectAddress", "onClick");
                customDialog.show();
            }

        });

        // ////////////////////////////////////按GPS获取数据//////////////////////////////////
        gpsButton = (Button) findViewById(R.id.gpsButton);
        gpsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("Fu", "经度:" + latitude);
                Log.i("Fu", "纬度:" + longitude);
                getDataByGPS(latitude, longitude);
            }
        });

        // ////////////////////////////////////spinner////////////////////////////////////////

        s1 = (Spinner) this.findViewById(R.id.danfeixuanze);
        s2 = (Spinner) this.findViewById(R.id.linfeixuanze);
        s3 = (Spinner) this.findViewById(R.id.jiafeixuanze);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.colors1,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // Toast.makeText(turuang.this,"position:"+position+"id:"+id+"value:"
                // +s1.getSelectedItem().toString(),
                // Toast.LENGTH_SHORT).show();
                NFer = s1.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Toast.makeText(turuang.this,"unselected",
                // Toast.LENGTH_SHORT).show();
            }

        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.colors2,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(adapter2);
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // Toast.makeText(turuang.this,"position:"+position+"id:"+id+"value:"
                // +s2.getSelectedItem().toString(),
                // Toast.LENGTH_SHORT).show();
                PFer = s2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Toast.makeText(turuang.this,"unselected",
                // Toast.LENGTH_SHORT).show();
            }

        });

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                this, R.array.colors3,
                android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s3.setAdapter(adapter3);
        s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // Toast.makeText(turuang.this,"position:"+position+"id:"+id+"value:"
                // +s3.getSelectedItem().toString(),
                // Toast.LENGTH_SHORT).show();
                KFer = s3.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Toast.makeText(turuang.this,"unselected",
                // Toast.LENGTH_SHORT).show();
            }

        });

    }

    // ////以上都是在onCreate方法里面

    public static void getDataByArea(final String area) {
        Thread t = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                String httpUrl = "http://115.28.180.110/app_interface/soilmes.php";
                String httpArg = null;
                try {
                    httpArg = URLEncoder.encode(area, "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                String path = httpUrl + "?area=" + httpArg;
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is, "UTF-8"));
                        String strRead = null;
                        StringBuffer sbf = new StringBuffer();
                        while ((strRead = reader.readLine()) != null) {
                            sbf.append(strRead);
                            sbf.append("\r\n");
                        }
                        reader.close();
                        // System.out.println(sbf.toString());
                        paresJson(sbf.toString());
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private static void getDataByGPS(final String lat, final String lon) {
        Thread t = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                String httpUrl = "http://115.28.180.110/app_interface/soilmesGPS.php";
                String path = httpUrl + "?longtitude=" + lon + "&latitude="
                        + lat;
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is, "UTF-8"));
                        String strRead = null;
                        StringBuffer sbf = new StringBuffer();
                        while ((strRead = reader.readLine()) != null) {
                            sbf.append(strRead);
                            sbf.append("\r\n");
                        }
                        reader.close();
                        handler.sendEmptyMessage(2); // 发送一个空消息
                        // System.out.println(sbf.toString());
                        Log.i("Fu", sbf.toString());

                        JSONObject jsonObject_root = new JSONObject(sbf.toString());
                        JSONObject jsonObject_data = jsonObject_root.getJSONObject("data");
                        String queryNull = jsonObject_data.getString("queryNull");
                        if (queryNull.equals("404")){
                            Message msg = handler.obtainMessage();
                            msg.what = 3;
                            handler.sendMessage(msg);
                        } else {
                            paresJson(sbf.toString());
                        }

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private static void paresJson(String strResult) {
        try {
            JSONObject jsonObject_root = new JSONObject(strResult);
            JSONObject jsonObject_data = jsonObject_root.getJSONObject("data");
            String validP = jsonObject_data.getString("validP");
            String fastK = jsonObject_data.getString("fastK");
            String fullN = jsonObject_data.getString("fullN");

            // System.out.println(message);
            // System.out.println(validP);
            // System.out.println(fastK);
            // System.out.println(fullN);

            Bundle bundle = new Bundle();
            bundle.putString("validP", validP);
            bundle.putString("fastK", fastK);
            bundle.putString("fullN", fullN);

            Message msg = handler.obtainMessage();
            msg.what = 0;
            msg.obj = bundle;
            handler.sendMessage(msg);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // TODO: 2016/5/14
    private void initSpinner() {
        Log.e(TAG, "initSpinner");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        layout = layoutInflater.inflate(R.layout.custom_dialog, null);
        final Spinner spinner1 = (Spinner) layout.findViewById(R.id.sel_spinner1);
        final Spinner spinner2 = (Spinner) layout.findViewById(R.id.sel_spinner2);
        final Spinner spinner3 = (Spinner) layout.findViewById(R.id.sel_spinner3);
        final Spinner spinner4 = (Spinner) layout.findViewById(R.id.sel_spinner4);

        //市
        adapter1 = new ArrayAdapter<String>(Turang.this,
                android.R.layout.simple_spinner_item, str_spinner1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(0, true);  //设置默认选中项，此处为默认选中第1个值
        //县
        adapter2 = new ArrayAdapter<String>(Turang.this,
                android.R.layout.simple_spinner_item, str_spinner2[0]);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(0, true);  //默认选中第0个
        //乡镇
        adapter3 = new ArrayAdapter<String>(Turang.this,
                android.R.layout.simple_spinner_item, str_spinner3[0][0]);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);
        spinner3.setSelection(0, true);
        //村庄
        adapter4 = new ArrayAdapter<String>(Turang.this,
                android.R.layout.simple_spinner_item, str_spinner4[0][0][0]);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(adapter4);
        spinner4.setSelection(0, true);

        spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                //arg2为当前省级选中的值的序号
                //将地级适配器的值改变为city[position]中的值
                adapter2 = new ArrayAdapter<String>(
                        Turang.this, android.R.layout.simple_spinner_item, str_spinner2[arg2]);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // 设置二级下拉列表的选项内容适配器
                spinner2.setAdapter(adapter2);
                spinner1Position = arg2;    //记录当前市级序号，留给下面修改县级适配器时用

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    /*
     *
	 *
	 */
        spinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                adapter3 = new ArrayAdapter<String>(Turang.this,
                        android.R.layout.simple_spinner_item, str_spinner3[spinner1Position][arg2]);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(adapter3);
                spinner2Position = arg2;    //记录当前级序号，留给下面修改县级适配器时用
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spinner3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                adapter4 = new ArrayAdapter<String>(Turang.this,
                        android.R.layout.simple_spinner_item, str_spinner4[spinner1Position][spinner2Position][arg2]);
                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner4.setAdapter(adapter4);
                spinner3Position = arg2;    //记录当前级序号，留给下面修改县级适配器时用
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        Button okButton = (Button) layout.findViewById(R.id.dialog_button_ok);
        Button cancelButton = (Button) layout.findViewById(R.id.dialog_button_cancel);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "okButton clicked");
                customDialog.dismiss();
                Log.d(TAG, spinner1.getSelectedItem().toString() + spinner2.getSelectedItem()
                        + spinner3.getSelectedItem() + spinner4.getSelectedItem());
                Toast.makeText(Turang.this, "您选择了" + spinner1.getSelectedItem().toString() + spinner2.getSelectedItem()
                        + spinner3.getSelectedItem() + spinner4.getSelectedItem(), Toast.LENGTH_LONG).show();


                //发送消息
                Message msg = Turang.handler.obtainMessage();
                msg.what = 1;
                msg.obj = spinner4.getSelectedItem().toString();
                Turang.handler.sendMessage(msg);


            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancleButton clicked");
                customDialog.dismiss();
            }
        });

    }
}
