package blockchain.medicalRecords.HealthCareData;

import blockchain.medicalRecords.HealthCareData.model.MiningWork;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class HealthCareDataApplication {

	@Autowired
	private ApplicationContext applicationContext;

	private static final Logger logger = LoggerFactory.getLogger(MiningWork.class);

	public static void main(String[] args) {
		SpringApplication.run(HealthCareDataApplication.class, args);
		logger.info("Logger started");
	}


	@Bean
	public RestTemplate getRestTemplate() { return new RestTemplate(); }

	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}

	@Bean
	public CommandLineRunner schedulingRunner(@Qualifier("taskExecutor") TaskExecutor executor) {
		return new CommandLineRunner() {
			public void run(String... args) throws Exception {
				MiningWork curr = new MiningWork();
				applicationContext.getAutowireCapableBeanFactory().autowireBean(curr);
				executor.execute(curr);
			}
		};
	}
}
