package x.etc.com.xixili;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.etc.entity.Hotel;
import com.etc.entity.Room;
import com.etc.task.AsynBitmapTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Select_roomActivity extends AppCompatActivity {

    //创建组件
    private GridView gdvRoomNum;
    private ListView lstdistance;
    private ListView lsttype;

    //储存该房型和酒店下的房间列表
    private List<Room> roomList = new ArrayList<Room>();

    //gridView的适配器
    private MyAdapter adapter;

    //存储房间号,房型和酒店id
    private String room_number = "";
    String room_type;
    int hotelID;

    //创建网络连接相关内容
    private String url = "http://10.0.2.2:8080/西西里web/RoomListServlet";
    private Handler handler;
    private String responseText;

    //酒店图标被点击
    private boolean isDistance = true;
    private boolean isType = true;

    //该页面
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);

        //获取组件
        gdvRoomNum = (GridView) findViewById(R.id.gdvRoomNum);
        lstdistance = (ListView) findViewById(R.id.spnOrder);
        lsttype = (ListView) findViewById(R.id.spnType);

        lstdistance.setVisibility(View.GONE);
        lsttype.setVisibility(View.GONE);

        lstdistance.setOnItemClickListener(new ss());
        lsttype.setOnItemClickListener(new ss());

        //获取参数
        intent = this.getIntent();
        room_type = intent.getStringExtra("room_type");
        hotelID = intent.getIntExtra("hotelID",0);

        //开启线程，请求服务器连接
        new Thread(new PostRunner()).start();

        //绑定适配器
        adapter = new MyAdapter();
        gdvRoomNum.setAdapter(adapter);

        //绑定监听器
        gdvRoomNum.setOnItemClickListener(new OnItemClickListenerImpl());

        //实例化handler
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    //解析gson字符串
                    parseGsonString(responseText);
                }
                else{
                    Toast.makeText(getApplicationContext(),"数据请求失败",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void type(View v){

        if(isType){
            lsttype.setVisibility(View.VISIBLE);
            AnimationSet animationSet = new AnimationSet(true);
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT,0f,
                    Animation.RELATIVE_TO_PARENT,0f,
                    Animation.RELATIVE_TO_SELF,-1f,
                    Animation.RELATIVE_TO_SELF,0f
            );
            translateAnimation.setDuration(500);
            animationSet.addAnimation(translateAnimation);
            lsttype.startAnimation(animationSet);
            isType = !isType;
        }
        else
        {
            lsttype.setVisibility(View.GONE);
            isType = true;
        }
        lstdistance.setVisibility(View.GONE);
        isDistance = true;
    }

    public void distance(View v){

        if(isDistance)
        {
            lstdistance.setVisibility(View.VISIBLE);
            AnimationSet animationSet = new AnimationSet(true);
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT,0f,
                    Animation.RELATIVE_TO_PARENT,0f,
                    Animation.RELATIVE_TO_SELF,-1f,
                    Animation.RELATIVE_TO_SELF,0f
            );
            translateAnimation.setDuration(500);
            animationSet.addAnimation(translateAnimation);
            lstdistance.startAnimation(animationSet);
            isDistance = !isDistance;
        }
        else
        {
            lstdistance.setVisibility(View.GONE);
            isDistance = true;
        }
        lsttype.setVisibility(View.GONE);
        isType = true;
    }

    private class ss implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (adapterView.getId()){
                case R.id.spnType:
                    Toast.makeText(getApplicationContext(),"type"+i,Toast.LENGTH_SHORT).show();
                    lsttype.setVisibility(View.GONE);
                    isType = true;
                    break;
                case R.id.spnOrder:
                    Toast.makeText(getApplicationContext(),"order"+i,Toast.LENGTH_SHORT).show();
                    lstdistance.setVisibility(View.GONE);
                    isDistance = true;
                    break;
                default:
                    break;
            }
        }
    }

    //点击确定按钮返回
    public void confirmReturn(View v){
        intent.putExtra("room_number",room_number);
        setResult(RESULT_OK,intent);
        finish();
    }

    //点击取消按钮返回
    public void cancelReturn(View v){
        finish();
    }

    //解析gson字符串
    private void parseGsonString(String gstr){

        Gson gson = new Gson();
        Type type = new TypeToken<List<Room>>(){}.getType();
        roomList = gson.fromJson(gstr,type);
        adapter.notifyDataSetChanged();
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
            //得到订单相关的所有信息
            params.add(new BasicNameValuePair("room_type", room_type));
            params.add(new BasicNameValuePair("hotelID", hotelID+""));


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

    //列表框对应的监听器，点击就进入
    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Room room = roomList.get(i);
            if(!room.isChecked())
                 room_number = room.getRoom_number();
            //改变对应item的背景色
            adapter.selectedIndex = i;
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),room.getRoom_number(),Toast.LENGTH_SHORT).show();
        }
    }

    //自定义适配器
    private class MyAdapter extends BaseAdapter {
        //鼠标点中的item位置
        int selectedIndex;

        @Override
        public int getCount() {
            return roomList.size();
        }

        @Override
        public Object getItem(int i) {
            return roomList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //填充view对象
            view = View.inflate(getApplicationContext(),R.layout.item_grid,null);

            //获取数据项
            Room room = roomList.get(i);

            //根据数据填充view对象
            TextView txtRoom_num = (TextView) view.findViewById(R.id.txtRoom_num);
            txtRoom_num.setText(room.getRoom_number());

            ImageView imgBtnRoom = (ImageView) view.findViewById(R.id.imgRooms);

            imgBtnRoom.setImageResource(R.drawable.room_choose);
            if(room.isChecked()) {
                imgBtnRoom.setImageResource(R.drawable.room_no);
            }
            else if(selectedIndex == i){
                view.setBackgroundColor(Color.parseColor("#2596d0"));
                imgBtnRoom.setImageResource(R.drawable.room_selected);
            }
            return view;
        }
    }
}
