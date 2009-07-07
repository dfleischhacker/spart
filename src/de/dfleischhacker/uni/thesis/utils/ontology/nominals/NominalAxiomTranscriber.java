package de.dfleischhacker.uni.thesis.utils.ontology.nominals;


import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.*;

import de.dfleischhacker.uni.thesis.GlobalSettings;
import uk.ac.manchester.cs.owl.OWLSubClassAxiomImpl;

import de.dfleischhacker.uni.thesis.utils.ontology.Ontology;


/**
 * Class used to transcribe nominal axioms.
 *
 * Kindly provided by Christian Meilicke. Adapted for use in the semantic
 * precision and recall calculator by getting rid of some unneeded dependencies
 * to other classes.
 */
public class NominalAxiomTranscriber extends OWLEntityCollector {


	private OWLDataFactory factory;
	private Ontology ontology;
	private OWLOntology ontologyCore;

	private static int nextExtCounter = 0;

	private ArrayList<OWLAxiom> rebuiltAxioms;
	private ArrayList<OWLObject> rebuiltObjects;



	public void setLocalOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public void setOntologyCore(OWLOntology ontology) {
		this.ontologyCore = ontology;
	}

	public void setFactory(OWLDataFactory factory) {
		this.factory = factory;
	}


	// *************************************************
	// *** visiting axioms potentially with nominals ***
	// *************************************************

	public ArrayList<OWLAxiom> rebuild(OWLAxiom axiom) throws TranscriptionException {
		this.rebuiltAxioms = new ArrayList<OWLAxiom>();
		this.rebuiltObjects = new ArrayList<OWLObject>();
		if (axiom instanceof OWLSubClassAxiom || axiom instanceof OWLSubClassAxiomImpl) {
			this.rebuild((OWLSubClassAxiom)axiom);
		}
		else if (axiom instanceof OWLEquivalentClassesAxiom) {
			this.rebuild((OWLEquivalentClassesAxiom)axiom);
		}
		else if (axiom instanceof OWLDisjointUnionAxiom) {
			this.rebuild((OWLDisjointUnionAxiom)axiom);
		}
		else if (axiom instanceof OWLDisjointClassesAxiom) {
			this.rebuild((OWLDisjointClassesAxiom)axiom);
		}
		else if (axiom instanceof OWLDataPropertyDomainAxiom) {
			this.rebuild((OWLDataPropertyDomainAxiom)axiom);
		}
		else if (axiom instanceof OWLDataPropertyRangeAxiom) {
			this.rebuild((OWLDataPropertyRangeAxiom)axiom);
		}
		else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
			this.rebuild((OWLObjectPropertyDomainAxiom)axiom);
		}
		else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
			this.rebuild((OWLObjectPropertyRangeAxiom)axiom);
		}
		else {
			throw new TranscriptionException(
				"could not rewrite ontology to get rid of nominals (" + axiom + " is " + axiom.getClass() +")"
			);
		}
		return this.rebuiltAxioms;
	}


    public void rebuild(OWLSubClassAxiom axiom) {
    	axiom.getSubClass().accept(this);
    	OWLDescription subClass = this.getRelevantDescription();
        axiom.getSuperClass().accept(this);
        OWLDescription superClass = this.getRelevantDescription();
        this.rebuiltAxioms.add(this.factory.getOWLSubClassAxiom(subClass, superClass));
    }

    public void rebuild(OWLEquivalentClassesAxiom axiom) {
        for (OWLDescription desc : axiom.getDescriptions()) {
            desc.accept(this);
        }
        Set<OWLDescription> equivClasses = this.getRelevantDescriptionsAsSet(0);
        this.rebuiltAxioms.add(this.factory.getOWLEquivalentClassesAxiom(equivClasses));
    }


    public void rebuild(OWLDisjointUnionAxiom axiom) {
        axiom.getOWLClass().accept(this);
        OWLClass equivClass = (OWLClass)this.getRelevantDescription();
        for (OWLDescription desc : axiom.getDescriptions()) {
            desc.accept(this);
        }
        Set<OWLDescription> disjointUnionClasses = this.getRelevantDescriptionsAsSet(0);
        this.rebuiltAxioms.add(this.factory.getOWLDisjointUnionAxiom(equivClass, disjointUnionClasses));
    }

    public void rebuild(OWLDisjointClassesAxiom axiom) {
        for (OWLDescription desc : axiom.getDescriptions()) {
            desc.accept(this);
        }
        Set<OWLDescription> disjointClasses = this.getRelevantDescriptionsAsSet(0);
        this.rebuiltAxioms.add(this.factory.getOWLDisjointClassesAxiom(disjointClasses));
    }

    public void rebuild(OWLDataPropertyDomainAxiom axiom) {
        axiom.getDomain().accept(this);
        OWLDescription domainClass = this.getRelevantDescription();
        this.rebuiltAxioms.add(this.factory.getOWLDataPropertyDomainAxiom(axiom.getProperty(), domainClass));
    }

    public void rebuild(OWLObjectPropertyDomainAxiom axiom) {
        axiom.getDomain().accept(this);
        OWLDescription domainClass = this.getRelevantDescription();
        this.rebuiltAxioms.add(this.factory.getOWLObjectPropertyDomainAxiom(axiom.getProperty(), domainClass));
    }

    public void rebuild(OWLObjectPropertyRangeAxiom axiom) {
        axiom.getRange().accept(this);
        OWLDescription rangeClass = this.getRelevantDescription();
        this.rebuiltAxioms.add(this.factory.getOWLObjectPropertyRangeAxiom(axiom.getProperty(), rangeClass));

    }

	// *************************************
	// *** visiting complex descriptions ***
	// *************************************

	// DONE
	@Override
    public void visit(OWLObjectIntersectionOf desc) {
    	int reduceTo = this.rebuiltObjects.size();
    	for (OWLDescription operand : desc.getOperands()) {
            operand.accept(this);
        }
    	HashSet<OWLDescription> ds = this.getRelevantDescriptionsAsSet(reduceTo);
    	this.rebuiltObjects.add(this.factory.getOWLObjectIntersectionOf(ds));
    }

    // DONE
	@Override
    public void visit(OWLObjectUnionOf desc) {
    	int reduceTo = this.rebuiltObjects.size();
    	for (OWLDescription operand : desc.getOperands()) {
            operand.accept(this);
        }
    	HashSet<OWLDescription> ds = this.getRelevantDescriptionsAsSet(reduceTo);
    	this.rebuiltObjects.add(this.factory.getOWLObjectUnionOf(ds));
    }

    // DONE
	@Override
    public void visit(OWLObjectComplementOf desc) {
        desc.getOperand().accept(this);
        OWLDescription d = this.getRelevantDescription();
        this.rebuiltObjects.add(this.factory.getOWLObjectComplementOf(d));
    }

    // DONE
	@Override
    public void visit(OWLObjectSomeRestriction desc) {
        desc.getProperty().accept(this);
        desc.getFiller().accept(this);
        OWLDescription d = this.getRelevantDescription();
        this.rebuiltObjects.add(this.factory.getOWLObjectSomeRestriction(desc.getProperty(), d));
    }

    // DONE
	@Override
    public void visit(OWLObjectAllRestriction desc) {
        // desc.getProperty().accept(this);
        desc.getFiller().accept(this);
        OWLDescription d = this.getRelevantDescription();
        this.rebuiltObjects.add(this.factory.getOWLObjectAllRestriction(desc.getProperty(), d));
    }

    // DONE
	@Override
    public void visit(OWLObjectMinCardinalityRestriction desc) {
        // desc.getProperty().accept(this);
        desc.getFiller().accept(this);
        OWLDescription d = this.getRelevantDescription();
        this.rebuiltObjects.add(this.factory.getOWLObjectMinCardinalityRestriction(desc.getProperty(), desc.getCardinality(), d));
    }

    // DONE
	@Override
    public void visit(OWLObjectExactCardinalityRestriction desc) {
	   // desc.getProperty().accept(this);
	   desc.getFiller().accept(this);
	   OWLDescription d = this.getRelevantDescription();
	   this.rebuiltObjects.add(this.factory.getOWLObjectExactCardinalityRestriction(desc.getProperty(), desc.getCardinality(), d));
    }

    // DONE
	@Override
    public void visit(OWLObjectMaxCardinalityRestriction desc) {
        // desc.getProperty().accept(this);
        desc.getFiller().accept(this);
        OWLDescription d = this.getRelevantDescription();
        this.rebuiltObjects.add(this.factory.getOWLObjectMaxCardinalityRestriction(desc.getProperty(), desc.getCardinality(), d));
    }



    // continu here, rething the outcommented stuff below
    // surround with if or better make two extension classes (to avoid to much if switches


    /* --- all this seems not to be relevant for the nominal case ---

    public void visit(OWLDataSomeRestriction desc) {
        desc.getProperty().accept(this);
        desc.getFiller().accept(this);
    }

    public void visit(OWLDataAllRestriction desc) {
        desc.getProperty().accept(this);
        desc.getFiller().accept(this);
    }

    public void visit(OWLDataValueRestriction desc) {
        desc.getProperty().accept(this);
        desc.getValue().accept(this);
    }


    public void visit(OWLDataMinCardinalityRestriction desc) {
        desc.getProperty().accept(this);
        desc.getFiller().accept(this);
    }

    public void visit(OWLDataExactCardinalityRestriction desc) {
        desc.getProperty().accept(this);
        desc.getFiller().accept(this);
    }

    public void visit(OWLDataMaxCardinalityRestriction desc) {
        desc.getProperty().accept(this);
        desc.getFiller().accept(this);
    }
    */

	// ***********************************
	// *** visiting named descriptions ***
	// ***********************************

    // DONE - very important one, forgot this in first version
	@Override
    public void visit(OWLClass c) {
    	this.rebuiltObjects.add(c);

    }

	// ******************************************
	// *** visiting and transcribing nominals ***
	// ******************************************

	@Override
    public void visit(OWLObjectOneOf desc) {
		URI nominalClassURI = URI.create(
			GlobalSettings.NOMINAL_PREFIX + "#OneOfSubstitute_" +
			nextExtCounter++);
		OWLClass nominalClass = this.factory.getOWLClass(nominalClassURI);
		this.addSubClassAxiomForNominalSubstitute(desc, nominalClass);
		this.rebuiltObjects.add(nominalClass);
	}

	@Override
    public void visit(OWLObjectValueRestriction desc) {
		URI nominalClassURI = URI.create(
				GlobalSettings.NOMINAL_PREFIX + "#ValueRestrictionSubstitute_"
				+ nextExtCounter++);
		OWLClass nominalClass = this.factory.getOWLClass(nominalClassURI);
		OWLObjectSomeRestriction someRestriction = this.factory.getOWLObjectSomeRestriction(desc.getProperty(), nominalClass);
		this.addSubClassAxiomForNominalSubstitute(desc.getValue(), nominalClass);
		this.rebuiltObjects.add(someRestriction);
	}

    // kind of nominal: EX loves.Self (self restriction, defines the class of those people loving theirselves)
    // seems to be a kind of special case of a ValueRestriction
    // TODO add subclass statements (just add the domain ... maybe, seems to be not part of OWL 1.0)
	@Override
    public void visit(OWLObjectSelfRestriction desc) {
    	URI nominalClassURI = URI.create(
				GlobalSettings.NOMINAL_PREFIX + "#ValueRestrictionSubstitute_"
				+ nextExtCounter++);
    	OWLClass nominalClass = this.factory.getOWLClass(nominalClassURI);
    	OWLObjectSomeRestriction someRestriction = this.factory.getOWLObjectSomeRestriction(desc.getProperty(), nominalClass);
    	this.rebuiltObjects.add(someRestriction);
    }

	// **********************
	// *** helper methods ***
	// **********************

	private void addSubClassAxiomForNominalSubstitute(OWLObjectOneOf desc, OWLClass nominalClass) {
		Set<OWLDescription> typesOfIndividuals = new HashSet<OWLDescription>();
    	for (OWLIndividual ind : desc.getIndividuals()) {
    		typesOfIndividuals.addAll(ind.getTypes(this.ontologyCore));
    	}
    	if (typesOfIndividuals.size() == 1) {
    		OWLDescription typeOfIndividual = null;
    		for (OWLDescription typeOfInd : typesOfIndividuals) { typeOfIndividual = typeOfInd; }
			OWLSubClassAxiom subClassAxiom = this.factory.getOWLSubClassAxiom(nominalClass, typeOfIndividual);
			this.rebuiltAxioms.add(subClassAxiom);
    	}
    	if (typesOfIndividuals.size() > 1) {
			OWLObjectUnionOf unionClass = this.factory.getOWLObjectUnionOf(typesOfIndividuals);
			OWLSubClassAxiom subClassAxiom = this.factory.getOWLSubClassAxiom(nominalClass, unionClass);
			this.rebuiltAxioms.add(subClassAxiom);
    	}

	}

	private void addSubClassAxiomForNominalSubstitute(OWLIndividual ind, OWLClass nominalClass) {
		Set<OWLDescription> typesOfIndividuals = ind.getTypes(this.ontologyCore);
    	if (typesOfIndividuals.size() == 1) {
    		OWLDescription typeOfIndividual = null;
    		for (OWLDescription typeOfInd : typesOfIndividuals) { typeOfIndividual = typeOfInd; }
			OWLSubClassAxiom subClassAxiom = this.factory.getOWLSubClassAxiom(nominalClass, typeOfIndividual);
			this.rebuiltAxioms.add(subClassAxiom);
    	}
    	if (typesOfIndividuals.size() > 1) {
			OWLObjectUnionOf unionClass = this.factory.getOWLObjectUnionOf(typesOfIndividuals);
			OWLSubClassAxiom subClassAxiom = this.factory.getOWLSubClassAxiom(nominalClass, unionClass);
			this.rebuiltAxioms.add(subClassAxiom);
    	}
	}

	private HashSet<OWLDescription>  getRelevantDescriptionsAsSet(int reduceTo) {
		HashSet<OWLDescription> descs = new HashSet<OWLDescription>();
		while (this.rebuiltObjects.size() > reduceTo) {
			descs.add((OWLDescription)this.rebuiltObjects.remove(this.rebuiltObjects.size() - 1));
		}
		return descs;
	}

	private OWLDescription getRelevantDescription() {
		return (OWLDescription)this.rebuiltObjects.remove(this.rebuiltObjects.size() - 1);
	}
}
