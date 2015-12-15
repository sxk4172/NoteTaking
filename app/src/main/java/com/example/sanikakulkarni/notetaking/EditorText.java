package com.example.sanikakulkarni.notetaking;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class EditorText extends AppCompatActivity {


    private String action;
    private EditText editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_text);

        editor = (EditText) findViewById(R.id.editText);
        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NoteProvider.CONTENT_ITEM_TYPE);
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

       switch (item.getItemId()){
           case android.R.id.home:
               finishEditing();
               break;
       }

        return true;
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();
        switch (action){
            case Intent.ACTION_INSERT:
                if (newText.length()==0){
                    setResult(RESULT_CANCELED);
                }
                else {
                    insertNote(newText);
                }
        }
        finish();
    }

    private void insertNote(String newText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newText);
        getContentResolver().insert(NoteProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishEditing();
    }
}
