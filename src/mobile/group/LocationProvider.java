package mobile.group;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LocationProvider extends ContentProvider {

	public final static String DBNAME = "LocationsDatabase";
	public final static String TABLE_NAMESTABLE = "Locations";
	public final static String ID = "_id";
	public final static String COLUMN_STREET = "street";
	public final static String COLUMN_CITY = "city";
	public final static String COLUMN_ZIP = "zip";	
	private static final String SQL_CREATE_MAIN = 
		"CREATE TABLE " +
		TABLE_NAMESTABLE +                       
		"(" +                         
		" _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COLUMN_STREET + " TEXT," +
		COLUMN_CITY   + " TEXT," + 
		COLUMN_ZIP    + " TEXT)";
		
	private MainDatabaseHelper mOpenHelper;
	public static final Uri CONTENT_URI = Uri.parse("content://mobile.group.provider/" + TABLE_NAMESTABLE);
	
	@Override public int delete(Uri uri, String whereClause, String[] whereArgs) {
		return mOpenHelper.getWritableDatabase().delete(TABLE_NAMESTABLE, whereClause, whereArgs);
	}

	@Override public String getType(Uri arg0) { return null; }

	@Override public Uri insert(Uri uri, ContentValues values) {
		long id = mOpenHelper.getWritableDatabase().insert(TABLE_NAMESTABLE, null, values);
		return Uri.withAppendedPath(CONTENT_URI, "" + id);
	}

	@Override public boolean onCreate() {
		mOpenHelper = new MainDatabaseHelper(getContext());
		return true;
	}

	@Override public Cursor query(Uri table, String[] columns, String selection, String[] args, String orderBy) {
		return mOpenHelper.getReadableDatabase().query(TABLE_NAMESTABLE, columns, selection, args, null, null, orderBy);
	}

	@Override public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) { return 0; }
	
	protected static final class MainDatabaseHelper extends SQLiteOpenHelper {
		MainDatabaseHelper(Context context) { super(context, TABLE_NAMESTABLE, null, 1); }
		@Override public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE_MAIN); }
		@Override public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
	}
}
