package mobile.group;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
	Button crumb, location, networkProvider, gps; 
	TextView locationText;
	LocationManager lm;	
	LocationListener ll;
	
	// for Content Provider
	Uri mNewUri;
	ContentValues mNewValues = new ContentValues();
	Cursor mCursor;
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) 
				finish(); 
		}
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		crumb = (Button) findViewById(R.id.crumb);
		crumb.setText("Drop Cookie Crumb...");
		crumb.setTextColor(Color.WHITE);
		crumb.setBackgroundResource(R.drawable.cookie);

		/*animation*/
		crumb.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				v.setBackgroundResource(R.drawable.cookie2);
				return false;
			}
		});

		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		}

		location = (Button) findViewById(R.id.location);
		locationText = (TextView) findViewById(R.id.locationText);

		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		ll = new LocationListener() {
			@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
			@Override public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(), "Waiting for location", Toast.LENGTH_SHORT).show();

			}
			@Override public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
			}
			@Override public void onLocationChanged(Location location) {

				Geocoder geo = new Geocoder(getApplicationContext());
				List<Address> addresses = null;
				boolean isInCPalready = true; 

				try {
					addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
				} catch (IOException e) { e.printStackTrace(); }

				if(addresses != null) {	
					// counter++;
					if(addresses.size() > 0 && addresses.get(0).getMaxAddressLineIndex() >= 0) {	
						locationText.setText(addresses.get(0).getAddressLine(0));
						String zero = addresses.get(0).getAddressLine(0);
						String one = addresses.get(0).getAddressLine(1); 
						String two = addresses.get(0).getAddressLine(2); 

						mCursor = getContentResolver().query(LocationProvider.CONTENT_URI, null, null, null, null);
						while (mCursor.moveToNext() == true) {
							if (mCursor.getString(1).equals(zero) &&
									mCursor.getString(2).equals(one) &&
									mCursor.getString(3).equals(two)) {
								// if address is already in Content Provider, DO NOT add it to Content Provider
								isInCPalready = false; 
								break;
							}								
						}
					}
				}
				if (isInCPalready == true) {
					/* insert values into database */
					mNewValues.put(LocationProvider.COLUMN_STREET, addresses.get(0).getAddressLine(0));
					mNewValues.put(LocationProvider.COLUMN_CITY, addresses.get(0).getAddressLine(1));
					mNewValues.put(LocationProvider.COLUMN_ZIP, addresses.get(0).getAddressLine(2));
					mNewUri = getContentResolver().insert(LocationProvider.CONTENT_URI, mNewValues);

					lm.removeUpdates(this);
				}
			}
		};
	}

	/*animation*/
	public void crumb (View v) {
		crumb.setBackgroundResource(R.drawable.cookie);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		crumb.setBackgroundResource(R.drawable.crumb);
		crumb.setText("");

		TranslateAnimation anim;
		anim = new TranslateAnimation(40,0,40,450);
		anim.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				crumb.setBackgroundResource(R.drawable.cookie);
				crumb.setText("Drop Cookie Crumb...");
			}
		});

		anim.setDuration(6000);
		crumb.startAnimation(anim);

		//Alert user location update in progress and get location 
		Toast.makeText(getApplicationContext(), "Waiting for  location ...", Toast.LENGTH_SHORT).show();
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
	}

	public void location (View v) { 
		Intent intent = new Intent(Main.this, ListView.class);
		startActivityForResult(intent, 1);
	}
	
	@Override public void onConfigurationChanged(Configuration newconfig){
		super.onConfigurationChanged(newconfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}	
}