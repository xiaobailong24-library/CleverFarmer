package com.qtech.cleverfarmer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.qtech.cleverfarmer.Activities.Nongzuowu;
import com.qtech.cleverfarmer.CustomView.CustomDialog;
import com.qtech.cleverfarmer.Location.LocationService;
import com.qtech.cleverfarmer.Update.UpdateInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Integer.parseInt;

public class MainActivity extends Activity {
    private LocationService locationService;
    private TextView locationTextView;
    private UpdateInfo info;
    private static int versionCode, romoteVersionCode;
    private String TAG = "测试";
    private final int CANCLE_UPDATE = 0;   //取消更新
    private final int UPDATE_CLIENT = 1;   //确定更新
    private final int GET_UPDATEINFO_ERROR = 2;   //获取服务器更新信息失败
    private final int DOWN_ERROR = 3;   //下载新版本失败
    private final int NEED_NOT_UPDATE = 4;      //不用更新
    private final int IS_EXIT = 5;       //是否按后退键
    private final int AUTO_CHECK_UPDATE = 6;       //是否按后退键

    private boolean isExit = false;
    private ImageButton btn1, btn2;

    CustomDialog customDialog;
    View layout;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CLIENT:
                    //对话框通知用户升级程序
                    showUpdateDialog();
                    break;
                case GET_UPDATEINFO_ERROR:
                    //服务器超时
                    Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                    break;
                case CANCLE_UPDATE:
                    //取消更新
                    Toast.makeText(getApplicationContext(), "取消更新", Toast.LENGTH_SHORT).show();
                    break;
                case NEED_NOT_UPDATE:
                    //已经是最新版本了
                    Toast.makeText(getApplicationContext(), "已经是最新版本", Toast.LENGTH_SHORT).show();
                    break;
                case IS_EXIT:
                    isExit = false;
                    break;
                case AUTO_CHECK_UPDATE:
                    //更新原点
                    // TODO: 2016/5/23
                    btn2.setImageResource(R.drawable.update);
                    break;
            }
        }


    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        autoCheckUpdate(AUTO_CHECK_UPDATE);

        InitGPS();

        ///////////////////////////////跳转////////////////////////////////////////////////////


        btn1 = (ImageButton) findViewById(R.id.xuanze);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent xuanze_intent = new Intent(MainActivity.this, Nongzuowu.class);
                MainActivity.this.startActivity(xuanze_intent);
            }
        });


        ///////////////////////////////版本更新////////////////////////////////////////////////////
        btn2 = (ImageButton) findViewById(R.id.banben);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCheckUpdate(UPDATE_CLIENT);
            }
        });

    }


    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                String timeString = location.getTime();   //时间

                String latString = String.valueOf(location.getLatitude());   //纬度
                // 					System.out.println("纬度" + latString);
                String lngString = String.valueOf(location.getLongitude());  //经度
                //					System.out.println("经度" + lngString);
                String locationString = location.getAddrStr();

                locationTextView.setText(locationString);

                //存储到Application中
                ((LocationApplication) getApplication()).time = timeString;
                ((LocationApplication) getApplication()).locationString = locationString;
                ((LocationApplication) getApplication()).Longtitude = lngString;
                ((LocationApplication) getApplication()).Latitude = latString;

            }
        }

    };

    public void InitGPS() {
        locationTextView = (TextView) findViewById(R.id.LocationText);
        // -----------location config ------------
        locationService = ((LocationApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听

        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

        locationService.start();// 定位SDK
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }


    /*
     *程序启动时检查更新
     */
    private void autoCheckUpdate(final int updateType){
        //获取当前应用版本号
        try {
            versionCode = getVersionCode();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Thread t = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                try {
                    //从资源文件获取服务器 地址
                    String path = "http://115.28.180.110/apk/version.xml";
                    //包装成url的对象
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    InputStream is = conn.getInputStream();
                    info = getUpdateInfo(is);
                    romoteVersionCode = parseInt(info.getVersion());
                    if (romoteVersionCode > versionCode) {
                        Log.i(TAG, "版本号不同 ,提示用户升级 ");
                        Message msg = handler.obtainMessage();
                        msg.what = updateType;
                        handler.sendMessage(msg);
                    } else {
                        Log.i(TAG, "版本号相同无需升级");
                        Message msg = handler.obtainMessage();
                        msg.what = NEED_NOT_UPDATE;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    // 待处理
                    Message msg = handler.obtainMessage();
                    msg.what = GET_UPDATEINFO_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /*
     * 获取当前程序的版本号
     */
    private int getVersionCode() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionCode;
    }

    /*
     * 用pull解析器解析服务器返回的xml文件 (xml封装了版本号)
     */
    public static UpdateInfo getUpdateInfo(InputStream is) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");//设置解析的数据源
        int type = parser.getEventType();
        UpdateInfo info = new UpdateInfo();//实体
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                    if ("version".equals(parser.getName())) {
                        info.setVersion(parser.nextText()); //获取版本号
                    } else if ("url".equals(parser.getName())) {
                        info.setUrl(parser.nextText()); //获取要升级的APK文件
                    } else if ("description".equals(parser.getName())) {
                        info.setDescription(parser.nextText()); //获取该文件的信息
                    }
                    break;
            }
            type = parser.next();
        }
        return info;
    }

    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "CleverFarmer_0." + romoteVersionCode + ".apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /*
     *
     * 弹出对话框通知用户更新程序
     *
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    protected void showUpdateDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        layout = layoutInflater.inflate(R.layout.update_dialog, null);
        Button okUpdate = (Button) layout.findViewById(R.id.update_ok);
        Button cancelUpdate = (Button) layout.findViewById(R.id.update_cancel);
        customDialog = new CustomDialog(MainActivity.this);
        customDialog.setTitle("版本升级");
        customDialog.setMessage(info.getDescription());
        customDialog.setView(layout);
        okUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "下载apk,更新");
                customDialog.dismiss();
                downLoadApk();
            }
        });
        cancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                Message msg = handler.obtainMessage();
                msg.what = CANCLE_UPDATE;
                handler.sendMessage(msg);
            }
        });
        customDialog.show();
    }

    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        final Thread downloadThread;
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();

        downloadThread = new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(info.getUrl(), pd);
                    //	                sleep(500);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = handler.obtainMessage();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        };
        downloadThread.start();
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.e(TAG, "onCancel: 取消下载");
                downloadThread.interrupt();
            }
        });
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (!isExit) {
            Message msg = handler.obtainMessage();
            msg.what = IS_EXIT;
            handler.sendMessageDelayed(msg, 2000);        //延迟两秒发送消息
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
