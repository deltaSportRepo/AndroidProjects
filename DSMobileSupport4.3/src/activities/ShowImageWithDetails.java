package activities;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.w3c.dom.Document;

import webservice.WSOGetImagesURL;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.dsmobilesupport.R;

public class ShowImageWithDetails extends Activity {

	Handler handler = new Handler();
	private String webResponse = "";
	String sillhouete;
	String itemPrice;
	String itemName;
	String itemId;
	Bitmap image = null;
	Document document;
	TableRow row;
	final private String emptyImageLink = "http://mobileappsupport.deltasport.com/Download/Resources/emptyImage.jpg";
	ProgressBar progressBarImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image_with_details);

		itemId = getIntent().getExtras().getString("itemId");
		sillhouete = getIntent().getExtras().getString("sillhouete");
		itemPrice = getIntent().getExtras().getString("itemPrice");
		itemName = getIntent().getExtras().getString("itemName");
		getImagesUrl(itemId);
		progressBarImage = new ProgressBar(this, null,
				android.R.attr.progressBarStyleLarge);
		Drawable progress = getResources().getDrawable(
				R.drawable.custom_progress_background);
		progressBarImage.setIndeterminateDrawable(progress);
		progressBarImage.setBackgroundColor(Color.BLACK);
		progressBarImage.setVisibility(View.INVISIBLE);
		LinearLayout linImg = (LinearLayout) findViewById(R.id.linear_image);
		linImg.addView(progressBarImage);
		FillTable();
	}

	private void getImagesUrl(final String query) {
		WSOGetImagesURL wsoImagesURL = new WSOGetImagesURL(
				getApplicationContext());
		Object[] parameters = new Object[1];
		parameters[0] = query + " nike";
		webResponse = wsoImagesURL.callWebService(parameters);
		handler.post(webServiceResult);
	}

	final Runnable webServiceResult = new Runnable() {
		public void run() {
			try {
				String imagePath = webResponse.matches("") ? emptyImageLink
						: webResponse;
				new MyTask().execute(imagePath);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				webResponse = "";
			}
		}
	};

	private class MyTask extends AsyncTask<String, Void, Bitmap> {

		Bitmap myBitmap;

		@Override
		protected void onPreExecute() {
			progressBarImage.setVisibility(View.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (Exception e) {

				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap param) {
			try {
				progressBarImage.setVisibility(View.GONE);
				if (image != null)
					super.cancel(true);
				image = myBitmap;
				FillImageView();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			try {
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				input.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private TextView recyclableTextView;

	public TextView makeTableRowWithTextAllInOne(String text, int textInt,
			int colors, int widthInPercentOfScreenWidth, int fixedHeightInPixels) {
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		recyclableTextView = new TextView(this);
		recyclableTextView.setTextSize(20);
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

	public void FillImageView() {
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int screenHeight = getResources().getDisplayMetrics().heightPixels;

		android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
				screenWidth, screenHeight * 70 / 100);
		LinearLayout lin_img = (LinearLayout) findViewById(R.id.linear_image);
		ImageView iView = new ImageView(this);
		iView.setImageBitmap(image);
		iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iView.setLayoutParams(params);
		iView.setBackgroundColor(Color.BLACK);
		lin_img.addView(iView);

	}

	public void FillTable() {
		TableLayout tableData = (TableLayout) findViewById(R.id.table_data);
		TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		row = new TableRow(this);
		row.setLayoutParams(wrapWrapTableRowParams);
		row.setGravity(Gravity.CENTER);
		row.setBackgroundColor(Color.GRAY);
		row.addView(makeTableRowWithTextAllInOne(null, R.string.sillhouete,
				Color.WHITE, 27, 40));
		row.addView(makeTableRowWithTextAllInOne(sillhouete, 0, Color.WHITE,
				73, 40));
		row.setPadding(2, 5, 0, 2);
		row.setSelected(false);
		tableData.addView(row);
		row = new TableRow(this);
		row.setLayoutParams(wrapWrapTableRowParams);
		row.setGravity(Gravity.CENTER);
		row.setBackgroundColor(Color.DKGRAY);
		row.addView(makeTableRowWithTextAllInOne(null, R.string.itemPrice,
				Color.WHITE, 27, 40));
		row.addView(makeTableRowWithTextAllInOne(itemPrice, 0, Color.WHITE, 73,
				40));
		row.setPadding(2, 5, 0, 2);
		row.setSelected(false);
		tableData.addView(row);
		row = new TableRow(this);
		row.setLayoutParams(wrapWrapTableRowParams);
		row.setGravity(Gravity.CENTER);
		row.setBackgroundColor(Color.GRAY);
		row.addView(makeTableRowWithTextAllInOne(null, R.string.itemName,
				Color.WHITE, 27, 100));
		row.addView(makeTableRowWithTextAllInOne(itemName, 0, Color.WHITE, 73,
				100));
		row.setPadding(2, 5, 0, 2);
		row.setSelected(false);
		tableData.addView(row);

	}

//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		if(!image.isRecycled() && image==null)
//		image.recycle();
//	}

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
