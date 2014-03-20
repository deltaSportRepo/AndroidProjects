package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;
import android.content.Context;

import com.example.dsmobilesupport.R;

public class WSOGetItemByRegionAndItemId extends WebServiceOperation {

	public WSOGetItemByRegionAndItemId(Context context) {
		super(context);
		methodName = context
				.getString(R.string.method_name_getitembyregionanditemid);
		soapAction = context
				.getString(R.string.soap_action_getitembyregionanditemid);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try {
			String reg = EncryptDecrypt.encrypt(parameters[0].toString());
			String itemId = EncryptDecrypt.encrypt(parameters[1].toString());
			String retailGroupId = EncryptDecrypt.encrypt(parameters[2]
					.toString());

			PropertyInfo region = new PropertyInfo();
			region.setName("regionId");
			region.setValue(reg);
			region.setType(String.class);
			request.addProperty(region);

			PropertyInfo item = new PropertyInfo();
			item.setName("itemId");
			item.setValue(itemId);
			item.setType(String.class);
			request.addProperty(item);

			PropertyInfo retailGroup = new PropertyInfo();
			retailGroup.setName("retailGroupId");
			retailGroup.setValue(retailGroupId);
			retailGroup.setType(String.class);
			request.addProperty(retailGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
