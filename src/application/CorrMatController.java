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
	// データのリストをつくる。フィールド分のCData インスタンスを保持する
	List<CData> dataList = new ArrayList<CData>();
	// 相関係数行列表示のために、行データ、相関係数1、相関係数2,...のクラスが必要
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
				// 選択肢の最初に空白を入れておく
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
		List<Integer> colPosList = new ArrayList<Integer>();
		// 変数リストのクリア
		colFieldList.getItems().clear();
		// ComoboBox リストに設定されたフィールド名を取得する
		for (ComboBox<String> c : colComboArray) {
			// fieldNameArray と比較して位置を返す
			int pos = 0;
			for (String s : fieldNameArray) {
				if (s.equals((String) c.getValue())) {
					log.appendText("\ncol[" + pos + "]:\t" + (String) c.getValue());
					colPosList.add(pos);
					colFieldList.getItems().add(s);
				}
				pos++;
			}
		} // end of 列変数のセット
			// 変数リストのクリア
		rowFieldList.getItems().clear();
		// 行変数名のセットが必要
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
		// ここまでで、vPosList と hPosList に対象となるフィールド番号が入っている。
		// 次に、vPosList に入っている番号のそれぞれに hPosList に入っている番号を順番に対応させる。
		// とりあえず、ペアを確認してみる
		int colPos, rowPos;
		// 相関係数表がはいる double配列を作っておく
		double[][] corr = new double[rowPosList.size()][colPosList.size()];
		int indexCol = 0;
		for (Integer v : colPosList) {
			int indexRow = 0;
			colPos = v.intValue();
			for (Integer h : rowPosList) {
				rowPos = h.intValue();
				System.out.println("(" + rowPos + "," + colPos + ")");
				// ペアになった番号から、そのフィールドのデータを読み込むのだが、どちらかのデータが空白であったり、
				// 排除コードであったりした場合にデータを「つめる」必要がある。
				// これはかなりやっかいなので別メソッドにする
				// ここで相関係数を計算していた方がわかりやすいので、data cleaning を含めて計算メソッドを作る
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
		// 相関係数行列を TableView に表示するためにもう一工夫。
		// TableView に表示する TableColumnは変数の選択によって数が異なる。
		// さらに初期値として変数名を各TableColumn の先頭にいれるし、最初の列は CCorr リストのvarNameが並ぶ
		// TableColumn の数が実行時に変化するので、これをどうするかが問題。
		//TableColumn が固定されていれば、データモデルのフィールドに合わせて CellFactory を使えばよいのだが
		//ここではそうは行かない。
		// 列方向の変数の数は corr の列数なので、ここでカウントしておく
		int vVarNum = corr[0].length;
		// CCorr クラスリストを作成する
		// ここで分かっているのは行変数名、列変数名の番号なので、番号からフィールド名を引いてCCorr をセット
		// 計算された相関計数行列 corr の列数分の配列を作っておく
		double[] valueArray = new double[vVarNum];
		List<CCorr> corrList = new ArrayList<CCorr>();
		// 行変数名と corr 各行のデータ
		int index = 0;
		for (Integer n : rowPosList) {
			int pos = (int) n;
			String name = fieldNameArray[pos];
			corrList.add(new CCorr(name, corr[index]));
			index++;
		}
		// corrList の中身
		for (CCorr c : corrList) {
			log.appendText("\n" + c.varNameProperty().toString() + ":");
			for(DoubleProperty d:c.corrProperty()) {
				log.appendText("\t"+d.doubleValue());
			}
		}
		//corrList をObservableList にしてみる
		ObservableList<CCorr> obCorr = FXCollections.observableList(corrList);
		// 最初の列
		TableColumn<CCorr, String> first = new TableColumn<CCorr, String>("var name");
		first.setCellValueFactory(new PropertyValueFactory<CCorr,String>("varName"));
		// 以降の列は選択した変数の数によって異なる
		// 第1列以外をListにしてみる
		List<TableColumn> columnList = new ArrayList<TableColumn>();
		for (Integer n : colPosList) {
			int pos = (int) n;
			String name = fieldNameArray[pos];
			columnList.add(new TableColumn<CCorr, String>(name));
			//corrMatrix.getColumns().add(c);
		}
		//第2列以降はフィールド名が入った状態で、空。そこに値をセットするが、 PropertyValueFactoryは使えない
		//そもそもTableView のcolumnにおけるcellの考え方自体が、「データモデルの一つの要素に対応する」となっているようなので、
		//配列の値をそれぞれ別の列に表示することはできないみたい
		//したがって、列の数が判明した時点で、配列を要素に分解したような値をデータモデルの外に出して
		//その値がならぶようにできないか？
		//columnList の要素にデータモデルを設定
		for(int i =0;i<columnList.size();i++) {
			final int n = i;
			//TableColumn 要素を取り出す
			columnList.get(i).setCellValueFactory(new Callback<CellDataFeatures<CCorr, Double>, ObservableValue<Double>>() {
			     public ObservableValue<Double> call(CellDataFeatures<CCorr, Double> p) {
			         // p.getValue() returns the Person instance for a particular TableView row
			         return p.getValue().getCorrValue(n);
			     }
			  });
		}
		corrMatrix.setItems(obCorr);
		//columnList を corrMatrix に add
		corrMatrix.getColumns().add(first);
		for(TableColumn c: columnList) {
			corrMatrix.getColumns().add(c);
		}
		

	}// end of execAction()
		//

	// データクリーニング
	private double calcCorr(int v, int h) {
		double r = 0.0;
		// dataList を壊さないようにデータを外に取り出す。
		String[] colOriginalDataStrArray = dataList.get(v).get();
		String[] rowOriginalDataStrArray = dataList.get(h).get();
		// 本当はTextField から除外番号を読み取るが、いまはここで与える
		String[] eliminate = { "99", "9", "" };
		// 冗長にはなるが、いったん排除文字を含めて、長さを合わせておく。
		int arraySize = colOriginalDataStrArray.length;
		if (rowOriginalDataStrArray.length < arraySize) {
			arraySize = rowOriginalDataStrArray.length;
		}
		// 短い方にあわせて、いったん配列を作っておく
		String[] colTmpDataArray = new String[arraySize];
		String[] rowTmpDataArray = new String[arraySize];
		// データを頭から詰める。つまり長さが合わなければ短い方に合わせる
		for (int i = 0; i < arraySize; i++) {
			colTmpDataArray[i] = colOriginalDataStrArray[i];
			rowTmpDataArray[i] = rowOriginalDataStrArray[i];
		}
		// 両方を頭からチェックしていき排除文字があったらつめるのだけれど
		// どちらかにあれば、両方のその場所を詰めなければならないので配列で扱うには
		// やっかいなので、Listを使う
		// 家での修正がレポジトリにあがっていないので、branchをつくってみた
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
		// 相関係数の計算
		// なんにせよ、平均、分散、共分散のメソッドを使うので double配列の方がよい。
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

	// 除外文字かどうかの判断。除外文字なら true
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
