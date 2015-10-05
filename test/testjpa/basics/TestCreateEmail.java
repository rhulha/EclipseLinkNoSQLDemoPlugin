package testjpa.basics;


import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import testjpa.model.Email;

public class TestCreateEmail {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("TestJPAPU");

        EntityManager em = emf.createEntityManager();
        Email email = new Email();
        
        email.setSubject("Subject");
        email.setSender("Sender");
        email.setTags("Tag1");
        email.setTextpreview("Textpreview");
        email.setReceived(new Date());

        email.setArchived(new Date());
        email.setCreated(new Date());
        email.setHash("sdfsdfdsf");

        em.getTransaction().begin();
        em.persist(email);
        em.getTransaction().commit();
        
        System.out.println("ID: " + email.getId());
        
        //TestEntity r = em.getReference(TestEntity.class, "55EB2D6C9E66C5F741431C77");
        //System.out.println("r = " + r.getId() + " " + r.getName());
        
        em.close();
        
        emf.close();
    }
}
