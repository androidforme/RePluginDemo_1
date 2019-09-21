package test.pvj.com.replugin_host;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button externalPlugins;

    TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<PluginInfo> arr = RePlugin.getPluginInfoList();

        infoText = findViewById(R.id.externalPluginsInfo);


        findViewById(R.id.builtInPlugins).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一个参数是插件的包名，第二个参数是插件的Activity。
                startActivity("plugin1", "test.pvj.com.testrreplugin1.MainActivity");
            }
        });

        externalPlugins = findViewById(R.id.externalPlugins);

        externalPlugins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openExternalPlugin("plugin2", "test.pvj.com.plugin2.MainActivity");

            }
        });

        if (RePlugin.isPluginInstalled("plugin2")) {
            infoText.setText("外置插件已安装");
        } else {
            infoText.setText("外置插件未安装");
        }
    }

    int pro = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pro++;
            if (pro <= 10) {
                infoText.setText("外置插件下载进度 " + pro * 10 + "%");
                handler.sendEmptyMessageDelayed(0, 1000);

            }
            if (pro == 10) {
                install("plugin2", "test.pvj.com.plugin2.MainActivity");
            }
        }
    };

    private void openExternalPlugin(String pluginName, String className) {
        externalPlugins.setEnabled(false);
        if (RePlugin.isPluginInstalled(pluginName)) {//判断是否已经安装，安装了的话，就打开Activity，并且检查插件版本，需要更新的话就下载插件
            PluginInfo info = RePlugin.getPluginInfo(pluginName);
            Log.d(TAG, "PluginInfo :  " + info.toString());
            startActivity(pluginName, className);
            externalPlugins.setEnabled(true);
        } else {
            Log.d(TAG, "未安装 插件 " + pluginName);
            handler.sendEmptyMessageDelayed(0, 1000);

        }
    }

    private void install(String pluginName, String className) {
        Log.d(TAG, "Environment.getExternalStorageDirectory() " + Environment.getExternalStorageDirectory());
        final PluginInfo info = RePlugin.install(Environment.getExternalStorageDirectory() + "/" + pluginName + ".apk");
        if (info != null) {
            Log.d(TAG, "PluginInfo : " + info.toString());
            infoText.setText("外置插件  PluginInfo : " + info.toString());

            startActivity(pluginName, className);
            externalPlugins.setEnabled(true);
        } else {
            infoText.setText("外置插件安装失败 ");
            Log.d(TAG, "安装失败 " + pluginName);
        }
    }


    private void startActivity(String pluginName, String className) {
        //第一个参数是插件的包名，第二个参数是插件的Activity。
        Intent intent = RePlugin.createIntent(pluginName, className);
        if (!RePlugin.startActivity(MainActivity.this, intent)) {
            Toast.makeText(getBaseContext(), "启动失败", Toast.LENGTH_LONG).show();
        }
    }
}
