package util;

import java.util.UUID;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class TelephoneManager {

	public static String getAndroidVersion() {
		String deviceVersion = Build.VERSION.RELEASE;
		return deviceVersion;
	}

	public static String getDeviceId(Context context, ContentResolver contentResolver) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						contentResolver,
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
	}
}
