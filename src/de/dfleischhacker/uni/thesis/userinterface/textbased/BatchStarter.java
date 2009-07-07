/*
 *
 * BatchStarter.java
 *
 * Software License Agreement (BSD License)
 *
 * Copyright (c) 2009 Daniel Fleischhacker <dev@dfleischhacker.de>
 * All rights reserved.
 
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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import de.dfleischhacker.uni.thesis.utils.wrapper.CalculationResult;
import de.dfleischhacker.uni.thesis.utils.wrapper.Wrapper;
import de.dfleischhacker.uni.thesis.utils.wrapper.WrapperException;


/**
 *
 * This class provides batchmodes for computing precision and recall without
 * using the graphical user interface. Thus, it simplifies the evaluation of
 * larger amounts of alignments by far. The batchmode is mainly "configured"
 * by filenames found in the processed directory. There are two different
 * modes: the general batchmode and the conference batchmode. Both are described
 * in the following
 *
 * <h3>General batchmode</h3>
 * This is the standard batchmode. It requires two parameters to be passed from
 * the commandline at runtime.
 *		SEMANTIC BASEDIR
 *
 * The first parameter is the name of the semantic
 * which is to simplify matters only the first part of the name and thus one
 * of Natural, Pragmatic or Null (for classical measures). The semantic name
 * is case-insensitive. The second argument is the base-directory whose
 * subdirectories will be traversed by the batchmode. The format for the
 * base-directory and its contents is as follows.
 *
 *	basedir/								[ directory passed at commandline ]
 *		subdir1/							[ directory of first ontology set ]
 *			onto1.rdf						[ first ontology ]
 *			onto2.rdf						[ second ontology ]
 *			refalign.rdf					[ reference alignment ]
 *			*.rdf							[ alignments to be evaluated ]
 *		subdir2/
 *			...
 *
 * The base-directory may contain an arbitrary number of sub-directories which
 * in turn may contain (besides the two ontologies and the reference alignment)
 * an arbitrary number of alignments. These alignments are evaluated according
 * to the given semantics and with regard to the ontologies and the reference
 * alignment.
 *
 * The results are written into an XML file named results.xml which is placed
 * in the base-directory.
 *
 *
 * <h3>Conference batchmode</h3>
 * As the conference test set would be very hard to evaluate using the general
 * batchmode, we implemented a special batchmode for this test set. This mode
 * requires three arguments prefixed by a special argument which activates the
 * conference batchmode.
 *
 *		--conference THRESHOLD SEMANTIC BASEDIR
 *
 * The argument "--conference" activates the conference mode. The next argument
 * specifies the threshold. This is a quirk of the conference evaluation. Only
 * correspondences with a threshold equal or greater than the specified threshold
 * are considered during the evaluation, all other correspondences are ignored.
 * This argument must be parseable into a float type, i.e. 0.2 , 0.5 or 0.7 .
 * The semantic argument is the same as described for the general batchmode.
 * The last argument determines the base-directory to work at. Different from the
 * general mode, the conference mode does not expect subdirectories but expects
 * the data to be directly in the provided directory. The naming conventions
 * for these files are as follows (we use regular expressions in the following
 * descriptions).
 * The base directory may contain three different types of files: ontologies,
 * reference alignments and alignments which should be evaluated.
 * The ontologies have to be named matching the expression [a-zA-Z]*\.owl
 * The reference alignments have names matching to FIRSTONTO-SECONDONTO\.(rdf|owl|xml)
 * and the alignments are named MATCHER-FIRSTONTO-SECONDONTO\.(rdf|owl|xml).
 * FIRSTONTO, SECONDONTO and MATCHER are string matching [a-zA-Z]*
 * For each possible combinaton of ontologies the name FIRSTONTO-SECONDONTO is
 * generated and if there exists a corresponding reference alignment each alignment
 * with an arbitrary matcher name and the correct reference alignment name is
 * evaluated against the ontologies and the reference alignment.
 *
 * The results are saved into the base directory as an XML file named results.xml
 *
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class BatchStarter {
	static HashMap<String, String> semanticResolverTable;
	
	// used to blacklist alignments which cause overwhelming runtime in Pellet
	static HashSet<String> conferenceBlacklist;

	static {
		semanticResolverTable = new HashMap<String, String>();
		semanticResolverTable.put("null", "Null Semantic");
		semanticResolverTable.put("natural", "Natural Semantic");
		semanticResolverTable.put("pragmatic", "Pragmatic Semantic");

		conferenceBlacklist = new HashSet<String>();
		conferenceBlacklist.add("dssim-cmt-iasted");
		conferenceBlacklist.add("asmov-cmt-iasted");
		conferenceBlacklist.add("lily-confof-iasted");
	}

	/**
	 * Calculates precision and recall for every subdirectory of the directory
	 * passed on the commandline which contains all required files and saves
	 * the generated data into the same directory.
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		if ((args.length != 2 && args.length != 4) || args[0].equals("--help")) {
			showHelp();
			System.exit(1);
		}

		String pathname = null;
		String semantic = null;
		boolean conferenceMode = false;
		float threshold = 0;

		int index = 0;

		if (args[index].equalsIgnoreCase("--conference")) {
			conferenceMode = true;

			// in case of conference mode we need 4 arguments
			if (args.length != 4) {
				showHelp();
				System.exit(1);
			}
			index++;
			threshold = Float.valueOf(args[index]);
			index++;
		}

		semantic = args[index++];
		pathname = args[index++];

		if (!semanticResolverTable.containsKey(semantic)) {
			showHelp();
			System.exit(1);
		}

		semantic = semanticResolverTable.get(semantic);


		if (conferenceMode) {
			handleConferenceBatchmode(pathname, semantic, threshold);
		}
		else {
			handleBatchmode(pathname, semantic);
		}
	}

	/**
	 * Actually handles the general batchmode
	 * @param pathname path of basedir
	 */
	public static void handleBatchmode(String pathname, String semantic) {
		File basedir = new File(pathname);
		if (!basedir.isDirectory()) {
			showHelp();
		}


		ResultAggregator agg = new ResultAggregator(basedir.getAbsolutePath(), semantic);
		// traverse over subdirs
		for (File subdir : basedir.listFiles()) {
			System.out.println("===========================================================\n" +
					subdir + "\n" +
					"--------------------------------------------------------");
			if (!subdir.isDirectory()) {
				System.out.println("Skipping: not a directory");
				continue;
			}

			

			File onto1 = null;
			File onto2 = null;
			File refalign = null;
			LinkedList<File> aligns = new LinkedList<File>();

			for (File entry : subdir.listFiles(new RDFFileNameFilter())) {
				if (entry.getName().equals("onto1.rdf")) {
					onto1 = entry;
					continue;
				}
				if (entry.getName().equals("onto2.rdf")) {
					onto2 = entry;
					continue;
				}
				if (entry.getName().equals("refalign.rdf")) {
					refalign = entry;
					continue;
				}
				if (entry.getName().endsWith(".rdf")) {
					aligns.add(entry);
					continue;
				}
			}

			if (onto1 == null) {
				System.out.println("Skipping: missing file 'onto1.rdf'");
				continue;
			}

			if (onto2 == null) {
				System.out.println("Skipping: missing file 'onto2.rdf'");
				continue;
			}

			if (refalign == null) {
				System.out.println("Skipping: missing file 'refalign.rdf'");
				continue;
			}

			if (aligns.size() == 0) {
				System.out.println("Skipping: no alignments to evaluate");
				continue;
			}

			try {
				Wrapper.checkOntology1(onto1.getAbsolutePath());
			} catch (WrapperException ex) {
				System.out.println("Skipping: unable to load ontology 1");
				continue;
			}
			try {
				Wrapper.checkOntology2(onto2.getAbsolutePath());
			} catch (WrapperException ex) {
				System.out.println("Skipping: unable to load ontology 2");
				continue;
			}

			try {
				Wrapper.checkEvaluationAlignment(refalign.getAbsolutePath());
			} catch (WrapperException ex) {
				System.out.println("Skipping: unable to load reference alignment");
				continue;
			}

			boolean validAlignments = true;
			String invalidAlignment = null;
			for (File align : aligns) {
				try {
					Wrapper.checkReferenceAlignment(refalign.getAbsolutePath());
				} catch (WrapperException ex) {
					validAlignments = false;
					invalidAlignment = align.getAbsolutePath();
					break;
				}
			}
			if (!validAlignments) {
				System.out.println("Skipping: unable to load evaluation alignment '"
						+ invalidAlignment + "'");
				continue;
			}

			CalculationResult res = null;

			for (File align : aligns) {
				String prefix = align.getName().substring(0, align.getName().lastIndexOf(".rdf"));
				System.out.println("=============================================");
				System.out.println("Processing " + align.getName());
				try {
					res = Wrapper.calculate(onto1.toURI(), onto2.toURI(),
							align.getAbsolutePath(), refalign.getAbsolutePath(), semantic);
				} catch (WrapperException ex) {
					System.out.println("Skipping: unable to calculate precision and recall ("+
							ex.getMessage() + ")");

					// add errormessage to aggregator
					agg.addError(prefix, subdir.getName(),
							"Unable to calculate precision and recall ("+
							ex.getMessage() + ")");
					continue;
				}

				// add results to aggregator
				agg.addResult(prefix, subdir.getName(), res.getPrecision(), res.getRecall(),
						res.getOriginalAlignment().getCorrespondenceCount(),
						res.getOriginalReference().getCorrespondenceCount(),
						res.getEvaluationClosure().getCorrespondenceCount(),
						res.getReferenceClosure().getCorrespondenceCount(),
						res.getIntersection().getCorrespondenceCount()
				);

				System.out.println("Precision: " + res.getPrecision() + "\t\tRecall: " + res.getRecall());

				// null semantic hasn't got any additional output
				if (semantic.equals("Null Semantic")) {
					continue;
				}
			} // end evaluation alignment traverse
		} // end subdir traverse
		try {
			// end subdir traverse
			agg.writeXML(basedir.getAbsolutePath() + File.separator + "results.xml");
		} catch (ParserConfigurationException ex) {
			System.out.println("Unable to write aggregated results file");
		} catch (TransformerConfigurationException ex) {
			System.out.println("Unable to write aggregated results file");
		} catch (TransformerException ex) {
			System.out.println("Unable to write aggregated results file");
		}
	}

	/**
	 * Handles the special batchmode used for evaluating the conference testset
	 * @param pathname pathname to read data from
	 * @param semantic semantic to use for evaluation
	 * @param threshold threshold to use for alignment loading (dont load
	 *	correspondences with confidence less than threshold
	 */
	private static void handleConferenceBatchmode(String pathname, String semantic,
			float threshold) {
		File basedir = new File(pathname);

		if (!basedir.isDirectory()) {
			System.out.println("ERROR: " + pathname + " is not a directory");
			System.exit(1);
		}

		// create a list of ontologies
		ArrayList<File> ontologyFiles = new ArrayList<File>();
		for (File ontologyFile : basedir.listFiles(new ConferenceOntologyFilter())) {
			ontologyFiles.add(ontologyFile);
		}

		// create map of reference alignments
		HashMap<String,File> referenceAlignments = new HashMap<String, File>();
		for (File referenceAlignment : basedir.listFiles(new ConferenceReferenceAlignmentFilter())) {
			referenceAlignments.put(cutSuffix(referenceAlignment.getName()), referenceAlignment);
		}

		// create map of alignments which should be evaluated
		HashMap<String,File> alignments = new HashMap<String, File>();
		HashMap<String,Boolean> matchers = new HashMap<String, Boolean>();

		for (File alignment : basedir.listFiles(new ConferenceAlignmentFilter())) {
			alignments.put(cutSuffix(alignment.getName()), alignment);

			// add matcher to list if not already done
			String matcher = alignment.getName().substring(0, alignment.getName().indexOf('-'));
			matcher = matcher.toLowerCase();
			if (!matchers.containsKey(matcher)) {
				matchers.put(matcher, true);
			}
		}

		// generate list of matchers from map
		ArrayList<String> matcherList = new ArrayList<String>(matchers.keySet());

		// now evaluate each possible combination
		ResultAggregator agg = new ResultAggregator(basedir.getAbsolutePath(), semantic);
		for (File firstOntology : ontologyFiles) {
				try {
					Wrapper.checkOntology1(firstOntology.toURI());
				} catch (WrapperException ex) {
					System.out.println("unable to load first ontology: "
							+ firstOntology.getName());
					continue;
				}
			for (File secondOntology : ontologyFiles) {
				// there a no alignments of one ontology onto itself
				if (firstOntology == secondOntology)
					continue;

				try {
					Wrapper.checkOntology2(secondOntology.toURI());
				} catch (WrapperException ex) {
					System.out.println("unable to load second ontology: "
							+ secondOntology.getName());
					continue;
				}

				// check for reference ontology
				String curReference =
						cutSuffix(firstOntology.getName()) + "-" +
						cutSuffix(secondOntology.getName());

				if (!referenceAlignments.containsKey(curReference)) {
					continue;
				}

				File curReferenceFile = referenceAlignments.get(curReference);
				try {
					Wrapper.checkReferenceAlignment(curReferenceFile.getAbsolutePath());
				} catch (WrapperException ex) {
					System.out.println("Unable to load reference alignment (" + curReference + "): " +
							ex.getMessage());
					continue;
				}

				// walk through all matchers
				for (String matcher : matcherList) {
					String curAlignment = matcher + "-" + curReference;
					System.out.println("Case: " + curAlignment);

					if (conferenceBlacklist.contains(curAlignment)) {
						System.out.println("Skipping because blacklisted");
						agg.addError(matcher, curReference, "Blacklisted");
						continue;
					}

					if (!alignments.containsKey(curAlignment)) {
						System.out.println("No alignment: " + curAlignment);
						continue;
					}
					File curAlignmentFile = alignments.get(curAlignment);

					try {
						Wrapper.checkEvaluationAlignment(curAlignmentFile.getAbsolutePath());
					} catch (WrapperException ex) {
						System.out.println("unable to load alignment: "
								+ curAlignmentFile.getName() + "'");
						continue;
					}

					CalculationResult res = null;


					try {
						res = Wrapper.calculate(firstOntology.toURI(), secondOntology.toURI(),
								curAlignmentFile.getAbsolutePath(),
								curReferenceFile.getAbsolutePath(),
								semantic, threshold);
					} catch (WrapperException ex) {
						System.out.println("Skipping: unable to calculate precision and recall ("+
								ex.getMessage() + ")");

						// add errormessage to aggregator
						agg.addError(matcher, curReference,
								"Unable to calculate precision and recall ("+
								ex.getMessage() + ")");
						continue;
					}

					// add results to aggregator
					agg.addResult(matcher, curReference, res.getPrecision(), res.getRecall(),
						res.getOriginalAlignment().getCorrespondenceCount(),
						res.getOriginalReference().getCorrespondenceCount(),
						res.getEvaluationClosure().getCorrespondenceCount(),
						res.getReferenceClosure().getCorrespondenceCount(),
						res.getIntersection().getCorrespondenceCount()
					);

					System.out.println("Precision: " + res.getPrecision() + "\t\tRecall: " + res.getRecall());
				} // end walk matchers
				try {
					agg.writeXML(basedir.getAbsolutePath() + File.separator + "results.xml");
				} catch (ParserConfigurationException ex) {
					System.out.println("Unable to write aggregated results file");
				} catch (TransformerConfigurationException ex) {
					System.out.println("Unable to write aggregated results file");
				} catch (TransformerException ex) {
					System.out.println("Unable to write aggregated results file");
				}
			}
		}
	}

	/**
	 * Prints a small help message
	 */
	private static void showHelp() {
		//TODO: complete help
		System.out.println("Please pass arguments as follows:\n\t[--conference THRESHOLD] SEMANTICNAME DIRECTORY");
		System.out.println("Where SEMANTICNAME is one of: null, natural, pragmatic");
	}

	/**
	 * Helper method which returns the filename withpout suffix. Everything after
	 * the first . (dot) is cut off.
	 * @param filename filename
	 * @return filename without suffix
	 */
	private static String cutSuffix(String filename) {
		return filename.substring(0, filename.indexOf('.'));
	}
}

/**
 * Used to get only .rdf files
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
class RDFFileNameFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".rdf");
	}
}

/**
 * Used to get only files satisfying regexp ^[a-zA-Z]*\.owl which are
 * ontology files in the conference testset.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
class ConferenceOntologyFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return name.matches("^[a-zA-Z]*\\.owl");
	}
}

/**
 * Used to get only files satisfying regexp ^[a-zA-Z]*-[a-zA-Z]*\.(xml|rdf)
 * which are reference alignments in the conference testset
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
class ConferenceReferenceAlignmentFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return name.matches("^[a-zA-Z]*-[a-zA-Z]*\\.(owl|rdf|xml)");
	}
}

/**
 * Used to get only files satisfying regexp ^[a-zA-Z]*-[a-zA-Z]*-[a-zA-Z]*\.(xml|rdf)
 * which are alignments in the conference testset
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
class ConferenceAlignmentFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return name.matches("^[a-zA-Z]*-[a-zA-Z]*-[a-zA-Z]*\\.(owl|rdf|xml)");
	}
}

