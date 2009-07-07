/*
 *
 * Calculator.java
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
package de.dfleischhacker.uni.thesis.calculator;

import java.util.HashSet;
import java.util.Set;
import de.dfleischhacker.uni.thesis.utils.alignment.Alignment;
import de.dfleischhacker.uni.thesis.utils.alignment.AlignmentManager;
import de.dfleischhacker.uni.thesis.utils.alignment.Correspondence;

/**
 * This class provides methods to calculate the values of precision and recall
 * for an alignment given a reference alignment.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class Calculator {
	private Alignment alignment;
	private Alignment reference;

	private Set<Correspondence> intersection;

	/**
	 * Initializes the Calculator class with the given alignments.
	 * @param alignment alignment to evaluate
	 * @param reference alignment to use as reference
	 */
	public Calculator(Alignment alignment, Alignment reference) {
		this.alignment = alignment;
		this.reference = reference;

		System.out.println("Size reference alignment: " + reference.getCorrespondences().size());
		System.out.println("Size evaluation alignment: " + alignment.getCorrespondences().size());

		// we need to create a new HashSet to prevent the modification of the original alignment
		intersection = new HashSet<Correspondence>(alignment.getCorrespondences());
		intersection.retainAll(reference.getCorrespondences());

		System.out.println("Calculator statistics:");
		
		System.out.println("Intersection size: " + intersection.size());
	}

	/**
	 * Returns the value for the precision of the alignment with regard to the
	 * reference alignment given at creation of this calculator class.
	 * @return precision value
	 */
	public float getPrecision() {
		return ((float)intersection.size())/this.alignment.getCorrespondences().size();
	}

	/**
	 * Returns the value for the recall of the alignment with regard to the
	 * reference alignment given at creation of this calculator class
	 * @return recall value
	 */
	public float getRecall() {
		return ((float)intersection.size())/this.reference.getCorrespondences().size();
	}

	/**
	 * Returns the intersection calculated for computing precision and recall as
	 * an alignment.
	 * @return calculated intersection of alignment and reference alignment
	 */
	public Alignment getIntersection() {
		return AlignmentManager.toAlignment(alignment.getOntology1(),
				alignment.getOntology2(), intersection);
	}
}
