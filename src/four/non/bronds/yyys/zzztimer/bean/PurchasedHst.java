package four.non.bronds.yyys.zzztimer.bean;

import java.io.Serializable;
import java.util.Date;

public class PurchasedHst  implements Serializable {

	/** Serial ID */
	private static final long serialVersionUID = -5149480867095231669L;
	
	
	private String		mOrderID;
	private int			mState;		
	private String		mPrID;		// 商品ID
	private String		mDeveloperPayload;	// ペイロード
	private Date		mPurchaseTime;
	
	
	public String getOrderID() {
		return mOrderID;
	}
	public void setOrderID(String mOrderID) {
		this.mOrderID = mOrderID;
	}
	
	
	public int getState() {
		return mState;
	}
	public void setState(int mState) {
		this.mState = mState;
	}
	
	
	public String getPrID() {
		return mPrID;
	}
	public void setPrID(String mPrID) {
		this.mPrID = mPrID;
	}
	
	
	public String getDeveloperPayload() {
		return mDeveloperPayload;
	}
	public void setDeveloperPayload(String mDeveloperPayload) {
		this.mDeveloperPayload = mDeveloperPayload;
	}
	
	
	public Date getPurchaseTime() {
		return mPurchaseTime;
	}
	public void setPurchaseTime(Date mPurchaseTime) {
		this.mPurchaseTime = mPurchaseTime;
	}
}
