package reasoningmodels.knn;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

public class KNNDemo {
  public static void main(String[] args) throws IOException {
    KNN knn = new KNN();

    Reader dataFile = new FileReader("/Users/davidzhang/Downloads/data2.csv");
    CSVReader reader = new CSVReader(dataFile);

    Iterator<String[]> iterator = reader.iterator();

    String[] headers = iterator.next();
    knn.addFeature(headers[0], new String[]{"red", "blue", "black", "orange"});
    knn.addFeature(headers[1], new String[]{"square", "circle"});
    knn.addFeature(headers[2], new String[]{"plus", "minus"});

    System.out.println(knn.toString());

//    while (iterator.hasNext()) {
//      String[] current = iterator.next();
//      for (String string : current) {
//        System.out.println(string);
//      }
//
//    }
  }
}
