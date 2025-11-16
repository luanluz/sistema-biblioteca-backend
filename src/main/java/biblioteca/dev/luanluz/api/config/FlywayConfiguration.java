package biblioteca.dev.luanluz.api.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Configuration
public class FlywayConfiguration {
    private final DataSource dataSource;

    @Value("${spring.flyway.baseline-on-migrate}")
    private boolean baselineOnMigrate;

    @Value("${flyway.migrations.base-path}")
    private String baseMigrationLocation;

    @Value("${flyway.migrations.common-path}")
    private String commonMigrationLocation;

    @Autowired
    public FlywayConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void triggerFlywayMigration() {
        String vendor = getDatabaseVendor();

        Flyway flyway = Flyway.configure()
                .baselineOnMigrate(baselineOnMigrate)
                .locations(commonMigrationLocation, baseMigrationLocation + vendor)
                .dataSource(dataSource)
                .load();
        flyway.migrate();
    }

    private String getDatabaseVendor() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName().toLowerCase();

            if (databaseProductName.contains("h2")) {
                return "h2";
            }

            if (databaseProductName.contains("postgres")) {
                return "postgres";
            }

            throw new UnsupportedOperationException("Banco de dados não suportado: " + databaseProductName);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter o vendor do banco de dados", e);
        }
    }
}
