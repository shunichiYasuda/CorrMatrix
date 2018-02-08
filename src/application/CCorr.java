package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author yasuda
 * 相関係数行列をTableView に表示するための Observable リストを作成するためのクラス
 */
public class CCorr {
	//TableView には「行変数」ごとに表示するので、変数名が最初にくる
	StringProperty varName;
	//対象変数と、列変数とのあいだの相関係数
	DoubleProperty[] corr;
	//
	public CCorr(String str, double[] data) {
		this.varName= new SimpleStringProperty(str);
		this.corr = new DoubleProperty[data.length];
		for(int i=0;i<data.length;i++) {
			this.corr[i] = new SimpleDoubleProperty(data[i]);
		}
	}
	//
	public StringProperty varNameProperty() {
		return this.varName;
	}
	public DoubleProperty[] corrProperty() {
		return this.corr;
	}
}
