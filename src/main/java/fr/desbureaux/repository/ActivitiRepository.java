/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.desbureaux.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sylvain Desbureaux <sylvain@desbureaux.fr>
 */
@Repository
public class ActivitiRepository {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    public String getActiveActiviti() {
        EntityManager em = this.entityManagerFactory.createEntityManager();
        try {
            Query query = em.createNativeQuery("select act_id_ from act_ru_execution");
            return (String) query.getSingleResult();
        } catch (NoResultException nre) {
            return "none";
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
