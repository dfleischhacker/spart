/*
 *
 * CalculationResult.java
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

import de.dfleischhacker.uni.thesis.utils.alignment.Alignment;
import de.dfleischhacker.uni.thesis.utils.ontology.Ontology;

/**
 * Represents the results got by calculating precision and recall
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class CalculationResult {
	private float precision;
	private float recall;
	private String nameOnto1;
	private int entCountOnto1;
	private String nameOnto2;
	private int entCountOnto2;
	private Ontology combinedEvOntology;
	private Ontology combinedRefOntology;
	private Alignment evaluationClosure;
	private Alignment referenceClosure;
	private Alignment intersection;
	private Alignment originalAlignment;
	private Alignment originalReference;
	private String nameEvAlign;
	private String nameRefAlign;

	/**
	 * @return the precision
	 */
	public float getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(float precision) {
		this.precision = precision;
	}

	/**
	 * @return the recall
	 */
	public float getRecall() {
		return recall;
	}

	/**
	 * @param recall the recall to set
	 */
	public void setRecall(float recall) {
		this.recall = recall;
	}

	/**
	 * @return the nameOnto1
	 */
	public String getNameOnto1() {
		return nameOnto1;
	}

	/**
	 * @param nameOnto1 the nameOnto1 to set
	 */
	public void setNameOnto1(String nameOnto1) {
		this.nameOnto1 = nameOnto1;
	}

	/**
	 * @return the entCountOnto1
	 */
	public int getEntCountOnto1() {
		return entCountOnto1;
	}

	/**
	 * @param entCountOnto1 the entCountOnto1 to set
	 */
	public void setEntCountOnto1(int entCountOnto1) {
		this.entCountOnto1 = entCountOnto1;
	}

	/**
	 * @return the nameOnto2
	 */
	public String getNameOnto2() {
		return nameOnto2;
	}

	/**
	 * @param nameOnto2 the nameOnto2 to set
	 */
	public void setNameOnto2(String nameOnto2) {
		this.nameOnto2 = nameOnto2;
	}

	/**
	 * @return the entCountOnto2
	 */
	public int getEntCountOnto2() {
		return entCountOnto2;
	}

	/**
	 * @param entCountOnto2 the entCountOnto2 to set
	 */
	public void setEntCountOnto2(int entCountOnto2) {
		this.entCountOnto2 = entCountOnto2;
	}

	/**
	 * @return the combinedOntology
	 */
	public Ontology getCombinedEvOntology() {
		return combinedEvOntology;
	}

	/**
	 * @param combinedOntology the combinedOntology to set
	 */
	public void setCombinedEvOntology(Ontology combinedOntology) {
		this.combinedEvOntology = combinedOntology;
	}

	/**
	 * @return the evaluationClosure
	 */
	public Alignment getEvaluationClosure() {
		return evaluationClosure;
	}

	/**
	 * @param evaluationClosure the evaluationClosure to set
	 */
	public void setEvaluationClosure(Alignment evaluationClosure) {
		this.evaluationClosure = evaluationClosure;
	}

	/**
	 * @return the referenceClosure
	 */
	public Alignment getReferenceClosure() {
		return referenceClosure;
	}

	/**
	 * @param referenceClosure the referenceClosure to set
	 */
	public void setReferenceClosure(Alignment referenceClosure) {
		this.referenceClosure = referenceClosure;
	}

	/**
	 * Returns the intersection calculated for the precision and recall value
	 * which is either the intersection of reference alignment and alignment
	 * in case of classical precision and recall or other wise the closure
	 * intersection
	 * @return the relevant intersection
	 */
	public Alignment getIntersection() {
		return intersection;
	}

	/**
	 * @param intersection the intersection to set
	 */
	public void setIntersection(Alignment intersection) {
		this.intersection = intersection;
	}

	/**
	 * @return the nameEvAlign
	 */
	public String getNameEvAlign() {
		return nameEvAlign;
	}

	/**
	 * @param nameEvAlign the nameEvAlign to set
	 */
	public void setNameEvAlign(String nameEvAlign) {
		this.nameEvAlign = nameEvAlign;
	}

	/**
	 * @return the nameRefAlign
	 */
	public String getNameRefAlign() {
		return nameRefAlign;
	}

	/**
	 * @param nameRefAlign the nameRefAlign to set
	 */
	public void setNameRefAlign(String nameRefAlign) {
		this.nameRefAlign = nameRefAlign;
	}

	/**
	 * @return the combinedRefOntology
	 */
	public Ontology getCombinedRefOntology() {
		return combinedRefOntology;
	}

	/**
	 * @param combinedRefOntology the combinedRefOntology to set
	 */
	public void setCombinedRefOntology(Ontology combinedRefOntology) {
		this.combinedRefOntology = combinedRefOntology;
	}

	/**
	 * @return the originalAlignment
	 */
	public Alignment getOriginalAlignment() {
		return originalAlignment;
	}

	/**
	 * @param originalAlignment the originalAlignment to set
	 */
	public void setOriginalAlignment(Alignment originalAlignment) {
		this.originalAlignment = originalAlignment;
	}

	/**
	 * @return the originalReference
	 */
	public Alignment getOriginalReference() {
		return originalReference;
	}

	/**
	 * @param originalReference the originalReference to set
	 */
	public void setOriginalReference(Alignment originalReference) {
		this.originalReference = originalReference;
	}
}
