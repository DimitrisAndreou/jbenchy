package gr.forth.ics.jbenchy.excel;

import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Records;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 * A utility that writes a report in Microsoft Excel (XML) format.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class ExcelWriter {
    private ExcelWriter() { }

    /**
     * Writes the specified records to a target file, using Microsoft Excel's XML format.
     *
     * @param records the records to write
     * @param file the file in which to create
     * @throws IOException if an error occurs while writing to the file
     */
    public static void write(Records records, File file) throws IOException {
        PrintWriter out = new PrintWriter(file);
        SimpleDateFormat f = new SimpleDateFormat();

        try {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"");
            out.println("xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
            out.println("xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
            out.println("xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"");
            out.println("xmlns:html=\"http://www.w3.org/TR/REC-html40\">");

            out.println(" <Styles>");
            out.println("  <Style ss:ID=\"bold\">");
            out.println("   <Font ss:Bold=\"1\"/>");
            out.println("  </Style>");
            out.println(" </Styles>");

            out.println(" <Worksheet ss:Name=\"AggregatorResults\">");
            out.println("  <Table>");
            out.println("   <Row>");
            for (String var : records.getVariables()) {
                out.println("    <Cell ss:StyleID=\"bold\"><Data ss:Type=\"String\">" + var + "</Data></Cell>");
            }
            out.println("   </Row>");
             for (Record record : records) {
                 out.println("   <Row>");
                 for (String var : records.getVariables()) {
                     String value = record.get(var).toString();
                     String type = "Number";
                     try {
                         Integer.parseInt(value);
                     } catch (NumberFormatException e) {
                         type = "String";
                     }
                 out.println("    <Cell><Data ss:Type=\"" + type + "\">" + value + "</Data></Cell>");
                 }
                 out.println("   </Row>");
             }
             out.println("  </Table>");
             out.println(" </Worksheet>");
             out.println("</Workbook>");
        } finally {
            out.close();
        }
    }
}
