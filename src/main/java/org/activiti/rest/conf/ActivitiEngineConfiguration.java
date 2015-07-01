package org.activiti.rest.conf;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.impl.asyncexecutor.AsyncExecutor;
import org.activiti.engine.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.rest.form.MonthFormType;
import org.activiti.rest.form.ProcessDefinitionFormType;
import org.activiti.rest.form.UserFormType;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ActivitiEngineConfiguration {

    private final Logger log = LoggerFactory.getLogger(ActivitiEngineConfiguration.class);

    @Autowired
    protected Environment environment;

    @Bean
    public DataSource activitiDataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends Driver> driverClass = 
                    (Class<? extends Driver>) Class.forName("org.h2.Driver");
            ds.setDriverClass(driverClass);

        } catch (final Exception e) {
            log.error("Error loading driver class", e);
        }
        // Connection settings
        ds.setUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000;DB_CLOSE_ON_EXIT=FALSE");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(activitiDataSource());
        return transactionManager;
    }

    @Bean(name = "processEngineFactoryBean")
    public ProcessEngineFactoryBean processEngineFactoryBean() {
        final ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

    @Bean(name = "processEngine")
    public ProcessEngine processEngine() {
        // Safe to call the getObject() on the @Bean annotated
        // processEngineFactoryBean(), will be
        // the fully initialized object instanced from the factory and will NOT
        // be created more than once
        try {
            return processEngineFactoryBean().getObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Bean
    public AsyncExecutor asyncExecutor() {
        final AsyncExecutor asyncExecutor = new DefaultAsyncJobExecutor();
        log.info("changing the default lock time for async");
        asyncExecutor.setAsyncJobLockTimeInMillis(900000);
        return asyncExecutor;
    }
    
    @Bean(name = "processEngineConfiguration")
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
        final SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(activitiDataSource());
        processEngineConfiguration.setDatabaseSchemaUpdate(environment.getProperty("engine.schema.update", "true"));
        processEngineConfiguration.setTransactionManager(annotationDrivenTransactionManager());
        processEngineConfiguration.setJobExecutorActivate(true);
        processEngineConfiguration.setHistory("full");
        processEngineConfiguration.setAsyncExecutor(asyncExecutor());
        processEngineConfiguration.setAsyncExecutorEnabled(true);
        processEngineConfiguration.setAsyncExecutorActivate(true);      
        
        final List<AbstractFormType> formTypes = new ArrayList<AbstractFormType>();
        formTypes.add(new UserFormType());
        formTypes.add(new ProcessDefinitionFormType());
        formTypes.add(new MonthFormType());
        processEngineConfiguration.setCustomFormTypes(formTypes);

        return processEngineConfiguration;
    }

    @Bean
    public RepositoryService repositoryService() {
        return processEngine().getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() {
        return processEngine().getRuntimeService();
    }
    
    @Bean
    public ActivitiRule activitiRule() {
        ActivitiRule activitiRule = new ActivitiRule();
        activitiRule.setProcessEngine(processEngine());
        return activitiRule;
    }

    @Bean
    public TaskService taskService() {
        return processEngine().getTaskService();
    }

    @Bean
    public HistoryService historyService() {
        return processEngine().getHistoryService();
    }

    @Bean
    public FormService formService() {
        return processEngine().getFormService();
    }

    @Bean
    public IdentityService identityService() {
        return processEngine().getIdentityService();
    }

    @Bean
    public ManagementService managementService() {
        return processEngine().getManagementService();
    }
    
    @Bean
    EntityManagerFactory entityManagerFactory() {
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("fr.desbureaux.entity");
        factory.setDataSource(activitiDataSource());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
