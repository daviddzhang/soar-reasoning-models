package reasoningmodels.bayesnet;

import java.util.List;

/**
 * This interface represents a node in a Bayes Net. Each node should hold their own parents and
 * CPT. The specifics of how to hold and maintain this information should be decided by the
 * specific implementation. Each implementation should be able to update the CPT, perform CPT
 * operations, query for node information, and output to a string.
 */
public interface INode {

  @Override
  String toString();

  /**
   * Updates the node's CPT with the given training example. Details on how this is done should
   * be documented by specific implementations.
   */
  void updateCPT(List<IRandomVariable> trainingEx);

  /**
   * Gets the name of the node.
   *
   * @return the node name
   */
  String getNodeName();

  /**
   * Creates a new node whose CPTs are inference CPTs, or include this node's random variable in
   * the entries.
   *
   * @return a new INode whose CPTs are suitable for inference operations
   */
  INode convertToInferenceCPT();

  /**
   * Gets the parents of this node in the form of a list of names.
   *
   * @return a list of names representing the parents
   */
  List<String> getParents();

  /**
   * Returns whether this node has the given parent.
   *
   * @param parent name of the parent to check for
   * @return true if the node has the parent, false otherwise
   */
  boolean hasParent(String parent);

  /**
   * Joins this node's CPT with a given CPT, and returns the result CPT. It performs the join
   * over the given join variable.
   *
   * @param other CPT to join with
   * @param joinVar the variable name to join on
   * @return the joined CPTs
   * @throws IllegalArgumentException if the given variable is not found in either of the CPTs
   */
  ICPT join(ICPT other, String joinVar) throws IllegalArgumentException;

  /**
   * Returns a copy of this node's CPT.
   *
   * @return the CPT
   */
  ICPT getCPT();

}
