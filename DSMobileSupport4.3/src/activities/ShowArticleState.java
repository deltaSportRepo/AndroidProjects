package activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.dsmobilesupport.R;

public class ShowArticleState extends Activity {

	private TextView tvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_article_state);

		String articleState = getIntent().getExtras().getString("articleState");

		tvResult = (TextView) findViewById(R.id.tvShowArticleState);
		tvResult.setText(articleState);
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
