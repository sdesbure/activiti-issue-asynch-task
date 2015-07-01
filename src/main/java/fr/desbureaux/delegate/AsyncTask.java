/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.desbureaux.delegate;

import fr.desbureaux.repository.ActivitiRepository;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sylvain Desbureaux <sylvain@desbureaux.fr>
 */
@Component
public class AsyncTask implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncTask.class);

    @Autowired
    private ActivitiRepository repo;
    
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.debug("entering AsyncTask");
        LOGGER.debug(" * found per execution : " + execution.getCurrentActivityId());
        LOGGER.debug(" * found via SQL (same as REST request): " + (String) repo.getActiveActiviti());
        LOGGER.debug("finished AsyncTask");
    }
    
}
