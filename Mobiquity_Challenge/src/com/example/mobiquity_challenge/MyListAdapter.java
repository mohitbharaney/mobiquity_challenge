package com.example.mobiquity_challenge;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI.ThumbFormat;
import com.dropbox.client2.DropboxAPI.ThumbSize;
import com.dropbox.client2.DropboxAPI.Entry;


public class MyListAdapter extends ArrayAdapter<String>{

	ArrayList<Drawable> drawables=new ArrayList<Drawable>();
	String PHOTO_DIR="";
	ArrayList<String> filePath=new ArrayList<String>();
	ArrayList<String>fileName=new ArrayList<String>();
	private final Activity context;
	public MyListAdapter(Activity context,String PHOTO_DIR)
	{
		super(context,R.layout.list_layout);
		this.PHOTO_DIR=PHOTO_DIR;
		this.context=context;
		DownLoadThumbnails dbt=new DownLoadThumbnails(context, MainActivity.mApi, PHOTO_DIR, drawables, fileName, filePath);
		dbt.execute();
		try {
			dbt.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		return fileName.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return filePath.get(position);
	}
	
	public String getItemAtPosition(int arg0)
	{
		if(!filePath.isEmpty())
		return filePath.get(arg0);
		else
			return null;
	}
	
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LayoutInflater inflater=context.getLayoutInflater();
		 View rowView=inflater.inflate(R.layout.list_layout, null,true);
		 
		 TextView txtTitle = (TextView) rowView.findViewById(R.id.fileName);
		 ImageView imageView = (ImageView) rowView.findViewById(R.id.thumbnail);
		
		 
		 txtTitle.setText(fileName.get(arg0));
		 
		 imageView.setImageDrawable(drawables.get(arg0));
		   
		 
		 return rowView;
		
		
	}

}
