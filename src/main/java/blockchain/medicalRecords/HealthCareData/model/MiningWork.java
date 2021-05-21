package blockchain.medicalRecords.HealthCareData.model;

import blockchain.medicalRecords.HealthCareData.util.DbUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class MiningWork implements  Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MiningWork.class);

    public MiningWork() {

    }

    @Autowired
    DbUtil dbUtil;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void run() {
        //System.setProperty("log4j.configurationFile","../../resources/log4j2.xml");

        try{
            dbUtil.createTables();
        }catch (Exception e) {
            System.out.println("create tables :: " + e);
            logger.error("Exception in Initializing DB:"+e);
        }

        while (true) {
            try {
                String targetNode = dbUtil.getAvailableIp();
                String uri = "http://"+targetNode+"/mine";
                String result = restTemplate.getForObject(uri, String.class);
                System.out.println(result);
                logger.info("Mining successful");
                logger.info(result);
            } catch (Exception e) {
                System.out.println("Exception at thread : " + e);
                logger.error("Exception at thread : " + e);
            } finally {
                try {
                    Thread.sleep(60000);
                } catch (Exception e) {
                    System.out.println("Sleep exception");
                    logger.error("Sleep exception");
                }

            }

        }




    }

}
