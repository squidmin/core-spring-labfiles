package rewards;

import config.RewardsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@EnableConfigurationProperties(RewardsRecipientProperties.class)
//@ConfigurationPropertiesScan  // Supported from Spring Boot 2.2.1.
@Import(RewardsConfig.class)  // This is required since the RewardsConfig configuration now provides the DataSource bean
                              // and will not be auto-detected through component scanning.
                              // Technically you don't have to disable data source auto-configuration given that
                              // Spring Boot will use the application-defined DataSource bean over of the
                              // auto-configured one.
public class RewardsApplication {

    static final String SQL = "SELECT count(*) FROM T_ACCOUNT";

    final Logger logger = LoggerFactory.getLogger(RewardsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RewardsApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            long numberOfAccounts = jdbcTemplate.queryForObject(SQL, Long.class);
            logger.info("Number of accounts: {}", numberOfAccounts);
        };
    }

    @Bean
    CommandLineRunner commandLineRunner2(RewardsRecipientProperties rewardsRecipientProperties) {
        return args -> System.out.println("Recipient: " + rewardsRecipientProperties.getName());
    }

}
