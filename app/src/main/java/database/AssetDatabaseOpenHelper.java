package database;


import android.database.sqlite.SQLiteDatabase;

import com.Red.PSTAR_app.utils.PSTARApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetDatabaseOpenHelper {
    private static final String TAG = AssetDatabaseOpenHelper.class.getSimpleName();
    private final String DB_NAME;

    public AssetDatabaseOpenHelper(String database_name) {
        this.DB_NAME = database_name;
    }

    public SQLiteDatabase saveDatabase() {
        File dbFile = PSTARApp.getInstance().getDatabasePath(DB_NAME);
        if (!dbFile.exists()) {
            try {
                return copyDatabase(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }

        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    private SQLiteDatabase copyDatabase(File dbFile) throws IOException {

        InputStream is = PSTARApp.getInstance().getAssets().open(DB_NAME);
        OutputStream os = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }
        os.flush();
        os.close();
        is.close();
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        sqLiteDatabase.needUpgrade(24);
        return sqLiteDatabase;
    }

}
