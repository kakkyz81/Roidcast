/**
 * 
 */
package net.krks.android.roidcast;

import net.krks.android.roidcast.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author kakkyz
 *
 */
public class ReceiveUrl extends Activity {
	private static final String TAG = "Roidcast";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Intent intent = getIntent();
    	String s = intent.getStringExtra(Intent.EXTRA_TEXT);
    	Log.i(TAG,"onCreate @@@ roidast" + s);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
