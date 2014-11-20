package com.zhaoyan.juyou.game.chengyudahui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.zhaoyan.juyou.game.chengyudahui.R;

/**
 * Base class representing an {@link ActionBarItem} used in {@link ActionBar}s.
 * The base implementation exposes a single Drawable as well as a content
 * description.
 */
public abstract class ActionBarItem {

    /**
     * The Type specifies a large set of pre-defined {@link ActionBarItem}s that
     * may be added to an {@link ActionBar}.
     */
    public enum Type {
        /**
         * A plus sign
         * 
         * @see R.drawable#gd_action_bar_add
         */
        Add,
    }

    protected Drawable mDrawable;

    protected CharSequence mContentDescription;
    protected View mItemView;

    protected Context mContext;
    protected ActionBar mActionBar;

    private int mItemId;

    void setActionBar(ActionBar actionBar) {
        mContext = actionBar.getContext();
        mActionBar = actionBar;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public ActionBarItem setDrawable(int drawableId) {
        return setDrawable(mContext.getResources().getDrawable(drawableId));
    }

    public ActionBarItem setDrawable(Drawable drawable) {
        if (drawable != mDrawable) {
            mDrawable = drawable;
            if (mItemView != null) {
                onDrawableChanged();
            }
        }
        return this;
    }

    public CharSequence getContentDescription() {
        return mContentDescription;
    }

    public ActionBarItem setContentDescription(int contentDescriptionId) {
        return setContentDescription(mContext.getString(contentDescriptionId));
    }

    public ActionBarItem setContentDescription(CharSequence contentDescription) {
        if (contentDescription != mContentDescription) {
            mContentDescription = contentDescription;
            if (mItemView != null) {
                onContentDescriptionChanged();
            }
        }
        return this;
    }

    public View getItemView() {
        if (mItemView == null) {
            mItemView = createItemView();
            prepareItemView();
        }
        return mItemView;
    }

    protected abstract View createItemView();

    protected void prepareItemView() {
    }

    protected void onDrawableChanged() {
    }

    protected void onContentDescriptionChanged() {
    }

    protected void onItemClicked() {
    }

    void setItemId(int itemId) {
        mItemId = itemId;
    }

    public int getItemId() {
        return mItemId;
    }

    static ActionBarItem createWithType(ActionBar actionBar, ActionBarItem.Type type) {

        int drawableId = 0;
        int descriptionId = 0;

        switch (type) {
            case Add:
//                drawableId = R.drawable.gd_action_bar_add;
//                descriptionId = R.string.gd_add;
                break;

            default:
                // Do nothing but return null
                return null;
        }

        final Drawable d = actionBar.getContext().getResources().getDrawable(drawableId);

        return actionBar.newActionBarItem(NormalActionBarItem.class).setDrawable(d).setContentDescription(descriptionId);
    }

}
