package edu.umn.lib.primo.pushto;

import com.exlibris.primo.soap.messages.AddataType;
import com.exlibris.primo.soap.messages.PrimoNMBibDocument;
import org.apache.commons.lang.StringUtils;

/**
 * Provides convenience methods to get citation data from a PNX record.
 *
 * If a method is plural (e.g. getAuthors), all PNX values will be
 * returned in a single semicolon-delimited string.
 *
 * If a method is singular (e.g. getIsbn), only the first occurrence
 * of a given value in the PNX will be returned.
 *
 * If a method cannot find a given value in the PNX, it will return
 * an empty string.
 *
 */
public class PnxCitationExtractor {
    private final PrimoNMBibDocument.PrimoNMBib pnx;
    private final AddataType pnxAddata;

    private String removeMarkup(String s) { return s.replaceAll("<.*?>", ""); }

    public PnxCitationExtractor(PrimoNMBibDocument.PrimoNMBib pnx) {
        this.pnx = pnx;
        this.pnxAddata = pnx.getRecordArray(0).getAddata();
    }

    public String getTitle() {
        return removeMarkup(this.pnx.getRecordArray(0).getDisplay().getTitleArray(0));
    }

    public String getAuthors() {
        if (this.pnxAddata.sizeOfAuArray() < 1) {
            return "";
        } else {
            return removeMarkup(StringUtils.join(this.pnxAddata.getAuArray(), "; "));
        }
    }

    public String getDoi() {
        if (this.pnxAddata.sizeOfDoiArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getDoiArray(0));
        }
    }

    public String getJournalTitle() {
        if (this.pnxAddata.sizeOfJtitleArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getJtitleArray(0));
        }
    }

    public String getPublisher() {
        if (this.pnxAddata.sizeOfPubArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getPubArray(0));
        }
    }

    public String getIsbn() {
        if (this.pnxAddata.sizeOfIsbnArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getIsbnArray(0));
        }
    }

    public String getCreateDate() {
        if (this.pnxAddata.sizeOfDateArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getDateArray(0));
        }
    }

    public String getIssn() {
        if (this.pnxAddata.sizeOfIssnArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getIssnArray(0));
        }
    }

    public String getVolume() {
        if (this.pnxAddata.sizeOfVolumeArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getVolumeArray(0));
        }
    }

    public String getIssue() {
        if (this.pnxAddata.sizeOfIssueArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getIssueArray(0));
        }
    }

   public String getStartPage() {
        if (this.pnxAddata.sizeOfSpageArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getSpageArray(0));
        }
    }

   public String getEndPage() {
        if (this.pnxAddata.sizeOfEpageArray() < 1) {
            return "";
        } else {
            return removeMarkup(this.pnxAddata.getEpageArray(0));
        }
    }

}
