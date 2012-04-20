package com.demo.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.R;
import com.demo.util.TwitterUtil;

public class CreateTweetFragment extends Fragment {

	TextView mTweetText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_create_tweet, null);
		Button tweet = (Button) v.findViewById(R.id.tweet);
		Button cancel = (Button) v.findViewById(R.id.cancel);
		mTweetText = (TextView) v.findViewById(R.id.tweetText);
		tweet.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				new CreateTweetAsync().execute(mTweetText.getText().toString());
			}
		});
		cancel.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		return v;
	}
	
	class CreateTweetAsync extends AsyncTask<String, Void, Void> {

		ProgressDialog mProgress = new ProgressDialog(getActivity());
		
    	@Override
    	protected void onPreExecute() {
    		mProgress.setTitle("Tweeting...");
    		mProgress.show();
    	}
    	
		@Override
		protected Void doInBackground(String... params) {
			try {
				TwitterUtil.tweet(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			try {
				mProgress.hide();
				Toast t = Toast.makeText(getActivity(), "Tweet Sent!", Toast.LENGTH_LONG);
				t.show();
				getActivity().finish();
			} catch(Exception ex) {
				ex.printStackTrace();
				Toast t = Toast.makeText(getActivity(), "Tweet Wasn't Sent. There was a problem...", Toast.LENGTH_LONG);
				t.show();
			}
		}
    }
	
}
