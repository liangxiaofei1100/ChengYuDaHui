package com.zhaoyan.juyou.game.chengyudahui.download;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

import com.zhaoyan.communication.cache.BitmapLruCache;
import com.zhaoyan.communication.cache.CacheableBitmapDrawable;
import com.zhaoyan.communication.cache.CacheableImageView;
import com.zhaoyan.communication.cache.SDK11;
import com.zhaoyan.juyou.game.chengyudahui.JuYouApplication;

/**
 * Simple extension of CacheableImageView which allows downloading of Images of the Internet.
 *
 * This code isn't production quality, but works well enough for this sample.s
 */
public class NetworkCacheableImageView extends CacheableImageView {

    public interface OnImageLoadedListener {
        void onImageLoaded(CacheableBitmapDrawable result);
    }

    /**
     * This task simply fetches an Bitmap from the specified URL and wraps it in a wrapper. This
     * implementation is NOT 'best practice' or production ready code.
     */
    private static class ImageUrlAsyncTask
            extends AsyncTask<String, Void, CacheableBitmapDrawable> {

        private final BitmapLruCache mCache;

        private final WeakReference<ImageView> mImageViewRef;
        private final OnImageLoadedListener mListener;

        private final BitmapFactory.Options mDecodeOpts;

        ImageUrlAsyncTask(ImageView imageView, BitmapLruCache cache,
                BitmapFactory.Options decodeOpts, OnImageLoadedListener listener) {
            mCache = cache;
            mImageViewRef = new WeakReference<ImageView>(imageView);
            mListener = listener;
            mDecodeOpts = decodeOpts;
        }

        @Override
        protected CacheableBitmapDrawable doInBackground(String... params) {
            try {
                // Return early if the ImageView has disappeared.
                if (null == mImageViewRef.get()) {
                    return null;
                }

                final String url = params[0];

                // Now we're not on the main thread we can check all caches
                CacheableBitmapDrawable result = mCache.get(url, mDecodeOpts);

                if (null == result) {
                    Log.d("ImageUrlAsyncTask", "Downloading: " + url);

                    // The bitmap isn't cached so download from the web
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    InputStream is = new BufferedInputStream(conn.getInputStream());

                    // Add to cache
                    result = mCache.put(url, is, mDecodeOpts);
                } else {
                    Log.d("ImageUrlAsyncTask", "Got from Cache: " + url);
                }

                return result;

            } catch (IOException e) {
                Log.e("ImageUrlAsyncTask", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(CacheableBitmapDrawable result) {
            super.onPostExecute(result);

            ImageView iv = mImageViewRef.get();
            if (null != iv) {
                iv.setImageDrawable(result);
            }

            if (null != mListener) {
                mListener.onImageLoaded(result);
            }
        }
    }

    private final BitmapLruCache mCache;

    private ImageUrlAsyncTask mCurrentTask;

    public NetworkCacheableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCache = JuYouApplication.getApplication(context).getBitmapCache();
    }

    /**
     * Loads the Bitmap.
     *
     * @param url      - URL of image
     * @param fullSize - Whether the image should be kept at the original size
     * @return true if the bitmap was found in the cache
     */
    public boolean loadImage(String url, final boolean fullSize, OnImageLoadedListener listener) {
        // First check whether there's already a task running, if so cancel it
        if (null != mCurrentTask) {
            mCurrentTask.cancel(true);
        }

        // Check to see if the memory cache already has the bitmap. We can
        // safely do
        // this on the main thread.
        BitmapDrawable wrapper = mCache.getFromMemoryCache(url);

        if (null != wrapper) {
            // The cache has it, so just display it
            setImageDrawable(wrapper);
            return true;
        } else {
            // Memory Cache doesn't have the URL, do threaded request...
            setImageDrawable(null);

            BitmapFactory.Options decodeOpts = null;

            if (!fullSize) {
                //decodeOpts = new BitmapFactory.Options();
                //decodeOpts.inSampleSize = 2;
            }

            mCurrentTask = new ImageUrlAsyncTask(this, mCache, decodeOpts, listener);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    SDK11.executeOnThreadPool(mCurrentTask, url);
                } else {
                    mCurrentTask.execute(url);
                }
            } catch (RejectedExecutionException e) {
                // This shouldn't happen, but might.
            }

            return false;
        }
    }

}
