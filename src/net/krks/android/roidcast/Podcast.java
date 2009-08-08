/**
 * 
 */
package net.krks.android.roidcast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Podcast情報を持つためのクラス
 * @author kakkyz
 * 
 */
public class Podcast {
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
	protected ArrayList<String> categories = null;
	protected ArrayList<PodcastItem> items = null;
	protected Date lastBuildDate = null;

	/**
	 * podcastのitem情報を持つクラス
	 * 
	 * @author kakkyz
	 */
	public class PodcastItem {

		protected String title = null;
		protected String link = null;
		protected String audioUri = null;

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

}
