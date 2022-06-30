package entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Site {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  private String status;

  @NotNull
  @Column(name = "status_time")
  private Date statusTime;

  @Column(name = "last_error")
  private String lastError;

  @NotNull
  private String url;

  @NotNull
  private String name;
}
