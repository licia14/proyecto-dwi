package pe.edu.utp.isi.dwi.proyecto_123_dwi.model;

// Clase Productora de EntityManager para la inyección con CDI.

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class EntityManagerProducer {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("Proyecto_123_DWI");

    @Produces
    public EntityManager produceEntityManager() {
        return emf.createEntityManager();
    }

    public void closeEntityManager(EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }
}
