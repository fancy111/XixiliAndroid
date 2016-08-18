package com.etc.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HandshakeCompletedListener;

/**
 * Created by fancy on 2016/8/10.
 */
public class AsynBitmapTask {
    //SoftReference存的只是内存的一个位置
    private HashMap<String,SoftReference<Bitmap>> imagecache;
    private String sdcardCacheDir;

    //构造函数
    public AsynBitmapTask(){
        this.imagecache = new HashMap<String,SoftReference<Bitmap>>();
        //得到内存卡的存储路径
        this.sdcardCacheDir = Environment.getExternalStorageDirectory().getPath()+"/xixili/Hotel";
    }

    //加载图片方法，第一个参数代表要更新的图片控件，第二个表示图片的位置，
    public Bitmap loadBitmap(final ImageView imageView,final String imageUrl,final ImageCallback imageCallback){

        //检测内存缓存
        if(imagecache.containsKey(imageUrl)){
            SoftReference<Bitmap> reference = imagecache.get(imageUrl);
            //从该内存位置中渠道相应的图
            Bitmap bitmap = reference.get();
            if(bitmap != null){
                System.out.println(">>>已经获取内存图片");
                return bitmap;
            }
        }

        //检测SD卡
        String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        File cacheDir = new File(sdcardCacheDir);
        if(cacheDir.exists()){
            //获取缓存中所有文件
            File[] cacheFiles = cacheDir.listFiles();
            int i = 0;
            for(;i<cacheFiles.length;i++){
                if(bitmapName.equals((cacheFiles[i].getName())))
                    break;
            }
            if(i<cacheFiles.length){
                System.out.println(">>> 已加载SD缓存图片！");
                Bitmap bitmap = BitmapFactory.decodeFile(sdcardCacheDir+"/"+bitmapName);
                //将该图片加入到缓存中去
                imagecache.put(imageUrl,new SoftReference<Bitmap>(bitmap));
                return bitmap;
            }
        }

        //定义handler
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                    imageCallback.imageLoad(imageView, (Bitmap) msg.obj);
            }
        };

        //远程图片加载,不能直接返回，因为是远程加载，所以需要一个handler来进行消息传送
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(imageUrl);
                    System.out.println(">>> 开始加载远程图片！");
                    InputStream input = url.openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    input.close();

                    //通知handler
                    Message message = handler.obtainMessage();
                    message.obj = bitmap;
                    handler.sendMessage(message);

                    //加入内存缓存
                    imagecache.put(imageUrl,new SoftReference<Bitmap>(bitmap));

                    //加入sd卡
                    File dir = new File(sdcardCacheDir);
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    File bitmapFile = new File(sdcardCacheDir+"/"+imageUrl.substring(imageUrl.lastIndexOf("/")+1));
                    FileOutputStream fos = new FileOutputStream(bitmapFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                    fos.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return null;
    }

    //定义回调接口,方法执行完之后，要通知UI
    public interface ImageCallback{
        public void imageLoad(ImageView imageView,Bitmap bitmap);
    }
}
