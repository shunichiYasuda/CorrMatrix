package application;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class CSingleData {
	String dataName;
	List<Integer> data ;
	//
	MathContext mc;
	//
	public CSingleData(String str) {
		dataName = new String(str);
		data = new ArrayList<Integer>();
		mc = new MathContext(4);
	}
	//
	public String getDataName(){
		return this.dataName;
	}
	//
	public void setData(Integer d) {
		this.data.add(d);
	}
	//
	public BigDecimal getAve() {
		BigDecimal a=new BigDecimal(0);
		BigDecimal sum = new BigDecimal(0);
		for(Integer n:this.data) {
			BigDecimal add = new BigDecimal(n.intValue());
			sum =sum.add(add);
		}
		a = sum.divide(new BigDecimal(data.size()),mc);
		return a;
	}
	//
	public BigDecimal getDev() {
		BigDecimal dev = new BigDecimal(0);
		BigDecimal ave = this.getAve();
		BigDecimal sum = new BigDecimal(0);
		for(Integer n: this.data) {
			BigDecimal v = new BigDecimal(n.intValue());
			BigDecimal arg = v.subtract(ave);
			sum =sum.add(arg.pow(2));
		}
		dev = sum.divide(new BigDecimal(data.size()),mc);
		return dev;
	}
	//
	public BigDecimal getStdDev() {
		BigDecimal dev = this.getDev();
		return dev.sqrt(mc);
	}
	//
	public int getSize() {
		return this.data.size();
	}
}
