package org.mandas.test;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;

import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.service.ServiceRegistry;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.ProducesAlternative;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

@RunWith(CdiRunner.class)
@ActivatedAlternatives(TestEntityManagerProducer.class)
public class EntityTest {

	@Inject
	protected EntityManager em;

	protected Configuration fillConfiguration(Configuration cfg) {
		return cfg.addAnnotatedClass(TestEntity.class);
	}

	@Produces
	@ProducesAlternative
	public EntityManagerFactory emf() {
		Properties properties = new Properties();

		properties.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=ORACLE");
		properties.setProperty("javax.persistence.transactionType", "RESOURCE_LOCAL");

		properties.setProperty("hibernate.dialect",
				"org.hibernate.dialect.H2Dialect");
		properties.setProperty("hibernate.connection.driver_class",
				"org.h2.Driver");
		properties.setProperty("hibernate.connection.url",
				"jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=ORACLE");
		properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.format_sql", "true");

		final class TestPersistenceProvider extends HibernatePersistenceProvider {
			protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(
					PersistenceUnitDescriptor persistenceUnitDescriptor,
					Map integration, ClassLoader providedClassLoader) {
				
				final class TestEntityManagerFactoryBuilder extends EntityManagerFactoryBuilderImpl {
					public TestEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings, ClassLoader providedClassLoader) {
						super(persistenceUnit, integrationSettings, providedClassLoader);
					}
					
					@Override
					public Configuration buildHibernateConfiguration(ServiceRegistry serviceRegistry) {
						return fillConfiguration(super.buildHibernateConfiguration(serviceRegistry));
					}
				}
				
				return new TestEntityManagerFactoryBuilder(persistenceUnitDescriptor, integration, providedClassLoader);
			}
		}
		
		PersistenceProviderResolverHolder.setPersistenceProviderResolver(new PersistenceProviderResolver() {
			
			@Override
			public List<PersistenceProvider> getPersistenceProviders() {
				return Lists.<PersistenceProvider>newArrayList(new TestPersistenceProvider());
			}
			
			@Override
			public void clearCachedProviders() { }
		});
		
		return Persistence.createEntityManagerFactory("default-persistence-unit", properties);
	}

	@Test
	public void setup() {
		TestEntity test = new TestEntity();
		test.setId(1L);

		TestEntityDao dao = new TestEntityDao();
		dao.setEntityManager(em);
		dao.getEntityManager().persist(test);
	}

}
