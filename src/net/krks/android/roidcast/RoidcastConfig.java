package net.krks.android.roidcast;

import java.io.Serializable;
import java.util.Date;

/**
 * アプリケーション全般についての情報を保持するクラス
 * @author kakkyz
 *
 */
public class RoidcastConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Date lastRecrawledDate = null;

	public Date getLastRecrawledDate() {
		return lastRecrawledDate != null ? lastRecrawledDate : new Date(); 
	}

	public void setLastRecrawledDate(Date lastRecrawledDate) {
		this.lastRecrawledDate = lastRecrawledDate;
	}	
}
