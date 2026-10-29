package de.ub0r.android.choosebrowser;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static java.util.concurrent.TimeUnit.SECONDS;

/** Local parsing never performs network I/O. Expansion belongs to a cancellable worker. */
class IntentParser {
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .callTimeout(3, SECONDS).followRedirects(false).followSslRedirects(false).build();
    private static final Set<String> SHORTENERS = new HashSet<>(Arrays.asList(
            "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly", "tiny.cc", "bit.do", "cut.ly"));
    private static final Pattern URL = Pattern.compile("https?://[^\\s]+", Pattern.CASE_INSENSITIVE);
    private volatile Call activeCall;
    private volatile boolean cancelled;

    Uri parseIntent(@NonNull Intent intent) {
        Uri parsed = validUri(intent.getData()) ? intent.getData() : null;
        if (parsed == null) parsed = uriOrNull(parseText(intent.getCharSequenceExtra(Intent.EXTRA_TEXT)));
        if (parsed == null && intent.getClipData() != null) parsed = parseClipData(intent.getClipData());
        return extractGoogleRedirect(parsed);
    }

    private Uri parseClipData(ClipData data) {
        for (int i = 0; i < data.getItemCount(); i++) {
            ClipData.Item item = data.getItemAt(i);
            if (validUri(item.getUri())) return item.getUri();
            Uri parsed = uriOrNull(parseText(item.getText()));
            if (parsed != null) return parsed;
        }
        return null;
    }

    String parseText(@Nullable CharSequence text) {
        if (text == null) return null;
        Matcher matcher = URL.matcher(text);
        if (!matcher.find()) return null;
        String candidate = text.subSequence(matcher.start(), matcher.end()).toString();
        return validUri(Uri.parse(candidate)) ? candidate : null;
    }

    private Uri uriOrNull(String text) { return text == null ? null : Uri.parse(text); }

    private Uri extractGoogleRedirect(Uri uri) {
        if (!validUri(uri)) return null;
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        if ((host.equals("google.com") || host.endsWith(".google.com")) && "/url".equals(uri.getPath())) {
            String target = uri.getQueryParameter("q");
            if (target == null) target = uri.getQueryParameter("url");
            Uri result = uriOrNull(target);
            if (validUri(result)) return result;
        }
        return uri;
    }

    boolean needsNetwork(Uri uri) {
        return validUri(uri) && SHORTENERS.contains(uri.getHost().toLowerCase(Locale.ROOT));
    }

    Uri resolveRedirect(@Nullable Uri uri) {
        Uri redirect = extractGoogleRedirect(uri);
        if (!needsNetwork(redirect)) return redirect;
        if (Looper.myLooper() == Looper.getMainLooper())
            throw new IllegalStateException("Resolve redirects off the UI thread");
        if (cancelled || Thread.currentThread().isInterrupted()) return redirect;
        try {
            Uri requestUri = redirect.buildUpon().scheme("https").build();
            Request request = new Request.Builder().url(requestUri.toString()).head().build();
            try (Response response = doHttp(request)) {
                if (cancelled) return redirect;
                String location = response.header("Location");
                if (response.code() >= 300 && response.code() < 400 && location != null) {
                    HttpUrl target = response.request().url().resolve(location);
                    if (target != null) {
                        Uri parsed = Uri.parse(target.toString());
                        if (validUri(parsed)) redirect = parsed;
                    }
                }
            }
        } catch (IOException | IllegalArgumentException ignored) {
            // Keep the usable input when a shortener times out or rejects HEAD.
        }
        return redirect;
    }

    boolean validUri(@Nullable Uri uri) {
        return uri != null && uri.isHierarchical() && uri.getHost() != null && !uri.getHost().isEmpty()
                && ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()));
    }

    Response doHttp(Request request) throws IOException {
        Call call = CLIENT.newCall(request);
        activeCall = call;
        try {
            if (cancelled || Thread.currentThread().isInterrupted()) call.cancel();
            return call.execute();
        } finally {
            activeCall = null;
        }
    }

    void cancel() {
        cancelled = true;
        Call call = activeCall;
        if (call != null) call.cancel();
    }
}
