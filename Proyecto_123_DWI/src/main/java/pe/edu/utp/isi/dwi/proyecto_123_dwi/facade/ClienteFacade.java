package pe.edu.utp.isi.dwi.proyecto_123_dwi.facade;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.dao.ClienteDAO;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Cliente;

@Stateless
public class ClienteFacade {

    @Inject
    private ClienteDAO clienteDAO;

    public void guardarCliente(Cliente cliente) {
        clienteDAO.guardarCliente(cliente);
    }
    
    public Cliente buscarPorCorreoYContrasena(String correo, String contrasena) {
        return clienteDAO.buscarPorCorreoYContrasena(correo, contrasena);
    }
    
    public void actualizarCliente(Cliente cliente) {
        clienteDAO.actualizarCliente(cliente); // Llama al método del DAO
    }
    
    public Cliente obtenerClientePorId(int idCliente) {
        return clienteDAO.buscarPorId(idCliente);
    }
    
    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }
}
