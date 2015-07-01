package fr.desbureaux;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.activiti.engine.impl.test.JobTestHelper;
import org.activiti.rest.conf.ApplicationConfiguration;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class MyUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyUnitTest.class);

    @Autowired
    @Rule
    public ActivitiRule activitiRule;

    @Autowired
    private RuntimeService runtimeService;

    @Test
    @Deployment(resources = {"org/activiti/test/my-process.bpmn20.xml"})
    public void test() {
        JobTestHelper jobTestHelper = new JobTestHelper();
        LOGGER.debug("starting test");
        LOGGER.debug("runtime service: " + runtimeService);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        LOGGER.debug("processInstance ID: " + processInstance.getId());
        assertNotNull(processInstance);
        jobTestHelper.waitForJobExecutorToProcessAllJobs(activitiRule, 10000L, 25L);
    }

}
