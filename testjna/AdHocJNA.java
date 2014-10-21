package testjna;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class AdHocJNA {
	/*
	typedef struct _GUID {
	  DWORD Data1;
	  WORD  Data2;
	  WORD  Data3;
	  BYTE  Data4[8];
	} GUID;

	 */

	public interface User32DLL extends Library{
		User32DLL INSTANCE = (User32DLL) Native.loadLibrary("user32.dll", User32DLL.class);
		/*

		int WINAPI MessageBox(
		  _In_opt_  HWND hWnd,
		  _In_opt_  LPCTSTR lpText,
		  _In_opt_  LPCTSTR lpCaption,
		  _In_      UINT uType
		);

		 */
		int MessageBoxA( int hWnd, String lpText, String lpCaption, int uType);
		/*
		 HRESULT GetIEnumDot11AdHocNetworks(
		  [in]   GUID *pContextGuid,
		  [out]  IEnumDot11AdHocNetworks **ppEnum
		);
		*/
		String GetIEnumDot11AdHocNetworks(int pContextGuid, int ppEnum);
	}
	public static void main(String[] args){
		//User32DLL.INSTANCE.MessageBoxA( 0, "wwwwww", "vvvv", 0);
		String res = User32DLL.INSTANCE.GetIEnumDot11AdHocNetworks( 0, 0);
		System.out.println(res);

	}
}
