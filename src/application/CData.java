package application;

import java.util.List;
//add CorrMatrix
public class CData {
	String[] data;
	//
	public CData(String[] d) {
		data = new String[d.length];
		data = d;
	}
	//
	public CData(List<String> list) {
		data = new String[list.size()];
		for(int i=0;i<data.length;i++) {
			data[i] = new String(list.get(i));
		}
	}
	//
	public String[] get() {
		return this.data;
	}
}
