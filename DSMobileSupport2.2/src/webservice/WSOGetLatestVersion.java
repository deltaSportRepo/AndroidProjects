package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;

import com.example.dsmobilesupport.R;

import android.content.Context;

public class WSOGetLatestVersion extends WebServiceOperation{

	public WSOGetLatestVersion(Context context) {
		super(context);
		methodName = context.getString(R.string.method_name_getlatestversion);
		soapAction = context.getString(R.string.soap_action_getlatestversion);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try{
			String androidVersionId = EncryptDecrypt.encrypt(parameters[0].toString());
			
			PropertyInfo version = new PropertyInfo();
			version.setName("androidVersionId");
			version.setValue(androidVersionId);
			version.setType(String.class);
			request.addProperty(version);
			}
			catch(Exception e){
				e.printStackTrace();
			}
	}
}
