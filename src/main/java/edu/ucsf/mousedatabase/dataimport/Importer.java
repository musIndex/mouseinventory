package edu.ucsf.mousedatabase.dataimport;

import java.util.ArrayList;
import java.util.HashMap;

public interface Importer {
	public void HandleImport(int importTaskId,ArrayList<HashMap<String, String>> csvData,HashMap<String, String> parameters);
}
