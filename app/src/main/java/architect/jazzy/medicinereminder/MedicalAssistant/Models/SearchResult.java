package architect.jazzy.medicinereminder.MedicalAssistant.Models;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 19-Aug-15.
 */
public class SearchResult {
  String term, file, server;
  int count = -1, retstart, retmax;
  ArrayList<WebDocument> webDocuments;
  String spellingCorrection = "";

  public SearchResult() {
  }

  public String getSpellingCorrection() {
    return spellingCorrection;
  }

  public void setSpellingCorrection(String spellingCorrection) {
    this.spellingCorrection = spellingCorrection;
  }

  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public int getRetstart() {
    return retstart;
  }

  public void setRetstart(int retstart) {
    this.retstart = retstart;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getRetmax() {
    return retmax;
  }

  public void setRetmax(int retmax) {
    this.retmax = retmax;
  }

  public ArrayList<WebDocument> getWebDocuments() {
    return webDocuments;
  }

  public void setWebDocuments(ArrayList<WebDocument> webDocuments) {
    this.webDocuments = webDocuments;
  }
}
