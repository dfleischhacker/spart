/*
 *
 * GlobalSettings.java
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

package de.dfleischhacker.uni.thesis;

/**
 * Class which contains most (should be all, but who knows) constants used
 * throughout the code.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class GlobalSettings {
	/**
	 * If set to true there will be debug outputs to console and some
	 * data created during a run will be saved iunto files.
	 */
	public static boolean DEBUG = true;

	/**
	 * URI prefix used to make ontology URIs unique
	 */
	public static final String ONTO_PREFIX ="http://thesis.dfleischhacker.de/onto";

	/**
	 * Namespace to use for aligned ontology
	 */
	public static final String ALIGNED_NAMESPACE = "http://thesis.dfleischhacker.de/thesis/aligned#";

	/**
	 * Prefix to use for transcreibed nominals
	 */
	public static String NOMINAL_PREFIX = "http://thesis.dfleischhacker.de/nominal";

	/**
	 * General prefix to be used in cases not matches by the previous prefixes
	 */
	public static String GENERAL_PREFIX = "http://thesis.dfleischhacker.de/general";
}
