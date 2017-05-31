package com.pma.chat.pmaChat.model;


public class NavItem {

    private String mTitle;

    private int mIcon;

    public NavItem(String title, int icon) {
        mTitle = title;
        mIcon = icon;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmIcon() {
        return mIcon;
    }

    public void setmIcon(int mIcon) {
        this.mIcon = mIcon;
    }

    @Override
    public String toString() {

        return mTitle;
    }

}
