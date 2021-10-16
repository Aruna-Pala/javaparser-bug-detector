package javaparserBugDetector;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import javaparserBugDetector.BugPattern.BugPattern;
import javaparserBugDetector.Checker.EqualsWithoutNullArgumentChecker;
import javaparserBugDetector.Checker.HashcodeWithoutEqualsChecker;
import javaparserBugDetector.Checker.InadequateLogInfoInCatchBlockChecker;


public class TestJavaparserBugDetector {
	@Test
	public void testEqualWithHashcode() {
        List<BugPattern> bugPatterns = new HashcodeWithoutEqualsChecker().check(new File("filesToParse/filesToParseTest/HashcodeWithEquals.java"));

		Assert.assertEquals(0, bugPatterns.size());
	}

	@Test
	public void testEqualWithoutHashcode() {
		List<BugPattern> bugPatterns = new HashcodeWithoutEqualsChecker().check(new File("filesToParse/HashcodeWithoutEquals.java"));

		Assert.assertEquals(1, bugPatterns.size());
		Assert.assertEquals("HashcodeWithoutEquals.java", bugPatterns.get(0).getFilename());
		Assert.assertEquals("hashCode", bugPatterns.get(0).getFunctionName());
		Assert.assertEquals(3, bugPatterns.get(0).getLine());
	}
	
	@Test
	public void testDuplicateLoggingStatementInCatchBlockOfSameTryWithAdequateInfo() {
		List<BugPattern> bugPatterns = new InadequateLogInfoInCatchBlockChecker().check(new File("filesToParse/filesToParseTest/AdequateLoggingInformationInCatchBlocks.java"));

		Assert.assertEquals(0, bugPatterns.size());
	}

	@Test
	public void testDuplicateLoggingStatementInCatchBlockOfSameTryInadequateInfo() {
		List<BugPattern> bugPatterns = new InadequateLogInfoInCatchBlockChecker().check(new File("filesToParse/InadequateLogInfoInCatchBlock.java"));

		Assert.assertEquals(1, bugPatterns.size());
		Assert.assertEquals("InadequateLogInfoInCatchBlock.java", bugPatterns.get(0).getFilename());
		Assert.assertEquals("InadequateMain", bugPatterns.get(0).getFunctionName());
		Assert.assertEquals(10, bugPatterns.get(0).getLine());
		
	}
	
	@Test
	public void testEqualsWithArgumentChecker() {
		List<BugPattern> bugPatterns = new InadequateLogInfoInCatchBlockChecker().check(new File("filesToParse/filesToParseTest/EqualsWithArgument.java"));
		Assert.assertEquals(0, bugPatterns.size());
	}
	
	@Test
	public void testEqualsWithoutNullArgumentChecker() {
		List<BugPattern> bugPatterns = new EqualsWithoutNullArgumentChecker().check(new File("filesToParse/EqualsWithoutNullArgument.java"));
		Assert.assertEquals(1, bugPatterns.size());
		Assert.assertEquals("EqualsWithoutNullArgument.java", bugPatterns.get(0).getFilename());
		Assert.assertEquals("equals", bugPatterns.get(0).getFunctionName());
		Assert.assertEquals(5, bugPatterns.get(0).getLine());
	}
	
}
