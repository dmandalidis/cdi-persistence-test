package org.mandas.test;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TestEntityDao {

	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@PersistenceContext(unitName = "default-persistence-unit")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
