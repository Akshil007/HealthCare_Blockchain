package blockchain.medicalRecords.HealthCareData.data_sources;

import blockchain.medicalRecords.HealthCareData.model.SQL_Details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class data_source_builder {

    @Autowired
    SQL_Details sql_details;

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        String url = System.getenv("DATABASE_HOST");
        if(url !=null){
            dataSourceBuilder.url("jdbc:mysql://healthcare-db:3306/HealthCareData?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&failOverReadOnly=false&maxReconnects=10");
            dataSourceBuilder.username("root");
            dataSourceBuilder.password("root");
            sql_details.setUrl("jdbc:mysql://healthcare-db:3306/HealthCareData?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&failOverReadOnly=false&maxReconnects=10");
            sql_details.setUn("root");
            sql_details.setPass("root");
        }else{
            dataSourceBuilder.url("jdbc:mysql://localhost:3306/HealthCareData?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
            dataSourceBuilder.username("akshil");
            dataSourceBuilder.password("akshil99");
            sql_details.setUrl("jdbc:mysql://localhost:3306/HealthCareData?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
            sql_details.setUn("akshil");
            sql_details.setPass("akshil99");
        }

        return dataSourceBuilder.build();
    }
}
