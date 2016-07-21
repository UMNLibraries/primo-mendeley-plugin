package edu.umn.lib.primo.pushto;

import com.exlibris.primo.soap.messages.PrimoNMBibDocument;
import com.exlibris.primo.xsd.commonData.PrimoResult;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * Encapsulates a "Push to Mendeley" request
 */
public class PushToMendeleyRequest {

    private static final String IMPORT_BASE_URL = "http://www.mendeley.com/import/?url=";
    private final PrimoResult primoResult;
    private final HttpServletRequest request;
    private String vid;

    public PushToMendeleyRequest(HttpServletRequest request, PrimoResult primoResult) throws IOException {
        this.request = request;
        this.primoResult = primoResult;
        this.vid = getViewId();
    }

    public boolean isFromMendeley() {
        String fromMendeley = request.getParameter("fromMendeley");
        return (fromMendeley != null && fromMendeley.equalsIgnoreCase("true"));
    }

    /**
     * @return Mendeley import URL for the Primo record
     * @throws UnsupportedEncodingException
     */
    public String getImportUrl() throws UnsupportedEncodingException {
        String callbackUrl = new StringBuilder(getFullUrl())
                .append("vid=")
                .append(this.vid)
                .append("&fromMendeley=true")
                .append("&fromSitemap=1")
                .append("&afterPDS=true")
                .toString();
        return IMPORT_BASE_URL + URLEncoder.encode(callbackUrl, CharEncoding.UTF_8);
    }

    public MendeleyCitationPage createCitationPage() {
        return MendeleyCitationPage.forRequest(this);

    }

    /**
     * @return persistent URL for the Primo record
     */
    public String getDeepLink() {
        String docId = this.getPnx().getRecordArray(0).getControl().getRecordidArray(0);
        return new StringBuilder(getBaseUrl().replace("PushToAction.do", "dlDisplay.do"))
                        .append("?docId=")
                        .append(docId)
                        .append("&vid=")
                        .append(vid)
                        .append("&afterPDS=true")
                        .toString();
    }

    public PrimoNMBibDocument.PrimoNMBib getPnx() {
        return primoResult.getSEGMENTS()
                          .getJAGROOTArray(0)
                          .getRESULT()
                          .getDOCSET()
                          .getDOCArray(0)
                          .getPrimoNMBib();
    }

    /**
     * Tries to get the current vid form the URL or Session object;
     * otherwise returns getDefaultViewId()
     */
    private String getViewId() {
        String vid = this.request.getParameter("vid");

        if (StringUtils.isEmpty(vid)) vid = (String) request.getAttribute("vid");

        if (StringUtils.isEmpty(vid)) {
            HttpSession session = request.getSession(false);
            vid = (String) ((session != null) ?
                    session.getAttribute("vid") :
                    getDefaultViewId());
        }

        return vid;
    }

    /**
     * Tries to load the preferred default vid from a config file;
     * otherwise returns "default"
     */
    private String getDefaultViewId() {
        String defaultVid = loadDefaultViewIdFromConfig();
        if (StringUtils.isEmpty(defaultVid)) defaultVid = "default";
        return defaultVid;
    }

    private String loadDefaultViewIdFromConfig() {
        String defaultVid = null;
        String propertiesFile = "/config.properties";
        try (InputStream inputStream = getClass().getResourceAsStream(propertiesFile)) {
            if (inputStream != null) {
                Properties properties = new Properties();
                properties.load(inputStream);
                defaultVid = properties.getProperty("defaultVid");
            }
        } catch (IOException e) {}
        return defaultVid;
    }

    private String getBaseUrl() {
        return request.getRequestURL().toString();
    }

    /**
     * Get the full requested URL (including query string)
     */
    private String getFullUrl() {
        StringBuffer requestUrl = request.getRequestURL();
        String queryString = request.getQueryString();

        if (StringUtils.isEmpty(queryString)) {
            requestUrl.append('?');
        } else {
            requestUrl.append('?').append(queryString).append('&');
        }

        return requestUrl.toString();
    }

}
