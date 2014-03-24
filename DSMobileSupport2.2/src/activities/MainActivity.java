package activities;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import util.Enums;
import util.MessageManager;
import util.ReadWrite;
import util.TelephoneManager;
import webservice.WSOCheckIsUserRegistered;
import webservice.WSOGetLatestVersion;
import webservice.WSOGetRetailStores;
import webservice.WSOStoreUserRequest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsmobilesupport.R;

import domain.RetailStore;

public class MainActivity extends Activity implements OnClickListener {

	private String deviceId = "";
	private String isUserRegistered = "";
	private String webResponse;
	private TextView tvNoLicenceNotification;
	private View btnSendRequest;
	private Spinner spRetailStores;
	private EditText etJMBG;
	private EditText etName;
	private EditText etEmail;
	private Handler handler = new Handler();
	private int webMethod = Enums.WebMethod.getRetailStores.ordinal();
	private RetailStore selectedRetailStore;
	private String androidVersion = "";
	private Pattern pattern;
	private Matcher matcher;
	private static String EMAIL_PATTERN="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public MainActivity() {		
		pattern = Pattern.compile(EMAIL_PATTERN);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			deviceId = TelephoneManager.getDeviceId(getBaseContext(),
					getContentResolver());
			androidVersion = TelephoneManager.getAndroidVersion();

			checkLicence();

			if (isUserRegistered.equals("null")) {
				setContentView(R.layout.activity_no_licence_notification);
				setTitle(R.string.title_activity_no_licence_notification);
				spRetailStores = (Spinner) findViewById(R.id.spRetailStoresRegister);
				etJMBG = (EditText) findViewById(R.id.etJMBG);
				etName = (EditText) findViewById(R.id.etNameSurname);
				etEmail = (EditText) findViewById(R.id.etEmail);
				getRetailStores();

				btnSendRequest = findViewById(R.id.btnUserRegister);
				btnSendRequest.setOnClickListener(this);

				String notification = getApplicationContext().getString(
						R.string.txtNoLicenceNotification);				

				tvNoLicenceNotification = (TextView) findViewById(R.id.tvLicenceNotification);
				tvNoLicenceNotification.setText(notification);

			} else {
				if(isUserRegistered.equals("True"))
				{
				setContentView(R.layout.activity_main);
				View btnExit = findViewById(R.id.btnExit);
				View btnSettings = findViewById(R.id.btnSettings);
				View btnGetState = findViewById(R.id.btnGetState);
				View btnDownloadNewVersion = findViewById(R.id.btnDownloadNewVersion);

				btnExit.setOnClickListener(this);
				btnSettings.setOnClickListener(this);
				btnGetState.setOnClickListener(this);
				btnDownloadNewVersion.setOnClickListener(this);

				checkIsNewVersionAvailable();
				}
				else
				{
					setContentView(R.layout.already_registered);
					setTitle(R.string.title_already_registrated);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkIsNewVersionAvailable() {
		Object[] parameters = new Object[1];
		parameters[0] = Enums.AndroidVersion.android22.ordinal();
		WSOGetLatestVersion wsoGetLatestVersion = new WSOGetLatestVersion(
				getApplicationContext());
		webResponse = wsoGetLatestVersion.callWebService(parameters);
		webMethod = Enums.WebMethod.checkIsNewVersionAvailable.ordinal();
		handler.post(webServiceResult);
	}

	private void getRetailStores() {
		WSOGetRetailStores wsoGetRetailStores = new WSOGetRetailStores(
				getApplicationContext());
		webResponse = wsoGetRetailStores.callWebService(null);
		handler.post(webServiceResult);
	}
	
	public boolean validate(final String hex) {
		matcher = pattern.matcher(hex);
		return matcher.matches();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnExit:
			this.finish();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.btnGetState:
			startActivity(new Intent(this, GetArticleState.class));
			break;
		case R.id.btnSettings:
			startActivity(new Intent(this, Settings.class));
			break;
		case R.id.btnDownloadNewVersion:
			startActivity(new Intent(this, DownloadNewVersion.class));
			break;
		case R.id.btnUserRegister:
			String jmbg = etJMBG.getText().toString().trim();
			String name = etName.getText().toString().trim();
			String mail = etEmail.getText().toString().trim();
			selectedRetailStore = (RetailStore) spRetailStores
					.getSelectedItem();
			if (etJMBG.getText().toString().trim().length() == 0
					|| etName.getText().toString().trim().length() == 0
					|| etEmail.getText().toString().trim().length() == 0) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Molimo Vas da unesete sve parametre!",
						Toast.LENGTH_LONG);
				toast.show();
			} else {
				if (validate(mail)) 
					register(jmbg, name, mail,
							String.valueOf(selectedRetailStore
									.getRetailStoreId()), deviceId,
							androidVersion);
				else
				{
					Toast toast = Toast.makeText(getApplicationContext(),
							"Molimo Vas da pravilno unesete E-mail!",
							Toast.LENGTH_LONG);
					toast.show();
				}				
			}
			break;
		}
	}

	private void register(final String jmbg, final String name,
			final String mail,final String retailStoreId, final String device,
			final String versionAnd) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						WSOStoreUserRequest wsoStoreUserRequest = new WSOStoreUserRequest(
								getApplicationContext());
						Object[] parameters = new Object[6];
						parameters[0] = jmbg;
						parameters[1] = name;
						parameters[2] = mail;
						parameters[3] = retailStoreId;
						parameters[4] = device;
						parameters[5] = versionAnd;

						webResponse = wsoStoreUserRequest
								.callWebService(parameters);
						webMethod = Enums.WebMethod.storeUserRequest.ordinal();
						handler.post(webServiceResult);
						setContentView(R.layout.already_registered);
						setTitle(R.string.title_already_registrated);
					}
				});
			}
		}).start();
	}

	private void checkLicence() {
		Object[] parameters = new Object[1];
		parameters[0] = deviceId;

		WSOCheckIsUserRegistered wsoCheckIsUserRegistered = new WSOCheckIsUserRegistered(
				getApplicationContext());
		isUserRegistered = wsoCheckIsUserRegistered.callWebService(parameters);
	}

	final Runnable webServiceResult = new Runnable() {
		public void run() {
			try {
				if (webMethod == Enums.WebMethod.storeUserRequest.ordinal()) {
					ReadWrite.writeToFile(getApplicationContext(),
							selectedRetailStore);
					MessageManager.showShortMessage(getApplicationContext(),
							webResponse);
				} else {
					if (webMethod == Enums.WebMethod.checkIsNewVersionAvailable
							.ordinal()) {
						if (checkVersionDifference()) {
							String message = getApplicationContext().getString(
									R.string.txtNewVersionNotification);
							MessageManager.showShortMessage(
									getApplicationContext(), message);
						}
					} else {
						List<RetailStore> spinnerArray = new ArrayList<RetailStore>();
						DocumentBuilder builder;
						builder = DocumentBuilderFactory.newInstance()
								.newDocumentBuilder();
						Document document = builder.parse(new InputSource(
								new StringReader(webResponse)));

						XPathFactory xPathfactory = XPathFactory.newInstance();
						XPath xpath = xPathfactory.newXPath();
						XPathExpression expr = null;
						expr = xpath.compile("/retailStores/retailStore");
						NodeList retailStores = null;
						retailStores = (NodeList) expr.evaluate(document,
								XPathConstants.NODESET);

						for (int i = 0; i < retailStores.getLength(); i++) {
							RetailStore rs = new RetailStore();

							int retailStoreId = Integer.valueOf(retailStores
									.item(i).getAttributes()
									.getNamedItem("retailStoreId")
									.getNodeValue().toString()
									.replace('"', ' ').trim());
							String name = retailStores.item(i).getAttributes()
									.getNamedItem("name").getNodeValue();
							int retailGroupId = Integer.valueOf(retailStores
									.item(i).getAttributes()
									.getNamedItem("retailGroupId")
									.getNodeValue().toString()
									.replace('"', ' ').trim());

							rs.setRetailStoreId(retailStoreId);
							rs.setName(name);
							rs.setRetailGroupId(retailGroupId);
							spinnerArray.add(rs);
						}

						ArrayAdapter<RetailStore> adapter = new ArrayAdapter<RetailStore>(
								getApplicationContext(),
								android.R.layout.simple_spinner_item,
								spinnerArray);
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spRetailStores.setAdapter(adapter);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private boolean checkVersionDifference() {
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			double currentVersion = Double.valueOf(pInfo.versionName);
			double serverVersion = Double.valueOf(webResponse);
			return serverVersion > currentVersion;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
}
