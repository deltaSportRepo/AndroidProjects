package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;
import android.content.Context;

import com.example.dsmobilesupport.R;

public class WSOGetItemByLocationAndItemId extends WebServiceOperation {

	public WSOGetItemByLocationAndItemId(Context context) {
		super(context);
		methodName = context
				.getString(R.string.method_name_getitembylocationanditemId);
		soapAction = context
				.getString(R.string.soap_action_getitembylocationanditemId);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try {
			String retailStoreId = EncryptDecrypt.encrypt(parameters[0]
					.toString());
			String itemId = EncryptDecrypt.encrypt(parameters[1].toString());

			PropertyInfo retailStore = new PropertyInfo();
			retailStore.setName("retailStoreId");
			retailStore.setValue(retailStoreId);
			retailStore.setType(String.class);
			request.addProperty(retailStore);

			PropertyInfo item = new PropertyInfo();
			item.setName("itemId");
			item.setValue(itemId);
			item.setType(String.class);
			request.addProperty(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
