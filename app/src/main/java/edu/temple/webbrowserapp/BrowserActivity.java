package edu.temple.webbrowserapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BrowserActivity extends AppCompatActivity implements PageControlFragment.PageControlInterface, PageViewerFragment.PageViewerInterface, BrowserControlFragment.BrowserControlInterface, PagerFragment.PagerInterface, PageListFragment.PageListInterface {
    FragmentManager fm;
    PageControlFragment pageControlFragment;
    BrowserControlFragment browserControlFragment;
    PageListFragment pageListFragment;
    PagerFragment pagerFragment;
    SharedPreferences preferences;
    ArrayList<PageViewerFragment> pages;
    ArrayList<String> savedList;
    private static final String SHARED_PREFS_FILE = "savedListFile";
    private static final String SAVED_PAGES = "savedList";
    private static final String PAGES_KEY = "pages";
    boolean listMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            pages = (ArrayList) savedInstanceState.getSerializable(PAGES_KEY);
            savedList = (ArrayList) savedInstanceState.getSerializable(SAVED_PAGES);
        }
        else {
            pages = new ArrayList<>();
            preferences = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            Set<String> savedSet = new HashSet<>();
            savedSet = preferences.getStringSet(SAVED_PAGES, null);
            if (savedSet != null) {
                savedList = new ArrayList<String>(savedSet);
            } else {
                savedList = new ArrayList<String>();
            }
        }

        fm = getSupportFragmentManager();
        listMode = findViewById(R.id.page_list_layout) != null;
        Fragment tmpFragment;

        // If PageControlFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.page_control_layout)) instanceof PageControlFragment)
            pageControlFragment = (PageControlFragment) tmpFragment;
        else {
            pageControlFragment = new PageControlFragment();
            fm.beginTransaction()
                    .add(R.id.page_control_layout, pageControlFragment)
                    .commit();
        }

        // If BrowserFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.browser_control_layout)) instanceof BrowserControlFragment)
            browserControlFragment = (BrowserControlFragment) tmpFragment;
        else {
            browserControlFragment = new BrowserControlFragment();
            fm.beginTransaction()
                    .add(R.id.browser_control_layout, browserControlFragment)
                    .commit();
        }

        // If PagerFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.page_display_layout)) instanceof PagerFragment)
            pagerFragment = (PagerFragment) tmpFragment;
        else {
            pagerFragment = PagerFragment.newInstance(pages);
            fm.beginTransaction()
                    .add(R.id.page_display_layout, pagerFragment)
                    .commit();
        }


        // If fragment already added (activity restarted) then hold reference
        // otherwise add new fragment IF container available. Only one instance
        // of fragment is ever present
        if (listMode) {
            if ((tmpFragment = fm.findFragmentById(R.id.page_list_layout)) instanceof PageListFragment)
                pageListFragment = (PageListFragment) tmpFragment;
            else {
                pageListFragment = PageListFragment.newInstance(pages);
                fm.beginTransaction()
                        .add(R.id.page_list_layout, pageListFragment)
                        .commit();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> savedSet = new HashSet<>();
        savedSet.addAll(savedList);
        editor.putStringSet(SAVED_PAGES, savedSet);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        Set<String> savedSet = new HashSet<>();
        savedSet = preferences.getStringSet(SAVED_PAGES, null);
        if (savedSet != null) {
            savedList = new ArrayList<String>(savedSet);
        } else {
            savedList = new ArrayList<String>();
        }
    }

    /**
     * Clear the url bar and activity title
     */
    private void clearIdentifiers() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
        pageControlFragment.updateUrl("");
    }

    // Notify all observers of collections
    private void notifyWebsitesChanged() {
        pagerFragment.notifyWebsitesChanged();
        if (listMode)
            pageListFragment.notifyWebsitesChanged();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save list of open pages for activity restart
        outState.putSerializable(PAGES_KEY, pages);
        outState.putSerializable(SAVED_PAGES, savedList);
    }

    /**
     * Update WebPage whenever PageControlFragment sends new Url
     * Create new page first if none exists
     * Alternatively, you can create an empty page when the activity first loads
     * @param url to load
     */
    @Override
    public void go(String url) {
        if (pages.size() > 0)
            pagerFragment.go(url);
        else {
            pages.add(PageViewerFragment.newInstance(url));
            notifyWebsitesChanged();
            pagerFragment.showPage(pages.size() - 1);
        }

    }

    /**
     * Go back to previous page when user presses Back in PageControlFragment
     */
    @Override
    public void back() {
        pagerFragment.back();
    }

    /**
     * Go forward to next page when user presses Forward in PageControlFragment
     */
    @Override
    public void forward() {
        pagerFragment.forward();
    }

    /**
     * Update displayed Url in PageControlFragment when Webpage Url changes
     * only if it is the currently displayed page, and not another page
     * @param url to display
     */
    @Override
    public void updateUrl(String url) {
        if (url != null && url.equals(pagerFragment.getCurrentUrl())) {
            pageControlFragment.updateUrl(url);

            // Update the ListView in the PageListFragment - results in updated titles
            notifyWebsitesChanged();
        }
    }

    /**
     * Update displayed page title in activity when Webpage Url changes
     * only if it is the currently displayed page, and not another page
     * @param title to display
     */
    @Override
    public void updateTitle(String title) {
        if (title != null && title.equals(pagerFragment.getCurrentTitle()) && getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        // Results in the ListView in PageListFragment being updated
        notifyWebsitesChanged();
    }

    /**
     * Add a new page/fragment to the list and display it
     */
    @Override
    public void newPage() {
        // Add page to list
        pages.add(new PageViewerFragment());
        // Update all necessary views
        notifyWebsitesChanged();
        // Display the newly created page
        pagerFragment.showPage(pages.size() - 1);
        // Clear the displayed URL in PageControlFragment and title in the activity
        clearIdentifiers();
    }

    @Override
    public void openBookmark() {
        Intent intent = new Intent(this, BookmarkActivity.class);
        intent.putStringArrayListExtra(SAVED_PAGES, savedList);
        //startActivity(intent);
        startActivityForResult(intent,1);
    }

    @Override
    public void addBookmark() {
        if (pages.size() == 0 || pagerFragment.getCurrentUrl().equals("")) {
            Toast.makeText(this, "Empty URL", Toast.LENGTH_SHORT).show();
        }
        else {
            savedList.add(pagerFragment.getCurrentUrl());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String url = data.getStringExtra("urlToLoad");
                go(url);
            }
        }
    }

    /**
     * Display requested page in the PagerFragment
     * @param position of page to display
     */
    @Override
    public void pageSelected(int position) {
        pagerFragment.showPage(position);
    }
}
