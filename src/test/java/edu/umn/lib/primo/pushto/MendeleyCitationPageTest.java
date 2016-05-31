package edu.umn.lib.primo.pushto;

import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class MendeleyCitationPageTest {

    private static final String DEEP_LINK = "http://www.example.com/";

    private static final String HTML =
        "<!DOCTYPE html>\n" +
        "<html>\n" +
        "    <head>\n" +
        "        <script language=\"JavaScript\">\n" +
        "            var url = \"" + DEEP_LINK + "\";\n" +
        "            window.location = url;\n" +
        "        </script>\n" +
        "        <noscript>\n" +
        "            <meta http-equiv=\"refresh\" content=\"0; url=" + DEEP_LINK + "\">\n" +
        "        </noscript>\n" +
        "        <meta name=\"citation_title\" content=\"Global and local fMRI signals.\">\n" +
        "        <meta name=\"citation_authors\" content=\"Lee, Jin Hyung; Durand, Remy; Gradinaru, Viviana;\">\n" +
        "        <meta name=\"citation_journal_title\" content=\"Nature\">\n" +
        "        <title>Mendeley Citation Import</title>\n" +
        "    </head>\n" +
        "    <body>\n" +
        "        <a href=\"" + DEEP_LINK + "\">continue to the resource</a>\n" +
        "    </body>\n" +
        "</html>";

    private Map<String, String> fields;

    @Before
    public void setUp() throws Exception {
        fields = new LinkedHashMap<>();
        fields.put("citation_title", "Global and local fMRI signals.");
        fields.put("citation_authors", "Lee, Jin Hyung; Durand, Remy; Gradinaru, Viviana;");
        fields.put("citation_journal_title", "Nature");
    }

    @Test
    public void testToString() throws Exception {
        MendeleyCitationPage page = new MendeleyCitationPage(DEEP_LINK, fields);
        assertEquals(HTML, page.toString());
    }

}