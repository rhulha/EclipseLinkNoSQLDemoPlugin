package testjpa.basics;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import testjpa.model.TestEntity;

public class TestDemoAdapter {
	public static void main(String[] args) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("TestJPAPU");

		EntityManager em = emf.createEntityManager();
		TestEntity testPersist1 = new TestEntity("Ray1");
		TestEntity testPersist2 = new TestEntity("Ray2");
		TestEntity testPersist3 = new TestEntity("Ray3");

		em.getTransaction().begin();
		em.persist(testPersist1);
		em.persist(testPersist2);
		em.persist(testPersist3);
		em.getTransaction().commit();

		System.out.println("ID: " + testPersist2.getId());

		TestEntity testFind = em.find(TestEntity.class, testPersist2.getId());

		System.out.println("name = " + testFind.getName());
		
		
        Query q = em.createQuery("SELECT t FROM TestEntity t where t.id > 70");
        List<TestEntity> list = q.getResultList();
        System.out.println("id, name");
        for (TestEntity e : list) {
            System.out.println(e.getId() + ", " + e.getName());
        }


		em.close();

		emf.close();
	}
}
