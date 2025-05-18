package pe.edu.utp.isi.dwi.proyecto_123_dwi.dao;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Rol;

import java.util.List;

public class RolDAO {

    @Inject
    private EntityManager em;

    // Guardar o actualizar un rol
    public void guardarRol(Rol rol) {
        em.persist(rol);
    }

    public void actualizarRol(Rol rol) {
        em.merge(rol);
    }
    
    // Buscar un rol por ID
    public Rol buscarRolPorId(int idRol) {
        return em.find(Rol.class, idRol);
    }

    // Eliminar un rol
    public void eliminarRol(Rol rol) {
        Rol rolToDelete = em.contains(rol) ? rol : em.merge(rol);
        em.remove(rolToDelete);
    }

    // Listar todos los roles
    public List<Rol> listarRoles() {
        TypedQuery<Rol> query = em.createQuery("SELECT r FROM Rol r", Rol.class);
        return query.getResultList();
    }

}
