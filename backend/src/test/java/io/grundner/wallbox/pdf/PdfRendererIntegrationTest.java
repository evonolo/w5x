package io.grundner.wallbox.pdf;

import io.grundner.wallbox.TestLogs;
import io.grundner.wallbox.domain.MonthlyReport;
import io.grundner.wallbox.parser.WallboxLogParser;
import io.grundner.wallbox.report.ReportService;
import io.grundner.wallbox.report.ReportView;
import io.grundner.wallbox.settings.Settings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifiziert die vollständige PDF-Kette (Thymeleaf -> jsoup -> openhtmltopdf)
 * gegen die Beispieldatei der Testdaten. AC-5.
 */
@SpringBootTest(properties = "w5x.settings-file=build/tmp/pdf-test/settings.json")
class PdfRendererIntegrationTest {

	@Autowired
	ReportService reportService;

	@Autowired
	PdfRenderer pdfRenderer;

	@Autowired
	WallboxLogParser parser;

	private MonthlyReport sampleReport() {
		Settings settings = new Settings("504F94A22F32", "user", "secret", "Teststandort", "A73B4D3");
		return reportService.buildReport(settings, parser.parse(TestLogs.sample()), 2024, 2);
	}

	@Test
	void rendersValidPdfForSampleReport() {
		MonthlyReport report = sampleReport();
		byte[] pdf = pdfRenderer.render(ReportView.of(report));

		assertTrue(pdf.length > 500, "PDF sollte nicht leer sein");
		// PDF-Signatur "%PDF"
		assertTrue(pdf[0] == '%' && pdf[1] == 'P' && pdf[2] == 'D' && pdf[3] == 'F',
				"Ausgabe sollte mit %PDF beginnen");
	}

	@Test
	void htmlContainsHeaderAndSessions() {
		MonthlyReport report = sampleReport();
		String html = pdfRenderer.renderHtml(ReportView.of(report));

		assertTrue(html.contains("Ladesitzungsbericht"));
		assertTrue(html.contains("66.99 kWh"));
		assertTrue(html.contains("Gesamte Energie"));
	}
}
