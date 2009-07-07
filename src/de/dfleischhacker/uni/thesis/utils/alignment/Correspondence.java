/*
 *
 * Correspondence.java
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

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a correspondence made of the following components
 *	<ul>
 *		<li>entity from ontology 1</li>
 *		<li>entity from ontology 2</li>
 *		<li>relation holdinhg between those two entities</li>
 *		<li>confidence measure (optional, default=1.0)</li>
 *	</ul>
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class Correspondence {
	private String entity1;
	private String entity2;
	private String relation;
	private float measure;
	private int hashCode;

	/**
	 * Initializes the correspondence using the given values
	 * @param entity1 entity from ontology 1
	 * @param entity2 entity from ontology 2
	 * @param relation relations holding between entity1 and entity2
	 * @param measure confidence measure for relation
	 */
	public Correspondence(String entity1, String entity2, String relation,
			float measure) {
		this.entity1 = entity1;
		this.entity2 = entity2;
		this.relation = relation;
		this.measure = measure;
	}

	/**
	 * Initializes the correspondence using the given values. The confidence
	 * measure for this correspondence is set to 1.0
	 * @param entity1 entity from ontology 1
	 * @param entity2 entity from ontology 2
	 * @param relation relations holding between entity1 and entity2
	 */
	public Correspondence(String entity1, String entity2, String relation) {
		this(entity1, entity2, relation, 1.0f);
	}

	/**
	 * Initializes the correspondence without values. This is used by the
	 * AlignmentManager while reading an alignment from files.
	 */
	public Correspondence() {
		this.measure = 1.0f;
	}

	/**
	 * Returns the correspondence's string representation
	 * @return correspondence's string representation
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("( ");
		builder.append(entity1);
		builder.append(" , ");
		builder.append(entity2);
		builder.append(" , ");
		builder.append(relation);
		builder.append(" , ");
		builder.append(measure);
		builder.append(" )");

		return builder.toString();
	}

	/**
	 * Returns this correspondence's XML representation. The given document is used
	 * to generate the elements.
	 * This is a convenience method which omits the RDF namespace declaration.
	 * @return XML representation
	 */
	public Element toXML(Document doc) throws ParserConfigurationException {
		return toXML(doc, false);
	}

	/**
	 * Returns this correspondence's XML representation. The given document is used
	 * to generate the elements. If addNamespace is true
	 * the namespace declaration for RDF is added to the root of the generated
	 * XML snippet, otherwise this namespace is omitted and should be added
	 * at an higher level element (i.e. when the different correspondence
	 * snippets are combined to a full XML document).
	 * @param addNamespace if true namespace delaration for RDF will be added to
	 *	the root of the generated XML snippets, otherwise this declaration
	 *	is omitted
	 * @return XML representation
	 */
	public Element toXML(Document doc, boolean addNamespace) throws ParserConfigurationException {
		Element elem = doc.createElement("map");
		if (addNamespace)
			elem.setAttribute("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		Element cell = doc.createElement("Cell");
		Element ent = doc.createElement("entity1");
		ent.setAttribute("rdf:resource", entity1);
		cell.appendChild(ent);
		ent = doc.createElement("entity2");
		ent.setAttribute("rdf:resource", entity2);
		cell.appendChild(ent);
		ent = doc.createElement("relation");
		ent.appendChild(doc.createTextNode(relation));
		cell.appendChild(ent);
		ent = doc.createElement("measure");
		ent.setAttribute("rdf:datatype", "http://www.w3.org/2001/XMLSchema#float");
		ent.appendChild(doc.createTextNode(Float.toString(measure)));
		cell.appendChild(ent);
		elem.appendChild(cell);

		return elem;
	}

	/**
	 * Returns this correspondence's hashCode. The hashcode is determined by
	 * creating the string containing entity1 + "--" + relation + "--" + entity2
	 * and returning this string's hashCode. This approachs guarantees that the
	 * hashcode requirements are fulfilled for the Correspondence objects.
	 * @return hashCode of this correspondence
	 */
	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = (entity1 + "--" + relation + "--" + entity2).hashCode();
		}

		return hashCode;
	}

	/**
	 * Returns true if the given correspondence is equal to this correspondence.
	 *
	 * Two correspondeces are equal if and only if entity1 AND entity2 AND relation
	 * are the same values in both correspondeces.
	 * @param obj correspondence to compare to
	 * @return true if correspondences are equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Correspondence other = (Correspondence) obj;
		if ((this.entity1 == null) ? (other.entity1 != null) : !this.entity1.equals(other.entity1)) {
			return false;
		}
		if ((this.entity2 == null) ? (other.entity2 != null) : !this.entity2.equals(other.entity2)) {
			return false;
		}
		if ((this.relation == null) ? (other.relation != null) : !this.relation.equals(other.relation)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Sets entity1
	 * @param entity1 URI for entity1 as string
	 */
	public void setEntity1(String entity1) {
		this.entity1 = entity1;
	}

	/**
	 * Sets entity2
	 * @param entity2 URI for entity 2 as string
	 */
	public void setEntity2(String entity2) {
		this.entity2 = entity2;
	}

	/**
	 * Sets the relation holding between entity 1 and 2
	 * @param relation relation holding between entities
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	/**
	 * Set measure. This should be in range from 0 to 1.
	 * @param measure measure to set
	 */
	public void setMeasure(float measure) {
		this.measure = measure;
	}

	/**
	 * @return the entity1
	 */
	public String getEntity1() {
		return entity1;
	}

	/**
	 * @return the entity2
	 */
	public String getEntity2() {
		return entity2;
	}

	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * @return the measure
	 */
	public float getMeasure() {
		return measure;
	}


}
