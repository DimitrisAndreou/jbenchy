package gr.forth.ics.jbenchy.diagram.gnuplot;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.StringUtils;
import gr.forth.ics.jbenchy.diagram.Diagram;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility class that prints files which command gnuplot to generate 1D or 2D diagrams (diagrams
 * which depict the values of a measurement with regards to one or two variables that
 * affect it).
 * @author andreou
 */
public class GnuPlotWriter {
    private final File rootFolder;
    private final String outputFolderName;
    private final Collection<File> plotFiles = Collections.synchronizedList(new ArrayList<File>());

    /**
     * Creates a GnuPlotWriter, that will create in the specified folder, subfolders
     * "data", "plt", "eps", which will contain the data files, the gnuplot command files
     * and the generated (by the plt files) postscript files respectively.
     * @param rootFolder the folder in which the gnuplot artifacts will be created
     */
    public GnuPlotWriter(File rootFolder) {
        this(rootFolder, "eps");
    }

    /**
     * Creates a GnuPlotWriter, that will create in the specified folder the specified
     * subfolders for the various gnuplot artifacts.
     * @param rootFolder the folder in which the gnuplot artifacts will be created
     * @param outputFolderName the subfolder name of the postscript files
     */
    public GnuPlotWriter(File rootFolder, String outputFolderName) {
        this.rootFolder = Preconditions.checkNotNull(rootFolder, "root folder");
        this.outputFolderName = Preconditions.checkNotNull(outputFolderName, "output folder name");
    }

    /**
     * Writes a diagram with a specified name.
     * @param diagram the diagram to render with gnuplot commands
     * @param diagramName the name of the diagram-specific artifacts
     * @throws java.io.IOException in case a IO failure occurs
     */
    public void writeDiagram(Diagram diagram, String diagramName) throws IOException {
        writeDiagram(diagram, diagramName, null);
    }

    /**
     * Writes a diagram with a specified name, and a custom gnuplot context. The diagram
     * must have one or two variables (i.e., <tt>diagram.getVariablesCount()</tt> should
     * return 1 or 2). Diagrams with more variables are not supported.
     * 
     * @param diagram the diagram to render with gnuplot commands
     * @param diagramName the name of the diagram-specific artifacts
     * @param context a custom context that can customize the gnuplot commands with 
     * extra settings and styles
     * @return the {@link GnuPlotFiles files} that are created
     * @throws java.io.IOException in case a IO failure occurs
     */
    public GnuPlotFiles writeDiagram(Diagram diagram, String diagramName, GnuPlotContext context) throws IOException {
        Preconditions.checkNotNull(diagram, "diagram");
        StringUtils.checkHasText(diagramName, "Empty diagram name");
        Preconditions.checkArgument(diagram.getVariableCount() == 1 ||
                diagram.getVariableCount() == 2,
                "Only 1D or 2D diagrams can be handled by gnuplot");

        if (context == null) {
            context = new GnuPlotContext();
        }

        createFolder(rootFolder);
        File benchDataFolder = rootFolder;
        File plotFolder = rootFolder;
        File outputFolder =
                createFolder(new File(rootFolder, outputFolderName));

        File dataFile = new File(benchDataFolder, diagramName + ".dat");
        File commandFile = new File(plotFolder, diagramName + ".plt");
        File diagramFile = new File(outputFolder, diagramName + ".eps");
        printDataFile(dataFile, diagram);
        printCommandFile(
                commandFile, diagramFile, dataFile,
                diagram, context);
        return new GnuPlotFiles(commandFile, dataFile, diagramFile);
    }

    private File createFolder(File folder) throws IOException {
        if (!folder.mkdirs() && !folder.exists()) {
            throw new IOException("Folder: '" + folder +
                    "' could not be created");
        }
        return folder;
    }

    /**
     * Writes into a file the data of a diagram, in an appropriate form to be read by gnuplot.
     * @param file the output file to create
     * @param diagram the diagram of which the data to write
     */
    protected void printDataFile(File file, Diagram diagram) throws IOException {
        PrintWriter out = new PrintWriter(file);
        printHeaderComments(out, diagram);
        final int domainSizeOfSecondVariable = diagram.getVariableCount() == 2 ?
            diagram.getDomainSize(1) : 1;
        int[] index = new int[2];
        for (Object row : diagram.getDomain(0)) {
            out.print(row);
            out.print("\t");
            index[1] = 0;
            for (int i = 0; i < domainSizeOfSecondVariable; i++) {
                Record record = diagram.getRecordAt(index);
                out.print(record == null ? null : record.getValue());
                out.print("\t");
                index[1]++;
            }
            out.println();
            index[0]++;
        }
        out.close();
    }

    /**
     * Writes the gnuplot command file that will generate the plot of a diagram.
     * @param commandFile the gnuplot command file to create
     * @param outputFile the target file in which to create the plot (for example, a postscript file)
     * @param dataFile the file which contains the data which will be plotted
     * @param diagram the diagram to be plotted
     * @param context gnuplot settings used to customize the gnuplot output
     */
    protected void printCommandFile(File commandFile, File outputFile, File dataFile,
            Diagram diagram, GnuPlotContext context) throws IOException {
        PrintWriter out = new PrintWriter(commandFile);
        out.println("set output '" + getRelativePath(outputFile) + "'");
        out.println("set terminal postscript eps monochrom \"Times-Roman\" 22");
        out.println("set xlabel '" + diagram.getLabelOf(0) + "'");
        out.println("set ylabel '" + diagram.getRangeLabel() + "'");
        out.println("set title '" + diagram.getTitle() + "'");
        
        context.doCreateContext(out);

        int col = 2;
        out.print("plot ");
        List<Object> columns = diagram.getVariableCount() == 2 ?
            diagram.getDomain(1) : Arrays.<Object>asList(diagram.getLabelOf(0));
        for (Object column : columns) {
            if (col > 2) {
                out.print(", ");
            }
            out.print("'" + getRelativePath(dataFile) + "' using 1:" + (col) +
                    " title '" + column +
                    "' " + context.getStyle(col - 1).toGnuPlotCommand());
            col++;
        }
        out.println(";\n");
        
        context.doDestroyContext(out);
        
        out.close();
        plotFiles.add(commandFile);
    }

    private String getRelativePath(File file) throws IOException {
        return file.getCanonicalPath().replace(rootFolder.getCanonicalPath() +
                File.separator, "");
    }

    /**
     * Creates a file in the root folder of this writer, with commands directing gnuplot
     * to execute all previously created diagram command files. Invoke this at the
     * end of the diagram generation to get a single file with commands that will
     * create all diagrams with gnuplot in one step.
     * @param masterFilename the file (inside the root folder) to be created
     * @return the file that was created
     * @throws java.io.IOException in case a IO failure occurs
     */
    public File createMasterPlotFile(String masterFilename) throws IOException {
        File masterFile = new File(rootFolder, masterFilename);
        PrintWriter out = new PrintWriter(masterFile);
        for (File plot : plotFiles) {
            out.print("load '");
            out.print(getRelativePath(plot));
            out.println("'");
        }
        out.close();
        return masterFile;
    }

    private void printHeaderComments(PrintWriter out, Diagram diagram) {
        out.print("# Each row is a different value of variable [");
        out.print(diagram.getVariables().get(0));
        out.println("]. Its domain appears in the first column.");
        out.println("# Each cell (except for the first column) is the respective value of [" + diagram.getRangeLabel() + "]");
        if (diagram.getVariableCount() == 2) {
            out.print("# Each column is a different value of variable: [");
            out.print(diagram.getVariables().get(1));
            out.print("]. Its domain is the following:\n#\t");

            out.print(Joiner.on(",\t").join(diagram.getDomain(1)));
            out.println();
        }
    }
    
    /**
     * A container of generated gnuplot files. It contains {@link #getCommandFile() a gnuplot command file},
     * {@link #getDataFile() a data file}, and a {@link #getTargetFile() diagram (postscript) file}. Note that the
     * last file will only be created when the command file is submitted to the gnuplot (external) executable.
     */
    public static class GnuPlotFiles {
        final File commandFile;
        final File dataFile;
        final File targetPostscriptFile;
        
        GnuPlotFiles(File commandFile, File dataFile, File targetPostscriptFile) {
            this.commandFile = commandFile;
            this.dataFile = dataFile;
            this.targetPostscriptFile = targetPostscriptFile;
        }
        
        /**
         * Returns the command file, containing gnuplot instructions that result in the creation of the diagram.
         */
        public File getCommandFile() {
            return commandFile;
        }
        
        /**
         * Returns the data file that contains the raw data of the generated diagram, in gnuplot (tab-delimitted) format.
         */
        public File getDataFile() {
            return dataFile;
        }
        
        /**
         * Returns the file which will be generated by gnuplot, containing a diagram. <strong>Note:</strong>
         * this file will most likely not exist, since the gnuplot console is not invoked by the GnuPlotWriter; it has
         * to be executed externally.
         */
        public File getTargetFile() {
            return targetPostscriptFile;
        }
    }
}
