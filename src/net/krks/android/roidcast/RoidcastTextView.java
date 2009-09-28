package net.krks.android.roidcast;

import android.content.Context;
import android.widget.TextView;

public class RoidcastTextView extends TextView {
	protected String AudioUri = null;
	protected String MediaType = null;
	
	public RoidcastTextView(Context context) {
		super(context);
	}
	
	public void setAudioUri(String audioUri) {
		AudioUri = audioUri;
	}

	public String getAudioUri() {
		return AudioUri;
	}

	public String getMediaType() {
		return MediaType != null ? MediaType : "";
	}

	public void setMediaType(String mediaType) {
		MediaType = mediaType;
	}

		
}
