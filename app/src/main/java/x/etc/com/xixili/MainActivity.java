package x.etc.com.xixili;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.etc.entity.Hotel;
import com.etc.task.AsynBitmapTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //酒店类型和排序方式的数组
    private String hotelTypes = "全部";
    private String hotelOrder = "综合排序";

    //创建组件
    private ListView lstvHotel;
    private ProgressBar pgrList;
    private Spinner spnType,spnOrder;
    private EditText edtKeyword;

    //创建数据列表
    private List<Hotel> dataList = new ArrayList<Hotel>();//存储用户进行选择后酒店的列表，即显示在屏幕上的列表

    private List<Hotel> allHotelList = new ArrayList<Hotel>();//存储该地区所有酒店的列表

    //创建网络连接相关内容
    private String url = "http://10.0.2.2:8080/西西里web/HotelListServlet";
    private Handler handler;
    private String responseText;

    //进入该界面的省份和城市
    private String province,city,keyword;

    //初始化远程加载图片
    private AsynBitmapTask asyncBitmap;

    //初始化适配器
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取界面传递参数
        province = "内蒙古";
        city = "呼和浩特";
        keyword = "";

        //获取组件
        lstvHotel = (ListView) findViewById(R.id.lstvHotel);
        pgrList = (ProgressBar) findViewById(R.id.pgrList);
        spnType = (Spinner) findViewById(R.id.spnType);
        spnOrder = (Spinner) findViewById(R.id.spnOrder);
        edtKeyword = (EditText) findViewById(R.id.edtKeyword);

        //创建图片加载类
        asyncBitmap = new AsynBitmapTask();

        //绑定适配器
        adapter = new MyAdapter();
        lstvHotel.setAdapter(adapter);

        //绑定监听器
        spnType.setOnItemSelectedListener(new OnItemSelectedListenerImpl());
        spnOrder.setOnItemSelectedListener(new OnItemSelectedListenerImpl());
        lstvHotel.setOnItemClickListener(new OnItemClickListenerImpl());

        //开启线程
        new Thread(new PostRunner()).start();

        //实例化handler
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {

                pgrList.setVisibility(View.GONE);
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

    //根据用户选择的关键字，进行筛选
    private void selectHotelType(String type){
        dataList.clear();
        for(int i = 0; i < allHotelList.size();i++){
            Hotel hotel = allHotelList.get(i);
            if("全部".equals(type) || type.equals(hotel.getType())){
                dataList.add(hotel);
            }
        }
        adapter.notifyDataSetChanged();
    }

    //根据用户选择的排序类型，进行排序
    private void sortHotelList(String orderType){
        switch (orderType){
            case "综合排序":
                Comparator<Hotel> compareAll = new Comparator<Hotel>() {
                    @Override
                    public int compare(Hotel hotel1, Hotel hotel2) {
                        if(hotel1.getCom_level()-hotel2.getCom_level() < 0)
                            return 1;
                        else
                            return -1;
                    }
                };
                Collections.sort(dataList,compareAll);
                break;
            case "好评优先":
                Comparator<Hotel> compareComment = new Comparator<Hotel>() {
                    @Override
                    public int compare(Hotel hotel1, Hotel hotel2) {
                        if(hotel1.getComment_level()-hotel2.getComment_level() < 0)
                            return 1;
                        else
                        return -1;
                    }
                };
                Collections.sort(dataList,compareComment);
                break;
            case "安全优先":
                Comparator<Hotel> compareSafe = new Comparator<Hotel>() {
                    @Override
                    public int compare(Hotel hotel1, Hotel hotel2) {
                        if(hotel1.getSafe_level()-hotel2.getSafe_level() < 0)
                            return 1;
                        else
                            return -1;
                    }
                };
                Collections.sort(dataList,compareSafe);
                break;
            case "热门优先":
                Comparator<Hotel> comparePersonNum = new Comparator<Hotel>() {
                    @Override
                    public int compare(Hotel hotel1, Hotel hotel2) {
                        if(hotel1.getVisitor_number()-hotel2.getVisitor_number() < 0)
                            return 1;
                        else
                            return -1;
                    }
                };
                Collections.sort(dataList,comparePersonNum);
                break;
            case "距离优先":
                break;
            case "低价优先":
                Comparator<Hotel> comparePrice = new Comparator<Hotel>() {
                    @Override
                    public int compare(Hotel hotel1, Hotel hotel2) {
                        if(hotel1.getLowestPrice()-hotel2.getLowestPrice() > 0)
                            return 1;
                        else
                            return -1;
                    }
                };
                Collections.sort(dataList,comparePrice);
                break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();
    }

    //解析gson字符串
    private void parseGsonString(String gstr){

        Gson gson = new Gson();
        Type type = new TypeToken<List<Hotel>>(){}.getType();
        allHotelList = gson.fromJson(gstr,type);
        selectHotelType("全部");
    }

    //点击搜索
    public void searchHotel(View view){
        keyword = edtKeyword.getText().toString();
        new Thread(new PostRunner()).start();
        Toast.makeText(getApplicationContext(),keyword,Toast.LENGTH_SHORT).show();
    }

    //列表框对应的监听器，点击就进入
    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Hotel hotel = dataList.get(i);
            Intent intent = new Intent(MainActivity.this,WriteOrderActivity.class);
            //intent.putExtra("hotel",hotel);
            startActivity(intent);
            finish();
        }
    }

    //下拉框对应的监听器
    private class OnItemSelectedListenerImpl implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(adapterView.getId() == R.id.spnType) {
                hotelTypes = spnType.getItemAtPosition(i).toString();
                selectHotelType(hotelTypes);
                Toast.makeText(getApplicationContext(), hotelTypes, Toast.LENGTH_SHORT).show();
            }
            else if(adapterView.getId() == R.id.spnOrder){
                hotelOrder = spnOrder.getItemAtPosition(i).toString();
                sortHotelList(hotelOrder);
                Toast.makeText(getApplicationContext(), hotelOrder, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    //自定义适配器
    private class MyAdapter extends BaseAdapter{
        private String imageBaseURL = "http://10.0.2.2:8080/西西里web/photos/hotel";

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //填充view对象
            view = View.inflate(getApplicationContext(),R.layout.hotel_layout,null);

            //获取数据项
            Hotel hotel = dataList.get(i);

            //根据数据填充view对象
            final ImageView imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);

            //加载远程图片
            String imageUrl = imageBaseURL + "/" + hotel.getPhoto();
            Bitmap bitmap = asyncBitmap.loadBitmap(imgPhoto,imageUrl, new AsynBitmapTask.ImageCallback() {
                @Override
                public void imageLoad(ImageView imageView, Bitmap bitmap) {
                    imgPhoto.setImageBitmap(bitmap);
                }
            });
            //不为空，则一定是从缓存中得到的图片
            if(bitmap != null){
                imgPhoto.setImageBitmap(bitmap);
            }
            //imgPhoto.setImageResource(R.drawable.p);

            RatingBar ratBarSafe = (RatingBar) view.findViewById(R.id.ratBarSafe);
            ratBarSafe.setRating(hotel.getSafe_level());

            TextView txtHname = (TextView) view.findViewById(R.id.txtHname);
            txtHname.setText(hotel.getRealname());

            TextView txtcomment = (TextView) view.findViewById(R.id.txtcomment);
            txtcomment.setText("用户评分：" + hotel.getComment_level()+"分");

            TextView txtType = (TextView) view.findViewById(R.id.txttype);
            txtType.setText(hotel.getType());

            TextView txtprice = (TextView) view.findViewById(R.id.txtprice);
            txtprice.setText(hotel.getLowestPrice()+"起");

            return view;
        }
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
                params.add(new BasicNameValuePair("province", province));
                params.add(new BasicNameValuePair("city", city));
                params.add(new BasicNameValuePair("keyword", keyword));
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
