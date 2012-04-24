package edu.ucsf.mousedatabase.objects;

public class Gene {
	private String symbol;
	private String fullname;
	private String mgiID;
	private int geneRecordID;
	private int recordCount;
	public int getRecordCount()
	{
		return recordCount;
	}
	public void setRecordCount(int recordCount)
	{
		this.recordCount = recordCount;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getMgiID() {
		return mgiID;
	}
	public void setMgiID(String mgiID) {
		this.mgiID = mgiID;
	}
	public int getGeneRecordID() {
		return geneRecordID;
	}
	public void setGeneRecordID(int geneRecordID) {
		this.geneRecordID = geneRecordID;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
}
