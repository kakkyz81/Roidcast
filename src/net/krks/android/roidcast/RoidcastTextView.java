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

import android.content.Context;
import android.widget.TextView;

public class RoidcastTextView extends TextView {
	protected String audioUri = null;
	protected String mediaType = null;
	
	public RoidcastTextView(Context context) {
		super(context);
	}
	
	public void setAudioUri(String audioUri) {
		this.audioUri = audioUri;
	}

	public String getAudioUri() {
		return audioUri;
	}

	public String getMediaType() {
		return mediaType != null ? mediaType : "";
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

		
}
