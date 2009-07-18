package net.krks.android.roidcast;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Roidcast extends Activity {
	private static final String TAG = "Roidcast";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG,"onCreate @@@ roidast");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}