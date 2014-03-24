package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;
import android.content.Context;

import com.example.dsmobilesupport.R;

public class WSOGetItemByLocationAndBarcode extends WebServiceOperation {

	public WSOGetItemByLocationAndBarcode(Context context) {
		super(context);
		methodName = context
				.getString(R.string.method_name_getitembylocationandbarcode);
		soapAction = context
				.getString(R.string.soap_action_getitembylocationandbarcode);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try {
			String retailStoreId = EncryptDecrypt.encrypt(parameters[0].toString());
			String bar = EncryptDecrypt.encrypt(parameters[1].toString());

			PropertyInfo retailStore = new PropertyInfo();
			retailStore.setName("retailStoreId");
			retailStore.setValue(retailStoreId);
			retailStore.setType(String.class);
			request.addProperty(retailStore);

			PropertyInfo barcode = new PropertyInfo();
			barcode.setName("barcode");
			barcode.setValue(bar);
			barcode.setType(String.class);
			request.addProperty(barcode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
