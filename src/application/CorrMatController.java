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
	// データのリストをつくる。フィールド分のCData インスタンスを保持する
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
		log.appendText("データファイルに" + dataFile.getAbsolutePath() + "がセットされました。");
		//
		String line = null;
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(dataFile), "JISAutoDetect"));
			line = br.readLine(); // 最初の行がフィールド行
			fieldNameArray = line.split(","); // フィールドをString 配列に入れる
			log.appendText("\nフィールド長 = " + fieldNameArray.length);
			// ComboBox 配列にフィールド名を入れて選択可能にする
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
			// ここまでで、フィールド名のcomboBox への登録は終わり。つづいてデータを保存しておく
			// 縦方向にデータを取る必要があるので、冗長だがListをいったん作る
			List<String> l = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				l.add(line);
			}
			// List からサイズを獲得
			int n = l.size();
			// フィールドの数だけ配列をつくってデータをいれ、それで CData を作成

			for (int j = 0; j < fieldNameArray.length; j++) {
				String[] tmpData = new String[n];
				for (int i = 0; i < n; i++) { // 1行ずつみていく
					String[] tmp = l.get(i).split(",");
					// よみとった1行の配列をフィールド順にデータを読む
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
		// 相関係数を計算するべきフィールド番号をいれておく
		List<Integer> vPosList = new ArrayList<Integer>();
		// ComoboBox リストに設定されたフィールド名を取得する
		for (ComboBox c : colComboArray) {
			// fieldNameArray と比較して位置を返す
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
