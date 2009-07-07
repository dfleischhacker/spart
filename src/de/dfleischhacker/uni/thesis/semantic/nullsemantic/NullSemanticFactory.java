/*
 * 
 * NullSemanticFactory.java
 * 
 * Software License Agreement (BSD License)
 * 
 * Copyright (c) 2009 Daniel Fleischhacker
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

package de.dfleischhacker.uni.thesis.semantic.nullsemantic;

import de.dfleischhacker.uni.thesis.semantic.*;
import de.dfleischhacker.uni.thesis.utils.ontology.MergingException;
import de.dfleischhacker.uni.thesis.utils.ontology.Ontology;

/**
 * The factory implementation for the NullSemantic class. Solely used
 * from the SemanticManager to create instances of NullSemantic.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class NullSemanticFactory implements SemanticFactory {
	/**
	 * Initializes the NullSemanticFactory object
	 */
	public NullSemanticFactory() {
	}

	/**
	 * Creates an instance of the NullSemantic class
	 * @return the new NullSemantic instance
	 */
	@Override
	public SemanticModule getInstance(Ontology onto1, Ontology onto2)
			throws MergingException{
		return new NullSemantic();
	}
}
