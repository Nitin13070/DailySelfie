package com.example.nitinvarun.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by NitinVarun on 5/19/2015.
 */
public class ImageListAdapter extends ArrayAdapter<ImageData>{

    Context context;
    ArrayList<ImageData> objects;
    ThumbnailLoaderTask mThumbnailLoaderTask;
    ViewHolder holder;
    public ImageListAdapter(Context context, int resource, ArrayList<ImageData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public ImageData getObjectListItem(int pos){
        return objects.get(pos);
    }
    public void removeItem(int pos){
        super.remove(objects.get(pos));
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent){


         View view = convertView;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.image_list_item, parent, false);

        }
            holder = (ViewHolder) view.getTag();

            if (holder == null) {
                holder = new ViewHolder();
                holder.image = (ImageView) view.findViewById(R.id.list_image);
                holder.name = (TextView) view.findViewById(R.id.title);
                holder.date = (TextView) view.findViewById(R.id.date);
                holder.time = (TextView) view.findViewById(R.id.time);
                view.setTag(holder);
            }


//            mThumbnailLoaderTask = new ThumbnailLoaderTask();
//            String path = objects.get(position).getName();
//            mThumbnailLoaderTask.execute(path);

            holder.image.setImageBitmap(BitmapFactory.decodeFile(objects.get(position).getName()));

            String getName = new File(objects.get(position).getName()).getName();

            String getDate = getName.substring(7,15);
            getDate = getDate.substring(6,8) + '/' + getDate.substring(4,6) + '/' + getDate.substring(0,4);
            holder.date.setText(getDate);

            String getTime = getName.substring(16,22);
            getTime = getTime.substring(0,2) + ':'+ getTime.substring(2,4) + ":" + getTime.substring(4,6);
            holder.time.setText(getTime);


            holder.name.setText(getName);
            return view;


    }
    public static class ViewHolder{
        ImageView image;
        TextView name;
        TextView date;
        TextView time;

    }
    public class ThumbnailLoaderTask extends AsyncTask<String,Integer,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap mBitmap = BitmapFactory.decodeFile(params[0]);
            return mBitmap;
        }
        @Override
        protected void onPostExecute(Bitmap mBitmap){
            holder.image.setImageBitmap(mBitmap);
        }


    }


}
