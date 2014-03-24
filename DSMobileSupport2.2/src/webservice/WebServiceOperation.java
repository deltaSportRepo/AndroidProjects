package webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import util.EncryptDecrypt;
import android.content.Context;
import android.os.Handler;

import com.example.dsmobilesupport.R;

public abstract class WebServiceOperation {

	protected String namespace;
	protected String url;
	protected String methodName;
	protected String soapAction;
	protected Thread thread;
	protected String webResponse;
	protected Handler handler = new Handler();
	protected Context context;
	protected int timeout;
	 

	public WebServiceOperation(Context context) {
		this.context = context;
		namespace = context.getString(R.string.namespace);
		url = context.getString(R.string.url);
	}

	public synchronized String getWebResponse() {
		return webResponse;
	}

	public synchronized String callWebService(final Object[] parameters) {
		thread = new Thread() {
			public void run() {
				try {
					SoapObject request = new SoapObject(namespace, methodName);
					
					PropertyInfo encryptDecryptType = new PropertyInfo();
					encryptDecryptType.setName("encryptDecryptType");
					encryptDecryptType.setValue(0);
					encryptDecryptType.setType(Integer.class);
					request.addProperty(encryptDecryptType);

					addParameters(request, parameters);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.dotNet = true;
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(
							url, timeout);

					androidHttpTransport.call(soapAction, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope
							.getResponse();
                    
					webResponse = EncryptDecrypt.decrypt(response.toString());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();
		try {
			//Kad ostale aktivnosti budu pokusavale da pristupe responsu, cekace da ga prvo ova nit upise
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return webResponse;
	}

	protected abstract void addParameters(SoapObject request,
			Object[] parameters);
}
