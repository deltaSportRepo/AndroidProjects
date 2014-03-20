package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;
import android.content.Context;

import com.example.dsmobilesupport.R;

public class WSOStoreUserRequest extends WebServiceOperation {

	public WSOStoreUserRequest(Context context) {
		super(context);
		methodName = context.getString(R.string.method_name_storeuserrequest);
		soapAction = context.getString(R.string.soap_action_storeuserrequest);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try {
			String jmbg = EncryptDecrypt.encrypt(parameters[0].toString());
			String name = EncryptDecrypt.encrypt(parameters[1].toString());
			String email = EncryptDecrypt.encrypt(parameters[2].toString());
			String retailStoreId = EncryptDecrypt.encrypt(parameters[3]
					.toString());
			String deviceId = EncryptDecrypt.encrypt(parameters[4].toString());
			String andrVersion = EncryptDecrypt.encrypt(parameters[5].toString());
			

			PropertyInfo userId = new PropertyInfo();
			userId.setName("jmbg");
			userId.setValue(jmbg);
			userId.setType(String.class);
			request.addProperty(userId);

			PropertyInfo nam = new PropertyInfo();
			nam.setName("name");
			nam.setValue(name);
			nam.setType(String.class);
			request.addProperty(nam);

			PropertyInfo mailAdress = new PropertyInfo();
			mailAdress.setName("email");
			mailAdress.setValue(email);
			mailAdress.setType(String.class);
			request.addProperty(mailAdress);
			
			PropertyInfo retailStore = new PropertyInfo();
			retailStore.setName("retailStoreId");
			retailStore.setValue(retailStoreId);
			retailStore.setType(String.class);
			request.addProperty(retailStore);

			PropertyInfo device = new PropertyInfo();
			device.setName("deviceId");
			device.setValue(deviceId);
			device.setType(String.class);
			request.addProperty(device);

			PropertyInfo androidVersion = new PropertyInfo();
			androidVersion.setName("androidVersion");
			androidVersion.setValue(andrVersion);
			androidVersion.setType(String.class);
			request.addProperty(androidVersion);		

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
