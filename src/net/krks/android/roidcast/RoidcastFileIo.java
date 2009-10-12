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

/**
 * 
 */
package net.krks.android.roidcast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

/**
 * データをファイルに書き出す処理
 * @author kakkyz
 */
public class RoidcastFileIo extends ContextWrapper {
	private static final String FILE_ID_PODCAST = "podcast";
	private static final String FILE_ID_CONFIG = "roidcastconfig";

	public RoidcastFileIo(Context base) {
		super(base);
	}
	
	public void doConfigSave(RoidcastConfig r) {
		Log.i(Roidcast.TAG,"doConfigSave");
		OutputStream o = null;
		try {
			o = openFileOutput(FILE_ID_CONFIG,MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(o);
			out.writeObject(r);
			o.flush();
		} catch (FileNotFoundException e) {
			new RoidcatUtil().iLog(e);
		} catch (IOException e) {
			new RoidcatUtil().eLog(e);
		} finally {
			try {
				if( o != null) {o.close();}
			} catch (IOException e) {
				new RoidcatUtil().eLog(e);
			}
		}
	}
	
	public RoidcastConfig doConfigLoad() {
		RoidcastConfig r = new RoidcastConfig();
		ObjectInputStream in = null;
    	try {
			in = new ObjectInputStream(openFileInput(FILE_ID_CONFIG));
			RoidcastConfig readObject = (RoidcastConfig)in.readObject();
			if(null != readObject) {
				r = readObject;
			}
		} catch (FileNotFoundException e) {
			// 初回の場合、処理を続行
			new RoidcatUtil().iLog(e);
		} catch (IOException e) {
			new RoidcatUtil().eLog(e);
		} catch (ClassNotFoundException e) {
			new RoidcatUtil().eLog(e);
		} finally {
			try {
				if(in != null) { in.close(); }
			} catch (IOException e) {
				new RoidcatUtil().eLog(e);
			}
			
		}
		return r;
	}
	
	
	public void doSave(ArrayList<Podcast> a) throws IOException {
		OutputStream o = openFileOutput(FILE_ID_PODCAST,MODE_PRIVATE);
		ObjectOutputStream out = new ObjectOutputStream(o);
		out.writeObject(a);
		o.flush();
		o.close();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Podcast> doLoad() {
		ArrayList<Podcast> array = new ArrayList<Podcast>();
		ObjectInputStream in = null;
    	try {
			in = new ObjectInputStream(openFileInput(FILE_ID_PODCAST));
			ArrayList<Podcast> readObject = (ArrayList<Podcast>)in.readObject();
			if(null != readObject) {
				array = readObject;
			}
		} catch (FileNotFoundException e) {
			// 初回の場合、空のArrayListを返す
			new RoidcatUtil().iLog(e);
		} catch (IOException e) {
			new RoidcatUtil().eLog(e);
		} catch (ClassNotFoundException e) {
			new RoidcatUtil().eLog(e);
		} finally {
			try {
				if(in != null) { in.close(); }
			} catch (IOException e) {
				new RoidcatUtil().eLog(e);
			}
			
		}
		
		// 空の要素を除いたものを戻す
		ArrayList<Podcast> returnArray = new ArrayList<Podcast>();
		for(Podcast p:array) {
			if(!p.isEmpty()) {
				returnArray.add(p);
			}
		}
				
		return returnArray;
	}
	
}
