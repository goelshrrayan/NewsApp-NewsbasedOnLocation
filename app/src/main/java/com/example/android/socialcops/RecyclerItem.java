package com.example.android.socialcops;

/**
 * Created by hp on 08-02-2019.
 */

public class RecyclerItem {

    private String mImageUrl;
    private String mTitle;
    private String mDescription;

    public RecyclerItem(String imageUrl,String title, String description)
    {mImageUrl=imageUrl;
    mTitle=title;
    mDescription=description;}

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }
}
