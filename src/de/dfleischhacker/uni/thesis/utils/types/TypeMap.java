/*
 *
 * TypeMap.java
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

package de.dfleischhacker.uni.thesis.utils.types;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLObjectProperty;
import de.dfleischhacker.uni.thesis.utils.ontology.Ontology;

/**
 * This class builds a map with entity types for the entities contained in the
 * given ontologies and provides an interface to receive these contents.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class TypeMap {
	private Ontology onto1;
	private Ontology onto2;
	private HashMap<String,OWLClass> classMap1;
	private HashMap<String,OWLDataProperty> dataPropertyMap1;
	private HashMap<String,OWLObjectProperty> objectPropertyMap1;

	private HashMap<String,OWLClass> classMap2;
	private HashMap<String,OWLDataProperty> dataPropertyMap2;
	private HashMap<String,OWLObjectProperty> objectPropertyMap2;

	/**
	 * Initializes the internal entity type maps from the given ontologies.
	 * @param onto1 ontology 1
	 * @param onto2 ontology 2
	 */
	public TypeMap(Ontology onto1, Ontology onto2) {
		this.onto1 = onto1;
		this.onto2 = onto2;

		// build entity lists for ontology 1
		classMap1 = new HashMap<String, OWLClass>();
		dataPropertyMap1 = new HashMap<String,OWLDataProperty>();
		objectPropertyMap1 = new HashMap<String,OWLObjectProperty>();

		for (OWLClass clazz : onto1.getReferencedClasses()) {
			classMap1.put(
				clazz.getURI().toString(),
				clazz
			);
		}

		for (OWLDataProperty prop : onto1.getReferencedDataProperties()) {
			dataPropertyMap1.put(
				prop.getURI().toString(),
				prop
			);
		}

		for (OWLObjectProperty prop : onto1.getReferencedObjectProperties()) {
			objectPropertyMap1.put(
				prop.getURI().toString(),
				prop
			);
		}

		// build ontology lists for ontology 2
		classMap2 = new HashMap<String, OWLClass>();
		dataPropertyMap2 = new HashMap<String,OWLDataProperty>();
		objectPropertyMap2 = new HashMap<String,OWLObjectProperty>();

		for (OWLClass clazz : onto2.getReferencedClasses()) {
			classMap2.put(
				clazz.getURI().toString(),
				clazz
			);
		}

		for (OWLDataProperty prop : onto2.getReferencedDataProperties()) {
			dataPropertyMap2.put(
				prop.getURI().toString(),
				prop
			);
		}

		for (OWLObjectProperty prop : onto2.getReferencedObjectProperties()) {
			objectPropertyMap2.put(
				prop.getURI().toString(),
				prop
			);
		}
	}

	/**
	 * Checks whether the given entity is a class.
	 * @param entity entity to check
	 * @return true if entity is class, false otherwise
	 */
	public boolean isClass(String entity) {
		return classMap1.containsKey(entity)
				|| classMap2.containsKey(entity);
	}

	/**
	 * Checks whether the given entity is a data property.
	 * @param entity entity to check
	 * @return true if entity is data property, false otherwise
	 */
	public boolean isDataProperty(String entity) {
		return dataPropertyMap1.containsKey(entity)
				|| dataPropertyMap2.containsKey(entity);
	}

	/**
	 * Checks whether the given entity is an object property.
	 * @param entity entity to check
	 * @return true if entity is object property, false otherwise
	 */
	public boolean isObjectProperty(String entity) {
		return objectPropertyMap1.containsKey(entity)
				|| objectPropertyMap2.containsKey(entity);
	}

	/**
	 * Returns the type of the given entity. This method is used for debugging
	 * purposes.
	 * @param uri entity's URI
	 * @return type as string
	 */
	public String getType(String uri) {
		String uriString = uri.toString();

		if (classMap1.containsKey(uriString) || classMap2.containsKey(uriString))
			return "class";

		if (dataPropertyMap1.containsKey(uriString) || dataPropertyMap2.containsKey(uriString))
			return "dataproperty";

		if (objectPropertyMap1.containsKey(uriString) || objectPropertyMap2.containsKey(uriString))
			return "objectproperty";

		return "unknown type";
	}

	/**
	 * Dumps the generated typemap to the given file
	 * @param filename filename to dump typemap to
	 */
	 public void dump(String filename) throws IOException {
		 PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

		 for (String cls : classMap1.keySet()) {
			 writer.println("Class: " + cls);
		 }
		 for (String cls : classMap2.keySet()) {
			 writer.println("Class: " + cls);
		 }
		 for (String cls : dataPropertyMap1.keySet()) {
			 writer.println("Data property: " + cls);
		 }
		 for (String cls : dataPropertyMap2.keySet()) {
			 writer.println("Data property: " + cls);
		 }
		 for (String cls : objectPropertyMap1.keySet()) {
			 writer.println("Object property: " + cls);
		 }
		 for (String cls : objectPropertyMap2.keySet()) {
			 writer.println("Object property: " + cls);
		 }

		 writer.close();
	 }
}
