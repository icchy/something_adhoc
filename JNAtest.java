package something_adhoc;

import com.sun.jna.Library;
import com.sun.jna.Native;


public class JNAtest {
	public interface User32Dll extends Library {
		User32Dll INSTANCE = (User32Dll)Native.loadLibrary("user32.dll", User32Dll.class);
		int MessageBoxA(int hWnd, String lpText, String lpCaption, int uType);
	}

	public static void main(String[] args) {
		User32Dll.INSTANCE.MessageBoxA(0, "Hello JNA!", "jna sample", 0x00000000);
	}
}