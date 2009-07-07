/*
 * 
 * Starter.java
 * 
 * Software License Agreement (BSD License)
 * 
 * Copyright (c) 2009 Daniel Fleischhacker
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

package de.dfleischhacker.uni.thesis.userinterface;

import de.dfleischhacker.uni.thesis.userinterface.swing.MainWindow;
import de.dfleischhacker.uni.thesis.userinterface.textbased.BatchStarter;

/**
 * This class wraps around the different types of userinterfaces to provide
 * a single starter class no matter if using the Swing based interface or
 * the batchmode.
 * @author Daniel Fleischhacker <dev@dfleischhacker.de>
 */
public class Starter {
	/**
	 * Checks the commandline parameters for a hint which interface should be
	 * started. Default is the GUI interface. The arguments are passed through
	 * to the actual interface's main method.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("No arguments given, starting GUI. Use --help for help");
			MainWindow.main(args);
			return;
		}

		if (args[0].equals("--gui")) {
			MainWindow.main(args);
			return;
		}

		if (args[0].equals("--batch")) {
			BatchStarter.main(removeArg(args, 0));
			return;
		}

		if (args[0].equals("--help")) {
			showHelp();
			return;
		}

		System.out.println("Unknown parameter: " + args[0] + "!");
		showHelp();
	}

	/**
	 * Removes the entry at the given index from the array and returns
	 * the resulting array. Internally this is done by copying the array contents
	 * except the one with the given index into a new array.
	 *
	 * @param args array to remove element from
	 * @param index index of element to remove
	 * @return array without the element at the given index
	 */
	public static String[] removeArg(String[] args, int index) {
		if (index >= args.length)
			return args;

		String[] ret = new String[args.length-1];
		int offset = 0;

		for (int i = 0; i < ret.length; i++) {
			if (i == index) {
				offset = 1;
				if ((i + 1) == args.length)
					break;
			}
			ret[i] = args[i+offset];
		}

		return ret;
	}

	/**
	 * Prints some help lines
	 */
	public static  void showHelp() {
		System.out.println("This is the help of the starter.\n");
		System.out.println("By default the Swing GUI is started by this launcher.\n" +
				"To use the batchmode please pass the argument\n" +
				"\t--batch\n" +
				"for more help concerning the batchmode use\n" +
				"\t--batch --help");
	}
}
