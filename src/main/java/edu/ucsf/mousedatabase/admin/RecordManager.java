package edu.ucsf.mousedatabase.admin;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.MGIConnect;
import edu.ucsf.mousedatabase.RGDConnect;
import edu.ucsf.mousedatabase.objects.*;

public class RecordManager {

    public static class PopulateMouseResult {
        public boolean Success;
        public String Message;

        public PopulateMouseResult(boolean success, String message) {
            super();
            Success = success;
            Message = message;
        }


    }

    public static class GeneInfo {
        public String mgiGeneID;
        public String targetMgiGeneID;
        public String manualGeneName;
        public String manualGeneSymbol;
        public String manualTargetGeneName;
        public String manualTargetGeneSymbol;
    }

    public static class AddGeneResult {
        public boolean Success;
        public String Message;

        public AddGeneResult(boolean success, String message) {
            Success = success;
            Message = message;
        }
    }

    public static PopulateMouseResult PopulateMouseDataFromRequest(MouseRecord newMouse, HttpServletRequest request) {
        PopulateMouseResult result = new PopulateMouseResult(true, null);

        GeneInfo geneInfo = new GeneInfo();
        geneInfo.manualGeneName = request.getParameter("geneManualName");
        geneInfo.manualGeneSymbol = request.getParameter("geneManualSymbol");
        geneInfo.manualTargetGeneName = request.getParameter("targetGeneManualName");
        geneInfo.manualTargetGeneSymbol = request.getParameter("targetGeneManualSymbol");
        geneInfo.mgiGeneID = request.getParameter("geneMGIID");
        geneInfo.targetMgiGeneID = request.getParameter("targetGeneMGIID");

        newMouse.setGeneLink(request.getParameter("geneMGIID"));

        if (newMouse.getExpressedSequence() != null) {
            if (newMouse.getExpressedSequence().equalsIgnoreCase("Mouse Gene (unmodified)")) {
                newMouse.setTargetGeneID(request.getParameter("exprSeqGene"));
            } else if (newMouse.getExpressedSequence().equalsIgnoreCase("Modified mouse gene or Other")) {
                newMouse.setOtherComment(request.getParameter("exprSeqComment"));
            } else if (newMouse.getExpressedSequence().equalsIgnoreCase("Reporter")) {
                newMouse.setReporter(request.getParameter("exprSeqRep"));
            }
        }
        AddGeneResult geneResult = CreateAndAddGenes(newMouse, geneInfo);
        if (!geneResult.Success) {
            result.Message += result.Message + "<br/>";
            result.Success = false;
        }

        AddHolders(newMouse, request);
        AddPubMedIds(newMouse, request);

        if (newMouse.isRat() && newMouse.isTG()) {
            if (newMouse.getRepositoryCatalogNumber() == null) {
                newMouse.setRepositoryCatalogNumber(request.getParameter("geneRGDID"));
            }
            if (newMouse.getOfficialSymbol() == null) {
                RGDResult geneType = RGDConnect.getGeneQuery(request.getParameter("geneRGDID"));
                if (geneType != null) {
                    newMouse.setSource(geneType.getSymbol());
                    if (newMouse.getOfficialMouseName() == null) {
                        newMouse.setOfficialMouseName(geneType.getName());
                    }
                }
            }
        }

        if (newMouse.isTG()){
            if (newMouse.getRegulatoryElement() == null){
                newMouse.setRegulatoryElement(request.getParameter("regulatoryElement"));
            }
        }
        return new PopulateMouseResult(true, null);
    }

    public static AddGeneResult CreateAndAddGenes(MouseRecord newMouse, GeneInfo geneInfo) {
        //convert MGI Gene IDs into mouse db gene IDs

        if (geneInfo.mgiGeneID != null && !geneInfo.mgiGeneID.isEmpty()) {
            String geneID;
            Gene geneObj = DBConnect.findGene(geneInfo.mgiGeneID);
            if (geneObj == null) {
                MGIResult result = MGIConnect.doMGIQuery(geneInfo.mgiGeneID, MGIConnect.MGI_MARKER, "This ID is not a valid MGI Gene Accession ID", false);
                if (result.isValid()) {
                    geneID = DBConnect.addGeneManually(result.getAccessionID(), result.getName(), result.getSymbol());
                } else if (geneInfo.manualGeneName == null || geneInfo.manualGeneName.isEmpty() || geneInfo.manualGeneSymbol == null || geneInfo.manualGeneSymbol.isEmpty()) {
                    String message = "Unable to automatically add gene because: " + result.getErrorString()
                            + "<br>.Please go back and manually enter the symbol and full name for the gene at MGI ID: " + geneInfo.mgiGeneID;
                    return new AddGeneResult(false, message);
                } else {
                    geneID = DBConnect.addGeneManually(result.getAccessionID(), geneInfo.manualGeneName, geneInfo.manualGeneSymbol);
                }
            } else {
                geneID = Integer.toString(geneObj.getGeneRecordID());
            }
            newMouse.setGeneID(geneID);
        }
        //mgiGeneID = request.getParameter("targetGeneMGIID");
        if (geneInfo.targetMgiGeneID != null && !geneInfo.targetMgiGeneID.isEmpty()) {
            String geneID;
            Gene geneObj = DBConnect.findGene(geneInfo.targetMgiGeneID);
            if (geneObj == null) {
                MGIResult result = MGIConnect.doMGIQuery(geneInfo.targetMgiGeneID, MGIConnect.MGI_MARKER, "This ID is not a valid MGI Gene Accession ID", false);
                if (result.isValid()) {
                    geneID = DBConnect.addGeneManually(result.getAccessionID(), result.getName(), result.getSymbol());
                } else if (geneInfo.manualTargetGeneName == null || geneInfo.manualTargetGeneName.isEmpty() || geneInfo.manualTargetGeneSymbol == null || geneInfo.manualTargetGeneSymbol.isEmpty()) {
                    String message = "Unable to automatically add gene because: " + result.getErrorString()
                            + "<br>.Please go back and manually enter the symbol and full name for the gene at MGI ID: " + geneInfo.targetMgiGeneID;
                    return new AddGeneResult(false, message);
                } else {
                    geneID = DBConnect.addGeneManually(result.getAccessionID(), geneInfo.manualTargetGeneName, geneInfo.manualTargetGeneSymbol);
                }
            } else {
                geneID = Integer.toString(geneObj.getGeneRecordID());
            }
            newMouse.setTargetGeneID(geneID);
        }
        return new AddGeneResult(true, null);
    }

    public static int RecordExists(MouseRecord newMouse) {
        if (newMouse.getRepositoryCatalogNumber() != null && !(newMouse.getRepositoryCatalogNumber().trim().equalsIgnoreCase("none"))) {
            try {
                int mID = -1;
                String recordIDString = newMouse.getMouseID();
                if (recordIDString != null && !recordIDString.isEmpty() && !recordIDString.equalsIgnoreCase("null")) {
                    mID = Integer.parseInt(newMouse.getMouseID());
                }

                return DBConnect.checkForDuplicates(Integer.parseInt(newMouse.getRepositoryCatalogNumber()), mID);

            } catch (NumberFormatException ex) {
                //ignore these exceptions, caused by abnormal mgi id
            }
        }
        return -1;
    }

    public static void AddHolders(MouseRecord newMouse, HttpServletRequest request) {
        //create nice and neat ArrayList of holders
        ArrayList<MouseHolder> holders = new ArrayList<MouseHolder>();

        String holderIDStr;
        int k = 0;
        while ((holderIDStr = request.getParameter("holder_id-" + k)) != null) {
            int holderID = HTMLGeneration.stringToInt(holderIDStr);
            if (holderID > 1) {
                MouseHolder holder = new MouseHolder();
                holder.setFacilityID(HTMLGeneration.stringToInt(request.getParameter("facility_id-" + k)));
                holder.setCovert(request.getParameter("covertHolder_-" + k) != null);
                holder.setCryoLiveStatus(request.getParameter("cryoLiveStatus-" + k));
                holder.setHolderID(holderID);
                holders.add(holder);
            }
            k++;
        }

        newMouse.setHolders(holders);
    }


    public static void AddFile(MouseRecord newMouse, File file) {
        //ArrayList<String> files = new ArrayList<String>();


        newMouse.setFilename(file.getName());
    }

    //public static void AddFiles(MouseRecord newMouse, )

    public static void AddPubMedIds(MouseRecord newMouse, HttpServletRequest request) {
        //create nice and neat ArrayList of PM IDs
        ArrayList<String> pmIDs = new ArrayList<String>();

        String pmID;
        int k = 1;
        while ((pmID = request.getParameter("pmid" + k)) != null) {
            pmIDs.add(pmID);
            k++;
        }
        newMouse.setPubmedIDs(pmIDs);
    }

}
