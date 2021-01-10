package apiminer.internal.service.git;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public interface GitService {

	Repository openRepositoryAndCloneIfNotExists(String path, String projectName, String cloneUrl) throws Exception;
	
	RevWalk fetchAndCreateNewRevsWalk(Repository repository, String branch) throws Exception;
	
	Map<ChangeType, List<GitFile>> fileTreeDiff(Repository repository, RevCommit commitNew) throws Exception;
	
	Integer countCommits(Repository repository, String branch) throws Exception ;
	
	void checkout(Repository repository, String commitId) throws Exception;
	
	RevCommit createRevCommitByCommitId(final Repository repository, final String commitId) throws Exception;
	
	public RevWalk createAllRevsWalk(Repository repository, String branch) throws Exception;
}
