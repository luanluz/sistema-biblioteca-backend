package biblioteca.dev.luanluz.api.service;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class RelatorioService {
    private final DataSource dataSource;

    public byte[] gerarRelatorioAutoresPDF() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            InputStream jasperStream = new ClassPathResource("reports/relatorio_autores.jasper").getInputStream();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", "Relatório de Autores");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, parameters, connection);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }

    public byte[] gerarRelatorioAutoresExcel() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            InputStream jasperStream = new ClassPathResource("reports/relatorio_autores.jasper").getInputStream();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", "Relatório de Autores");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, parameters, connection);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            JRXlsxExporter exporter = createXlsxExporter(jasperPrint, outputStream);
            exporter.exportReport();

            return outputStream.toByteArray();
        }
    }

    private JRXlsxExporter createXlsxExporter(JasperPrint jasperPrint, ByteArrayOutputStream outputStream) {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);

        exporter.setConfiguration(configuration);
        return exporter;
    }
}
