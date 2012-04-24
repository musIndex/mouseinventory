package edu.ucsf.mousedatabase.objects;

public class Facility {
	private int facilityID;
	private String facilityName;
	private String facilityDescription;
	private String facilityCode;
	
	private int recordCount;
	public int getRecordCount()
	{
		return recordCount;
	}
	public void setRecordCount(int recordCount)
	{
		this.recordCount = recordCount;
	}
	public String getFacilityDescription()
	{
		return facilityDescription;
	}
	public void setFacilityDescription(String facilityDescription)
	{
		this.facilityDescription = facilityDescription;
	}
	public int getFacilityID() {
		return facilityID;
	}
	public void setFacilityID(int facilityID) {
		this.facilityID = facilityID;
	}
	public String getFacilityName() {
		return facilityName;
	}
	public void setFacilityName(String facility) {
		this.facilityName = facility;
	}
	public String getFacilityCode() {
		return facilityCode;
	}
	public void setFacilityCode(String facilityCode) {
		this.facilityCode = facilityCode;
	}
	
}
