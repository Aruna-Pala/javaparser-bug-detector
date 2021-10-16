package javaparserBugDetector.Checker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import javaparserBugDetector.CommonUtil;
import javaparserBugDetector.DirExplorer;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;

import javaparserBugDetector.BugPattern.BugPattern;
import javaparserBugDetector.BugPattern.EqualsWithoutNullArgumentBugPattern;
import javaparserBugDetector.BugPattern.HashcodeWithoutEqualsBugPattern;
import javaparserBugDetector.Interfaces.IChecker;

public class EqualsWithoutNullArgumentChecker implements IChecker {
	public List<BugPattern> check(File projectDir) {

		List<BugPattern> bugPatterns = new ArrayList<>();
		Map<String, Map<Integer, Set<String>>> variables = new HashMap<>();
		
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
			try {
				final int[] line = { 0 };
				new VoidVisitorAdapter<Object>() {
					@Override
					public void visit(MethodDeclaration md, Object arg) {
						super.visit(md, arg);
						if (md.getNameAsString().equals("equals") && md.getTypeAsString().equals("boolean")) {
							NodeList<Parameter> nodes = md.getParameters();
							if ((nodes.size() == 1) && (nodes.get(0).getTypeAsString().equals("Object"))) {
								Parameter parameter = nodes.get(0);
								String className = CommonUtil.getClassName(md);
                                int functionHashcode = md.hashCode();

								if (className != null) {
                                    if (variables.containsKey(className)) {
                                        // This class is known: add the method name to the hashset
                                        Map<Integer, Set<String>> mapClass = variables.get(className);
                                        if (mapClass.containsKey(functionHashcode)) {
                                            mapClass.get(functionHashcode).add(parameter.getNameAsString());
                                        } else {
                                            Set<String> methodSet = new HashSet<>();
                                            methodSet.add(parameter.getNameAsString());
                                            mapClass.put(functionHashcode, methodSet);
                                        }
                                    } else {
                                        // First time we loop on this class: create the hashset and add the method name
                                        Map<Integer, Set<String>> mapClass = new HashMap<>();
                                        Set<String> methodSet = new HashSet<>();
                                        methodSet.add(parameter.getNameAsString());
                                        mapClass.put(functionHashcode, methodSet);
                                        variables.put(className, mapClass);
                                    }
                                }
							}
						}
					}

				}.visit(JavaParser.parse(file), null);

			} catch (IOException e) {
				new RuntimeException(e);
			}
		}).explore(projectDir);
		
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
			try {
				final int[] line = { 0 };
				new VoidVisitorAdapter<Object>() {
					
					@Override
					public void visit(IfStmt n, Object arg) {
						super.visit(n, arg);
						Node nCond = n.getChildNodes().get(0);
                        String className = CommonUtil.getClassName(n);
                        String functionName = CommonUtil.getFunctionName(n);
                        MethodDeclaration methodDeclaration = CommonUtil.getMethodDeclaration(n);
                        if (methodDeclaration != null) {
                            int functionHashcode = methodDeclaration.hashCode();

                            if (nCond != null &&
                                className != null &&
                                functionName != null &&
                                variables.containsKey(className) &&
                                variables.get(className).containsKey(functionHashcode) &&
                                shouldAddBugPattern(nCond, variables.get(className).get(functionHashcode))&&
                                checkReturnFalseForNull(n.getChildNodes().get(1))) {
                                // Get line
                                int line = (n.getRange().isPresent() ? n.getRange().get().begin.line : 0);

                                // Append to bug pattern
                                bugPatterns.add(new EqualsWithoutNullArgumentBugPattern(line, file, functionName));
                            }
                        }
					}
				}.visit(JavaParser.parse(file), null);

			} catch (IOException e) {
				new RuntimeException(e);
			}
		}).explore(projectDir);
		return bugPatterns;
	}
	 private static boolean checkReturnFalseForNull(Node n) {
		 boolean returnFalse = false;
		 if(n.getChildNodes().get(0).toString().equals("return false;")) {
			 returnFalse = true;
		 }
		 return returnFalse;
	 }
	 private static boolean shouldAddBugPattern(Node n, Set<String> variables)
	    {
	        if (n instanceof BinaryExpr) {
	            boolean checkVariable = false;

	            if (((BinaryExpr) n).getOperator().toString().equals("EQUALS")) {
	                checkVariable = true;
	            }
	            else if (((BinaryExpr) n).getOperator().toString().equals("NOT_EQUALS")) {
	                checkVariable = true;
	            }
	            
	            
	            if(((BinaryExpr) n).getRight().toString().equals("null")) {
	            	checkVariable = true;
	            }
	              if((((BinaryExpr) n).getRight() instanceof IntegerLiteralExpr)||(((BinaryExpr) n).getRight() instanceof DoubleLiteralExpr)) {	
	            	checkVariable = false;
	            }
	            
				if((((BinaryExpr) n).getLeft() instanceof IntegerLiteralExpr)||(((BinaryExpr) n).getLeft() instanceof DoubleLiteralExpr)) {          	
					checkVariable = false;
				 }

	            if (checkVariable) {
	                for (Node node : n.getChildNodes()) {
	                    if (node.getClass().getSimpleName().equals("NameExpr")) {
	                        String variableToCheck = node.toString();
	                        if (variables.contains(variableToCheck)) {
	                            return true;
	                        }
	                    }
	                    return shouldAddBugPattern(node, variables);
	                }
	            }
	        }

	        for (Node node : n.getChildNodes()) {
	            if (!shouldAddBugPattern(node, variables)) {
	                return false;
	            }
	        }

	        return false;
	    }
}