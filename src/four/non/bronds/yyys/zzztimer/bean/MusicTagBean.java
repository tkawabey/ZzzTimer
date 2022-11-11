package four.non.bronds.yyys.zzztimer.bean;

public class MusicTagBean {
	public MusicTagBean(String name, String value) {
		strName = name;
		strValue = value;
	}
	
	
	String strName;
	String strValue;
		
	public String getName() {
		return strName;
	}
	public void setName(String strName) {
		this.strName = strName;
	}
	public String getValue() {
		return strValue;
	}
	public void setValue(String strValue) {
		this.strValue = strValue;
	}
}
