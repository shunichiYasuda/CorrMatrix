package application;
/**
 * 
 * @author yasuda
 * ���֌W���s���TableView �ɕ\�����邽�߂� Observable ���X�g���쐬���邽�߂̃N���X
 */
public class CCorr {
	//TableView �ɂ́u�s�ϐ��v���Ƃɕ\������̂ŁA�ϐ������ŏ��ɂ���
	String varName;
	//�Ώەϐ��ƁA��ϐ��Ƃ̂������̑��֌W��
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
