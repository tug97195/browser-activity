package edu.temple.webbrowserapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PageViewerFragment extends Fragment  implements Parcelable {

    private static final String URL_KEY = "url";


    private WebView webView;
    private PageViewerInterface browserActivity;

    private String url;


    protected PageViewerFragment(Parcel in) {
        url = in.readString();
    }

    public static final Creator<PageViewerFragment> CREATOR = new Creator<PageViewerFragment>() {
        @Override
        public PageViewerFragment createFromParcel(Parcel in) {
            return new PageViewerFragment(in);
        }

        @Override
        public PageViewerFragment[] newArray(int size) {
            return new PageViewerFragment[size];
        }
    };

    public static PageViewerFragment newInstance(String url) {
        PageViewerFragment fragment = new PageViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL_KEY, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    public PageViewerFragment() {}

    // Save reference to parent
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof PageViewerInterface) {
            browserActivity = (PageViewerInterface) context;
        } else {
            throw new RuntimeException("You must implement PageViewerInterface to attach this fragment");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            url = getArguments().getString(URL_KEY);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View l = inflater.inflate(R.layout.fragment_page_viewer, container, false);

        webView = l.findViewById(R.id.WebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Inform parent activity that URL is changing
                //browserActivity.updateUrl(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                browserActivity.updateTitle(webView.getTitle());
                browserActivity.updateUrl(url);
            }
        });

        // Restore WebView settings
        if (savedInstanceState != null)
            webView.restoreState(savedInstanceState);
        else {
            if (url != null) {
                webView.loadUrl(url);
            } else {
                browserActivity.updateUrl("");
            }
        }

        return l;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Store URL and previous/back in case fragment is restarted
        webView.saveState(outState);
    }

    /**
     * Load provided URL in webview
     * @param url to load
     */
    public void go (String url) {
        webView.loadUrl(url);
    }

    /**
     * Go to previous page
     */
    public void back () {
        webView.goBack();
    }

    /**
     * Go to next page
     */
    public void forward () {
        webView.goForward();
    }

    /**
     * Get the title of the page currently being displayed,
     * or the page's URL if title not available when requested
     * @return the title of the page currently being viewed
     */
    public String getTitle() {
        String title;
        if (webView != null) {
            title = webView.getTitle();
            return title == null || title.isEmpty() ? webView.getUrl() : title;
        } else
            return "Blank Page";
    }

    /**
     * Get the URL of the page currently being displayed
     * @return the URL of the page currently being viewed
     */
    public String getUrl() {
        if (webView != null)
            return webView.getUrl();
        else
            return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
    }

    interface PageViewerInterface {
        void updateUrl(String url);
        void updateTitle(String title);
    }
}