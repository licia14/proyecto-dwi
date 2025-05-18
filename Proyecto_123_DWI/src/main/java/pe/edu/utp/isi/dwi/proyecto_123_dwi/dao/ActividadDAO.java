package pe.edu.utp.isi.dwi.proyecto_123_dwi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Actividad;

import java.util.List;

public class ActividadDAO {

    @PersistenceContext(unitName = "Proyecto_123_DWI")
    private EntityManager em;

    public void guardarActividad(Actividad actividad) {
        em.persist(actividad);
    }

    public void actualizarActividad(Actividad actividad) {
        em.merge(actividad);
    }

    public void eliminarActividad(int idActividad) {
        Actividad actividad = em.find(Actividad.class, idActividad);
        if (actividad != null) {
            em.remove(actividad);
        }
    }

    public Actividad buscarPorId(int idActividad) {
        return em.find(Actividad.class, idActividad);
    }

    public List<Actividad> listarActividadesPorSolicitud(int idSolicitud) {
        TypedQuery<Actividad> query = em.createQuery(
            "SELECT a FROM Actividad a WHERE a.asignacion.solicitud.idSolicitud = :idSolicitud ORDER BY a.fechaRegistro DESC",
            Actividad.class
        );
        query.setParameter("idSolicitud", idSolicitud);
        return query.getResultList();
    }


    public List<Actividad> listarTodasLasActividades() {
        TypedQuery<Actividad> query = em.createQuery(
                "SELECT a FROM Actividad a ORDER BY a.fechaRegistro DESC",
                Actividad.class);
        return query.getResultList();
    }
}
