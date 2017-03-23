package com.blue.modualtests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * my ImageLoader
 * Created by getbl on 2016/6/6.
 */
public class imageLoader {
    private String baseUrl;
    private Bitmap mBitmapRes;
    private int defaultImageResource = 0;
    private int errorImageResource = 0;
    private ImageView targetImageView;
    private LruCache<String, Bitmap> memoryCache;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                targetImageView.setImageBitmap(mBitmapRes);
            } else {
                if (errorImageResource != 0)
                    targetImageView.setImageResource(errorImageResource);
                //targetImageView.setImageDrawable(errorImage);
            }
        }
    };


    public imageLoader(LruCache<String,Bitmap> memoryCache) {

        this.memoryCache = memoryCache;
    }

    public imageLoader from(String url) {
        baseUrl = url;
        return this;
    }


    /**
     * imageLoader loader = new loader();
     * loder.from(textView).into(imageView);
     */

    public imageLoader into(ImageView imageView) {
        if (imageView != null) targetImageView = imageView;
        targetImageView.setImageResource(defaultImageResource);
        return this;
    }

    public imageLoader execute() {

        Bitmap cachedBitmap = memoryCache.get(baseUrl);
        if (cachedBitmap == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    netStuff();
                }
            }).start();
        } else targetImageView.setImageBitmap(cachedBitmap);
        return this;
    }

    public imageLoader setDefaultImage(int defaultImageResource) {
        this.defaultImageResource = defaultImageResource;
        return this;
    }

    public void backToDefault() {
        targetImageView.setImageResource(defaultImageResource);
    }

    public imageLoader setErrorImage(int errorImageResource) {
        this.errorImageResource = errorImageResource;
        return this;
    }

    public imageLoader changeImage(String url) {
        baseUrl = url;
        execute();
        return this;
    }

    private void netStuff() {
        Message message = new Message();
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            //OutputStream stream  = conn.getOutputStream();
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            mBitmapRes = BitmapFactory.decodeStream(inputStream);
            //throw new IOException("@@@");
            message.what = 1;
             memoryCache.put(baseUrl, mBitmapRes);

        } catch (IOException e) {
            message.what = 2;
            e.printStackTrace();
        } finally {
            mHandler.sendMessage(message);
        }

    }
}
