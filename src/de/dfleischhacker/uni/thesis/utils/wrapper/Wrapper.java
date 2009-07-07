/*
 *
 * Wrapper.java
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

package de.dfleischhacker.uni.thesis.utils.wrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.xml.sax.SAXException;
import de.dfleischhacker.uni.thesis.calculator.Calculator;
import de.dfleischhacker.uni.thesis.semantic.ClosureResult;
import de.dfleischhacker.uni.thesis.semantic.SemanticManager;
import de.dfleischhacker.uni.thesis.semantic.SemanticModule;
import de.dfleischhacker.uni.thesis.semantic.closure.ClosureGenerationException;
import de.dfleischhacker.uni.thesis.utils.alignment.Alignment;
import de.dfleischhacker.uni.thesis.utils.alignment.AlignmentFormatException;
import de.dfleischhacker.uni.thesis.utils.alignment.AlignmentManager;
import de.dfleischhacker.uni.thesis.utils.alignment.InvalidAlignmentException;
import de.dfleischhacker.uni.thesis.utils.ontology.MergingException;
import de.dfleischhacker.uni.thesis.utils.ontology.Ontology;
import de.dfleischhacker.uni.thesis.utils.ontology.OntologyManager;

/**
 * Wraps the step necessary to calculate precision and recall and provides
 * methods to check the validity of input files.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class Wrapper {
	/**
	 * Checks if it is possible to load the ontology from the given filename
	 * @param filename name of ontology file
	 */
	public static void checkOntology1(String filename) throws WrapperException {
		try {
			OntologyManager.loadOntology(filename);
		} catch (OWLOntologyCreationException ex) {
			throw new WrapperException("Error loading ontology: " + ex.getMessage());
		}
	}

	/**
	 * Checks if it is possible to load the ontology from the given URI
	 * @param uri URI of ontology file
	 */
	public static void checkOntology1(URI uri) throws WrapperException {
		try {
			OntologyManager.loadOntology(uri);
		} catch (OWLOntologyCreationException ex) {
			throw new WrapperException("Error loading ontology: " + ex.getMessage());
		}
	}

	/**
	 * Checks if it is possible to load the ontology from the given filename
	 * @param filename name of ontology file
	 */
	public static void checkOntology2(String filename) throws WrapperException {
		try {
			OntologyManager.loadOntology(filename);
		} catch (OWLOntologyCreationException ex) {
			throw new WrapperException("Error loading ontology: " + ex.getMessage());
		}
	}

	/**
	 * Checks if it is possible to load the ontology from the given URI
	 * @param uri URI of ontology file
	 */
	public static void checkOntology2(URI uri) throws WrapperException {
		try {
			OntologyManager.loadOntology(uri);
		} catch (OWLOntologyCreationException ex) {
			throw new WrapperException("Error loading ontology: " + ex.getMessage());
		}
	}

	/**
	 * Loads the evaluation alignment from the given file
	 * @param filename alignment's filename
	 */
	public static void checkEvaluationAlignment(String filename) throws WrapperException {
		try {
			AlignmentManager.loadAlignment(filename);
		} catch (FileNotFoundException ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		} catch (SAXException ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		} catch (IOException ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		} catch (ParserConfigurationException ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		} catch (Exception ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		}
	}

	/**
	 * Loads the reference alignment from the given file
	 * @param filename alignment's filename
	 */
	public static void checkReferenceAlignment(String filename) throws WrapperException {
		try {
			AlignmentManager.loadAlignment(filename);
		} catch (FileNotFoundException ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		} catch (SAXException ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		} catch (IOException ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		} catch (ParserConfigurationException ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		} catch (Exception ex) {
			throw new WrapperException("Error loading alignment: " + ex.getMessage());
		}
	}

	/**
	 * Checks if the given string is a valid semantic identifier
	 * @param semantic string identifying the semantic
	 */
	public static void checkSemantic(String semantic) throws WrapperException {
		if (!SemanticManager.isModuleAvailable(semantic))
			throw new WrapperException("'" + semantic + "' is not a valid semantic" );
	}

	/**
	 * Calculates recall and precision and returns an CalculationResult object
	 */
	public static CalculationResult calculate(URI uriOnto1, URI uriOnto2, String nameEvAlign,
			String nameRefAlign, String nameSemantic) throws WrapperException {
		return calculate(uriOnto1, uriOnto2, nameEvAlign, nameRefAlign, nameSemantic, 0, true);
	}

	/**
	 * Calculates recall and precision and returns an CalculationResult object
	 */
	public static CalculationResult calculate(URI uriOnto1, URI uriOnto2, String nameEvAlign,
			String nameRefAlign, String nameSemantic, boolean deleteABox) throws WrapperException {
		return calculate(uriOnto1, uriOnto2, nameEvAlign, nameRefAlign, nameSemantic, 0, deleteABox);
	}

	/**
	 * Calculates recall and precision and returns an CalculationResult object
	 */
	public static CalculationResult calculate(URI uriOnto1, URI uriOnto2, String nameEvAlign,
			String nameRefAlign, String nameSemantic, float threshold) throws WrapperException {
		return calculate(uriOnto1, uriOnto2, nameEvAlign, nameRefAlign, nameSemantic, threshold, true);
	}

	/**
	 * Calculates recall and precision and returns an CalculationResult object
	 */
	public static CalculationResult calculate(URI uriOnto1, URI uriOnto2, String nameEvAlign,
			String nameRefAlign, String nameSemantic, float threshold, boolean deleteABox) throws WrapperException {
		CalculationResult res = new CalculationResult();

		/*
		 * Load needed files into corresponding objects
		 */
		res.setNameOnto1(uriOnto1.toString());
		Ontology onto1 = null;
		try {
			onto1 = OntologyManager.loadOntology(uriOnto1);
		} catch (OWLOntologyCreationException ex) {
			throw new WrapperException("Error loading ontology 1: " + ex.getMessage());
		}
		res.setEntCountOnto1(onto1.getReferencedEntities().size());

		res.setNameOnto2(uriOnto2.toString());
		Ontology onto2 = null;
		try {
			onto2 = OntologyManager.loadOntology(uriOnto2);
		} catch (OWLOntologyCreationException ex) {
			throw new WrapperException("Error loading ontology 2: " + ex.getMessage());
		}
		res.setEntCountOnto2(onto2.getReferencedEntities().size());

		res.setNameEvAlign(nameEvAlign);
		Alignment evAlign = null;
		try {
			evAlign = AlignmentManager.loadAlignment(nameEvAlign, threshold);
		} catch (FileNotFoundException ex) {
			throw new WrapperException("Error loading evaluation alignment: " + ex.getMessage());
		} catch (SAXException ex) {
			throw new WrapperException("Error loading evaluation alignment: " + ex.getMessage());
		} catch (IOException ex) {
			throw new WrapperException("Error loading evaluation alignment: " + ex.getMessage());
		} catch (ParserConfigurationException ex) {
			throw new WrapperException("Error loading evaluation alignment: " + ex.getMessage());
		} catch (AlignmentFormatException ex) {
			throw new WrapperException("Error loading evaluation alignment: " + ex.getMessage() + " " + ex.getClass().getName());
		}
		catch (Exception ex) {
			throw new WrapperException("Error loading evaluation alignment: " + ex.getMessage() + " " + ex.getClass().getName());
		}

		res.setNameRefAlign(nameRefAlign);
		Alignment refAlign = null;
		try {
			refAlign = AlignmentManager.loadAlignment(nameRefAlign);
		} catch (FileNotFoundException ex) {
			throw new WrapperException("Error loading reference alignment: " + ex.getMessage());
		} catch (SAXException ex) {
			throw new WrapperException("Error loading reference alignment: " + ex.getMessage());
		} catch (IOException ex) {
			throw new WrapperException("Error loading reference alignment: " + ex.getMessage());
		} catch (ParserConfigurationException ex) {
			throw new WrapperException("Error loading reference alignment: " + ex.getMessage());
		} catch (AlignmentFormatException ex) {
			throw new WrapperException("Error loading reference alignment: " + ex.getMessage());
		}

		// validate alignments against ontologies
		try {
			refAlign.validate(onto1, onto2);
		} catch (InvalidAlignmentException ex) {
			throw new WrapperException("Invalid reference alignment: " + ex.getMessage());
		}

		try {
			evAlign.validate(onto1, onto2);
		} catch (InvalidAlignmentException ex) {
			throw new WrapperException("Invalid evaluation alignment: " + ex.getMessage());
		}

		SemanticModule sem = SemanticManager.getModuleInstance(nameSemantic, onto1, onto2,
				deleteABox);

		if (sem == null) {
			System.out.println("Semantic == null!");
		}

		ClosureResult evaluationClosureRes = null;
		try {
			evaluationClosureRes = sem.getClosure(evAlign);
		} catch (MergingException ex) {
			throw new WrapperException("Error merging ontologies: " + ex.getMessage());
		} catch (ClosureGenerationException ex) {
			throw new WrapperException("Error generating alignment closure: " + ex.getMessage());
		}
		res.setCombinedEvOntology(evaluationClosureRes.getAlignedOntology());
		res.setEvaluationClosure(evaluationClosureRes.getClosure());

		ClosureResult referenceClosureRes = null;
		try {
			referenceClosureRes = sem.getClosure(refAlign);
		} catch (MergingException ex) {
			throw new WrapperException("Error merging ontologies: " + ex.getMessage());
		} catch (ClosureGenerationException ex) {
			throw new WrapperException("Error generating reference alignment closure: " + ex.getMessage());
		}
		res.setCombinedRefOntology(referenceClosureRes.getAlignedOntology());
		res.setReferenceClosure(referenceClosureRes.getClosure());


		Calculator calc = new Calculator(
				evaluationClosureRes.getClosure(),
				referenceClosureRes.getClosure()
		);

		res.setPrecision(calc.getPrecision());
		res.setRecall(calc.getRecall());
		res.setIntersection(calc.getIntersection());

		// save original alignments into result
		try {
			res.setOriginalAlignment(AlignmentManager.loadAlignment(nameEvAlign));
		} catch (Exception e) {
			System.out.println("ERROR saving alignment");
		}
		try {
			res.setOriginalReference(AlignmentManager.loadAlignment(nameRefAlign));
		} catch (Exception e) {
			System.out.println("ERROR saving reference alignment");
		}

		return res;
	}
}
