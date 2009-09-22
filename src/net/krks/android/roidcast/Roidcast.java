package net.krks.android.roidcast;

import java.io.IOException;
import java.util.ArrayList;

import net.krks.android.roidcast.Podcast.PodcastItem;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

@SuppressWarnings("unused")
public class Roidcast extends ExpandableListActivity {
	public static final String TAG = "Roidcast";
	ExpandableListAdapter mAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG,"onCreate @@@ roidast");
        super.onCreate(savedInstanceState);
        
        ArrayList<Podcast> loadData = doLoad();        
        
        RoidcastEVLAdapter roidcastEVLAdapter = new RoidcastEVLAdapter();
        roidcastEVLAdapter.setPodcastList(loadData);
        
        // ExpandListViewを登録する時のお決まりの文法
        mAdapter = roidcastEVLAdapter;
        setListAdapter(mAdapter);
        registerForContextMenu(getExpandableListView());
        
        //setContentView(R.layout.main);
    }
	
    public void doSave(ArrayList<Podcast> a) throws IOException{
    	RoidcastFileIo r = new RoidcastFileIo(getApplicationContext());
    	r.doSave(a);
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
		 * クリックされた時の動作もここで記述する 
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
			/* クリックされた時の動作を記述 */
            textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					RoidcastTextView tv = (RoidcastTextView) v;
					// デフォルトのオーディオプレーヤを起動
					Intent i = new Intent();
					i.setAction(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.parse(tv.getAudioUri()), "audio/*");
					
					startActivity(i);
					
					/* Musicの再生を手動で実装するならこっちだけど・・・
					 * 大変なので今回はデフォルトに任せた 
					MediaPlayer mp = new MediaPlayer();
					 
					try {
						mp.setDataSource(tv.getAudioUri());
						mp.prepare();
						mp.start();
					} catch (IllegalArgumentException e) {
						new RoidcatUtil().eLog(e);
					} catch (IllegalStateException e) {
						new RoidcatUtil().eLog(e);
					} catch (IOException e) {
						new RoidcatUtil().eLog(e);
					}
					*/
				}
			});
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
            textView.setText(p.getTitle());
            return textView;
		}
		
		@Override
		public boolean hasStableIds() {
			// TODO
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
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);

            t.setLayoutParams(lp);
            // Center the text vertically
            t.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            t.setPadding(36, 0, 0, 0);
        }

	}
}