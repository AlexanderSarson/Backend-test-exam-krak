/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dtos.HobbyDTO;
import entity.Hobby;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

/**
 *
 * @author root
 */
public class HobbyFacade {

    private static EntityManagerFactory emf;
    private static HobbyFacade instance;

    private HobbyFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static HobbyFacade getHobbyFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HobbyFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<HobbyDTO> getAllHobbies() {
        EntityManager em = getEntityManager();
        List<HobbyDTO> listOfHobbiesDTO = new ArrayList<>();
        try {
            List<Hobby> hobbies = em.createQuery("SELECT h FROM Hobby h", Hobby.class).getResultList();
            for (Hobby hobby : hobbies) {
                listOfHobbiesDTO.add(new HobbyDTO(hobby));
            }
        } finally {
            em.close();
        }
        return listOfHobbiesDTO;
    }

    public HobbyDTO addHobby(HobbyDTO hobbyDTO) {
        EntityManager em = getEntityManager();
        try {
            Hobby hobby = findHobbyByName(hobbyDTO.getName());
            if (hobby == null) {
                hobby = new Hobby(hobbyDTO.getName(), hobbyDTO.getDescription());
                em.getTransaction().begin();
                em.persist(hobby);
                em.getTransaction().commit();
            }
            hobbyDTO.setId(hobby.getId());
        } finally {
            em.close();
        }
        return hobbyDTO;
    }

    public Hobby findHobbyByName(String name) {
        EntityManager em = getEntityManager();
        Hobby hobby = null;
        try {
            hobby = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            return hobby;
        } finally {
            em.close();
        }
        return hobby;
    }

    public HobbyDTO deleteHobby(HobbyDTO hobbyDTO1) {
        EntityManager em = getEntityManager();
        try{
            Hobby hobby = em.find(Hobby.class, hobbyDTO1.getId());
            if(hobby == null){
                return new HobbyDTO(hobby);
            } else {
                em.getTransaction().begin();
                em.remove(hobby);
                em.getTransaction().commit();
            }
            return new HobbyDTO(hobby);
        } finally {
            em.close();
        }
    }

    public HobbyDTO editHobby(HobbyDTO hobbyDTO) {
        EntityManager em = getEntityManager();
        try {
            Hobby hobby = em.find(Hobby.class, hobbyDTO.getId());
            em.getTransaction().begin();
            em.persist(hobby);
            hobby.setName(hobbyDTO.getName());
            hobby.setDescription(hobbyDTO.getDescription());
            em.getTransaction().commit();
            return new HobbyDTO(hobby);
        } finally {
            em.close();
        }
    }

}
