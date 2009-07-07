/*
 *
 * Ontology.java
 *
 * Software License Agreement (BSD License)
 *
 * Copyright (c) 2009 Daniel Fleischhacker <dev@dfleischhacker.de>
 * All rights reserved.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package de.dfleischhacker.uni.thesis.utils.ontology;

import de.dfleischhacker.uni.thesis.utils.ontology.nominals.NominalAxiomDetector;
import de.dfleischhacker.uni.thesis.utils.ontology.nominals.NominalAxiomTranscriber;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLLogicalAxiom;
import org.semanticweb.owl.model.OWLMutableOntology;
import org.semanticweb.owl.model.OWLNamedObjectVisitor;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLObjectVisitorEx;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.RemoveAxiom;
import org.semanticweb.owl.model.SWRLRule;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.util.OWLEntityRemover;

/**
 * Ontology is a class wrapping the OWLOntology interface to provide the capability
 * to store a reference to the OWLOntologyManager used to create this ontology.
 * This knowledge is needed to work with the ontology (i.e. renaming entities)
 * and because of that the class Ontology is used throughout this code in
 * favor of the OWLOntology interface.
 *
 * Additionally this class provides methods to remove individuals from the
 * ontology.
 * 
 * <b>Be aware of the fact that using the same OWLOntologyManager to create
 * multiple OWLOntology instances will almost definitely result in errors.
 * Therefore the preferred way to generate an Ontology is the usage of the
 * OntologyManager.</b>
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class Ontology implements OWLMutableOntology {
	// ontology wrapped by this class
	private OWLOntology o;
	
	// OntologyManager used to create the wrapped OWLOntology
	OWLOntologyManager manager;

	/**
	 * Sets the OWLOntology to wrap and the OWLOntologyManager used to create it.
	 * @param ontology ontology to wrap
	 * @param manager manager used to create wrapped ontology
	 */
	public Ontology(OWLOntology ontology, OWLOntologyManager manager) {
		this.manager = manager;
		this.o = ontology;
	}


	/**
	 * Deletes individuals found in the ABox of this ontology. Before doing so
	 * possible nominals are transcribed.
	 *
	 * This methods bases on code kindly provided by Christian Meilicke.
	 * @throws java.lang.Exception
	 */
	public void deleteABox() throws Exception {
		transcribeNominalAxioms();
		HashSet<OWLOntology> dummy = new HashSet();
		dummy.add(this);
        OWLEntityRemover remover = new OWLEntityRemover(this.manager, dummy);
        int indCounter = 0;
        for(OWLIndividual individual : this.getReferencedIndividuals()) {
            indCounter++;
            individual.accept(remover);
        }
        if (this.getReferencedIndividuals().size() > 0) {
            try {
                this.manager.applyChanges(remover.getChanges());
            }
            catch (OWLOntologyChangeException e) {
                throw new Exception("Remove");
            }
            System.out.println("removed " +  indCounter + " individuals");
        }
        else {
            System.out.println("ontology contained no individuals");
        }
    }

	/**
	 * Used to transcribe nominal axioms before removing instances.
	 *
	 * Kindly provided by Christian Meilicke. Adapted to blend with the
	 * calculator object hierarchies.
	 */
	private void transcribeNominalAxioms() throws Exception {
		// init the detector
		NominalAxiomDetector nominalDetector = new NominalAxiomDetector();
		// init the transcriber
		NominalAxiomTranscriber nominalTranscriber = new NominalAxiomTranscriber();
		nominalTranscriber.setLocalOntology(this);
		nominalTranscriber.setOntologyCore(this);
		nominalTranscriber.setFactory(this.getManager().getOWLDataFactory());
		// ... keep track of the things to be changed
		Set<OWLAxiom> axiomsToBeRemoved = new HashSet<OWLAxiom>();
		Set<OWLAxiom> axiomsToBeAdded = new HashSet<OWLAxiom>();
		Set<OWLAxiom> axioms = this.getAxioms();
		for (OWLAxiom axiom : axioms) {
			//System.out.println("AXIOM:" + axiom);
			axiom.accept(nominalDetector);
			if (nominalDetector.detectedNominal()) {
				axiomsToBeRemoved.add(axiom);
				nominalDetector.reset();
				System.out.print("detected axiom using nominal: " + axiom);
				ArrayList<OWLAxiom> rebuiltAxioms = nominalTranscriber.rebuild(axiom);
				System.out.print("[transcribed as: ");
				for (OWLAxiom rebuiltAxiom : rebuiltAxioms) {
					axiomsToBeAdded.add(rebuiltAxiom);
					System.out.print(rebuiltAxiom.toString() + " ");
				}
				System.out.println("]");
			}
		}
		// first add all axioms replacing nominal-axioms
		for (OWLAxiom axiomToBeAdded : axiomsToBeAdded) {
			 this.manager.applyChange(new AddAxiom(this, axiomToBeAdded));
		}
		// then remove the nominal-axioms
		for (OWLAxiom axiomToBeRemoved : axiomsToBeRemoved) {
			 this.manager.applyChange(new RemoveAxiom(this, axiomToBeRemoved));
		}
	}

	/**
	 * Returns the OWLOntologyManager used to create this ontology
	 * @return manager used to create ontology
	 */
	public OWLOntologyManager getManager() {
		return manager;
	}


	/*
	 * The following methods implement the OWLOntology interface.
	 */
	@Override
	public Set<OWLAxiom> getAxioms() {
		return o.getAxioms();
	}

	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms() {
		return o.getLogicalAxioms();
	}

	@Override
	public Set<SWRLRule> getRules() {
		return o.getRules();
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType) {
		return o.getAxioms(axiomType);
	}

	@Override
	public Set<OWLClassAxiom> getClassAxioms() {
		return o.getClassAxioms();
	}

	@Override
	public Set<OWLPropertyAxiom> getObjectPropertyAxioms() {
		return o.getObjectPropertyAxioms();
	}

	@Override
	public Set<OWLPropertyAxiom> getDataPropertyAxioms() {
		return o.getDataPropertyAxioms();
	}

	@Override
	public Set<OWLIndividualAxiom> getIndividualAxioms() {
		return o.getIndividualAxioms();
	}

	@Override
	public Set<OWLClassAxiom> getGeneralClassAxioms() {
		return o.getGeneralClassAxioms();
	}

	@Override
	public Set<OWLObjectPropertyChainSubPropertyAxiom> getPropertyChainSubPropertyAxioms() {
		return o.getPropertyChainSubPropertyAxioms();
	}

	@Override
	public Set<OWLEntity> getReferencedEntities() {
		return o.getReferencedEntities();
	}

	@Override
	public Set<OWLClass> getReferencedClasses() {
		return o.getReferencedClasses();
	}

	@Override
	public Set<OWLObjectProperty> getReferencedObjectProperties() {
		return o.getReferencedObjectProperties();
	}

	@Override
	public Set<OWLDataProperty> getReferencedDataProperties() {
		return o.getReferencedDataProperties();
	}

	@Override
	public Set<OWLIndividual> getReferencedIndividuals() {
		return o.getReferencedIndividuals();
	}

	@Override
	public Set<URI> getAnnotationURIs() {
		return o.getAnnotationURIs();
	}

	@Override
	public Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity) {
		return o.getReferencingAxioms(owlEntity);
	}

	@Override
	public boolean containsEntityReference(OWLEntity owlEntity) {
		return o.containsEntityReference(owlEntity);
	}

	@Override
	public boolean containsEntityDeclaration(OWLEntity owlEntity) {
		return o.containsEntityDeclaration(owlEntity);
	}

	@Override
	public boolean containsClassReference(URI owlClassURI) {
		return o.containsClassReference(owlClassURI);
	}

	@Override
	public boolean containsObjectPropertyReference(URI propURI) {
		return o.containsObjectPropertyReference(propURI);
	}

	@Override
	public boolean containsDataPropertyReference(URI propURI) {
		return o.containsDataPropertyReference(propURI);
	}

	@Override
	public boolean containsIndividualReference(URI individualURI) {
		return o.containsIndividualReference(individualURI);
	}

	@Override
	public boolean containsDataTypeReference(URI datatypeURI) {
		return o.containsDataTypeReference(datatypeURI);
	}

	@Override
	public boolean isPunned(URI uri) {
		return o.isPunned(uri);
	}

	@Override
	public Set<OWLClassAxiom> getAxioms(OWLClass cls) {
		return o.getAxioms(cls);
	}

	@Override
	public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression prop) {
		return o.getAxioms(prop);
	}

	@Override
	public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty prop) {
		return o.getAxioms(prop);
	}

	@Override
	public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual) {
		return o.getAxioms(individual);
	}

	@Override
	public Set<OWLAnnotationAxiom> getAnnotationAxioms() {
		return o.getAnnotationAxioms();
	}

	@Override
	public Set<OWLDeclarationAxiom> getDeclarationAxioms() {
		return o.getDeclarationAxioms();
	}

	@Override
	public Set<OWLImportsDeclaration> getImportsDeclarations() {
		return o.getImportsDeclarations();
	}

	@Override
	public Set<OWLOntology> getImports(OWLOntologyManager ontologyManager) throws UnknownOWLOntologyException {
		return o.getImports(ontologyManager);
	}

	@Override
	public boolean containsAxiom(OWLAxiom axiom) {
		return o.containsAxiom(axiom);
	}

	@Override
	public Set<OWLDeclarationAxiom> getDeclarationAxioms(OWLEntity subject) {
		return o.getDeclarationAxioms(subject);
	}

	@Override
	public Set<OWLAxiomAnnotationAxiom> getAnnotations(OWLAxiom axiom) {
		return o.getAnnotations(axiom);
	}

	@Override
	public Set<OWLEntityAnnotationAxiom> getEntityAnnotationAxioms(OWLEntity entity) {
		return o.getEntityAnnotationAxioms(entity);
	}

	@Override
	public Set<OWLOntologyAnnotationAxiom> getAnnotations(OWLOntology subject) {
		return getAnnotations(subject);
	}

	@Override
	public Set<OWLOntologyAnnotationAxiom> getOntologyAnnotationAxioms() {
		return o.getOntologyAnnotationAxioms();
	}

	@Override
	public Set<OWLSubClassAxiom> getSubClassAxiomsForLHS(OWLClass cls) {
		return o.getSubClassAxiomsForLHS(cls);
	}

	@Override
	public Set<OWLSubClassAxiom> getSubClassAxiomsForRHS(OWLClass cls) {
		return o.getSubClassAxiomsForRHS(cls);
	}

	@Override
	public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(OWLClass cls) {
		return o.getEquivalentClassesAxioms(cls);
	}

	@Override
	public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(OWLClass cls) {
		return o.getDisjointClassesAxioms(cls);
	}

	@Override
	public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(OWLClass owlClass) {
		return o.getDisjointUnionAxioms(owlClass);
	}

	@Override
	public Set<OWLObjectSubPropertyAxiom> getObjectSubPropertyAxiomsForLHS(OWLObjectPropertyExpression property) {
		return o.getObjectSubPropertyAxiomsForLHS(property);
	}

	@Override
	public Set<OWLObjectSubPropertyAxiom> getObjectSubPropertyAxiomsForRHS(OWLObjectPropertyExpression property) {
		return o.getObjectSubPropertyAxiomsForRHS(property);
	}

	@Override
	public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectPropertyExpression property) {
		return o.getObjectPropertyDomainAxioms(property);
	}

	@Override
	public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectPropertyExpression property) {
		return o.getObjectPropertyRangeAxioms(property);
	}

	@Override
	public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(OWLObjectPropertyExpression property) {
		return o.getInverseObjectPropertyAxioms(property);
	}

	@Override
	public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(OWLObjectPropertyExpression property) {
		return o.getEquivalentObjectPropertiesAxioms(property);
	}

	@Override
	public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxiom(OWLObjectPropertyExpression property) {
		return o.getDisjointObjectPropertiesAxiom(property);
	}

	@Override
	public OWLFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(OWLObjectPropertyExpression property) {
		return o.getFunctionalObjectPropertyAxiom(property);
	}

	@Override
	public OWLInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(OWLObjectPropertyExpression property) {
		return o.getInverseFunctionalObjectPropertyAxiom(property);
	}

	@Override
	public OWLSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(OWLObjectPropertyExpression property) {
		return o.getSymmetricObjectPropertyAxiom(property);
	}

	@Override
	public OWLAntiSymmetricObjectPropertyAxiom getAntiSymmetricObjectPropertyAxiom(OWLObjectPropertyExpression property) {
		return o.getAntiSymmetricObjectPropertyAxiom(property);
	}

	@Override
	public OWLReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(OWLObjectPropertyExpression property) {
		return o.getReflexiveObjectPropertyAxiom(property);
	}

	@Override
	public OWLIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(OWLObjectPropertyExpression property) {
		return o.getIrreflexiveObjectPropertyAxiom(property);
	}

	@Override
	public OWLTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(OWLObjectPropertyExpression property) {
		return o.getTransitiveObjectPropertyAxiom(property);
	}

	@Override
	public Set<OWLDataSubPropertyAxiom> getDataSubPropertyAxiomsForLHS(OWLDataProperty lhsProperty) {
		return o.getDataSubPropertyAxiomsForLHS(lhsProperty);
	}

	@Override
	public Set<OWLDataSubPropertyAxiom> getDataSubPropertyAxiomsForRHS(OWLDataPropertyExpression property) {
		return o.getDataSubPropertyAxiomsForRHS(property);
	}

	@Override
	public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty property) {
		return o.getDataPropertyDomainAxioms(property);
	}

	@Override
	public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxiom(OWLDataProperty property) {
		return o.getDataPropertyRangeAxiom(property);
	}

	@Override
	public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxiom(OWLDataProperty property) {
		return o.getEquivalentDataPropertiesAxiom(property);
	}

	@Override
	public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxiom(OWLDataProperty property) {
		return o.getDisjointDataPropertiesAxiom(property);
	}

	@Override
	public OWLFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(OWLDataPropertyExpression property) {
		return o.getFunctionalDataPropertyAxiom(property);
	}

	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLIndividual individual) {
		return o.getClassAssertionAxioms(individual);
	}

	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLClass type) {
		return o.getClassAssertionAxioms(type);
	}

	@Override
	public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(OWLIndividual individual) {
		return o.getDataPropertyAssertionAxioms(individual);
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(OWLIndividual individual) {
		return o.getObjectPropertyAssertionAxioms(individual);
	}

	@Override
	public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(OWLIndividual individual) {
		return o.getNegativeObjectPropertyAssertionAxioms(individual);
	}

	@Override
	public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(OWLIndividual individual) {
		return o.getNegativeDataPropertyAssertionAxioms(individual);
	}

	@Override
	public Set<OWLSameIndividualsAxiom> getSameIndividualAxioms(OWLIndividual individual) {
		return o.getSameIndividualAxioms(individual);
	}

	@Override
	public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(OWLIndividual individual) {
		return o.getDifferentIndividualAxioms(individual);
	}

	@Override
	public URI getURI() {
		return o.getURI();
	}

	@Override
	public void accept(OWLNamedObjectVisitor visitor) {
		o.accept(visitor);
	}

	@Override
	public void accept(OWLObjectVisitor visitor) {
		o.accept(visitor);
	}

	@Override
	public <O> O accept(OWLObjectVisitorEx<O> visitor) {
		return o.accept(visitor);
	}

	@Override
	public int compareTo(OWLObject arg0) {
		return o.compareTo(arg0);
	}

	@Override
	public List<OWLOntologyChange> applyChange(OWLOntologyChange change) throws OWLOntologyChangeException {
		try {
			return ((OWLMutableOntology) o).applyChange(change);
		}
		catch (OWLOntologyChangeException e) {
			throw e;
		}
	}

	@Override
	public List<OWLOntologyChange> applyChanges(List<OWLOntologyChange> changes) throws OWLOntologyChangeException {
		try {
			return ((OWLMutableOntology) o).applyChanges(changes);
		}
		catch (OWLOntologyChangeException e) {
			throw e;
		}
	}

}
