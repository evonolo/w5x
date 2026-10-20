package io.grundner.wallbox.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import io.grundner.wallbox.report.ReportView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Rendert einen Bericht als A4-PDF: Thymeleaf-Vorlage -> HTML, per jsoup zu
 * wohlgeformtem XHTML normalisiert, per openhtmltopdf nach PDF (ADR 0002).
 */
@Component
public class PdfRenderer {

	private final ITemplateEngine templateEngine;

	public PdfRenderer(ITemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public byte[] render(ReportView report) {
		String html = renderHtml(report);
		String xhtml = toXhtml(html);
		return toPdf(xhtml);
	}

	/** Öffentlich für die HTML-Vorschau bzw. Tests. */
	public String renderHtml(ReportView report) {
		Context context = new Context(Locale.GERMANY);
		context.setVariable("report", report);
		return templateEngine.process("report", context);
	}

	private String toXhtml(String html) {
		Document document = Jsoup.parse(html);
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		return document.html();
	}

	private byte[] toPdf(String xhtml) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.useFastMode();
			builder.withHtmlContent(xhtml, "");
			builder.toStream(out);
			builder.run();
			return out.toByteArray();
		}
		catch (Exception e) {
			throw new PdfRenderingException("PDF-Erzeugung fehlgeschlagen", e);
		}
	}

	public static class PdfRenderingException extends RuntimeException {
		public PdfRenderingException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
