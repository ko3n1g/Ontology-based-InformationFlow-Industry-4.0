package EventProcessor;


import openllet.owlapi.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import org.slf4j.*;

public class OWLApiHandler {

    final static private IRI ONTOLOGYIRI   = IRI.create("http://localhost/concept/OML");

    static Logger LOGGER = LoggerFactory.getLogger(OWLApiHandler.class);


    /**
     * 1. Get schema, data from Main.FusekiHandler ; input from param
     *
     * 2. data := data + input
     * 3. Ontology := schema + data
     * 4. InferredOntology := ontology + Reasoner.pellet
     * 5. InferredAxioms := InferredOntology \ Ontology
     *
     * 6. Put data, inferredAxioms
     *
     * @throws OWLOntologyCreationException
     */
    public static List<Path> inferOntology(Integer mode) throws Exception {


        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        final OWLOntologyMerger merger   = new OWLOntologyMerger(manager);

        IRI SCHEMA, DATA, GN, SICK, SWRL, PART, ORG, SOSA, SSN;

        if (mode == 1) {
            DATA = IRI.create(StardogHandler.getTTL(StardogHandler.GRAPH_DATA));
            GN = IRI.create(StardogHandler.getTTL(StardogHandler.GRAPH_GN));
            SICK = IRI.create(StardogHandler.getTTL(StardogHandler.GRAPH_SICK));

            manager.loadOntology(GN);
            manager.loadOntology(SICK);
            manager.loadOntology(DATA);

        } else {
            SCHEMA = IRI.create(FusekiHandler.getTTL(FusekiHandler.GRAPH_DATA));
            DATA = IRI.create(FusekiHandler.getTTL(FusekiHandler.GRAPH_DATA));

            manager.loadOntology(SCHEMA);
            manager.loadOntology(DATA);
        }



        OWLOntology ontology = merger.createMergedOntology(manager, ONTOLOGYIRI);

        // TODO: I shall present ... the most stupid workaround:
        // For some reason, it's not as easy as one would think to merge some
        // ontologies. This workaround, to save it to file and reload it instantly,
        // solves this mysterious behavior. cheers!
        File temp = writeTTL(manager, ontology).toFile();
        manager.removeOntology(ontology);
        ontology = manager.loadOntologyFromOntologyDocument(temp);

        // Retrieve inferred axioms and store as ontology
        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);

        // Create inferred axioms
        OWLOntology inferredAxioms = getInferredAxioms(reasoner, ontology);

        List<Path> files = new ArrayList<>();
        files.add(writeTTL(manager, inferredAxioms));
        return files;
    }

    public static Path writeTTL(OWLOntologyManager manager, OWLOntology ontology) {

        Path tempFile = null;

        try {
            tempFile = File.createTempFile("owlApiHandler",".ttl").toPath();

            DefaultPrefixManager pm = new DefaultPrefixManager();
            pm.setDefaultPrefix(ONTOLOGYIRI + "/#");
            pm.setPrefix("oml.gn:", "http://localhost/concept/ManufacturingOntologyLanguage.General#");
            pm.setPrefix("sick:", "http://localhost/concept/ManufacturingOntologyLanguage.Sick#");

            pm.setPrefix("org:", "http://www.w3.org/ns/org#");
            pm.setPrefix("ssn:", "http://www.w3.org/ns/ssn/");
            pm.setPrefix("sosa:", "http://www.w3.org/ns/sosa/");
            pm.setPrefix("part:", "http://www.w3.org/2001/sw/BestPractices/OEP/SimplePartWhole/part.owl#");

            pm.setPrefix("aoi:", "http://localhost/concept/aoi#");
            pm.setPrefix("shopfloor:", "http://localhost/concept/shopfloor#");

            pm.setPrefix("voaf:", "http://purl.org/vocommons/voaf#");
            pm.setPrefix("schema:", "http://schema.org/");
            pm.setPrefix("skos:", "http://www.w3.org/2004/02/skos/core#");
            pm.setPrefix("foaf:", "http://xmlns.com/foaf/0.1/");
            pm.setPrefix("prov:", "http://www.w3.org/ns/prov#");


            TurtleDocumentFormat ttl = new TurtleDocumentFormat();
            // TODO necessary?
            ttl.copyPrefixesFrom(pm);

            manager.saveOntology(ontology, ttl, IRI.create("file://" + tempFile));

        } catch (IOException|OWLOntologyStorageException e) {
            e.printStackTrace();
        }

        // TODO remove
        System.out.println(tempFile);

        return tempFile;
    }

    private static OWLOntology createInferredOntology(OWLOntologyManager manager, OpenlletReasoner reasoner) throws OWLOntologyCreationException {
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();

        gens.add(new InferredClassAssertionAxiomGenerator());
        gens.add(new InferredEquivalentClassAxiomGenerator());
        gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
        gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());

        OWLOntology infOntology = manager.createOntology(IRI.create("http://localhost/concept/inferredAxioms"));

        long startTime, stopTime, elapsedTime;
        startTime = System.currentTimeMillis();

        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        iog.fillOntology(manager.getOWLDataFactory(), infOntology);

        stopTime = System.currentTimeMillis();
        elapsedTime = (stopTime - startTime) / 1000;
        System.out.println(elapsedTime + " seconds of inference time");

        return infOntology;
    }

    /**
     * Target: Extract only inferred axioms, not the all given axioms.
     *
     *
     * @param reasoner
     * @param ontology
     * @return
     * @throws Exception
     */
    private static OWLOntology getInferredAxioms(OpenlletReasoner reasoner, OWLOntology ontology) throws Exception {

        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();

        long startTime, stopTime, elapsedTime;
        startTime = System.currentTimeMillis();

        // Retrieve inferred axioms and store as ontology
        OWLOntology inferredAxiomsOntology = manager.createOntology();
        inferredAxiomsOntology.addAxioms(getInferredClassAssertionAxioms(dataFactory, reasoner, ontology));
        inferredAxiomsOntology.addAxioms(getInferredPropertyAssertionAxioms(dataFactory, reasoner, ontology));
        inferredAxiomsOntology.addAxioms(getInferredInverseObjectPropertiesAxioms(dataFactory, reasoner, ontology));

        inferredAxiomsOntology.addAxioms(getInferredSubClassAxioms(dataFactory, reasoner, ontology));
        inferredAxiomsOntology.addAxioms(getInferredEquivalentClassAxioms(dataFactory, reasoner, ontology));
        inferredAxiomsOntology.addAxioms(getInferredObjectPropertyCharacteristicAxioms(dataFactory, reasoner, ontology));
        inferredAxiomsOntology.addAxioms(getInferredSubObjectPropertyAxioms(dataFactory, reasoner, ontology));

        stopTime = System.currentTimeMillis();
        elapsedTime = (stopTime - startTime) / 1000;
        System.out.println(elapsedTime + " seconds of inference time");

        return inferredAxiomsOntology;
    }



    /**
     *  getInferred{...}Axioms(OWLDataFactory dataFactory,
                               OpenlletReasoner reasoner,
                               final OWLOntology ontology)
     *
     */
    private static List<OWLSubClassOfAxiom> getInferredSubClassAxioms(OWLDataFactory dataFactory,
                                                         OpenlletReasoner reasoner,
                                                         final OWLOntology ontology)
    {
        // Retrieve axioms of infOntology
        InferredSubClassAxiomGenerator gen = new InferredSubClassAxiomGenerator();
        Set<OWLSubClassOfAxiom> axiomsSet = gen.createAxioms(dataFactory, reasoner);

        List<OWLSubClassOfAxiom> inferred = new ArrayList<>();
        for (OWLSubClassOfAxiom ax : axiomsSet) if (!ontology.containsAxiom(ax))
            inferred.add(ax);

        return inferred;
    }
    private static List<OWLClassAssertionAxiom> getInferredClassAssertionAxioms(OWLDataFactory dataFactory,
                                                               OpenlletReasoner reasoner,
                                                               OWLOntology ontology)
    {
        // Retrieve axioms of infOntology
        InferredClassAssertionAxiomGenerator gen = new InferredClassAssertionAxiomGenerator();
        Set<OWLClassAssertionAxiom> axiomsSet = gen.createAxioms(dataFactory, reasoner);

        List<OWLClassAssertionAxiom> inferred = new ArrayList<>();
        for (OWLClassAssertionAxiom ax : axiomsSet) if (!ontology.containsAxiom(ax))
            inferred.add(ax);

        return inferred;
    }
    private static List<OWLEquivalentClassesAxiom> getInferredEquivalentClassAxioms(OWLDataFactory dataFactory,
                                                                                OpenlletReasoner reasoner,
                                                                                OWLOntology ontology)
    {
        // Retrieve axioms of infOntology
        InferredEquivalentClassAxiomGenerator gen = new InferredEquivalentClassAxiomGenerator();
        Set<OWLEquivalentClassesAxiom> axiomsSet = gen.createAxioms(dataFactory, reasoner);

        List<OWLEquivalentClassesAxiom> inferred = new ArrayList<>();
        for (OWLEquivalentClassesAxiom ax : axiomsSet) if (!ontology.containsAxiom(ax))
            inferred.add(ax);

        return inferred;
    }
    private static List<OWLInverseObjectPropertiesAxiom> getInferredInverseObjectPropertiesAxioms(OWLDataFactory dataFactory,
                                                                                    OpenlletReasoner reasoner,
                                                                                    OWLOntology ontology)
    {
        // Retrieve axioms of infOntology
        InferredInverseObjectPropertiesAxiomGenerator gen = new InferredInverseObjectPropertiesAxiomGenerator();
        Set<OWLInverseObjectPropertiesAxiom> axiomsSet = gen.createAxioms(dataFactory, reasoner);

        List<OWLInverseObjectPropertiesAxiom> inferred = new ArrayList<>();
        for (OWLInverseObjectPropertiesAxiom ax : axiomsSet) if (!ontology.containsAxiom(ax))
            inferred.add(ax);

        return inferred;
    }
    private static List<OWLObjectPropertyCharacteristicAxiom> getInferredObjectPropertyCharacteristicAxioms(OWLDataFactory dataFactory,
                                                                                                  OpenlletReasoner reasoner,
                                                                                                  OWLOntology ontology)
    {
        // Retrieve axioms of infOntology
        InferredObjectPropertyCharacteristicAxiomGenerator gen = new InferredObjectPropertyCharacteristicAxiomGenerator();
        Set<OWLObjectPropertyCharacteristicAxiom> axiomsSet = gen.createAxioms(dataFactory, reasoner);

        List<OWLObjectPropertyCharacteristicAxiom> inferred = new ArrayList<>();
        for (OWLObjectPropertyCharacteristicAxiom ax : axiomsSet) if (!ontology.containsAxiom(ax))
            inferred.add(ax);

        return inferred;
    }
    private static List<OWLPropertyAssertionAxiom> getInferredPropertyAssertionAxioms(OWLDataFactory dataFactory,
                                                                                                           OpenlletReasoner reasoner,
                                                                                                           OWLOntology ontology)
    {
        // Retrieve axioms of infOntology
        InferredPropertyAssertionGenerator gen = new InferredPropertyAssertionGenerator();
        Set<OWLPropertyAssertionAxiom<?, ?>> axiomsSet = gen.createAxioms(dataFactory, reasoner);

        List<OWLPropertyAssertionAxiom> inferred = new ArrayList<>();
        for (OWLPropertyAssertionAxiom ax : axiomsSet) if (!ontology.containsAxiom(ax)) {
            inferred.add(ax);
        }

        return inferred;
    }
    private static List<OWLSubObjectPropertyOfAxiom> getInferredSubObjectPropertyAxioms(OWLDataFactory dataFactory,
                                                                                      OpenlletReasoner reasoner,
                                                                                      OWLOntology ontology)
    {
        // Retrieve axioms of infOntology
        InferredSubObjectPropertyAxiomGenerator gen = new InferredSubObjectPropertyAxiomGenerator();
        Set<OWLSubObjectPropertyOfAxiom> axiomsSet = gen.createAxioms(dataFactory, reasoner);

        List<OWLSubObjectPropertyOfAxiom> inferred = new ArrayList<>();
        for (OWLSubObjectPropertyOfAxiom ax : axiomsSet) if (!ontology.containsAxiom(ax)) {
            inferred.add(ax);
        }

        return inferred;
    }

}
