package javaparserBugDetector.Interfaces;

import java.io.File;
import java.util.List;

import javaparserBugDetector.BugPattern.BugPattern;


public interface IChecker {
    List<BugPattern> check(File projectDir);

}
