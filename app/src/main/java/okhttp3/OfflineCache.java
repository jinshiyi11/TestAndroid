package okhttp3;

import java.io.File;
import java.io.IOException;

import okhttp3.internal.DiskLruCache;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;

//TODO:remove()何时调用？
public class OfflineCache extends OkHttpCache {
    public OfflineCache(File directory, long maxSize, int appVersion) {
        super(directory, maxSize, appVersion);
    }

    @Override
    protected String urlToKey(Request request) {
        HttpUrl url = request.url();
        //TODO:
        return null;
    }

    public void put(String cacheKey, Response response) throws IOException {
        if (cacheKey == null) {
            return;
        }

        OkHttpCache.Entry entry = new OkHttpCache.Entry(response);
        DiskLruCache.Editor editor = null;
        try {
            editor = cache.edit(cacheKey);
            if (editor == null) {
                return;
            }
            entry.writeTo(editor);
            BufferedSink sink = Okio.buffer(editor.newSink(ENTRY_BODY));
            sink.write(response.body().bytes());
            sink.close();
            editor.commit();
        } catch (IOException e) {
            abortQuietly(editor);
        }

        return;
    }

    public void update(Response cached, Response network) {
        OkHttpCache.Entry entry = new OkHttpCache.Entry(network);
        DiskLruCache.Snapshot snapshot = ((OkHttpCache.CacheResponseBody) cached.body()).snapshot;
        DiskLruCache.Editor editor = null;
        try {
            editor = snapshot.edit(); // Returns null if snapshot is not current.
            if (editor != null) {
                entry.writeTo(editor);
                editor.commit();
            }
        } catch (IOException e) {
            abortQuietly(editor);
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
