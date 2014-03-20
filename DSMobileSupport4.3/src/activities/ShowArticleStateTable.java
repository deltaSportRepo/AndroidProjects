package activities;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.ActionBar.LayoutParams;
//import activities.ShowArticleStateGrid.ProgressTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.dsmobilesupport.R;

public class ShowArticleStateTable extends Activity {

	private Document document;
	String articalState = "";
	TableRow row;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_article_state_table);
		articalState = getIntent().getExtras().getString("articleState");
		loadTable();
	}

	private void loadTable() {
		try {
			DocumentBuilder builder;
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(
					articalState)));
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			XPathExpression expr = null;
			// sve radnje
			expr = xPath.compile("retailStores/retailStore");
			NodeList retailStores = null;
			retailStores = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);

			TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.1f);			
			int[] fixedColumnWidths = new int[] { 48, 47 };
			int fixedHeaderHeight = 30;
			row = new TableRow(this);
			LinearLayout linear = (LinearLayout) findViewById(R.id.linear);

			linear.setMinimumHeight(40);
			TableLayout header = (TableLayout) findViewById(R.id.table_header);

			progressBar = new ProgressBar(this, null,
					android.R.attr.progressBarStyleSmall);
			Drawable progress = getResources().getDrawable(R.drawable.custom_progress_background);
			progressBar.setIndeterminateDrawable(progress);
			progressBar.setBackgroundColor(Color.BLACK);
			progressBar.setVisibility(View.INVISIBLE);

			row.setLayoutParams(wrapWrapTableRowParams);
			row.setGravity(Gravity.CENTER);
			row.setBackgroundColor(Color.BLACK);
			row.addView(makeTableRowWithTextAllInOne(null,
					R.string.column2header, Color.WHITE, fixedColumnWidths[0],
					fixedHeaderHeight));
			row.addView(makeTableRowWithTextAllInOne(null,
					R.string.column3header, Color.WHITE, fixedColumnWidths[1],
					fixedHeaderHeight));
			row.addView(progressBar);
			row.setSelected(false);
			header.addView(row);

			for (int i = 0; i < retailStores.getLength(); i++) {
				// za radnje
				XPathExpression exprItems = xPath.compile("item");
				NodeList items = (NodeList) exprItems.evaluate(
						retailStores.item(i), XPathConstants.NODESET);
				String status = retailStores.item(i).getAttributes()
						.getNamedItem("status").getNodeValue();
				int available = Integer.valueOf(retailStores.item(i)
						.getAttributes().getNamedItem("available")
						.getNodeValue());

				row = new TableRow(this);
				row.setLayoutParams(wrapWrapTableRowParams);
				row.setGravity(Gravity.CENTER);
				row.setBackgroundColor(available == 0 ? Color.DKGRAY
						: getResources().getColor(R.color.unavailableStore));
				row.addView(makeTableRowWithTextAllInOne(status, 0,
						Color.WHITE, 100, 30));
				row.setSelected(false);

				linear.addView(row);

				for (int j = 0; j < items.getLength(); j++) {
					// za item-e
					XPathExpression exprSize = xPath.compile("size");
					NodeList size = (NodeList) exprSize.evaluate(items.item(j),
							XPathConstants.NODESET);
					String[] parametersForSending = new String[4];
					parametersForSending[0] = items.item(j).getAttributes()
							.getNamedItem("itemId").getNodeValue();
					parametersForSending[1] = items.item(j).getAttributes()
							.getNamedItem("sillhouete").getNodeValue();
					parametersForSending[2] = items.item(j).getAttributes()
							.getNamedItem("itemPrice").getNodeValue();
					parametersForSending[3] = items.item(j).getAttributes()
							.getNamedItem("itemName").getNodeValue();

					row = new TableRow(this);
					row.setLayoutParams(wrapWrapTableRowParams);
					row.setGravity(Gravity.CENTER);
					row.setBackgroundColor(Color.GRAY);
					row.addView(makeTableRowWithTextAllInOne("Šifra artikla: "
							+ parametersForSending[0], 0, Color.WHITE, 100, 30));
					row.setTag(parametersForSending);
					final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					row.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							vibe.vibrate(90);
							String[] params = (String[]) v.getTag();
							Intent intent = new Intent(
									ShowArticleStateTable.this,
									ShowImageWithDetails.class);
							intent.putExtra("itemId", params[0]);
							intent.putExtra("sillhouete", params[1]);
							intent.putExtra("itemPrice", params[2]);
							intent.putExtra("itemName", params[3]);
							startActivity(intent);
							new ProgressTask().execute();
						}
					});
					row.setId(j);
					linear.addView(row);
					for (int k = 0; k < size.getLength(); k++) {
						// za velcine i kolicine
						String sizeId = size.item(k).getAttributes()
								.getNamedItem("sizeId").getNodeValue();
						String qtyPost = size.item(k).getAttributes()
								.getNamedItem("qtyPosted").getNodeValue();
						row = new TableRow(this);
						row.setLayoutParams(wrapWrapTableRowParams);
						row.setGravity(Gravity.CENTER);
						row.addView(makeTableRowWithTextAllInOne(sizeId, 0,
								Color.BLACK, 50, fixedHeaderHeight));
						row.addView(makeTableRowWithTextAllInOne(qtyPost, 0,
								Color.BLACK, 50, fixedHeaderHeight));
						row.setSelected(false);
						row.setBackgroundColor(Color.LTGRAY);
						row.setPadding(2, 5, 0, 2);
						linear.addView(row);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private TextView recyclableTextView;

	public TextView makeTableRowWithTextAllInOne(String text, int textInt,
			int colors, int widthInPercentOfScreenWidth, int fixedHeightInPixels) {
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		recyclableTextView = new TextView(this);
		recyclableTextView.setTextSize(15);
		recyclableTextView.setWidth(widthInPercentOfScreenWidth * screenWidth
				/ 100);
		recyclableTextView.setHeight(fixedHeightInPixels);
		recyclableTextView.setTextColor(colors);
		if (text != null)
			recyclableTextView.setText(text);
		else
			recyclableTextView.setText(textInt);
		if (colors == Color.BLACK)
			recyclableTextView
					.setBackgroundResource(R.drawable.border_btw_views);
		recyclableTextView.setPadding(10, 0, 5, 0);
		return recyclableTextView;
	}

	private class ProgressTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.INVISIBLE);
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

		case R.id.menu_home:
			startActivity(new Intent(this, MainActivity.class));
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

}
