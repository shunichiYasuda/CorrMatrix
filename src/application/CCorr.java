package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author yasuda
 * ���֌W���s���TableView �ɕ\�����邽�߂� Observable ���X�g���쐬���邽�߂̃N���X
 */
public class CCorr {
	//TableView �ɂ́u�s�ϐ��v���Ƃɕ\������̂ŁA�ϐ������ŏ��ɂ���
	StringProperty varName;
	//�Ώەϐ��ƁA��ϐ��Ƃ̂������̑��֌W��
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
