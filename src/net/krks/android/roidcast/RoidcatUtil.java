package net.krks.android.roidcast;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

public class RoidcatUtil  {
	public void eLog(Throwable e) {
		StringWriter sw = new StringWriter(); 
		e.printStackTrace(new PrintWriter(sw));
		Log.e(Roidcast.TAG, sw.toString());
	}
}
