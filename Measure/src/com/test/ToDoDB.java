package com.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;

//database
public class ToDoDB extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "Measure_DB_2015";
    private final static int DATABASE_VERSION = 2;

    private final static String TABLE_NAME_Infor = "measure_infor";
    public final static String FIELD_id = "_id";
    public final static String Gun_N = "gun_num";
    public final static String Gun_T = "gun_type";
    public final static String Gun_L = "gun_len";
    public final static String Gun_CL = "gun_cl";
    public final static String Gun_O = "gun_offer";
    public final static String Gun_D = "gun_direct";
    public final static String Gun_OF = "gun_offset";

    private final static String TABLE_NAME_Data = "measure_data";
    public final static String Gun_Offset_X = "Gun_Offset_X";
    public final static String Gun_Offset_Y = "Gun_Offset_Y";
    public final static String Gun_Data_Time = "Gun_Data_Time";

    private final static String TABLE_NAME_BLUET = "bluetooth_infor";
    public final static String Blue_T_ADD = "Blue_address";
    public final static String BLue_T_Name = "Blue_name";
    public final static String BLue_T_Selected = "Blue_Selected";

    private final static String TABLE_NAME_Language = "language_infor";
    public final static String Lan_Name = "Language_name";
    public final static String Lan_Current = "Language_current";

    private final static String TABLE_NAME_Sound = "sound_infor";
    public final static String Sound_Name = "sound_name";
    public final static String Sound_Current = "sound_current";

    private final static String TABLE_NAME_ENCODE = "Encode_infor";
    public final static String Encode_Name = "Encode_name";
    public final static String Encode_Unit = "Encode_Unit";



    public ToDoDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        //Guninfor table
        String sqlinfor = "CREATE TABLE " + TABLE_NAME_Infor + " (" + FIELD_id
                + " INTEGER primary key autoincrement, " + " " + Gun_N
                + " text, "+Gun_T+ " text, "+Gun_L+ " text, "+Gun_CL+ " text, "+Gun_O+ " text, "+Gun_D+" text, "+Gun_OF+ " text"+")";
        db.execSQL(sqlinfor);
        //gun data table
        String sqldata = "CREATE TABLE " + TABLE_NAME_Data + " (" + FIELD_id
                + " INTEGER primary key autoincrement, " + " " + Gun_N
                + " text, "+Gun_Data_Time+ " text, "+Gun_L+ " text, "+Gun_CL+ " text, "+Gun_Offset_X+ " text, "+Gun_Offset_Y+ " text"+")";
        db.execSQL(sqldata);
        //bluetooth table
        String sqlblueinfor = "CREATE TABLE " + TABLE_NAME_BLUET + " (" + FIELD_id
                + " INTEGER primary key autoincrement, " + " " +Blue_T_ADD + " text, "+BLue_T_Name+  " text, "+BLue_T_Selected+" text"+")";
        db.execSQL(sqlblueinfor);
        //language table
        String sqllanguage = "CREATE TABLE " + TABLE_NAME_Language + " (" + FIELD_id  +
                " INTEGER primary key autoincrement, " + " " + Lan_Name  + " text, "+Lan_Current+ " text"+")";
        db.execSQL(sqllanguage);
        //sound table
        String sqlsound = "CREATE TABLE " + TABLE_NAME_Sound + " (" + FIELD_id  +
                " INTEGER primary key autoincrement, " + " " + Sound_Name  + " text, "+Sound_Current+ " text"+")";
        db.execSQL(sqlsound);
        //init(db);
        String sqlEncode = "CREATE TABLE " + TABLE_NAME_ENCODE + " (" + FIELD_id  +
                " INTEGER primary key autoincrement, " + " " + Encode_Name  + " text, "+Encode_Unit+ " text"+")";
        db.execSQL(sqlEncode);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlinfor = "DROP TABLE IF EXISTS " + TABLE_NAME_Infor;
        db.execSQL(sqlinfor);
        String sqldata = "DROP TABLE IF EXISTS " + TABLE_NAME_Data;
        db.execSQL(sqldata);
        String sqlblu = "DROP TABLE IF EXISTS " + TABLE_NAME_BLUET;
        db.execSQL(sqlblu);
        String sqllan = "DROP TABLE IF EXISTS " + TABLE_NAME_Language;
        db.execSQL(sqllan);
        String sqlsound = "DROP TABLE IF EXISTS " + TABLE_NAME_Sound;
        db.execSQL(sqlsound);
        String sqlEncode = "DROP TABLE IF EXISTS " + TABLE_NAME_ENCODE;
        db.execSQL(sqlEncode);
        onCreate(db);
    }

    public void init( SQLiteDatabase db)
    {
        long row;
        ContentValues cv = new ContentValues();
        Cursor myCursor;
        //gun infor table
        myCursor=select(TABLE_NAME_Infor);
        if (myCursor.getCount()<1) {
            cv.put(Gun_N, "3345");
            cv.put(Gun_T, "包胶软辊");
            cv.put(Gun_L, "0000");
            cv.put(Gun_CL, "00000");
            cv.put(Gun_O, "昆山");
            cv.put(Gun_D, "0000");
            row = db.insert(TABLE_NAME_Infor, Gun_N, cv);
        }
        //gun data table
        myCursor=select(TABLE_NAME_Data);
        if (myCursor.getCount()<1) {
            cv.clear();
            cv.put(Gun_N, "0000");
            cv.put(Gun_Data_Time, "0000");
            cv.put(Gun_L, "0000");
            cv.put(Gun_CL, "50");
            cv.put(Gun_Offset_X, "161");
            cv.put(Gun_Offset_Y, "1848");
            row = db.insert(TABLE_NAME_Data, Gun_N, cv);
        }
        //bluetooth table
        myCursor=select(TABLE_NAME_BLUET);
        if (myCursor.getCount()<1) {
            cv.clear();
            cv.put(Blue_T_ADD, "0000");
            cv.put(BLue_T_Name, "0000");
            row = db.insert(TABLE_NAME_BLUET, Blue_T_ADD, cv);
        }
        //bluetooth table
        myCursor=select(TABLE_NAME_Language);
        if (myCursor.getCount()<1) {
        cv.clear();
        cv.put(Lan_Name, "简体中文");
        cv.put(Lan_Current, "1");
        row = db.insert(TABLE_NAME_Language, Lan_Name, cv);
        }

        //Encode table
        myCursor=select(TABLE_NAME_ENCODE);
        if (myCursor.getCount()<1) {
            cv.clear();
            cv.put(Encode_Name, "173.0");
            cv.put(Encode_Unit, "mm");
            row = db.insert(TABLE_NAME_ENCODE, Encode_Name, cv);
        }

    }

    public long InsertLanguage(String Language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(Lan_Name, Language);
        cv.put(Lan_Current, "1");
        return db.insert(TABLE_NAME_Language, Lan_Name, cv);
    }

    public long InsertSound(String Sound) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(Sound_Name, Sound);
        cv.put(Sound_Current, "1");
        return db.insert(TABLE_NAME_Sound, Sound_Name, cv);
    }
    public long InsertEncode(String Encode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(Encode_Name, Encode);
        cv.put(Encode_Unit, "mm");
        return db.insert(TABLE_NAME_ENCODE, Encode_Name, cv);
    }
    //get all the data from one table
    public Cursor select(String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }
    //select distict value GunN
    public Cursor selectGunType(String GunN) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] whereValue = { GunN};
        return  db.rawQuery("select gun_type,gun_len,gun_cl from measure_infor where gun_num= ?", whereValue);

    }
    public Cursor selectBlue() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] whereValue = {  Integer.toString(1)};
        return  db.rawQuery("select * from bluetooth_infor where Blue_Selected= ?", whereValue);
    }

    public Cursor selectOldBlue(String BlueAdd) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] whereValue = { BlueAdd};
        return  db.rawQuery("select Blue_address,Blue_name from bluetooth_infor where Blue_address= ?", whereValue);

    }

    //select distict value GunN
    public Cursor selectGunN() {
        SQLiteDatabase db = this.getReadableDatabase();
        return  db.rawQuery("select distinct gun_num from measure_data", null);
    }
    //select distict value GunData Total Number
    public Cursor selectGunDatatTotalNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        return  db.rawQuery("select distinct Gun_Data_Time from measure_data", null);
    }

    //select distict Xvalue Yvalue
    public Cursor selectGunDataXY(String GunNumber, String Gun_date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] GunNDateValue = { GunNumber,Gun_date};
        if(GunNumber.equals("")||Gun_date.equals(""))
        {
            return null;
        }
        return  db.rawQuery("select distinct Gun_Offset_X,Gun_Offset_Y from measure_data where gun_num = ? and Gun_Data_Time=?", GunNDateValue);
    }

    //select distict time
    public Cursor selectDateT(String GunN) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] whereValue = { GunN};
        if(GunN.equals(""))
        {
            return null;
        }
        return  db.rawQuery("select distinct Gun_Data_Time from measure_data where gun_num = ?", whereValue);
    }

    //get count
    public Cursor selectGunCount( String value_where) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] whereValue = { value_where};
        return   db.rawQuery("select distinct _id from measure_infor where gun_num = ?", whereValue);
    }

    //insert bluetooth information
    public long insertblue(String BlueAddress,String BlueName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Blue_T_ADD, BlueAddress);
        cv.put(BLue_T_Name, BlueName);
        cv.put(BLue_T_Selected, "1");
        return db.insert(TABLE_NAME_BLUET, Blue_T_ADD, cv);
    }

    //insert data
    public long insert(String TABLE_NAME,String GunNtext,String GunTtext,String GunLtext,String GunCLtext,String GunOtext,String GunDtext) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //two tables must be same six
        if (TABLE_NAME.equals(TABLE_NAME_Infor)) {
            cv.put(Gun_N, GunNtext);
            cv.put(Gun_T, GunTtext);
            cv.put(Gun_L, GunLtext);
            cv.put(Gun_CL, GunCLtext);
            cv.put(Gun_O, GunOtext);
            cv.put(Gun_D, GunDtext);
            cv.put(Gun_OF, "");
        }
        else
        {
            cv.put(Gun_N, GunNtext);
            cv.put(Gun_Data_Time, GunTtext);
            cv.put(Gun_L, GunLtext);
            cv.put(Gun_CL, GunCLtext);
            cv.put(Gun_Offset_X, GunOtext);
            cv.put(Gun_Offset_Y, GunDtext);
        }
        long row = db.insert(TABLE_NAME, Gun_N, cv);
        return row;
    }

    //delete certain values
    public void delete(String TABLE_NAME, String key_where, String value_where) {
        SQLiteDatabase db = this.getWritableDatabase();

		String where = key_where + " = ?";
		String[] whereValue = { value_where };
		db.delete(TABLE_NAME, where, whereValue);
    }

    public void updatelan( String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = Lan_Current + " = ?";
		String[] whereValue = { Integer.toString(1) };
		ContentValues cv = new ContentValues();
		cv.put(Lan_Name, text);
		db.update(TABLE_NAME_Language, cv, where, whereValue);
    }
    public void updateEncode( String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = Encode_Unit + " = ?";
        String[] whereValue = { "mm" };
        ContentValues cv = new ContentValues();
        cv.put(Encode_Name, text);
        db.update(TABLE_NAME_ENCODE, cv, where, whereValue);
    }
    public void updateSound( String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = Sound_Current + " = ?";
        String[] whereValue = { Integer.toString(1) };
        ContentValues cv = new ContentValues();
        cv.put(Sound_Name, text);
        db.update(TABLE_NAME_Sound, cv, where, whereValue);
    }

    public void updateblue( String blueadd, String BlueName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = BLue_T_Selected + " = ?";
        String[] whereValue = { Integer.toString(1) };
        ContentValues cv = new ContentValues();
        cv.put(Blue_T_ADD, blueadd);
        cv.put(BLue_T_Name, BlueName);
        db.update(TABLE_NAME_BLUET, cv, where, whereValue);
    }

    public void updateoffset( String text,String offset ) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_id + " = ?";
        String[] whereValue = {text };
        ContentValues cv = new ContentValues();
        cv.put(Gun_OF, offset);
        db.update(TABLE_NAME_Infor, cv, where, whereValue);
    }

}
