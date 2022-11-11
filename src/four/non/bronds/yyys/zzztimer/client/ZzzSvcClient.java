package four.non.bronds.yyys.zzztimer.client;

import java.lang.*;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;



public class ZzzSvcClient {
	
	public enum COMMAND {
		CMD_UKNOWN( 0 ),
		CMD_POWEROFF( 9000 ),
		CMD_SHUTDOWN( 9001 ),
		CMD_LOGOFF( 9002 ),
		CMD_REBOOT( 9003 ),
		CMD_FORCE( 9004 ),
		CMD_STAND_BY( 9005 ),
		CMD_HIBERNATE( 9006 ),
		CMD_LOCK_COMPUTER( 9007 ),
		CMD_SCRENN_OFF( 9008 ),
		CMD_REMOTE_EXEC( 9050 );
		private int value;
		private COMMAND(int n) {
			this.value = n;
		}
		public int getValue() {
			return this.value;
		}
	};
	
	public static COMMAND getCommandIndex(int index) {
		COMMAND command;
    	switch( index ) {
    	case 0:
    		command = ZzzSvcClient.COMMAND.CMD_SHUTDOWN;
    		break;
    	case 1:
    		command = ZzzSvcClient.COMMAND.CMD_REBOOT;
    		break;
    	case 2:
    		command = ZzzSvcClient.COMMAND.CMD_STAND_BY;
    		break;
    	case 3:
    		command = ZzzSvcClient.COMMAND.CMD_HIBERNATE;
    		break;
    	case 4:
    		command = ZzzSvcClient.COMMAND.CMD_LOGOFF;
    		break;
    	case 5:
    		command = ZzzSvcClient.COMMAND.CMD_LOCK_COMPUTER;
    		break;
    	case 6:
    		command = ZzzSvcClient.COMMAND.CMD_SCRENN_OFF;
    		break;
    	case 7:
    		command = ZzzSvcClient.COMMAND.CMD_REMOTE_EXEC;
    		break;
   		default:
   			command = ZzzSvcClient.COMMAND.CMD_UKNOWN;
    		break;
    	}
		
		return command;
	}
	
	public class ZzzNetIfInf {
		public	String	name;
		public	int		ipaddress;
		public	int		subnetmask;
		public	byte[]	physicalAddr;
	}
	
	public class ZzzCompInf {
		public	String				computerName;
		public	List<ZzzNetIfInf>	netinfs;
		
		public	ZzzCompInf()
		{
			netinfs = new ArrayList<ZzzNetIfInf>();
		}
	}
	
	private void innerWriteString(DataOutputStream osStr, String value) throws UnsupportedEncodingException, IOException {
		byte b[] = value.getBytes("UTF-8");
		osStr.writeInt(b.length);
		osStr.write(b, 0, b.length);
	}
	private String innerReadString(DataInputStream inStr) throws UnsupportedEncodingException, IOException {
		String str = "";
		int len = inStr.readInt();
		
		if( len == 0 ) {
			return str;
		}
		byte buf[] = new byte[len];
		inStr.read(buf, 0, len);
		str = new String(buf, "UTF-8");
		
		return str;
	}
	
	/**
	 * @param mode	0:POWEROFF
	 *				1:SHUTDOWN
	 *				2:LOGOFF
	 *				3:REBOOT
	 *				4:FORCE
	 *				5:STAND_BY
	 *				6:HIBERNATE
	 */
	public int shutdown(
			String host, 
			int port, 
			COMMAND command,
			int after_time,
			String strMsg,
			int resume,
			int resume_opt,
			String strExeImage,
			String strExeParams,
			String strExeCurDir)  {
		int ret = 0;
		try{

		
		
			// ソケットを生成
			Socket socket = new Socket(host, port);
			
			
			// 出力ストリームを取得
			DataOutputStream osStr = new DataOutputStream(socket.getOutputStream());
			
			// 入力ストリームを取得
			InputStream is = socket.getInputStream();
			DataInputStream  irStr = new DataInputStream (is);
			
			// シャットダウンコマンド送信
			osStr.writeInt(0);
			osStr.writeInt(command.getValue());
			osStr.writeInt(1); // API Ver
			osStr.writeInt(after_time); // after_time
			innerWriteString(osStr, strMsg);
			osStr.writeInt(resume); // resume
			osStr.writeInt(resume_opt); // resume_opt
			innerWriteString(osStr, strExeImage);
			innerWriteString(osStr, strExeParams);
			innerWriteString(osStr, strExeCurDir);
			osStr.flush();
			
			ret = irStr.readInt();
			osStr.close();
			irStr.close();
			
			// ソケットを閉じる
			socket.close();
		} catch(SocketException e) {
			e.printStackTrace();
			ret = 1;
		} catch(IOException e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}
	
	public ZzzCompInf tellZzzSvcTCP(String host, int port) {
		ZzzCompInf	cmpInf = new ZzzCompInf();
		try{
			// ソケットを生成
			Socket socket = new Socket(host, port);
			// 出力ストリームを取得
			DataOutputStream osStr = new DataOutputStream(socket.getOutputStream());
			
			// 入力ストリームを取得
			InputStream is = socket.getInputStream();
			DataInputStream  irStr = new DataInputStream (is);
			// シャットダウンコマンド送信
			osStr.writeInt(16);
			osStr.writeInt(5000);
			osStr.flush();
			

			int compNameLen = irStr.readInt();
			byte bCompName[] = new byte[compNameLen];
			irStr.read(bCompName);

			cmpInf.computerName = new String(bCompName, 0, bCompName.length);
			int nNumOfIfs = 0;
			nNumOfIfs = irStr.readInt();

			for(int i = 0; i < nNumOfIfs; i++ ) {
				byte bytearry[];

				ZzzNetIfInf netInf = new ZzzNetIfInf();
				int physLen = 0;
				
				// Name
				netInf.name = innerReadString(irStr);
				
				// IP Address
				netInf.ipaddress = irStr.readInt();
				bytearry = toByte( netInf.ipaddress );

				//System.out.println("  IP Address:" + 
				//	getByte2Int(bytearry[3]) + "." + getByte2Int(bytearry[2]) + "." + getByte2Int(bytearry[1]) + "." + getByte2Int(bytearry[0]) );
				
				// MASK
				netInf.subnetmask = irStr.readInt();
				bytearry = toByte( netInf.subnetmask );
				//System.out.println("     MASK Address:" + 
				//	getByte2Int(bytearry[3]) + "." + getByte2Int(bytearry[2]) + "." + getByte2Int(bytearry[1]) + "." + getByte2Int(bytearry[0]) );
				
				physLen = irStr.readInt();
				//System.out.println("     PhysAddr Len:" + physLen  );
				if( physLen > 0 ) {
					netInf.physicalAddr = new byte[physLen];
					irStr.read(netInf.physicalAddr);
					
					//System.out.print("     PhysAddr :");
					//for(int j = 0; j < physLen; j++ ) {
					//	String strTmp = "" + Integer.toHexString( getByte2Int(netInf.physicalAddr[j])  );
					//	if( j != 0 ) {
					//		System.out.print("-");
					//	}
					//	if( strTmp.length() == 1 ) {
					//		System.out.print("0");
					//	}
					//	System.out.print( strTmp );
					//}
					//System.out.println();
				}
				cmpInf.netinfs.add(netInf);
			}
			
			osStr.close();
			irStr.close();
			
			// ソケットを閉じる
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return cmpInf;
	}
	
	public boolean tellZZZSvc(String strBroadcatAddr, int remotePort, int ownPort, List<ZzzCompInf> list) {
		boolean ret = false;
		DatagramSocket receiveSocket = null;
		try{
			// UDPパケットを送信する先となるブロードキャストアドレス
			InetSocketAddress remoteAddress = new InetSocketAddress(strBroadcatAddr, remotePort);
			receiveSocket = new DatagramSocket(ownPort);
			int nNumOfIfs = 0;
			
			receiveSocket.setSoTimeout(1000);
			
			// 送信データ
			//  自バインドしている、ポート番号(4Byte)
			byte[] sendBuffer = toByte(ownPort);
			// 受け付けるデータバッファ
			byte receiveBuffer[] = new byte[1024];
			DatagramPacket receivePacket =
				new DatagramPacket(receiveBuffer, receiveBuffer.length);


			// UDPパケット
			DatagramPacket sendPacket =
				new DatagramPacket(sendBuffer, sendBuffer.length, remoteAddress);

			// DatagramSocketインスタンスを生成して、UDPパケットを送信
			new DatagramSocket().send(sendPacket);


			while( true ) {
				// UDPパケットを受信
				try {
					receiveSocket.receive(receivePacket);
				} catch(SocketTimeoutException  ex ) {
					break;
				}
				
				ZzzCompInf cmpInf = new ZzzCompInf();

				InputStream bais = new ByteArrayInputStream(receivePacket.getData());
				DataInputStream dins = new DataInputStream(bais);
				int compNameLen = dins.readInt();
				byte bCompName[] = new byte[compNameLen];
				dins.read(bCompName);
				//System.out.println("Name Len:" + compNameLen  );
				//System.out.println(new String(bCompName, 0, bCompName.length));
				
				cmpInf.computerName = new String(bCompName, 0, bCompName.length);
				
				nNumOfIfs = dins.readInt();
				for(int i = 0; i < nNumOfIfs; i++ ) {
					byte bytearry[];
					ZzzNetIfInf netInf = new ZzzNetIfInf();
					int physLen = 0;
					// IP Address
					netInf.ipaddress = dins.readInt();
					bytearry = toByte( netInf.ipaddress );
					//System.out.println("  IP Address:" + 
					//	getByte2Int(bytearry[3]) + "." + getByte2Int(bytearry[2]) + "." + getByte2Int(bytearry[1]) + "." + getByte2Int(bytearry[0]) );
					
					// MASK
					netInf.subnetmask = dins.readInt();
					bytearry = toByte( netInf.subnetmask );
					//System.out.println("     MASK Address:" + 
					//	getByte2Int(bytearry[3]) + "." + getByte2Int(bytearry[2]) + "." + getByte2Int(bytearry[1]) + "." + getByte2Int(bytearry[0]) );
					
					physLen = dins.readInt();
					//System.out.println("     PhysAddr Len:" + physLen  );
					if( physLen > 0 ) {
						netInf.physicalAddr = new byte[physLen];
						dins.read(netInf.physicalAddr);
						
						//System.out.print("     PhysAddr :");
						//for(int j = 0; j < physLen; j++ ) {
						//	String strTmp = "" + Integer.toHexString( getByte2Int(netInf.physicalAddr[j])  );
						//	if( j != 0 ) {
						//		System.out.print("-");
						//	}
						//	if( strTmp.length() == 1 ) {
						//		System.out.print("0");
						//	}
						//	System.out.print( strTmp );
						//}
						//System.out.println();
					}
					cmpInf.netinfs.add(netInf);
				}
				list.add(cmpInf);
			}
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		if( receiveSocket != null ) {
			try {
				receiveSocket.close();
			} catch(Exception ee) {
			
			}
		}
		return ret;
	}
	
	static public int getByte2Int(DatagramPacket receivePacket) throws Exception {
		int ret = -1;
		if( receivePacket.getLength() < 4 ) {
			throw new Exception("Data is shortage.[length" + receivePacket.getLength() + "]" );
		}
		InputStream bais = new ByteArrayInputStream(receivePacket.getData());
		DataInputStream dins = new DataInputStream(bais);
		ret = dins.readInt();
		return ret;
	}
	
	static public int getByte2Int(byte b) throws Exception {
		int ret = -1;
		byte temp[] = new byte[4];
		temp[0] = 0;
		temp[1] = 0;
		temp[2] = 0;
		temp[3] = b;
		InputStream bais = new ByteArrayInputStream(temp);
		DataInputStream dins = new DataInputStream(bais);
		ret = dins.readInt();
		return ret;		
	}
	
	static public byte[] toByte(int value)  throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(value);
		dos.flush();
		return baos.toByteArray();
	}
	
	
	
	
	public static void main(String[] args)  throws Exception {
		if( args.length < 1 ) {
			System.out.println("Invalid command line.");
			return ;
		}
		if( args[0].equals("t") ) {
			// Tell Zzz
			int port_no = 4949;
			String strBroadcatAddr = "127.0.0.1";

			ZzzSvcClient c = new ZzzSvcClient();
			c.tellZzzSvcTCP(strBroadcatAddr, port_no);
			
		} else
		if( args[0].equals("s") ) {
			// Remote Hibernation
			//  option arg
			//    command
			//    hostname
			//    [port]
			int port_no = 4949;
			COMMAND command = COMMAND.CMD_POWEROFF;
			String strHostName;
			
			if( args.length < 3 ) {
				System.out.println("");
				return ;
			}
			if( args[1].equals("0") ) {	command = COMMAND.CMD_POWEROFF;		} else
			if( args[1].equals("1") ) {	command = COMMAND.CMD_SHUTDOWN;		} else
			if( args[1].equals("2") ) {	command = COMMAND.CMD_LOGOFF;		} else
			if( args[1].equals("3") ) {	command = COMMAND.CMD_REBOOT;		} else
			if( args[1].equals("4") ) {	command = COMMAND.CMD_FORCE;		} else
			if( args[1].equals("5") ) {	command = COMMAND.CMD_STAND_BY;		} else
			if( args[1].equals("6") ) {	command = COMMAND.CMD_HIBERNATE;		} else
			{
				System.out.println("invalid hivernate command." + args[1] );
				return ;
			}
			strHostName = args[2];
			if( args.length >= 4 ) {
				port_no = Integer.parseInt(args[3]);
			}
			
			
			ZzzSvcClient c = new ZzzSvcClient();
			c.shutdown(strHostName, port_no, command, 1, "メッセージ",
					0, 0, "notepad.exe", "", "");
		} else
		if( args[0].equals("t") ) {
			// Tell ZzzSVC
			//  option arg
			//    [broad cat address]
			//    [remote port]
			//    [own port]
			String strBroadcatAddr = "255.255.255.255";
			int remote_port = 4650;
			int own_port = 5100;
			List<ZzzCompInf> list = new ArrayList<ZzzCompInf>();
			
			if( args.length >= 2 ) {
				strBroadcatAddr = args[1];
			}
			if( args.length >= 3 ) {
				remote_port = Integer.parseInt(args[2]);
			}
			if( args.length >= 4 ) {
				own_port = Integer.parseInt(args[3]);
			}
			
			ZzzSvcClient c = new ZzzSvcClient();
			c.tellZZZSvc(strBroadcatAddr, remote_port, own_port, list);
			
			for(ZzzCompInf inf : list ) {
				System.out.println("Computer : " + inf.computerName);
				for(ZzzNetIfInf netInf : inf.netinfs ) {
					byte bytearry[];
					
					// IP Address
					bytearry = c.toByte( netInf.ipaddress );
					System.out.println("  IP Address:" + netInf.ipaddress + "   " + 
						c.getByte2Int(bytearry[3]) + "." + c.getByte2Int(bytearry[2]) + "." + c.getByte2Int(bytearry[1]) + "." + c.getByte2Int(bytearry[0]) );
					
					// IP Address
					bytearry = c.toByte( netInf.subnetmask );
					System.out.println("     Subnet Mask:" + 
						c.getByte2Int(bytearry[3]) + "." + c.getByte2Int(bytearry[2]) + "." + c.getByte2Int(bytearry[1]) + "." + c.getByte2Int(bytearry[0]) );
					
					if( netInf.physicalAddr != null ) {
						System.out.print("     PhysAddr :");
						for(int j = 0; j < netInf.physicalAddr.length; j++ ) {
							String strTmp = "" + Integer.toHexString( c.getByte2Int(netInf.physicalAddr[j])  );
							if( j != 0 ) {
								System.out.print("-");
							}
							if( strTmp.length() == 1 ) {
								System.out.print("0");
							}
							System.out.print( strTmp );
						}
						System.out.println();
					}
				}
			}
		} else {
			System.out.println("unkown command. " + args[0]  );
		}
	}
}

