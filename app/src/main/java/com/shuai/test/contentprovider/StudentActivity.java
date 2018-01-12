package com.shuai.test.contentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shuai.test.R;

/**
 *
 */
public class StudentActivity extends Activity {
    private Button mBtnAdd;
    private Button mBtnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_provider);
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mBtnRetrieve = (Button) findViewById(R.id.btn_retrieve);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddName(v);
            }
        });
        mBtnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRetrieveStudents(v);
            }
        });
    }

    public void onClickAddName(View view) {
        // Add a new student record
        ContentValues values = new ContentValues();
        values.put(StudentsProvider.NAME,
                ((EditText) findViewById(R.id.editText2)).getText().toString());

        values.put(StudentsProvider.GRADE,
                ((EditText) findViewById(R.id.editText3)).getText().toString());

        Uri uri = getContentResolver().insert(
                StudentsProvider.CONTENT_URI, values);

        Toast.makeText(getBaseContext(),
                uri.toString(), Toast.LENGTH_LONG).show();
    }

    public void onClickRetrieveStudents(View view) {
        // Retrieve student records
        Uri students = StudentsProvider.CONTENT_URI;
        Cursor c = managedQuery(students, null, null, null, "name");

        if (c.moveToFirst()) {
            do {
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(StudentsProvider._ID)) +
                                ", " + c.getString(c.getColumnIndex(StudentsProvider.NAME)) +
                                ", " + c.getString(c.getColumnIndex(StudentsProvider.GRADE)),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }
}
