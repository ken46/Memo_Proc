package jp.ken46.memo_proc01;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBCreate extends SQLiteOpenHelper{
	
	private static final int DB_VERSION = 1;
    private static final String DB_NAME = "memo.db";
    
    public DBCreate(Context context) {
    	super(context, DB_NAME, null, DB_VERSION);
	
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		
		StringBuffer sql;
		
		// データベース作成
		sql = new StringBuffer();
        sql.append("CREATE TABLE T_USER (");
        sql.append(" USER_ID integer primary key autoincrement,");
        sql.append(" TITLE text NOT NULL,");
        sql.append(" TEXT text NOT NULL");
        sql.append(")");
        db.execSQL(sql.toString());
        
        // レコードを挿入
        ContentValues val = new ContentValues();
        val.put("TITLE", "NO_DATA");
        val.put("TEXT", "");
        
        db.insert("T_USER", null,val);
        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	       // Upgrade Database ver.2
        if (oldVersion < 2) {
            StringBuffer sql = new StringBuffer();
            sql.append(" AGE integer NOT NULL DEFAULT 0;");
            db.execSQL(sql.toString());
        }
        // Upgrade Database ver.3
        if (oldVersion < 3) {
            StringBuffer sql = new StringBuffer();
            sql.append("ALTER TABLE T_USER ADD COLUMN ");
            sql.append(" ADDRESS text;");
            db.execSQL(sql.toString());
        }
		
	}
	
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
