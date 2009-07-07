/*
 *
 * ResultAggregator.java
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

package de.dfleischhacker.uni.thesis.userinterface.textbased;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to log the results of calculations and provides methods
 * to export the data to a file. Results are logged per test subject and
 * testcase, a logged result consists, amongst others, of precision and recall.
 *
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class ResultAggregator {
	private HashMap<String,HashMap<String,Result>> results;
	private String basedir;
	private String semanticName;
	private HashMap<String,Boolean> testcases;

	/**
	 * Internal class representing a result consisting of testcase name and 
	 * precision and recall values. May be returned through getResult()
	 * method.
	 */
	public class Result {
		public boolean isError() { return false; }
		public String testcase;
		public float precision;
		public float recall;
		public int evaluationAlignmentSize;
		public int referenceAlignmentSize;
		public int evaluationClosureSize;
		public int referenceClosureSize;
		public int intersectionSize;
	}

	/**
	 * Subclass of Result representing an error and containing the
	 * appropriate error message
	 */
	public class ErrorMessage extends Result {
		@Override
		public boolean isError() { return true; }
		public String errorMessage;
	}

	/**
	 * Initializes an empty ResultsAggregator
	 * @param basedir directory serving as base for batchstarter
	 * @param semanticName name of semantic used for evaluation
	 */
	public ResultAggregator(String basedir, String semanticName) {
		results = new HashMap();
		this.basedir = basedir;
		this.semanticName = semanticName;
		testcases = new HashMap<String,Boolean>();
	}

	/**
	 * Adds the given results for a specific subject and testcase combination
	 * into the result log.
	 * @param subject string identifying the subject
	 * @param testcase string identifying the specific testcase
	 * @param precision precision result
	 * @param recall recall result
	 */
	public void addResult(String subject, String testcase, float precision,
			float recall, int evaluationAlignmentSize, int referenceAlignmentSize,
			int evaluationClosureSize, int referenceClosureSize,
			int intersectionSize) {
		// log testcases
		if (!testcases.containsKey(testcase)) {
			testcases.put(testcase, true);
		}

		if (!results.containsKey(subject)) {
			results.put(subject, new HashMap<String,Result>());
		}
		
		Result res = new Result();
		res.testcase = testcase;
		res.recall = recall;
		res.precision = precision;
		res.evaluationAlignmentSize = evaluationAlignmentSize;
		res.evaluationClosureSize = evaluationClosureSize;
		res.intersectionSize = intersectionSize;
		res.referenceAlignmentSize = referenceAlignmentSize;
		res.referenceClosureSize = referenceClosureSize;
		
		results.get(subject).put(testcase,res);
	}

	/**
	 * Logs an error occuring during calculation.
	 * @param subject subject the error occurs for
	 * @param testcase testcase the error occurs for
	 * @param errorMessage message describing the error
	 */
	public void addError(String subject, String testcase, String errorMessage) {
		if (!testcases.containsKey(testcase)) {
			testcases.put(testcase, true);
		}

		if (!results.containsKey(subject)) {
			results.put(subject, new HashMap<String,Result>());
		}

		ErrorMessage res = new ErrorMessage();
		res.testcase = testcase;
		res.errorMessage = errorMessage;

		results.get(subject).put(testcase,res);
	}

	/**
	 * Returns the result for the given subject/testcase combination.
	 * @param subject subject to get result for
	 * @param testcase string identifying the testcase to get result for
	 * @return Result object containing the logged values, null if subject/testcase
	 *	combination invalid
	 */
	public Result getResult(String subject, String testcase) {
		if (!results.containsKey(subject))
			return null;

		if (!results.get(subject).containsKey(testcase))
			return null;

		return results.get(subject).get(testcase);
	}

	/**
	 * Writes the results aggregated by this ResultAggregator into the
	 * given file. A simple XML file format is used for this purpose which
	 * allows easy processing by the user.
	 * @param filename filename to write the aggreated results to
	 */
	public void writeXML(String filename) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
		Document doc =
			DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		doc.appendChild(doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"results.xsl\""));
		Element root = doc.createElement("resultset");
		root.setAttribute("xmlns", "http://uni.dfleischhacker.de/thesis/resultset#");
		root.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema#");

		// save metadata to document
		Element meta = doc.createElement("metadata");
		Element basedirNode = doc.createElement("basedir");
		basedirNode.appendChild(doc.createTextNode(basedir));
		meta.appendChild(basedirNode);

		Element semanticNode = doc.createElement("semantic");
		semanticNode.appendChild(doc.createTextNode(semanticName));
		meta.appendChild(semanticNode);

		root.appendChild(meta);

		for (String subject : results.keySet()) {
			// variables used to calculate average
			float precisionSum = 0;
			float recallSum = 0;
			int validPrecisionCount = 0;
			int validRecallCount = 0;

			HashMap<String,Result> subjectHash = results.get(subject);

			Element subjectNode = doc.createElement("subject");
			subjectNode.setAttribute("name", subject);

			for (String testcase : testcases.keySet()) {
				Element resNode = doc.createElement("testcase");
				resNode.setAttribute("name", testcase);

				// there was an error in this testcase
				if (!subjectHash.containsKey(testcase)) {
					Element errorNode = doc.createElement("error");
					errorNode.appendChild(doc.createTextNode("unspecified error"));
					resNode.appendChild(errorNode);
					subjectNode.appendChild(resNode);
					continue;
				}
				if (subjectHash.get(testcase).isError()) {
					Element errorNode = doc.createElement("error");
					errorNode.appendChild(
						doc.createTextNode(
							((ErrorMessage) subjectHash.get(testcase)).errorMessage
						)
					);
					resNode.appendChild(errorNode);
					subjectNode.appendChild(resNode);
					continue;
				}

				Result result = subjectHash.get(testcase);

				// ignore NaN values when computing average
				if (result.precision != Float.NaN) {
					validPrecisionCount++;
					precisionSum += result.precision;
				}
				if (result.recall != Float.NaN) {
					validRecallCount++;
					recallSum += result.recall;
				}
				
				Element ent = doc.createElement("precision");
				ent.appendChild(doc.createTextNode(Float.toString(result.precision)));
				resNode.appendChild(ent);

				ent = doc.createElement("recall");
				ent.appendChild(doc.createTextNode(Float.toString(result.recall)));
				resNode.appendChild(ent);

				ent = doc.createElement("evaluationAlignmentSize");
				ent.appendChild(doc.createTextNode(Integer.toString(result.evaluationAlignmentSize)));
				resNode.appendChild(ent);

				ent = doc.createElement("evaluationClosureSize");
				ent.appendChild(doc.createTextNode(Integer.toString(result.evaluationClosureSize)));
				resNode.appendChild(ent);

				ent = doc.createElement("referenceAlignmentSize");
				ent.appendChild(doc.createTextNode(Integer.toString(result.referenceAlignmentSize)));
				resNode.appendChild(ent);

				ent = doc.createElement("referenceClosureSize");
				ent.appendChild(doc.createTextNode(Integer.toString(result.referenceClosureSize)));
				resNode.appendChild(ent);

				ent = doc.createElement("intersectionSize");
				ent.appendChild(doc.createTextNode(Integer.toString(result.intersectionSize)));
				resNode.appendChild(ent);

				subjectNode.appendChild(resNode);
			}

			// save calculated average
			Element avgNode = doc.createElement("average");
			Element ent = doc.createElement("precision");
			ent.appendChild(doc.createTextNode(
				Float.toString( ((float) precisionSum) / validPrecisionCount))
			);
			avgNode.appendChild(ent);

			ent = doc.createElement("recall");
			ent.appendChild(doc.createTextNode(
				Float.toString( ((float) recallSum) / validRecallCount))
			);
			avgNode.appendChild(ent);

			subjectNode.appendChild(avgNode);

			root.appendChild(subjectNode);
		}
		
		doc.appendChild(root);

		// write data to file
		File file = new File(filename);
		StreamResult strRes = new StreamResult(file);
		TransformerFactory tfac = TransformerFactory.newInstance();
		tfac.setAttribute("indent-number", new Integer(4));
		Transformer trans = TransformerFactory.newInstance().newTransformer();
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.transform(new DOMSource(doc), strRes);
	}
}
