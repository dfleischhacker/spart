/*
 * 
 * NullSemantic.java
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
import de.dfleischhacker.uni.thesis.utils.alignment.Alignment;

/**
 * Implements the null semantic. This semantic is used to do no reasoning or
 * altering of the alignment at all but return every alignment passed into the
 * semantic as is. Thus, it is possible to compute the classical precision and
 * recall measures with our architecture designed for semantics.
 *
 * As an implication of this design this method does not pose any restrictions
 * on the correspondences contained in the alignments.
 * @author Daniel Fleischhacker
 */
public class NullSemantic implements SemanticModule {
	/**
	 * Initializes the NullSemantic object. Different from the other classes
	 * there are no arguments required
	 */
	public NullSemantic() {
	}

	/**
	 * Returns the given alignment (packed in a ClosureResult) as is which
	 * enables us to compute the classical precision and recall measures.
	 * @param align alignment to return
	 * @return unchanged alignment contained in ClosureResult
	 */
	@Override
	public ClosureResult getClosure(Alignment align) {
		ClosureResult res = new ClosureResult(align);
		return res;
	}

	/**
	 * Does nothing for the null semantic
	 * @param delete unused
	 */
	@Override
	public void setDeleteABox(boolean delete) {
	}
}
