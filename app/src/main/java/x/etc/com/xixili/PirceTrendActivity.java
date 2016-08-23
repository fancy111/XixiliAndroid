package x.etc.com.xixili;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.etc.task.DrawChartView;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PirceTrendActivity extends AppCompatActivity {

    private String city;

    //创建网络连接相关内容
    private String url = "http://10.0.2.2:8080/西西里web/GetPriceTrendServlet";
    private Handler handler;
    private String responseText;

    LinearLayout layout;

    //请求服务器的值
    float[] cityPrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pirce_trend);

        //接受界面传值
        Intent intent = this.getIntent();
        city = intent.getStringExtra("city");

        cityPrices = new float[4];

        //启动线程
        new Thread(new PostRunner()).start();

        //获取组件
        layout = (LinearLayout) findViewById(R.id.myLayout);


        //实例化handler
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    //解析gson字符串
                    Gson gson = new Gson();
                    cityPrices = gson.fromJson(responseText,float[].class);
                    layout.addView(new DrawChartView(getApplicationContext(),cityPrices));
                }
                else{
                    Toast.makeText(getApplicationContext(),"数据请求失败",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    //子线程,请求连接数据库
    private class PostRunner implements Runnable{
        @Override
        public void run() {
            //设置超时
            HttpParams httppramas = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httppramas,3000);
            HttpConnectionParams.setSoTimeout(httppramas,5000);
            //实例化HttpClient对象
            HttpClient client = new DefaultHttpClient(httppramas);
            //实例化HttpPost请求对象，传入访问的url
            HttpPost request = new HttpPost(url);
            //使用NameValuePair对象封装Post请求参数的名-值对，并设置请求参数的编码格式为UTF_8
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("city", city));
            //将用户名和密码传给url
            try {
                request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //发送请求，并返回HttpResponse对象
                HttpResponse response = client.execute(request);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    responseText = EntityUtils.toString(response.getEntity());
                    handler.sendEmptyMessage(1);}

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(-1);
            }catch (IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(-1);
            }
        }
    }


}
