package javaparserBugDetector.BugPattern;

import java.io.File;

public class HashcodeWithoutEqualsBugPattern extends BugPattern {
    public HashcodeWithoutEqualsBugPattern(int line, File file, String functionName) {
        super(line, file, functionName);
    }

    @Override
    public String getIdentifier() {
        return "HE";
    }

    @Override
    public String getName() {
        return "Hashcode Equals";
    }

    @Override
    public String getDescription() {
        return "Class defines hashCode() but not equals()";
    }
}
