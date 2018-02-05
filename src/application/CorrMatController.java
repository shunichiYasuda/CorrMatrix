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
		for (ComboBox c : colComboArray) {
			// fieldNameArray �Ɣ�r���Ĉʒu��Ԃ�
			int pos = 0;
			for(String s : fieldNameArray) {
				if (s.equals((String) c.getValue())) {
					log.appendText("\n" + pos + ":" + s + "\t" + (String) c.getValue());
					vPosList.add(pos);
				}
				pos++;
			}
		}

	}

	@FXML
	private void saveAction() {

	}
}
