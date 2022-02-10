import parser.Crawler;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class Main {
  public static void main(String[] args) {
    Set<String> list = new ForkJoinPool().invoke(new Crawler("https://severts.ru"));
  }
}
