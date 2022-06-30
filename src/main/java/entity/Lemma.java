package entity;

import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Lemma {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  private String lemma;

  @NotNull
  private int frequency;

  public Lemma() {
  }

  public Lemma(String lemma) {
    this.lemma = lemma;
  }

  public Lemma(String lemma, int frequency) {
    this(lemma);
    this.frequency = frequency;
  }

  public int getId() {
    return id;
  }
  public String getLemma() {
    return lemma;
  }
  public void setLemma(String lemma) {
    this.lemma = lemma;
  }
  public int getFrequency() {
    return frequency;
  }
  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }
}
