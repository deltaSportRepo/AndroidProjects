package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;

import android.content.Context;

import com.example.dsmobilesupport.R;

public class WSOGetImagesURL extends WebServiceOperation {
	
	public WSOGetImagesURL(Context context) {
		super(context);
		methodName = context
				.getString(R.string.method_name_getimagesurl);
		soapAction = context
				.getString(R.string.soap_action_getimagesurl);
		timeout = 5 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try {
			String itemId = EncryptDecrypt.encrypt(parameters[0].toString());
			
			PropertyInfo query = new PropertyInfo();
			query.setName("query");
			query.setValue(itemId);
			query.setType(String.class);
			request.addProperty(query);
			
		} catch (Exception ex) {
			ex.getStackTrace();
		}

	}

}
