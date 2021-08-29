package com.examples.suggestions_project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * The web server is started on a random port, which can be retrieved by
 * injecting in the test a {@link LocalServerPort}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginWebControllerIT {

	private final String USERNAME = "admin";
	private final String PASSWORD = "admin";

	@LocalServerPort
	private int port;

	private WebDriver driver;

	private String baseUrl;

	@Before
	public void setup() {
		baseUrl = "http://localhost:" + port + "/";
		driver = new HtmlUnitDriver();
	}

	@After
	public void teardown() {
		driver.quit();
	}

	@Test
	public void testLoginPageLinks() {
		// go to "/"
		driver.get(baseUrl);
		// go to "/login"
		driver.findElements(By.linkText("here")).get(0).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl + "login");
		// go to "/"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
	}

	@Test
	public void testLoginPageSuccessfully() {
		// go to "/login"
		driver.get(baseUrl + "login");
		assertThat(driver.findElement(By.name("login_form")).getText()).contains("User Name:", "Password:", "Login");
		// fill the form
		driver.findElement(By.name("username")).sendKeys(USERNAME);
		driver.findElement(By.name("password")).sendKeys(PASSWORD);
		// submit login
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
		// go to "/login"
		driver.findElements(By.linkText("here")).get(0).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl + "login");
		assertThat(driver.getPageSource()).contains("Already logged as admin");
		assertThat(driver.findElement(By.name("logout_form")).getText()).contains("Sign Out");
		// perform sign out
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl + "login?logout");
		assertThat(driver.getPageSource()).contains("You have been logged out.");
		// go to "/"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
	}

	@Test
	public void testLoginPageLoginError() {
		// go to "/login"
		driver.get(baseUrl + "login");
		assertThat(driver.findElement(By.name("login_form")).getText()).contains("User Name:", "Password:", "Login");
		// fill the form
		driver.findElement(By.name("username")).sendKeys("wrong username");
		driver.findElement(By.name("password")).sendKeys("wrong password");
		// submit wrong login
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl + "login?error");
		assertThat(driver.getPageSource()).contains("Invalid username and password.");
		assertThat(driver.findElement(By.name("login_form")).getText()).contains("User Name:", "Password:", "Login");
		// go to "/"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
	}
}
