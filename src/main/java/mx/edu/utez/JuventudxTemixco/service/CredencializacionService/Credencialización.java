package mx.edu.utez.JuventudxTemixco.service.CredencializacionService;

import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

@Service
public class Credencialización {

    @Autowired
    private DataSource dataSource;

    public byte[] generarReporteSQL(
            String nombreReporte,
            Map<String, Object> parametros) throws Exception {


        String ruta = "/credenciales/" + nombreReporte + ".jrxml";
        InputStream reporteStream = getClass().getResourceAsStream(ruta);

        if (reporteStream == null) {
            throw new RuntimeException("No se encontró la plantilla .jrxml en la ruta: " + ruta);
        }


        InputStream logoStream = getClass().getResourceAsStream("/img/logo.png");
        if (logoStream == null) {
            logoStream = getClass().getResourceAsStream("/static/img/logo.png");
        }

        if (logoStream != null) {
            parametros.put("LOGO_LOGO", logoStream);
        }

        try {

            JasperReport reporte = JasperCompileManager.compileReport(reporteStream);

            try (Connection conexion = dataSource.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(
                        reporte,
                        parametros,
                        conexion);

                return JasperExportManager.exportReportToPdf(print);
            }
        } catch (Exception e) {
            System.err.println("ERROR CRITICO AL GENERAR JASPER REPORT: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}