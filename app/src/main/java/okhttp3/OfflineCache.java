package okhttp3;

import java.io.File;
import java.io.IOException;

import okhttp3.internal.DiskLruCache;
import okhttp3.internal.Util;
import okhttp3.internal.http.CacheRequest;
import okhttp3.internal.http.OkHeaders;

//TODO:remove()何时调用？
public class OfflineCache extends OkHttpCache {
    public OfflineCache(File directory, long maxSize) {
        super(directory, maxSize);
    }

    @Override
    protected String urlToKey(Request request) {
        HttpUrl url = request.url();
        //url.
        return null;
    }

    public CacheRequest put(String cacheKey, Response response) throws IOException {
        if (cacheKey == null) {
            return null;
        }
        String requestMethod = response.request().method();

        if (OkHeaders.hasVaryAll(response)) {
            return null;
        }

        OkHttpCache.Entry entry = new OkHttpCache.Entry(response);
        DiskLruCache.Editor editor = null;
        try {
            editor = cache.edit(cacheKey);
            if (editor == null) {
                return null;
            }
            entry.writeTo(editor);
            return new OkHttpCache.CacheRequestImpl(editor);
        } catch (IOException e) {
            abortQuietly(editor);
            return null;
        }
    }

    public Response get(String cacheKey, Request request) throws IOException {
        if (cacheKey == null) {
            return null;
        }
        DiskLruCache.Snapshot snapshot;
        OkHttpCache.Entry entry;
        try {
            snapshot = cache.get(cacheKey);
            if (snapshot == null) {
                return null;
            }
        } catch (IOException e) {
            // Give up because the cache cannot be read.
            return null;
        }

        try {
            entry = new OkHttpCache.Entry(snapshot.getSource(ENTRY_METADATA));
        } catch (IOException e) {
            Util.closeQuietly(snapshot);
            return null;
        }

        Response response = entry.response(snapshot);

        if (!entry.matches(request, response)) {
            Util.closeQuietly(response.body());
            return null;
        }

        return response;
    }

    public void remove(Request request) throws IOException {
        //TODO:urlKey
        cache.remove(urlToKey(request));
    }
}
