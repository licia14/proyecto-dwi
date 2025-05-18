package pe.edu.utp.isi.dwi.proyecto_123_dwi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Solicitud;

import java.util.List;
import java.util.Map;

public class SolicitudDAO {

    @PersistenceContext(unitName = "Proyecto_123_DWI")
    public EntityManager em;

    public void guardarSolicitud(Solicitud solicitud) {
        em.persist(solicitud);
    }

    public void actualizarSolicitud(Solicitud solicitud) {
        em.merge(solicitud);
    }

    public List<Solicitud> listarSolicitudesPorCliente(int idCliente) {
        TypedQuery<Solicitud> query = em.createQuery(
            "SELECT s FROM Solicitud s WHERE s.cliente.idCliente = :idCliente", Solicitud.class);
        query.setParameter("idCliente", idCliente);
        return query.getResultList();
    }
    
    public List<Solicitud> listarTodasLasSolicitudes() {
        TypedQuery<Solicitud> query = em.createQuery("SELECT s FROM Solicitud s", Solicitud.class);
        return query.getResultList();
    }
    
    public Solicitud buscarPorId(int idSolicitud) {
        return em.find(Solicitud.class, idSolicitud);
    }
    
    public int calcularNroSolicitud(int idCliente) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(s) FROM Solicitud s WHERE s.cliente.idCliente = :idCliente", Long.class);
        query.setParameter("idCliente", idCliente);
        return query.getSingleResult().intValue() + 1; // Incrementa en 1
    }
    
    public List<Solicitud> obtenerPorEstado(String estado) {
        TypedQuery<Solicitud> query = em.createQuery(
            "SELECT s FROM Solicitud s WHERE s.estado = :estado", Solicitud.class);
        query.setParameter("estado", estado);
        return query.getResultList();
    }
    
    
    //metodos dashboards
     
    // Método para obtener el conteo de solicitudes agrupadas por estado
    public Map<String, Integer> obtenerConteoPorEstado() {
        List<Object[]> resultados = em.createQuery(
            "SELECT s.estado, COUNT(s) FROM Solicitud s GROUP BY s.estado",
            Object[].class)
            .getResultList();

        Map<String, Integer> conteoPorEstado = new HashMap<>();
        for (Object[] fila : resultados) {
            String estado = (String) fila[0];
            Long total = (Long) fila[1];
            conteoPorEstado.put(estado, total.intValue());
        }
        return conteoPorEstado;
    }

    // Método para obtener el conteo de solicitudes agrupadas por mes
    public Map<String, Integer> obtenerConteoPorMes() {
        try {
            // Consulta JPQL para obtener las fechas y sus conteos
            List<Object[]> resultados = em.createQuery(
                "SELECT s.fechaRegistro, COUNT(s) " +
                "FROM Solicitud s " +
                "GROUP BY s.fechaRegistro",
                Object[].class)
                .getResultList();

            // Procesar los resultados en Java para agrupar por mes
            Map<String, Integer> conteoPorMes = new HashMap<>();
            for (Object[] fila : resultados) {
                LocalDateTime fecha = (LocalDateTime) fila[0]; // Fecha completa
                String mes = fecha.getMonth().name(); // Nombre del mes en Java
                Long total = (Long) fila[1]; // Conteo
                conteoPorMes.put(mes, conteoPorMes.getOrDefault(mes, 0) + total.intValue());
            }

            // Imprimir resultados para depuración
            System.out.println("Resultados del DAO (Conteo por Mes): " + conteoPorMes);
            return conteoPorMes;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap(); // Retornar un mapa vacío en caso de error
        }
    }
    
    // Método para obtener el conteo de solicitudes agrupadas por año
    public Map<String, Integer> obtenerConteoPorAnio() {
        try {
            // Consulta JPQL para obtener las fechas y sus conteos
            List<Object[]> resultados = em.createQuery(
                "SELECT s.fechaRegistro, COUNT(s) " +
                "FROM Solicitud s " +
                "GROUP BY s.fechaRegistro",
                Object[].class)
                .getResultList();

            // Procesar los resultados en Java para agrupar por año
            Map<String, Integer> conteoPorAnio = new HashMap<>();
            for (Object[] fila : resultados) {
                LocalDateTime fecha = (LocalDateTime) fila[0]; // Fecha completa
                int anio = fecha.getYear(); // Año en Java
                Long total = (Long) fila[1]; // Conteo
                conteoPorAnio.put(String.valueOf(anio), conteoPorAnio.getOrDefault(String.valueOf(anio), 0) + total.intValue());
            }

            // Imprimir resultados para depuración
            System.out.println("Resultados del DAO (Conteo por Año): " + conteoPorAnio);
            return conteoPorAnio;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap(); // Retornar un mapa vacío en caso de error
        }
    }
}
