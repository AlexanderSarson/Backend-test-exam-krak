/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entity.Address;
import entity.Hobby;
import entity.Person;
import errorhandling.NotFoundException;
import errorhandling.UserException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.EMF_Creator;

/**
 *
 * @author root
 */
public class PersonFacadeTest {

    private static EntityManagerFactory entityManagerFactory;
    private static PersonFacade personFacade;
    private static Hobby h1, h2, h3, h4;
    private static Address a1, a2;
    private static Person p1, p2;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        entityManagerFactory = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        personFacade = PersonFacade.getPersonFacade(entityManagerFactory);
        h1 = new Hobby("Badminton", "its fun");
        h2 = new Hobby("Fodbold", "its fun");
        h3 = new Hobby("Tennis", "its fun");
        h4 = new Hobby("Haandbold", "its fun");
        a1 = new Address("street1", "city1", 2880);
        a2 = new Address("street2", "city2", 2890);
        List<Hobby> hobbies = new ArrayList<>();
        List<Hobby> hobbies2 = new ArrayList<>();
        hobbies.add(h1);
        hobbies.add(h2);
        hobbies2.add(h3);
        hobbies2.add(h4);
        p1 = new Person("email1@ok.dk", "firstname1", "lastname1", "44556677", hobbies, a1);
        p2 = new Person("email2@ok.dk", "firstname2", "lastname2", "44556678", hobbies2, a2);
    }
    @BeforeEach
    public void setUp() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();  
            em.persist(h1);
            em.persist(h2);
            em.persist(h3);
            em.persist(h4);
            em.persist(a1);
            em.persist(a2);
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    

    @Test
    public void testGetPersonById() throws NotFoundException {
        long personId = p1.getId();
        String expectedFirstName = "firstname1";
        String resultFirstName = personFacade.getPersonById(personId).getFirstName();
        assertEquals(expectedFirstName, resultFirstName);
    }
    
    @Test
    public void testGetPersonById_with_invalid_id() {
        long personId = 1000;
        assertThrows(NotFoundException.class, () -> {
            personFacade.getPersonById(personId);
        });
    }
    
    @Test
    public void testGetPersonByEmail() throws UserException {
        String expectedEmail = "email1@ok.dk";
        String resultEmail = personFacade.getPersonByEmail(expectedEmail).getEmail();
        assertEquals(expectedEmail, resultEmail);
    }
    
    @Test
    public void testGetPersonById_with_invalid_email() {
        String invalidEmail = "invalid";
        assertThrows(UserException.class, () -> {
            personFacade.getPersonByEmail(invalidEmail);
        });
    }
    
}
