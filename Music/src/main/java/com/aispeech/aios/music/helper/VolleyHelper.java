package com.aispeech.aios.music.helper;

import android.widget.ImageView;

import com.aispeech.aios.music.AIMusicApp;
import com.aispeech.aios.music.pojo.BitmapCache;
import com.aispeech.aios.music2.R;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-30
 * @copyright aispeech.com
 */
public class VolleyHelper {

    private static VolleyHelper mInstance;

    private static RequestQueue mQueue;

    private VolleyHelper(){
        mQueue = Volley.newRequestQueue(AIMusicApp.getContext());
    }

    public static synchronized VolleyHelper getInstance(){
        if(mInstance==null){
            mInstance = new VolleyHelper();
        }
        return mInstance;
    }

    public void getImage(ImageView imageView,String url){
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        imageLoader.get(url, listener);
    }

}
