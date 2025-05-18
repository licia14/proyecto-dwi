package pe.edu.utp.isi.dwi.proyecto_123_dwi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SecurityUtils;

public class ColaboradorDAO {

    @PersistenceContext(unitName = "Proyecto_123_DWI")
    private EntityManager em;

    public void guardarColaborador(Colaborador colaborador) {
        em.persist(colaborador);
    }

    public Colaborador buscarPorCorreoYContrasena(String correo, String contrasena) {
        try {
            // Buscar el colaborador por correo
            Colaborador colaborador = em.createQuery(
                    "SELECT c FROM Colaborador c WHERE c.correo = :correo", Colaborador.class)
                    .setParameter("correo", correo)
                    .getSingleResult();

            // Generar el hash de la contraseña ingresada con el salt del colaborador
            String hashGenerado = SecurityUtils.hashPasswordWithSalt(contrasena, colaborador.getSalt());

            // Comparar el hash generado con el almacenado
            if (hashGenerado.equals(colaborador.getContrasena())) {
                return colaborador; // Retornar el colaborador si las credenciales son válidas
            } else {
                return null; // Contraseña incorrecta
            }
        } catch (NoResultException e) {
            return null; // Correo no encontrado
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar colaborador: " + e.getMessage(), e);
        }
    }
    
     public List<Colaborador> listarTodos() {
        TypedQuery<Colaborador> query = em.createQuery("SELECT c FROM Colaborador c", Colaborador.class);
        return query.getResultList();
    }
     
     @Transactional
    public void actualizarColaborador(Colaborador colaborador) {
        try {
            em.merge(colaborador); // Actualiza el cliente en la base de datos
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el colaborador: " + e.getMessage(), e);
        }
    }
}
