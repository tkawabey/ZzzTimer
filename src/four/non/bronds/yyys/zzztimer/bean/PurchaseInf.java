package four.non.bronds.yyys.zzztimer.bean;

import java.io.Serializable;

public class PurchaseInf implements Serializable {
	/** Serial ID */
	private static final long serialVersionUID = -6879963531769954943L;
	
	private boolean			mOgg = false;

	public boolean isOgg() {
		return mOgg;
	}
	public void setOgg(boolean mOgg) {
		this.mOgg = mOgg;
	}

}
