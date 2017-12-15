package EventProcessor;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.Filter;
import org.apache.jena.vocabulary.ReasonerVocabulary;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class JenaHandler {


    public static List<Path> inferOntologyJenaRules(List<Path> inferredAxioms) throws Exception {

        InfModel inferredOntology   = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        final Model SCHEMA = FusekiHandler.getModel(FusekiHandler.GRAPH_SCHEMA);
        final Model DATA   = FusekiHandler.getModel(FusekiHandler.GRAPH_DATA);

        inferredOntology.add(SCHEMA);
        inferredOntology.add(DATA);

        for (Path inferredAxiom: inferredAxioms) {
            final InputStream inferred = new URL("file://" + inferredAxiom.toString()).openStream();
            inferredOntology.read(inferred, null, "TURTLE");
        }

        Resource configuration = inferredOntology.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
        configuration.addProperty(ReasonerVocabulary.PROPruleSet,  IRIHandler.SICKRULES);

        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);

        InfModel inferredRuleOntology = ModelFactory.createInfModel(reasoner, inferredOntology);
        InfModel ruleStatements       = differentiate(inferredRuleOntology, inferredOntology);

        List<Path> files = new ArrayList<>();
        files.add(writeTTL(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, ruleStatements)));

        return files;
    }

    private static InfModel differentiate(InfModel first, InfModel second) {

        // An iterator over the statements of pModel that *aren't* in the base model.
        ExtendedIterator<Statement> stmts = first.listStatements().filterDrop(new Filter<Statement>() {
            @Override
            public boolean accept(Statement s) {
                return second.contains(s);
            }
        });

        InfModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.add(stmts.toList());

        return model;
    }

    protected static Path writeTTL(OntModel owl) throws Exception {

        File output = File.createTempFile("jenaHandler", ".ttl");
        FileWriter stream = new FileWriter(output);



        // TODO Remove
        System.out.println(output);
        try {
            owl.write(stream, "TURTLE");
            return output.toPath();

        }
        finally {
            try {
                stream.close();
            }
            catch (Exception closeException) {
                // ignore
            }
        }
    }

}
