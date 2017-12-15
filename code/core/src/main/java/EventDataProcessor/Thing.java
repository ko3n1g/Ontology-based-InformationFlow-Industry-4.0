package EventDataProcessor;

import EventProcessor.IRIHandler;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Thing {

    protected OWLOntology ontology;
    protected OWLOntologyManager manager;
    protected OWLDataFactory dataFactory;
    protected OWLNamedIndividual self;

    protected IRI iri;

    protected String NS_SOSA        = "http://www.w3.org/ns/sosa/";
    protected String NS_SSN         = "http://www.w3.org/ns/ssn/";
    protected String NS_gn          = "http://localhost/concept/oml/gn#";
    protected String NS_sick        = "http://localhost/concept/oml/sick#";
    protected String NS_aoi         = "http://localhost/concept/aoi#";
    protected String NS_individual  = "http://localhost/concept/";

    protected String save_dir       = IRIHandler.INPUT;

    protected String ontologyName;

    public Thing(OWLOntologyManager manager, String ontologyName) throws OWLOntologyCreationException {

        this.manager = manager;
        this.dataFactory = manager.getOWLDataFactory();
        this.ontologyName = ontologyName;

        IRI iri = getIRI(NS_individual, "process" + "Individuals");

        this.ontology = (manager.getOntology(iri) == null)
                ? manager.createOntology(iri)
                : manager.getOntology(iri);
    }

    public void print() throws OWLOntologyStorageException {
        print(false);
    }

    public void print(Boolean one) throws  OWLOntologyStorageException {
        if (one) {
            TurtleDocumentFormat ttl = new TurtleDocumentFormat();

            try {

                OutputStream file = new FileOutputStream(this.save_dir + this.ontologyName + ".ttl", true);
                this.manager.saveOntology(this.ontology, ttl, file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    protected IRI getIRI(String NS, String id) {
        return IRI.create(NS + id);
    }

    public OWLNamedIndividual create(Integer id, List<IRI> inputs, List<IRI> outputs, IRI sensor) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLNamedIndividual ind = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "#" + id));
        return ind;
    }
}
