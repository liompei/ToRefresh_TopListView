package blm.newandroid.com.toplistview;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private Db db;
    private SQLiteDatabase sqliteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        db=new Db(this);
        sqliteDatabase=db.getWritableDatabase();

        initViews();
    }

    private void initViews() {
        editText = (EditText) findViewById(R.id.edit_add);
        button = (Button) findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                ContentValues contentValues=new ContentValues();
                contentValues.put("name",content);
                sqliteDatabase.insert("user",null,contentValues);
                sqliteDatabase.close();
                finish();
            }
        });
    }

}
