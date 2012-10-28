package mobile.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListView extends ListActivity {
	ArrayList<String> listItems  = new ArrayList<String>();
	ArrayList<String> listItems2 = new ArrayList<String>(); 
	ArrayAdapter<String> adapter;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Cursor mCursor;

		List <Map<String,String>> data = new ArrayList<Map<String,String>>();
		HashMap<String, String> map;

		getListView().setBackgroundColor(Color.rgb(205, 133, 63));

		mCursor = getContentResolver().query(LocationProvider.CONTENT_URI, null, null, null, null);
		while (mCursor.moveToNext() == true) {
			listItems.add(mCursor.getString(1));
			listItems2.add(mCursor.getString(2));
			map = new HashMap<String, String>();
			map.put("address", mCursor.getString(1));
			map.put("rest", mCursor.getString(2));
			data.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(
				this, data, 
				android.R.layout.simple_list_item_2, 
				new String[]{"address","rest"}, 
				new int[] {android.R.id.text1, android.R.id.text2});
		setListAdapter(adapter);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mymenu, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, MapViewActivity.class);
		Cursor mCursor = getContentResolver().query(LocationProvider.CONTENT_URI, null, null, null, null);

		switch (item.getItemId()) {
		case R.id.clear:
			setListAdapter(null);
			getContentResolver().delete(LocationProvider.CONTENT_URI, null, null);
			break;
		case R.id.tracks:
			startActivity(intent);
			break;
		case R.id.exit:
			setResult(RESULT_OK);
			finish();
		}
		return false;
	}

	@Override protected void onListItemClick(android.widget.ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String address = null; 
		Intent intent = new Intent(this, MapViewActivity.class); 
		Cursor mCursor = getContentResolver().query(LocationProvider.CONTENT_URI, null, null, null, null);

		while(mCursor.moveToNext() == true){
			if (mCursor.getPosition() == position) {
				address = mCursor.getString(1) + " " + mCursor.getString(2) + " " + mCursor.getString(3);
				break; 
			}
		}
		intent.putExtra("myAddress", address);
		startActivity(intent);
	}	
}
