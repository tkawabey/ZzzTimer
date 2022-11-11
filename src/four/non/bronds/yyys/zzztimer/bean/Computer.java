package four.non.bronds.yyys.zzztimer.bean;

import java.io.Serializable;

public class Computer implements Serializable {

	/** Serial ID */
	private static final long serialVersionUID = 8676472684728107722L;
	private int	id = -1;
	private String mac_addr = "";
	private String hostname = "";
	private String name = "";
	private int	broadcast_addr;
	private int	OS = 0;
	private String OSver = "";
	private String username = "";
	private String passwd = "";
	private String exec_image = "";
	private String exec_params = "";
	private String exec_cur_dir = "";
	private int	zzz_tcp_port = 0;
	private int	port_wol = 0;
	private int	resume = 0;
	private int	resume_opt = 0;
	private String	shutdown_msg = "";
	private int	shutdown_after_tm = 0;

	
	
	@Override
	public String toString() {
		return name;
	}
	
	
	public Computer() {
		
	}
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getMac_addr() {
		return mac_addr;
	}



	public void setMac_addr(String mac_addr) {
		this.mac_addr = mac_addr;
	}



	public String getHostname() {
		return hostname;
	}



	public void setHostname(String hostname) {
		this.hostname = hostname;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getBroadcast_addr() {
		return broadcast_addr;
	}



	public void setBroadcast_addr(int broadcast_addr) {
		this.broadcast_addr = broadcast_addr;
	}



	public int getOS() {
		return OS;
	}



	public void setOS(int oS) {
		OS = oS;
	}



	public String getOSver() {
		return OSver;
	}



	public void setOSver(String oSver) {
		OSver = oSver;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPasswd() {
		return passwd;
	}



	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}



	public String getExec_image() {
		return exec_image;
	}



	public void setExec_image(String exec_image) {
		this.exec_image = exec_image;
	}



	public String getExec_params() {
		return exec_params;
	}



	public void setExec_params(String exec_params) {
		this.exec_params = exec_params;
	}



	public String getExec_cur_dir() {
		return exec_cur_dir;
	}



	public void setExec_cur_dir(String exec_cur_dir) {
		this.exec_cur_dir = exec_cur_dir;
	}



	public int getZzz_tcp_port() {
		return zzz_tcp_port;
	}



	public void setZzz_tcp_port(int zzz_tcp_port) {
		this.zzz_tcp_port = zzz_tcp_port;
	}



	public int getPort_wol() {
		return port_wol;
	}



	public void setPort_wol(int port_wol) {
		this.port_wol = port_wol;
	}


	public int getResume() {
		return resume;
	}


	public void setResume(int resume) {
		this.resume = resume;
	}


	public int getResume_opt() {
		return resume_opt;
	}


	public void setResume_opt(int resume_opt) {
		this.resume_opt = resume_opt;
	}


	public String getShutdown_msg() {
		return shutdown_msg;
	}


	public void setShutdown_msg(String shutdown_msg) {
		this.shutdown_msg = shutdown_msg;
	}


	public int getShutdown_after_tm() {
		return shutdown_after_tm;
	}


	public void setShutdown_after_tm(int shutdown_after_tm) {
		this.shutdown_after_tm = shutdown_after_tm;
	}


}
