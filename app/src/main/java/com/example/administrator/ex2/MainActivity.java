package com.example.administrator.ex2;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Database 관련 객체들
    SQLiteDatabase db;
    String dbName = "idList.db"; // name of Database;
    String tableName = "idListTable"; // name of Table;
    int dbMode = Context.MODE_PRIVATE;//이패키지에서만 디비 사용하겟다.


    // layout object
    EditText mEtName; //데이터 추가,수정시 입력창
    EditText mEtId;//각데이터의인덱스
    Button mBtInsert; //insert 버튼
    Button mBtRead; //자료 읽어오는 버튼

    Button mBtDelete; // 삭제 버튼
    Button mBtUpdate;//수정버튼


    ListView mList;//멤버변수
    ArrayAdapter<String> baseAdapter;   //어댑터
    ArrayList<String> nameList;//스트링 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // // Database 생성 및 열기
        db = openOrCreateDatabase(dbName,dbMode,null);
        // 테이블 생성
        createTable();

        mEtName = (EditText) findViewById(R.id.et_text);
        mBtInsert = (Button) findViewById(R.id.bt_insert);
        mBtRead = (Button) findViewById(R.id.bt_read);
        mBtDelete = (Button) findViewById(R.id.bt_delete);//id랑 변수이름이랑 같은것으로 사용
        mBtUpdate = (Button) findViewById(R.id.bt_update);
        mEtId=(EditText) findViewById(R.id.et_index);

        ListView mList = (ListView) findViewById(R.id.list_view);//여러개의 데이터가 리스트처럼 한번에 보여줄때 사용-xml에 하나밖에 없음
       /*삽입*/
        mBtInsert.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             String name = mEtName.getText().toString();
                                             insertData(name);
                                         }
                                     }

        );

        /*삭제*/
        mBtDelete = (Button) findViewById(R.id.bt_delete);
        mBtDelete.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             String id = mEtId.getText().toString();
                                             int index=Integer.parseInt(id);
                                             removeData(index);
                                         }
                                     }

        );
               /*수정*/
        mBtUpdate = (Button) findViewById(R.id.bt_update);
        mBtUpdate.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             String name1 = mEtName.getText().toString();
                                             String id1 = mEtId.getText().toString();
                                             int index1=Integer.parseInt(id1);
                                             updateData(index1,name1);
                                         }
                                     }

        );

        /*읽기*/
        mBtRead = (Button) findViewById(R.id.bt_read);//read 버튼 누르면 데이터 읽어와서 출력
        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameList.clear();//클리어 안하면 계속 중복되서 계속 출력됨!!
                selectAll();  //
                baseAdapter.notifyDataSetChanged();  //어댑터에 넣은 원본데이터를 수정하고 notifyDataSetChanged() 부르면 적용*****
            }
        });

        // Create listview
        nameList = new ArrayList<String>();
        baseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameList);//this:현재 액티비티에서 사용,.simple_dropdown_item_1line사전에 정의 되어있는 리소스,namelist 원본
        mList.setAdapter(baseAdapter);

    }

    // Table 생성
    public void createTable() {
        try {
            String sql = "create table " + tableName + "(id integer/*int 형*/ primary key autoincrement, " + "name text not null)";//not null- 꼭 입력을하라-입력안하면 오류
            db.execSQL(sql);//delete insert 등과같은거 인자로 넣으면 기능 가능
        } catch (android.database.sqlite.SQLiteException e) {
            Log.d("Lab sqlite","error: "+ e);
        }
    }

    // Table 삭제
    public void removeTable() {
        String sql = "drop table " + tableName;
        db.execSQL(sql);
    }

    // Data 추가
    public void insertData(String name) {
        String sql = "insert into " + tableName + " values(NULL, '" + name + "');";//null 이면 autoincrement이므로 자동증가
        db.execSQL(sql);
    }

    // Data 업데이트
    public void updateData(int index, String name) {
        String sql = "update " + tableName + " set name = '" + name + "' where id = " + index + ";";
        db.execSQL(sql);
    }

    // Data 삭제
    public void removeData(int index) {
        String sql = "delete from " + tableName + " where id = " + index + ";";
        db.execSQL(sql);
    }

    // Data 읽기(꺼내오기)
    public void selectData(int index) {
        String sql = "select * from " + tableName + " where id = " + index +";";//"order by" +"asc"+ ";";
        Cursor result = db.rawQuery(sql,null);

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (result.moveToFirst()) {
            int id = result.getInt(0);
            String name = result.getString(1);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_LONG).show();
            Log.d("lab_sqlite", "\"index= \" + id + \" name=\" + name ");
        }
        result.close();
    }

    // 모든 Data 읽기
    public void selectAll() {
        String sql = "select * from " + tableName + " order by id desc;";//모든 필드들을 가져오겟다.
        Cursor results = db.rawQuery(sql,null);//rawQuery -select만씀-Query(요구한다-데이터 리턴),나머지는 명령어(insert, delete, update)
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int id = results.getInt(0);//
            String name = results.getString(1);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_LONG).show();
            Log.d("lab_sqlite", "index= " + id + " name=" + name);//log.d :안드로이드시 프린트 에프 같은것//실행창에 띄워줌

            nameList.add(name);//네임 어레이리스트에 추가추가추가!!!
            results.moveToNext();
        }
        results.close();
    }

}
