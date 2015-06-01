package com.example.nitinvarun.dailyselfie;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by NitinVarun on 5/22/2015.
 */
public class ImageDisplayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rooView = inflater.inflate(R.layout.image_diplay,container,false);
        ImageView mImageView = (ImageView) rooView.findViewById(R.id.imageView);
        Bundle args = getArguments();
        int pos = args.getInt("position");
        mImageView.setImageBitmap(BitmapFactory.decodeFile(MainActivity.filenamelist.get(pos)));
        return rooView;
    }
}
