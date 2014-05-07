package org.mandas.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@SuppressWarnings("javadoc")
@Alternative
public class TestEntityManagerProducer {
	@Produces
	@Alternative
	@ApplicationScoped
	public EntityManager em(EntityManagerFactory emf) {
		return emf.createEntityManager();
	}
}
