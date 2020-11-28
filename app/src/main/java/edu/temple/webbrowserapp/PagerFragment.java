package edu.temple.webbrowserapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PagerFragment extends Fragment {

    private PagerInterface browserActivity;

    private ViewPager viewPager;

    private ArrayList<PageViewerFragment> pages;

    private static final String PAGES_KEY = "pages";

    public PagerFragment() {}

    /**
     *
     * @param pages - reference to list of pages (Fragments) stored in parent
     * @return PagerFragment that contains reference to dynamic list of pages
     */
    public static PagerFragment newInstance(ArrayList<PageViewerFragment> pages) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(PAGES_KEY, pages);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pages = (ArrayList) getArguments().getSerializable(PAGES_KEY);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof PagerInterface) {
            browserActivity = (PagerInterface) context;
        } else {
            throw new RuntimeException("You must implement PagerInterface to attach this fragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View l = inflater.inflate(R.layout.fragment_pager, container, false);

        viewPager = l.findViewById(R.id.Pager_Display_Layout);

        // Set FragmentStatePagerAdapter to manage pages and provide swipe action
        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {

            // Just like an adapterview's adapter, this will return a fragment to the viewpager
            // when requested
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return pages.get(position);
            }

            // This will tell the viewpager how many pages are available in the adapter
            @Override
            public int getCount() {
                return pages.size();
            }

        });

        // Set OnPageChangeListener to notify parent whenever currently viewed page URL is changed
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }


            // Whenever the user scrolls from one page to another,
            // inform the parent activity to it can show the current URL and title
            @Override
            public void onPageSelected(int position) {
                browserActivity.updateUrl((pages.get(position)).getUrl());
                browserActivity.updateTitle((pages.get(position)).getTitle());
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        return l;
    }

    /**
     * Update the list of fragments in viewpager when a new page is added
     */
    public void notifyWebsitesChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
    }

    /**
     * Switch viewpager to to specified fragment
     * @param index of page to view
     */
    public void showPage(int index) {
        viewPager.setCurrentItem(index);
    }

    /**
     * Update WebPage being displayed in current fragment's webview
     * @param url to load
     */
    public void go(String url) {
        pages.get(viewPager.getCurrentItem()).go(url);
    }

    /**
     * Go back to previous page in current fragment's webview
     */
    public void back() {
        pages.get(viewPager.getCurrentItem()).back();
    }

    /**
     * Go forward to next page in current fragment's webview
     */
    public void forward() {
        pages.get(viewPager.getCurrentItem()).forward();
    }

    /**
     * Retrieve URL of page being displayed in current fragment
     * @return current URL
     */
    public String getCurrentUrl() {
        return (pages.get(viewPager.getCurrentItem())).getUrl();
    }

    /**
     * Retrieve title of page being displayed in current fragment
     * @return current page title
     */
    public String getCurrentTitle() {
        return (pages.get(viewPager.getCurrentItem())).getTitle();
    }

    interface PagerInterface {
        void updateUrl(String url);
        void updateTitle(String title);
    }

}