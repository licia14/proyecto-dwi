package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Map;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.SolicitudFacade;

@Named
@SessionScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SolicitudFacade solicitudFacade;

    private String pieChartData; // JSON para el gráfico circular
    private String barChartData; // JSON para el gráfico de barras
    private String tipoGrafico = "mes"; // Por defecto, gráfico de barras por mes

    @PostConstruct
    public void init() {
        cargarDatosPieChart();
        cargarDatosBarChart();
    }

    // Método para cargar datos del gráfico circular
    public void cargarDatosPieChart() {
        try {
            Map<String, Integer> conteoPorEstado = solicitudFacade.obtenerConteoPorEstado();

            StringBuilder labels = new StringBuilder();
            StringBuilder data = new StringBuilder();
            StringBuilder colors = new StringBuilder();

            String[] colorPalette = {"#FF6384", "#36A2EB", "#FFCE56"};
            int index = 0;

            for (Map.Entry<String, Integer> entry : conteoPorEstado.entrySet()) {
                if (index > 0) {
                    labels.append(", ");
                    data.append(", ");
                    colors.append(", ");
                }
                labels.append("\"").append(entry.getKey()).append("\"");
                data.append(entry.getValue());
                colors.append("\"").append(colorPalette[index % colorPalette.length]).append("\"");
                index++;
            }

            pieChartData = "{" +
                "\"labels\": [" + labels + "]," +
                "\"datasets\": [{" +
                "\"data\": [" + data + "]," +
                "\"backgroundColor\": [" + colors + "]" +
                "}]" +
                "}";

        } catch (Exception e) {
            e.printStackTrace();
            pieChartData = "{}";
        }
    }

    // Método para cargar datos del gráfico de barras
    public void cargarDatosBarChart() {
        try {
            Map<String, Integer> conteo = tipoGrafico.equals("mes") 
                ? solicitudFacade.obtenerConteoPorMes() 
                : solicitudFacade.obtenerConteoPorAnio();

            StringBuilder labels = new StringBuilder();
            StringBuilder data = new StringBuilder();
            StringBuilder colors = new StringBuilder();

            String[] colorPalette = {"#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"};
            int index = 0;

            for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                if (index > 0) {
                    labels.append(", ");
                    data.append(", ");
                    colors.append(", ");
                }
                labels.append("\"").append(entry.getKey()).append("\"");
                data.append(entry.getValue());
                colors.append("\"").append(colorPalette[index % colorPalette.length]).append("\"");
                index++;
            }

            barChartData = "{" +
                "\"labels\": [" + labels + "]," +
                "\"datasets\": [{" +
                "\"label\": \"Solicitudes por " + (tipoGrafico.equals("mes") ? "Mes" : "Año") + "\"," +
                "\"data\": [" + data + "]," +
                "\"backgroundColor\": [" + colors + "]" +
                "}]" +
                "}";

        } catch (Exception e) {
            e.printStackTrace();
            barChartData = "{}";
        }
    }

    // Getters y Setters
    public String getPieChartData() {
        return pieChartData;
    }

    public String getBarChartData() {
        return barChartData;
    }

    public String getTipoGrafico() {
        return tipoGrafico;
    }

    public void setTipoGrafico(String tipoGrafico) {
        this.tipoGrafico = tipoGrafico;
        cargarDatosBarChart(); // Recargar datos del gráfico de barras
    }
}
