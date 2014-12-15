package jp.ken46.memo_proc01;

import java.util.ArrayList;

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
	
	private ListView lsList;
	static ArrayList<String> dtlist = new ArrayList<String>();
	static ArrayAdapter<String> adapter;
	
	private Button btTextNew;
	private Button btSetting;
	private TextView tvDel;
	public  boolean   loadFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // コントロール参照
        setContentView(R.layout.activity_main);
        lsList = (ListView)findViewById(R.id.memoList);
        btTextNew = (Button)findViewById(R.id.btNew);
        btSetting = (Button)findViewById(R.id.btSet);
        tvDel = (TextView)findViewById(R.id.tvDel);

        btTextNew.setOnClickListener(new NewButtonListener());
        btSetting.setOnClickListener(new SetButtonListener());
        
        dtlist.clear();
        
        tvDel.setVisibility(View.INVISIBLE);
        
        // データベースの作成,取得
        getDataBase();
                
        // DBからリスト取得
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dtlist);
            lsList.setAdapter(adapter);
    
        // クリックリスナー
        lsList.setOnItemClickListener(new ListitemClickListener());
       
    }
    

    @Override
    protected void onResume() {
    	super.onResume();

        //lsList.setAdapter(adapter);
    	

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
			
			// 新規作成フラグOFF
			loadFile = false;
			
			// タップした番号
			int selectNum = (int) id  ;
						
			// インテント設定
			Intent intent = new Intent(MainActivity.this,ImputTextActivity.class);
			intent.putExtra("tapNum", selectNum);
			intent.putExtra("bool", true);
			startActivity(intent);
	
		}
	
	}

	/**
	 *	新規ボタンクリック時
	 */
	public class NewButtonListener implements OnClickListener {
	
		@Override
		public void onClick(View v) {
			
			// 新規作成フラグON
			loadFile = true;
			
			// インテント設定
			Intent intent = new Intent(MainActivity.this,ImputTextActivity.class);
			startActivity(intent);
	
		}
	
	}

	/**
	 * 	編集ボタンクリック時
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
	
	// アダプターの更新
	public static void adaperUpdate(String text){
		adapter.add(text);
		adapter.notifyDataSetChanged();	
	}
}
