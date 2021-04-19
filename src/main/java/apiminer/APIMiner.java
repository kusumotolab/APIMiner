package apiminer;

import apiminer.enums.Classifier;
import apiminer.internal.analysis.DiffProcessor;
import apiminer.internal.service.git.GitService;
import apiminer.internal.service.git.GitServiceImpl;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class APIMiner implements DiffDetector{

    private String nameProject;

    private String path;

    private String url;

    private Logger logger = LoggerFactory.getLogger(APIMiner.class);

    public APIMiner(final String nameProject, final String url) {
        this.url = url;
        this.nameProject = nameProject;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public Result detectChangeAtCommit(String commitId, Classifier classifierAPI) {
        Result result = new Result();
        try {
            GitService service = new GitServiceImpl();
            Repository repository = service.openRepositoryAndCloneIfNotExists(this.path, this.nameProject, this.url);
            RevCommit commit = service.createRevCommitByCommitId(repository, commitId);
            Result resultByClassifier = this.diffCommit(commit, repository, classifierAPI);
            result.getChangeType().addAll(resultByClassifier.getChangeType());
            result.getChangeMethod().addAll(resultByClassifier.getChangeMethod());
            result.getChangeField().addAll(resultByClassifier.getChangeField());
        } catch (Exception e) {
            this.logger.error("Error in calculating commit in diff ", e);
        }
        this.logger.info("Finished processing.");
        return result;
    }

    @Override
    public Result detectChangeAllHistory(String branch, List<Classifier> classifiers) throws Exception {
        Result result = new Result();
        GitService service = new GitServiceImpl();
        Repository repository = service.openRepositoryAndCloneIfNotExists(this.path, this.nameProject, this.url);
        RevWalk revWalk = service.createAllRevsWalk(repository, branch);
        //Commits.
        for (RevCommit currentCommit : revWalk) {
            for (Classifier classifierAPI : classifiers) {
                Result resultByClassifier = this.diffCommit(currentCommit, repository, classifierAPI);
                result.getChangeType().addAll(resultByClassifier.getChangeType());
                result.getChangeMethod().addAll(resultByClassifier.getChangeMethod());
                result.getChangeField().addAll(resultByClassifier.getChangeField());
            }
        }
        this.logger.info("Finished processing.");
        return result;
    }

    @Override
    public Result detectChangeAllHistory(List<Classifier> classifiers) throws Exception {
        return this.detectChangeAllHistory(null, classifiers);
    }

    @Override
    public Result fetchAndDetectChange(List<Classifier> classifiers) {
        Result result = new Result();
        try {
            GitService service = new GitServiceImpl();
            Repository repository = service.openRepositoryAndCloneIfNotExists(this.path, this.nameProject, this.url);
            RevWalk revWalk = service.fetchAndCreateNewRevsWalk(repository, null);
            //Commits.
            for (RevCommit currentCommit : revWalk) {
                for (Classifier classifierAPI : classifiers) {
                    Result resultByClassifier = this.diffCommit(currentCommit, repository, classifierAPI);
                    result.getChangeType().addAll(resultByClassifier.getChangeType());
                    result.getChangeMethod().addAll(resultByClassifier.getChangeMethod());
                    result.getChangeField().addAll(resultByClassifier.getChangeField());
                }
            }
        } catch (Exception e) {
            this.logger.error("Error in calculating commit diff ", e);
        }

        this.logger.info("Finished processing.");
        return result;
    }

    @Override
    public Result detectChangeAllHistory(String branch, Classifier classifier) throws Exception {
        return this.detectChangeAllHistory(branch, Arrays.asList(classifier));
    }

    @Override
    public Result detectChangeAllHistory(Classifier classifier) throws Exception {
        return this.detectChangeAllHistory(Arrays.asList(classifier));
    }

    @Override
    public Result fetchAndDetectChange(Classifier classifier) {
        return this.fetchAndDetectChange(Arrays.asList(classifier));
    }

    private Result diffCommit(final RevCommit currentCommit, final Repository repository, Classifier classifierAPI) {
        if(currentCommit.getParentCount() != 0){//there is at least one parent
            try {
                DiffProcessor diffProcessor = new DiffProcessor();
                return diffProcessor.detectChange(repository,currentCommit,classifierAPI);
            } catch (Exception e) {
                this.logger.error("Error during checkout [commit=" + currentCommit + "]");
            }
        }
        return new Result();
    }
}
