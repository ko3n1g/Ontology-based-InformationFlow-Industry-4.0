package EventDataProcessor;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Observation extends Thing {

    protected OWLObjectProperty madeBySensor = dataFactory.getOWLObjectProperty(getIRI(NS_SOSA, "madeBySensor"));
    protected OWLObjectProperty observedProperty = dataFactory.getOWLObjectProperty(getIRI(NS_SOSA, "observedProperty"));
    protected OWLObjectProperty hasFeatureOfInterest = dataFactory.getOWLObjectProperty(getIRI(NS_SOSA, "hasFeatureOfInterest"));

    protected OWLDataProperty hasSimpleResult = dataFactory.getOWLDataProperty(getIRI(NS_SOSA, "hasSimpleResult"));
    protected OWLDataProperty resultTime = dataFactory.getOWLDataProperty(getIRI(NS_SOSA, "resultTime"));

    protected Integer processId, num_observations, observationId;

    protected OWLNamedIndividual xshift = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, "aoi#x-shift"));
    protected OWLNamedIndividual yshift = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, "aoi#y-shift"));
    protected OWLNamedIndividual twisting = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, "aoi#twisting"));

    protected OWLNamedIndividual sensor;
    protected List<OWLNamedIndividual> properties = new ArrayList<>();

    public Observation(OWLOntologyManager manager, Integer processId, IRI sensor) throws OWLOntologyCreationException {
        super(manager, "observation");
        this.processId = processId;
        this.num_observations = 18;
        this.observationId = (processId-1) * num_observations + 1;

        this.properties.add(xshift);
        this.properties.add(yshift);
        this.properties.add(twisting);

        this.sensor = dataFactory.getOWLNamedIndividual(sensor);
    }

    public List<IRI> create(IRI component) {

        OWLNamedIndividual ind;
        OWLNamedIndividual comp = dataFactory.getOWLNamedIndividual(component);
        List<IRI> observations = new ArrayList<>();

        OWLObjectPropertyAssertionAxiom ax1;
        OWLObjectPropertyAssertionAxiom ax2;
        OWLObjectPropertyAssertionAxiom ax3;
        OWLDataPropertyAssertionAxiom ax4;
        OWLDataPropertyAssertionAxiom ax5;

        for (OWLNamedIndividual property : properties) {
            ind = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "#" + observationId));

            OWLLiteral dataLiteral = dataFactory.getOWLLiteral("2016-05-18T12:55:27.954", OWL2Datatype.XSD_DATE_TIME);


            ax1 = dataFactory.getOWLObjectPropertyAssertionAxiom(madeBySensor, ind, this.sensor);
            ax2 = dataFactory.getOWLObjectPropertyAssertionAxiom(observedProperty, ind, property);
            ax3 = dataFactory.getOWLObjectPropertyAssertionAxiom(hasFeatureOfInterest, ind, comp);
            ax4 = dataFactory.getOWLDataPropertyAssertionAxiom(hasSimpleResult, ind, dataFactory.getOWLLiteral(ThreadLocalRandom.current().nextInt(2, 10 + 1)));
            ax5 = dataFactory.getOWLDataPropertyAssertionAxiom(resultTime, ind, dataLiteral);

            ontology.addAxiom(ax1);
            ontology.addAxiom(ax2);
            ontology.addAxiom(ax3);
            ontology.addAxiom(ax4);
            ontology.addAxiom(ax5);

            observations.add(ind.getIRI());
            observationId++;

        }

        return observations;

    }
}
