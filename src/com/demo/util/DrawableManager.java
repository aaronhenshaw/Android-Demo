package com.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class DrawableManager {
	
	private LinkedList<PhotoToLoad> photoQueue = new LinkedList<PhotoToLoad>();
	private PhotosLoader photoLoaderThread;
	private Context mContext;
	private boolean _threadRunning = false;
	private Handler handler;
	
	public DrawableManager(Context ctx) {
		this.mContext = ctx;
	}
	
    public BitmapDrawable fetchDrawable(String urlString) {        
        try {
        	BitmapDrawable drawable = new BitmapDrawable(fetch(urlString));            
            return drawable;
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
	
    private Bitmap fetch(String urlString) throws MalformedURLException, IOException {
    	Bitmap bitmap = null;
    	URL url = new URL(urlString);
    	URLConnection connection = url.openConnection();
    	connection.setUseCaches(true);
    	Bitmap response = BitmapFactory.decodeStream((InputStream) connection.getContent());
    	if(response instanceof Bitmap){
    		 bitmap = (Bitmap)response;
    	}
    	return bitmap;
    }
    
    public void queueDrawableFetch(final String urlString, final ImageView imageView) {
    	if (urlString == null || urlString.equals("")) {
    		if (imageView != null)
    			imageView.setImageResource(android.R.drawable.btn_star);
    		return;
    	} else
    		if (imageView.getTag() == null)
				imageView.setTag(urlString);    			 		
        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
            	try {
	            	if (message != null) {
		            	PhotoToLoad pl = (PhotoToLoad)message.obj;
		            	if (pl != null) {
			            	String tag = (String)pl.imageView.getTag();
			        		if (pl.photo != null && pl.photo.getBitmap() != null && tag != null && pl.url != null && tag.toLowerCase().trim().equals(pl.url.toLowerCase().trim())) {
			        			pl.imageView.setImageDrawable(pl.photo);
			        			if (pl.imageView.getDrawable() == null) {
			        				pl.imageView.setImageResource(android.R.drawable.btn_star);
			        			}
			        		} else {
			        			pl.imageView.setImageResource(android.R.drawable.btn_star);			
			        		}
		            	}
	            	}
            	} catch(Exception ex) { 
            		try {
            			PhotoToLoad pl = (PhotoToLoad)message.obj;
            			if (pl != null) {
            				pl.imageView.setImageResource(android.R.drawable.btn_star);
            			}
            		} catch(Exception ex2) {
            			ex2.printStackTrace();
            		}
            	}
            }
        };    

    	
    	photoQueue.addLast(new PhotoToLoad(urlString, imageView));    	
    	if (!_threadRunning) {
    		_threadRunning = true;    		
    		try {
    			//photoLoaderThread.stop();
    			photoLoaderThread = new PhotosLoader(mContext);
    			photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
    			photoLoaderThread.start();
    		} catch (Exception ex) {
    			Log.e(this.getClass().getSimpleName(), "photo: failed to start photoLoader thread...", ex);
    			_threadRunning = false;    		
    		}
    	}    	
    }
    
    class PhotosLoader extends Thread {
    	
    	private Context ctx = null;
    	
    	public PhotosLoader(Context ctx) {
    		this.ctx = ctx;
    	}
    	
        public void run() {
            try {
                while(photoQueue.size() > 0)
                {
                    //thread waits until there are any images to load in the queue
                    if(photoQueue.size()!=0)
                    {
                        PhotoToLoad photoToLoad;
                        synchronized(photoQueue){
                            photoToLoad=photoQueue.remove(0);
                        }
                        BitmapDrawable drw = null;
                        if (photoToLoad.url != null && !photoToLoad.url.equals("") && (drw == null || drw.getBitmap() == null)) {
                        	drw = fetchDrawable(photoToLoad.url);
	                    }                        
                        Object tag = photoToLoad.imageView.getTag();
                        if (drw != null && drw.getBitmap() != null && tag!=null) {
	                        if(((String)tag).equals(photoToLoad.url)) {
	                        	photoToLoad.photo = drw;
	                        }
                        }
                        Message message = handler.obtainMessage(1, photoToLoad);
                        handler.sendMessage(message);             
                    }
                    if(Thread.interrupted()) {
                    	break;
                    }
                }
                _threadRunning = false;
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
            	_threadRunning = false;
            	this.interrupt();
            }
            this.interrupt();
        }
    }
    
    public void clear() {
    	photoQueue.clear();
    }
    
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public BitmapDrawable photo;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
}
