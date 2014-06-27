package ela.riskey.parammod;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnOptionsItemSelectedListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import ela.riskey.parammod.utils.JSONfunctions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

public class SubmitActivity extends Activity implements OnCreateOptionsMenuListener, OnOptionsItemSelectedListener{
	ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);
	
	EditText inputTitle;
	EditText inputUser;
	EditText inputLink;
	EditText inputPrev;
	
	private ProgressDialog pDialog; 
	
	JSONObject jsonobject;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSherlock
		.setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		mSherlock.setContentView(R.layout.submit);
		inputTitle = (EditText) findViewById(R.id.judul);
		inputUser = (EditText) findViewById(R.id.pembuat);
		inputLink = (EditText) findViewById(R.id.link);
		inputPrev = (EditText) findViewById(R.id.prev);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		new uploadParam().execute();
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu (android.view.Menu menu) {
		return mSherlock.dispatchCreateOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("Submit").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);		
		return true;
	}
	
	class uploadParam extends AsyncTask<String, String, String> {
		
		SharedPreferences prefs = getSharedPreferences("paramprefs",
				Context.MODE_PRIVATE);			
		String model = prefs.getString("model", null);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SubmitActivity.this);
			pDialog.setMessage("Creating Product..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			String title = inputTitle.getText().toString();
			String user = inputUser.getText().toString();
			String link = inputLink.getText().toString();
			String prev = inputPrev.getText().toString();

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("title", title));
			postParameters.add(new BasicNameValuePair("user", user));
			postParameters.add(new BasicNameValuePair("link", link));
			postParameters.add(new BasicNameValuePair("prev", prev));
			postParameters.add(new BasicNameValuePair("model", model));

			jsonobject = JSONfunctions.getJSONfromURL(
					"http://192.168.18.1/test2.php", "POST", postParameters);
			// TODO Auto-generated method stub
			try {
				int success = jsonobject.getInt("success");
				if (success == 1) {
					// successfully created product					
					
					// closing this screen
					finish();
				} else {
					// failed to create product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
				
		protected void onPostExecuted() {
			pDialog.dismiss();
		}
		
	}

}
