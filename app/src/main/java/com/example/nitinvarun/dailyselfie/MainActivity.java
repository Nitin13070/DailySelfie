package com.example.nitinvarun.dailyselfie;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends ActionBarActivity {
    static String fullFilename;
    String thumbnailname;
    static String shortFilename;
    static int CAMERA_REQUEST_CODE = 100;
    static final int TIME_DIALOG_ID = 200;
    static ArrayList<String> filenamelist;
    static ImageListAdapter mImageListAdapter;
    AlarmManager alarmManager;
    Intent mNotificationReceiverIntent;
    PendingIntent mNotificationReceiverPendingIntent;
    static final long INITIAL_ALARM_DELAY = 1 * 60 * 1000L; // = 1 minute

    int mHour;
    int mMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        mNotificationReceiverIntent = new Intent(this,AlarmNotificationReciever.class);
//        if(PendingIntent.getBroadcast(this,0,mNotificationReceiverIntent,PendingIntent.FLAG_NO_CREATE) == null){

//            mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(this,0,mNotificationReceiverIntent,0);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ INITIAL_ALARM_DELAY,AlarmManager.INTERVAL_DAY,mNotificationReceiverPendingIntent);

//        }
//        else if(PendingIntent.getBroadcast(this,0,mNotificationReceiverIntent,PendingIntent.FLAG_NO_CREATE) != null){
//            Toast.makeText(this,"Alarm already exists",Toast.LENGTH_SHORT).show();
//        }


        filenamelist = new ArrayList<String>();// stores the full file path of image
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"SelfieTaken"); // image file directory
        File mediaThumbnail = new File(Environment.getExternalStorageDirectory(),"SelfieThumbnail"); // thumbnail directory
        if(!mediaThumbnail.exists()){
            mediaThumbnail.mkdir();
        }
        if(!mediaStorageDir.exists()){
            mediaStorageDir.mkdir();
        }
        Log.d("DEBUGER","arraylist inisiated");
            //function to read bitmap from location
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ListFragment())
                .addToBackStack(null)
                .commit(); // start the List Fragement

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.CancelAlarm) { // cancel reminder

            alarmManager.cancel(mNotificationReceiverPendingIntent);
//            mNotificationReceiverPendingIntent.cancel();
            Toast.makeText(this,"Alarm Canceled",Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.SetAlarm){ // set reminder
            showDialog(TIME_DIALOG_ID); // show time Picker Dialog
        }
        else if (id == R.id.open_camera){ // Opens Camera
            //open camera here.
            Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mIntent.putExtra(MediaStore.EXTRA_OUTPUT,getFileUri()); // getFile Uri is for setting location of image that we are going to click
            mIntent.putExtra("android.intent.extras.CAMERA_FACING",1); // For front Camera

            startActivityForResult(mIntent, CAMERA_REQUEST_CODE);

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected Dialog onCreateDialog(int id){
        switch (id){
            case TIME_DIALOG_ID:
                Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog mTimePickerDialog = new TimePickerDialog(this,mOnTimeSetListener,mHour,mMinute,false);
                mTimePickerDialog.setTitle("Set Daily Selfie Reminder");
                return mTimePickerDialog;

        }
        return null;

    }

    TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() { // It is called time is set
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(view.isShown()){//to check time Picker called once
                Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                long timeSelectedInMilli = (hourOfDay)*60*60*1000L + (minute)*60*1000L;
                long timeCurrInMilli = mHour*60*60*1000L + mMinute*60*1000L;
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                mNotificationReceiverIntent = new Intent(getApplicationContext(),AlarmNotificationReciever.class);
                mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,mNotificationReceiverIntent,0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ (timeSelectedInMilli - timeCurrInMilli),AlarmManager.INTERVAL_DAY,mNotificationReceiverPendingIntent);
                Toast.makeText(getApplicationContext(),"Alarm Created : Hour-"+hourOfDay+", Minute-"+minute,Toast.LENGTH_SHORT).show();
            }

        }
    };

    public Uri getFileUri(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"SelfieTaken");
        File mediaThumbnail = new File(Environment.getExternalStorageDirectory(),"SelfieThumbnail");
        if(!mediaThumbnail.exists()){
            mediaThumbnail.mkdir();
        }
        if(!mediaStorageDir.exists()){
            mediaStorageDir.mkdir();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fullFilename = mediaStorageDir.getPath() + File.separator + "SelFie_"+timeStamp+".jpg";
        thumbnailname = mediaThumbnail.getPath() + File.separator + "SelFie_"+timeStamp+".jpg";
        shortFilename = "SelFie_"+timeStamp+".jpg";
        File mediaFile = new File(fullFilename);

        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("DEBUGER", "camera returned");

                File mFile = new File(fullFilename);
                File mThumFile = new File(thumbnailname);
                new saveImageTask().execute(mFile,mThumFile); // save Image and its thumbnail
                Toast.makeText(getApplicationContext(),"Camera Returned",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class saveImageTask extends AsyncTask<File,Integer,ImageData>{

        @Override
        protected ImageData doInBackground(File... params) {

            Bitmap image = BitmapFactory.decodeFile(params[0].getAbsolutePath());
            try {
                FileOutputStream fos = new FileOutputStream(params[1]);
                image = Bitmap.createScaledBitmap(image,100,100,false);
                image.compress(Bitmap.CompressFormat.JPEG,100,fos);// storing thumbnail image

                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String name = params[1].getAbsolutePath();//storing thumbnail file path in Imagedata

            ImageData mImageData = new ImageData(name);
            Log.d("DEBUGER","Data read Successful");
            return mImageData;
        }
        @Override
        protected void onPostExecute(ImageData imageData){
            Log.d("DEBUGER","onPost Executed Successful");
            mImageListAdapter.add(imageData);//thumbnail file path

            filenamelist.add(fullFilename);// complete file path
        }
    }

    @Override
    public void onBackPressed(){
        Log.d("DEBUGER","Under OnBackPressed");
        ImageDisplayFragment mImageDisplyFragment = (ImageDisplayFragment) getSupportFragmentManager().findFragmentByTag("ImageViewFragmentTag");
        if(mImageDisplyFragment != null && mImageDisplyFragment.isVisible()){
            Log.d("DEBUGER","In the Condition of OnBackPressed");
            super.onBackPressed();
        }
        else{
//            alarmManager.cancel(mNotificationReceiverPendingIntent);
            finish();
        }


    }
}
