package javaparserBugDetector.Checker;

import javaparserBugDetector.DirExplorer;
import javaparserBugDetector.BugPattern.BugPattern;
import javaparserBugDetector.BugPattern.HashcodeWithoutEqualsBugPattern;
import javaparserBugDetector.Interfaces.IChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class HashcodeWithoutEqualsChecker implements IChecker {
	public List<BugPattern> check(File projectDir) {

		List<BugPattern> bugPatterns = new ArrayList<>();

		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
			try {
				final boolean[] equalsFound = { false };

				final boolean[] hcFound = { false };

				final int[] line = { 0 };

				new VoidVisitorAdapter<Object>() {
					@Override
					public void visit(MethodDeclaration md, Object arg) {
						super.visit(md, arg);
						if (md.getNameAsString().equals("equals") && md.getTypeAsString().equals("boolean")) {
							NodeList<Parameter> nodes = md.getParameters();
							if ((nodes.size() == 1) && (nodes.get(0).getTypeAsString().equals("Object"))) {
								equalsFound[0] = true;
							}
						} else if (md.getNameAsString().equals("hashCode") && md.getTypeAsString().equals("int")
								&& md.getParameters().size() == 0) {
							hcFound[0] = true;
							// Get line
							line[0] = (md.getRange().isPresent() ? md.getRange().get().begin.line : 0);
						}
					}
				}.visit(JavaParser.parse(file), null);

				if (hcFound[0] && !equalsFound[0]) {
					bugPatterns.add(new HashcodeWithoutEqualsBugPattern(line[0], file, "hashCode"));
				}

			} catch (IOException e) {
				new RuntimeException(e);
			}
		}).explore(projectDir);

		return bugPatterns;
	}
}
