package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;
import android.content.Context;

import com.example.dsmobilesupport.R;

public class WSOCheckIsUserRegistered extends WebServiceOperation{

	public WSOCheckIsUserRegistered(Context context) {
		super(context);
		methodName = context.getString(R.string.method_name_checkisuserregistered);
		soapAction = context.getString(R.string.soap_action_checkisuserregistered);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try{
		String deviceId = EncryptDecrypt.encrypt(parameters[0].toString());
		
		PropertyInfo device = new PropertyInfo();
		device.setName("deviceId");
		device.setValue(deviceId);
		device.setType(String.class);
		request.addProperty(device);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
