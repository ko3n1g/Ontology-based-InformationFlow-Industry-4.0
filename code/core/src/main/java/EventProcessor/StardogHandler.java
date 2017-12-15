package EventProcessor;

import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.api.*;
import org.openrdf.rio.RDFFormat;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class StardogHandler {

    private static final String NS = "http://localhost/concept/oml/";

    public static final String GRAPH_GN       = NS + "gn";
    public static final String GRAPH_SICK     = NS + "sick";
    public static final String GRAPH_DATA     = NS + "data";
    public static final String GRAPH_INFERRED = NS + "inferred";

    public static final String DB = "oml";

    public static void upload(List<Path> input, String location) throws Exception {
        try (Connection conn = ConnectionConfiguration
                .to(DB)
                .server("http://localhost:5820/")
                .credentials("admin", "admin")
                .connect()) {

            conn.begin();

            for (Path path : input) {
                conn.add().io()
                        .context(Values.iri(location))
                        .format(RDFFormat.TURTLE)
                        .stream(new FileInputStream(path.toString()));
            }

            conn.commit();
            conn.close();
            System.out.println("done");
        }
    }

    public static void replace(List<Path> input, String location) throws Exception {
        try (Connection conn = ConnectionConfiguration
                .to(DB)
                .server("http://localhost:5820/")
                .credentials("admin", "admin")
                .connect()) {

            conn.begin();

            for (Path path : input) {

                conn.add().io()
                        .context(Values.iri(location))
                        .format(RDFFormat.TURTLE)
                        .stream(new FileInputStream(path.toString()));
            }

            conn.commit();
            conn.close();
        }
    }

    public static File getTTL(String location) throws Exception {
        try (Connection conn = ConnectionConfiguration
                .to(DB)
                .server("http://localhost:5820/")
                .credentials("admin", "admin")
                .connect()) {


            Exporter exporter = conn.export();

            File output = File.createTempFile("stardogHandler", ".ttl");
            FileOutputStream stream = new FileOutputStream(output);

            System.out.println(output);
            exporter.context(Values.iri(location)).to(stream);

            return output;
        }
    }


}
