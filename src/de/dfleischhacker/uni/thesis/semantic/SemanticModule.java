/*
 * 
 * SemanticModule.java
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


package de.dfleischhacker.uni.thesis.semantic;

import de.dfleischhacker.uni.thesis.semantic.closure.ClosureGenerationException;
import de.dfleischhacker.uni.thesis.utils.alignment.Alignment;
import de.dfleischhacker.uni.thesis.utils.ontology.MergingException;

/**
 * This defines the interface for modules implementing new semantics.
 * It is actually made for supporting reductionistic semantics but
 * the design is general enough so that it should be possible to implement
 * some non-reductionistic semantics.
 * For some examples how to implement semantics see the NullSemantic (not really a
 * semantic), NaturalSemantic and PragmaticSemantic classes (these are
 * reductionistic semantics).
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public interface SemanticModule {
	/**
	 * This methods returns the closure result of the given alignment. The
	 * internal implementation is pretty much unrestricted but there are some
	 * classes which use simplify the development of new semantic modules.
	 * The OntologyRenamer class is the most notable of these helper classes.
	 * @param align alignment to generate the closure for
	 * @return closure result for the given alignment under the ontologies and semantic
	 *	of this semantic module
	 */
	public ClosureResult getClosure(Alignment align)
			throws MergingException, ClosureGenerationException;

	/**
	 * Used to set if the semantic should pay attention to individuals in the
	 * ontologies or has to ignore them.
	 * @param delete if true the semantic ignores individuals in the ontology,
	 *	if false it pays attention to the ontology's individuals
	 */
	public void setDeleteABox(boolean delete);
}
