package apiminer.internal.analysis;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import apiminer.Result;
import apiminer.internal.visitor.APIVersion;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;


public interface DiffProcessor {
	
	public Map<RefactoringType, List<Refactoring>> detectRefactoring(final Repository repository, final String commit);
	
	public Result detectChange(final APIVersion version1, final APIVersion version2, final Repository repository, final RevCommit revCommit);

}
