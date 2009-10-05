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
import java.util.Date;

import net.krks.android.roidcast.Podcast.PodcastItem;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
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
	
    public ArrayList<Podcast> getLoadData() {
		return loadData;
	}

	public void setLoadData(ArrayList<Podcast> loadData) {
		this.loadData = loadData;
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
    	Log.d(TAG,"onCreate @@@ roidast");
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
        RoidcastEVLAdapter roidcastEVLAdapter = new RoidcastEVLAdapter();
        roidcastEVLAdapter.setPodcastList(loadData);
        
        // ExpandListViewを登録する時のお決まりの文法
        mAdapter = roidcastEVLAdapter;
        setListAdapter(mAdapter);
      // ?
      // registerForContextMenu(getExpandableListView());
        
	}
	
	
	final int MENU_ITME_NAME_CHANGE = 0;
	final int MENU_ITEM_DELETE = 1;
	final int MENU_ITEM_UP = 2;
	final int MENU_ITEM_DOWN = 3;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.roidcast_context_menu_header);
		menu.add(Menu.NONE, MENU_ITME_NAME_CHANGE , MENU_ITME_NAME_CHANGE, R.string.roidcast_context_menu_change_name);
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
		}else if(MENU_ITME_NAME_CHANGE == menuid) {
			if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				doNameChengeGroup(groupPosition);
			}else if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				doNameChengeChild(groupPosition,childPosition);
			}
		}
		
		doDraw();
		
		return true;
	}
	
	/**
	 * 選択された要素の名前を変更する
	 * @param groupPosition
	 * @param childPosition
	 */
	private void doNameChengeChild(final int groupPosition,final int childPosition) {
		Podcast podcast = (Podcast)mAdapter.getGroup(groupPosition);
		ArrayList<PodcastItem> items  = podcast.getItems();
		final PodcastItem nameChangeItem = items.get(childPosition);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		final String title = nameChangeItem.getTitle();
		
		builder.setMessage(getText(R.string.roidcast_context_menu_change_name));
		
		builder.setCancelable(true);
		builder.setTitle(title);
		final EditText changedNameView = new EditText(this);
		changedNameView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		builder.setView(changedNameView);
		builder.setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newTitle = changedNameView.getText().toString();
				if(null != newTitle && !"".equals(newTitle)) {
					nameChangeItem.setTitle(newTitle);
				}
//				Toast.makeText(getApplicationContext()
//						, title + " " + getString(R.string.roidcast_context_menu_delete_done)
//						, Toast.LENGTH_LONG).show();
				doDraw(); // 再描画
			}
		});
		
		builder.setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// nothing to do
				;
			}
		});
		
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	/**
	 * 選択された要素の名前を変更する
	 * @param groupPosition
	 */
	private void doNameChengeGroup(final int groupPosition) {
		Podcast podcast = (Podcast)mAdapter.getGroup(groupPosition);
		final String title = podcast.getTitle();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// TODO message
		builder.setMessage(getText(R.string.roidcast_context_menu_change_name));
		
		builder.setCancelable(true);
		builder.setTitle(title);
		final EditText changedNameView = new EditText(this);
		changedNameView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		builder.setView(changedNameView);
		builder.setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newTitle = changedNameView.getText().toString();
				if(null != newTitle && !"".equals(newTitle)) {
					loadData.get(groupPosition).setTitle(newTitle);
				}
//				Toast.makeText(getApplicationContext()
//						, title + " " + getString(R.string.roidcast_context_menu_delete_done)
//						, Toast.LENGTH_LONG).show();
				doDraw(); // 再描画
			}
		});
		
		builder.setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// nothing to do
				;
			}
		});
		
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}

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
	
	/**
	 * podcastの1話を削除する
	 * @param groupPosition
	 * @param childPosition
	 */
	private void doDeleteChild(int groupPosition,final int childPosition) {
		Podcast podcast = (Podcast)mAdapter.getGroup(groupPosition);
		final ArrayList<PodcastItem> items  = podcast.getItems();
		PodcastItem moveItem = items.get(childPosition);
		final String title = moveItem.getTitle();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(title + getText(R.string.roidcast_context_menu_delete_do));
		
		builder.setCancelable(true);
		builder.setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				items.remove(childPosition);
				Toast.makeText(getApplicationContext()
						, title + " " + getString(R.string.roidcast_context_menu_delete_done)
						, Toast.LENGTH_LONG).show();
				doDraw(); // 再描画
			}
		});
		builder.setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// nothing to do
				;
			}
		});
		
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
	/**
	 * podcastを削除する
	 * @param groupPosition
	 */
	private void doDeleteGroup(final int groupPosition) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String title = loadData.get(groupPosition).getTitle();
		
		builder.setMessage(title + getText(R.string.roidcast_context_menu_delete_do));
		
		builder.setCancelable(true);
		builder.setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadData.remove(groupPosition);
				Toast.makeText(getApplicationContext()
						, title + " " + getString(R.string.roidcast_context_menu_delete_done)
						, Toast.LENGTH_LONG).show();
				doDraw(); //再描画
			}
		});
		builder.setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// nothing to do
				;
			}
		});
		
		
		AlertDialog dialog = builder.create();
		dialog.show();
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
	}
	
	public void doSave() throws IOException{
    	RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    	r.doSave(loadData);
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
		try {
			doSave();
		} catch (IOException e) {
			new RoidcatUtil().eLog(e);
		}
		doDraw();
	}
}