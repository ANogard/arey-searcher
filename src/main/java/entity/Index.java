package entity;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "Index_rank")
public class Index {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  @Column(name = "page_id")
  private int pageId;

  @NotNull
  @Column(name = "lemma_id")
  private int lemmaId;

  @NotNull
  @Column(name = "rank_value")
  private float rank;

  public Index() {
  }

  public Index(int pageId, int lemmaId) {
    this.pageId = pageId;
    this.lemmaId = lemmaId;
  }

  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public int getPageId() {
    return pageId;
  }
  public void setPageId(int pageId) {
    this.pageId = pageId;
  }
  public int getLemmaId() {
    return lemmaId;
  }
  public void setLemmaId(int lemmaId) {
    this.lemmaId = lemmaId;
  }
  public float getRank() {
    return rank;
  }
  public void setRank(float rank) {
    this.rank = rank;
  }
}
