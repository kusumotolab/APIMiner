package apiminer.internal.analysis;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import apiminer.Result;
import apiminer.internal.refactor.RefactorProcessor;
import apiminer.internal.visitor.APIVersion;
import apiminer.internal.refactor.RefactoringProcessorImpl;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class DiffProcessorImpl implements DiffProcessor {

	@Override
	public Result detectChange(final APIVersion version1, final APIVersion version2, final Repository repository, final RevCommit revCommit) {
		Result result = new Result();
		Map<RefactoringType, List<Refactoring>> refactorings = this.detectRefactoring(repository, revCommit.getId().getName());
		result.getChangeType().addAll(new TypeDiff().detectChange(version1, version2, refactorings, revCommit));
		result.getChangeMethod().addAll(new MethodDiff().detectChange(version1, version2, refactorings, revCommit));
		result.getChangeField().addAll(new FieldDiff().detectChange(version1, version2, refactorings, revCommit));
		return result;
	}

	@Override
	public Map<RefactoringType, List<Refactoring>> detectRefactoring(Repository repository, String commit) {
		RefactorProcessor refactoringDetector = new RefactoringProcessorImpl();
		try {
			return refactoringDetector.detectRefactoringAtCommit(repository, commit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
