package net.krks.android.roidcast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import net.krks.android.roidcast.Podcast.PodcastItem;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

@SuppressWarnings("unused")
public class Roidcast extends ExpandableListActivity  implements View.OnClickListener{
	public static final String TAG = "Roidcast";
	ExpandableListAdapter mAdapter;
	
	ArrayList<Podcast> loadData;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
    	Log.i(TAG,"onCreate @@@ roidast");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ImageButton b = (ImageButton)findViewById(R.id.RecrawlButton);
        //RoidcastClickLisner roidcastClickLisner = new RoidcastClickLisner();
        b.setOnClickListener(this);
        
        registerForContextMenu(getExpandableListView());
        
        // TODO 起動時に自動でrecrawlする(すべきでないのでは？と考えとりあえず実装しない)
        //loadData = doLoad();
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		try {
			doSave();
		} catch (IOException e) {
			new RoidcatUtil().eLog(e);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		loadData = doLoad();
		
        doDraw();
	}
	
	protected void doDraw() {
		//RoidcastEVLAdapter roidcastEVLAdapter = new RoidcastEVLAdapter(getApplicationContext());
        RoidcastEVLAdapter roidcastEVLAdapter = new RoidcastEVLAdapter();
        roidcastEVLAdapter.setPodcastList(loadData);
        
        // ExpandListViewを登録する時のお決まりの文法
        mAdapter = roidcastEVLAdapter;
        setListAdapter(mAdapter);
      //!?
       // registerForContextMenu(getExpandableListView());
        
	}
	
	int MENU_ITEM_DELETE = 0;
	int MENU_ITEM_UP = 1;
	int MENU_ITEM_DOWN = 2;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.roidcast_context_menu_header);
		menu.add(Menu.NONE, MENU_ITEM_DELETE , MENU_ITEM_DELETE, R.string.roidcast_context_menu_delete);
		menu.add(Menu.NONE, MENU_ITEM_UP , MENU_ITEM_UP, R.string.roidcast_context_menu_up);
		menu.add(Menu.NONE, MENU_ITEM_DOWN , MENU_ITEM_DOWN, R.string.roidcast_context_menu_down);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		ExpandableListContextMenuInfo menuinfo = (ExpandableListContextMenuInfo)item.getMenuInfo();
		
		int menuid = item.getItemId();
		// int groupid = item.getGroupId(); //menuはgroup化していないので使用しない
		
		int type = ExpandableListView.getPackedPositionType(menuinfo.packedPosition);
		int groupPosition = ExpandableListView.getPackedPositionGroup(menuinfo.packedPosition);
		int childPosition = ExpandableListView.getPackedPositionChild(menuinfo.packedPosition);
		
		if(MENU_ITEM_DELETE == menuid){
			if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				doDeleteGroup(groupPosition);
			}else if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				doDeleteChild(groupPosition,childPosition);
			}	
		}else if(MENU_ITEM_UP == menuid) {
			if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				doUpGroup(groupPosition);
			}else if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				doUpChild(groupPosition,childPosition);
			}
		}else if(MENU_ITEM_DOWN == menuid) {
			if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				doDownGroup(groupPosition);
			}else if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				doDownChild(groupPosition,childPosition);
			}
		}
		
		doDraw();
		
		//String dummy = new String();
			// super.onContextItemSelected(item);
			// 親要素の時だけ処理する
/*			int groupPosition = ExpandableListView.getPackedPositionGroup(menuinfo.packedPosition);
			Podcast podcast = (Podcast)mAdapter.getGroup(groupPosition);
			String title = podcast.getTitle();
			loadData.remove(groupPosition);
			try {
				doSave();
			} catch (IOException e) {
				new RoidcatUtil().eLog(e);
			}
			
			doDraw();
			Toast.makeText(getApplicationContext()
					, title + " " + getString(R.string.roidcast_context_menu_delete_done)
					, Toast.LENGTH_SHORT).show();
*/
		
		return true;
	}

	/*
	protected class RoidcastClickLisner implements OnClickListener{
    	@Override
    	public void onClick(View v) {
    		RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    		loadData= r.doLoad();
    		
    		for(Podcast p:loadData) {
    			p.reCrawl();
    		}
    		try {
				r.doSave(loadData);
			} catch (IOException e) {
				new RoidcatUtil().eLog(e);
			}
			
    	}
    }
    */
	
	/**
	 * 選択された要素を１つ下げる
	 * 
	 */
    private void doDownChild(int groupPosition, int childPosition) {
		Podcast podcast = (Podcast)mAdapter.getGroup(groupPosition);
		ArrayList<PodcastItem> items  = podcast.getItems();
		PodcastItem moveItem = items.get(childPosition);
		
		if((items.size() -1 ) <= childPosition) { return;}
		
		items.remove(childPosition);
		items.add(childPosition + 1,moveItem);
	}
    
    /**
     * 選択された要素を１つ下げる
     * @param groupPosition
     */
	private void doDownGroup(int groupPosition) {
		Podcast podcast = (Podcast)mAdapter.getGroup(groupPosition);
		
		if((loadData.size() -1 ) <= groupPosition) { return;}				
		
		loadData.remove(groupPosition);
		loadData.add(groupPosition + 1,podcast);
	}

	private void doUpChild(int groupPosition, int childPosition) {
		Podcast podcast = (Podcast)mAdapter.getGroup(groupPosition);
		ArrayList<PodcastItem> items  = podcast.getItems();
		PodcastItem moveItem = items.get(childPosition);
		
		if(0 == childPosition) { return;}
		
		items.remove(childPosition);
		items.add(childPosition - 1,moveItem);
	}

	private void doUpGroup(int groupPosition) {
		Podcast podcast = (Podcast)mAdapter.getGroup(groupPosition);
		
		if(0 == groupPosition) { return;}
		
		loadData.remove(groupPosition);
		loadData.add(groupPosition - 1,podcast);
		
	}

	private void doDeleteChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteGroup(int groupPosition) {
		// TODO Auto-generated method stub
		
	}

	public void doSave() throws IOException{
    	RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    	r.doSave(loadData);
	}
    
    /**
     * 子要素をタップまたはカーソルで選んでトラックボールのボタンを押したとき
     */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if (v instanceof RoidcastTextView) {
			RoidcastTextView rv = (RoidcastTextView) v;
			
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse(rv.getAudioUri()), rv.getMediaType());
			try {
				// 最後に再生した時間を保存する
				loadData.get(groupPosition).getItems().get(childPosition).setLastPlayedDate(new Date());
				doSave();
				startActivity(i);
			} catch (ActivityNotFoundException e) {
				/*
				 * TODO mp4を独自再生する
				VideoView video = new VideoView(getApplicationContext());
				video.setMediaController(new MediaController(getApplicationContext()));
				video.setVideoURI(Uri.parse(tv.getAudioUri()));
				video.requestFocus();
				*/
				
				Toast.makeText(getApplicationContext(), R.string.activity_notfound, Toast.LENGTH_SHORT).show();
				new RoidcatUtil().eLog(e);
			} catch (IOException e) {
				new RoidcatUtil().eLog(e);
			}
			return true;
		}
		return false;
		//return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	public ArrayList<Podcast> doLoad(){
		RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    	return r.doLoad();
	}
	
	
	public class RoidcastEVLAdapter extends BaseExpandableListAdapter {
		private ArrayList<Podcast> podcastList = new ArrayList<Podcast>();
		
		public void setPodcastList(ArrayList<Podcast> podcastList) {
			this.podcastList = podcastList;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return podcastList.get(groupPosition).getItems().get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		
		/** 
		 * expandviewの親をクリックした後開かれる子要素のViewクラスを返す 
		 * */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
        	// TODO
			RoidcastTextView textView = new RoidcastTextView(Roidcast.this);
			setTextViewParams(textView);
			
			PodcastItem podcatItem = (PodcastItem)getChild(groupPosition, childPosition);
			/* 子要素の表示はここで決まる */
            textView.setText(podcatItem.getTitle());
			textView.setAudioUri(podcatItem.getAudioUri());
			textView.setMediaType(podcatItem.getMediaType());
			// 再生していないものは色変更
			if(null == podcatItem.getLastPlayedDate()) {
				textView.setTextColor(Color.CYAN);
			}
				
			
			return textView;
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {
			return podcastList.get(groupPosition).getItems().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return podcastList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return podcastList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

            TextView textView = new TextView(Roidcast.this);
            setTextViewParams(textView);
			
			Podcast p = (Podcast) getGroup(groupPosition);
			//CharSequence c =  getResources().getText((R.string.last_publish_date));
            textView.setText(p.getTitle() + "\n" + 
            		p.getLatestItemDate());
            
            // 子要素に、再生していないものがあれば色を変更する
            Podcast podcast = (Podcast)getGroup(groupPosition);
            boolean hasNotPlayItem = false;
            for(PodcastItem item:podcast.getItems()) {
            	if(item.getLastPlayedDate() == null) {
            		hasNotPlayItem = true;
            	}
            }
            if(hasNotPlayItem) {
            	textView.setTextColor(Color.CYAN);
            }
            return textView;
		}
		
		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return !(podcastList.get(groupPosition).isEmpty());
		}

		/**
		 * TextViewのプロパティ値を設定する。（親要素、子要素共通）
		 */
        protected void setTextViewParams(TextView t) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);

            t.setLayoutParams(lp);
            t.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            t.setPadding(36, 0, 0, 0);
        }

	}
	
	private ProgressDialog loadingDialog;
	
	@Override
	public void onClick(View v) {
		ProgressDialog progressDialog = new ProgressDialog(this);
		
		// 再読み込み処理
		if(v.getId() == R.id.RecrawlButton) {
			String message = "now loading...";
			loadingDialog = new ProgressDialog(this);
			
			loadingDialog.setMessage(getText(R.string.roidcast_progress_message));
			loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loadingDialog.show();
			
			final Handler handler = new Handler();
			final Runnable returnMethod = new Runnable() {				
				@Override
				public void run() {
					doReclawlThreadEnded();
				}
			};
			
			Thread t = new Thread() {
				public void run() {
					for(Podcast p:loadData){
						p.reCrawl();
					}
					handler.post(returnMethod);
				}
			};
			t.start();
		}
		
		
		
	}
	
	/**
	 * 再読み込みが終わった後（プログレスダイアログを閉じる）
	 */
	public void doReclawlThreadEnded() {
		// ダイアログを閉じる
		loadingDialog.dismiss();
		
		// 再読み込みしたデータの保存
		RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
		try {
			r.doSave(loadData);
		} catch (IOException e) {
			new RoidcatUtil().eLog(e);
		}
		doDraw();
	}
	
}