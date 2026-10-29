package de.ub0r.android.choosebrowser;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import static org.junit.Assert.*;

/** Offline checks; synthetic responses never visit a real shortener or alter user preferences. */
@RunWith(AndroidJUnit4.class)
public class ChooserRegressionTest {
    private static class Parser extends IntentParser {
        Request request;
        int status = 302;
        String location = "/next";
        boolean fail;
        boolean closed;
        @Override Response doHttp(Request request) throws IOException {
            this.request = request;
            if (fail) throw new IOException("offline test");
            ResponseBody body = new ResponseBody() {
                private final Buffer buffer = new Buffer();
                @Override public MediaType contentType() { return null; }
                @Override public long contentLength() { return 0; }
                @Override public BufferedSource source() { return buffer; }
                @Override public void close() { closed = true; super.close(); }
            };
            return new Response.Builder().request(request).protocol(Protocol.HTTP_1_1)
                    .code(status).message("test").header("Location", location).body(body).build();
        }
    }

    @Test public void parseDoesNotRequestShortener() {
        Parser parser = new Parser();
        Uri url = Uri.parse("https://bit.ly/demo");
        assertEquals(url, parser.parseIntent(new Intent(Intent.ACTION_VIEW, url)));
        assertNull(parser.request);
    }

    @Test public void googleShortenerUsesExtractedUrl() {
        Parser parser = new Parser();
        Uri url = Uri.parse("https://google.com/url?q=https%3A%2F%2Fbit.ly%2Fdemo");
        assertEquals("https://bit.ly/next", parser.resolveRedirect(url).toString());
        assertEquals("https://bit.ly/demo", parser.request.url().toString());
        assertTrue(parser.closed);
    }

    @Test public void absoluteLocationIsResolved() {
        Parser parser = new Parser();
        parser.location = "https://example.com/target?q=1";
        assertEquals(parser.location, parser.resolveRedirect(Uri.parse("https://t.co/demo")).toString());
        assertTrue(parser.closed);
    }

    @Test public void failedNetworkPreservesInput() {
        Parser parser = new Parser(); parser.fail = true;
        Uri input = Uri.parse("https://bit.ly/demo");
        assertEquals(input, parser.resolveRedirect(input));
    }

    @Test public void nonRedirectStatusDoesNotChangeUrl() {
        Parser parser = new Parser(); parser.status = 200;
        Uri input = Uri.parse("https://bit.ly/demo");
        assertEquals(input, parser.resolveRedirect(input));
        assertTrue(parser.closed);
    }

    @Test public void schemeUpgradeDoesNotRewriteQuery() {
        Parser parser = new Parser();
        parser.resolveRedirect(Uri.parse("http://bit.ly/demo?value=http://example.com"));
        assertEquals("https", parser.request.url().scheme());
        assertEquals("http://example.com", parser.request.url().queryParameter("value"));
    }

    @Test public void cancelledParserDoesNotStartRequest() {
        Parser parser = new Parser(); parser.cancel();
        Uri input = Uri.parse("https://bit.ly/demo");
        assertEquals(input, parser.resolveRedirect(input));
        assertNull(parser.request);
    }

    @Test public void nullTextFallsBackToClipData() {
        Intent input = new Intent();
        input.putExtra(Intent.EXTRA_TEXT, (String) null);
        input.setClipData(ClipData.newPlainText("test", "https://example.com/path"));
        assertEquals("https://example.com/path", new IntentParser().parseIntent(input).toString());
    }

    @Test public void rejectsUnsafeSchemesAndHandlesRootUrls() {
        IntentParser parser = new IntentParser();
        assertNull(parser.parseText(null));
        assertFalse(parser.validUri(Uri.parse("httpfake://example.com/path")));
        assertFalse(parser.validUri(Uri.parse("javascript:alert(1)")));
        assertTrue(parser.validUri(Uri.parse("HTTPS://example.com")));
    }

    @Test public void similarlyNamedGoogleHostIsNotExtracted() {
        Uri input = Uri.parse("https://notgoogle.com/url?q=https://example.com/path");
        assertEquals(input, new IntentParser().resolveRedirect(input));
    }

    @Test public void invalidGoogleTargetIsNotExtracted() {
        Uri input = Uri.parse("https://google.com/url?q=javascript:alert(1)");
        assertEquals(input, new IntentParser().resolveRedirect(input));
    }

    private ResolveInfo app(String name) {
        ResolveInfo info = new ResolveInfo();
        info.activityInfo = new ActivityInfo();
        info.activityInfo.packageName = name;
        info.activityInfo.name = "Main";
        info.activityInfo.exported = true;
        info.activityInfo.enabled = true;
        info.activityInfo.applicationInfo = new ApplicationInfo();
        info.activityInfo.applicationInfo.enabled = true;
        return info;
    }

    @Test public void allHiddenFallbackExcludesSelfFirst() {
        ResolveInfo own = app("self"), other = app("browser");
        List<ResolveInfo> result = BrowserCatalog.filter(Arrays.asList(own, other), "self", Collections.singleton("browser"));
        assertEquals(Collections.singletonList(other), result);
    }

    @Test public void noOtherAppsProducesEmptyResult() {
        assertTrue(BrowserCatalog.filter(Collections.singletonList(app("self")), "self", Collections.emptySet()).isEmpty());
    }

    @Test public void hiddenAppIsExcludedWhenAnotherIsVisible() {
        ResolveInfo hidden = app("hidden"), visible = app("visible");
        assertEquals(Collections.singletonList(visible), BrowserCatalog.filter(
                Arrays.asList(hidden, visible), "self", Collections.singleton("hidden")));
    }
}
