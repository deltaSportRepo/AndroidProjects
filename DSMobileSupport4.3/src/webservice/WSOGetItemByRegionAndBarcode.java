package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;
import android.content.Context;

import com.example.dsmobilesupport.R;

public class WSOGetItemByRegionAndBarcode extends WebServiceOperation {

	public WSOGetItemByRegionAndBarcode(Context context) {
		super(context);
		methodName = context
				.getString(R.string.method_name_getitembyregionandbarcode);
		soapAction = context
				.getString(R.string.soap_action_getitembyregionandbarcode);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try {
			String reg = EncryptDecrypt.encrypt(parameters[0].toString());
			String bar = EncryptDecrypt.encrypt(parameters[1].toString());
			String retailGroupId = EncryptDecrypt.encrypt(parameters[2].toString());

			PropertyInfo region = new PropertyInfo();
			region.setName("regionId");
			region.setValue(reg);
			region.setType(String.class);
			request.addProperty(region);

			PropertyInfo barcode = new PropertyInfo();
			barcode.setName("barcode");
			barcode.setValue(bar);
			barcode.setType(String.class);
			request.addProperty(barcode);

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
