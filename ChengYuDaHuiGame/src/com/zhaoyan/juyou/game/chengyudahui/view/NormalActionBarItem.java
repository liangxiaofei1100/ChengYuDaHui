package com.zhaoyan.juyou.game.chengyudahui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.zhaoyan.juyou.game.chengyudahui.R;

/**
 * Default implementation of an {@link ActionBarItem}. A
 * {@link NormalActionBarItem} is a simple {@link ActionBarItem} containing a
 * single icon.
 */
public class NormalActionBarItem extends ActionBarItem {

    @Override
    protected View createItemView() {
        return LayoutInflater.from(mContext).inflate(R.layout.zy_action_bar_item_base, mActionBar, false);
    }

    @Override
    protected void prepareItemView() {
        super.prepareItemView();
        final ImageButton imageButton = (ImageButton) mItemView.findViewById(R.id.zy_action_bar_item);
        imageButton.setImageDrawable(mDrawable);
        imageButton.setContentDescription(mContentDescription);
    }

    @Override
    protected void onContentDescriptionChanged() {
        super.onContentDescriptionChanged();
        mItemView.findViewById(R.id.zy_action_bar_item).setContentDescription(mContentDescription);
    }

    @Override
    protected void onDrawableChanged() {
        super.onDrawableChanged();
        ImageButton imageButton = (ImageButton) mItemView.findViewById(R.id.zy_action_bar_item);
        imageButton.setImageDrawable(mDrawable);
    }

}
