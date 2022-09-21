package ru.skillbox.areysearcher.service.crawler;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.skillbox.areysearcher.model.entity.Site;

@Component
@ConfigurationProperties(prefix = "application")
public class CrawlerProps {

  private List<Site> sites;

  public List<Site> getSites() {
    return sites;
  }

  public void setSites(List<Site> sites) {
    this.sites = sites;
  }
}
