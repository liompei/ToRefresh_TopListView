package blm.newandroid.com.toplistview;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private ListView listView;
    private Button button;
    private MyAdapter adapter;
    private List<String> listData;
    private String name;
    private SQLiteDatabase sqLiteDatabase;
    private Db db;
    private PullToRefreshListView pullToRefreshListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listData = new ArrayList<>();
        initViews();
        db = new Db(this);
        adapter = new MyAdapter(this, queryDb());
//        listView.setAdapter(adapter);
        pullToRefreshListView.setAdapter(adapter);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
              new AsyncTask<Void,Void,Void>(){

                  @Override
                  protected Void doInBackground(Void... params) {
                      try {
                          Thread.sleep(2000);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                      return null;
                  }

                  @Override
                  protected void onPostExecute(Void aVoid) {
                      queryDb();
                      adapter.notifyDataSetChanged();
                      pullToRefreshListView.onRefreshComplete();
                  }
              }.execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }




    private void initViews() {
//        listView = (ListView) findViewById(R.id.list_item);
        pullToRefreshListView= (PullToRefreshListView) findViewById(R.id.list_item);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
//        listView.setOnItemLongClickListener(this);
//        listView.setOnItemClickListener(this);
        pullToRefreshListView.setOnItemClickListener(this);
        pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(MainActivity.this,AddActivity.class);
        startActivity(intent);
    }

    //插入
    private void insertDb() {
        sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);  //将制定填入数据表
        sqLiteDatabase.insert("user", null, contentValues);//插入的表名,空列填充,ContentValues对象
        sqLiteDatabase.close();
    }

    //读取
    private List<String> queryDb() {
        listData.clear();
        sqLiteDatabase = db.getReadableDatabase();  //最后的from to 可以用SimpleCursorAdapter来直接传入adapter
        //如果使用SimpleCursorAdapter,比如在表中有一个_id列  _id integer primary key autoincrement
        Cursor cursor = sqLiteDatabase.query("user", null, null, null, null, null, null);//查询的表名,返回哪几列数据,查询的条件(如name=\"小张\"),查询条件的参数()
        while (cursor.moveToNext()) {
            //逐行获得内容
            String name = cursor.getString(cursor.getColumnIndex("name"));
            listData.add(0, name);
        }
        sqLiteDatabase.close();
        return listData;
    }

    //删除
    private void deleteDb(int position){
        sqLiteDatabase=db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("user", null, null, null, null, null, null);//查询的表名,返回哪几列数据,查询的条件(如name=\"小张\"),查询条件的参数()
        int size=cursor.getCount();
        cursor.moveToPosition(size-position);
        Log.i("AAA","size="+size+"position="+position+"行标为 "+(size-position));
        int i=cursor.getInt(cursor.getColumnIndex("_id"));
        System.out.println(i);
        sqLiteDatabase.delete("user","_id=?",new String[]{i+""});
        sqLiteDatabase.close();
    }
    //更新
    private void updateDb(int position,String content){
        sqLiteDatabase=db.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",content);
        Cursor cursor = sqLiteDatabase.query("user", null, null, null, null, null, null);//查询的表名,返回哪几列数据,查询的条件(如name=\"小张\"),查询条件的参数()
        int size=cursor.getCount();
        cursor.moveToPosition(size-position);  //使用cursor方法是定位要修改的行
        int i=cursor.getInt(cursor.getColumnIndex("_id"));
        sqLiteDatabase.update("user",contentValues,"_id=?",new String[]{i+""});
    }


    //item点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        System.out.println(position);
        String content=listData.get(position-1);
        //设置弹出框
        LayoutInflater layoutInflater= LayoutInflater.from(MainActivity.this);
        View view1=layoutInflater.inflate(R.layout.edit,null);
        final EditText editContent= (EditText) view1.findViewById(R.id.edit);
        editContent.setText(content);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("修改")
                .setView(view1)
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content=editContent.getText().toString();
                        updateDb(position,content);
                        queryDb();
                        adapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    //item长按事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        new AlertDialog.Builder(MainActivity.this)
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDb(position);
                        queryDb();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setTitle("提醒")
                .setMessage("你确定要删除该条吗?")
                .show();
        return true;
    }

    @Override
    protected void onResume() {
        queryDb();
        adapter.notifyDataSetChanged();
        Log.i("ceshi","重新运行");
        super.onResume();
    }

}
