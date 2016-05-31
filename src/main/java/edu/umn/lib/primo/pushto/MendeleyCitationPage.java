package edu.umn.lib.primo.pushto;

import com.exlibris.primo.soap.messages.PrimoNMBibDocument;
import org.apache.commons.lang3.CharEncoding;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates a landing page that Mendeley's web importer will use to
 * obtain citation data.
 */
public class MendeleyCitationPage {

    protected static final String PAGE_TITLE = "Mendeley Citation Import";
    protected static final String TEMPLATE_PATH = "/templates/mendeley.vm";
    private final Map<String, String> fields;
    private final String deepLink;
    private final String page;

    /**
     * @param deepLink The "permalink" URL to the record in Primo
     * @param fields Citation meta tags and corresponding values
     *               (see: https://www.mendeley.com/import/information-for-publishers/#web-importer-support)
     */
    public MendeleyCitationPage(String deepLink, Map<String, String> fields) {
        this.deepLink = deepLink;
        this.fields = fields;
        this.page = formatPage();
    }

    /**
     * Static factory
     * @return MendeleyCitationPage for a given request
     */
    public static MendeleyCitationPage forRequest(PushToMendeleyRequest mendeleyRequest) {
        Map<String, String> citationFields =
                buildCitationFieldsFromPnx(mendeleyRequest.getPnx());
        return new MendeleyCitationPage(mendeleyRequest.getDeepLink(), citationFields);
    }

    /**
     * @return citation fields formatted using the {@value #TEMPLATE_PATH} template
     */
    public String toString() {return this.page;}

    private String formatPage() {
        VelocityEngine ve = initializeVelocityEngine();
        VelocityContext context = initializeVelocityContext();
        Reader reader = getTemplateReader();
        StringWriter page = new StringWriter();
        ve.evaluate(context, page, "Mendeley Page", reader);
        return page.toString();
    }

    private Reader getTemplateReader() {
        InputStream template = this.getClass().getResourceAsStream(this.TEMPLATE_PATH);
        Reader reader;
        try {
            reader = new InputStreamReader(template, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            reader = new InputStreamReader(template);
        }
        return reader;
    }

    private VelocityContext initializeVelocityContext() {
        VelocityContext context = new VelocityContext();
        context.put("title", this.PAGE_TITLE);
        context.put("citationFields", this.fields);
        context.put("deepLink", this.deepLink);
        return context;
    }

    private VelocityEngine initializeVelocityEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        return ve;
    }

    private static Map<String, String> buildCitationFieldsFromPnx(PrimoNMBibDocument.PrimoNMBib pnx) {
        PnxCitationExtractor citationExtractor = new PnxCitationExtractor(pnx);
        LinkedHashMap<String, String> citationFields = new LinkedHashMap<>();
        citationFields.put("citation_title", citationExtractor.getTitle());
        citationFields.put("citation_authors", citationExtractor.getAuthors());
        citationFields.put("citation_doi", citationExtractor.getDoi());
        citationFields.put("citation_journal_title", citationExtractor.getJournalTitle());
        citationFields.put("citation_publisher", citationExtractor.getPublisher());
        citationFields.put("citation_date", citationExtractor.getCreateDate());
        citationFields.put("citation_isbn", citationExtractor.getIsbn());
        citationFields.put("citation_issn", citationExtractor.getIssn());
        citationFields.put("citation_volume", citationExtractor.getVolume());
        citationFields.put("citation_issue", citationExtractor.getIssue());
        citationFields.put("citation_firstpage", citationExtractor.getStartPage());
        citationFields.put("citation_lastpage", citationExtractor.getEndPage());
        return citationFields;
    }
}
