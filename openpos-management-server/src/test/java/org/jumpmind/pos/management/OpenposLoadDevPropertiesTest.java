package org.jumpmind.pos.management;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A simple test to ensure that spring.profiles.include works as we would like it
 * to, which is that if there is a property file found by spring boot during startup
 * and it includes spring.profiles.include=<environment name>, then the profile
 * named in that property will be loaded from the main profile or application.yml.  In this case,
 * make sure that the 'dev' profile as defined in the 'localtest' profile will
 * get loaded.  In practice, we would include an application-env.properties file
 * in the deployment environment along with an application.yml that contains profile
 * settings for several environments.  The application-env.properties file will
 * have the spring.profiles.include=<environment name> property defined.
 * Then we would ensure the 'env' profile was enabled by adding it to the 
 * spring.profiles.active (e.g., -Dspring.profiles.active=env).
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"localtest", "enable_dev"})
public class OpenposLoadDevPropertiesTest {
    @Value("${openpos.managementServer.testProperty1}")
    String testProperty;
    
    @Value("${openpos.managementServer.testProperty2Encrypted}")
    String testPropertyEncrypted;
    
    @BeforeClass
    public static void beforeClass() {
        System.setProperty("jasypt.encryptor.password", EncryptionTestConstants.ENCRYPTOR_PASSWORD);
        System.setProperty("jasypt.encryptor.algorithm", EncryptionTestConstants.ENCYPTION_ALGORITHM);
    }
    
    @AfterClass
    public static void afterClass() {
        System.setProperty("jasypt.encryptor.password", "");
        System.setProperty("jasypt.encryptor.algorithm", "");
    }
    
    @Test
    public void checkDevProperty() {
        assertThat(testProperty).isEqualTo("dev-value");
    }
    
    @Test
    public void checkEncryptedProperty() {
        assertThat(testPropertyEncrypted).isEqualTo("changeit");
    }
    
}