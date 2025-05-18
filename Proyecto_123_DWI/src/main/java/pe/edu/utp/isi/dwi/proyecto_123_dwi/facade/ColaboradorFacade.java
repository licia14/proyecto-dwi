package pe.edu.utp.isi.dwi.proyecto_123_dwi.facade;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.dao.ColaboradorDAO;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;

@Stateless
public class ColaboradorFacade {

    @Inject
    private ColaboradorDAO colaboradorDAO;

    public void guardarColaborador(Colaborador colaborador) {
        colaboradorDAO.guardarColaborador(colaborador);
    }

    public Colaborador buscarPorCorreoYContrasena(String correo, String contrasena) {
        return colaboradorDAO.buscarPorCorreoYContrasena(correo, contrasena);
    }
    
    public List<Colaborador> listarTodos() {
        return colaboradorDAO.listarTodos();
    }
    
    public void actualizarColaborador(Colaborador colaborador) {
        colaboradorDAO.actualizarColaborador(colaborador); // Llama al método del DAO
    }
}
