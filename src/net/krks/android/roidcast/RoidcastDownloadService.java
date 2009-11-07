package net.krks.android.roidcast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import net.krks.android.roidcast.R;
import net.krks.android.roidcast.R.drawable;
import net.krks.android.roidcast.R.string;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class RoidcastDownloadService extends Service {
	
	private Handler mHandler;
	private boolean mRunning;
	private String mUri;
	private String mMediaType;
	private String mTitle;
	private NotificationManager mManager;
	
	
	
	public void doDown() {
		Log.i(Roidcast.TAG,"DownloadService start. uri=" + mUri);
		// TODO Auto-generated method stub
		RoidcastDataHttpGet httpget = new RoidcastDataHttpGet();
		InputStream in = httpget.getImputStreamOnWeb(mUri);

		String[] tmpString = mUri.split("/");
		String fileName = tmpString[tmpString.length -1];
		
		File file = new File(Environment.getExternalStorageDirectory(),fileName);
		
		String[] tmpStringForExtension = fileName.split("\\.");
		String exteinsion = tmpStringForExtension[tmpStringForExtension.length - 1];
		
		String[] tmpStringsForMediaType = mMediaType.split("/");
		String mediaType = tmpStringsForMediaType[0];
		
		ContentValues values = new ContentValues(3);
		Uri uri;
		
		// save for MediaStore
		if(isMediaTypeAudio(mediaType,exteinsion)) {
			values.put(android.provider.MediaStore.Audio.Media.MIME_TYPE,mMediaType);
			values.put(android.provider.MediaStore.Audio.Media.TITLE,mTitle);
			values.put(android.provider.MediaStore.Audio.Media.DATA, file.getAbsolutePath());
			uri = getContentResolver().insert(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
		}else if(isMediaTypeVideo(mediaType,exteinsion)){
			values.put(android.provider.MediaStore.Video.Media.MIME_TYPE,mMediaType);
			values.put(android.provider.MediaStore.Video.Media.TITLE,mTitle);
			values.put(android.provider.MediaStore.Video.Media.DATA, file.getAbsolutePath());
			uri = getContentResolver().insert(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
		}else {
			throw new RuntimeException("unknown media type");
		}

		BufferedOutputStream bout = null;
		BufferedInputStream bin = null;
		
		try {
			bin = new BufferedInputStream(in, 1024*8);
			bout = new BufferedOutputStream(getContentResolver().openOutputStream(uri),1024*8);
						
			byte buf[] = new byte[1024*8];
			int len;
			while((len = in.read(buf)) != -1) {
				bout.write(buf,0,len);
			}
			bout.flush();
			Log.i(Roidcast.TAG,"DownloadService normal end. uri=" + mUri);
			
			Notification n = new Notification();
			PendingIntent pintent = PendingIntent.getActivity(this, 0, new Intent(this,Roidcast.class), 0);
			n.setLatestEventInfo(getApplicationContext(),
								getString(R.string.roidcast_download_service_complete),
								mTitle,
								pintent);
			n.when = System.currentTimeMillis();
			n.tickerText = getString(R.string.roidcast_download_service_complete);
			n.icon = R.drawable.roidcast_icon_01;
			
			mManager.notify(R.string.app_name,n);
			
		} catch (Exception e) {
			Notification n = new Notification();
			PendingIntent pintent = PendingIntent.getActivity(this, 0, new Intent(this,Roidcast.class), Notification.FLAG_AUTO_CANCEL);
			n.setLatestEventInfo(getApplicationContext(),
								getString(R.string.roidcast_download_service_failed),
								mTitle,
								pintent);
			n.when = System.currentTimeMillis();
			n.tickerText = getString(R.string.roidcast_download_service_failed);
			n.icon = R.drawable.roidcast_icon_01;
			
			mManager.notify(R.string.app_name,n);
			new RoidcatUtil().eLog(e);
		} finally {
			if(null != bout) {
				try {
					bout.close();
				} catch (IOException e) {
					new RoidcatUtil().eLog(e);
				}
			}
			if(null != bin) {
				try {
					bin.close();
				} catch (IOException e) {
					new RoidcatUtil().eLog(e);
				}
			}
			stopSelf();
		}

	}
	

	
	@Override
	public void onCreate() {
		Log.i(Roidcast.TAG,"RoidcastDownladService:onCreate");
		super.onCreate();
		mRunning = false;
		mHandler = new Handler();
		mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	}


	@Override
	public void onDestroy() {
		Log.i(Roidcast.TAG,"RoidcastDownladService:onDestroy");
		mRunning = false;
		super.onDestroy();
	}


	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(Roidcast.TAG,"RoidcastDownladService:onStart " + startId);
		
	//	super.onStart(intent, startId);
		mUri = intent.getStringExtra(RoidcastFileIo.EXTRA_URI);
		mMediaType = intent.getStringExtra(RoidcastFileIo.EXTRA_MEDIA_TYPE);
		mTitle = intent.getStringExtra(RoidcastFileIo.EXTRA_TITLE);
//		mHandler.post(this); // run()メソッドを起動
		doDown();
	}


	/**
	 * リモートから呼び出される場合に実装、今回は関係ない
	 */
	@Override
	public IBinder onBind(Intent intent) {	return null;}	
	
	/**
	 * メディアタイプと、拡張子からAUDIOかどうかを判断する
	 * @param mediaType
	 * @param extension
	 * @return
	 */
	private boolean isMediaTypeAudio(String mediaType,String extension) {
		if(mediaType == null || extension == null) {
			return false;
		}
		if("audio".equalsIgnoreCase(mediaType)){
			return true;
		}
		if("mp3".equalsIgnoreCase(extension)) {
			return true;
		}
		return false;
	}
	
	/**
	 * メディアタイプと、拡張子からVIDEOかどうかを判断する
	 * @param mediaType
	 * @param extension
	 * @return
	 */
	private boolean isMediaTypeVideo(String mediaType,String extension) {
		if(mediaType == null || extension == null) {
			return false;
		}
		if("video".equalsIgnoreCase(mediaType)){
			return true;
		}
		if("mp4".equalsIgnoreCase(extension) || "3gp".equalsIgnoreCase(extension) 
				|| "m4a".equalsIgnoreCase(extension) ) {
			return true;
		}
		return false;
	}
	
}
