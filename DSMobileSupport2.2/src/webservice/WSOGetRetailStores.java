package webservice;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;

import com.example.dsmobilesupport.R;

public class WSOGetRetailStores extends WebServiceOperation {
	
	public WSOGetRetailStores(Context context) {
		super(context);
		methodName = context.getString(R.string.method_name_getretailstores);
		soapAction = context.getString(R.string.soap_action_getretailstores);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object [] parameters) {
		System.out.println("Nema parametre");
	}
}
