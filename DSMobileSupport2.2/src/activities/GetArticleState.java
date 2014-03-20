package activities;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
import util.IntentIntegrator;
import util.IntentResult;
import util.MessageManager;
import util.ReadWrite;
import webservice.WSOGetItemByLocationAndBarcode;
import webservice.WSOGetItemByLocationAndItemId;
import webservice.WSOGetItemByRegionAndBarcode;
import webservice.WSOGetItemByRegionAndItemId;
import webservice.WSOGetRegions;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dsmobilesupport.R;

import domain.RetailStore;

public class GetArticleState extends Activity {

	private String webResponse = "Web servis nedostupan";

	private TextView txtNotification;
	private EditText etItemId;
	private Handler handler = new Handler();
	private Button btnSendRequest;
	private List<domain.Region> spinnerArray;
	private ArrayAdapter<domain.Region> adapter;
	private Spinner spRegions;
	private Document document;
	private int webMethod;
	private RetailStore currentRetailStore;

	// private WSOGetItemByRegionAndItemId wsoGetItemByRegionAnItemId;
	/** Called when the activity is first created. */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_article_state);

		txtNotification = (TextView) findViewById(R.id.tvGetArticleStateNotification);
		etItemId = (EditText) findViewById(R.id.itemId);
		try {
			currentRetailStore = ReadWrite
					.getCurrentRetailStore(getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		getRegions();

		btnSendRequest = (Button) findViewById(R.id.btnSendRequest);

		btnSendRequest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final domain.Region region = (domain.Region) spRegions
						.getSelectedItem();
				final String parameter = etItemId.getText().toString();

				if (parameter.length() < 4) {
					String text = "Minimalan broj karaktera je 4";
					MessageManager.showShortMessage(getApplicationContext(),
							text);
					return;
				}

				txtNotification.setVisibility(View.VISIBLE);
				txtNotification.setText("Obrada...");
				// Pozivanje web servisa vrsi se u novom Thread-u da bi se
				// ispisalo obrada... dok sistem poziva servis paralelno
				new Thread(new Runnable() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// Pretraga u trenutnoj radnji
								if (region.getRegionId() == 0) {
									if (webMethod == util.Enums.WebMethod.getAtcicleStateByBarcode
											.ordinal())
										getStateByBarcode(parameter);
									else
										getStateByItemId(parameter);
								}
								// Pretraga u regionu
								else {
									if (webMethod == util.Enums.WebMethod.getAtcicleStateByBarcode
											.ordinal())
										getStateByRegionAndBarcode(
												region.getRegionId(), parameter);
									else
										getStateByRegionAndItemId(
												region.getRegionId(), parameter);
								}
							}
						});
					}
				}).start();
			}
		});
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {         
        switch (item.getItemId())
        {
        case R.id.menu_search:            
        	startActivity(new Intent(this, GetArticleState.class));
            return true;
 
        case R.id.menu_preferences:
            startActivity(new Intent(this, Settings.class));
            return true;
 
        case R.id.menu_new_version:           
        	startActivity(new Intent(this, DownloadNewVersion.class));
            return true;
 
        case R.id.menu_exit:           
        	this.finish();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
            return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	private void getRegions() {
		WSOGetRegions wsoGetRegions = new WSOGetRegions(getApplicationContext());
		webResponse = wsoGetRegions.callWebService(null);
		webMethod = Enums.WebMethod.getRegions.ordinal();
		handler.post(webServiceResult);
	}

	public void getStateByItemId(final String itemId) {
		WSOGetItemByLocationAndItemId wsoGetItemByLocationAndItemId = new WSOGetItemByLocationAndItemId(
				getApplicationContext());
		Object[] parameters = new Object[2];
		parameters[0] = currentRetailStore.getRetailStoreId();
		parameters[1] = itemId;
		webResponse = wsoGetItemByLocationAndItemId.callWebService(parameters);
		webMethod = Enums.WebMethod.getAtcicleStateByItemId.ordinal();
		handler.post(webServiceResult);
	}

	public void getStateByRegionAndItemId(final int regionId,
			final String itemId) {
		WSOGetItemByRegionAndItemId wsoGetItemByRegionAnItemId = new WSOGetItemByRegionAndItemId(
				getApplicationContext());
		Object[] parameters = new Object[3];
		parameters[0] = regionId;
		parameters[1] = itemId;
		parameters[2] = currentRetailStore != null ? currentRetailStore.getRetailGroupId() : 0;
		webResponse = wsoGetItemByRegionAnItemId.callWebService(parameters);
		webMethod = Enums.WebMethod.getAtcicleStateByItemId.ordinal();
		handler.post(webServiceResult);
	}

	public void getStateByBarcode(final String barcodeId) {
		WSOGetItemByLocationAndBarcode wsoGetItemByLocationAndBarcode = new WSOGetItemByLocationAndBarcode(
				getApplicationContext());
		Object[] parameters = new Object[2];
		parameters[0] = currentRetailStore.getRetailStoreId();
		parameters[1] = barcodeId;
		webResponse = wsoGetItemByLocationAndBarcode.callWebService(parameters);
		webMethod = Enums.WebMethod.getAtcicleStateByBarcode.ordinal();
		handler.post(webServiceResult);
	}

	public void getStateByRegionAndBarcode(final int regionId,
			final String barcode) {
		WSOGetItemByRegionAndBarcode wsoGetItemByRegionAndBarcode = new WSOGetItemByRegionAndBarcode(
				getApplicationContext());
		Object[] parameters = new Object[3];
		parameters[0] = regionId;
		parameters[1] = barcode;
		parameters[2] = currentRetailStore.getRetailGroupId();
		webResponse = wsoGetItemByRegionAndBarcode.callWebService(parameters);
		webMethod = Enums.WebMethod.getAtcicleStateByBarcode.ordinal();
		handler.post(webServiceResult);
	}

	final Runnable webServiceResult = new Runnable() {
		public void run() {
			try {
				// Ispituje da li je u pitanju trazenje stanja ili popunjavanje
				// regiona
				if (webMethod == util.Enums.WebMethod.getAtcicleStateByItemId
						.ordinal()
						|| webMethod == util.Enums.WebMethod.getAtcicleStateByBarcode
								.ordinal()) {				

					if(webResponse.equals(getApplicationContext().getString(
							R.string.noqty_string))){						
					    MessageManager.showShortMessage(getApplicationContext(), getApplicationContext().getString(
								R.string.noqty_notification));	
						return;
					}
					
					Intent intent = new Intent(GetArticleState.this,
							ShowArticleStateTable.class);
					intent.putExtra("articleState", webResponse);
					startActivity(intent);
					
				} else {
					spinnerArray = new ArrayList<domain.Region>();

					DocumentBuilder builder;
					builder = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder();
					document = builder.parse(new InputSource(new StringReader(
							webResponse)));

					XPathFactory xPathfactory = XPathFactory.newInstance();
					XPath xpath = xPathfactory.newXPath();
					XPathExpression expr = null;
					expr = xpath.compile("/regions/region");
					NodeList regions = null;
					regions = (NodeList) expr.evaluate(document,
							XPathConstants.NODESET);

					if (currentRetailStore != null) {
						domain.Region current = new domain.Region(0,
								currentRetailStore.getName());
						spinnerArray.add(current);
					}

					for (int i = 0; i < regions.getLength(); i++) {
						domain.Region r = new domain.Region();

						int regionId = Integer.valueOf(regions.item(i)
								.getAttributes().getNamedItem("regionId")
								.getNodeValue());
						String name = regions.item(i).getAttributes()
								.getNamedItem("name").getNodeValue();

						r.setRegionId(regionId);
						r.setName(name);

						spinnerArray.add(r);
					}

					adapter = new ArrayAdapter<domain.Region>(
							getApplicationContext(),
							android.R.layout.simple_spinner_item, spinnerArray);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spRegions = (Spinner) findViewById(R.id.spRegions);
					spRegions.setAdapter(adapter);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				txtNotification.setVisibility(View.INVISIBLE);
				webMethod = util.Enums.WebMethod.getAtcicleStateByItemId
						.ordinal();
				webResponse = "Web servis je nedostupan";
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	public void onClick(View view) {
		webMethod = util.Enums.WebMethod.getAtcicleStateByBarcode.ordinal();
		IntentIntegrator intentIntegrator = new IntentIntegrator(this);
		intentIntegrator.initiateScan();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		try {
			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);
			if (scanResult != null) {
				if (scanResult.getContents().length() > 0) {
				etItemId.setText(scanResult.getContents());			
				}

			}
			else{
				webMethod = util.Enums.WebMethod.getAtcicleStateByItemId.ordinal();
			}
		} catch (Exception e) {
			webMethod = util.Enums.WebMethod.getAtcicleStateByItemId.ordinal();
		}
	}
}
