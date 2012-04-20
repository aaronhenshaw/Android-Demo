package com.demo;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.ui.CreateTweetActivity;
import com.demo.util.DrawableManager;
import com.demo.util.TwitterUtil;

public class HomeActivity extends Activity {
    
	TwitterArrayAdapter mTwitterAdapter;
	ListView lv;	
	ProgressBar mLoading;
	Button mRefresh;
	Button mCreate;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mCreate = (Button) findViewById(R.id.new_tweet);
        mRefresh = (Button) findViewById(R.id.refresh);
        lv = (ListView) findViewById(R.id.listView1);
        
        mCreate.setOnClickListener(new  OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, CreateTweetActivity.class);
				startActivity(i);
			}
		});
        mRefresh.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				new TwitterAsync().execute();
			}
		});
        lv.setOnScrollListener(new OnScrollListener() {		
        	
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch(scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE:
						if (mTwitterAdapter != null)
							mTwitterAdapter.setupImageLoad();
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {}
		});
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	new TwitterAsync().execute();
    }
    
    class TwitterArrayAdapter extends ArrayAdapter<Status> {

    	public TwitterArrayAdapter(Context context, int resource, int textViewResourceId, List<Status> objects) {
			super(context, resource, textViewResourceId, objects);
			mDrawableManager = new DrawableManager(context);
		}
		
		DrawableManager mDrawableManager;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.component_tweet, null);
			}
				
			TextView tweeter = (TextView) convertView.findViewById(R.id.tweeter);
			TextView tweet = (TextView) convertView.findViewById(R.id.tweet);
			ImageView img = (ImageView) convertView.findViewById(R.id.tweeter_image);
			Status s = getItem(position);
			tweet.setText(s.getText());
			tweeter.setText(s.getUser().getScreenName());
			img.setImageResource(android.R.drawable.btn_star);
			img.setTag(s.getUser().getProfileImageURL().toString());
			mDrawableManager.queueDrawableFetch(s.getUser().getProfileImageURL().toString(), img);
			return convertView;
		}
		
		public void addItems(ResponseList<Status> statuses) {
			clear();
			
			for (Status s : statuses) {
				insert(s, 0);
			}
			notifyDataSetChanged();
		}
		
		public void setupImageLoad() {
			mDrawableManager.clear();
			for(int i=0; i<lv.getChildCount(); i++) {
				ImageView iv = (ImageView) lv.getChildAt(i).findViewById(R.id.tweeter_image);
				mDrawableManager.queueDrawableFetch((String)iv.getTag(), iv);
			}
		}
    	
    }
    
    class TwitterAsync extends AsyncTask<Void, Void, ResponseList<Status>> {

    	@Override
    	protected void onPreExecute() {
    		mLoading.setVisibility(View.VISIBLE);
    		lv.setVisibility(View.GONE);
    	}
    	
		@Override
		protected ResponseList<twitter4j.Status> doInBackground(Void... params) {
			try {
				return TwitterUtil.getTweets();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
    	
		@Override
		protected void onPostExecute(ResponseList<twitter4j.Status> result) {
			try {
				if (result == null) {
					Toast t = Toast.makeText(HomeActivity.this, "Something went wrong...", Toast.LENGTH_LONG);
					t.show();
					return;
				}
				mLoading.setVisibility(View.GONE);
	    		lv.setVisibility(View.VISIBLE);
	    		mTwitterAdapter = new TwitterArrayAdapter(HomeActivity.this, R.layout.component_tweet, 0, result);
	    		lv.setAdapter(mTwitterAdapter);
			} catch(Exception ex) {
				
				ex.printStackTrace();
			}
		}
    }
}