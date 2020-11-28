package edu.temple.webbrowserapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class BrowserControlFragment extends Fragment {

    private ImageButton newPageButton, bookmarkButton, saveBookmarkButton;

    private BrowserControlInterface browserActivity;

    public BrowserControlFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof BrowserControlInterface) {
            browserActivity = (BrowserControlInterface) context;
        } else {
            throw new RuntimeException("Cannot manage windows if interface not BrowserControlInterface implemented");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View l = inflater.inflate(R.layout.fragment_browser_control, container, false);
        newPageButton = l.findViewById(R.id.newTabButton);
        bookmarkButton = l.findViewById(R.id.bookMarkButton);
        saveBookmarkButton = l.findViewById(R.id.saveBookmarkButton);


        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(newPageButton)) {
                    browserActivity.newPage();
                }
                else if (v.equals(bookmarkButton)) {
                    browserActivity.openBookmark();
                }
                else if (v.equals(saveBookmarkButton)) {
                    browserActivity.addBookmark();
                }
            }
        };

        newPageButton.setOnClickListener(ocl);
        bookmarkButton.setOnClickListener(ocl);
        saveBookmarkButton.setOnClickListener(ocl);

        return l;
    }

    interface BrowserControlInterface {
        void newPage();
        void openBookmark();
        void addBookmark();
    }
}