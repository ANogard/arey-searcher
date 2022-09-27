package ru.skillbox.areysearcher.service.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.skillbox.areysearcher.model.entity.Field;
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.model.entity.Site;
import ru.skillbox.areysearcher.model.entity.Status;
import ru.skillbox.areysearcher.service.Constants;

@Component
@ConfigurationProperties(prefix = "application")
public class CrawlerUtils {

  private List<Site> sites;

  public List<Site> getSites() {
    return sites;
  }

  public void setSites(List<Site> sites) {
    this.sites = sites;
  }

  public static String getRelativePath(String url, String host) {
    Pattern siteRoot = Pattern.compile("((http[s]?://)([\\w-\\.]+\\.\\w+))(.*)");
    Matcher root = siteRoot.matcher(url);
    if (root.find()) {
      if (!(root.group(1)).equals(host)) {
        return "#";
      } else {
        return root.group(4);
      }
    } else {
      return url;
    }
  }

  public static String getRootPath(String url) {
    Pattern siteRoot = Pattern.compile("(http[s]?://[\\w-\\.]+\\.\\w+)(.*)");
    Matcher root = siteRoot.matcher(url);
    return (root.find()) ? root.group(1) : "";
  }

  public static String getTitle(String path){
    try {
      Response response = Jsoup.connect(path).userAgent(Constants.USER_AGENT).timeout(10000)
          .execute();
      Document doc = response.parse();
      return doc.getElementsByTag("title").text();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
