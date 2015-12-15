package com.example.sanikakulkarni.notetaking;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.URI;
import java.util.logging.LogManager;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new NoteCursorAdapter(this,null,0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,EditorText.class);
                Uri uri = Uri.parse(NoteProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NoteProvider.CONTENT_ITEM_TYPE,uri);
                startActivityForResult(intent,EDITOR_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);

    }

    private void insertNote(String note) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, note);
        Uri noteUri = getContentResolver().insert(NoteProvider.CONTENT_URI, values);

        Log.d("MainActivity", "Inserted Note : " + noteUri.getLastPathSegment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_sample:
                deleteAllNotes();
                break;
        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            getContentResolver().delete(NoteProvider.CONTENT_URI,null,null);
                            restartLoader();

                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0,null,this);
    }

    private void insertSampleData() {
        insertNote("Sample Note");
        insertNote("Multi-line\nNew Note");
        insertNote("this is a very long note to test if it fits in screen");
        restartLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,NoteProvider.CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void editTextMethod(View view) {
        Intent intent = new Intent(this,EditorText.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }
}
