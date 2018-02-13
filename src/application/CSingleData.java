package application;

import java.util.ArrayList;
import java.util.List;

public class CSingleData {
	String dataName;
	List<Integer> data ;
	//
	public CSingleData(String str) {
		dataName = new String(str);
		data = new ArrayList<Integer>();
	}
	//
	public String getDataName(){
		return this.dataName;
	}
	//
	public void setData(int d) {
		this.data.add(d);
	}
}
