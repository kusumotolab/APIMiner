package apiminer.internal.refactor;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;


public interface RefactorProcessor {
	
	public Map<RefactoringType, List<Refactoring>> detectRefactoringAtCommit (final Repository repository, final String commit) throws Exception;

}
