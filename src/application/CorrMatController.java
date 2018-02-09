package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class CorrMatController {
	File dataFile;
	String[] fieldNameArray;
	// �f�[�^�̃��X�g������B�t�B�[���h����CData �C���X�^���X��ێ�����
	List<CData> dataList = new ArrayList<CData>();
	// ���֌W���s��\���̂��߂ɁA�s�f�[�^�A���֌W��1�A���֌W��2,...�̃N���X���K�v
	List<CCorr> corrList = new ArrayList<CCorr>();
	@FXML
	TextArea log;
	@FXML
	TableView<CCorr> corrMatrix;
	@FXML
	ListView<String> colFieldList;
	@FXML
	ListView<String> rowFieldList;
	@FXML
	CheckBox varConCheck1;
	@FXML
	CheckBox varConCheck2;
	@FXML
	TextField varConEliminate;
	@FXML
	private List<ComboBox<String>> colComboArray;
	@FXML
	private List<ComboBox<String>> rowComboArray;

	@FXML
	private void quitAction() {
		System.exit(0);
	}

	@FXML
	private void openAction() {
		FileChooser fc = new FileChooser();
		dataFile = fc.showOpenDialog(log.getScene().getWindow());
		log.appendText("�f�[�^�t�@�C����" + dataFile.getAbsolutePath() + "���Z�b�g����܂����B");
		//
		String line = null;
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(dataFile), "JISAutoDetect"));
			line = br.readLine(); // �ŏ��̍s���t�B�[���h�s
			fieldNameArray = line.split(","); // �t�B�[���h��String �z��ɓ����
			log.appendText("\n�t�B�[���h�� = " + fieldNameArray.length);
			// ComboBox �z��Ƀt�B�[���h�������đI���\�ɂ���
			for (ComboBox<String> c : colComboArray) {
				// �I�����̍ŏ��ɋ󔒂����Ă���
				c.getItems().add(null);
				for (String s : fieldNameArray) {
					c.getItems().add(s);
				}
			}
			for (ComboBox<String> r : rowComboArray) {
				for (String s : fieldNameArray) {
					r.getItems().add(s);
				}
			}
			// �����܂łŁA�t�B�[���h����comboBox �ւ̓o�^�͏I���B�Â��ăf�[�^��ۑ����Ă���
			// �c�����Ƀf�[�^�����K�v������̂ŁA�璷����List������������
			List<String> l = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				l.add(line);
			}
			// List ����T�C�Y���l��
			int n = l.size();
			// �t�B�[���h�̐������z��������ăf�[�^������A����� CData ���쐬

			for (int j = 0; j < fieldNameArray.length; j++) {
				String[] tmpData = new String[n];
				for (int i = 0; i < n; i++) { // 1�s���݂Ă���
					String[] tmp = l.get(i).split(",");
					// ��݂Ƃ���1�s�̔z����t�B�[���h���Ƀf�[�^��ǂ�
					tmpData[i] = tmp[j];
				}
				dataList.add(new CData(tmpData));
			}

			br.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	private void execAction() {
		// ���֌W�����v�Z����ׂ��t�B�[���h�ԍ�������Ă���
		List<Integer> colPosList = new ArrayList<Integer>();
		// �ϐ����X�g�̃N���A
		colFieldList.getItems().clear();
		// ComoboBox ���X�g�ɐݒ肳�ꂽ�t�B�[���h�����擾����
		for (ComboBox<String> c : colComboArray) {
			// fieldNameArray �Ɣ�r���Ĉʒu��Ԃ�
			int pos = 0;
			for (String s : fieldNameArray) {
				if (s.equals((String) c.getValue())) {
					log.appendText("\ncol[" + pos + "]:\t" + (String) c.getValue());
					colPosList.add(pos);
					colFieldList.getItems().add(s);
				}
				pos++;
			}
		} // end of ��ϐ��̃Z�b�g
			// �ϐ����X�g�̃N���A
		rowFieldList.getItems().clear();
		// �s�ϐ����̃Z�b�g���K�v
		List<Integer> rowPosList = new ArrayList<Integer>();
		for (ComboBox<String> c : rowComboArray) {
			int pos = 0;
			for (String s : fieldNameArray) {
				if (s.equals((String) c.getValue())) {
					log.appendText("\nrow[" + pos + "]:\t" + (String) c.getValue());
					rowPosList.add(pos);
					rowFieldList.getItems().add(s);
				}
				pos++;
			}
		}
		// �����܂łŁAvPosList �� hPosList �ɑΏۂƂȂ�t�B�[���h�ԍ��������Ă���B
		// ���ɁAvPosList �ɓ����Ă���ԍ��̂��ꂼ��� hPosList �ɓ����Ă���ԍ������ԂɑΉ�������B
		// �Ƃ肠�����A�y�A���m�F���Ă݂�
		int colPos, rowPos;
		// ���֌W���\���͂��� double�z�������Ă���
		double[][] corr = new double[rowPosList.size()][colPosList.size()];
		int indexCol = 0;
		for (Integer v : colPosList) {
			int indexRow = 0;
			colPos = v.intValue();
			for (Integer h : rowPosList) {
				rowPos = h.intValue();
				System.out.println("(" + rowPos + "," + colPos + ")");
				// �y�A�ɂȂ����ԍ�����A���̃t�B�[���h�̃f�[�^��ǂݍ��ނ̂����A�ǂ��炩�̃f�[�^���󔒂ł�������A
				// �r���R�[�h�ł������肵���ꍇ�Ƀf�[�^���u�߂�v�K�v������B
				// ����͂��Ȃ��������Ȃ̂ŕʃ��\�b�h�ɂ���
				// �����ő��֌W�����v�Z���Ă��������킩��₷���̂ŁAdata cleaning ���܂߂Čv�Z���\�b�h�����
				corr[indexRow][indexCol] = calcCorr(rowPos, colPos);
				indexRow++;
			}
			indexCol++;
		}
		// check
		for (int i = 0; i < corr.length; i++) {
			log.appendText("\n");
			for (int j = 0; j < corr[0].length; j++) {
				log.appendText("\tcorr[" + i + "][" + j + "] = " + corr[i][j]);
			}
		}
		// ���֌W���s��� TableView �ɕ\�����邽�߂ɂ�����H�v�B
		// TableView �ɕ\������ TableColumn�͕ϐ��̑I���ɂ���Đ����قȂ�B
		// ����ɏ����l�Ƃ��ĕϐ������eTableColumn �̐擪�ɂ���邵�A�ŏ��̗�� CCorr ���X�g��varName������
		// TableColumn �̐������s���ɕω�����̂ŁA������ǂ����邩�����B
		//TableColumn ���Œ肳��Ă���΁A�f�[�^���f���̃t�B�[���h�ɍ��킹�� CellFactory ���g���΂悢�̂���
		//�����ł͂����͍s���Ȃ��B
		// ������̕ϐ��̐��� corr �̗񐔂Ȃ̂ŁA�����ŃJ�E���g���Ă���
		int vVarNum = corr[0].length;
		// CCorr �N���X���X�g���쐬����
		// �����ŕ������Ă���͍̂s�ϐ����A��ϐ����̔ԍ��Ȃ̂ŁA�ԍ�����t�B�[���h����������CCorr ���Z�b�g
		// �v�Z���ꂽ���֌v���s�� corr �̗񐔕��̔z�������Ă���
		double[] valueArray = new double[vVarNum];
		List<CCorr> corrList = new ArrayList<CCorr>();
		// �s�ϐ����� corr �e�s�̃f�[�^
		int index = 0;
		for (Integer n : rowPosList) {
			int pos = (int) n;
			String name = fieldNameArray[pos];
			corrList.add(new CCorr(name, corr[index]));
			index++;
		}
		// corrList �̒��g
		for (CCorr c : corrList) {
			log.appendText("\n" + c.varNameProperty().toString() + ":");
			for(DoubleProperty d:c.corrProperty()) {
				log.appendText("\t"+d.doubleValue());
			}
		}
		//corrList ��ObservableList �ɂ��Ă݂�
		ObservableList<CCorr> obCorr = FXCollections.observableList(corrList);
		// �ŏ��̗�
		TableColumn<CCorr, String> first = new TableColumn<CCorr, String>("var name");
		first.setCellValueFactory(new PropertyValueFactory<CCorr,String>("varName"));
		// �ȍ~�̗�͑I�������ϐ��̐��ɂ���ĈقȂ�
		// ��1��ȊO��List�ɂ��Ă݂�
		List<TableColumn> columnList = new ArrayList<TableColumn>();
		for (Integer n : colPosList) {
			int pos = (int) n;
			String name = fieldNameArray[pos];
			columnList.add(new TableColumn<CCorr, String>(name));
			//corrMatrix.getColumns().add(c);
		}
		//��2��ȍ~�̓t�B�[���h������������ԂŁA��B�����ɒl���Z�b�g���邪�A PropertyValueFactory�͎g���Ȃ�
		//��������TableView ��column�ɂ�����cell�̍l�������̂��A�u�f�[�^���f���̈�̗v�f�ɑΉ�����v�ƂȂ��Ă���悤�Ȃ̂ŁA
		//�z��̒l�����ꂼ��ʂ̗�ɕ\�����邱�Ƃ͂ł��Ȃ��݂���
		//���������āA��̐��������������_�ŁA�z���v�f�ɕ��������悤�Ȓl���f�[�^���f���̊O�ɏo����
		//���̒l���Ȃ�Ԃ悤�ɂł��Ȃ����H
		//columnList �̗v�f�Ƀf�[�^���f����ݒ�
		for(int i =0;i<columnList.size();i++) {
			final int n = i;
			//TableColumn �v�f�����o��
			columnList.get(i).setCellValueFactory(new Callback<CellDataFeatures<CCorr, Double>, ObservableValue<Double>>() {
			     public ObservableValue<Double> call(CellDataFeatures<CCorr, Double> p) {
			         // p.getValue() returns the Person instance for a particular TableView row
			         return p.getValue().getCorrValue(n);
			     }
			  });
		}
		corrMatrix.setItems(obCorr);
		//columnList �� corrMatrix �� add
		corrMatrix.getColumns().add(first);
		for(TableColumn c: columnList) {
			corrMatrix.getColumns().add(c);
		}
		

	}// end of execAction()
		//

	// �f�[�^�N���[�j���O
	private double calcCorr(int v, int h) {
		double r = 0.0;
		// dataList ���󂳂Ȃ��悤�Ƀf�[�^���O�Ɏ��o���B
		String[] colOriginalDataStrArray = dataList.get(v).get();
		String[] rowOriginalDataStrArray = dataList.get(h).get();
		// �{����TextField ���珜�O�ԍ���ǂݎ�邪�A���܂͂����ŗ^����
		String[] eliminate = { "99", "9", "" };
		// �璷�ɂ͂Ȃ邪�A��������r���������܂߂āA���������킹�Ă����B
		int arraySize = colOriginalDataStrArray.length;
		if (rowOriginalDataStrArray.length < arraySize) {
			arraySize = rowOriginalDataStrArray.length;
		}
		// �Z�����ɂ��킹�āA��������z�������Ă���
		String[] colTmpDataArray = new String[arraySize];
		String[] rowTmpDataArray = new String[arraySize];
		// �f�[�^�𓪂���l�߂�B�܂蒷��������Ȃ���ΒZ�����ɍ��킹��
		for (int i = 0; i < arraySize; i++) {
			colTmpDataArray[i] = colOriginalDataStrArray[i];
			rowTmpDataArray[i] = rowOriginalDataStrArray[i];
		}
		// �����𓪂���`�F�b�N���Ă����r����������������߂�̂������
		// �ǂ��炩�ɂ���΁A�����̂��̏ꏊ���l�߂Ȃ���΂Ȃ�Ȃ��̂Ŕz��ň����ɂ�
		// ��������Ȃ̂ŁAList���g��
		// �Ƃł̏C�������|�W�g���ɂ������Ă��Ȃ��̂ŁAbranch�������Ă݂�
		List<Integer> colDataList = new ArrayList<Integer>();
		List<Integer> rowDataList = new ArrayList<Integer>();
		for (int i = 0; i < arraySize; i++) {
			String colTmpStr = colTmpDataArray[i];
			String rowTmpStr = rowTmpDataArray[i];
			boolean checkFlag = true;
			if (checkChar(colTmpStr, eliminate))
				checkFlag = false;
			if (checkChar(rowTmpStr, eliminate))
				checkFlag = false;
			try {
				int tmpColData = Integer.parseInt(colTmpStr);
				int tmpRowData = Integer.parseInt(rowTmpStr);
			} catch (NumberFormatException e) {
				checkFlag = false;
			}
			if (checkFlag) {
				colDataList.add(Integer.parseInt(colTmpStr));
				rowDataList.add(Integer.parseInt(rowTmpStr));
			}
		} // end of for(int i=0 ...
			//
		System.out.println("after cleaninig....");
		System.out.println("row size =" + rowDataList.size() + "\tcol size =" + rowDataList.size());
		for (int i = 0; i < colDataList.size(); i++) {
			System.out.println((int) rowDataList.get(i) + "," + (int) colDataList.get(i));
		}
		// ���֌W���̌v�Z
		// �Ȃ�ɂ���A���ρA���U�A�����U�̃��\�b�h���g���̂� double�z��̕����悢�B
		int dataNum = colDataList.size();
		double[] x = new double[dataNum];
		double[] y = new double[dataNum];
		for (int i = 0; i < dataNum; i++) {
			x[i] = (double) rowDataList.get(i);
			y[i] = (double) colDataList.get(i);
		}
		r = corr(x, y);

		return r;
	}// end of calcCorr()

	private double corr(double[] x, double[] y) {
		double c = 0.0;
		c = corrVariance(x, y) / (stdDev(x) * stdDev(y));
		return c;
	}

	private double stdDev(double[] x) {
		return Math.sqrt(dev(x));
	}

	private double corrVariance(double[] x, double[] y) {
		double c = 0.0;
		double aveX = ave(x);
		double aveY = ave(y);
		double sum = 0.0;
		for (int i = 0; i < x.length; i++) {
			sum += (x[i] - aveX) * (y[i] - aveY);
		}
		c = sum / x.length;
		return c;
	}

	private double dev(double[] x) {
		double d = 0.0;
		double sum = 0.0;
		double a = ave(x);
		for (double v : x) {
			sum += (v - a) * (v - a);
		}
		d = sum / x.length;
		return d;
	}

	private double ave(double[] x) {
		double a = 0.0;
		double sum = 0.0;
		for (double c : x) {
			sum += c;
		}
		a = sum / x.length;
		return a;
	}

	// ���O�������ǂ����̔��f�B���O�����Ȃ� true
	private boolean checkChar(String in, String[] e) {
		boolean r = false;
		for (String s : e) {
			if (in.equals(s))
				r = true;
		}
		return r;
	}

	@FXML
	private void saveAction() {

	}
}
