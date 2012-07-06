package mobile.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapViewActivity extends MapActivity {
	MapView map;     MapController mc; LocationManager lm; GeoPoint geoPoint;
	Drawable marker; Geocoder coder;   GeoPoint p1;       List<Address> address;

	//Create an overlay class
	class MyOverlay extends ItemizedOverlay<OverlayItem> {
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		public MyOverlay(Drawable drawable) {
			super(drawable);
			boundCenterBottom(drawable);

			//This is where we add all of the locations to the map
			Cursor mCursor = getContentResolver().query(LocationProvider.CONTENT_URI, null, null, null, null);

			Bundle extras = getIntent().getExtras();
			if (extras != null) {
					String location = extras.getString("myAddress");

					try {
						address = coder.getFromLocationName(location,10);
						Address newlocation = address.get(0);
						newlocation.getLatitude(); 
						newlocation.getLongitude(); 

						p1 = new GeoPoint((int) (newlocation.getLatitude() * 1E6), 
								(int) (newlocation.getLongitude() * 1E6)); 

						items.add(new OverlayItem(p1, "Location 1", location));
					} catch (IOException e) { e.printStackTrace(); }
			}
			else {
				while(mCursor.moveToNext() == true){
					String location = mCursor.getString(1) + " " + mCursor.getString(2) + " " + mCursor.getString(3);

					try {
						address = coder.getFromLocationName(location,10);
						Address newlocation = address.get(0);
						newlocation.getLatitude(); 
						newlocation.getLongitude(); 

						p1 = new GeoPoint((int) (newlocation.getLatitude() * 1E6), 
								(int) (newlocation.getLongitude() * 1E6)); 

						items.add(new OverlayItem(p1, "Location 1", location));
					} catch (IOException e) { e.printStackTrace(); } 
				}
			}
			populate();	
		}

		@Override
		protected OverlayItem createItem(int index) {
			return items.get(index);
		}

		@Override
		protected boolean onTap(int i) {
			Toast.makeText(MapViewActivity.this,
					items.get(i).getSnippet(),
					Toast.LENGTH_SHORT).show();

			return(true);
		}

		@Override public int size() { return items.size(); }
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplayout);
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		Cursor mCursor = getContentResolver().query(LocationProvider.CONTENT_URI, null, null, null, null);

		coder = new Geocoder(this);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		map = (MapView) findViewById(R.id.mapView);
		mc = map.getController();

		map.setBuiltInZoomControls(true);
		
		// to center it
		while(mCursor.moveToNext() == true){
			String location = mCursor.getString(1) + " " + mCursor.getString(2) + " " + mCursor.getString(3);

			try {
				address = coder.getFromLocationName(location,10);
				Address newlocation = address.get(0);
				newlocation.getLatitude(); 
				newlocation.getLongitude(); 

				p1 = new GeoPoint((int) (newlocation.getLatitude() * 1E6), 
						(int) (newlocation.getLongitude() * 1E6)); 

				items.add(new OverlayItem(p1, "Location 1", "Location " + location));
			} catch (IOException e) { e.printStackTrace(); }
		}

		geoPoint = new GeoPoint((int) (30.446142 * 1E6), (int) (-84.299673 * 1E6));
		mc.setCenter(p1);
		marker = getResources().getDrawable(R.drawable.crumby);
		marker.setBounds(105, 105, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		mc.setZoom(17);
		map.getOverlays().add(new MyOverlay(marker));
	}

	@Override protected boolean isRouteDisplayed() { return false; }

}
