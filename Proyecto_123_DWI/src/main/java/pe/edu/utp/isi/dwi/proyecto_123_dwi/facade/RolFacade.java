package pe.edu.utp.isi.dwi.proyecto_123_dwi.facade;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.dao.RolDAO;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Rol;

import java.util.List;

@Stateless
public class RolFacade {

    @Inject
    private RolDAO rolDAO;

    public List<Rol> listarRoles() {
        return rolDAO.listarRoles();
    }

    public void guardarRol(Rol rol) {
        rolDAO.guardarRol(rol);
    }

    public void actualizarRol(Rol rol) {
        rolDAO.actualizarRol(rol);
    }

    public Rol buscarRolPorId(int idRol) {
        return rolDAO.buscarRolPorId(idRol);
    }

    public void eliminarRol(Rol rol) {
        rolDAO.eliminarRol(rol);
    }
}
