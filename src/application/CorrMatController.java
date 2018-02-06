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
		for (ComboBox<String> c : colComboArray) {
			// fieldNameArray と比較して位置を返す
			int pos = 0;
			for (String s : fieldNameArray) {
				if (s.equals((String) c.getValue())) {
					log.appendText("\ncol[" + pos + "]:\t" + (String) c.getValue());
					vPosList.add(pos);
					colFieldList.getItems().add(s);
				}
				pos++;
			}
		} // end of 列変数のセット
			// 行変数名のセットが必要
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
		// ここまでで、vPosList と hPosList に対象となるフィールド番号が入っている。
		// 次に、vPosList に入っている番号のそれぞれに hPosList に入っている番号を順番に対応させる。
		// とりあえず、ペアを確認してみる
		int vPos, hPos;
		for (Integer v : vPosList) {
			vPos = v.intValue();
			for (Integer h : hPosList) {
				hPos = h.intValue();
				System.out.println("(" + vPos + "," + hPos + ")");
				// ペアになった番号から、そのフィールドのデータを読み込むのだが、どちらかのデータが空白であったり、
				// 排除コードであったりした場合にデータを「つめる」必要がある。
				// これはかなりやっかいなので別メソッドにする
				dataCleaning(vPos, hPos);
			}
		}
		//
	}// end of execAction()

	// データクリーニング
	private void dataCleaning(int v, int h) {
		// dataList を壊さないようにデータを外に取り出す。
		String[] colOriginalDataStrArray = dataList.get(v).get();
		String[] rowOriginalDataStrArray = dataList.get(h).get();
		//本当はTextField から除外番号を読み取るが、いまはここで与える
		String[] eliminate = {"99","9",""};
		//冗長にはなるが、いったん排除文字を含めて、長さを合わせておく。
		int arraySize = colOriginalDataStrArray.length;
		if(rowOriginalDataStrArray.length < arraySize) {
			arraySize = rowOriginalDataStrArray.length;
		}
		//短い方にあわせて、いったん配列を作っておく
		String[] colTmpDataArray = new String[arraySize];
		String[] rowTmpDataArray = new String[arraySize];
		//データを頭から詰める。つまり長さが合わなければ短い方に合わせる
		for(int i=0;i<arraySize;i++) {
			colTmpDataArray[i] = colOriginalDataStrArray[i];
			rowTmpDataArray[i] = rowOriginalDataStrArray[i];
		}
		//両方を頭からチェックしていき排除文字があったらつめるのだけれど
		//どちらかにあれば、両方のその場所を詰めなければならないので配列で扱うには
		//やっかいなので、Listを使う
		// check
		for (String s : colOriginalDataStrArray) {
			//文字をチェック
			if (checkChar(s,eliminate)) {
				System.out.print("空白");
			} else {
				System.out.print("\t" + s);
			}
		}
		System.out.println();
	}
	//除外文字かどうかの判断。除外文字なら true
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
