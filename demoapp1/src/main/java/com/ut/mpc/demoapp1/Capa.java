package com.ut.mpc.demoapp1;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by nathanielwendt on 11/3/15.
 */
public class Capa {

    private static boolean imgInit = false;
    private static Context context;

    public static void configureImg(Context ctx){
//        if(!imgInit) {
//            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx).build();
//            ImageLoader.getInstance().init(config);
//            imgInit = true;
//        }
        context = ctx;
    }

    public static void ImageMap(ImageView imageView, String imageUri){
        System.out.println("changing image");
        Picasso.with(context).load(imageUri).into(imageView);
    }

    public static void TextMap(TextView textView, String newVal){
        textView.setText(newVal);
    }
}
