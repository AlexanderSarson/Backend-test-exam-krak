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
    
}
