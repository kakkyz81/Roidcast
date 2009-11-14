package net.krks.android.roidcast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class RoidcastDownloadService extends Service {
	
	private NotificationManager mManager;
	
	
	/**
	 * 拡張子を返す
	 * @param uri
	 * @return 
	 */
	private String getStringExteinsion(final String uri) {
		try {
			String[] tmpStrings = uri.split("\\.");
			return tmpStrings[tmpStrings.length -1];
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * ファイル名を返す
	 * @param uri
	 * @return
	 */
	private String getFileName(final String uri) {
		try {
			String[] tmpStrings = uri.split("/");
			return tmpStrings[tmpStrings.length -1];
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * "audio/mpeg" のとき、"audio"を返す
	 */
	private String getMediaTypeFirst(final String mediaType) {
		try {
			String[] tmpStringsForMediaType = mediaType.split("/");
			return tmpStringsForMediaType[0];
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 * ダウンロードを実行する
	 * @param uri
	 * @param mediaType
	 * @param title
	 */
	public void doDown(String uri,String mediaType,String title) {
		Log.i(Roidcast.TAG,"DownloadService start. uri=" + uri);
		
		RoidcastDataHttpGet httpget = new RoidcastDataHttpGet();
		InputStream in = httpget.getImputStreamOnWeb(uri);
		
		File file = null;
		File dir = null;
		dir = new File(Environment.getExternalStorageDirectory() + "/" +
				getString(R.string.app_name));
		file = new File(dir,getFileName(uri));
		
		if(!dir.exists()) {
			if(!dir.mkdir()){
				throw new RuntimeException("mkdir failed");
			}
		}
		
		String exteinsion = getStringExteinsion(uri);
		String mediaTypeFirst = getMediaTypeFirst(mediaType);
		
		ContentValues values = new ContentValues(3);
		Uri mediaStoreUri;
		
		// save for MediaStore
		if(isMediaTypeAudio(mediaTypeFirst,exteinsion)) {
			values.put(android.provider.MediaStore.Audio.Media.MIME_TYPE,mediaTypeFirst);
			values.put(android.provider.MediaStore.Audio.Media.TITLE,title);
			values.put(android.provider.MediaStore.Audio.Media.DATA, file.getAbsolutePath());
			mediaStoreUri = getContentResolver().insert(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
		}else if(isMediaTypeVideo(mediaTypeFirst,exteinsion)){
			values.put(android.provider.MediaStore.Video.Media.MIME_TYPE,mediaTypeFirst);
			values.put(android.provider.MediaStore.Video.Media.TITLE,title);
			values.put(android.provider.MediaStore.Video.Media.DATA, file.getAbsolutePath());
			mediaStoreUri = getContentResolver().insert(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
		}else {
			throw new RuntimeException("unknown media type");
		}

		BufferedOutputStream bout = null;
		BufferedInputStream bin = null;
		
		try {
			/* バッファ読み込みと保存 */
			bin = new BufferedInputStream(in, 1024*8);
			bout = new BufferedOutputStream(getContentResolver().openOutputStream(mediaStoreUri),1024*8);
						
			byte buf[] = new byte[1024*8];
			int len;
			while((len = in.read(buf)) != -1) {
				bout.write(buf,0,len);
			}
			bout.flush();
			Log.i(Roidcast.TAG,"DownloadService normal end. uri=" + uri);
			
			Notification n = new Notification();
			PendingIntent pintent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(),Roidcast.class), 0);
			n.setLatestEventInfo(getApplicationContext(),
								getString(R.string.roidcast_download_service_complete),
								title,
								pintent);
			n.when = System.currentTimeMillis();
			n.tickerText = getString(R.string.roidcast_download_service_complete);
			n.icon = R.drawable.roidcast_icon_01;
			
			mManager.notify(R.string.app_name,n);
			
		} catch (Exception e) {
			Notification n = new Notification();
			PendingIntent pintent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(),Roidcast.class), 0);
			
			n.setLatestEventInfo(getApplicationContext(),
								getString(R.string.roidcast_download_service_failed),
								title,
								pintent);
			n.when = System.currentTimeMillis();
			n.tickerText = getString(R.string.roidcast_download_service_failed);
			n.icon = R.drawable.roidcast_icon_01;
			
			mManager.notify(R.string.app_name,n);
			new RoidcastUtil().eLog(e);
		} finally {
			if(null != bout) {
				try {
					bout.close();
				} catch (IOException e) {
					new RoidcastUtil().eLog(e);
				}
			}
			if(null != bin) {
				try {
					bin.close();
				} catch (IOException e) {
					new RoidcastUtil().eLog(e);
				}
			}
			stopSelf();
		}

	}
	

	
	@Override
	public void onCreate() {
		Log.i(Roidcast.TAG,"RoidcastDownladService:onCreate");
		super.onCreate();
		mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	}


	@Override
	public void onDestroy() {
		Log.i(Roidcast.TAG,"RoidcastDownladService:onDestroy");
		super.onDestroy();
	}


	@Override
	public synchronized void onStart(final Intent intent, int startId) {
		Log.i(Roidcast.TAG,"RoidcastDownladService:onStart " + startId);
		Thread t = new Thread() {
			public void run() {
				doDown(intent.getStringExtra(RoidcastFileIo.EXTRA_URI),
					   intent.getStringExtra(RoidcastFileIo.EXTRA_MEDIA_TYPE),
					   intent.getStringExtra(RoidcastFileIo.EXTRA_TITLE));			
			}
		};
		t.start();

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
