package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bean.LocationDTO;

public class LocationOperation {

    // Database fields
    private DataBaseWrapper dbHelper;
    private String[] STUDENT_TABLE_COLUMNS = { DataBaseWrapper.LOCATION_ID,

            DataBaseWrapper.LOCATION_NAME};
    private SQLiteDatabase database;

    public LocationOperation(Context context) {
        dbHelper = new DataBaseWrapper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        database.close();
    }
//faster database insert operation

    public void addLocation(LocationDTO locationDTO) {
        database.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.LOCATION_NAME, locationDTO.getmLocationName());
        values.put(DataBaseWrapper.LOCATION_LAT, locationDTO.getmLat()+"");
        values.put(DataBaseWrapper.LOCATION_LONG, locationDTO.getmLong()+"");
        values.put(DataBaseWrapper.LOCATION_IMG, locationDTO.getmLocationImage());
        values.put(DataBaseWrapper.LOCATION_ADDRESS, locationDTO.getAddress());
        database.insert(DataBaseWrapper.MY_LOCATIONS, null, values);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public List getAllLocations() {
        List books = new LinkedList();

        // select book query
        String query = "SELECT  * FROM " + DataBaseWrapper.MY_LOCATIONS+
        " ORDER BY "+ DataBaseWrapper.LOCATION_ID + " DESC";

        // get reference of the BookDB database
        Cursor cursor = database.rawQuery(query, null);

        // parse all results
        LocationDTO locationDTO = null;
        if (cursor.moveToFirst()) {
            do {
                locationDTO = new LocationDTO();
                locationDTO.setmLocationID(cursor.getInt(0));
                locationDTO.setmLocationName(cursor.getString(1));
                locationDTO.setmLocationImage(Integer.parseInt(cursor.getString(3)));
                locationDTO.setmLat(Double.parseDouble(cursor.getString(2)));
                locationDTO.setmLong(Double.parseDouble(cursor.getString(4)));
                locationDTO.setAddress(cursor.getString(5));
                books.add(locationDTO);
            } while (cursor.moveToNext());
        }
        return books;
    }

    public int deleteLocation(int locationID){
        return database.delete(DataBaseWrapper.MY_LOCATIONS,
                DataBaseWrapper.LOCATION_ID+"=?",
                new String[] { String.valueOf(locationID) });
    }

    public int update(String locationName, LocationDTO locationDTO) {
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.LOCATION_NAME, locationName);

        return database.update(DataBaseWrapper.MY_LOCATIONS,
                values,
                DataBaseWrapper.LOCATION_ID+"=?",
                new String[] { String.valueOf(locationDTO.getmLocationID()) });
    }
}

