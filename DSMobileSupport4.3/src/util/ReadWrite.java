package util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import domain.RetailStore;

public class ReadWrite {

	public static void writeToFile(Context context, RetailStore retailStore)
			throws IOException {
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath()
					+ "/dsmobilesupport_res");
			dir.mkdir();
			File file = new File(dir, "settings.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
			outputStreamWriter.write(String.valueOf(retailStore
					.getRetailStoreId()
					+ "-"
					+ retailStore.getName()
					+ "-"
					+ retailStore.getRetailGroupId()));
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "Error: " + e.toString());
			throw e;
		}
	}

	public static void createSettinsFile(Context context) throws IOException {
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath()
					+ "/dsmobilesupport_res");
			dir.mkdir();
			File file = new File(dir, "settings.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
			outputStreamWriter.write("");
			outputStreamWriter.close();
			Toast.makeText(context, "Done writing SD 'mysdfile.txt'",
					Toast.LENGTH_SHORT).show();

		} catch (IOException e) {
			Log.e("Exception", "Error: " + e.toString());
			throw e;
		}
	}

	private static String readFromFile(Context context) throws Exception {

		String receiveString = "";

		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File myFile = new File(sdCard.getAbsolutePath()
					+ "/dsmobilesupport_res/settings.txt");
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(
					fIn));
			receiveString = "";
			String aDataRow = "";
			String aBuffer = "";
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow;
			}
			receiveString = aBuffer;
			myReader.close();

		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
			throw e;
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return receiveString;
	}

	public static RetailStore getCurrentRetailStore(Context context)
			throws Exception {
		RetailStore retailStore;
		try {
			String retailStoreString = readFromFile(context);
			String[] arrayRetailStore = retailStoreString.split("-");
			int retailStoreId = Integer.valueOf(arrayRetailStore[0]);
			String name = arrayRetailStore[1];
			int retailGroupId = Integer.valueOf(arrayRetailStore[2]);
			retailStore = new RetailStore(retailStoreId, name, retailGroupId);
		} catch (FileNotFoundException e) {
			retailStore = new RetailStore();
			retailStore.setRetailGroupId(0);
			retailStore.setName("Nije odabrana ni jedna radnja");
		}

		return retailStore;
	}
}
