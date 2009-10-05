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
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * ブラウザなどから呼び出されて、URLを保存するためのクラス
 * 正常にURLを保存できた時は、Roidcastのメイン画面へ遷移する
 * 
 * @author kakkyz
 *
 */
public class ReceiveUrl extends Activity {
	private ProgressDialog loadingDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	// メッセージとして受けたuriを取り出す
    	final Intent intent = getIntent();
    	final String uri = intent.getStringExtra(Intent.EXTRA_TEXT);
    	
    	Log.d(Roidcast.TAG,"onCreate @@@ roidast" + uri);
    	
    	// loadingのダイアログを出す
		loadingDialog = new ProgressDialog(this);
		
		loadingDialog.setMessage(getText(R.string.roidcast_progress_message));
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.show();
		
		final Handler handler = new Handler();
		final Runnable returnMethod = new Runnable() {				
			@Override
			public void run() {
				loadingDialog.dismiss();
			}
		};
		
		Thread t = new Thread() {
			public void run() {
				// uriをパースしてpodcastオブジェクトにする
		    	Podcast podcast = null;
		    	XMLParse xmlParse = new XMLParse();
		    	podcast = xmlParse.parsePodcastXML(uri);
		    	
		    	if(podcast != null) {
		        	// パース結果を保存する
			    	savePodcast(podcast);
			    	// 次のActivity（メイン画面）を呼び出す
			    	Intent nextIntent = new Intent(getApplicationContext(),Roidcast.class);
			    	startActivity(nextIntent);
		    	} else {
		    		// パースできなかった場合、メッセージを出力して終了する
		    		Log.i(Roidcast.TAG,"ParseError");
		    		Toast.makeText(getApplicationContext()
		    				, getString(R.string.reciveurl_could_not_parse)
							, Toast.LENGTH_SHORT).show();
		    	}
				handler.post(returnMethod);
				finish(); // このactivityは画面表示がないので，backボタンを押したときに何もない画面に戻るのを防止するため，正常時もfinishをコールする。
			}
		};
		t.start();
    }
    
    /**
     * podcastを保存する 
     * @param podcast
     */
    protected void savePodcast(Podcast podcast) {
    	if(podcast == null) { throw new AssertionError(); }

    	RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    	ArrayList<Podcast> podlist = r.doLoad(); // TODO 読み込んで追加して保存という手順が非効率な気がする
    	// 重複時は保存しない
    	if(!isDuplicate(podlist, podcast)){
			podlist.add(podcast);
			try {
				r.doSave(podlist);
			} catch (IOException e) {
				new RoidcatUtil().eLog(e);
			}
    	}
    	
    	r = null; // 後処理
    }
    
    /**
     * podcastが重複していないかチェックする
     * 
     */
    protected boolean isDuplicate(ArrayList<Podcast> a,Podcast newPodcast) {
    	for(Podcast p:a) {
    		if(p.getXmlUrl().equals(newPodcast.getXmlUrl())) {
    			return true;
    		}
    	}
    	return false;
    }
}
