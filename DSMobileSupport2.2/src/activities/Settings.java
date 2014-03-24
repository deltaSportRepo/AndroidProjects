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

import util.MessageManager;
import util.ReadWrite;
import webservice.WSOGetRetailStores;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dsmobilesupport.R;

import domain.RetailStore;

public class Settings extends Activity {

	private Button btnApply;
	private Spinner spRetailStores;
	private TextView tVCcurrentRetailStore;
	private Handler handler = new Handler();
	private String webResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		try {
			tVCcurrentRetailStore = (TextView) findViewById(R.id.twCurrentRetailStore);

			getRetailStores();

			setCurrentRetailStoreText();

			btnApply = (Button) findViewById(R.id.btnApplySettings);
			btnApply.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					RetailStore selectedRetailStore = (RetailStore) spRetailStores
							.getSelectedItem();
					applySettings(selectedRetailStore);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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

	public void getRetailStores() {
		WSOGetRetailStores wsoGetRetailStores = new WSOGetRetailStores(
				getApplicationContext());
		webResponse = wsoGetRetailStores.callWebService(null);
		handler.post(webServiceResult);
	}

	final Runnable webServiceResult = new Runnable() {
		public void run() {
			try {
				// Uzima odgovor iz klase koja je pozvala odgovarajucu metodu
				// web servisa				
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

					int retailStoreId = Integer
							.valueOf(retailStores.item(i).getAttributes()
									.getNamedItem("retailStoreId")
									.getNodeValue().toString()
									.replace('"', ' ').trim());
					String name = retailStores.item(i).getAttributes()
							.getNamedItem("name").getNodeValue();
					int retailGroupId = Integer
							.valueOf(retailStores.item(i).getAttributes()
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
						android.R.layout.simple_spinner_item, spinnerArray);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spRetailStores = (Spinner) findViewById(R.id.spRetailStore);
				spRetailStores.setAdapter(adapter);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void setCurrentRetailStoreText() {
		RetailStore rs = null;
		try {
			rs = ReadWrite.getCurrentRetailStore(getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		tVCcurrentRetailStore
				.setText(rs == null ? "Radnja: Nije odabrana ni jedna radnja"
						: "Radnja: " + rs.getName());
	}

	private void applySettings(RetailStore retailStore) {
		try {
			Context context = getApplicationContext();
			ReadWrite.writeToFile(context, retailStore);

			String text = "Uspesna operacija";
			MessageManager.showShortMessage(context, text);

			setCurrentRetailStoreText();
		} catch (Exception e) {
			Log.e("Exception", "Error: " + e.toString());
			MessageManager.showShortMessage(getApplicationContext(),
					e.getMessage());
		}
	}
}
