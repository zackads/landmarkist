package com.landmarkist.www;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.testcontainers.containers.BrowserWebDriverContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public final class AcceptanceTests {

  private static final BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>()
    .withCapabilities(new ChromeOptions());

  @LocalServerPort
  private int port;

  @BeforeAll
  static void beforeAll(@Autowired Environment environment) {
    org.testcontainers.Testcontainers.exposeHostPorts(
      environment.getProperty("local.server.port", Integer.class)
    );
    chrome.start();
  }

  @Test
  public void indexPageLoads(@Autowired Environment env) {
    RemoteWebDriver driver = chrome.getWebDriver();
    String url = "http://host.testcontainers.internal" + ":" + port;

    driver.get(url);
    assertEquals(
      1,
      driver.findElements(By.tagName("h1")).size(),
      "User can see a single page heading"
    );
  }

  @Test
  public void userCanSeeAListedBuildingMarker(@Autowired Environment env) {
    RemoteWebDriver driver = chrome.getWebDriver();
    String url = "http://host.testcontainers.internal" + ":" + port;

    driver.get(url);
    assertEquals(
      1,
      driver
        .findElements(By.cssSelector("div[aria-label='Listed building']"))
        .size(),
      "User can see a listed building marker"
    );
  }
}
