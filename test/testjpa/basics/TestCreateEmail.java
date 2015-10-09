package testjpa.basics;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import testjpa.model.Email;

public class TestCreateEmail {
	
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("TestJPAPU");

		EntityManager em = emf.createEntityManager();
		Email email1 = new Email("Subject1", "Sender", "Textpreview", "Hash1", "Tag1", new Date(), new Date(), new Date());
		Email email2 = new Email("Subject2", "Sender", "Textpreview", "Hash1", "Tag1", new Date(), new Date(), new Date());
		Email email3 = new Email("Subject3", "Sender", "Textpreview", "Hash1", "Tag1", new Date(), new Date(), new Date());

		em.getTransaction().begin();
		em.persist(email1);
		em.persist(email2);
		em.persist(email3);
		em.getTransaction().commit();

		System.out.println("ID: " + email2.getId());

		Email testFind = em.find(Email.class, email2.getId());

		System.out.println("subject = " + testFind.getSubject());

		TypedQuery<Email> q = em.createQuery("SELECT t FROM Email t where t.id > 70", Email.class);
		List<Email> list = q.getResultList();
		System.out.println("id, subject");
		for (Email e : list) {
			System.out.println(e.getId() + ", " + e.getSubject());
		}

		em.close();

		emf.close();
	}
}
