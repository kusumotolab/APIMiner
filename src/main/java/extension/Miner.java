//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLModelASTReader;
import gr.uom.java.xmi.diff.UMLClassBaseDiff;
import gr.uom.java.xmi.diff.UMLModelDiff;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositoryWrapper;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeEntry;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedIterator;
import org.refactoringminer.api.Churn;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringMinerTimedOutException;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Miner implements GitHistoryRefactoringMiner {
    Logger logger = LoggerFactory.getLogger(GitHistoryRefactoringMinerImpl.class);
    private Set<RefactoringType> refactoringTypesToConsider = null;
    private GitHub gitHub;
    private static final String systemFileSeparator;
    private static final String GITHUB_URL = "https://github.com/";
    private static final String BITBUCKET_URL = "https://bitbucket.org/";

    public Miner() {
        this.setRefactoringTypesToConsider(RefactoringType.ALL);
    }

    public void setRefactoringTypesToConsider(RefactoringType... types) {
        this.refactoringTypesToConsider = new HashSet();
        RefactoringType[] var2 = types;
        int var3 = types.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            RefactoringType type = var2[var4];
            this.refactoringTypesToConsider.add(type);
        }

    }

    private void detect(GitService gitService, Repository repository, RefactoringHandler handler, Iterator<RevCommit> i) {
        int commitsCount = 0;
        int errorCommitsCount = 0;
        int refactoringsCount = 0;
        File metadataFolder = repository.getDirectory();
        File projectFolder = metadataFolder.getParentFile();
        String projectName = projectFolder.getName();
        long time = System.currentTimeMillis();

        while(i.hasNext()) {
            RevCommit currentCommit = (RevCommit)i.next();

            try {
                List<Refactoring> refactoringsAtRevision = this.detectRefactorings(gitService, repository, handler, projectFolder, currentCommit);
                refactoringsCount += refactoringsAtRevision.size();
            } catch (Exception var16) {
                this.logger.warn(String.format("Ignored revision %s due to error", currentCommit.getId().getName()), var16);
                handler.handleException(currentCommit.getId().getName(), var16);
                ++errorCommitsCount;
            }

            ++commitsCount;
            long time2 = System.currentTimeMillis();
            if (time2 - time > 20000L) {
                time = time2;
                this.logger.info(String.format("Processing %s [Commits: %d, Errors: %d, Refactorings: %d]", projectName, commitsCount, errorCommitsCount, refactoringsCount));
            }
        }

        handler.onFinish(refactoringsCount, commitsCount, errorCommitsCount);
        this.logger.info(String.format("Analyzed %s [Commits: %d, Errors: %d, Refactorings: %d]", projectName, commitsCount, errorCommitsCount, refactoringsCount));
    }

    protected List<Refactoring> detectRefactorings(GitService gitService, Repository repository, RefactoringHandler handler, File projectFolder, RevCommit currentCommit) throws Exception {
        String commitId = currentCommit.getId().getName();
        List<String> filePathsBefore = new ArrayList();
        List<String> filePathsCurrent = new ArrayList();
        Map<String, String> renamedFilesHint = new HashMap();
        gitService.fileTreeDiff(repository, currentCommit, filePathsBefore, filePathsCurrent, renamedFilesHint);
        Set<String> repositoryDirectoriesBefore = new LinkedHashSet();
        Set<String> repositoryDirectoriesCurrent = new LinkedHashSet();
        Map<String, String> fileContentsBefore = new LinkedHashMap();
        Map<String, String> fileContentsCurrent = new LinkedHashMap();
        RevWalk walk = new RevWalk(repository);

        List refactoringsAtRevision;
        try {
            if (!filePathsBefore.isEmpty() && !filePathsCurrent.isEmpty() && currentCommit.getParentCount() > 0) {
                RevCommit parentCommit = currentCommit.getParent(0);
                this.populateFileContents(repository, parentCommit, filePathsBefore, fileContentsBefore, repositoryDirectoriesBefore);
                UMLModel parentUMLModel = this.createModel((Map)fileContentsBefore, (Set)repositoryDirectoriesBefore);
                this.populateFileContents(repository, currentCommit, filePathsCurrent, fileContentsCurrent, repositoryDirectoriesCurrent);
                UMLModel currentUMLModel = this.createModel((Map)fileContentsCurrent, (Set)repositoryDirectoriesCurrent);
                UMLModelDiff modelDiff = parentUMLModel.diff(currentUMLModel, renamedFilesHint);
                refactoringsAtRevision = modelDiff.getRefactorings();
                for(UMLClass currentUMLClass:currentUMLModel.getClassList()){
                    UMLClassBaseDiff umlClassBaseDiff = modelDiff.getUMLClassDiff(currentUMLClass.getName());
                    UMLClass removedClass = modelDiff.getRemovedClass(currentUMLClass.getName());
                    UMLClass addedClass = modelDiff.getAddedClass(currentUMLClass.getName());
                    if(addedClass!=null||removedClass!=null){
                        System.out.print("");
                    }
                }
                refactoringsAtRevision = this.filter(refactoringsAtRevision);
            } else {
                refactoringsAtRevision = Collections.emptyList();
            }

            handler.handle(commitId, refactoringsAtRevision);
            walk.dispose();
        } catch (Throwable var21) {
            try {
                walk.close();
            } catch (Throwable var20) {
                var21.addSuppressed(var20);
            }

            throw var21;
        }

        walk.close();
        return refactoringsAtRevision;
    }

    private void populateFileContents(Repository repository, RevCommit commit, List<String> filePaths, Map<String, String> fileContents, Set<String> repositoryDirectories) throws Exception {
        this.logger.info("Processing {} {} ...", repository.getDirectory().getParent().toString(), commit.getName());
        RevTree parentTree = commit.getTree();
        TreeWalk treeWalk = new TreeWalk(repository);

        try {
            treeWalk.addTree(parentTree);
            treeWalk.setRecursive(true);

            label46:
            while(true) {
                String pathString;
                do {
                    do {
                        if (!treeWalk.next()) {
                            break label46;
                        }

                        pathString = treeWalk.getPathString();
                        if (filePaths.contains(pathString)) {
                            ObjectId objectId = treeWalk.getObjectId(0);
                            ObjectLoader loader = repository.open(objectId);
                            StringWriter writer = new StringWriter();
                            IOUtils.copy(loader.openStream(), writer);
                            fileContents.put(pathString, writer.toString());
                        }
                    } while(!pathString.endsWith(".java"));
                } while(!pathString.contains("/"));

                String directory = pathString.substring(0, pathString.lastIndexOf("/"));
                repositoryDirectories.add(directory);
                String subDirectory = new String(directory);

                while(subDirectory.contains("/")) {
                    subDirectory = subDirectory.substring(0, subDirectory.lastIndexOf("/"));
                    repositoryDirectories.add(subDirectory);
                }
            }
        } catch (Throwable var13) {
            try {
                treeWalk.close();
            } catch (Throwable var12) {
                var13.addSuppressed(var12);
            }

            throw var13;
        }

        treeWalk.close();
    }

    protected List<Refactoring> detectRefactorings(RefactoringHandler handler, File projectFolder, String cloneURL, String currentCommitId) {
        List refactoringsAtRevision = Collections.emptyList();

        try {
            GitHistoryRefactoringMinerImpl.ChangedFileInfo changedFileInfo = this.populateWithGitHubAPI(projectFolder, cloneURL, currentCommitId);
            String parentCommitId = changedFileInfo.getParentCommitId();
            List<String> filesBefore = changedFileInfo.getFilesBefore();
            List<String> filesCurrent = changedFileInfo.getFilesCurrent();
            Map<String, String> renamedFilesHint = changedFileInfo.getRenamedFilesHint();
            File var10002 = projectFolder.getParentFile();
            String var10003 = projectFolder.getName();
            File currentFolder = new File(var10002, var10003 + "-" + currentCommitId);
            var10002 = projectFolder.getParentFile();
            var10003 = projectFolder.getName();
            File parentFolder = new File(var10002, var10003 + "-" + parentCommitId);
            if (!currentFolder.exists()) {
                this.downloadAndExtractZipFile(projectFolder, cloneURL, currentCommitId);
            }

            if (!parentFolder.exists()) {
                this.downloadAndExtractZipFile(projectFolder, cloneURL, parentCommitId);
            }

            if (currentFolder.exists() && parentFolder.exists()) {
                UMLModel currentUMLModel = this.createModel(currentFolder, filesCurrent);
                UMLModel parentUMLModel = this.createModel(parentFolder, filesBefore);
                UMLModelDiff modelDiff = parentUMLModel.diff(currentUMLModel, renamedFilesHint);
                refactoringsAtRevision = modelDiff.getRefactorings();
                refactoringsAtRevision = this.filter(refactoringsAtRevision);
            } else {
                this.logger.warn(String.format("Folder %s not found", currentFolder.getPath()));
            }
        } catch (Exception var16) {
            this.logger.warn(String.format("Ignored revision %s due to error", currentCommitId), var16);
            handler.handleException(currentCommitId, var16);
        }

        handler.handle(currentCommitId, refactoringsAtRevision);
        return refactoringsAtRevision;
    }

    private void downloadAndExtractZipFile(File projectFolder, String cloneURL, String commitId) throws IOException {
        String downloadLink = extractDownloadLink(cloneURL, commitId);
        File var10002 = projectFolder.getParentFile();
        String var10003 = projectFolder.getName();
        File destinationFile = new File(var10002, var10003 + "-" + commitId + ".zip");
        this.logger.info(String.format("Downloading archive %s", downloadLink));
        FileUtils.copyURLToFile(new URL(downloadLink), destinationFile);
        this.logger.info(String.format("Unzipping archive %s", downloadLink));
        ZipFile zipFile = new ZipFile(destinationFile);

        try {
            Enumeration entries = zipFile.entries();

            while(entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                File entryDestination = new File(projectFolder.getParentFile(), entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    out.close();
                }
            }
        } finally {
            zipFile.close();
        }

    }

    private GitHistoryRefactoringMinerImpl.ChangedFileInfo populateWithGitHubAPI(File projectFolder, String cloneURL, String currentCommitId) throws IOException {
        this.logger.info("Processing {} {} ...", cloneURL, currentCommitId);
        String var10000 = projectFolder.getName();
        String jsonFilePath = var10000 + "-" + currentCommitId + ".json";
        File jsonFile = new File(projectFolder.getParent(), jsonFilePath);
        if (jsonFile.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            GitHistoryRefactoringMinerImpl.ChangedFileInfo changedFileInfo = (GitHistoryRefactoringMinerImpl.ChangedFileInfo)mapper.readValue(jsonFile, GitHistoryRefactoringMinerImpl.ChangedFileInfo.class);
            return changedFileInfo;
        } else {
            GHRepository repository = this.getGitHubRepository(cloneURL);
            List<org.kohsuke.github.GHCommit.File> commitFiles = new ArrayList();
            GHCommit commit = (new GHRepositoryWrapper(repository)).getCommit(currentCommitId, commitFiles);
            String parentCommitId = ((GHCommit)commit.getParents().get(0)).getSHA1();
            List<String> filesBefore = new ArrayList();
            List<String> filesCurrent = new ArrayList();
            Map<String, String> renamedFilesHint = new HashMap();
            Iterator var13 = commitFiles.iterator();

            while(var13.hasNext()) {
                org.kohsuke.github.GHCommit.File commitFile = (org.kohsuke.github.GHCommit.File)var13.next();
                if (commitFile.getFileName().endsWith(".java")) {
                    if (commitFile.getStatus().equals("modified")) {
                        filesBefore.add(commitFile.getFileName());
                        filesCurrent.add(commitFile.getFileName());
                    } else if (commitFile.getStatus().equals("added")) {
                        filesCurrent.add(commitFile.getFileName());
                    } else if (commitFile.getStatus().equals("removed")) {
                        filesBefore.add(commitFile.getFileName());
                    } else if (commitFile.getStatus().equals("renamed")) {
                        filesBefore.add(commitFile.getPreviousFilename());
                        filesCurrent.add(commitFile.getFileName());
                        renamedFilesHint.put(commitFile.getPreviousFilename(), commitFile.getFileName());
                    }
                }
            }

            GitHistoryRefactoringMinerImpl.ChangedFileInfo changedFileInfo = new GitHistoryRefactoringMinerImpl.ChangedFileInfo(parentCommitId, filesBefore, filesCurrent, renamedFilesHint);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(jsonFile, changedFileInfo);
            return changedFileInfo;
        }
    }

    private GitHub connectToGitHub() {
        if (this.gitHub == null) {
            try {
                Properties prop = new Properties();
                InputStream input = new FileInputStream("github-oauth.properties");
                prop.load(input);
                String oAuthToken = prop.getProperty("OAuthToken");
                if (oAuthToken != null) {
                    this.gitHub = GitHub.connectUsingOAuth(oAuthToken);
                    if (this.gitHub.isCredentialValid()) {
                        this.logger.info("Connected to GitHub with OAuth token");
                    }
                } else {
                    this.gitHub = GitHub.connect();
                }
            } catch (FileNotFoundException var4) {
                this.logger.warn("File github-oauth.properties was not found in RefactoringMiner's execution directory", var4);
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

        return this.gitHub;
    }

    protected List<Refactoring> filter(List<Refactoring> refactoringsAtRevision) {
        if (this.refactoringTypesToConsider == null) {
            return refactoringsAtRevision;
        } else {
            List<Refactoring> filteredList = new ArrayList();
            Iterator var3 = refactoringsAtRevision.iterator();

            while(var3.hasNext()) {
                Refactoring ref = (Refactoring)var3.next();
                if (this.refactoringTypesToConsider.contains(ref.getRefactoringType())) {
                    filteredList.add(ref);
                }
            }

            return filteredList;
        }
    }

    public void detectAll(Repository repository, String branch, final RefactoringHandler handler) throws Exception {
        GitService gitService = new GitServiceImpl() {
            public boolean isCommitAnalyzed(String sha1) {
                return handler.skipCommit(sha1);
            }
        };
        RevWalk walk = gitService.createAllRevsWalk(repository, branch);

        try {
            this.detect(gitService, repository, handler, walk.iterator());
        } finally {
            walk.dispose();
        }

    }

    public void fetchAndDetectNew(Repository repository, final RefactoringHandler handler) throws Exception {
        GitService gitService = new GitServiceImpl() {
            public boolean isCommitAnalyzed(String sha1) {
                return handler.skipCommit(sha1);
            }
        };
        RevWalk walk = gitService.fetchAndCreateNewRevsWalk(repository);

        try {
            this.detect(gitService, repository, handler, walk.iterator());
        } finally {
            walk.dispose();
        }

    }

    protected UMLModel createModel(Map<String, String> fileContents, Set<String> repositoryDirectories) throws Exception {
        return (new UMLModelASTReader(fileContents, repositoryDirectories)).getUmlModel();
    }

    protected UMLModel createModel(File projectFolder, List<String> filePaths) throws Exception {
        Map<String, String> fileContents = new LinkedHashMap();
        Set<String> repositoryDirectories = new LinkedHashSet();
        Iterator var5 = filePaths.iterator();

        while(var5.hasNext()) {
            String path = (String)var5.next();
            String fullPath = projectFolder + File.separator + path.replaceAll("/", systemFileSeparator);
            String contents = FileUtils.readFileToString(new File(fullPath));
            fileContents.put(path, contents);
            String directory = new String(path);

            while(directory.contains("/")) {
                directory = directory.substring(0, directory.lastIndexOf("/"));
                repositoryDirectories.add(directory);
            }
        }

        return (new UMLModelASTReader(fileContents, repositoryDirectories)).getUmlModel();
    }

    public void detectAtCommit(Repository repository, String commitId, RefactoringHandler handler) {
        String cloneURL = repository.getConfig().getString("remote", "origin", "url");
        File metadataFolder = repository.getDirectory();
        File projectFolder = metadataFolder.getParentFile();
        GitService gitService = new GitServiceImpl();
        RevWalk walk = new RevWalk(repository);

        try {
            RevCommit commit = walk.parseCommit(repository.resolve(commitId));
            if (commit.getParentCount() > 0) {
                walk.parseCommit(commit.getParent(0));
                this.detectRefactorings(gitService, repository, handler, projectFolder, commit);
            } else {
                this.logger.warn(String.format("Ignored revision %s because it has no parent", commitId));
            }
        } catch (MissingObjectException var15) {
            this.detectRefactorings(handler, projectFolder, cloneURL, commitId);
        } catch (RefactoringMinerTimedOutException var16) {
            this.logger.warn(String.format("Ignored revision %s due to timeout", commitId), var16);
        } catch (Exception var17) {
            this.logger.warn(String.format("Ignored revision %s due to error", commitId), var17);
            handler.handleException(commitId, var17);
        } finally {
            walk.close();
            walk.dispose();
        }

    }

    public void detectAtCommit(Repository repository, String commitId, RefactoringHandler handler, int timeout) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future f = null;

        try {
            Runnable r = () -> {
                this.detectAtCommit(repository, commitId, handler);
            };
            f = service.submit(r);
            f.get((long)timeout, TimeUnit.SECONDS);
        } catch (TimeoutException var13) {
            f.cancel(true);
        } catch (ExecutionException var14) {
            var14.printStackTrace();
        } catch (InterruptedException var15) {
            var15.printStackTrace();
        } finally {
            service.shutdown();
        }

    }

    public String getConfigId() {
        return "RM1";
    }

    public void detectBetweenTags(Repository repository, String startTag, String endTag, final RefactoringHandler handler) throws Exception {
        GitService gitService = new GitServiceImpl() {
            public boolean isCommitAnalyzed(String sha1) {
                return handler.skipCommit(sha1);
            }
        };
        Iterable<RevCommit> walk = gitService.createRevsWalkBetweenTags(repository, startTag, endTag);
        this.detect(gitService, repository, handler, walk.iterator());
    }

    public void detectBetweenCommits(Repository repository, String startCommitId, String endCommitId, final RefactoringHandler handler) throws Exception {
        GitService gitService = new GitServiceImpl() {
            public boolean isCommitAnalyzed(String sha1) {
                return handler.skipCommit(sha1);
            }
        };
        Iterable<RevCommit> walk = gitService.createRevsWalkBetweenCommits(repository, startCommitId, endCommitId);
        this.detect(gitService, repository, handler, walk.iterator());
    }

    public Churn churnAtCommit(Repository repository, String commitId, RefactoringHandler handler) {
        GitService gitService = new GitServiceImpl();
        RevWalk walk = new RevWalk(repository);

        try {
            RevCommit commit = walk.parseCommit(repository.resolve(commitId));
            if (commit.getParentCount() > 0) {
                walk.parseCommit(commit.getParent(0));
                Churn var7 = gitService.churn(repository, commit);
                return var7;
            }

            this.logger.warn(String.format("Ignored revision %s because it has no parent", commitId));
        } catch (MissingObjectException var12) {
            this.logger.warn(String.format("Ignored revision %s due to missing commit", commitId), var12);
        } catch (Exception var13) {
            this.logger.warn(String.format("Ignored revision %s due to error", commitId), var13);
            handler.handleException(commitId, var13);
        } finally {
            walk.close();
            walk.dispose();
        }

        return null;
    }

    public void detectAtCommit(String gitURL, String commitId, RefactoringHandler handler, int timeout) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future f = null;

        try {
            Runnable r = () -> {
                this.detectRefactorings(handler, gitURL, commitId);
            };
            f = service.submit(r);
            f.get((long)timeout, TimeUnit.SECONDS);
        } catch (TimeoutException var13) {
            f.cancel(true);
        } catch (ExecutionException var14) {
            var14.printStackTrace();
        } catch (InterruptedException var15) {
            var15.printStackTrace();
        } finally {
            service.shutdown();
        }

    }

    protected List<Refactoring> detectRefactorings(RefactoringHandler handler, String gitURL, String currentCommitId) {
        List refactoringsAtRevision = Collections.emptyList();

        try {
            Set<String> repositoryDirectoriesBefore = ConcurrentHashMap.newKeySet();
            Set<String> repositoryDirectoriesCurrent = ConcurrentHashMap.newKeySet();
            Map<String, String> fileContentsBefore = new ConcurrentHashMap();
            Map<String, String> fileContentsCurrent = new ConcurrentHashMap();
            Map<String, String> renamedFilesHint = new ConcurrentHashMap();
            this.populateWithGitHubAPI(gitURL, currentCommitId, fileContentsBefore, fileContentsCurrent, renamedFilesHint, repositoryDirectoriesBefore, repositoryDirectoriesCurrent);
            UMLModel currentUMLModel = this.createModel((Map)fileContentsCurrent, (Set)repositoryDirectoriesCurrent);
            UMLModel parentUMLModel = this.createModel((Map)fileContentsBefore, (Set)repositoryDirectoriesBefore);
            UMLModelDiff modelDiff = parentUMLModel.diff(currentUMLModel, renamedFilesHint);
            refactoringsAtRevision = modelDiff.getRefactorings();
            refactoringsAtRevision = this.filter(refactoringsAtRevision);
        } catch (RefactoringMinerTimedOutException var13) {
            this.logger.warn(String.format("Ignored revision %s due to timeout", currentCommitId), var13);
            handler.handleException(currentCommitId, var13);
        } catch (Exception var14) {
            this.logger.warn(String.format("Ignored revision %s due to error", currentCommitId), var14);
            handler.handleException(currentCommitId, var14);
        }

        handler.handle(currentCommitId, refactoringsAtRevision);
        return refactoringsAtRevision;
    }

    private void populateWithGitHubAPI(String cloneURL, String currentCommitId, Map<String, String> filesBefore, Map<String, String> filesCurrent, Map<String, String> renamedFilesHint, Set<String> repositoryDirectoriesBefore, Set<String> repositoryDirectoriesCurrent) throws IOException, InterruptedException {
        this.logger.info("Processing {} {} ...", cloneURL, currentCommitId);
        GHRepository repository = this.getGitHubRepository(cloneURL);
        List<org.kohsuke.github.GHCommit.File> commitFiles = new ArrayList();
        GHCommit currentCommit = (new GHRepositoryWrapper(repository)).getCommit(currentCommitId, commitFiles);
        String parentCommitId = ((GHCommit)currentCommit.getParents().get(0)).getSHA1();
        Set<String> deletedAndRenamedFileParentDirectories = ConcurrentHashMap.newKeySet();
        ExecutorService pool = Executors.newFixedThreadPool(commitFiles.size());
        Iterator var14 = commitFiles.iterator();

        while(var14.hasNext()) {
            org.kohsuke.github.GHCommit.File commitFile = (org.kohsuke.github.GHCommit.File)var14.next();
            String fileName = commitFile.getFileName();
            if (commitFile.getFileName().endsWith(".java")) {
                Runnable r;
                if (commitFile.getStatus().equals("modified")) {
                    r = () -> {
                        try {
                            URL currentRawURL = commitFile.getRawUrl();
                            InputStream currentRawFileInputStream = currentRawURL.openStream();
                            String currentRawFile = IOUtils.toString(currentRawFileInputStream);
                            String rawURLInParentCommit = currentRawURL.toString().replace(currentCommitId, parentCommitId);
                            InputStream parentRawFileInputStream = (new URL(rawURLInParentCommit)).openStream();
                            String parentRawFile = IOUtils.toString(parentRawFileInputStream);
                            filesBefore.put(fileName, parentRawFile);
                            filesCurrent.put(fileName, currentRawFile);
                        } catch (IOException var12) {
                            var12.printStackTrace();
                        }

                    };
                    pool.submit(r);
                } else if (commitFile.getStatus().equals("added")) {
                    r = () -> {
                        try {
                            URL currentRawURL = commitFile.getRawUrl();
                            InputStream currentRawFileInputStream = currentRawURL.openStream();
                            String currentRawFile = IOUtils.toString(currentRawFileInputStream);
                            filesCurrent.put(fileName, currentRawFile);
                        } catch (IOException var6) {
                            var6.printStackTrace();
                        }

                    };
                    pool.submit(r);
                } else if (commitFile.getStatus().equals("removed")) {
                    r = () -> {
                        try {
                            URL rawURL = commitFile.getRawUrl();
                            InputStream rawFileInputStream = rawURL.openStream();
                            String rawFile = IOUtils.toString(rawFileInputStream);
                            filesBefore.put(fileName, rawFile);
                            if (fileName.contains("/")) {
                                deletedAndRenamedFileParentDirectories.add(fileName.substring(0, fileName.lastIndexOf("/")));
                            }
                        } catch (IOException var7) {
                            var7.printStackTrace();
                        }

                    };
                    pool.submit(r);
                } else if (commitFile.getStatus().equals("renamed")) {
                    r = () -> {
                        try {
                            String previousFilename = commitFile.getPreviousFilename();
                            URL currentRawURL = commitFile.getRawUrl();
                            InputStream currentRawFileInputStream = currentRawURL.openStream();
                            String currentRawFile = IOUtils.toString(currentRawFileInputStream);
                            String rawURLInParentCommit = currentRawURL.toString().replace(currentCommitId, parentCommitId).replace(fileName, previousFilename);
                            InputStream parentRawFileInputStream = (new URL(rawURLInParentCommit)).openStream();
                            String parentRawFile = IOUtils.toString(parentRawFileInputStream);
                            filesBefore.put(previousFilename, parentRawFile);
                            filesCurrent.put(fileName, currentRawFile);
                            renamedFilesHint.put(previousFilename, fileName);
                            if (previousFilename.contains("/")) {
                                deletedAndRenamedFileParentDirectories.add(previousFilename.substring(0, previousFilename.lastIndexOf("/")));
                            }
                        } catch (IOException var15) {
                            var15.printStackTrace();
                        }

                    };
                    pool.submit(r);
                }
            }
        }

        pool.shutdown();
        pool.awaitTermination(9223372036854775807L, TimeUnit.MILLISECONDS);
        this.repositoryDirectories(currentCommit.getTree(), "", repositoryDirectoriesCurrent, deletedAndRenamedFileParentDirectories);
        repositoryDirectoriesCurrent.addAll(deletedAndRenamedFileParentDirectories);
    }

    private void repositoryDirectories(GHTree tree, String pathFromRoot, Set<String> repositoryDirectories, Set<String> targetPaths) throws IOException {
        Iterator var5 = tree.getTree().iterator();

        while(var5.hasNext()) {
            GHTreeEntry entry = (GHTreeEntry)var5.next();
            String path = null;
            if (pathFromRoot.equals("")) {
                path = entry.getPath();
            } else {
                path = pathFromRoot + "/" + entry.getPath();
            }

            if (this.atLeastOneStartsWith(targetPaths, path)) {
                if (targetPaths.contains(path)) {
                    repositoryDirectories.add(path);
                } else {
                    repositoryDirectories.add(path);
                    GHTree asTree = entry.asTree();
                    if (asTree != null) {
                        this.repositoryDirectories(asTree, path, repositoryDirectories, targetPaths);
                    }
                }
            }
        }

    }

    private boolean atLeastOneStartsWith(Set<String> targetPaths, String path) {
        Iterator var3 = targetPaths.iterator();

        String targetPath;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            targetPath = (String)var3.next();
            if (path.endsWith("/") && targetPath.startsWith(path)) {
                return true;
            }
        } while(path.endsWith("/") || !targetPath.startsWith(path + "/"));

        return true;
    }

    public void detectAtPullRequest(String cloneURL, int pullRequestId, RefactoringHandler handler, int timeout) throws IOException {
        GHRepository repository = this.getGitHubRepository(cloneURL);
        GHPullRequest pullRequest = repository.getPullRequest(pullRequestId);
        PagedIterable<GHPullRequestCommitDetail> commits = pullRequest.listCommits();
        PagedIterator var8 = commits.iterator();

        while(var8.hasNext()) {
            GHPullRequestCommitDetail commit = (GHPullRequestCommitDetail)var8.next();
            this.detectAtCommit(cloneURL, commit.getSha(), handler, timeout);
        }

    }

    public GHRepository getGitHubRepository(String cloneURL) throws IOException {
        GitHub gitHub = this.connectToGitHub();
        String repoName = extractRepositoryName(cloneURL);
        return gitHub.getRepository(repoName);
    }

    private static String extractRepositoryName(String cloneURL) {
        int hostLength = 0;
        if (cloneURL.startsWith("https://github.com/")) {
            hostLength = "https://github.com/".length();
        } else if (cloneURL.startsWith("https://bitbucket.org/")) {
            hostLength = "https://bitbucket.org/".length();
        }

        int indexOfDotGit = cloneURL.length();
        if (cloneURL.endsWith(".git")) {
            indexOfDotGit = cloneURL.indexOf(".git");
        } else if (cloneURL.endsWith("/")) {
            indexOfDotGit = cloneURL.length() - 1;
        }

        String repoName = cloneURL.substring(hostLength, indexOfDotGit);
        return repoName;
    }

    public static String extractCommitURL(String cloneURL, String commitId) {
        int indexOfDotGit = cloneURL.length();
        if (cloneURL.endsWith(".git")) {
            indexOfDotGit = cloneURL.indexOf(".git");
        } else if (cloneURL.endsWith("/")) {
            indexOfDotGit = cloneURL.length() - 1;
        }

        String commitResource = "/";
        if (cloneURL.startsWith("https://github.com/")) {
            commitResource = "/commit/";
        } else if (cloneURL.startsWith("https://bitbucket.org/")) {
            commitResource = "/commits/";
        }

        String commitURL = cloneURL.substring(0, indexOfDotGit) + commitResource + commitId;
        return commitURL;
    }

    private static String extractDownloadLink(String cloneURL, String commitId) {
        int indexOfDotGit = cloneURL.length();
        if (cloneURL.endsWith(".git")) {
            indexOfDotGit = cloneURL.indexOf(".git");
        } else if (cloneURL.endsWith("/")) {
            indexOfDotGit = cloneURL.length() - 1;
        }

        String downloadResource = "/";
        if (cloneURL.startsWith("https://github.com/")) {
            downloadResource = "/archive/";
        } else if (cloneURL.startsWith("https://bitbucket.org/")) {
            downloadResource = "/get/";
        }

        String downloadLink = cloneURL.substring(0, indexOfDotGit) + downloadResource + commitId + ".zip";
        return downloadLink;
    }

    static {
        systemFileSeparator = Matcher.quoteReplacement(File.separator);
    }

    public static class ChangedFileInfo {
        private String parentCommitId;
        private List<String> filesBefore;
        private List<String> filesCurrent;
        private Map<String, String> renamedFilesHint;

        public ChangedFileInfo() {
        }

        public ChangedFileInfo(String parentCommitId, List<String> filesBefore, List<String> filesCurrent, Map<String, String> renamedFilesHint) {
            this.filesBefore = filesBefore;
            this.filesCurrent = filesCurrent;
            this.renamedFilesHint = renamedFilesHint;
            this.parentCommitId = parentCommitId;
        }

        public String getParentCommitId() {
            return this.parentCommitId;
        }

        public List<String> getFilesBefore() {
            return this.filesBefore;
        }

        public List<String> getFilesCurrent() {
            return this.filesCurrent;
        }

        public Map<String, String> getRenamedFilesHint() {
            return this.renamedFilesHint;
        }
    }
}
