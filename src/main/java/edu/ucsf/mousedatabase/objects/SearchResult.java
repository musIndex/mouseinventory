package edu.ucsf.mousedatabase.objects;

import java.util.ArrayList;

public class SearchResult {
  private SearchStrategy strategy;
  private ArrayList<Integer> matchingIds;
  
  public String getComment() {
    return getStrategy().getComment();
  }
  public int getTotal() {
    if (getMatchingIds() == null) return -1;
    return getMatchingIds().size();
  }
  public ArrayList<Integer> getMatchingIds() {
    return matchingIds;
  }
  public void setMatchingIds(ArrayList<Integer> matchingIds) {
    this.matchingIds = matchingIds;
  }
  public SearchStrategy getStrategy() {
    return strategy;
  }
  public void setStrategy(SearchStrategy strategy) {
    this.strategy = strategy;
  }

  
  
}
