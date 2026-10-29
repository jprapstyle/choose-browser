package de.ub0r.android.choosebrowser;

import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@org.robolectric.annotation.Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class IntentParserTest {

    @Test public void nullTextIsRejected() { assertNull(new IntentParser().parseText(null)); }

    @Test public void similarlyNamedGoogleHostIsNotRewritten() {
        Uri uri = Uri.parse("https://notgoogle.com/url?q=https://example.com/path");
        assertEquals(uri, new IntentParser().resolveRedirect(uri));
    }

    @Test public void unsafeGoogleDestinationIsNotAccepted() {
        Uri uri = Uri.parse("https://google.com/url?q=javascript:alert(1)");
        assertEquals(uri, new IntentParser().resolveRedirect(uri));
    }

    @Test public void parsesShortenerWithoutNetwork() {
        IntentParser parser = new IntentParser() {
            @Override okhttp3.Response doHttp(okhttp3.Request request) {
                throw new AssertionError("parseIntent must not call the network");
            }
        };
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://bit.ly/test"));
        assertEquals("https://bit.ly/test", parser.parseIntent(intent).toString());
    }

    private final IntentParser intentParser = new IntentParser();

    @Test
    public void returnsNullOnMissingUrl() throws Exception {
        assertNull(intentParser.parseText("Some text w/o url"));
    }

    @Test
    public void parsesUrlFromTextHttp() throws Exception {
        assertEquals("http://example.com/foo.html", new IntentParser().parseText("Long text, with words http://example.com/foo.html"));
    }

    @Test
    public void parsesUrlFromTextHttps() throws Exception {
        assertEquals("https://example.com", new IntentParser().parseText("Long text, with words https://example.com"));
    }

    @Test
    public void parsesUrlFromTextSlashAtEnd() throws Exception {
        assertEquals("https://example.com/", new IntentParser().parseText("Long text, with words https://example.com/"));
    }

    @Test
    public void parsesUrlFromTextWordsAfterUrl() throws Exception {
        assertEquals("https://example.com/", new IntentParser().parseText("Long text, with words https://example.com/ and more words"));
    }

    @Test
    public void extractGoogleRedirectUrl() throws Exception {
        assertEquals(Uri.parse("https://foo.bar/j/123?pwd=usr"),
                new IntentParser().resolveRedirect(Uri.parse("https://www.google.com/url?q=https://foo.bar/j/123?pwd%3Dusr&sa=D")));
    }
}