package com.example.myapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {
    // burdaki metotlarla DatabaseHelper classındaki metotları birbirine
    // bağlayacaz.

    static final String PROVIDER_NAME = "com.example.myapplication.ArtContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/arts";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String NAME = "name";
    static final String IMAGE = "image";

    static final int ARTS = 1;
    static final UriMatcher uriMatcher;
    // gelen bilgileri tablolarla bunun sayesinde eşleştiririm.

    static{
        // BU SINIF İÇERİSİNDE DİĞER SINIFLARDAN HERHANGİ BİR DEĞİŞKENE
        // ULAŞTIĞIMDA İLK ÖNCE BURDAKİ uriMatcher KULLANILACAK.
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // ilk başta eşleşme yok.
        uriMatcher.addURI(PROVIDER_NAME,"arts",ARTS);
    }

    private static HashMap<String,String> ART_PROJECTION_MAP;



    // ------------------------------Database------------------------
    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME = "Arts";
    static final String ARTS_TABLE_NAME = "arts";
    static final int DATABASE_VERSİON = 1;
    static final String CREATE_TABLE_DATABASE_TABLE = "CREATE TABLE " +
            ARTS_TABLE_NAME + "(name TEXT NOT NULL," +
            // NULL OLMAYACAK.
            "image BLOB NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper{
        // bu sınıfla database işlemlerini daha rahat yapıcaz.
        public DatabaseHelper(@Nullable Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSİON);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            sqLiteDatabase.execSQL(CREATE_TABLE_DATABASE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // versiyon değiştiriyoruz.
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ARTS_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }     // ------------------------------Database------------------------


    @Override
    public boolean onCreate() {
        // bu sınıf çağırıldığında ne olacağını yazdığımız yer.

        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        // vtyi yaptık.


        return sqLiteDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;

            default:
        }

        if (sortOrder == null || sortOrder.matches("")){
            // NEYE GÖRE DİZİCEZ. BOŞ GELMESİNDE GÖRE.
            sortOrder = NAME;
            // İSME GÖRE DİZ.
        }

        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        // uri de bir değişiklik olursa onu takip etmek için kullandığımı metot.
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        long rowId = sqLiteDatabase.insert(ARTS_TABLE_NAME,"",contentValues);
        if (rowId > 0){
            // hata yoksa
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI,rowId);
            getContext().getContentResolver().notifyChange(newUri,null);

            return newUri;
        }
        throw new SQLException("Error");
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowCount = 0;

        switch (uriMatcher.match(uri)){
            case ARTS:

                rowCount = sqLiteDatabase.delete(ARTS_TABLE_NAME,selection,selectionArgs);

                break;

            default:
                throw new IllegalArgumentException("Failed URI");
                // bu hatayı veriyorsa uri eşleşememiş demektir.
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowCount = 0;

        switch (uriMatcher.match(uri)){
            case ARTS:

                rowCount = sqLiteDatabase.update(ARTS_TABLE_NAME,contentValues,selection,selectionArgs);

                break;

            default:
                throw new IllegalArgumentException("Failed URI");
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return rowCount;
    }
}
