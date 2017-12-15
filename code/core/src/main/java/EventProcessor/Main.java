package EventProcessor;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Thread {

    public static final Integer FUSEKI = 0;
    public static final Integer STARDOG = 1;

    public static Integer INTERFERENCE = 0;
    public static Integer DB_SERVER = 0;

    public static void main(String[] args) throws Exception {

        // Options:
        // useStardog()
        // useFuseki()
        // enableInterference()

        useStardog();
        enableInterference();

        while (true) {

            Main.streamProcessing();
            Thread.sleep(1000);
        }
    }

    /**
     * Either way:
     * (1) push to db server,
     * (2) execute reasoning, if INTERFERENCE
     *
     * @return
     * @throws Exception
     */
    public static Boolean streamProcessing() throws Exception {

        List<Path> input = watchFolder(Paths.get(IRIHandler.INPUT));

        // update new input data
        if (usesFuseki())
            FusekiHandler.uploadTTL(input, FusekiHandler.GRAPH_DATA);

        else if (usesStardog())
            StardogHandler.upload(input, StardogHandler.GRAPH_DATA);


        if (usesInterference() && usesFuseki()) {

            // reasoning and jena generic rule reasoner
            List<Path> inferredAxioms = OWLApiHandler.inferOntology(FUSEKI);
            List<Path> inferredRules  = JenaHandler.inferOntologyJenaRules(inferredAxioms);

            // replace inferred graph with new inferred axioms &
            // add result of generic rule reasoner
            FusekiHandler.replaceTTL(inferredAxioms, FusekiHandler.GRAPH_INFERRED);
            FusekiHandler.uploadTTL(inferredRules, FusekiHandler.GRAPH_INFERRED);

        } else if (usesInterference() && usesStardog()) {

            // reasoning with OWLApiHandler &
            // SWRL rules should be part of the schema at this mode
            List<Path> inferredAxioms = OWLApiHandler.inferOntology(STARDOG);

            // replace inferred graph with new inferred axioms
            StardogHandler.replace(inferredAxioms, StardogHandler.GRAPH_INFERRED);

        }

        // delete input files
        if (!input.isEmpty())
            deleteFile(input);

        return true;
    }

    private static void enableInterference() {
        INTERFERENCE = 1;
    }

    private static void useStardog() {
        DB_SERVER = STARDOG;
    }

    private static void useFuseki() {
        DB_SERVER = FUSEKI;
    }

    private static Boolean usesStardog() {
        return DB_SERVER.equals(STARDOG);
    }

    private static Boolean usesFuseki() {
        return DB_SERVER.equals(FUSEKI);
    }

    private static Boolean usesInterference() { return INTERFERENCE.equals(1); }

    /**
     * Collects all files from a directory and collects into a list
     *
     * Future: Restrict to num of elements per iteration
     *
     * @param input
     * @return
     */
    private static List<Path> watchFolder(Path input) {

        List<Path> files = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(input)) {
            for (Path file: stream) {

                String fileName = file.getFileName().toString();
                Boolean passFilters = !fileName.startsWith(".") && (fileName.endsWith(".ttl"));

                if (passFilters)
                    files.add(file);

            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }

        return files;
    }

    /**
     * Deletes all files from a List
     * @param files
     */
    public static void deleteFile(List<Path> files) {
        for (Path file : files) {
            try {
                Files.delete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
