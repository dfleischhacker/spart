/*
 * 
 * OntologyManager.java
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


package de.dfleischhacker.uni.thesis.utils.ontology;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;

import java.io.File;
import java.net.URI;

/**
 * This class provides methods to generate ontologies. It is possible to generate
 * ontologies from a given OWL file. Furthermore, the OntologyManager is able
 * to write ontologies to files.
 *
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class OntologyManager {
	/**
	 * Loads the given file as ontology. The filename has to be absolute. If no
	 * schema (schema://) is given in the URI it is assumed that the filename
	 * identifies a local file and the file:// schema will be added. It is assumed
	 * that the schema is missing if there is no "://" in filename.
	 * 
	 * @param filename absolute URI of ontology as string
	 * @return ontology loaded from URI
	 * @throws org.semanticweb.owl.model.OWLOntologyCreationException
	 */
	public static Ontology loadOntology(String filename)
		throws OWLOntologyCreationException {

		if (!filename.contains("://")) {
			filename = String.valueOf(new File(filename).toURI());
		}

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		return new Ontology(
				manager.loadOntologyFromPhysicalURI(URI.create(filename)),
				manager
		);
	}

	/**
	 * Loads the file from the given physical URI as ontology.
	 *
	 * @param uri physical URI of ontology
	 * @return ontology loaded from URI
	 * @throws org.semanticweb.owl.model.OWLOntologyCreationException
	 */
	public static Ontology loadOntology(URI uri)
		throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		return new Ontology(
				manager.loadOntologyFromPhysicalURI(uri),
				manager
		);
	}

	/**
	 * Writes the given ontology into a file. The RDF/XML format is used to
	 * represent this ontology
	 * @param onto ontology to write to file
	 * @param filename filname to use
	 */
	public static void writeOntology(Ontology onto, String filename)
			throws UnknownOWLOntologyException, OWLOntologyStorageException {
		onto.getManager().saveOntology(onto, new RDFXMLOntologyFormat(),
				URI.create(new File(filename).toURI().toString()));
	}

	/**
	 * Creates an Ontology object and adds the OWLOntologyManager used to
	 * do this into the wrapper class.
	 * @param uri URI to identify the ontology
	 */
	public static Ontology createOntology(URI uri) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		return new Ontology(
				manager.createOntology(uri),
				manager
		);
	}
}
