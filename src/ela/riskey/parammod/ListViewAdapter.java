package ela.riskey.parammod;

import java.util.ArrayList;
import java.util.HashMap;

import ela.riskey.parammod.MainActivity.Global;
import ela.riskey.parammod.utils.ImageLoader;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	// Declare Variables
	Context context;
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	ImageLoader imageLoader;
	HashMap<String, String> resultp = new HashMap<String, String>();

	public ListViewAdapter(Context context,
			ArrayList<HashMap<String, String>> arraylist) {
		this.context = context;
		data = arraylist;
		imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// Declare Variables
		TextView judul;
		TextView pembuat;
		TextView md5sum;
		TextView code;
		ImageView preview;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.listview_item, parent, false);
		// Get the position
		resultp = data.get(position);

		// Locate the TextViews in listview_item.xml
		judul = (TextView) itemView.findViewById(R.id.judul);
		pembuat = (TextView) itemView.findViewById(R.id.pembuat);
		md5sum = (TextView) itemView.findViewById(R.id.md5);
		code = (TextView) itemView.findViewById(R.id.code);

		// Locate the ImageView in listview_item.xml
		preview = (ImageView) itemView.findViewById(R.id.preview);

		// Capture position and set results to the TextViews
		judul.setText(resultp.get(MainActivity.TITLE));
		pembuat.setText(resultp.get(MainActivity.USER));
		md5sum.setText(resultp.get(MainActivity.MD5SUM));
		code.setText(resultp.get(MainActivity.ID));
		// Capture position and set results to the ImageView
		// Passes flag images URL into ImageLoader.class
		imageLoader.DisplayImage(resultp.get(MainActivity.PREVIEW), preview);
		// Capture ListView item click
		itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get the position
				resultp = data.get(position);
				Intent intent = new Intent(context, SingleItemView.class);
				// judul
				intent.putExtra("judul", resultp.get(MainActivity.TITLE));
				// pembuat
				intent.putExtra("pembuat", resultp.get(MainActivity.USER));
				// md5sum
				intent.putExtra("md5sum", resultp.get(MainActivity.MD5SUM));
				// preview
				intent.putExtra("preview", resultp.get(MainActivity.PREVIEW));
				// link
				intent.putExtra("link", resultp.get(MainActivity.LINK));
				// code
				intent.putExtra("code", resultp.get(MainActivity.ID));
				// partisi
				intent.putExtra("partisi", Global.partisi);
				// filepath
				String filepath = Environment.getExternalStorageDirectory()
						.toString()
						+ "/riskey/param/"
						+ resultp.get(MainActivity.ID) + ".lfs";
				intent.putExtra("filepath", filepath);
				// Start SingleItemView Class
				context.startActivity(intent);

			}
		});
		return itemView;
	}
}
