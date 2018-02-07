package application;
/**
 * 
 * @author yasuda
 * 相関係数行列をTableView に表示するための Observable リストを作成するためのクラス
 */
public class CCorr {
	//TableView には「行変数」ごとに表示するので、変数名が最初にくる
	String varName;
	//対象変数と、列変数とのあいだの相関係数
	double[] corr;
	//
	public CCorr(String str, double[] data) {
		this.varName = str;
		this.corr = data;
	}
	//
	public String getName() {
		return this.varName;
	}
	public double[] getCorr() {
		return this.corr;
	}
}
