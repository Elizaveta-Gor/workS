package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    void setUp() {
        Configuration.holdBrowserOpen = false;
        Configuration.headless = true;
        Configuration.browserBinary = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";

        // Уникальная временная директория профиля Chrome
        String userDataDir = System.getProperty("java.io.tmpdir") + "/chrome-profile-" + System.currentTimeMillis();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--user-data-dir=" + userDataDir);
        options.addArguments("--window-size=1366,768");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        Configuration.browserCapabilities = capabilities;

        open("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    void shouldSubmitFormWithValidData() {
        String date = generateDate(3);

        $("[data-test-id=city] input").setValue("Самара");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Анна-Софья Иванова");
        $("[data-test-id=phone] input").setValue("+79001112233");
        $("[data-test-id=agreement]").click();

        $$("button").findBy(Condition.exactText("Забронировать")).click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Встреча успешно забронирована на " + date));
    }

    @Test
    void shouldSubmitFormWithDoubleName() {
        String date = generateDate(5);

        $("[data-test-id=city] input").setValue("Уфа");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Мария Иванова-Петрова");
        $("[data-test-id=phone] input").setValue("+79005554433");
        $("[data-test-id=agreement]").click();

        $$("button").findBy(Condition.exactText("Забронировать")).click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Встреча успешно забронирована на " + date));
    }

    @Test
    void shouldSubmitFormWithNameContainingSpace() {
        String date = generateDate(7);

        $("[data-test-id=city] input").setValue("Красноярск");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Николай Иван");
        $("[data-test-id=phone] input").setValue("+79007778899");
        $("[data-test-id=agreement]").click();

        $$("button").findBy(Condition.exactText("Забронировать")).click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Встреча успешно забронирована на " + date));
    }
}
