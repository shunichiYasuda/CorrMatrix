package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
		log.appendText("データファイルに" + dataFile.getAbsolutePath() + "がセットされました。");
		//
		String line = null;
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(dataFile), "JISAutoDetect"));
			line = br.readLine(); //最初の行がフィールド行
			String[] tmpField = line.split(","); //フィールドをString 配列に入れる
			log.appendText("\nフィールド長 = " + tmpField.length);
			//ComboBox 配列にフィールド名を入れて選択可能にする
			for(ComboBox<String> c : colComboArray) {
				for(String s: tmpField) {
					c.getItems().add(s);
				}
			}
			for(ComboBox<String> r: rowComboArray) {
				for(String s:tmpField) {
					r.getItems().add(s);
				}
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
		//

	}

	@FXML
	private void saveAction() {

	}
}
