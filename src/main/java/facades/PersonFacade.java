/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dtos.PersonDTO;
import entity.Person;
import errorhandling.NotFoundException;
import errorhandling.UserException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

/**
 *
 * @author root
 */
public class PersonFacade {

    private static EntityManagerFactory emf;
    private static PersonFacade instance;

    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public PersonDTO getPersonById(long personId) throws NotFoundException {
        EntityManager em = getEntityManager();
        try {
            Person person = em.find(Person.class, personId);
            if (person == null) {
                throw new NotFoundException("No person found with that id");
            } else {
                return new PersonDTO(person);
            }
        } finally {
            em.close();
        }
    }

    public PersonDTO getPersonByEmail(String email) throws UserException {
        EntityManager em = getEntityManager();
        try {
            Person person = em.createQuery("SELECT p FROM Person p WHERE p.email = :email ", Person.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return new PersonDTO(person);
        } catch (NoResultException e){
            throw new UserException("No person found with that email");
        } finally {
            em.close();
        }
    }

}
