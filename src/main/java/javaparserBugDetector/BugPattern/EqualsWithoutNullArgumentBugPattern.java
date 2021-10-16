package javaparserBugDetector.BugPattern;

import java.io.File;

public class EqualsWithoutNullArgumentBugPattern extends BugPattern {
	public EqualsWithoutNullArgumentBugPattern(int line, File file, String functionName) {
        super(line, file, functionName);
    }

    @Override
    public String getIdentifier() {
        return "NP";
    }

    @Override
    public String getName() {
        return "Equals";
    }

    @Override
    public String getDescription() {
        return "Equals() method does not check for null argument";
    }
}
