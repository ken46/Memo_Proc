package jp.ken46.memo_proc01;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
	static ListView lsList;
	static ArrayList<String> dtlist = new ArrayList<String>();
	static ArrayAdapter<String> adapter;
	static Context context;
	private static Context appContext;
	
	private Button btTextNew;
	private Button btSetting;
	private TextView tvDel;
	private int selectNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // コントロール参照
        setContentView(R.layout.activity_main);
        lsList = (ListView)findViewById(R.id.memoList);
        btTextNew = (Button)findViewById(R.id.btNew);
        btSetting = (Button)findViewById(R.id.btSet);
        tvDel = (TextView)findViewById(R.id.tvDel);
        
        // ボタンのクリックリスナー
        btTextNew.setOnClickListener(new NewButtonListener());
        btSetting.setOnClickListener(new SetButtonListener());
        
        dtlist.clear();
        tvDel.setVisibility(View.INVISIBLE);        
        appContext = getApplicationContext();
        
        // データベースの作成,取得
        getDataBase();
                
        // DBからリスト取得
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dtlist);
            lsList.setAdapter(adapter);
    
        // リストのクリックリスナー
        lsList.setOnItemClickListener(new ListitemClickListener());       
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.v("test", "再表示");
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
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
     *	 リストクリック時
     */
	public class ListitemClickListener implements OnItemClickListener {
	
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			// タップした番号
			selectNum = (int) id;
			
			// 削除モードかどうかを判断して処理
			switch (tvDel.getVisibility()) {
			case View.VISIBLE:				// 削除モード時
				// 削除確認ダイアログ表示
				showDialog();
				break;
					
			case View.INVISIBLE:			// 非削除モード時
				// インテント設定
				Intent intent = new Intent(MainActivity.this,ImputTextActivity.class);
				intent.putExtra("tapNum", selectNum);
				intent.putExtra("bool", true);
				startActivity(intent);	
				break;
			}	
		}	
	}

	/**
	 *	新規ボタンクリック時
	 */
	public class NewButtonListener implements OnClickListener {
	
		@Override
		public void onClick(View v) {			

			if (!tvDel.isShown()) {
				// インテント設定
				Intent intent = new Intent(MainActivity.this,ImputTextActivity.class);
				intent.putExtra("bool", false);
				startActivity(intent);
				
			}
			
	
		}
	}

	/**
	 * 	編集ボタンクリック時(削除モードON_OFF切り替え)
	 */
	public class SetButtonListener implements OnClickListener {
	
		@Override
		public void onClick(View v) {
			switch (tvDel.getVisibility()) {
			case View.VISIBLE:
				tvDel.setVisibility(View.INVISIBLE);
				break;

			case View.INVISIBLE:
				tvDel.setVisibility(View.VISIBLE);
				break;
			}	
		}	
	}

	/**
	 * データベースの作成、取得
	 */
	public void getDataBase(){
		
        // データベース作成
        DBCreate helper = new DBCreate(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        
        String table = "T_USER";
        String columns[] = new String[]{"TITLE"};
        
        Cursor c = database.query(table, columns, null, null,  null, null, null);
        int idxnd = c.getColumnIndex("TITLE");
        
        // databaseの中身が空の場合"NO_DATA"を挿入
        if(c.getCount() == 0){
            ContentValues val = new ContentValues();
            val.put("TITLE", "NO_DATA");
            val.put("TEXT", "");
            database.insert("T_USER", null, val);
        }

        // リストに入れるデータの準備
        c.moveToFirst(); 
        String sn = c.getString(idxnd);
        dtlist.add(sn);
        
        while (c.moveToNext()) {
			sn = "" + c.getString(idxnd);
			dtlist.add(sn);
			Log.v("test", "while"+ c.getString(idxnd));
			
		}    
        // データベースを閉じる
        database.close();
	}
	
	/**
	 * 削除時(データベース)
	 */
	public void delDataBase(){
		
        // データベース作成
        DBCreate helper = new DBCreate(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        String table = "T_USER";
        String id    = "USER_ID";
        //String num = String.valueOf(selectNum);
        
        String columns[] = new String[] {"USER_ID, TITLE, TEXT"};
        Cursor c = database.query(table, columns, null, null,  null, null, null);
        c.moveToFirst();
        c.moveToPosition(selectNum);
        
        // 選択されたリストのprimary key 取得
        int idxId = c.getColumnIndex("USER_ID");
        int numId = c.getInt(idxId);
                
        // トランザクション制御の開始
        database.beginTransaction();
        
        // 削除
        database.delete(table, id + "=" +String.valueOf(numId),null);	
    	Log.v("test", String.valueOf(numId));
    	
    	// コミット
    	database.setTransactionSuccessful();
    	
    	// トランザクション制御終了
    	database.endTransaction();
    	
    	// データベースを閉じる
    	database.close();	
	}
	
	/**
	 * 削除の確認ダイアログ
	 */
	private void showDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setTitle("削除してもよろしいですか？");
		dialog.setMessage("選択してください");
		
		// OKボタン押下
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 選択したメモの削除
				delList();				
			}
		});
		
		// NOボタン押下
		dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.v("test", "NO");				
			}
		});
		// ダイアログ表示
		dialog.show();
	}

	
	/**
	 * 削除時のデータベースとリストビュー反映
	 */
	public void delList() {
		delDataBase();
		adapter.remove(adapter.getItem(selectNum));
		adapter.notifyDataSetChanged();
		lsList.invalidateViews();		
	}
	
	/**
	 * コンテキストの取得
	 */
    public static Context getAppContext() {
        return appContext;
    }
    
	/**
	 * 入力画面で保存を押されたときのadapter追加処理
	 */
	public static void adaperUpdate(String text){
		adapter.add(text);
		adapter.notifyDataSetChanged();	
	}
	
	/**
	 * 既存リストメモを保存した場合のメモタイトル更新処理
	 */
	public static void updateListString(String str , int num) {
		dtlist.set(num, str);
		adapter.notifyDataSetChanged();	
	}
}
