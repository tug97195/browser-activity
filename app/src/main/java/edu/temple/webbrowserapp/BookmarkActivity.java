package edu.temple.webbrowserapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class BookmarkActivity extends AppCompatActivity {
    private static final String SHARED_PREFS_FILE = "savedListFile";
    private static final String SAVED_PAGES = "savedList";
    SharedPreferences preferences;
    ArrayList<String> savedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        Intent i = getIntent();
        savedList = i.getStringArrayListExtra(SAVED_PAGES);
        BookmarkListAdapter adapter = new BookmarkListAdapter(savedList, this);
        ListView lView = (ListView)findViewById(R.id.bookmark_layout);
        lView.setAdapter(adapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String urlClicked = savedList.get(i);
                Intent intent = new Intent();
                intent.putExtra("urlToLoad", urlClicked);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> savedSet = new HashSet<>();
        if (savedList != null) {
            savedSet.addAll(savedList);
            editor.putStringSet(SAVED_PAGES, savedSet);
            editor.commit();
        } else {
            savedList = new ArrayList<String>();
            savedSet.addAll(savedList);
            editor.putStringSet(SAVED_PAGES, savedSet);
            editor.commit();
        }

    }
}
