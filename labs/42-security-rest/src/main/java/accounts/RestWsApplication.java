package accounts;

import config.RestSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(RestSecurityConfig.class)
@EntityScan("rewards.internal")
public class RestWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestWsApplication.class, args);
    }

}

// Test the method security using browser or curl
// - Re-run this application
// - Using Chrome Incognito browser, access
//   http://localhost:8080/authorities?username=user
// - Enter "user"/"user" and verify that 403 failure occurs
// - If you want to use "curl", use
//   curl -i -u admin:admin localhost:8080/authorities\?username=admin
//
// - Close the Chrome Incognito browser and start a new one
// - Access http://localhost:8080/authorities?username=admin
// - Enter "admin"/"admin" and verify that the roles are displayed successfully
// - If you want to use "curl", use
//   curl -i -u admin:admin http://localhost:8080/authorities\?username=admin
//
// - Close the Chrome Incognito browser and start a new one
// - Access http://localhost:8080/authorities?username=superadmin
// - Enter "superadmin"/"superadmin" and verify that the roles are displayed successfully
// - If you want to use "curl", use
//   curl -i -u superadmin:superadmin http://localhost:8080/authorities\?username=superadmin

// ------------------------------------------------

// Verify that the newly added custom UserDetailsService works
// - Re-run this application
// - Using Chrome Incognito browser, access
//   http://localhost:8080/accounts/0
// - Enter "mary"/"mary" and verify accounts data gets displayed
// - If you want to use "curl", use
//   curl -i -u mary:mary http://localhost:8080/accounts/0
//
// - Close the Chrome Incognito browser and start a new one
// - Using Chrome Incognito browser, access
//   http://localhost:8080/accounts/0
// - Enter "joe"/"joe" and verify accounts data gets displayed
// - If you want to use "curl", use
//   curl -i -u joe:joe http://localhost:8080/accounts/0

// ------------------------------------------------

// TODO-19 (Optional): Verify that the newly added custom AuthenticationProvider works
// - Re-run this application
// - Using Chrome Incognito browser, access
//   http://localhost:8080/accounts/0
// - Enter "spring"/"spring" and verify accounts data
// - If you want to use "curl", use
//   curl -i -u spring:spring http://localhost:8080/accounts/0