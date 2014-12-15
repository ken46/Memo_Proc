package jp.ken46.memo_proc01;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ImputTextActivity extends ActionBarActivity {
	
	private Button btReturn;
	private Button btSave;
	private EditText etTitle;
	private EditText etText;
	private int tapNum;
	private boolean loadfile;
	
	DBCreate helper;
	SQLiteDatabase database;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
	        // コントロール参照
	        setContentView(R.layout.activity_imput_text);
	        
	        btReturn = (Button)findViewById(R.id.btReturn);
	        btSave   = (Button)findViewById(R.id.btSave);
	        
	        etTitle = (EditText)findViewById(R.id.titleText);
	        etText = (EditText)findViewById(R.id.impText);
	        
	        btReturn.setOnClickListener(new ReturnClickListener());
	        btSave.setOnClickListener(new SaveClickListener());
	        
	        
	        // インテントを取得
	        Intent intent = getIntent();
	        // インテントに保存されたデータを取得
	        int num = 0;
	        tapNum = intent.getIntExtra("tapNum", num);
	        loadfile = intent.getBooleanExtra("bool", loadfile);
	        if (loadfile) {
	        	findDataBase();				
			}
	        Log.v("test", String.valueOf(tapNum));
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	/**
	 * 保存ボタン押下
	 */
	public class SaveClickListener implements OnClickListener {
	
		@Override
		public void onClick(View v) {
			setDataBase();
			MainActivity.adaperUpdate(etTitle.getText().toString());
			finish();
			
	
		}
	
	}

	/**
	 * 戻るボタン押下
	 */
	public class ReturnClickListener implements OnClickListener {
	
		@Override
		public void onClick(View v) {
			finish();
	
		}
	
	}
	
	/**
	 * データベースに値を保存
	 */
	public void setDataBase(){
		
        // データベース作成
        DBCreate helper = new DBCreate(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        
        ContentValues val = new ContentValues();
        val.put("TITLE", etTitle.getText().toString() );
        val.put("TEXT", etText.getText().toString());
        database.insert("T_USER", null, val);	
        
        // データベースを閉じる
        database.close();
		}

	public void findDataBase() {
	      // SQLiteOpenHelper のインスタンスを生成
        DBCreate helper = new DBCreate(this);
 
        // データベースが作成
        SQLiteDatabase database = helper.getWritableDatabase();
        
		String table = "T_USER";								// テーブル名		
        String columns[] = new String[] {"USER_ID, TITLE, TEXT"};
        Cursor c = database.query(table, columns, null, null,  null, null, null);
        c.moveToFirst();
        c.moveToPosition(tapNum);
        		
		String title = c.getString(c.getColumnIndex("TITLE"));
		String text = c.getString(c.getColumnIndex("TEXT"));
		
		etTitle.setText(title);
		etText.setText(text);
		Log.v("test", title);
		Log.v("test", text);
	}

}
