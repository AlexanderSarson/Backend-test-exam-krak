/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dtos.HobbyDTO;
import entity.Address;
import entity.Hobby;
import entity.Person;
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
public class HobbyFacadeTest {
    
    private static EntityManagerFactory entityManagerFactory;
    private static HobbyFacade hobbyFacade;
    private static Hobby h1, h2, h3, h4;
    private static Address a1, a2;
    private static Person p1, p2;

    public HobbyFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        entityManagerFactory = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        hobbyFacade = HobbyFacade.getHobbyFacade(entityManagerFactory);
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
        hobbies2.add(h1);
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
    public void testGetAllHobbies() throws UserException{
        int ExpectedSize = 4;
        List<HobbyDTO> listOfHobbies = hobbyFacade.getAllHobbies();
        assertEquals(ExpectedSize, listOfHobbies.size());
    }
    
}
