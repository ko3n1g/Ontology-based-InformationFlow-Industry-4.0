package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

public class Material extends Thing {

    protected OWLObjectProperty hasFunctionalTaskInput = dataFactory.getOWLObjectProperty(getIRI(NS_gn, "hasFunctionalTaskInput"));
    protected OWLObjectProperty hasFunctionalTaskOutput = dataFactory.getOWLObjectProperty(getIRI(NS_gn, "hasFunctionalTaskOutput"));

    protected Integer num_materials = 9;
    protected String materialType;
    protected Integer processId;
    protected Integer materialId;

    public Material(OWLOntologyManager manager, Integer processId) throws OWLOntologyCreationException {
        super(manager, "material");
        this.processId = processId;
        this.materialId = (processId-1) * num_materials + 1;
    }

    public Material(OWLOntologyManager manager, String materialType) throws OWLOntologyCreationException {
        super(manager, materialType);
        this.materialType = materialType;
    }

    public IRI add(Material material) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLNamedIndividual ind = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "/" + material.materialType + "#" + materialId));

        String className = Character.toUpperCase(material.materialType.charAt(0)) + material.materialType.substring(1);
        OWLIndividualAxiom ax = dataFactory.getOWLClassAssertionAxiom(dataFactory.getOWLClass(getIRI(NS_sick, className)), ind);
        this.ontology.addAxiom(ax);

        this.materialId++;
        return ind.getIRI();
    }

    public IRI addSolderPaste() throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new SolderPaste(this.manager));
    }

    public IRI addBoard() throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new Board(this.manager));

    }

    public IRI addComponent() throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new Component(this.manager));

    }

    public IRI addPreparedBoard() throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new PreparedBoard(this.manager));

    }

    public IRI addStickedBoard() throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new StickedBoard(this.manager));

    }

    public IRI addSetBoard() throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new SetBoard(this.manager));
    }

    public IRI addSensor() throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new Sensor(this.manager));
    }
}
