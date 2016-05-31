package edu.umn.lib.primo.pushto;

import com.exlibris.primo.soap.messages.PrimoNMBibDocument;
import com.exlibris.primo.xsd.commonData.PrimoResult;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class PnxCitationExtractorTest {

    private static final String pnxPath = "/10.1373:clinchem.2014.230656.xml";
    private PrimoNMBibDocument.PrimoNMBib pnx;
    private PnxCitationExtractor citationExtractor;

    @Before public void setUp() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream(pnxPath);

        this.pnx = PrimoResult.Factory.parse(inputStream)
                .getSEGMENTS()
                .getJAGROOTArray(0)
                .getRESULT()
                .getDOCSET()
                .getDOCArray(0)
                .getPrimoNMBib();

        inputStream.close();

        this.citationExtractor = new PnxCitationExtractor(pnx);

    }

    @Test public void testGetTitle() throws Exception {
        String title = "Impact of smoothing on parameter estimation in quantitative DNA amplification experiments";
        assertEquals(title, this.citationExtractor.getTitle());

    }

    @Test public void testGetAuthors() throws Exception {
        String authors = "Spiess, Andrej-Nikolai; Deutschmann, Claudia; Schierack, Peter; Rödiger, Stefan; " +
                         "Burdukiewicz, Michał; Himmelreich, Ralf; Klat, Katharina";
        assertEquals(authors, this.citationExtractor.getAuthors());

    }

    @Test public void testGetDoi() throws Exception {
        String doi = "10.1373/clinchem.2014.230656";
        assertEquals(doi, this.citationExtractor.getDoi());
    }

    @Test public void testGetIsbn() throws Exception {
        String isbn = "";
        assertEquals(isbn, this.citationExtractor.getIsbn());
    }

    @Test public void testGetPublisher() throws Exception {
        String publisher = "American Association for Clinical Chemistry Inc.";
        assertEquals(publisher, this.citationExtractor.getPublisher());
    }

     @Test public void testGetJournalTitle() throws Exception {
        String jtitle = "Clinical Chemistry";
        assertEquals(jtitle, this.citationExtractor.getJournalTitle());
    }

    @Test public void testGetCreateDate() throws Exception {
        String expected = "20150201";
        assertEquals(expected, this.citationExtractor.getCreateDate());
    }

    @Test public void testGetIssn() throws Exception {
        String expected = "00099147";
        assertEquals(expected, this.citationExtractor.getIssn());
    }

    @Test public void testGetVolume() throws Exception {
        String expected = "61";
        assertEquals(expected, this.citationExtractor.getVolume());
    }

    @Test public void testGetIssue() throws Exception {
        String expected = "2";
        assertEquals(expected, this.citationExtractor.getIssue());
    }

    @Test public void testGetStartPage() throws Exception {
        String expected = "379";
        assertEquals(expected, this.citationExtractor.getStartPage());
    }

    @Test public void testGetEndPage() throws Exception {
        String expected = "388";
        assertEquals(expected, this.citationExtractor.getEndPage());
    }
}