package apiminer.internal.service.git;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apiminer.internal.util.UtilTools;

public class GitServiceImpl implements GitService {
	
	private static final String REMOTE_REFS_PREFIX = "refs/remotes/origin/";
	
	private RevFilterCommitValid commitsFilter = new RevFilterCommitValid();
	
	private Logger logger = LoggerFactory.getLogger(GitServiceImpl.class);
	
	private static final Long MINUTE = 60000L; //60.000 miliseconds
	private static final Long HOUR = MINUTE * 60;
	private static final Long DAY = HOUR * 24;
	private static final Long SEVEN_DAYS = DAY * 7; //7 dias em milissegundos.
	
	private class RevFilterCommitValid extends RevFilter {
		
		@Override
		public final boolean include(final RevWalk walker, final RevCommit c) {
			
			Long diffTimestamp = 0L;
			diffTimestamp = this.calcDiffTimeCommit(c);
			
			if(c.getParentCount() > 1){//merge
				logger.info("Merge of the branches deleted. [commitId=" + c.getId().getName() + "]");
				return false;
			}
				
			//TODO: create other filter to date.
//			if(diffTimestamp > SEVEN_DAYS){//old
//				logger.info("Old commit old deleted. [commitId=" + c.getId().getName() + "][date=" + getDateCommitFormat(c) + "]");
//				return false;
//			}
			
			return true;
		}
		
		private Long calcDiffTimeCommit(final RevCommit c){
			Long timestampNow = Calendar.getInstance().getTimeInMillis();
			Long timestampCommit = c.getAuthorIdent().getWhen().getTime();
			Calendar calendarCommit = Calendar.getInstance();
			calendarCommit.setTime(new Date(timestampCommit));
			return Math.abs(timestampCommit - timestampNow);
		}
		
		private String getDateCommitFormat(final RevCommit c){
			Long timestampCommit = c.getAuthorIdent().getWhen().getTime();
			Calendar calendarCommit = Calendar.getInstance();
			calendarCommit.setTime(new Date(timestampCommit));
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			return format.format(timestampCommit);
		}

		@Override
		public final RevFilter clone() {
			return this;
		}

		@Override
		public final boolean requiresCommitBody() {
			return false;
		}

		@Override
		public String toString() {
			return "RegularCommitsFilter";
		}
	}
	
	@Override
	public Repository openRepositoryAndCloneIfNotExists(String path, String projectName, String cloneUrl) throws Exception {
		File folder = new File(UtilTools.getPathProject(path , projectName));
		Repository repository = null;
		
		if (folder.exists()) {
			this.logger.info(projectName + " exists. Reading properties ... (wait)");
			RepositoryBuilder builder = new RepositoryBuilder();
			repository = builder
					.setGitDir(new File(folder, ".git"))
					.readEnvironment()
					.findGitDir()
					.build();
			
		} else {
			this.logger.info("Cloning " + cloneUrl  + " in " + cloneUrl + " ... (wait)");
			Git git = Git.cloneRepository()
					.setDirectory(folder)
					.setURI(cloneUrl)
					.setCloneAllBranches(true)
					.call();
			repository = git.getRepository();
		}
		this.logger.info("Process " + projectName  + " finish.");
		return repository;
	}
	
	@Override
	public RevWalk fetchAndCreateNewRevsWalk(Repository repository, String branch) throws Exception {
		List<ObjectId> currentRemoteRefs = new ArrayList<ObjectId>(); 
		for (Ref ref : repository.getRefDatabase().getRefs()) {
			String refName = ref.getName();
			if (refName.startsWith(REMOTE_REFS_PREFIX)) {
				currentRemoteRefs.add(ref.getObjectId());
			}
		}
		
		List<TrackingRefUpdate> newRemoteRefs = this.fetch(repository);
		
		RevWalk walk = new RevWalk(repository);
		for (TrackingRefUpdate newRef : newRemoteRefs) {
			if (branch == null || newRef.getLocalName().endsWith("/" + branch)) {
				walk.markStart(walk.parseCommit(newRef.getNewObjectId()));
			}
		}
		for (ObjectId oldRef : currentRemoteRefs) {
			walk.markUninteresting(walk.parseCommit(oldRef));
		}
		walk.setRevFilter(commitsFilter);
		return walk;
	}
	
	public RevWalk createAllRevsWalk(Repository repository, String branch) throws Exception {
		List<ObjectId> currentRemoteRefs = new ArrayList<ObjectId>(); 
		for (Ref ref : repository.getRefDatabase().getRefs() ) {
			String refName = ref.getName();
			if (refName.startsWith(REMOTE_REFS_PREFIX)) {
				if (branch == null || refName.endsWith("/" + branch)) {
					currentRemoteRefs.add(ref.getObjectId());
				}
			}
		}
		
		RevWalk walk = new RevWalk(repository);
		for (ObjectId newRef : currentRemoteRefs) {
			walk.markStart(walk.parseCommit(newRef));
		}
		walk.setRevFilter(commitsFilter);
		return walk;
	}
	
	private List<TrackingRefUpdate> fetch(Repository repository) throws Exception {
		this.logger.info("Fetching changes of repository " + repository.getDirectory().toString());
        try (Git git = new Git(repository)) {
    		FetchResult result = git.fetch().call();
    		
    		Collection<TrackingRefUpdate> updates = result.getTrackingRefUpdates();
    		List<TrackingRefUpdate> remoteRefsChanges = new ArrayList<TrackingRefUpdate>();
    		for (TrackingRefUpdate update : updates) {
    			String refName = update.getLocalName();
    			if (refName.startsWith(REMOTE_REFS_PREFIX)) {
    				ObjectId newObjectId = update.getNewObjectId();
    				this.logger.info(refName +" is now at " + newObjectId.getName());
    				remoteRefsChanges.add(update);
    			}
    		}
    		if (updates.isEmpty()) {
    			this.logger.info("Nothing changed");
    		}
    		return remoteRefsChanges;
        }
	}
	
	@Override
	public Integer countCommits(Repository repository, String branch) throws Exception {
		RevWalk walk = new RevWalk(repository);
		try {
			Ref ref = repository.findRef(REMOTE_REFS_PREFIX + branch);
			ObjectId objectId = ref.getObjectId();
			RevCommit start = walk.parseCommit(objectId);
			walk.setRevFilter(RevFilter.NO_MERGES);
			return RevWalkUtils.count(walk, start, null);
		} finally {
			walk.dispose();
		}
	}
	
	@Override
	public Map<ChangeType, List<GitFile>> fileTreeDiff(Repository repository, RevCommit commitNew) throws Exception {
	       
		Map<ChangeType, List<GitFile>> mapDiff = new HashMap<ChangeType, List<GitFile>>();
		mapDiff.put(ChangeType.ADD, new ArrayList<>());
		mapDiff.put(ChangeType.COPY, new ArrayList<>());
		mapDiff.put(ChangeType.DELETE, new ArrayList<>());
		mapDiff.put(ChangeType.MODIFY, new ArrayList<>());
		mapDiff.put(ChangeType.RENAME, new ArrayList<>());
		
		if(commitNew.getParentCount() == 0){
			this.logger.warn("Commit don't have parent [commitId="+commitNew.getId().getName()+"]");
			return mapDiff;
		}
       
		ObjectId headOld = commitNew.getParent(0).getTree(); //Commit pai no grafo.
		ObjectId headNew = commitNew.getTree(); //Commit corrente.

        // prepare the two iterators to compute the diff between
		ObjectReader reader = repository.newObjectReader();
		
		CanonicalTreeParser treeRepositoryOld = new CanonicalTreeParser();
		treeRepositoryOld.reset(reader, headOld);
		
		CanonicalTreeParser treeRepositoryNew = new CanonicalTreeParser();
		treeRepositoryNew.reset(reader, headNew);
		
		// finally get the list of changed files
		List<DiffEntry> diffs = new Git(repository).diff()
		                    .setNewTree(treeRepositoryNew)
		                    .setOldTree(treeRepositoryOld)
		                    .setShowNameAndStatusOnly(true)
		                    .call();
		
        for (DiffEntry entry : diffs) {
        	if(UtilTools.isJavaFile(entry.getOldPath()) || UtilTools.isJavaFile(entry.getNewPath())) {
        		String pathNew =  "/dev/null".equals(entry.getNewPath())?null:entry.getNewPath();
        		String pathOld =  "/dev/null".equals(entry.getOldPath())?null:entry.getOldPath();
        		GitFile file = new GitFile(pathOld, pathNew, entry.getChangeType());
        		mapDiff.get(entry.getChangeType()).add(file);
        	}
        }
        return mapDiff;
	}
	
	@Override
	public void checkout(Repository repository, String commitId) throws Exception {
	    this.logger.info("Checking out {} {} ...", repository.getDirectory().getParent().toString(), commitId);
	    try (Git git = new Git(repository)) {
	        CheckoutCommand checkout = git.checkout().setName(commitId);
	        checkout.call();
	    }
	}
	
	@Override
	public RevCommit createRevCommitByCommitId(final Repository repository, final String commitId) throws Exception{
		RevWalk walk = new RevWalk(repository);
		RevCommit commit = walk.parseCommit(repository.resolve(commitId));
		walk.parseCommit(commit.getParent(0));
		return commit;
	}
}
