package mx.edu.utez.JuventudxTemixco.service.CredencializacionService;

import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;

@Service
public class Credencialización {

    @Autowired
    private DataSource dataSource;


    public byte[] generarReporteSQL(
            String nombreReporte,
            Map<String, Object> parametros) throws Exception {


        if (parametros == null) {
            parametros = new HashMap<>();
        }

        InputStream imagenPorDefecto = getClass().getResourceAsStream("/img/default.png");

        if (imagenPorDefecto == null) {
            throw new RuntimeException("No se encontró la imagen default.png en /img/default.png");
        }

        validarImagen(imagenPorDefecto, "DEFAULT");

        InputStream logoStream =
                getClass().getResourceAsStream("/img/logo.png");

        if (logoStream == null) {
            throw new RuntimeException(
                    "No se encontró la imagen logo.png en /img/logo.png"
            );
        }


        validarImagen(logoStream, "LOGO");

        imagenPorDefecto = getClass().getResourceAsStream("/img/default.png");


        logoStream = getClass().getResourceAsStream("/img/logo.png");

        parametros.put("IMAGEN_DEFAULT", imagenPorDefecto);
        parametros.put("LOGO_LOGO", logoStream);

        String ruta = "/credenciales/" + nombreReporte + ".jrxml";

        InputStream reporteStream = getClass().getResourceAsStream(ruta);

        if (reporteStream == null) {
            throw new RuntimeException("No se encontró la plantilla: " + ruta);
        }

        try {

            JasperReport reporte = JasperCompileManager.compileReport(reporteStream);



            try (Connection conexion = dataSource.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(
                                reporte,
                                parametros,
                                conexion
                        );

                return JasperExportManager.exportReportToPdf(print);
            }

        } catch (Exception e) {

            System.err.println(
                    "ERROR CRITICO AL GENERAR JASPER REPORT: "
                            + e.getMessage()
            );

            e.printStackTrace();

            throw e;
        }

    }


    private void validarImagen(InputStream stream, String nombre) {

        try {
            byte[] bytes = stream.readNBytes(10);
            StringBuilder hex = new StringBuilder();

            for (byte b : bytes) {
                hex.append(
                        String.format("%02X ", b)
                );

            }

            System.out.println(nombre + " -> " + hex);

        } catch (Exception e) {
            System.out.println(
                    "Error validando "
                            + nombre
                            + ": "
                            + e.getMessage()
            );
        }
    }

}