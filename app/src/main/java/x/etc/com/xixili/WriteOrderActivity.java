package x.etc.com.xixili;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.etc.entity.Hotel;
import com.etc.entity.MyApp;
import com.etc.entity.Orders;
import com.etc.entity.Room;
import com.etc.entity.Types;
import com.etc.entity.Users;

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
import java.util.Calendar;
import java.util.List;

//如何设置日历，使其当前日期之前的都不可选！！！！！！！！！！！！！！！！

public class WriteOrderActivity extends AppCompatActivity {

    //创建组件
    private TextView txtcheckin = null;
    private TextView txtcheckout = null;
    private TextView txtPrice = null;
    private Button btncheckin = null;
    private Button btncheckout = null;
    private Button btncsRoom;
    private EditText edtUsername;
    private EditText edtPhone;

    //得到roomTypes类，和user类,以及用户所选房间号
    private Users users;
    private Types roomtype;
    private String room_number = "";

    //创建对话框
    private Dialog dlgComfirm,dlgWarn;

    //创建网络连接相关内容
    private String url = "http://10.0.2.2:8080/西西里web/AddOrderServlet";
    private Handler handler;
    private String responseText;

    //用来保存年月日：
    private int inmYear,outmYear;
    private int inmMonth,outmMonth;
    private int inmDay,outmDay;

    //声明一个独一无二的标识，来作为要显示DatePicker的Dialog的ID：
    static final int DATE_DIALOG_ID = 0;
    protected static final int DATE_DIALOG_ID_OUT = 1;


    //-----------------------------------------------函数-------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_order);

        //获取组件
        txtcheckin = (TextView) findViewById(R.id.txtcheckin);
        txtcheckout = (TextView) findViewById(R.id.txtcheckout);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        btncheckin = (Button) findViewById(R.id.btncheckin);
        btncheckout = (Button) findViewById(R.id.btncheckout);
        edtUsername = (EditText) findViewById(R.id.edtusername);
        edtPhone = (EditText) findViewById(R.id.edtphone);
        btncsRoom = (Button) findViewById(R.id.btncsRoom);

        //获取用户对象
        users = new Users();
        /*MyApp myApp = (MyApp) this.getApplication();
        users = myApp.getUsers();*/
        users.setUserID(1);

        //获取页面参数传递得到的roomtype对象
        roomtype = new Types();
        /*Intent intent = this.getIntent();
        roomtype = (Types) intent.getSerializableExtra("roomtype");*/
        roomtype.setHotelID(2);
        roomtype.setPrice(210);
        roomtype.setRoom_type("高级大床房");

        txtPrice.setText(roomtype.getPrice()+"");

        //给button添加事件监听器：
        btncheckin.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                //调用Activity类的方法来显示Dialog:调用这个方法会允许Activity管理该Dialog的生命周期，
                //并会调用 onCreateDialog(int)回调函数来请求一个Dialog
                showDialog(DATE_DIALOG_ID);
            }
        });

        //给button添加事件监听器：
        btncheckout.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                //调用Activity类的方法来显示Dialog:调用这个方法会允许Activity管理该Dialog的生命周期，
                //并会调用 onCreateDialog(int)回调函数来请求一个Dialog
                showDialog(DATE_DIALOG_ID_OUT);
            }
        });

        //获得当前的日期
        getCurrentDate();

        //设置对话框
        createWarnDlg();

        //实例化handler
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    //输出插入订单状态
                    Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"数据请求失败",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    //获得当前的日期：
    private void getCurrentDate(){
        final Calendar currentDate = Calendar.getInstance();
        inmYear = currentDate.get(Calendar.YEAR);
        outmYear = currentDate.get(Calendar.YEAR);
        inmMonth = currentDate.get(Calendar.MONTH);
        inmDay = currentDate.get(Calendar.DAY_OF_MONTH);
        outmMonth = currentDate.get(Calendar.MONTH);
        outmDay = currentDate.get(Calendar.DAY_OF_MONTH);
        //设置文本的内容：
        btncheckin.setText(new StringBuilder()
                .append(inmYear).append("-")
                .append(inmMonth + 1).append("-")//得到的月份+1，因为从0开始
                .append(inmDay));

        btncheckout.setText(new StringBuilder()
                .append(outmYear).append("-")
                .append(outmMonth + 1).append("-")//得到的月份+1，因为从0开始
                .append(outmDay+1));
    }

    //初始化对话框
    private void createDlg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("插入确认框");
        builder.setMessage("是否确认提交此订单？");
        builder.setIcon(R.drawable.question);

        builder.setPositiveButton("是", new ComfirmOnClickLinstenerImpl());
        builder.setNegativeButton("否", null);

        this.dlgComfirm = builder.create();
    }

    //初始化警告对话框
    private void createWarnDlg(){
        AlertDialog.Builder buildertime = new AlertDialog.Builder(this);
        buildertime.setTitle("警告框");
        buildertime.setMessage("请选择正确的抵离时间");
        buildertime.setPositiveButton("确定",null);
        dlgWarn = buildertime.create();
    }

    //按下提交按钮，弹出确认对话框
    public void submit(View view){

        //弹出确认对话框
        createDlg();
        this.dlgComfirm.show();
    }

    //选择房间按钮
    public void chooseRoom(View view){
        //跳转到选择房间界面，并回传选择的房间号
        Intent intent = new Intent(WriteOrderActivity.this,Select_roomActivity.class);

        //传递参数
        intent.putExtra("hotelID",roomtype.getHotelID());
        intent.putExtra("room_type",roomtype.getRoom_type());

        //启动Activity
        startActivityForResult(intent, 1);   //requestCode用于标识启动的是哪个Activity
    }

    //发送界面中重写onActivityResult()方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){   //操作成功

            //接收返回值
            room_number = data.getStringExtra("room_number");
            if(!"".equals(room_number))
                btncsRoom.setText("已选房间："+room_number);
            else
                btncsRoom.setText("选择房间");
            Toast.makeText(getApplicationContext(), room_number, Toast.LENGTH_SHORT).show();
        }else{
            btncsRoom.setText("选择房间");
            Toast.makeText(getApplicationContext(), room_number, Toast.LENGTH_SHORT).show();
        }
    }


    //按下确认对话框的监听器，若选择确认，则将该订单添加提交到服务器端
    private class ComfirmOnClickLinstenerImpl implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(i == Dialog.BUTTON_POSITIVE){
                if("".equals(edtUsername.getText().toString())||"".equals(edtPhone.getText().toString())||"".equals(room_number))
                    Toast.makeText(getApplicationContext(),"请输入入住人姓名和电话,并选择房间号",Toast.LENGTH_SHORT).show();
                else//开启线程
                    new Thread(new PostRunner()).start();
            }
        }
    }

    //需要定义弹出的DatePicker对话框的事件监听器：
    private DatePickerDialog.OnDateSetListener inmDateSetListener =new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                inmYear = year;
                inmMonth = monthOfYear;
                inmDay = dayOfMonth;

                //设置文本的内容：
                btncheckin.setText(new StringBuilder()
                        .append(inmYear).append("-")
                        .append(inmMonth + 1).append("-")//得到的月份+1，因为从0开始
                        .append(inmDay));
        }
    };

    private DatePickerDialog.OnDateSetListener outmDateSetListener =new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //check out时间不能早于check in的时间
            if(year > inmYear || monthOfYear > inmMonth || dayOfMonth > inmDay)
            {
                outmYear = year;
                outmMonth = monthOfYear;
                outmDay = dayOfMonth;

                //设置文本的内容：
                btncheckout.setText(new StringBuilder()
                        .append(outmYear).append("-")
                        .append(outmMonth + 1).append("-")//得到的月份+1，因为从0开始
                        .append(outmDay));
            }
            else
                dlgWarn.show();

        }
    };

     //当Activity调用showDialog函数时会触发该函数的调用：
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,inmDateSetListener,inmYear, inmMonth, inmDay);
            case DATE_DIALOG_ID_OUT:
                return new DatePickerDialog(this,outmDateSetListener,outmYear, outmMonth, outmDay);
        }
        return null;
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
            params.add(new BasicNameValuePair("resident", edtUsername.getText().toString()));
            params.add(new BasicNameValuePair("phone", edtPhone.getText().toString()));
            params.add(new BasicNameValuePair("checkin_time", btncheckin.getText().toString()));
            params.add(new BasicNameValuePair("checkout_time", btncheckout.getText().toString()));
            params.add(new BasicNameValuePair("userID",users.getUserID()+""));
            params.add(new BasicNameValuePair("hotelID",roomtype.getHotelID()+""));
            params.add(new BasicNameValuePair("room_number",room_number));
            params.add(new BasicNameValuePair("realprice",roomtype.getPrice()+""));
            params.add(new BasicNameValuePair("room_type",roomtype.getRoom_type()));

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
