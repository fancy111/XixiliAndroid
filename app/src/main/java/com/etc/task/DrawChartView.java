package com.etc.task;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by fancy on 2016/8/23.
 */
//绘制图表的函数
public class DrawChartView extends View {

    private float[] prices;

    //横纵坐标的刻度显示
    private String[] XLabel;
    private String[] YLabel;

    //图中点的坐标
    private ArrayList<Point> pointList;
    //显示的标题
    private String title;

    //画图的画笔
    private Paint paint;
    private Paint paintText;

    //设置坐标系的原点
    private int XPoint = 120;
    private int YPoint = 600;

    //构造函数，得到价格的走势
    public DrawChartView(Context context,float[] prices) {
        super(context);
        this.prices = prices;
        init();
    }

    //初始化图像
    private void init(){
        //初始化画笔,是关于线条的画笔
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLUE);
        //初始化画笔的线条
        paintText = new Paint();
        paintText.setTextSize(30);
        paintText.setColor(Color.BLUE);
        paintText.setStrokeWidth(2);

        //得到点的坐标集合
        pointList = new ArrayList<Point>();
        //将相应点坐标加入到list中去
        for(int i = 0; i < prices.length; i++){
            //得到相应点对应的坐标
            Point point = new Point(XPoint+100+(i)*200, (int)(YPoint-prices[i]/2));
            pointList.add(point);
        }
        //设置横纵坐标的内容
        XLabel = new String[]{"1~3月","4~6月","7~9月","10~12月"};
        YLabel = new String[]{"0","100","200","300","400","500","600","700","800","900","1000"};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置背景颜色
        canvas.drawColor(Color.WHITE);

        //画横竖坐标轴
        canvas.drawLine(XPoint,YPoint,XPoint+900,YPoint,paint);
        canvas.drawLine(XPoint,YPoint,XPoint,YPoint-550,paint);
        canvas.drawLine(XPoint+900,YPoint,XPoint+880,YPoint-20,paint);
        canvas.drawLine(XPoint+900,YPoint,XPoint+880,YPoint+20,paint);
        canvas.drawLine(XPoint,YPoint-550,XPoint-20,YPoint-530,paint);
        canvas.drawLine(XPoint,YPoint-550,XPoint+20,YPoint-530,paint);

        //显示横坐标的内容，包括坐标和刻线
        for(int i = 0; i < prices.length;i++){
            canvas.drawLine(XPoint+200*i+200,YPoint,XPoint+200*i+200,YPoint-10,paint);
            canvas.drawText(XLabel[i],XPoint+50+i*200,YPoint+50,paintText);
            //显示各个时间段的价格
            Paint paintprice = new Paint();
            paintprice.setStrokeWidth(5);
            paintprice.setColor(Color.BLACK);
            paintprice.setTextSize(30);
            canvas.drawText(prices[i]+"",pointList.get(i).x-20,pointList.get(i).y-30,paintprice);
        }
        //显示纵坐标的内容
        for(int i = 0; i <= 10; i ++){
            canvas.drawLine(XPoint,YPoint-i*50,XPoint+10,YPoint-i*50,paint);
            canvas.drawText(YLabel[i],XPoint-80,YPoint-i*50,paintText);
        }

        //画曲线
        for(int i = 0; i < 3;i++){
            canvas.drawLine(pointList.get(i).x,pointList.get(i).y,pointList.get(i+1).x,pointList.get(i+1).y,paint);
        }
        //更新界面
        invalidate();
    }


}
