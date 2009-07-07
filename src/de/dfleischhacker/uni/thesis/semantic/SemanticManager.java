/*
 *
 * SemanticManager.java
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

import de.dfleischhacker.uni.thesis.semantic.pragmaticsemantic.PragmaticSemanticFactory;
import de.dfleischhacker.uni.thesis.semantic.nullsemantic.NullSemanticFactory;
import de.dfleischhacker.uni.thesis.semantic.naturalsemantic.NaturalSemanticFactory;
import java.util.ArrayList;
import java.util.HashMap;
import de.dfleischhacker.uni.thesis.utils.ontology.Ontology;

/**
 * Provides methods to get semantic modules and contains a list of available
 * modules as well as factory methods to create those semantic modules.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class SemanticManager {
	private static HashMap<String,SemanticFactory> moduleMap;

	/*
	 * List of available modules. New modules should be registered here
	 */
	static {
		moduleMap = new HashMap<String, SemanticFactory>();
		moduleMap.put("Null Semantic", new NullSemanticFactory());
		moduleMap.put("Natural Semantic", new NaturalSemanticFactory());
		moduleMap.put("Pragmatic Semantic", new PragmaticSemanticFactory());
	}

	/**
	 * Private constructor to prevent instantiation
	 */
	private SemanticManager() {
	}

	/**
	 * Returns a list containing the names of all registered semantic modules
	 * as strings
	 * @return names of the available semantic modules
	 */
	public static ArrayList<String> getAvailableModules() {
		ArrayList<String> res = new ArrayList(moduleMap.size());

		for (String name : moduleMap.keySet()) {
			res.add(name);
		}

		return res;
	}

	/**
	 * Checks if a module with the given name is available.
	 * @param name name of module whose availability to check
	 * @return true if a module with the given name is available, otherwise false
	 */
	public static boolean isModuleAvailable(String name) {
		return moduleMap.containsKey(name);
	}

	/**
	 * Returns an instance of the SemanticModule class which is determined
	 * by the given name.
	 * @param name name of the semantic module to instantiate
	 * @param onto1 first ontology
	 * @param onto2 second ontology
	 * @param deleteABox if true ABox of merged ontology is deleted
	 * @return instance of the semantic module class or null if the given name
	 *	does not identify such a class
	 */
	public static SemanticModule getModuleInstance(String name, Ontology onto1,
			Ontology onto2, boolean deleteABox) {
		if (moduleMap.containsKey(name)) {
			try {
				SemanticModule mod = moduleMap.get(name).getInstance(onto1, onto2);
				mod.setDeleteABox(deleteABox);
				return mod;
			} catch (Exception ex) {
				System.out.println("Error getting module: " + ex.getMessage());
				return null;
			}
		}
		System.out.println("No module " + name + " found");
		return null;
	}
}
