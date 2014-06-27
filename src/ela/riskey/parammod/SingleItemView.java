package ela.riskey.parammod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnOptionsItemSelectedListener;
import com.actionbarsherlock.view.Menu;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import ela.riskey.parammod.MainActivity.Global;
import ela.riskey.parammod.utils.ImageLoader;
import ela.riskey.parammod.utils.MD5;
import ela.riskey.parammod.utils.ShellInterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TableRow;
import android.widget.TextView;

public class SingleItemView extends Activity implements
		OnCreateOptionsMenuListener, OnOptionsItemSelectedListener {
	ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);

	// Anu Dialog
	Builder mAlertDialog;

	// Declare Variables
	public static final int DOWNLOAD = 0;
	public static final int INSTALL = 1;
	public static final int HELP = 2;

	// Anu
	String judul;
	String pembuat;
	String md5sum;
	String preview;
	String link;
	String position;
	String filepath;
	String partisi;
	String code;
	ImageLoader imageLoader = new ImageLoader(this);
	ImageView my_image;

	// Anu Notif
	NotificationManager mManager;
	NotificationCompat.Builder mBuilder;
	Notification mNotification;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from singleitemview.xml
		mSherlock
				.setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		mSherlock.setContentView(R.layout.singleitemview);
		AdView adView = (AdView) findViewById(R.id.adview);
		adView.loadAd(new AdRequest());

		Intent i = getIntent();
		// Anu judul
		judul = i.getStringExtra("judul");
		// Anu pembuat
		pembuat = i.getStringExtra("pembuat");
		// Anu md5sum
		md5sum = i.getStringExtra("md5sum");
		// Anu preview
		preview = i.getStringExtra("preview");
		// Anu filepath
		filepath = i.getStringExtra("filepath");
		// Anu partisi
		partisi = i.getStringExtra("partisi");
		// Anu link
		link = i.getStringExtra("link");
		// Anu codename
		code = i.getStringExtra("code");

		// Locate the TextViews in singleitemview.xml
		TextView txtjudul = (TextView) findViewById(R.id.judul);
		TextView txtpembuat = (TextView) findViewById(R.id.pembuat);
		TextView txtmd5sum = (TextView) findViewById(R.id.md5);
		TextView txtcode = (TextView) findViewById(R.id.code);

		// Locate the ImageView in singleitemview.xml
		ImageView imgprev = (ImageView) findViewById(R.id.preview);

		// Set results to the TextViews
		txtjudul.setText(judul);
		txtpembuat.setText(pembuat);
		txtmd5sum.setText(md5sum);
		txtcode.setText(code);
		// Capture position and set results to the ImageView
		// Passes flag images URL into ImageLoader.class
		imageLoader.DisplayImage(preview, imgprev);
		Log.e(getPackageName(), link);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(Menu.NONE, DOWNLOAD, Menu.NONE, getString(R.string.download))
				.setIcon(R.drawable.ic_menu_backup)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(Menu.NONE, INSTALL, Menu.NONE, getString(R.string.install))
				.setIcon(R.drawable.ic_menu_archive)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(Menu.NONE, HELP, Menu.NONE, getString(R.string.help))
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
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case DOWNLOAD:
			download();
			break;
		case INSTALL:
			install();
			break;
		case HELP:
			help();
			break;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DOWNLOAD:
			download();
			break;
		case INSTALL:
			install();
			break;
		case HELP:
			help();
			break;
		}
		return false;
	}
	
	private void help() {
		// TODO Auto-generated method stub
		mAlertDialog = new AlertDialog.Builder(SingleItemView.this);
		mAlertDialog.setTitle(R.string.help).setNeutralButton(R.string.keluar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
		LayoutInflater inflater = getLayoutInflater();
		View troubleView = inflater.inflate(R.layout.help, null, false);
		TableRow refresh = (TableRow) troubleView.findViewById(R.id.tableRow3);
		refresh.setVisibility(View.GONE);
		TextView download = (TextView) troubleView.findViewById(R.id.backdown);
		download.setText(R.string.hdown);
		TextView install = (TextView) troubleView.findViewById(R.id.resinst);
		install.setText(R.string.hinstall);
		mAlertDialog.setView(troubleView);
		mAlertDialog.create().show();
	}

	private void download() {
		Intent i = getIntent();		
		final String link = i.getStringExtra("link");
		mAlertDialog = new AlertDialog.Builder(SingleItemView.this);
		mAlertDialog.setTitle(getString(R.string.app_name));
		mAlertDialog.setMessage(getString(R.string.wdown));
		mAlertDialog.setCancelable(false);
		mAlertDialog.setPositiveButton(R.string.ya, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new DownloadTask().execute(link);					
			}
		});
		mAlertDialog.setNegativeButton(R.string.tak, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();					
			}
		});
		mAlertDialog.create();
		mAlertDialog.show();
	}
	
	private void install() {		
		mAlertDialog = new AlertDialog.Builder(SingleItemView.this);
		mAlertDialog.setTitle(getString(R.string.app_name));
		mAlertDialog.setMessage(getString(R.string.winstall));
		mAlertDialog.setCancelable(false);
		mAlertDialog.setPositiveButton(R.string.ya, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				new installTask().execute();
			}
		});
		mAlertDialog.setNegativeButton(R.string.tak, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		mAlertDialog.create();
		mAlertDialog.show();
	}

	/**
	 * Background Async Task
	 * */
	class installTask extends AsyncTask<String, String, String> {

		Intent i = getIntent();
		String filepath = i.getStringExtra("filepath");
		String partisi = i.getStringExtra("partisi");
		String md5sum = i.getStringExtra("md5sum");
		String link = i.getStringExtra("link");
		String tmp = filepath + ".tmp";
		String command = "dd if=" + filepath + " of=" + partisi;
		File file = new File(filepath);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!file.exists()) {
				mAlertDialog = new AlertDialog.Builder(SingleItemView.this);
				mAlertDialog.setTitle(getString(R.string.app_name));
				mAlertDialog.setMessage(getString(R.string.wnotexist));
				mAlertDialog.setCancelable(false);
				mAlertDialog.setPositiveButton(getString(R.string.ya),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub								
								new DownloadTask().execute(link);
								cancel(true);
							}
						});
				mAlertDialog.setNegativeButton(getString(R.string.tak),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								cancel(true);
							}
						});
				mAlertDialog.create();
				mAlertDialog.show();
			} else {
				if (!MD5.checkMD5(md5sum, file)) {
					file.delete();
					Log.e("MD5", "Missmatch");
					mAlertDialog = new AlertDialog.Builder(SingleItemView.this);
					mAlertDialog.setTitle(getString(R.string.app_name));
					mAlertDialog.setMessage(getString(R.string.wdownagain));
					mAlertDialog.setCancelable(false);
					mAlertDialog.setPositiveButton(getString(R.string.ya),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									cancel(true);
									new DownloadTask().execute(link);								
								}
							});
					mAlertDialog.setNegativeButton(getString(R.string.tak),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									cancel(true);
								}
							});
					mAlertDialog.create();
					mAlertDialog.show();
				}
			}
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			if (ShellInterface.isSuAvailable()) {
				ShellInterface.runCommand("umount /mnt/.lfs");
				ShellInterface.runCommand(command);
				ShellInterface.runCommand("mount "+Global.partisi+" /mnt/.lfs");
				ShellInterface.runCommand("sleep 5");
			}
			return null;
		}

		@Override
		protected void onPostExecute(String arg1) {

			mManager = (NotificationManager) getApplicationContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent(getApplicationContext(), getClass());
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, intent, 0);
			mBuilder = new NotificationCompat.Builder(getApplicationContext())
					.setTicker(getString(R.string.installcom))
					.setOngoing(false)
					.setContentTitle(getString(R.string.app_name))
					.setContentText(getString(R.string.installcom))
					.setSmallIcon(R.drawable.ic_launcher)
					.setWhen(System.currentTimeMillis())
					.setContentIntent(contentIntent);
			mNotification = mBuilder.build();
			mNotification.defaults = Notification.DEFAULT_ALL;
			int NOTIF_ID = 10;
			mManager.notify(NOTIF_ID, mNotification);
		}

	}

	class DownloadTask extends AsyncTask<String, Integer, String> {

		Intent i = getIntent();
		String filepath = i.getStringExtra("filepath");
		String partisi = i.getStringExtra("partisi");
		String md5sum = i.getStringExtra("md5sum");
		String tmp = filepath + ".tmp";
		String command = "dd if=" + filepath + " of=" + partisi;
		File file = new File(filepath);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RemoteViews remoteView = new RemoteViews(getPackageName(),
					R.layout.status_bar);
			remoteView.setProgressBar(R.id.progress_bar, 100, 10, true);
			remoteView.setImageViewResource(R.id.appIcon,
					R.drawable.ic_launcher);
			remoteView
					.setTextViewText(R.id.title, getString(R.string.app_name));
			remoteView.setTextViewText(R.id.progress_text, " ");
			remoteView.setTextViewText(R.id.description,
					getString(R.string.downloading));
			mManager = (NotificationManager) getApplicationContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent(getApplicationContext(), getClass());
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, intent, 0);
			mBuilder = new NotificationCompat.Builder(getApplicationContext())
					.setContent(remoteView)
					.setTicker(getString(R.string.downloading))
					.setOngoing(true).setSmallIcon(R.drawable.ic_launcher)
					.setProgress(100, 0, true).setContentIntent(contentIntent);
			mNotification = mBuilder.build();
			mNotification.defaults = Notification.DEFAULT_ALL;
			mNotification.contentView = remoteView;
			int NOTIF_ID = 13;
			mManager.notify(NOTIF_ID, mNotification);
		}

		@SuppressLint("Wakelock")
		@Override
		protected String doInBackground(String... sUrl) {
			// take CPU lock to prevent CPU from going off if the user
			// presses the power button during download

			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(
					PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
			wl.acquire();			
			try {
				InputStream input = null;
				OutputStream output = null;
				HttpURLConnection connection = null;
				try {
					URL url = new URL(sUrl[0]);
					connection = (HttpURLConnection) url.openConnection();
					connection.connect();

					// expect HTTP 200 OK, so we don't mistakenly save error
					// report
					// instead of the file
					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
						return "Server returned HTTP "
								+ connection.getResponseCode() + " "
								+ connection.getResponseMessage();

					// this will be useful to display download percentage
					// might be -1: server did not report the length
					int fileLength = connection.getContentLength();					

					// download the file
					input = connection.getInputStream();
					output = new FileOutputStream(tmp);

					byte data[] = new byte[4096];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1) {
						// allow canceling with back button
						if (isCancelled()) {
						}
						total += count;
						// publishing the progress....
						if (fileLength > 0) // only if total length is known
							publishProgress((int) (total * 100 / fileLength));
						output.write(data, 0, count);
					}
				} catch (Exception e) {
					return e.toString();
				} finally {
					try {
						if (output != null)
							output.close();
						if (input != null)
							input.close();
					} catch (IOException ignored) {
					}

					if (connection != null)
						connection.disconnect();
				}
			} finally {
				wl.release();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			// if we get here, length is known, now set indeterminate to false
			RemoteViews remoteView = new RemoteViews(getPackageName(),
					R.layout.status_bar);
			remoteView.setProgressBar(R.id.progress_bar, 100, progress[0],
					false);
			remoteView.setImageViewResource(R.id.appIcon,
					R.drawable.ic_launcher);
			remoteView
					.setTextViewText(R.id.title, getString(R.string.app_name));
			remoteView.setTextViewText(R.id.progress_text,
					progress[0].toString() + "%");
			remoteView.setTextViewText(R.id.description,
					getString(R.string.downloading));
			mManager = (NotificationManager) getApplicationContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent(getApplicationContext(), getClass());
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, intent, 0);
			mBuilder = new NotificationCompat.Builder(getApplicationContext())
					.setContent(remoteView)
					.setTicker(getString(R.string.downloading))
					.setOngoing(true).setSmallIcon(R.drawable.ic_launcher)
					.setProgress(100, progress[0], false)
					.setContentIntent(contentIntent);
			mNotification = mBuilder.build();
			mNotification.contentView = remoteView;
			int NOTIF_ID = 13;
			mManager.notify(NOTIF_ID, mNotification);

		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			super.onPostExecute(file_url);
			File tmp1 = new File(tmp);
			Log.i("MD5", MD5.calculateMD5(tmp1) + "result");
			Log.i("MD5", md5sum + "ori");

			if (!MD5.checkMD5(md5sum, tmp1)) {
				tmp1.delete();
				Log.e("MD5", "mismatch");
				mManager = (NotificationManager) getApplicationContext()
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Intent intent = new Intent(getApplicationContext(), getClass());
				PendingIntent contentIntent = PendingIntent.getActivity(
						getApplicationContext(), 0, intent, 0);
				mBuilder = new NotificationCompat.Builder(
						getApplicationContext())
						.setTicker(getString(R.string.filekorup))
						.setContentTitle(getString(R.string.app_name))
						.setContentText(getString(R.string.filekorup))
						.setWhen(System.currentTimeMillis()).setOngoing(false)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentIntent(contentIntent);
				mNotification = mBuilder.build();
				mNotification.defaults = Notification.DEFAULT_ALL;
				int NOTIF_ID = 13;
				mManager.notify(NOTIF_ID, mNotification);
			} else {
				tmp1.renameTo(file);
				mManager = (NotificationManager) getApplicationContext()
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Intent intent = new Intent(getApplicationContext(), getClass());
				PendingIntent contentIntent = PendingIntent.getActivity(
						getApplicationContext(), 0, intent, 0);
				mBuilder = new NotificationCompat.Builder(
						getApplicationContext())
						.setTicker(getString(R.string.downloadcom))
						.setContentTitle(getString(R.string.app_name))
						.setContentText(getString(R.string.downloadcom))
						.setWhen(System.currentTimeMillis()).setOngoing(false)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentIntent(contentIntent);
				mNotification = mBuilder.build();
				mNotification.defaults = Notification.DEFAULT_ALL;
				int NOTIF_ID = 13;
				mManager.notify(NOTIF_ID, mNotification);				
			}

		}
	}
}