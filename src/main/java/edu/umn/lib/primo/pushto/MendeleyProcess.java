package edu.umn.lib.primo.pushto;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.exlibris.primo.interfaces.PushToInterface;
import com.exlibris.primo.xsd.commonData.PrimoResult;
import org.apache.commons.lang3.Validate;

/**
 * Custom PushTo plugin for sending a Primo search result to the
 * Mendeley web importer (see: https://www.mendeley.com/import/).
 *
 * For details on the PushTo interface, see:
 * https://developers.exlibrisgroup.com/primo/integrations/frontend/pushto
 *
 */
public class MendeleyProcess implements PushToInterface {

    /**
     * Mendeley's web importer can't handle multiple records, so redirect
     * to the RIS export adaptor (which at least works with Mendeley Desktop).
     */
    private static void sendToRisExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuffer requestUrl = request.getRequestURL();
        String qstring = request.getQueryString()
                                .replace("pushToType=Mendeley",
                                         "pushToType=RISPushTo");
        String risRedirectUrl = requestUrl.append('?').append(qstring).toString();
        response.sendRedirect(risRedirectUrl);
    }

    public String pushTo(HttpServletRequest request, HttpServletResponse response, PrimoResult[] record, boolean fromBasket) throws Exception {

        if (record.length > 1) {
            sendToRisExport(request, response);
        }

        PushToMendeleyRequest pushToMendeleyRequest = new PushToMendeleyRequest(request, record[0]);

        if (pushToMendeleyRequest.isFromMendeley()) {
            MendeleyCitationPage citationPage = pushToMendeleyRequest.createCitationPage();
            PrintWriter out = response.getWriter();
            out.print(citationPage);
            out.flush();
            out.close();
        } else {
            String importUrl = pushToMendeleyRequest.getImportUrl();
            response.sendRedirect(importUrl);
        }

        return null;
    }

    @Deprecated
    public String getFormAction(){
        return null;
    }

    @Deprecated
    public String getContent(HttpServletRequest request,boolean fromBasket) throws IOException{
        return null;
    }
}
