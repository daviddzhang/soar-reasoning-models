package reasoningmodels.naivebayes;

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
import reasoningmodels.knn.KNN;

public class NBDemo {
  public static void main(String[] args) throws IOException {

    NaiveBayes nb = new NaiveBayes("sign");

    Reader dataFile = new FileReader("/Users/davidzhang/Downloads/data2.csv");
    CSVReader reader = new CSVReader(dataFile);

    Iterator<String[]> iterator = reader.iterator();

    String[] headers = iterator.next();
    nb.addFeature(headers[0], new String[]{"red", "blue", "black", "orange"});
    nb.addFeature(headers[1], new String[]{"square", "circle"});
    nb.addFeature(headers[2], new String[]{"plus", "minus"});

    while (iterator.hasNext()) {
      List<IFeature> currentFeatures = new ArrayList<>();
      String[] current = iterator.next();
      for (int i = 0; i < current.length; i++) {
        currentFeatures.add(FeatureFactory.createFeature(headers[i], current[i],
                nb.getFeatures().get(headers[i])));
      }
      nb.train(new EntryImpl(currentFeatures));

    }

    IFeature blue = new CategoricalFeature("color",
            KNN.getVectorForCategoricalValue(nb.getFeatures().get("color"), "blue"), "blue");
    IFeature orange = new CategoricalFeature("color",
            KNN.getVectorForCategoricalValue(nb.getFeatures().get("color"), "orange"), "orange");
    IFeature square = new CategoricalFeature("shape",
            KNN.getVectorForCategoricalValue(nb.getFeatures().get("shape"), "square"), "square");
    List<IFeature> queryFeatures = new ArrayList<>(Arrays.asList(orange, square));
    IEntry queryEntry = new EntryImpl(queryFeatures);



    System.out.println(nb.toString() + "\n");
    System.out.println(nb.query(queryEntry));
  }
}
