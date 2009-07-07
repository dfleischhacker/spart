/*
 * 
 * Alignment.java
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


package de.dfleischhacker.uni.thesis.utils.alignment;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import de.dfleischhacker.uni.thesis.utils.alignment.InvalidAlignmentException;

/**
 * Represents an alignment.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class Alignment {
	private Set<Correspondence> correspondences;
	private String onto1;
	private String onto2;

	// some more data which is not used in this class
	private String level = "0";
	private String type = "**";

	/**
	 * Initializes an empty alignment.
	 */
	Alignment() {
		correspondences = new HashSet<Correspondence>();
	}

	/**
	 * Initializes the alignment using the given values
	 * @param onto1 uri of ontology 1
	 * @param onto2 uri of ontology 2
	 * @param corrSet set of correspondences
	 */
	Alignment(String onto1, String onto2, Set<Correspondence> corrSet) {
		this.onto1 = onto1;
		this.onto2 = onto2;
		this.correspondences = corrSet;
	}

	/**
	 * Adds a correspondence
	 * @param correspondence correspondence to add
	 */
	public void addCorrespondence(Correspondence correspondence) {
		correspondences.add(correspondence);
	}

	/**
	 * Returns this alignment's correspondences as ArrayList
	 * @return alignment's correspondences as ArrayList
	 */
	public Set<Correspondence> getCorrespondences() {
		return correspondences;
	}

	/**
	 * Returns the number of correspondences in this alignment
	 * @return number of correspondences in this alignment
	 */
	public int getCorrespondenceCount() {
		return correspondences.size();
	}

	/**
	 * Returns the alignment's string representation. This method is
	 * used mainly for debugging purposes.
	 * @return the alignment's string representation
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Ontology 1: ");
		builder.append(onto1);
		builder.append("\nOntology 2: ");
		builder.append(onto2);
		builder.append("\n");

		for (Correspondence corr : correspondences) {
			builder.append(corr.toString());
			builder.append("\n");
		}

		return builder.toString();
	}

	/**
	 * Returns this alignment's XML representation as DOM document.
	 * @return this alignment's XML representation
	 */
	public Document toXML() throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = doc.createElement("rdf:RDF");
		root.setAttribute("xmlns:", "http://knowledgeweb.semanticweb.org/heterogeneity/alignment");
		root.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema#");
		root.setAttribute("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

		Element alignment = doc.createElement("Alignment");
		Element elem = doc.createElement("xml");
		elem.appendChild(doc.createTextNode("yes"));
		alignment.appendChild(elem);

		elem = doc.createElement("level");
		elem.appendChild(doc.createTextNode(level));
		alignment.appendChild(elem);

		elem = doc.createElement("type");
		elem.appendChild(doc.createTextNode(type));
		alignment.appendChild(elem);

		elem = doc.createElement("onto1");
		elem.appendChild(doc.createTextNode(onto1 == null ? "" : onto1));
		alignment.appendChild(elem);

		elem = doc.createElement("onto2");
		elem.appendChild(doc.createTextNode(onto2 == null ? "" : onto2));
		alignment.appendChild(elem);

		for (Correspondence corr : correspondences) {
			alignment.appendChild(corr.toXML(doc));
		}

		root.appendChild(alignment);
		doc.appendChild(root);


		return doc;
	}

	/**
	 * Returns the alignment's XML representation as string. It also adds
	 * the needed DTD definition which is only possible during this step and
	 * not via the DOM interface
	 * @return string containing XML serialization of alignment
	 */
	public String toXMLString() throws TransformerConfigurationException,
		ParserConfigurationException, TransformerException {
		Document doc = toXML();
		TransformerFactory transfact = TransformerFactory.newInstance();
		Transformer trans = transfact.newTransformer();
		trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "align.dtd");
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter writer = new StringWriter();
		StreamResult res = new StreamResult(writer);
		DOMSource src = new DOMSource(doc);
		trans.transform(src, res);
		return writer.toString();
	}

	/**
	 * Validates the aligment against the given ontologies. This method uses
	 * the passed OWLOntology objects to perform this validation.
	 *
	 * During the validation there is only checked if each entity mentioned in
	 * the alignment is contained in the respective ontology.
	 * @param onto1 ontology 1
	 * @param onto2 ontology 2
	 * @return true if validation succeeded (it will not return false but throw
	 *	an InvalidAlignmentException if the validation fails)
	 */
	public boolean validate(OWLOntology onto1, OWLOntology onto2)
		throws InvalidAlignmentException {
		Set<OWLEntity> entities1 = onto1.getReferencedEntities();
		Set<OWLEntity> entities2 = onto2.getReferencedEntities();

		// build hashmap backed sets of entities contained in the ontologies
		// (should improve performance)
		Set<String> entitySet1 = new HashSet<String>();
		for (OWLEntity ent : entities1) {
			entitySet1.add(ent.getURI().toString());
		}

		Set<String> entitySet2 = new HashSet<String>();
		for (OWLEntity ent : entities2) {
			entitySet2.add(ent.getURI().toString());
		}

		for (Correspondence corr : correspondences) {
			if (!entitySet1.contains(corr.getEntity1())) {
				throw new InvalidAlignmentException(
					"Ontology 1 does not contain the entity '" + corr.getEntity1()
					+ "' which is referenced by this alignment"
				);
			}

			if (!entitySet2.contains(corr.getEntity2())) {
				throw new InvalidAlignmentException(
					"Ontology 2 does not contain the entity '" + corr.getEntity2()
					+ "' which is referenced by this alignment"
				);
			}
		}

		return true;
	}

	/**
	 * Sets the level of this alignment
	 * @param level level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * Sets the type of this alignment
	 * @param type type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets the URI for ontology 1.
	 * @param uri URI of ontology 1 as string
	 */
	public void setOntology1(String uri) {
		this.onto1 = uri;
	}

	/**
	 * Sets the URI for ontology 2.
	 * @param uri URI of ontology 2 as string
	 */
	public void setOntology2(String uri) {
		this.onto2 = uri;
	}

	/**
	 * Returns the URI of ontology 1
	 * @return URI of ontology 1
	 */
	public String getOntology1() {
		return onto1;
	}

	/**
	 * Returns the URI of ontology 2
	 * @return URI of ontology 2
	 */
	public String getOntology2() {
		return onto2;
	}

	/**
	 * Returns the level of this alignment
	 * @return this alignment's level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * Returns the type of this alignment
	 * @return this alignment's type
	 */
	public String getType() {
		return type;
	}

}
