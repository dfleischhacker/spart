package de.dfleischhacker.uni.thesis.utils.ontology.nominals;

import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.util.OWLEntityCollector;


/**
 * Class used to detect axioms
 *
 * Kindly provided by Christian Meilicke.
 *
 */
public class NominalAxiomDetector extends OWLEntityCollector {

	// private HashSet<OWLIndividual> nominalIndividuals = new HashSet<OWLIndividual>();
	private boolean containsNominal = false;

	@Override
    public void visit(OWLObjectOneOf desc) {
		// System.out.println("ObjectOneOf: " +  desc + " with values " + desc.getIndividuals());
		// this.nominalIndividuals.addAll(desc.getIndividuals());
		containsNominal = true;
	}

	@Override
    public void visit(OWLObjectValueRestriction desc) {
		// System.out.println("ObjectValueRestriction: " + desc + " with value " + desc.getValue());
		// this.nominalIndividuals.add(desc.getValue());
		containsNominal = true;
	}

	/**
	* Informs about the existence of a nominal in an axiom.
	*
	* @return True if in previous check contains a definition of a nominal.
	*/
	public boolean detectedNominal() {
		if (this.containsNominal) {
			this.containsNominal = false;
			return true;
		}
		else {
			return false;
		}
	}

	/**
	* Resets the flag that indicates wether or not a nominal has been found.
	*/
	@Override
	public void reset() {
		this.containsNominal = false;
	}
}
