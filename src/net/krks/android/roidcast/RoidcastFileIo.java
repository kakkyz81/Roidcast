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

/**
 * データをファイルに書き出す処理
 * @author kakkyz
 */
public class RoidcastFileIo extends ContextWrapper {
	private static final String FILE_ID = "podcast";

	public RoidcastFileIo(Context base) {
		super(base);
	}

	public void doSave(ArrayList<Podcast> a) throws IOException {
		OutputStream o = openFileOutput(FILE_ID,MODE_PRIVATE);
		ObjectOutputStream out = new ObjectOutputStream(o);
		out.writeObject(a);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Podcast> doLoad() {
		ArrayList<Podcast> array = new ArrayList<Podcast>();
		ObjectInputStream in = null;
    	try {
			in = new ObjectInputStream(openFileInput(FILE_ID));
			ArrayList<Podcast> readObject = (ArrayList<Podcast>)in.readObject();
			array = readObject;
		} catch (FileNotFoundException e) {
			// 初回の場合、空のArrayListを返す
			new RoidcatUtil().eLog(e);
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
