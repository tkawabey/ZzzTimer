package four.non.bronds.yyys.zzztimer.util;

//package jp.co.ascade.net;

import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;

/**
 * Library for Wake-on-Lan.
 * 
 * <pre>
 * Use of WakeOnLan lib.
 *     try{
 *         // Set broadcast address and port number.(ex. 192.168.1.255:2304)
 *         WakeOnLan wol = new WakeOnLan("192.168.1.255", 2304);
 *         // Use default configuration. (255.255.255.255:9)
 *         // WakeOnLan wol = new WakeOnLan();
 * 
 *         // Send MagicPacket to specified IEEE address(ex. 00:01:02:03:04:05)
 *        wol.send("00:01:02:03:04:05");
 *     }
 *     catch(java.io.IOException){
 *         // error handling
 *     }
 * </pre>
 */
public class WakeOnLan {
	final static public String DEFAULT_ADDRESS = "255.255.255.255";
	final static public int DEFAULT_PORT = 9;// AMD's Magic Packet Utility 2304
	final static public String MAC_SEPARATOR = "(\\:|\\-|\\.|\\ )";

	// ///////////////////////////////////////////////////////////////////////

	private InetAddress ipAddress;
	private int port;

	public WakeOnLan() {
		try {
			init(InetAddress.getByName(DEFAULT_ADDRESS), DEFAULT_PORT);
		} catch (UnknownHostException e) {
		} // ignore UnknownHostException.
	}

	public WakeOnLan(String ipAddress) throws UnknownHostException {
		init(InetAddress.getByName(ipAddress), port);
	}

	public WakeOnLan(String ipAddress, int port) throws UnknownHostException {
		init(InetAddress.getByName(ipAddress), port);
	}

	public WakeOnLan(InetAddress ipAddress) {
		init(ipAddress, DEFAULT_PORT);
	}

	public WakeOnLan(InetAddress ipAddress, int port) {
		init(ipAddress, port);
	}

	private void init(InetAddress ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public void send(String macAddress) throws IOException,
			InvalidMacAddressException {
		send(parseMac(macAddress, MAC_SEPARATOR));
	}

	public void send(byte[] macAddress) throws IOException,
			InvalidMacAddressException {
		byte[] packet = makePacket(macAddress);

		DatagramPacket dpacket = new DatagramPacket(packet, packet.length,
				this.ipAddress, this.port);

		DatagramSocket socket = new DatagramSocket();
		try {
			socket.send(dpacket);
		} finally {
			socket.close();
		}
	}

	private byte[] makePacket(byte[] macAddress)
			throws InvalidMacAddressException {
		int addrSize = macAddress.length;
		if (6 != addrSize) {
			throw new InvalidMacAddressException("Invalid mac address length("
					+ addrSize + ")");
		}

		byte[] packet = new byte[6 + (addrSize * 16)];
		// a synchronization stream of 6 bytes of FFh.
		Arrays.fill(packet, 0, 6, (byte) 0xff);
		// 16 times the repetition of the Ethernet address
		for (int i = 0; i < 16; i += 1) {
			int start = 6 + (addrSize * i);
			System.arraycopy(macAddress, 0, packet, start, addrSize);
		}
		return packet;
	}

	public byte[] parseMac(String addr, String separator)
			throws InvalidMacAddressException {
		try {
			String[] sbytes = addr.split(separator);
			byte[] bytes = new byte[sbytes.length];
			for (int i = 0; i < bytes.length; i += 1) {
				bytes[i] = (byte) Integer.parseInt(sbytes[i], 16);
			}
			return bytes;
		} catch (NumberFormatException nfe) {
			throw new InvalidMacAddressException("Invalid mac address - "
					+ addr);
		}
	}

	// ///////////////////////////////////////////////////////////////////////

	public class InvalidMacAddressException extends IOException {
		public InvalidMacAddressException(String msg) {
			super(msg);
		}
	}

	// ///////////////////////////////////////////////////////////////////////

	static private void usage() {
		System.out.println("Usage: WakeOnLan [-h] [-v] [-p port]"
				+ " [-i broadcast-ip-address] mac-address ...");
		System.out.println("Ex.) WakeOnLan -i 192.168.1.255"
				+ " 00:01:02:03:04:05 00:0a:0b:0c:0d:0e");
		System.exit(-1);
	}

	static public void main(String[] args) throws Exception {
		boolean isVerbose = false;
		String ipAddress = DEFAULT_ADDRESS;
		int port = DEFAULT_PORT;
		ArrayList<String> macAddressList = new ArrayList<String>();

		// ////////////////////////////////////////////////////

		try {
			for (int i = 0; i < args.length; i += 1) {
				String arg = args[i];
				if ("-h".equals(arg)) {
					usage();
				} else if ("-v".equals(arg)) {
					isVerbose = true;
				} else if ("-p".equals(arg)) {
					i += 1;
					port = Integer.parseInt(args[i]);
				} else if ("-i".equals(arg)) {
					i += 1;
					ipAddress = args[i];
				} else {
					macAddressList.add(arg);
				}
			}
		} catch (Exception e) {
			usage();
		}
		if (0 == macAddressList.size()) {
			usage();
		}

		// ////////////////////////////////////////////////////

		WakeOnLan wol = new WakeOnLan(ipAddress, port);
		for (int i = 0; i < macAddressList.size(); i += 1) {
			String macAddress = macAddressList.get(i);
			wol.send(macAddress);
			if (isVerbose) {
				System.out.println("Sent Wake-on-LAN packet(" + macAddress
						+ ") to " + ipAddress + "(" + port + ").");
			}
		}
	}
}
