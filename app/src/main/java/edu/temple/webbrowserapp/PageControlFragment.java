package edu.temple.webbrowserapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class PageControlFragment extends Fragment {

    private ImageButton goButton, backButton, forwardButton;
    private TextView urlTextView;

    private PageControlInterface browserActivity;

    public PageControlFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof PageControlInterface) {
            browserActivity = (PageControlInterface) context;
        } else {
            throw new RuntimeException("You must implement the required interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View l = inflater.inflate(R.layout.fragment_page_control, container, false);

        urlTextView = l.findViewById(R.id.eTextURL);
        goButton = l.findViewById(R.id.goButton);
        backButton = l.findViewById(R.id.BackButton);
        forwardButton = l.findViewById(R.id.ForwardButton);

        View.OnClickListener controlOcl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(goButton))
                    browserActivity.go(formatUrl(urlTextView.getText().toString()));
                else if (v.equals(backButton))
                    browserActivity.back();
                else if (v.equals(forwardButton))
                    browserActivity.forward();
            }
        };

        goButton.setOnClickListener(controlOcl);
        backButton.setOnClickListener(controlOcl);
        forwardButton.setOnClickListener(controlOcl);

        return l;
    }

    /**
     * Display updated URL
     * @param url
     */
    public void updateUrl(String url) {
        urlTextView.setText(url);
    }

    /**
     * Add http to URL if no scheme specified
     * @param url to format
     * @return formatted URL
     */
    private String formatUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        } else {
            return url;
        }
    }

    interface PageControlInterface {
        void go(String url);
        void back();
        void forward();
    }
}