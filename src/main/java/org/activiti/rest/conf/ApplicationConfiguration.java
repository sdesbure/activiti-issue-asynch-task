package org.activiti.rest.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "org.activiti.rest.conf",
        "fr.desbureaux.delegate",
        "fr.desbureaux.repository"})
public class ApplicationConfiguration {

}
