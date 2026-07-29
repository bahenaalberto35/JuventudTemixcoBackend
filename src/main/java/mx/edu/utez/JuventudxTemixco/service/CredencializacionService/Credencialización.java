package mx.edu.utez.JuventudxTemixco.service.CredencializacionService;

import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

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

        InputStream defaultStream =
                getClass().getResourceAsStream("/img/default.jpg");

        if(defaultStream == null){
            throw new RuntimeException("No existe /img/default.jpg");
        }

        byte[] defaultBytes = defaultStream.readAllBytes();

        parametros.put("IMAGEN_DEFAULT", defaultBytes);

        InputStream logoStream = getClass().getResourceAsStream("/img/logo.png");

        byte[] logoBytes = logoStream.readAllBytes();

        parametros.put("LOGO_LOGO", logoBytes);

        String ruta = "/credenciales/" + nombreReporte + ".jrxml";

        InputStream reporteStream = getClass().getResourceAsStream(ruta);

        if(reporteStream == null){throw new RuntimeException("No existe plantilla: " + ruta);}

        try {
            JasperReport reporte = JasperCompileManager.compileReport(reporteStream);

            try(Connection conexion = dataSource.getConnection()){

                JasperPrint print = JasperFillManager.fillReport(
                                reporte,
                                parametros,
                                conexion
                );
                return JasperExportManager.exportReportToPdf(print);
            }

        }catch(Exception e){
            System.err.println("ERROR GENERANDO REPORTE: " + e.getMessage());
            throw e;
        }

    }
}