package blockchain.medicalRecords.HealthCareData;

import blockchain.medicalRecords.HealthCareData.model.MiningWork;
import org.springframework.beans.factory.annotation.Autowired;
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

	public static void main(String[] args) {
		SpringApplication.run(HealthCareDataApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() { return new RestTemplate(); }

	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}

	@Bean
	public CommandLineRunner schedulingRunner(TaskExecutor executor) {
		return new CommandLineRunner() {
			public void run(String... args) throws Exception {
				MiningWork curr = new MiningWork();
				applicationContext.getAutowireCapableBeanFactory().autowireBean(curr);
				executor.execute(curr);
			}
		};
	}
}
