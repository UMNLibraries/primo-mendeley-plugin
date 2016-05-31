package edu.umn.lib.primo.pushto;

import com.exlibris.primo.xsd.commonData.PrimoResult;
import com.google.common.io.CharStreams;
import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MendeleyProcessTest {

    private static final String HOSTNAME = "primo.somewhere.edu";
    private static final String IMPORT_URL = "http://www.mendeley.com/import/?url=";
    private static final String PNX_PATH = "/10.1373:clinchem.2014.230656.xml";
    private static final String CITATION_PAGE_PATH = "/10.1373:clinchem.2014.230656.html";
    private static final String VID = "default";

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private PrintWriter output;
    @Captor ArgumentCaptor argCaptor;

    private PrimoResult[] primoResults;
    private MendeleyProcess mendeleyProcess;

    @Before public void setUp() throws Exception {
        setUpTestDoubles();
        this.primoResults = loadPrimoResults();
        this.mendeleyProcess = new MendeleyProcess();
    }

    private void setUpTestDoubles() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(request.getRequestURL()).thenReturn(getPushToRequestUrl());
        when(request.getQueryString()).thenReturn("pushToType=Mendeley");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("vid")).thenReturn(VID);
        when(response.getWriter()).thenReturn(output);
    }

    private PrimoResult[] loadPrimoResults() throws IOException, XmlException {
        InputStream inputStream = this.getClass().getResourceAsStream(PNX_PATH);
        PrimoResult pnx = PrimoResult.Factory.parse(inputStream);
        inputStream.close();
        return new PrimoResult[]{pnx};
    }

    @Test public void redirectsToMendeleyWhenCommingFromPrimo() throws Exception {
        theRequestIsComingFromPrimo();
        mendeleyProcess.pushTo(request, response, primoResults, false);
        verify(response).sendRedirect(getMendeleyRedirectUrl());
    }

    @Test public void presentsCitationPageWhenComingFromMendeley() throws Exception {
        theRequestIsComingFromMedndeley();
        mendeleyProcess.pushTo(request, response, primoResults, false);
        String expectedHtml = loadCitationPage();
        assertOutputEquals(expectedHtml);
    }

    @Test public void cannotSendMultipleRecordsToMendeley() throws Exception {
        theRequestIsComingFromPrimo();
        PrimoResult[] twoResults = {this.primoResults[0], this.primoResults[0]};
        mendeleyProcess.pushTo(request, response, twoResults, false);
        String expectedRedirect = getPushToRequestUrl().append("?pushToType=RISPushTo").toString();
        verify(response).sendRedirect(expectedRedirect);
    }

    @Test public void usesDefaultVidFromConfigFile() throws Exception {
        theRequestIsComingFromPrimo();
        theViewIdIsUnkown();
        mendeleyProcess.pushTo(request, response, primoResults, false);
        String expectedRedirect =
                getMendeleyRedirectUrl().replace(VID, "CONFIG_FILE_VID");
        verify(response).sendRedirect(contains(expectedRedirect));
    }

    @Test public void depricatedMethodsReturnNull() throws IOException {
        assertNull(mendeleyProcess.getContent(request, false));
        assertNull(mendeleyProcess.getFormAction());
    }

// custom assertions

    private void assertOutputEquals(String expectedHtml) {
        verify(output).print(argCaptor.capture());
        assertEquals(expectedHtml, argCaptor.getValue().toString());
    }

// test utility methods

    private void theRequestIsComingFromPrimo() {
        when(request.getParameter("fromMendeley")).thenReturn(null);
    }

    private void theRequestIsComingFromMedndeley() {
        when(request.getParameter("fromMendeley")).thenReturn("true");
    }
    private void theViewIdIsUnkown() {
        when(request.getSession(false)).thenReturn(null);
        assertNull(request.getParameter("vid"));
    }

    private StringBuffer getPushToRequestUrl() {
       return new StringBuffer("http://").append(HOSTNAME)
               .append("/primo_library/libweb/action/PushToAction.do");
    }

    private String getMendeleyRedirectUrl() throws UnsupportedEncodingException {
         String callbackUrl =
                    getPushToRequestUrl()
                    .append("?")
                    .append("pushToType=Mendeley")
                    .append("&vid=")
                    .append(VID)
                    .append("&fromMendeley=true")
                    .append("&fromSitemap=1")
                    .toString();
        String expectedUrl = IMPORT_URL + URLEncoder.encode(callbackUrl, "utf-8");
        return expectedUrl;
    }

    private String loadCitationPage() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(CITATION_PAGE_PATH);
        String expectedHtml = CharStreams.toString(new InputStreamReader(inputStream));
        inputStream.close();
        return expectedHtml;
    }
}