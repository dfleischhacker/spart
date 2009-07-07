/*
 *
 * AlignmentManager.java
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides methods to read the alignment format from files as defined in
 *		Jérôme Euzenat - An API for ontology alignment
 *
 * To read the alignment a DOM parser is used internally.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class AlignmentManager {
	/**
	 * Private constructor to prevent instantiation
	 */
	private AlignmentManager() {
	}

	/**
	 * Loads an alignment from the given stream and returns the corresponding
	 * Alignment object.
	 * @param stream stream to read the alignment from
	 * @param threshold load only correspondences with at least this value
	 *		as confidence measure
	 * @return Alignment object representing the loaded alignment
	 */
	public static Alignment loadAlignment(InputStream stream, float threshold)
		throws SAXException, IOException, ParserConfigurationException, AlignmentFormatException {
		Alignment alignment = new Alignment();
        DocumentBuilder docBuilder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.parse(stream);

        // extract data from DOM tree
        Element root = doc.getDocumentElement();

		NodeList tempNodelist = root.getElementsByTagName("Alignment");

		if (tempNodelist.getLength() != 1) {
			throw new AlignmentFormatException("There must be exactly one Alignment tag!");
		}

		Element alignmentNode = (Element) tempNodelist.item(0);

		/*
		 * Extract level
		 */
		tempNodelist = alignmentNode.getElementsByTagName("level");
		if (tempNodelist.getLength() != 1) {
			throw new AlignmentFormatException("There must be exactly one level tag!");
		}
		Element curElem = (Element) tempNodelist.item(0);
		alignment.setLevel(curElem.getChildNodes().item(0).getNodeValue());

		/*
		 * Extract type
		 */
		tempNodelist = alignmentNode.getElementsByTagName("type");
		if (tempNodelist.getLength() != 1) {
			throw new AlignmentFormatException("There must be exactly one type tag!");
		}
		curElem = (Element) tempNodelist.item(0);
		alignment.setType(curElem.getChildNodes().item(0).getNodeValue());

		/*
		 * Extract ontology 1
		 */
		tempNodelist = alignmentNode.getElementsByTagName("onto1");
		if (tempNodelist.getLength() != 1) {
			// this used to be an error but there are alignments not having
			// an onto1 tag
		}
		else {
			curElem = (Element) tempNodelist.item(0);

			String textNode = "";
			if (curElem.getChildNodes().item(0) != null)
				textNode = curElem.getChildNodes().item(0).getNodeValue();
			alignment.setOntology1(textNode);
		}

		/*
		 * Extract ontology 2
		 */
		tempNodelist = alignmentNode.getElementsByTagName("onto2");
		if (tempNodelist.getLength() != 1) {
			// this used to be an error but there are alignments not having
			// an onto2 tag
		}
		else {
			curElem = (Element) tempNodelist.item(0);

			String textNode = "";
			if (curElem.getChildNodes().item(0) != null)
				textNode = curElem.getChildNodes().item(0).getNodeValue();
			alignment.setOntology2(textNode);
		}

		/*
		 * Extract mappings
		 */
		NodeList mappings = alignmentNode.getElementsByTagName("map");

		for (int i = 0; i < mappings.getLength(); i++) {
			Element map = (Element) ((Element) mappings.item(i)).getElementsByTagName("Cell").item(0);
			Correspondence corr = new Correspondence();

			tempNodelist = map.getElementsByTagName("measure");
			if (tempNodelist.getLength() != 1) {
				throw new AlignmentFormatException("Invalid mapping definition");
			}
			curElem = (Element) tempNodelist.item(0);
			float measureValue = Float.parseFloat(curElem.getChildNodes().item(0).getNodeValue());

			// add only correspondences satisfying the confidence threshold
			if (measureValue < threshold)
				continue;

			tempNodelist = map.getElementsByTagName("entity1");
			if (tempNodelist.getLength() != 1) {
				throw new AlignmentFormatException("Invalid mapping definition");
			}
			curElem = (Element) tempNodelist.item(0);
			corr.setEntity1(curElem.getAttribute("rdf:resource"));

			tempNodelist = map.getElementsByTagName("entity2");
			if (tempNodelist.getLength() != 1) {
				throw new AlignmentFormatException("Invalid mapping definition");
			}
			curElem = (Element) tempNodelist.item(0);
			corr.setEntity2(curElem.getAttribute("rdf:resource"));

			tempNodelist = map.getElementsByTagName("relation");
			if (tempNodelist.getLength() != 1) {
				throw new AlignmentFormatException("Invalid mapping definition");
			}
			curElem = (Element) tempNodelist.item(0);
			corr.setRelation(curElem.getChildNodes().item(0).getNodeValue());

			corr.setMeasure(measureValue);

			alignment.addCorrespondence(corr);
		}

		return alignment;
	}

	/**
	 * Loads an alignment from the given stream and returns the corresponding
	 * Alignment object.
	 * @param stream stream to read the alignment from
	 * @return Alignment object representing the loaded alignment
	 */
	public static Alignment loadAlignment(InputStream stream)
		throws SAXException, IOException, ParserConfigurationException, Exception {
		return loadAlignment(stream, 0);
	}

	/**
	 * Loads an alignment from the given file. This is a convinience method
	 * which creates an InputStream and passes it to another method.
	 * @param filename file to load alignment from
	 * @param threshold load only correspondences with at least this value
	 *		as confidence measure
	 */
	public static Alignment loadAlignment(String filename, float threshold)
            throws FileNotFoundException, SAXException, IOException,
            ParserConfigurationException, Exception {
		return loadAlignment(new FileInputStream(filename), threshold);
	}

	/**
	 * Loads an alignment from the given file. This is a convinience method
	 * which creates an InputStream and passes it to another method.
	 * @param filename file to load alignment from
	 */
	public static Alignment loadAlignment(String filename)
            throws FileNotFoundException, SAXException, IOException,
            ParserConfigurationException, AlignmentFormatException {
		return loadAlignment(new FileInputStream(filename), 0);
	}

	/**
	 * Returns an empty alignment
	 * @return empty alignment
	 */
	public static Alignment getEmptyAlignment() {
		return new Alignment();
	}

	/**
	 * Creates an alignment from the given set of correspondences
	 * @param onto1 uri of ontology 1
	 * @param onto2 uri of ontology 2
	 * @param corrSet set of correspondences to create alignment from
	 * @return alignment created from set of correspondences
	 */
	public static Alignment toAlignment(String onto1, String onto2, Set<Correspondence> corrSet) {
		return new Alignment(onto1, onto2, corrSet);
	}

	/**
	 * Writes the given Alignment into the given file. The alignment is represented
	 * in XML.
	 * @param filename file to write the alignment into
	 */
	public static void writeAlignment(String filename, Alignment alignment)
		throws IOException, TransformerConfigurationException,
			ParserConfigurationException, TransformerException {
		FileWriter writer = new FileWriter(filename);
		writer.write(alignment.toXMLString());
		writer.close();
	}
}
