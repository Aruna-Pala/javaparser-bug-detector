package javaparserBugDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import javaparserBugDetector.BugPattern.BugPattern;

public class CommonUtil {

	public static String reportFilePath = "report/bugpattern.txt";

	public static int getLineNumber(Node statement) {
		return statement.getRange().isPresent() ? statement.getRange().get().begin.line : 0;
	}

	public static String getFunctionName(Node node) {
		MethodDeclaration methodDeclaration = CommonUtil.getMethodDeclaration(node);

		if (methodDeclaration == null) {
			return null;
		}

		return Objects.requireNonNull(methodDeclaration).getName().getIdentifier();
	}

	public static MethodDeclaration getMethodDeclaration(Node node) {
		// Get the class name by going back up
		Node currentParent = node.getParentNode().orElse(null);
		while (!(currentParent instanceof MethodDeclaration) && currentParent != null) {
			currentParent = currentParent.getParentNode().orElse(null);
		}

		MethodDeclaration methodDeclaration = (MethodDeclaration) currentParent;

		if (methodDeclaration == null) {
			return null;
		}

		return methodDeclaration;
	}

	public static String getClassName(Node node) {
		// Get the class name by going back up
		Node currentParent = node.getParentNode().orElse(null);
		while (!(currentParent instanceof ClassOrInterfaceDeclaration) && currentParent != null) {
			currentParent = currentParent.getParentNode().orElse(null);
		}

		ClassOrInterfaceDeclaration className = (ClassOrInterfaceDeclaration) currentParent;

		if (className == null) {
			return null;
		}

		return Objects.requireNonNull(className).getName().getIdentifier();
	}

	/**
	 * Delete files from the report folder
	 */
	static void deleteFiles() {
		File file = new File(reportFilePath);
		if(file.exists()) {
				file.delete();			
		}
		
	}

	static boolean createFiles() {
		try {
			File files = new File(reportFilePath);
			if (files.createNewFile()) {
				System.out.println("File created: " + files.getName());
				return true;
			} else {
				System.out.println("File already exists.");
				return true;
			}

		} catch (IOException e) {
			System.out.println("Error while creating report, " + e.toString());
			return false;
		}
	}

	public static void generateReport(List<BugPattern> bugPatternsList) {
		try {
			boolean filecreated = createFiles();
			if (filecreated) {
				FileWriter myWriter = new FileWriter(reportFilePath);
				for (BugPattern bugPattern : bugPatternsList) {
					System.out.println(bugPattern.toString());
					myWriter.write(bugPattern.toString());
				}
				myWriter.close();
				System.out.println("Successfully wrote to the file.");
			}

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

}
