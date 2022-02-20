package parser;

import com.sun.istack.NotNull;
import javax.persistence.*;

@Entity
@Table(indexes = @Index(name = "pathindex", columnList = "path"))
public class Page {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  private String path;

  @NotNull
  private int code;

  @NotNull
  @Column(columnDefinition = "MEDIUMTEXT") //TODO: изменить аннотацию
  private String content;

  public Page(){}

  public Page(String path, int code, String content){
    this.path = path;
    this.code = code;
    this.content = content;
  }

  @Override
  public String toString(){
    return path + ", " + code;
  }
}