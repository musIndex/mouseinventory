package edu.ucsf.mousedatabase.objects;

public class RGDResult {
    private String accessionID;
    private int type;
    private String symbol;
    private String name;
    private String authors;
    private String title;
    private String errorString;
    private boolean valid;
    private boolean isOGFPage;
    private String alleleAccessionID;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAccessionID() {
        return accessionID;
    }

    public void setAccessionID(String accessionID) {
        this.accessionID = accessionID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isOGFPage() {
        return isOGFPage;
    }

    public void setOGFPage(boolean OGFPage) {
        isOGFPage = OGFPage;
    }

    public String getAlleleAccessionID() {
        return alleleAccessionID;
    }

    public void setAlleleAccessionID(String alleleAccessionID) {
        this.alleleAccessionID = alleleAccessionID;
    }



}
