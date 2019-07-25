package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public  class BayesSampler {

  public static class SingleVarSample {
    Random rand;
    double occurrence;

    public SingleVarSample(double occurrence) {
      this.occurrence = occurrence;
      this.rand = new Random();
    }

    SingleVarSample(double occurrence, int rand) {
      this.occurrence = occurrence;
      this.rand = new Random(rand);
    }

    public boolean sample() {
      double random = rand.nextDouble();

      if (random <= this.occurrence) {
        return true;
      } else {
        return false;
      }
    }
  }

  public static class MultiVarSample {
    Random rand;
    HashMap<List<IRandomVariable>, Double> prob;

    public MultiVarSample(HashMap<List<IRandomVariable>, Double> prob) {
      this.prob = prob;
      this.rand = new Random();
    }

    public MultiVarSample(HashMap<List<IRandomVariable>, Double> prob, int rand) {
      this.prob = prob;
      this.rand = new Random(rand);
    }

    public boolean sample(ArrayList<IRandomVariable> givens) {
      double probForGiven = this.prob.get(givens);
      double random = this.rand.nextDouble();

      if (random <= probForGiven) {
        return true;
      } else {
        return false;
      }

    }
  }


  public static class BurglaryData {
    Random rand;
    double occurrence = 0.9;

    public BurglaryData() {
      this.rand = new Random();
    }

    public BurglaryData(int rand) {
      this.rand = new Random(rand);
    }

    public boolean sample() {
      double random = rand.nextDouble();

      if (random <= this.occurrence) {
        return true;
      } else {
        return false;
      }
    }
  }

  public static class EarthquakeData {
    Random rand;
    double occurrence = 0.3;

    public EarthquakeData() {
      this.rand = new Random();
    }

    // Constructor for testing
    public EarthquakeData(int randSeed) {
      this.rand = new Random(randSeed);
    }

    public boolean sample() {
      double random = rand.nextDouble();

      if (random <= this.occurrence) {
        return true;
      } else {
        return false;
      }
    }
  }

  public static class AlarmData {
    Random rand = new Random();
    HashMap<List<IRandomVariable>, Double> prob = new HashMap<List<IRandomVariable>, Double>();

    public AlarmData() {
      this.initializeCPT();
    }

    public AlarmData(int randSeed) {
      this.initializeCPT();
      this.rand = new Random(randSeed);
    }

    void initializeCPT() {
      IRandomVariable eVarPlus = new RandomVariableImpl("E", true);
      IRandomVariable eVarMinus = new RandomVariableImpl("E", false);
      IRandomVariable bVarPlus = new RandomVariableImpl("B", true);
      IRandomVariable bVarMinus = new RandomVariableImpl("B", false);

      ArrayList<IRandomVariable> ePlusBPlus = new ArrayList<IRandomVariable>(Arrays.asList(eVarPlus,
              bVarPlus));
      ArrayList<IRandomVariable> eMinusBPlus =
              new ArrayList<IRandomVariable>(Arrays.asList(eVarMinus, bVarPlus));
      ArrayList<IRandomVariable> eMinusBMinus =
              new ArrayList<IRandomVariable>(Arrays.asList(eVarMinus, bVarMinus));
      ArrayList<IRandomVariable> ePlusBMinus = new ArrayList<IRandomVariable>(Arrays.asList(eVarPlus
              , bVarMinus));

      this.prob.put(ePlusBPlus, .99);
      this.prob.put(eMinusBPlus, .9);
      this.prob.put(eMinusBMinus, .1);
      this.prob.put(ePlusBMinus, .15);
    }

    public boolean sample(ArrayList<IRandomVariable> givens) {
      double probForGiven = this.prob.get(givens);
      double random = this.rand.nextDouble();

      if (random <= probForGiven) {
        return true;
      } else {
        return false;
      }

    }
  }

  public static class JohnData {
    Random rand = new Random();
    HashMap<List<IRandomVariable>, Double> prob = new HashMap<List<IRandomVariable>, Double>();

    public JohnData() {
      this.initializeCPT();
    }

    public JohnData(int rand) {
      this.initializeCPT();
      this.rand = new Random(rand);
    }

    void initializeCPT() {
      IRandomVariable aPlus = new RandomVariableImpl("A", true);
      IRandomVariable aMinus = new RandomVariableImpl("A", false);

      ArrayList<IRandomVariable> aPlusList = new ArrayList<IRandomVariable>(Arrays.asList(aPlus));
      ArrayList<IRandomVariable> aMinusList = new ArrayList<IRandomVariable>(Arrays.asList(aMinus));

      this.prob.put(aPlusList, .9);
      this.prob.put(aMinusList, .1);
    }

    public boolean sample(ArrayList<IRandomVariable> givens) {
      double probForGivens = this.prob.get(givens);
      double random = this.rand.nextDouble();

      if (random <= probForGivens) {
        return true;
      } else {
        return false;
      }
    }
  }

  public static class MaryData {
    Random rand = new Random();
    HashMap<List<IRandomVariable>, Double> prob = new HashMap<List<IRandomVariable>, Double>();

    public MaryData() {
      this.initializeCPT();
    }

    public MaryData(int rand) {
      this.initializeCPT();
      this.rand = new Random(rand);
    }


    void initializeCPT() {
      IRandomVariable aPlus = new RandomVariableImpl("A", true);
      IRandomVariable aMinus = new RandomVariableImpl("A", false);

      ArrayList<IRandomVariable> aPlusList = new ArrayList<IRandomVariable>(Arrays.asList(aPlus));
      ArrayList<IRandomVariable> aMinusList = new ArrayList<IRandomVariable>(Arrays.asList(aMinus));

      this.prob.put(aPlusList, .7);
      this.prob.put(aMinusList, .15);
    }

    public boolean sample(ArrayList<IRandomVariable> givens) {
      double probForGivens = this.prob.get(givens);
      double random = this.rand.nextDouble();

      if (random <= probForGivens) {
        return true;
      } else {
        return false;
      }
    }
  }
}