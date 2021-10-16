package javaparserBugDetector;

import java.io.File;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javaparserBugDetector.BugPattern.BugPattern;
import javaparserBugDetector.Checker.EqualsWithoutNullArgumentChecker;
import javaparserBugDetector.Checker.HashcodeWithoutEqualsChecker;
import javaparserBugDetector.Checker.InadequateLogInfoInCatchBlockChecker;
import javaparserBugDetector.Interfaces.IChecker;

public class Main {

	public static void main(String[] args) {
		
		// Delete the previous report
        CommonUtil.deleteFiles();
        
		// TODO Auto-generated method stub
		File projectDir = new File("filesToParse");

		// Create a hashset of bug patterns so that we won't have any duplicates
		Set<BugPattern> bugPatterns = new HashSet<>();

		IChecker checker = new HashcodeWithoutEqualsChecker();
		bugPatterns.addAll(checker.check(projectDir));

		// Inadequate logging information in catch blocks
		checker = new InadequateLogInfoInCatchBlockChecker();
		bugPatterns.addAll(checker.check(projectDir));

		checker = new EqualsWithoutNullArgumentChecker();
		bugPatterns.addAll(checker.check(projectDir));
		
		CommonUtil.generateReport(new ArrayList<BugPattern>(bugPatterns));
		
		
		
	}

}
