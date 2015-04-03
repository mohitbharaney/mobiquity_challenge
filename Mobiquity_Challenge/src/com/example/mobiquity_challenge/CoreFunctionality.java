package com.example.mobiquity_challenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.R.string;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class CoreFunctionality extends ActionBarActivity {
    private static final int NEW_PICTURE = 1;
    private String mCameraFileName;
    private String mAudioFileName;
    private  String PHOTO_DIR="/Photos/";
    public static int RECORD_REQUEST = 0;
    private static final int NEW_TEXT= 2;
    String textFilePath=""; 
//    Uri audioFileUri;
    static int counter=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 if (savedInstanceState != null) {
	            mCameraFileName = savedInstanceState.getString("mCameraFileName");
	            mAudioFileName=savedInstanceState.getString("mAudioFileName");
	        }
		setContentView(R.layout.activity_core_functionality);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.core_functionality, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void listPhoto(View v)
	{
		Intent listPhotoIntent=new Intent(this,DBList.class);
		startActivity(listPhotoIntent);
	}
	
	
	 public void clickPhoto(View v) {
         Intent intent = new Intent();
         // Picture from camera
         intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

         // This is not the right way to do this, but for some reason, having
         // it store it in
         // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

         Date date = new Date();
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.US);

         String newPicFile = df.format(date) + ".jpg";
         String outPath = new File(Environment.getExternalStorageDirectory(), newPicFile).getPath();
         File outFile = new File(outPath);

         mCameraFileName = outFile.toString();
         Uri outuri = Uri.fromFile(outFile);
         intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
         Log.i("mobiquity", "Importing New Picture: " + mCameraFileName);
         try {
             startActivityForResult(intent, NEW_PICTURE);
         } catch (ActivityNotFoundException e) {
             ////showToast("There doesn't seem to be a camera.");
        	 Toast error = Toast.makeText(this, "There doesn't seem to be a camera.", Toast.LENGTH_LONG);
 	        error.show();
         }
     }
	
	  protected void onSaveInstanceState(Bundle outState) {
	        outState.putString("mCameraFileName", mCameraFileName);
	        outState.putString("mAudioFileName",mAudioFileName);
	        super.onSaveInstanceState(outState);
	    }
	  
	  public void recordSound(View v)
	  {
		  Date date = new Date();
	         DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.US);
	       
		  String newAudioFile = df.format(date) +".amr";
	         String outPath = new File(Environment.getExternalStorageDirectory(), newAudioFile).getPath();
	         
	         File outFile = new File(outPath);

	         mAudioFileName = outFile.toString();
	         Uri audioFileUri = Uri.fromFile(outFile);
	         
		  Intent intent = new Intent(
		          MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		  intent.putExtra(MediaStore.EXTRA_OUTPUT, audioFileUri);
		      startActivityForResult(intent, RECORD_REQUEST);
	  }
	  
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (requestCode == NEW_PICTURE) {
	            // return from file upload
	            if (resultCode == Activity.RESULT_OK) {
	                Uri uri = null;
	                if (data != null) {
	                    uri = data.getData();
	                }
	                if (uri == null && mCameraFileName != null) {
	                    uri = Uri.fromFile(new File(mCameraFileName));
	                }
	                File file = new File(mCameraFileName);

	                if (uri != null) {
	                    UploadPicture upload = new UploadPicture(this, MainActivity.mApi, PHOTO_DIR, file);
	                    upload.execute();
	                }
	            } else {
	                Log.w("mobiquity", "Unknown Activity Result from mediaImport: "
	                        + resultCode);
	            }
	        }
	        else if(requestCode==RECORD_REQUEST&&resultCode==RESULT_OK)
	        {
	        	Uri uri=null;
	        	if(data!=null)
	        	{	uri=data.getData();
	        		Log.d("mobi", "something recorded");
	        		Log.d("mobi", mAudioFileName);
	        	}
//	        	if(uri==null&&mAudioFileName!=null)
//	        		uri=Uri.fromFile(new File(mAudioFileName));
//	        	File file=new File(mAudioFileName);
//	        
	        	
	        	

	        	File folder = new File(Environment.getExternalStorageDirectory(), "/Recordings");
	            long folderModi = folder.lastModified();
	         
	            
	            
	            File[] folderList = folder.listFiles();

	            int max=0;
	            long maxVal=folderList[0].lastModified();
	            
	            for(int i=1; i<folderList.length;i++)
	            {
	                long fileModi = folderList[i].lastModified();
	                Log.d("mobi","test "+folderList[i].getAbsolutePath());
	                if(maxVal < fileModi)
	                {
	                	max=i;
	                	maxVal=fileModi;
	                
	                }
	            }
	        	
	        	
	        	
	        	if (max<folderList.length) {
                    UploadPicture upload = new UploadPicture(this, MainActivity.mApi, PHOTO_DIR, folderList[max]);
                    upload.execute();
	        	}
//	        	MediaPlayer mediaPlayer = MediaPlayer.create(this, audioFileUri);
//	            mediaPlayer.setOnCompletionListener((OnCompletionListener) this);
//	            mediaPlayer.start();
	        	
	        }
	        else if(requestCode==NEW_TEXT)
	        {
	        	String path=textFilePath;
	        	if(path!=null)
	        	{
	        		File file=new File(path);
	        		if(file.exists()){
	        		 UploadPicture upload = new UploadPicture(this, MainActivity.mApi, PHOTO_DIR, file);
	                    upload.execute();
	        		}
	        	}
	        }
	    }

	
	  public void trimSound(View v)
	  {
		  Intent intent=getPackageManager().getLaunchIntentForPackage("com.ringdroid");
		  startActivity(intent);
	  }
	 
	  public void creatFile(View v){
		  Date date = new Date();
	         DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.US);
	       
		  String newTxtFile = df.format(date) +".txt";
	         String outPath = new File(Environment.getExternalStorageDirectory(), newTxtFile).getPath();
	         
	         //FileWriter fos=openFileOutput(outPath, 0);
	          
//
		  Intent intent = new Intent(Intent.ACTION_VIEW); 
		  Uri uri = Uri.parse(outPath); 
		  intent.setDataAndType(uri, "text/plain"); 
		  intent.putExtra("FILE_NAME", outPath);
		  textFilePath=outPath;
		  try{
			  startActivityForResult(intent, NEW_TEXT);
		  }
		  catch (ActivityNotFoundException e) {
	             ////showToast("There doesn't seem to be a camera.");
	        	 Toast error = Toast.makeText(this, "There doesn't seem to be an app to open text file.", Toast.LENGTH_LONG);
	 	        error.show();
	         }	  }

}
