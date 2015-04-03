package com.example.mobiquity_challenge;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
public class DBList extends Activity {
private String PHOTO_DIR="/Photos/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dblist);
		
		/*
		 * retrive the instance of DBRoulette
		 */
		
	    DropboxAPI<AndroidAuthSession> mApi=MainActivity.mApi;
	    
	    TextView tv=(TextView)findViewById(R.id.textView1);
	    tv.setText("/Photos/");
	    String result="File Structure\n";
	    /*
	     * get the list of files that belong to the folder in question.
	     */
	    
			//Entry testing=mApi.metadata(PHOTO_DIR, 0, null, true, null);
			MyListAdapter adapter=new MyListAdapter(DBList.this, PHOTO_DIR);
		final	 ListView list=(ListView)findViewById(R.id.list);
			
		
	    list.setOnItemClickListener(new OnItemClickListener() {
	    	@Override
	    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	    			long arg3) {
	    		// TODO Auto-generated method stub
	    	String path=(String)list.getItemAtPosition(arg2);
	    		Intent selectedPic=new Intent(DBList.this,DownloadPic.class);
	    		selectedPic.putExtra("FILE_PATH", path);
	    		startActivity(selectedPic);
	    		
	    		Log.d("mobiquity",""+(String)list.getItemAtPosition(arg2));
	    	}
		});
	    
	    Log.d("FileList", result);
	    
	    list.setAdapter(adapter);   
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dblist, menu);
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
}
