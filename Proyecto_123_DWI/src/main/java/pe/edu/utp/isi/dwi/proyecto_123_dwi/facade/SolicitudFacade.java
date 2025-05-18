package pe.edu.utp.isi.dwi.proyecto_123_dwi.facade;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.dao.SolicitudDAO;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Solicitud;

import java.util.List;
import java.util.Map;

@Stateless
public class SolicitudFacade {

    @Inject
    private SolicitudDAO solicitudDAO;

    public void guardarSolicitud(Solicitud solicitud) {
        solicitudDAO.guardarSolicitud(solicitud);
    }

    public void actualizarSolicitud(Solicitud solicitud) {
        solicitudDAO.actualizarSolicitud(solicitud);
    }

    public List<Solicitud> listarSolicitudesPorCliente(int idCliente) {
        return solicitudDAO.listarSolicitudesPorCliente(idCliente);
    }
    
    public List<Solicitud> listarSolicitudes() {
        return solicitudDAO.listarTodasLasSolicitudes();
    }
    
    public Solicitud buscarPorId(int idSolicitud) {
        return solicitudDAO.buscarPorId(idSolicitud);
    }
    
     public int calcularNroSolicitud(int idCliente) {
        return solicitudDAO.calcularNroSolicitud(idCliente);
    }
     
    public List<Solicitud> obtenerPorEstado(String estado) {
        return solicitudDAO.obtenerPorEstado(estado);
    } 
    
    
    //dashboard
     public Map<String, Integer> obtenerConteoPorEstado() {
        return solicitudDAO.obtenerConteoPorEstado();
    }

    public Map<String, Integer> obtenerConteoPorMes() {
        return solicitudDAO.obtenerConteoPorMes();
    }
    
    public Map<String, Integer> obtenerConteoPorAnio() {
        return solicitudDAO.obtenerConteoPorAnio();
    }
}
