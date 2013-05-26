package uw.cse.dineon.library.image;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLLite helper for retrieving local images.
 * @author mhotan
 */
class ImageSQLiteHelper extends SQLiteOpenHelper {

	private static final String TAG = ImageSQLiteHelper.class.getSimpleName();
	
	/**
	 * Name of the SQL Lite table that contains the images.
	 */
	public static final String TABLE_IMAGES = "dineonimages";

	// Column of the SQL ID of the image
	public static final String COLUMN_ID = "_id";
	// Parse ID where Image was received
	// This is our unique identifier
	public static final String COLUMN_PARSEID = "parseid";
	// Columnn for when the image was last updated
	public static final String COLUMN_LAST_UDPATED = "last_updated";
	// Column for when the image was last used.
	public static final String COLUMN_LAST_USED = "last_used";
	// Column for actual image storage
	public static final String COLUMN_IMG = "image";

	private static final String DATABASE_NAME = "images.db";
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_CREATE = "create table " + TABLE_IMAGES 
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_PARSEID + " text not null, "
			+ COLUMN_LAST_UDPATED + " long, "
			+ COLUMN_LAST_USED + " long, "
			+ COLUMN_IMG + " BLOB);";

	/**
	 * Creates a SQL Lite helper for accessing the image database.
	 * @param ctx Context to create the database in.
	 */
	ImageSQLiteHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
		onCreate(db);
	}
	
}
