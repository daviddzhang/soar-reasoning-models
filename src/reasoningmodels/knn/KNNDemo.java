package reasoningmodels.knn;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import reasoningmodels.classifiers.CategoricalFeature;
import reasoningmodels.classifiers.EntryImpl;
import reasoningmodels.classifiers.FeatureFactory;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

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

    while (iterator.hasNext()) {
      List<IFeature> currentFeatures = new ArrayList<>();
      String[] current = iterator.next();
      for (int i = 0; i < current.length; i++) {
        currentFeatures.add(FeatureFactory.createFeature(headers[i], current[i],
                knn.getFeatures().get(headers[i])));
      }
      knn.train(new EntryImpl(currentFeatures));

    }

    IFeature blue = new CategoricalFeature("color",
            KNN.getVectorForCategoricalValue(knn.getFeatures().get("color"), "blue"), "blue");
    IFeature square = new CategoricalFeature("shape",
            KNN.getVectorForCategoricalValue(knn.getFeatures().get("shape"), "square"), "square");
    List<IFeature> queryFeatures = new ArrayList<>(Arrays.asList(blue, square));
    IEntry queryEntry = new EntryImpl(queryFeatures);

    knn.query(queryEntry, 1);

    System.out.println(knn.toString() + "\n");
    System.out.println(knn.getResult());


  }
}
