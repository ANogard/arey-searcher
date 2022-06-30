package entity;

import com.sun.istack.NotNull;
import javax.persistence.*;

@Entity
@Table(indexes = @javax.persistence.Index(name = "pathindex", columnList = "path"))
public class Page {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  private String path;

  @NotNull
  private int code;

  @NotNull
  @Column(columnDefinition = "MEDIUMTEXT")
  private String content;

  public Page(){}

  public Page(String path){
    this.path = path;
  }

  public Page(String path, int code, String content){
    this(path);
    this.code = code;
    this.content = content;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getId() {
    return id;
  }
}