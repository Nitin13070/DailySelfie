package com.example.nitinvarun.dailyselfie;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by NitinVarun on 5/20/2015.
 */
public class ListFragment extends Fragment {
    Context mContext;
    ListView imageListView;
    public ListFragment() {

    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.mContext = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        imageListView = (ListView) rootView.findViewById(R.id.imageslist);


        new LoadImagesTask().execute(new File(Environment.getExternalStorageDirectory(), "SelfieThumbnail")); // Loads thumbnail and put it in ListView

        Log.d("DEBUGER", "PendingIntent inisiated");
        MainActivity.mImageListAdapter = new ImageListAdapter(mContext,R.id.container,new ArrayList<ImageData>());
        imageListView.setAdapter(MainActivity.mImageListAdapter);
        registerForContextMenu(imageListView);

        Log.d("DEBUGER", "before onclick listener");
        imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putInt("position",position);
                ImageDisplayFragment mImageDisplayFragment = new ImageDisplayFragment();
                mImageDisplayFragment.setArguments(args);
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, mImageDisplayFragment, "ImageViewFragmentTag") // For ImageView
                        .addToBackStack(null)
                        .commit();
            }
        });

        Log.d("DEBUGER", "After onclick listener");
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){ // For Context Menu :  Delete and Share
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        menu.setHeaderTitle("Context Menu");
        inflater.inflate(R.menu.menu_context,menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String thumbnailFilePath = MainActivity.mImageListAdapter.getObjectListItem(info.position).getName();
        String imageFilePath = MainActivity.filenamelist.get(info.position);

        switch (item.getItemId()){
            case R.id.delete: // Delete image and its thumbnail
                File thumFile = new File(thumbnailFilePath);
                File imageFile = new File(imageFilePath);
                MainActivity.filenamelist.remove(info.position);
                thumFile.delete();
                imageFile.delete();
                MainActivity.mImageListAdapter.removeItem(info.position);
                Toast.makeText(mContext,"DELETE",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share: // Share image to facebook
                Toast.makeText(mContext,"SHARE",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    public class LoadImagesTask extends AsyncTask<File,Integer,ArrayList<ImageData>>{

        @Override
        protected ArrayList<ImageData> doInBackground(File... params) {
            ArrayList<ImageData> imageDataList = new ArrayList<ImageData>();
            File[] fileList = params[0].listFiles();
            File mFile1 = new File(Environment.getExternalStorageDirectory(),"SelfieTaken");
            for(File file : fileList){ //
//                Bitmap mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                String name = file.getAbsolutePath();
                imageDataList.add(new ImageData(name ));

                MainActivity.filenamelist.add(mFile1.getPath()+File.separator+ file.getName()); // adding full file path of image
            }

            return imageDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<ImageData> imageDataList){

            for(int i=0;i<imageDataList.size();i++){
                MainActivity.mImageListAdapter.add(imageDataList.get(i)); // add list to adapter

            }

        }
    }
}

