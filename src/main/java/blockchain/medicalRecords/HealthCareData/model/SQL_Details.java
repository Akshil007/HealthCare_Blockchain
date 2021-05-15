package blockchain.medicalRecords.HealthCareData.model;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


@Service
public class SQL_Details {
    private String url;
    private String un;
    private String pass;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
