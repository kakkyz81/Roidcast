package net.krks.android.roidcast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

@SuppressWarnings("unused")
public class Roidcast extends ListActivity {
	public static final String TAG = "Roidcast";
	ArrayAdapter<Podcast> mAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG,"onCreate @@@ roidast");
        super.onCreate(savedInstanceState);
        
        ArrayList<Podcast> loadData = doLoad();        
        
        mAdapter = new ArrayAdapter<Podcast>(
			        		getApplicationContext(),
			        		R.layout.main_list_row,
			        		loadData
        				);
        
        setListAdapter(mAdapter);
        setContentView(R.layout.main);
    }
	
    public void doSave(ArrayList<Podcast> a) throws IOException{
    	RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    	r.doSave(a);
	}
	
	public ArrayList<Podcast> doLoad(){
		RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    	return r.doLoad();
	}
}