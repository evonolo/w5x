package io.grundner.wallbox.web;

import io.grundner.wallbox.domain.MonthlyReport;
import io.grundner.wallbox.pdf.PdfRenderer;
import io.grundner.wallbox.report.ReportService;
import io.grundner.wallbox.report.ReportView;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-API des Monatsberichts – Contract: docs/api/report-api.md.
 */
@RestController
@RequestMapping("/api")
public class ReportController {

	private final ReportService reportService;
	private final PdfRenderer pdfRenderer;

	public ReportController(ReportService reportService, PdfRenderer pdfRenderer) {
		this.reportService = reportService;
		this.pdfRenderer = pdfRenderer;
	}

	@GetMapping("/report")
	public ReportView report(
			@RequestParam int year,
			@RequestParam int month) {
		validate(year, month);
		MonthlyReport report = reportService.buildReport(year, month);
		return ReportView.of(report);
	}

	@GetMapping("/report/pdf")
	public ResponseEntity<byte[]> reportPdf(
			@RequestParam int year,
			@RequestParam int month) {
		validate(year, month);
		MonthlyReport report = reportService.buildReport(year, month);
		byte[] pdf = pdfRenderer.render(ReportView.of(report));

		String filename = "ladesitzungsbericht-%d-%02d.pdf".formatted(year, month);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
		return ResponseEntity.ok().headers(headers).body(pdf);
	}

	private static void validate(int year, int month) {
		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("month muss zwischen 1 und 12 liegen");
		}
		if (year < 1970 || year > 9999) {
			throw new IllegalArgumentException("year ist ungültig");
		}
	}
}
