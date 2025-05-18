package pe.edu.utp.isi.dwi.proyecto_123_dwi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Cliente;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SecurityUtils;

public class ClienteDAO {

    @PersistenceContext(unitName = "Proyecto_123_DWI")
    private EntityManager em;

    public void guardarCliente(Cliente cliente) {
        em.persist(cliente);
    }
    
    public Cliente buscarPorCorreoYContrasena(String correo, String contrasena) {
        try {
            // Buscar el cliente por correo
            Cliente cliente = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.correo = :correo", Cliente.class)
                .setParameter("correo", correo)
                .getSingleResult();

            // Generar el hash de la contraseña ingresada con el salt del cliente
            String hashGenerado = SecurityUtils.hashPasswordWithSalt(contrasena, cliente.getSalt());

            // Comparar el hash generado con el almacenado
            if (hashGenerado.equals(cliente.getContrasena())) {
                return cliente; // Retornar el cliente si las credenciales son válidas
            } else {
                return null; // Contraseña incorrecta
            }
        } catch (NoResultException e) {
            return null; // Correo no encontrado
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar cliente: " + e.getMessage(), e);
        }
    }
    
    public Cliente buscarPorId(int idCliente) {
        try {
            return em.find(Cliente.class, idCliente);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar cliente por ID: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void actualizarCliente(Cliente cliente) {
        try {
            em.merge(cliente); // Actualiza el cliente en la base de datos
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el cliente: " + e.getMessage(), e);
        }
    }
    
    public List<Cliente> listarTodos() {
        TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c", Cliente.class);
        return query.getResultList();
    }
}
