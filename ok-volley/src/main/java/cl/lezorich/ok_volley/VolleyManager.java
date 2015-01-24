package cl.lezorich.ok_volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import cl.lezorich.ok_volley.utils.LruBitmapCache;

/**
 * Singleton class that encapsulates RequestQueue and other volley functionality.
 *
 * Created by lukas on 02-11-14.
 */
public class VolleyManager {

    private static final String TAG = VolleyManager.class.getSimpleName();

    private static VolleyManager mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mContext;

    /**
     * Private constructor to prevent that VolleyManager is instanciated outside this class.
     *
     * @param context the context of the controller
     */
    private VolleyManager(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(LruBitmapCache
                .getCacheSize(context)));
    }

    /**
     * Returns the singleton instance of VolleyManager. If there is no instance,
     * then it creates a new one, else it returns the existing one.
     *
     * A key concept is that context must be the Application context,
     * not an Activity context. This  ensures that the RequestQueue will last for the lifetime of
     * your app, instead of being recreated every time the activity is recreated (for example,
     * when the user rotates the device).
     * @param context the context where the method is called. This context MUST be Application
     *                context.
     * @return the instance of VolleyManager
     */
    public static synchronized VolleyManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyManager(context);
        }

        return mInstance;
    }

    /**
     * Returns the singleton instance of RequestQueue that last the lifetime of the app. If there
     * is no instance of RequestQueue, then a new one is created.
     *
     * The created RequestQueue uses the {@link cl.lezorich.ok_volley.OkHttpStack OkHttpStack}
     * for networking.
     *
     * @return RequestQueue instance
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() keeps from leaking Activity or BroadcastReceiver if
            // someone pass one in
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(),
                    new OkHttpStack());
        }

        return  mRequestQueue;
    }

    /**
     * @return the image loader
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }


    /**
     * Adds a request to the RequestQueue with the default tag.
     *
     * @param request the request that will be added to the queue.
     * @param <T> The type of the request
     */
    public <T> void addToRequestQueue(Request<T> request) {
        addToRequestQueue(request, TAG);
    }

    /**
     * Adds a request to the RequestQueue with a specific tag.
     *
     * @param request The request that will be added to the queue.
     * @param tag The tag to be added to the request
     * @param <T> The type of the request
     */
    public <T> void addToRequestQueue(Request<T> request, Object tag) {
        request.setTag(tag == null ? TAG : tag);
        getRequestQueue().add(request);
    }

    /**
     * Cancels all pending requests by the specified tag. It is important to specify a tag so
     * that pending/ongoing requests can be cancelled.
     *
     * @param tag the tag of the requests that are going to be cancelled
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
