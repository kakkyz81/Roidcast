package net.krks.android.roidcast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class XMLParseDataHttpGet extends DefaultHttpClient{
	public HttpGet httpGet;
	
	/**
	 * uriを受け取って、inputstreamを返す
	 * 
	 * @param uri
	 * @return InputStream
	 */
	public InputStream getImputStreamOnWeb(String uri){
		Log.i(Roidcast.TAG, "getImputStreamOnWeb:" + uri);
		
		try {
			httpGet.setURI(new URI(uri));
		} catch(URISyntaxException ex){
			Log.e(Roidcast.TAG, "URISyntaxException");
			ex.printStackTrace();
			return null;
		}
		
		HttpResponse response = null;
		
		try {
			response = execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			Log.i(Roidcast.TAG, "statusCode:" + statusCode);
			// success
			if ( HttpStatus.SC_OK != statusCode) {
				throw new IOException();
			}
		    return response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
      return null;
	}

	public XMLParseDataHttpGet() {
		this.httpGet = new HttpGet();
	}
}