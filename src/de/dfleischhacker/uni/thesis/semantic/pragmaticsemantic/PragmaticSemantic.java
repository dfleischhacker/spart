/*
 *
 * PragmaticSemantic.java
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


package de.dfleischhacker.uni.thesis.semantic.pragmaticsemantic;

import de.dfleischhacker.uni.thesis.semantic.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.mindswap.pellet.owlapi.PelletReasonerFactory;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import de.dfleischhacker.uni.thesis.GlobalSettings;
import de.dfleischhacker.uni.thesis.semantic.closure.ClosureGenerationException;
import de.dfleischhacker.uni.thesis.utils.alignment.Alignment;
import de.dfleischhacker.uni.thesis.utils.alignment.AlignmentManager;
import de.dfleischhacker.uni.thesis.utils.alignment.Correspondence;
import de.dfleischhacker.uni.thesis.utils.ontology.MergingException;
import de.dfleischhacker.uni.thesis.utils.ontology.Ontology;
import de.dfleischhacker.uni.thesis.utils.ontology.OntologyManager;
import de.dfleischhacker.uni.thesis.utils.ontology.OntologyRenamer;
import de.dfleischhacker.uni.thesis.utils.types.TypeMap;
import org.mindswap.pellet.PelletOptions;

/**
 * Implements the pragmatic semantic.
 *
 * The pragmatic semantic supports the following pairs of entities in a
 * correspondence (C - concept, OP - objecttype property, DP - datatype
 * property):
 * <ul>
 *	<li>C - C</li>
 *  <li>any combinations of DP and OP</li>
 * </ul>
 *
 * The supported relations are
 * <ul>
 *	<li>= (equivalence)</li>
 *	<li>&gt; and &lt; (subproperty/-class)</li>
 * </ul>
 * All other correspondences will result in an UnsupportedCorrespondenceException.
 *
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class PragmaticSemantic implements SemanticModule {
	private Ontology onto1;
	private Ontology onto2;
	private TypeMap map;
	private Reasoner reasoner;
	private OntologyRenamer renamer;
	private boolean deleteABox;

	static List<String> supportedRelations;

	/*
	 * Add all relations supported by the NaturalSemantic to the list
	 */
	static {
		supportedRelations = new ArrayList<String>(3);
		supportedRelations.add("=");
		supportedRelations.add(">");
		supportedRelations.add("<");
	}

	/**
	 * Initializes the pragmatic semantic module. Therefore, a merge of the
	 * given two ontologies is prepared by renaming the entities contained
	 * in these ontologies. Be aware of the fact that this renaming is an
	 * INPLACE renaming in the given ontologies and WILL ALTER their content.
	 *
	 * Furthermore, the semantic is configured by this constructor to
	 * remove individuals possibly found in the aligned ontology. This
	 * behaviour can be changed by using the <pre>setDeleteABox</pre>
	 * method.
	 * @param onto1 ontology 1
	 * @param onto2 ontology 2
	 */
	public PragmaticSemantic(Ontology onto1, Ontology onto2) throws MergingException {
		this.onto1 = onto1;
		this.onto2 = onto2;
		this.deleteABox = true;

		renamer = new OntologyRenamer(onto1, onto2);
		map = renamer.getTypeMap();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeleteABox(boolean delete) {
		this.deleteABox = delete;
	}

	/**
	 * Generates a set of axioms from the given correspondence using the OWLDataFactory.
	 * factory should be the data factory of the ontology to which the generated
	 * axiom will be added otherwise errors are likely to occur.
	 *
	 * The pragmatic semantic supports the following pairs of entities in a
	 * correspondence (C - concept, OP - objecttype property, DP - datatype
	 * property):
	 * <ul>
	 *	<li>C - C</li>
	 *  <li>any combinations of DP and OP</li>
	 * </ul>
	 *
	 * The supported relations are
	 * <ul>
	 *	<li>= (equivalence)</li>
	 *	<li>&gt; and &lt; (subproperty/-class)</li>
	 * </ul>
	 * All other correspondences will result in an UnsupportedCorrespondenceException.
	 *
	 * @param corr correspondence to create axiom from
	 * @param ontology ontology to create axiom for
	 * @throws UnsupportedCorrespondenceException thrown on an unsupported
	 *		correspondence
	 * @return created axiom set
	 */
	private Set<OWLAxiom> toAxiom(Correspondence corr, Ontology ontology)
		throws UnsupportedCorrespondenceException {
		String entURI1 = OntologyRenamer.resolveURI(1, corr.getEntity1()).toString();
		String entURI2 = OntologyRenamer.resolveURI(2, corr.getEntity2()).toString();
		String relation = corr.getRelation();

		OWLAxiom axiom = toAxiom(entURI1, relation, entURI2, ontology);

		if (axiom != null) {
			return Collections.singleton(axiom);
		}

		throw new UnsupportedCorrespondenceException(
			"Correspondence from " + map.getType(entURI1) + " to " +
			map.getType(entURI2) + " is unsupported by the pragmatic semantic.\n" +
			"Found in\n" + corr.toString()
		);
	}

	/**
	 * Returns an axiom which represents the relation identified by the given
	 * relation symbol between the given two entities. It does not resolve the
	 * given entity URIs as it assumes those URIs to be already resolved in a
	 * previous step. If the combination of entity types and relation is
	 * unsupported by the pragmatic semantic null will be returned.
	 * @param entURI1 URI of first entity
	 * @param relation relation symbol
	 * @param entURI2 URI of second entity
	 * @param ontology ontology to create axiom for
	 * @return axiom representing the given relation or null on an unsupported
	 *	combination of entities and relation
	 */
	public OWLAxiom toAxiom(String entURI1, String relation, String entURI2,
			Ontology ontology) {
		OWLAxiom axiom = null;
		OWLDataFactory factory = ontology.getManager().getOWLDataFactory();

		// handle C - C correspondences
		if (map.isClass(entURI1) && map.isClass(entURI2)) {
			if (relation.equalsIgnoreCase("=")) {
				axiom = factory.getOWLEquivalentClassesAxiom(
						factory.getOWLClass(
							URI.create(entURI1)
						),
						factory.getOWLClass(
							URI.create(entURI2)
						)
					);
			}
			if (relation.equalsIgnoreCase("<")) {
				axiom = factory.getOWLSubClassAxiom(
						factory.getOWLClass(
							URI.create(entURI1)
						),
						factory.getOWLClass(
							URI.create(entURI2)
						)
					);
			}
			if (relation.equalsIgnoreCase(">")) {
				axiom = factory.getOWLSubClassAxiom(
						factory.getOWLClass(
							URI.create(entURI2)
						),
						factory.getOWLClass(
							URI.create(entURI1)
						)
					);
			}
		}

		// handle OP - OP correspondences
		if (map.isObjectProperty(entURI1) &&
			map.isObjectProperty(entURI2)) {

			if (relation.equalsIgnoreCase("=")) {
				HashSet<OWLObjectProperty> properties = new HashSet(2);
				properties.add(
					factory.getOWLObjectProperty(
						URI.create(entURI1)
					)
				);
				properties.add(
					factory.getOWLObjectProperty(
						URI.create(entURI2)
					)
				);

				axiom = factory.getOWLEquivalentObjectPropertiesAxiom(properties);
			}

			if (relation.equalsIgnoreCase("<")) {
				axiom = factory.getOWLSubObjectPropertyAxiom(
					factory.getOWLObjectProperty(
						URI.create(entURI1)
					),
					factory.getOWLObjectProperty(
						URI.create(entURI2)
					)
				);
			}

			if (relation.equalsIgnoreCase(">")) {
				axiom = factory.getOWLSubObjectPropertyAxiom(
					factory.getOWLObjectProperty(
						URI.create(entURI2)
					),
					factory.getOWLObjectProperty(
						URI.create(entURI1)
					)
				);
			}
		}

		// handle DP - DP correspondences
		if (map.isDataProperty(entURI1) &&
			map.isDataProperty(entURI2)) {

			OWLDataSomeRestriction restriction1 =
				factory.getOWLDataSomeRestriction(
					factory.getOWLDataProperty(URI.create(entURI1)),
					factory.getTopDataType()
			);

			OWLDataSomeRestriction restriction2 =
				factory.getOWLDataSomeRestriction(
					factory.getOWLDataProperty(URI.create(entURI2)),
					factory.getTopDataType()
			);


			if (relation.equalsIgnoreCase("=")) {
				axiom = factory.getOWLEquivalentClassesAxiom(restriction1, restriction2);
			}

			if (relation.equalsIgnoreCase("<")) {
				axiom = factory.getOWLSubClassAxiom(restriction2, restriction1);
			}

			if (relation.equalsIgnoreCase(">")) {
				axiom = factory.getOWLSubClassAxiom(restriction1, restriction2);
			}
		}

		// handle DP - OP
		if (map.isDataProperty(entURI1) && map.isObjectProperty(entURI2)) {
			OWLDataSomeRestriction restriction1 =
				factory.getOWLDataSomeRestriction(
					factory.getOWLDataProperty(URI.create(entURI1)),
					factory.getTopDataType()
			);

			OWLObjectSomeRestriction restriction2 =
				factory.getOWLObjectSomeRestriction(
					factory.getOWLObjectProperty(URI.create(entURI2)),
					factory.getOWLThing()
			);

			if (relation.equalsIgnoreCase("=")) {
				axiom = factory.getOWLEquivalentClassesAxiom(restriction1, restriction2);
			}

			if (relation.equalsIgnoreCase("<")) {
				axiom = factory.getOWLSubClassAxiom(restriction2, restriction1);
			}

			if (relation.equalsIgnoreCase(">")) {
				axiom = factory.getOWLSubClassAxiom(restriction1, restriction2);
			}
		}

		// handle OP - DP
		if (map.isObjectProperty(entURI1) && map.isDataProperty(entURI2)) {

			OWLObjectSomeRestriction restriction1 =
				factory.getOWLObjectSomeRestriction(
					factory.getOWLObjectProperty(URI.create(entURI1)),
					factory.getOWLThing()
			);

			OWLDataSomeRestriction restriction2 =
				factory.getOWLDataSomeRestriction(
					factory.getOWLDataProperty(URI.create(entURI2)),
					factory.getTopDataType()
			);

			if (relation.equalsIgnoreCase("=")) {
				axiom = factory.getOWLEquivalentClassesAxiom(restriction1, restriction2);
			}

			if (relation.equalsIgnoreCase("<")) {
				axiom = factory.getOWLSubClassAxiom(restriction2, restriction1);
			}

			if (relation.equalsIgnoreCase(">")) {
				axiom = factory.getOWLSubClassAxiom(restriction1, restriction2);
			}
		}

		return axiom;
	}

	/**
	 * Returns the closure of the given alignment
	 * @param align alignment to create closure for
	 * @return closure result of given alignment
	 */
	@Override
	public ClosureResult getClosure(Alignment align) throws MergingException, ClosureGenerationException {
		/*
		 * Merge the ontologies given the alignment and semantic.
		 */
		Ontology alignedOntology;
		try {
			alignedOntology = OntologyManager.createOntology(URI.create(GlobalSettings.ALIGNED_NAMESPACE));
		} catch (OWLOntologyCreationException ex) {
			throw new MergingException(ex.getMessage());
		}

		// add axioms from ontology 1 to the aligned ontology
		for (OWLAxiom axiom : onto1.getAxioms()) {
			try {
				alignedOntology.getManager().addAxiom(alignedOntology, axiom);
			} catch (OWLOntologyChangeException ex) {
				throw new MergingException(ex.getMessage());
			}
		}

		// copy axioms from onto2 into aligned ontology
		for (OWLAxiom axiom : onto2.getAxioms()) {
			try {
				alignedOntology.getManager().addAxiom(alignedOntology, axiom);
			} catch (OWLOntologyChangeException ex) {
				throw new MergingException(ex.getMessage());
			}
		}

		// add all axioms generated by the translation of correspondences
		for (Correspondence corr : align.getCorrespondences()) {
			try {
				Set<OWLAxiom> axiomset = this.toAxiom(corr, alignedOntology);
				for (OWLAxiom axiom : axiomset) {
					alignedOntology.getManager().addAxiom(alignedOntology, axiom);
				}
			} catch (OWLOntologyChangeException ex) {
				throw new MergingException(ex.getMessage());
			}
			catch (UnsupportedCorrespondenceException ex) {
				throw new MergingException(ex.getMessage());
			}
		}

		// remove the following line if the Pellet bug has been fixed
		PelletOptions.USE_CD_CLASSIFICATION = false;
		
		PelletReasonerFactory factory = new PelletReasonerFactory();

		if (deleteABox) {
			try {
				alignedOntology.deleteABox();
			} catch (Exception ex) {
				System.out.println("Unable to delete ABox");
			}
		}

		reasoner = factory.createReasoner(alignedOntology.getManager());
		Set<OWLOntology> importsClosure = alignedOntology.getManager().getImportsClosure(alignedOntology);
		reasoner.loadOntologies(importsClosure);
		System.out.println("Starting classification");
		reasoner.classify();
		System.out.println("Finished classification");
		if (!reasoner.isConsistent()) {
			System.out.println("Expl: " + reasoner.getExplanation());
			throw new InconsistentOntologyException("Aligned ontology is inconsistent");
		}

		Alignment closure = AlignmentManager.getEmptyAlignment();
		closure.setOntology1(onto1.getURI().toString());
		closure.setOntology2(onto2.getURI().toString());

		Set<OWLEntity> entities1 = onto1.getReferencedEntities();
		Set<OWLEntity> entities2 = onto2.getReferencedEntities();

		/*
		 * iterate over all possible entity-relation-entity combinations and
		 * check if they are entailed by the aligned ontology
		 */
		for (OWLEntity ent1 : entities1) {
			for (OWLEntity ent2 : entities2) {
				for (String relation : supportedRelations) {
					OWLAxiom axiom = this.toAxiom(
						ent1.getURI().toString(),
						relation,
						ent2.getURI().toString(),
						alignedOntology
					);

					// the combination of entities and relation is unsupported
					if (axiom == null) {
						continue;
					}

					boolean entailed = false;
					entailed = reasoner.isEntailed(axiom);

					if (entailed) {
						Correspondence corr = new Correspondence(
								ent1.getURI().toString(),
								ent2.getURI().toString(),
								relation
						);
						closure.addCorrespondence(corr);
					}
				}
			}
		}

		ClosureResult res = new ClosureResult(closure);
		res.setAlignedOntology(alignedOntology);
		return res;
	}

}
