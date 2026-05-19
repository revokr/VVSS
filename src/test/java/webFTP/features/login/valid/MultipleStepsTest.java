package webFTP.features.login.valid;

import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.UseTestDataFrom;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import webFTP.steps.serenity.*;

@RunWith(SerenityParameterizedRunner.class)
@UseTestDataFrom("src/test/resources/multiple-steps.csv")
public class MultipleStepsTest {

    @Managed(uniqueSession = true)
    public WebDriver webdriver;

    @Steps
    public LoginPageSteps user;

    @Steps
    public AccountPageSteps userLoggedIn;

    @Steps
    public NewDirectoryPageSteps userNewDirectory;

    @Steps
    public DeleteDirectoryPageSteps userDeleteDirectory;

    @Steps
    public LogoutPageSteps userLoggedOut;

    // CSV parameters
    String server;
    String username;
    String password;
    String directoryName1;
    String directoryName2;
    String message;

    @Test
    public void completeDirectoryWorkflowScenario() {
        // STEP 1: Login with provided credentials
        // Encode credentials and pass via Chrome DevTools
        webdriver.get("about:blank");
        ((org.openqa.selenium.JavascriptExecutor) webdriver).executeScript(
            "var xhr = new XMLHttpRequest();" +
            "xhr.open('GET', 'https://scs.ubbcluj.ro/vvta/net2ftp/index.php', false, 'vvss', 'strugure');" +
            "xhr.send();"
        );
        webdriver.get("https://scs.ubbcluj.ro/vvta/net2ftp/index.php");
        user.click_saveCookies();
        user.login_steps(server, username, password);
        userLoggedIn.should_be_in_user_directory("/home/" + username);
        Assert.assertTrue("User should be logged in", webdriver.getCurrentUrl().contains("index.php"));

        // STEP 2: Create first directory
        userLoggedIn.newDirectory();
        userNewDirectory.createDirectory(directoryName1);
        userLoggedIn.should_be_able_to_see_new_directory(directoryName1);
        Assert.assertTrue("First directory '" + directoryName1 + "' should be created",
                webdriver.getPageSource().contains(directoryName1));

        // STEP 3: Create second directory
        userLoggedIn.newDirectory();
        userNewDirectory.createDirectory(directoryName2);
        userLoggedIn.should_be_able_to_see_new_directory(directoryName2);
        Assert.assertTrue("Second directory '" + directoryName2 + "' should be created",
                webdriver.getPageSource().contains(directoryName2));

        // STEP 4: Delete first directory
        userLoggedIn.select_directory_to_delete(directoryName1);
        userLoggedIn.delete_selected_directory();
        userDeleteDirectory.delete_directory(directoryName1);
        userLoggedIn.should_not_be_able_to_see_new_directory(directoryName1);
        Assert.assertFalse("First directory '" + directoryName1 + "' should be deleted",
                webdriver.getPageSource().contains(directoryName1));

        // STEP 5: Verify second directory still exists
        userLoggedIn.should_be_able_to_see_new_directory(directoryName2);
        Assert.assertTrue("Second directory '" + directoryName2 + "' should still exist",
                webdriver.getPageSource().contains(directoryName2));

        // STEP 6: Delete second directory
        userLoggedIn.select_directory_to_delete(directoryName2);
        userLoggedIn.delete_selected_directory();
        userDeleteDirectory.delete_directory(directoryName2);
        userLoggedIn.should_not_be_able_to_see_new_directory(directoryName2);
        Assert.assertFalse("Second directory '" + directoryName2 + "' should be deleted",
                webdriver.getPageSource().contains(directoryName2));

        // STEP 7: Logout
        userLoggedIn.logout();
        userLoggedOut.should_see_logout_message(message);
        Assert.assertTrue("Logout message should appear: " + message,
                webdriver.getPageSource().contains(message));
    }
}