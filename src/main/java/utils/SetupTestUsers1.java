package utils;


import entity.Address;
import entity.Hobby;
import entity.Person;
import entity.Role;
import entity.User;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SetupTestUsers1 {

  public static void main(String[] args) {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    EntityManager em = emf.createEntityManager();
    
    // IMPORTAAAAAAAAAANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // This breaks one of the MOST fundamental security rules in that it ships with default users and passwords
    // CHANGE the three passwords below, before you uncomment and execute the code below
    // Also, either delete this file, when users are created or rename and add to .gitignore
    // Whatever you do DO NOT COMMIT and PUSH with the real passwords
    Hobby hobby1 = new Hobby("Badminton", "its fun");
    Hobby hobby2 = new Hobby("Fodbold", "its fun");
    Hobby hobby3 = new Hobby("Tennis", "its fun");
    Hobby hobby4 = new Hobby("Haandbold", "its fun");
    Address address1 = new Address("street1", "city1", 2880);
    Address address2 = new Address("street2", "city2", 2890);
    Address address3 = new Address("street3", "city3", 2900);
    List<Hobby> hobbies = new ArrayList<>();
    List<Hobby> hobbies2 = new ArrayList<>();
    List<Hobby> hobbies3 = new ArrayList<>();
    
    hobbies.add(hobby1);
    hobbies.add(hobby2);
    hobbies2.add(hobby3);
    hobbies2.add(hobby4);
    hobbies3.add(hobby1);
    hobbies3.add(hobby4);


    Person person1 = new Person("email1@ok.dk", "firstname1", "lastname1","44556677", hobbies, address1);
    Person person2 = new Person("email2@ok.dk", "firstname2", "lastname2","44556678", hobbies2, address2);
    Person person3 = new Person("email3@ok.dk", "firstname3", "lastname3","44556679", hobbies3, address3);

    em.getTransaction().begin();
    em.persist(hobby1);
    em.persist(hobby2);
    em.persist(hobby3);
    em.persist(hobby4);
    em.persist(address1);
    em.persist(address2);
    em.persist(address3);
    em.persist(person1);
    em.persist(person2);
    em.persist(person3);
    em.getTransaction().commit();
  }

}
