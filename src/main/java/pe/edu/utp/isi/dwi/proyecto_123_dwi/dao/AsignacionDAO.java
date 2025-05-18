package pe.edu.utp.isi.dwi.proyecto_123_dwi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Asignacion;

import java.util.List;

public class AsignacionDAO {

    @PersistenceContext(unitName = "Proyecto_123_DWI")
    private EntityManager em;

    // Guardar una nueva asignación
    public void guardarAsignacion(Asignacion asignacion) {
        if (asignacion.getFechaAsignacion() == null) {
            asignacion.setFechaAsignacion(java.time.LocalDate.now());
        }
        em.persist(asignacion);
    }

    // Actualizar una asignación existente
    public void actualizarAsignacion(Asignacion asignacion) {
        em.merge(asignacion);
    }

    // Buscar una asignación por ID
    public Asignacion buscarAsignacionPorId(int idAsignacion) {
        return em.find(Asignacion.class, idAsignacion);
    }

    // Eliminar una asignación por ID
    public void eliminarAsignacion(int idAsignacion) {
        Asignacion asignacion = em.find(Asignacion.class, idAsignacion);
        if (asignacion != null) {
            em.remove(asignacion);
        }
    }

    // Listar todas las asignaciones relacionadas con una solicitud
    public List<Asignacion> listarPorSolicitud(int idSolicitud) {
        TypedQuery<Asignacion> query = em.createQuery(
                "SELECT a FROM Asignacion a WHERE a.solicitud.idSolicitud = :idSolicitud", Asignacion.class);
        query.setParameter("idSolicitud", idSolicitud);
        return query.getResultList();
    }

    // Listar todas las asignaciones de un colaborador
    public List<Asignacion> listarPorColaborador(int idColaborador) {
        TypedQuery<Asignacion> query = em.createQuery(
            "SELECT a FROM Asignacion a WHERE a.colaborador.idColaborador = :idColaborador", Asignacion.class);
        query.setParameter("idColaborador", idColaborador);
        return query.getResultList();
    }


    // Listar todas las asignaciones
    public List<Asignacion> listarTodas() {
        TypedQuery<Asignacion> query = em.createQuery("SELECT a FROM Asignacion a", Asignacion.class);
        return query.getResultList();
    }
    
    public Asignacion buscarPorId(int idAsignacion) {
        return em.find(Asignacion.class, idAsignacion);
    }
    
    public Asignacion obtenerPorColaboradorYSolicitud(int idColaborador, int idSolicitud) {
        TypedQuery<Asignacion> query = em.createQuery(
            "SELECT a FROM Asignacion a WHERE a.colaborador.idColaborador = :idColaborador AND a.solicitud.idSolicitud = :idSolicitud", 
            Asignacion.class
        );
        query.setParameter("idColaborador", idColaborador);
        query.setParameter("idSolicitud", idSolicitud);
        return query.getResultStream().findFirst().orElse(null);
    }
    
    public Asignacion obtenerCoordinadorPorSolicitud(int idSolicitud) {
        try {
            return em.createQuery(
                    "SELECT a FROM Asignacion a WHERE a.solicitud.idSolicitud = :idSolicitud AND a.esCoordinador = true",
                    Asignacion.class)
                    .setParameter("idSolicitud", idSolicitud)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Manejar caso donde no se encuentre coordinador
        }
    }
}
