package pe.edu.utp.isi.dwi.proyecto_123_dwi.facade;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.dao.AsignacionDAO;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Asignacion;

import java.util.List;

@Stateless
public class AsignacionFacade {

    @Inject
    private AsignacionDAO asignacionDAO;

    public void guardarAsignacion(Asignacion asignacion) {
        asignacionDAO.guardarAsignacion(asignacion);
    }

    public void actualizarAsignacion(Asignacion asignacion) {
        asignacionDAO.actualizarAsignacion(asignacion);
    }

    public Asignacion buscarAsignacionPorId(int idAsignacion) {
        return asignacionDAO.buscarAsignacionPorId(idAsignacion);
    }

    public void eliminarAsignacion(int idAsignacion) {
        asignacionDAO.eliminarAsignacion(idAsignacion);
    }

    public List<Asignacion> listarPorSolicitud(int idSolicitud) {
        return asignacionDAO.listarPorSolicitud(idSolicitud);
    }

    public List<Asignacion> listarPorColaborador(int idColaborador) {
        return asignacionDAO.listarPorColaborador(idColaborador);
    }

    public List<Asignacion> listarTodas() {
        return asignacionDAO.listarTodas();
    }
    
    public Asignacion buscarPorId(int idAsignacion) {
        return asignacionDAO.buscarPorId(idAsignacion);
    }
    
    public Asignacion buscarAsignacionPorColaboradorYSolicitud(int idColaborador, int idSolicitud) {
        return asignacionDAO.obtenerPorColaboradorYSolicitud(idColaborador, idSolicitud);
    }
    
    public Asignacion obtenerCoordinadorPorSolicitud(int idSolicitud) {
        return asignacionDAO.obtenerCoordinadorPorSolicitud(idSolicitud);
    }
    
}
