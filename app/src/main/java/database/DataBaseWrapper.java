package database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DataBaseWrapper extends SQLiteOpenHelper {

   public static final String MY_LOCATIONS = "Location";
   public static final String LOCATION_ID = "_id";
   public static final String LOCATION_NAME = "_name";
   public static final String LOCATION_LAT = "_lat";
   public static final String LOCATION_LONG = "_long";
   public static final String LOCATION_IMG = "_img";
   public static final String LOCATION_ADDRESS = "_address";

   private static final String DATABASE_NAME = "Students.db";
   private static final int DATABASE_VERSION = 1;

   // creation SQLite statement

   private static final String DATABASE_CREATE = "create table " + MY_LOCATIONS + "("
           + LOCATION_ID + "" +" integer primary key autoincrement, "
           + LOCATION_NAME + " text not null,"
           + LOCATION_LAT + " text not null,"
           + LOCATION_IMG + " int,"
           + LOCATION_LONG + " text not null,"
           + LOCATION_ADDRESS + " text not null" +
           ");";


   public DataBaseWrapper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   @Override
   public
   void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE);
   }
   @Override
   public
   void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + MY_LOCATIONS);
      onCreate(db);
   }

}