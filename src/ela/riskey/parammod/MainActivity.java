/*
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ela.riskey.parammod;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnOptionsItemSelectedListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import ela.riskey.parammod.filechooser.FileChooserActivity;
import ela.riskey.parammod.utils.FileUtils;
import ela.riskey.parammod.utils.JSONfunctions;
import ela.riskey.parammod.utils.ShellInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends Activity implements
		OnCreateOptionsMenuListener, OnOptionsItemSelectedListener {
	ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);

	// Anu Item
	private static final int BACKUP = 1;
	private static final int RESTORE = 2;
	private static final int REFRESH = 3;
	private static final int HELP = 4;	

	Builder rDialog;
	Builder hDialog;
	Builder iDialog;

	// Anu Notif
	NotificationManager mManager;
	NotificationCompat.Builder mBuilder;
	Notification mNotification;

	// Anu File Chooser
	private static final int REQUEST_CODE = 6384;

	// Declare Variables
	static String filebackup;
	JSONObject jsonobject;
	JSONArray jsonarray;
	ListView listview;
	ListViewAdapter adapter;
	ProgressDialog mProgressDialog;
	ArrayList<HashMap<String, String>> arraylist;
	static String TITLE = "title";
	static String USER = "user";
	static String MD5SUM = "md5sum";
	static String PREVIEW = "preview";
	static String LINK = "link";
	static String ID = "id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSherlock
				.setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		mSherlock.setContentView(R.layout.listview_main);
		AdView adView = (AdView) findViewById(R.id.adview);
		adView.loadAd(new AdRequest());
		// Buat Folder
		createDirIfNotExists();
		SharedPreferences prefs = this.getSharedPreferences("paramprefs",
				Context.MODE_PRIVATE);
		Global.partisi = prefs.getString("partisi", null);

	}
	
	protected void onStart() {
		super.onStart();
		check();
	}

	private boolean check() {
		final SharedPreferences prefs = this.getSharedPreferences("paramprefs",
				Context.MODE_PRIVATE);
		Boolean setuju = prefs.getBoolean("setuju", false);
		if (!setuju) {
			hDialog = new AlertDialog.Builder(MainActivity.this);
			hDialog.setTitle(R.string.app_name);
			hDialog.setMessage(R.string.falert);
			hDialog.setCancelable(false);
			hDialog.setPositiveButton(R.string.ya, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					try {
						ShellInterface.setShell("sh");
						String partisi = ShellInterface
								.getProcessOutput("mount | grep -i lfs | awk '{print $1}'");
						String model = ShellInterface
								.getProcessOutput("getprop ro.product.model");
						Log.e(getPackageName(), partisi);
						Log.e(getPackageName(), model);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("partisi", partisi);
						editor.putString("model", model);
						editor.putBoolean("setuju", true);
						editor.commit();
					} catch (Exception e) {
						Log.e("Error", e.getMessage());
						e.printStackTrace();
					}
				}
			});
			hDialog.setNegativeButton(R.string.tak, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			hDialog.create();
			hDialog.show();
		}
		return false;		
	}

	public static class Global {		
		static String partisi;
		static String fbackup;
		static String model;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar

		menu.add(Menu.NONE, BACKUP, 0, R.string.backup)
				.setIcon(R.drawable.ic_menu_backup)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(Menu.NONE, RESTORE, 0, R.string.restore)
				.setIcon(R.drawable.ic_menu_archive)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(Menu.NONE, REFRESH, 0, R.string.refresh)
				.setIcon(R.drawable.ic_menu_refresh)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(Menu.NONE, HELP, 0, R.string.help)
				.setIcon(R.drawable.ic_menu_help)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		return mSherlock.dispatchCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case BACKUP:
			// app icon in action bar clicked; go home
			new backupParam().execute();
			break;
		case RESTORE:
			openFolder();
			break;
		case REFRESH:
			refresh();
			break;
		case HELP:
			help();
			break;
		}
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case BACKUP:
			// app icon in action bar clicked; go home
			new backupParam().execute();
			break;
		case RESTORE:
			openFolder();
			break;
		case REFRESH:
			refresh();
			break;
		case HELP:
			help();
			break;
		}
		return super.onOptionsItemSelected(item);
	}	

	private boolean isNetworkAvailable() {
		// Using ConnectivityManager to check for Network Connection
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public void openFolder() {
		Intent intent = new Intent(this, FileChooserActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	private void refresh() {
		if (!isNetworkAvailable()) {
			iDialog = new AlertDialog.Builder(MainActivity.this);
			iDialog.setTitle(R.string.app_name);
			iDialog.setMessage(R.string.winternet);
			iDialog.setCancelable(true);
			iDialog.create();
			iDialog.show();
		} else {
			new DownloadJSON().execute();
		}
	}
	
	private void help() {
		hDialog = new AlertDialog.Builder(MainActivity.this);
		hDialog.setTitle(R.string.help).setNeutralButton(R.string.keluar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
		LayoutInflater inflater = getLayoutInflater();
		View troubleView = inflater.inflate(R.layout.help, null, false);
		hDialog.setView(troubleView);
		hDialog.create().show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					final Uri uri = data.getData();

					try {
						final File file = FileUtils.getFile(uri);
						Global.fbackup = file.toString();
						rDialog = new AlertDialog.Builder(MainActivity.this);
						rDialog.setTitle(R.string.app_name);
						rDialog.setMessage(R.string.wrestore);
						rDialog.setPositiveButton(R.string.ya,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										new restoreParam().execute();
									}
								});
						rDialog.setNegativeButton(R.string.tak,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								});
						rDialog.create();
						rDialog.show();
					} catch (Exception e) {
						Log.e("FileSelectorTestActivity", "File select error",
								e);
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static boolean createDirIfNotExists() {
		boolean ret = true;
		String actualFileName = "param:.cache";
		String[] tempFileNames;
		// misahin array bro
		String delimiter = ":";
		tempFileNames = actualFileName.split(delimiter, 0);
		for (int j = 0; j < tempFileNames.length; j++) {
			File file = new File(Environment.getExternalStorageDirectory(),
					"/riskey/" + tempFileNames[j]);
			if (!file.exists()) {
				if (!file.mkdirs()) {
					Log.e("TravellerLog :: ", "Problem creating folder");
					ret = false;
				}
			}
		}
		return ret;
	}

	// Backup
	private class backupParam extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (ShellInterface.isSuAvailable()) {
				ShellInterface.setShell("su");
				long date = System.currentTimeMillis();
				SimpleDateFormat format = new SimpleDateFormat("yyMMdd-HHmm",
						Locale.getDefault());
				String tanggal = format.format(date);
				File dir = new File(Environment.getExternalStorageDirectory(),
						"/riskey/param/backup/");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String nama = new String(dir.toString() + "/param-" + tanggal
						+ ".lfs");
				String command = new String("busybox dd if=" + Global.partisi
						+ " of=" + nama);
				ShellInterface.runCommand("umount /mnt/.lfs");
				ShellInterface.runCommand(command);
				ShellInterface.runCommand("mount "+Global.partisi+" /mnt/.lfs");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			mManager = (NotificationManager) getApplicationContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent(getApplicationContext(), getClass());
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, intent, 0);
			mBuilder = new NotificationCompat.Builder(getApplicationContext())
					.setTicker(getString(R.string.backupcom))
					.setContentTitle(getString(R.string.app_name))
					.setContentText(getString(R.string.backupcom))
					.setWhen(System.currentTimeMillis()).setOngoing(false)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(contentIntent);
			mNotification = mBuilder.build();
			mNotification.defaults = Notification.DEFAULT_ALL;
			int NOTIF_ID = 14;
			mManager.notify(NOTIF_ID, mNotification);
		}

	}

	private class restoreParam extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (ShellInterface.isSuAvailable()) {
				ShellInterface.setShell("su");
				ShellInterface.runCommand("umount /mnt/.lfs");
				String command = new String("busybox dd of=" + Global.partisi
						+ " if=" + Global.fbackup);
				ShellInterface.runCommand(command);
				ShellInterface.runCommand("mount "+Global.partisi+" /mnt/.lfs");				
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			mManager = (NotificationManager) getApplicationContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent(getApplicationContext(), getClass());
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, intent, 0);
			mBuilder = new NotificationCompat.Builder(getApplicationContext())
					.setTicker(getString(R.string.restorecom))
					.setContentTitle(getString(R.string.app_name))
					.setContentText(getString(R.string.restorecom))
					.setWhen(System.currentTimeMillis()).setOngoing(false)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(contentIntent);
			mNotification = mBuilder.build();
			mNotification.defaults = Notification.DEFAULT_ALL;
			int NOTIF_ID = 15;
			mManager.notify(NOTIF_ID, mNotification);
		}

	}

	// DownloadJSON AsyncTask
	private class DownloadJSON extends AsyncTask<Void, Void, Void> {
		
		SharedPreferences prefs = getSharedPreferences("paramprefs",
				Context.MODE_PRIVATE);			
		String model = prefs.getString("model", null);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a progressdialog
			mProgressDialog = new ProgressDialog(MainActivity.this);
			// Set progressdialog title
			mProgressDialog.setTitle(R.string.app_name);
			// Set progressdialog message
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			// Show progressdialog
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Create an array			
			arraylist = new ArrayList<HashMap<String, String>>();
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("model", model));
			// Retrieve JSON Objects from the given URL address
			jsonobject = JSONfunctions
					.getJSONfromURL("http://192.168.18.1/tarik.php", "GET", postParameters);

			try {
				// Locate the array name in JSON
				jsonarray = jsonobject.getJSONArray(model);

				for (int i = 0; i < jsonarray.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					jsonobject = jsonarray.getJSONObject(i);
					// Retrive JSON Objects
					map.put("title", jsonobject.getString("title"));
					map.put("user", jsonobject.getString("user"));
					map.put("md5sum", jsonobject.getString("md5sum"));
					map.put("preview", jsonobject.getString("preview"));
					map.put("link", jsonobject.getString("link"));
					map.put("id", jsonobject.getString("id"));
					// Set the JSON Objects into the array
					arraylist.add(map);
				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			// Locate the listview in listview_main.xml
			listview = (ListView) findViewById(R.id.listview);
			// Pass the results into ListViewAdapter.java
			adapter = new ListViewAdapter(MainActivity.this, arraylist);
			// Set the adapter to the ListView
			listview.setAdapter(adapter);
			// Close the progressdialog
			mProgressDialog.dismiss();
		}
	}

}
