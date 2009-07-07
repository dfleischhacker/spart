/*
 *
 * AlignmentTableModel.java
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


package de.dfleischhacker.uni.thesis.userinterface.swing;

import java.net.URI;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import de.dfleischhacker.uni.thesis.utils.alignment.Alignment;
import de.dfleischhacker.uni.thesis.utils.alignment.Correspondence;

/**
 * TableModel used to show Alignments.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class AlignmentTableModel extends DefaultTableModel {
	private int curRowNumber = 1;

	/**
	 * TableModel for displaying differences between alignments. It generates
	 * the tables and adds the columns and rows.
	 * @param align
	 * @param refalign
	 */
	public AlignmentTableModel(Alignment align, Alignment refalign) {
		Set<Correspondence> alignSet = align.getCorrespondences();
		Set<Correspondence> refalignSet = refalign.getCorrespondences();

		addColumn("No");
		addColumn("Entity 1");
		addColumn("Entity 2");
		addColumn("Rel");
		addColumn("Conf");
		addColumn("S");

		for (Correspondence correspondence : alignSet) {
			String[] row = new String[6];
			row[0] = Integer.toString(curRowNumber++);
			row[1] = extractName(correspondence.getEntity1());
			row[2] = extractName(correspondence.getEntity2());
			row[3] = correspondence.getRelation();
			row[4] = Float.toString(Math.round(correspondence.getMeasure()*100)/(float)100);

			String state = "";
			if (refalignSet.contains(correspondence)) {
				state = "\u2714";
			}
			else {
				state = "\u2718";
			}

			row[5] = state;

			addRow(row);
		}

		for (Correspondence correspondence : refalignSet) {
			if (alignSet.contains(correspondence)) {
				continue;
			}

			String[] row = new String[6];
			row[0] = Integer.toString(curRowNumber++);
			row[1] = extractName(correspondence.getEntity1());
			row[2] = extractName(correspondence.getEntity2());
			row[3] = correspondence.getRelation();
			row[4] = Float.toString(Math.round(correspondence.getMeasure()*100)/(float)100);

			row[5] = "";

			addRow(row);
		}
	}

	/**
	 * Makes all cells uneditable.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/**
	 * Extracts the entity name from a given URI. This is done in several steps
	 * until the first method succeeds. First, the last fragment of the URI
	 * is tried to be used (the part after the #), then the element after the
	 * last slash is used. If the URI ends with a slash, the previous element
	 * is used.
	 * @param uri URI to extract name from
	 * @return extracted name
	 */
	private String extractName(String uri) {
		String name = URI.create(uri).getFragment();

		// no # in URI so we try to use the element after the last /
		if (name == null) {
			int lastSlash = uri.lastIndexOf('/') + 1;

			// apparently the the URI ends with an /, try to extract last element before
			if (lastSlash > uri.length()) {
				// remove ending slash
				uri = uri.substring(0, uri.length() - 1);
			}
			name = uri.substring(uri.lastIndexOf('/')+1);
		}
		return name;
	}
}
