/**
 * 
 */
package net.krks.android.roidcast;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author kakkyz
 *
 */
public class ReceiveUrl extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	// メッセージとして受けたuriを取り出す
    	Intent intent = getIntent();
    	String uri = intent.getStringExtra(Intent.EXTRA_TEXT);
    	
    	Log.i(Roidcast.TAG,"onCreate @@@ roidast" + uri);
    	
    	// uriをパースしてpodcastオブジェクトにする
    	Podcast podcast = null;
    	XMLParse xmlParse = new XMLParse();
    	podcast = xmlParse.parsePodcastXML(uri);
    	
    	// パース結果を保存する
    	RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    	ArrayList<Podcast> podlist = r.doLoad(); // TODO 読み込んで追加して保存という手順が非効率な気がする
    	podlist.add(podcast);
    	try {
			r.doSave(podlist);
		} catch (IOException e) {
			new RoidcatUtil().eLog(e);
		}
    	r = null; // 後処理
    	
    	// 次のActivity（メイン画面）を呼び出す
    	Intent nextIntent = new Intent(getApplicationContext(),Roidcast.class);
    	startActivity(nextIntent);
        
    }
}
