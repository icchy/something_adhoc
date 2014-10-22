package something_adhoc;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

public class JNAtest2 {
	public interface JNAWiFiLibrary extends Library {
		public static final String JNA_LIBRARY_NAME = "JNAWiFi";
		public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(JNAWiFiLibrary.JNA_LIBRARY_NAME);
		public static final JNAWiFiLibrary INSTANCE = (JNAWiFiLibrary)Native.loadLibrary(JNAWiFiLibrary.JNA_LIBRARY_NAME, JNAWiFiLibrary.class);
		public static final int DOT11_SSID_MAX_LENGTH = 32;
		public static interface WLAN_INTERFACE_STATE {
			public static final int wlan_interface_state_not_ready = 0;
			public static final int wlan_interface_state_connected = 1;
			public static final int wlan_interface_state_ad_hoc_network_formed = 2;
			public static final int wlan_interface_state_disconnecting = 3;
			public static final int wlan_interface_state_disconnected = 4;
			public static final int wlan_interface_state_associating = 5;
			public static final int wlan_interface_state_discovering = 6;
			public static final int wlan_interface_state_authenticating = 7;
		};
		public static class WLAN_INTERFACE_INFO_LIST extends Structure {
			public int dwNumberOfItems;
			public int dwIndex;
			public Pointer InterfaceInfo;
			public WLAN_INTERFACE_INFO_LIST() {
				super();
			}
			protected List<? > getFieldOrder() {
				return Arrays.asList("dwNumberOfItems", "dwIndex", "InterfaceInfo");
			}
			public WLAN_INTERFACE_INFO_LIST(int dwNumberOfItems, int dwIndex, Pointer InterfaceInfo) {
				super();
				this.dwNumberOfItems = dwNumberOfItems;
				this.dwIndex = dwIndex;
				this.InterfaceInfo = InterfaceInfo;
			}
			public WLAN_INTERFACE_INFO_LIST(Pointer peer) {
				super(peer);
			}
			public static class ByReference extends WLAN_INTERFACE_INFO_LIST implements Structure.ByReference {

			};
			public static class ByValue extends WLAN_INTERFACE_INFO_LIST implements Structure.ByValue {

			};
		};
		public static class WLAN_INTERFACE_INFO extends Structure {
			public JNAWiFiLibrary.GUID InterfaceGuid;
			public short[] strInterfaceDescription = new short[256];
			/** @see WLAN_INTERFACE_STATE */
			public int isState;
			public WLAN_INTERFACE_INFO() {
				super();
			}
			protected List<? > getFieldOrder() {
				return Arrays.asList("InterfaceGuid", "strInterfaceDescription", "isState");
			}
			public WLAN_INTERFACE_INFO(JNAWiFiLibrary.GUID InterfaceGuid, short strInterfaceDescription[], int isState) {
				super();
				this.InterfaceGuid = InterfaceGuid;
				if ((strInterfaceDescription.length != this.strInterfaceDescription.length))
					throw new IllegalArgumentException("Wrong array size !");
				this.strInterfaceDescription = strInterfaceDescription;
				this.isState = isState;
			}
			public WLAN_INTERFACE_INFO(Pointer peer) {
				super(peer);
			}
			public static class ByReference extends WLAN_INTERFACE_INFO implements Structure.ByReference {

			};
			public static class ByValue extends WLAN_INTERFACE_INFO implements Structure.ByValue {

			};
		};
		public static class DOT11_SSID extends Structure {
			public NativeLong uSSIDLength;
			public JNAWiFiLibrary.UCHAR[] ucSSID = new JNAWiFiLibrary.UCHAR[DOT11_SSID_MAX_LENGTH];
			public DOT11_SSID() {
				super();
			}
			protected List<? > getFieldOrder() {
				return Arrays.asList("uSSIDLength", "ucSSID");
			}
			public DOT11_SSID(NativeLong uSSIDLength, JNAWiFiLibrary.UCHAR ucSSID[]) {
				super();
				this.uSSIDLength = uSSIDLength;
				if ((ucSSID.length != this.ucSSID.length))
					throw new IllegalArgumentException("Wrong array size !");
				this.ucSSID = ucSSID;
			}
			public DOT11_SSID(Pointer peer) {
				super(peer);
			}
			public static class ByReference extends DOT11_SSID implements Structure.ByReference {

			};
			public static class ByValue extends DOT11_SSID implements Structure.ByValue {

			};
		};
		public static class WLAN_RAW_DATA extends Structure {
			public int dwDataSize;
			public byte[] DataBlob = new byte[1];
			public WLAN_RAW_DATA() {
				super();
			}
			protected List<? > getFieldOrder() {
				return Arrays.asList("dwDataSize", "DataBlob");
			}
			public WLAN_RAW_DATA(int dwDataSize, byte DataBlob[]) {
				super();
				this.dwDataSize = dwDataSize;
				if ((DataBlob.length != this.DataBlob.length))
					throw new IllegalArgumentException("Wrong array size !");
				this.DataBlob = DataBlob;
			}
			public WLAN_RAW_DATA(Pointer peer) {
				super(peer);
			}
			public static class ByReference extends WLAN_RAW_DATA implements Structure.ByReference {

			};
			public static class ByValue extends WLAN_RAW_DATA implements Structure.ByValue {

			};
		};
		/** Pointer to unknown (opaque) type */
		public static class UCHAR extends PointerType {
			public UCHAR(Pointer address) {
				super(address);
			}
			public UCHAR() {
				super();
			}
		};
		/** Pointer to unknown (opaque) type */
		public static class GUID extends PointerType {
			public GUID(Pointer address) {
				super(address);
			}
			public GUID() {
				super();
			}
		};
	}



	public interface WlanAPI extends Library {
		WlanAPI INSTANCE = (WlanAPI)Native.loadLibrary("Wlanapi", WlanAPI.class);
		int WlanOpenHandle(int dwClientVersion, Pointer pReserved, Pointer pdwNegotiatedVersion, Pointer phClientHandle);
		// dwClientVersion: 1 => Client version for Windows XP with SP3 and Wireless LAN API for Windows XP with SP2.
		//					2 => Client version for Windows Vista and Windows Server 2008
		// pReserved: NULL

		int WlanEnumInterfaces(int hClientHandle, Pointer pReserved, Pointer ppInterfaceList);
		// pReserved: null
		//

		int WlanScan(int hClientHandle, Pointer pInterfaceGuid, Pointer pDot11Ssid, Pointer pIeData, Pointer pReserved);
		// pReserved: null
	}

	public static void main (String[] argv) {
		Pointer pdwNV = null, phCH = null;
		int hClientHandle = WlanAPI.INSTANCE.WlanOpenHandle(2, null, pdwNV, phCH);
		Pointer pReserved = null;
		Pointer ppInterfaceList = null;
		int InterfaceGuid = WlanAPI.INSTANCE.WlanEnumInterfaces(hClientHandle, pReserved, ppInterfaceList);
//		DOT11_SSID Dot11Ssid = new DOT11_SSID();
		Pointer pDot11Ssid = null, pIeData = null;
		Pointer pInterfaceGuid = new Pointer(InterfaceGuid);
		int res = WlanAPI.INSTANCE.WlanScan(hClientHandle, pInterfaceGuid, pDot11Ssid, pIeData, pReserved);
		System.out.println(hClientHandle);
		System.out.println(pDot11Ssid.getPointer(0));
		System.out.println(res);
	}


}
