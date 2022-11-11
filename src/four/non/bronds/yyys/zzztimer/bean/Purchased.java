package four.non.bronds.yyys.zzztimer.bean;

import java.io.Serializable;

public class Purchased implements Serializable {
	/** Serial ID */
	private static final long serialVersionUID = -5163181523984149910L;
	
	
	private String		mPrID;		// 商品ID
	private int			mQuantity;	// 数量
	
	
	public String getPrID() {
		return mPrID;
	}
	public void setPrID(String mPrID) {
		this.mPrID = mPrID;
	}
	
	public int getQuantity() {
		return mQuantity;
	}
	public void setQuantity(int mQuantity) {
		this.mQuantity = mQuantity;
	}
}
