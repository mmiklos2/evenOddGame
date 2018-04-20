package net.miklos.evenodd;

import net.sf.ehcache.CacheManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync
public class EvenOddApplication extends SpringBootServletInitializer implements ApplicationContextAware {
	
	@Autowired
    private DataSource dataSource;

	private static ApplicationContext context;

	public static void main(String[] args) {
		//when run as java app
		context = SpringApplication.run(EvenOddApplication.class, args);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		//when run as anything
		context = applicationContext;
	}

	@Bean
	public Executor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}

	@Bean
	@DependsOn("dataSource")
	public DataSourceInitializer initializer() {
		final DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(dataSource);
		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
				new ClassPathResource("db/evenodd_schema.sql"),
				new ClassPathResource("db/evenodd.sql")
		);
		dataSourceInitializer.setDatabasePopulator(populator);
		final ResourceDatabasePopulator cleaner = new ResourceDatabasePopulator(
		);
		dataSourceInitializer.setDatabaseCleaner(cleaner);
		populator.setContinueOnError(true);
		return dataSourceInitializer;
	}

	@Bean
	EhCacheCacheManager cacheManager(CacheManager cm) {
		return new EhCacheCacheManager(cm);
	}

	@Bean
	public EhCacheManagerFactoryBean ehcache() {
		EhCacheManagerFactoryBean ehCacheManagerBean = new EhCacheManagerFactoryBean();
		ehCacheManagerBean.setConfigLocation(new ClassPathResource("cache/ehcache.xml"));
		return ehCacheManagerBean;
	}

}
