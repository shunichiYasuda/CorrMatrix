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
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class CorrMatController {
	File dataFile;
	String[] fieldNameArray;
	// �f�[�^�̃��X�g������B�t�B�[���h����CData �C���X�^���X��ێ�����
	List<CData> dataList = new ArrayList<CData>();
	@FXML
	TextArea log;
	@FXML
	TableView<String> corrMatrix;
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
		List<Integer> vPosList = new ArrayList<Integer>();
		// ComoboBox ���X�g�ɐݒ肳�ꂽ�t�B�[���h�����擾����
		for (ComboBox<String> c : colComboArray) {
			// fieldNameArray �Ɣ�r���Ĉʒu��Ԃ�
			int pos = 0;
			for (String s : fieldNameArray) {
				if (s.equals((String) c.getValue())) {
					log.appendText("\ncol[" + pos + "]:\t" + (String) c.getValue());
					vPosList.add(pos);
					colFieldList.getItems().add(s);
				}
				pos++;
			}
		} // end of ��ϐ��̃Z�b�g
			// �s�ϐ����̃Z�b�g���K�v
		List<Integer> hPosList = new ArrayList<Integer>();
		for (ComboBox<String> c : rowComboArray) {
			int pos = 0;
			for (String s : fieldNameArray) {
				if (s.equals((String) c.getValue())) {
					log.appendText("\nrow[" + pos + "]:\t" + (String) c.getValue());
					hPosList.add(pos);
					rowFieldList.getItems().add(s);
				}
				pos++;
			}
		}
		// �����܂łŁAvPosList �� hPosList �ɑΏۂƂȂ�t�B�[���h�ԍ��������Ă���B
		// ���ɁAvPosList �ɓ����Ă���ԍ��̂��ꂼ��� hPosList �ɓ����Ă���ԍ������ԂɑΉ�������B
		// �Ƃ肠�����A�y�A���m�F���Ă݂�
		int vPos, hPos;
		for (Integer v : vPosList) {
			vPos = v.intValue();
			for (Integer h : hPosList) {
				hPos = h.intValue();
				System.out.println("(" + vPos + "," + hPos + ")");
				// �y�A�ɂȂ����ԍ�����A���̃t�B�[���h�̃f�[�^��ǂݍ��ނ̂����A�ǂ��炩�̃f�[�^���󔒂ł�������A
				// �r���R�[�h�ł������肵���ꍇ�Ƀf�[�^���u�߂�v�K�v������B
				// ����͂��Ȃ��������Ȃ̂ŕʃ��\�b�h�ɂ���
				dataCleaning(vPos, hPos);
			}
		}
		//
	}// end of execAction()

	// �f�[�^�N���[�j���O
	private void dataCleaning(int v, int h) {
		// dataList ���󂳂Ȃ��悤�Ƀf�[�^���O�Ɏ��o���B
		String[] colOriginalDataStrArray = dataList.get(v).get();
		String[] rowOriginalDataStrArray = dataList.get(h).get();
		//�{����TextField ���珜�O�ԍ���ǂݎ�邪�A���܂͂����ŗ^����
		String[] eliminate = {"99","9",""};
		//�璷�ɂ͂Ȃ邪�A��������r���������܂߂āA���������킹�Ă����B
		int arraySize = colOriginalDataStrArray.length;
		if(rowOriginalDataStrArray.length < arraySize) {
			arraySize = rowOriginalDataStrArray.length;
		}
		//�Z�����ɂ��킹�āA��������z�������Ă���
		String[] colTmpDataArray = new String[arraySize];
		String[] rowTmpDataArray = new String[arraySize];
		//�f�[�^�𓪂���l�߂�B�܂蒷��������Ȃ���ΒZ�����ɍ��킹��
		for(int i=0;i<arraySize;i++) {
			colTmpDataArray[i] = colOriginalDataStrArray[i];
			rowTmpDataArray[i] = rowOriginalDataStrArray[i];
		}
		//�����𓪂���`�F�b�N���Ă����r����������������߂�̂������
		//�ǂ��炩�ɂ���΁A�����̂��̏ꏊ���l�߂Ȃ���΂Ȃ�Ȃ��̂Ŕz��ň����ɂ�
		//��������Ȃ̂ŁAList���g��
		// check
		for (String s : colOriginalDataStrArray) {
			//�������`�F�b�N
			if (checkChar(s,eliminate)) {
				System.out.print("��");
			} else {
				System.out.print("\t" + s);
			}
		}
		System.out.println();
	}
	//���O�������ǂ����̔��f�B���O�����Ȃ� true
	private boolean checkChar(String in, String[] e) {
		boolean r = false;
		for(String s: e) {
			if(in.equals(s)) r = true;
		}
		return r;
	}

	@FXML
	private void saveAction() {

	}
}
