package net.krks.android.roidcast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import net.krks.android.roidcast.Podcast.PodcastItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

/**
 * @author kakkyz
 *
 */
public class XMLParse {
	/** 個別mp3データの件数 */
	private static final int MAXITEMS = 20;

	/**
	 * uriを開いて解析結果をpodcastオブジェクトにして返す
	 * 
	 * @param uri
	 * @return Podcast 
	 */
	public Podcast parsePodcastXML(String uri){
		Log.i(Roidcast.TAG, "parsePodcastXML:" + uri);
		XMLParseDataHttpGet xmlParceDataHttpGet = new XMLParseDataHttpGet();
		InputStream in = xmlParceDataHttpGet.getImputStreamOnWeb(uri);
		Podcast p = new Podcast();
		
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in,null);
			
			int eventType = parser.getEventType();
			boolean done = false;
			
			int mainDepth = 0; /* xmlの、全体のタイトルなどを持っているdepth */
			int depth = 0;/* 今のdepth */ 
			
			// ファイル終了か、読み込み対象を読み込んだ段階で抜ける
			while(eventType != XmlPullParser.END_DOCUMENT && !done) {
				String name = null;
				
				//String text = parser.getText();
				
				//String type = null;
				name = parser.getName();
				
				switch(eventType) {
				case XmlPullParser.START_DOCUMENT:
					//type = "START_DOCUMENT"; //for debug
					break;
				case XmlPullParser.START_TAG:
					//type = "START_TAG"; //for debug
					if(name.equalsIgnoreCase("channel") && mainDepth == 0) {
						//channel+1がメイン部分,+2は個別アイテム 
						mainDepth = depth + 1;
					}
					// メイン部分のセット
					if(name.equalsIgnoreCase("title") && depth == mainDepth) { p.setTitle(parser.nextText());}
					if(name.equalsIgnoreCase("link") && depth == mainDepth) { p.setLink(parser.nextText());}
					if(name.equalsIgnoreCase("description") && depth == mainDepth) { p.setDescription(parser.nextText());}
					if(name.equalsIgnoreCase("language") && depth == mainDepth) { p.setLanguage(parser.nextText());}
					if(name.equalsIgnoreCase("subtitle") && depth == mainDepth) { p.setSubtitle(parser.nextText());}
					if(name.equalsIgnoreCase("imageUri") && depth == mainDepth) { p.setImageUri(parser.nextText());}
					if(name.equalsIgnoreCase("author") && depth == mainDepth) { p.setAuthor(parser.nextText());}
					if(name.equalsIgnoreCase("summary") && depth == mainDepth) { p.setSummary(parser.nextText());}
					if(name.equalsIgnoreCase("lastBuildDate") && depth == mainDepth) { 
						try {
							p.setLastBuildDate(new Date(parser.nextText()));
						} catch (Exception e) {
							p.setLastBuildDate(null);
						}
					}
					if(name.equalsIgnoreCase("item")) {
						int itemDepth;
						// itemの解析
						do { // itemの中に入るまで進める
							eventType = parser.next();
							name = parser.getName();
							 itemDepth = parser.getDepth();
						} while (eventType != XmlPullParser.START_TAG && eventType != XmlPullParser.END_TAG);
						
						PodcastItem item = p.new PodcastItem();
						while(depth <= itemDepth){
							switch(eventType) {
							case XmlPullParser.START_TAG:
								if(name.equalsIgnoreCase("title")) {item.setTitle(parser.nextText());}
								if(name.equalsIgnoreCase("link")) {item.setLink(parser.nextText());}
								if(name.equalsIgnoreCase("enclosure")) {item.setAudioUri(parser.getAttributeValue(null,"url"));}
								break;
							case  XmlPullParser.END_TAG:
								break;
							}
							eventType = parser.next();
							name = parser.getName();
							itemDepth = parser.getDepth();
						}
						p.items.add(item);
						// 制限数を越えたときは処理を打ちきる
						if(p.items.size() >= MAXITEMS) { 
							done = true;
						}
					}
					break;
				case XmlPullParser.END_TAG:
					//type = "END_TAG"; //for debug
					break;
				case XmlPullParser.TEXT:
					//type = "TEXT"; //for debug
					break;
				}
				
				//Log.i(Roidcast.TAG,new Integer(eventType).toString() + type + ":" + name + ":" + text);
				eventType = parser.next();
				depth = parser.getDepth();
			}
			
			if(p.getItems().size() > MAXITEMS) { done = true; }
			
		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			new RoidcatUtil().eLog(e);
		} catch (IOException e) {
			//e.printStackTrace();
			new RoidcatUtil().eLog(e);
		}
		
		return p;
	}
	
	/**
	 * コンストラクタ
	 */
	public XMLParse() {
		Log.i(Roidcast.TAG, "XMLParse:");
		
	}
}
