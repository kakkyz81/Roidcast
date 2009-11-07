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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Podcast情報を持つためのクラス
 * @author kakkyz
 * 
 */
public class Podcast implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() {
		return title + "\n" ;
		
	};
	
	public Podcast() {
		super();
		this.categories = new ArrayList<String>();
		this.items = new ArrayList<PodcastItem>();
	}

	protected String title = null;
	protected String link = null;
	protected String description = null;
	protected String language = null;
	protected String subtitle = null;
	protected String imageUri = null;
	protected String author = null;
	protected String summary = null;
	protected String xmlUrl = null;
	
	protected ArrayList<String> categories = null;
	protected ArrayList<PodcastItem> items = null;
	protected Date lastBuildDate = null;

	public boolean isEmpty() {
		return (0 == items.size());
	}
	
	/**
	 * 再度クローリングをしてitem情報を最新のものを取得する
	 */
	public void reCrawl() {
		XMLParse xmlParse = new XMLParse();
		Podcast newPodcast = xmlParse.parsePodcastXML(xmlUrl);
		if(newPodcast != null) {
			refresh(newPodcast);
		}
	}
	
	/**
	 * 新しいデータを受け取って、自分のデータを更新する
	 * podcastItemは、再生した時間を保持しているので、単純に入れ替えできない。
	 * @param newPodcast
	 */
	protected void refresh(Podcast newPodcast) {
		ArrayList<PodcastItem> tmpItems = new ArrayList<PodcastItem>();
		
		/* まず新しいオブジェクトのリストを作り、それと同じ古いオブジェクトがあれば、
		 * 古いオブジェクトを優先して採用する
		 */
		for(PodcastItem newItem:newPodcast.getItems()) {
			tmpItems.add(newItem);
			for(PodcastItem item:items) {
				if(newItem.getAudioUri().equals(item.getAudioUri())) {
					tmpItems.remove(newItem);
					tmpItems.add(item);
				}
			}
		}
		items = tmpItems;
	
		// title         = newPodcast.getTitle(); // titleは人が変更できるので，初回のものを常に採用する
		link          = newPodcast.getLink();
		description   = newPodcast.getDescription();
		language      = newPodcast.getLanguage();
		subtitle      = newPodcast.getSubtitle();
		imageUri      = newPodcast.getImageUri();
		author        = newPodcast.getAuthor();
		summary       = newPodcast.getSummary();
		categories    = newPodcast.getCategories();
		lastBuildDate = newPodcast.getLastBuildDate();
		//xmlUrl      = newPodcast.get//xmlUrl     ();
	}
	
	/**
	 * itemの最新のpubDateを返す
	 * @return localeString or ""
	 */
	public String getLatestItemDate() {
		Date latestDate = null;
		
		for(PodcastItem item:items) {
			Date itemDate = item.getPubDate();
			if(itemDate == null) { continue; }
			
			if(latestDate == null) {
				latestDate = itemDate;
			}else if(latestDate.compareTo(itemDate) < 0) {
				latestDate = itemDate;
			}
		}
		
		return latestDate != null ? latestDate.toLocaleString() : "";
	}
	
	/**
	 * podcastのitem情報を持つクラス
	 * 
	 * @author kakkyz
	 */
	public class PodcastItem implements Serializable{
		private static final long serialVersionUID = 6908868932993712873L;
		
		protected String title = null;
		protected String link = null;
		protected String audioUri = null;
		protected String mediaType = null;
		protected Date lastPlayedDate = null;
		protected Date pubDate = null;
		
		// 容量が大きくなるし、見せるつもりもないのでコメント情報は持たない
		// protected String description = null;
		// protected String content = null;
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getAudioUri() {
			return audioUri;
		}

		public void setAudioUri(String audioUri) {
			this.audioUri = audioUri;
		}

		public Date getLastPlayedDate() {
			return lastPlayedDate;
		}

		public void setLastPlayedDate(Date lastPlayedDate) {
			this.lastPlayedDate = lastPlayedDate;
		}
		
		@Override
		public String toString() {
			return 	title;
		}

		public String getMediaType() {
			return mediaType;
		}

		public void setMediaType(String mediaType) {
			this.mediaType = mediaType;
		}
		
		public Date getPubDate() {
			return pubDate;
		}

		public void setPubDate(Date pubDate) {
			this.pubDate = pubDate;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}

	public ArrayList<PodcastItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<PodcastItem> items) {
		this.items = items;
	}

	public Date getLastBuildDate() {
		return lastBuildDate;
	}

	public void setLastBuildDate(Date lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}
	
	public String getXmlUrl() {
		return xmlUrl;
	}

	public void setXmlUrl(String xmlUrl) {
		this.xmlUrl = xmlUrl;
	}

}
