package blockchain.medicalRecords.HealthCareData.model;

import blockchain.medicalRecords.HealthCareData.util.DbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class MiningWork implements  Runnable {

    public MiningWork() {

    }

    @Autowired
    DbUtil dbUtil;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void run() {

        try{
            dbUtil.createTables();
        }catch (Exception e) {
            System.out.println("create tables :: " + e);
        }

        while (true) {
            try {
                String targetNode = dbUtil.getAvailableIp();
                String uri = "http://"+targetNode+"/mine";
                String result = restTemplate.getForObject(uri, String.class);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("Exception at thread : " + e);
            } finally {
                try {
                    Thread.sleep(60000);
                } catch (Exception e) {
                    System.out.println("Sleep exception");
                }

            }

        }




    }

}
