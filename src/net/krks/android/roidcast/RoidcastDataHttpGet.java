/*
Copyright (C) 2009 kakkyz

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any 
later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
details.

You should have received a copy of the GNU General Public License along with this program. 
If not, see <http://www.gnu.org/licenses/>.

*/

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

public class RoidcastDataHttpGet extends DefaultHttpClient{
	public HttpGet httpGet;
	
	/**
	 * uriを受け取って、inputstreamを返す
	 * 
	 * @param uri
	 * @return InputStream
	 */
	public InputStream getImputStreamOnWeb(String uri){
		Log.d(Roidcast.TAG, "getImputStreamOnWeb:" + uri);
		
		try {
			httpGet.setURI(new URI(uri));
		} catch(URISyntaxException ex){
			new RoidcatUtil().eLog(ex);
			return null;
		}
		
		HttpResponse response = null;
		
		try {
			response = execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			Log.d(Roidcast.TAG, "statusCode:" + statusCode);
			// success
			if ( HttpStatus.SC_OK != statusCode) {
				throw new IOException();
			}
		    return response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			new RoidcatUtil().eLog(e);
		} catch (IOException e) {
			new RoidcatUtil().eLog(e);
		}
		
      return null;
	}

	public RoidcastDataHttpGet() {
		this.httpGet = new HttpGet();
	}
}