package EventProcessor;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class FusekiHandler {

    private static final String SERVICEIRI = "http://localhost:3030/OML.complete/data";

    public static final String GRAPH_SCHEMA   = "http://localhost/foo/schema";
    public static final String GRAPH_DATA     = "http://localhost/foo/data";
    public static final String GRAPH_INFERRED = "http://localhost/foo/inferred";


    protected static Model getModel(String graph) {
        DatasetAccessor accessor = DatasetAccessorFactory
                .createHTTP(SERVICEIRI);

        return accessor.getModel(graph);
    }

    protected static Boolean uploadModel(String graph, Model model) {

        // upload the resulting model
        DatasetAccessor accessor = DatasetAccessorFactory
                .createHTTP(SERVICEIRI);
        accessor.add(graph, model);

        return true;
    }

    protected static Boolean putModel(String graph, Model model) {

        // delete the default model of the accessed dataset
        DatasetAccessor accessor = DatasetAccessorFactory
                .createHTTP(SERVICEIRI);
        accessor.putModel(graph, model);

        return true;
    }

    protected static Boolean uploadTTL(List<Path> paths, String graph) throws Exception {

        Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        for (Path path : paths) {
            final InputStream content = new URL("file://" + path.toString()).openStream();
            model.read(content, null, "TURTLE");
        }

        return FusekiHandler.uploadModel(graph, model);
    }

    protected static Boolean replaceTTL(List<Path> paths, String graph) throws Exception {

        Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        for (Path path : paths) {
            final InputStream content = new URL("file://" + path.toString()).openStream();
            model.read(content, null, "TURTLE");
        }

        return FusekiHandler.putModel(graph, model);
    }

    protected static File getTTL(String graph) throws Exception {

        return JenaHandler.writeTTL(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, getModel(graph))).toFile();

    }

}
