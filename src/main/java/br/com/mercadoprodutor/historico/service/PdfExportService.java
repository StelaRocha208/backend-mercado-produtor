package br.com.mercadoprodutor.historico.service;

import br.com.mercadoprodutor.historico.dto.VisitHistoryExportDTO;
import br.com.mercadoprodutor.historico.dto.VisitHistorySummaryDTO;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] gerarPdf(List<VisitHistoryExportDTO> dados, VisitHistorySummaryDTO summary) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Histórico de Visitas", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Data de geração: " + LocalDateTime.now()));
            document.add(new Paragraph(" "));

            PdfPTable summaryTable = new PdfPTable(4);
            summaryTable.setWidthPercentage(100);
            adicionarCelula(summaryTable, "Total de visitas");
            adicionarCelula(summaryTable, String.valueOf(summary.totalVisitas()));
            adicionarCelula(summaryTable, "Valor total pago");
            adicionarCelula(summaryTable, summary.valorTotalPago().toPlainString());
            adicionarCelula(summaryTable, "Valor total pendente");
            adicionarCelula(summaryTable, summary.valorTotalPendente().toPlainString());
            adicionarCelula(summaryTable, "Tempo médio");
            adicionarCelula(summaryTable, summary.tempoMedioPermanencia());
            document.add(summaryTable);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            adicionarCelula(table, "Produtor");
            adicionarCelula(table, "CPF");
            adicionarCelula(table, "Data");
            adicionarCelula(table, "Seção");
            adicionarCelula(table, "Espaço");
            adicionarCelula(table, "Entrada");
            adicionarCelula(table, "Saída");
            adicionarCelula(table, "Permanência");
            adicionarCelula(table, "Valor");
            adicionarCelula(table, "Status");

            for (VisitHistoryExportDTO item : dados) {
                adicionarCelula(table, item.produtorNome());
                adicionarCelula(table, item.produtorCpf());
                adicionarCelula(table, item.dataVisita() != null ? item.dataVisita().toString() : "");
                adicionarCelula(table, item.secao());
                adicionarCelula(table, item.numeroEspaco());
                adicionarCelula(table, item.horarioEntrada() != null ? item.horarioEntrada().toString() : "");
                adicionarCelula(table, item.horarioSaida() != null ? item.horarioSaida().toString() : "");
                adicionarCelula(table, item.tempoPermanencia());
                adicionarCelula(table, item.valorCobrado() != null ? item.valorCobrado().toPlainString() : "");
                adicionarCelula(table, item.statusPagamento());
            }

            document.add(table);
            document.close();
        } catch (DocumentException exception) {
            throw new IllegalStateException("Falha ao gerar PDF do histórico de visitas", exception);
        }

        return outputStream.toByteArray();
    }

    private void adicionarCelula(PdfPTable table, String valor) {
        PdfPCell cell = new PdfPCell(new Phrase(valor != null ? valor : ""));
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }
}
