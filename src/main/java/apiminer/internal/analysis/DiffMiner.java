//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package apiminer.internal.analysis;

import apiminer.enums.Classifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLModelASTReader;
import gr.uom.java.xmi.diff.UMLModelDiff;
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
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositoryWrapper;
import org.kohsuke.github.GitHub;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringMinerTimedOutException;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DiffMiner {
    private static final String systemFileSeparator;
    private static final String GITHUB_URL = "https://github.com/";
    private static final String BITBUCKET_URL = "https://bitbucket.org/";

    static {
        systemFileSeparator = Matcher.quoteReplacement(File.separator);
    }

    Logger logger = LoggerFactory.getLogger(GitHistoryRefactoringMinerImpl.class);
    private Classifier classifierAPI;
    private RevCommit revCommit;
    private APIModelDiff apiModelDiff;

    private GitHub gitHub;

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

    public APIModelDiff createModelDiff(Repository repository, String commitId, Classifier classifierAPI, RevCommit revCommit) {
        this.classifierAPI = classifierAPI;
        this.revCommit = revCommit;
        detectAtCommit(repository, commitId, new RefactoringHandler() {
        });
        return apiModelDiff;
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
            if (currentCommit.getParentCount() > 0) {
                RevCommit parentCommit = currentCommit.getParent(0);
                UMLModel parentUMLModel = null;
                UMLModel currentUMLModel = null;
                if (!filePathsBefore.isEmpty()) {
                    this.populateFileContents(repository, parentCommit, filePathsBefore, fileContentsBefore, repositoryDirectoriesBefore);
                    parentUMLModel = this.createModel(fileContentsBefore, repositoryDirectoriesBefore);
                }
                if (!filePathsCurrent.isEmpty()) {
                    this.populateFileContents(repository, currentCommit, filePathsCurrent, fileContentsCurrent, repositoryDirectoriesCurrent);
                    currentUMLModel = this.createModel(fileContentsCurrent, repositoryDirectoriesCurrent);
                }
                apiModelDiff = new APIModelDiff(parentUMLModel, currentUMLModel, renamedFilesHint, classifierAPI, revCommit);
            }
            handler.handle(commitId, Collections.emptyList());
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
        return Collections.emptyList();
    }

    private void populateFileContents(Repository repository, RevCommit commit, List<String> filePaths, Map<String, String> fileContents, Set<String> repositoryDirectories) throws Exception {
        this.logger.info("Processing {} {} ...", repository.getDirectory().getParent(), commit.getName());
        RevTree parentTree = commit.getTree();
        TreeWalk treeWalk = new TreeWalk(repository);

        try {
            treeWalk.addTree(parentTree);
            treeWalk.setRecursive(true);

            label46:
            while (true) {
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
                    } while (!pathString.endsWith(".java"));
                } while (!pathString.contains("/"));

                String directory = pathString.substring(0, pathString.lastIndexOf("/"));
                repositoryDirectories.add(directory);
                String subDirectory = directory;

                while (subDirectory.contains("/")) {
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
                apiModelDiff = new APIModelDiff(parentUMLModel, currentUMLModel, renamedFilesHint, classifierAPI, revCommit);
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

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
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
            GitHistoryRefactoringMinerImpl.ChangedFileInfo changedFileInfo = mapper.readValue(jsonFile, GitHistoryRefactoringMinerImpl.ChangedFileInfo.class);
            return changedFileInfo;
        } else {
            GHRepository repository = this.getGitHubRepository(cloneURL);
            List<org.kohsuke.github.GHCommit.File> commitFiles = new ArrayList();
            GHCommit commit = (new GHRepositoryWrapper(repository)).getCommit(currentCommitId, commitFiles);
            String parentCommitId = commit.getParents().get(0).getSHA1();
            List<String> filesBefore = new ArrayList();
            List<String> filesCurrent = new ArrayList();
            Map<String, String> renamedFilesHint = new HashMap();

            for (GHCommit.File commitFile : commitFiles) {
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

    protected UMLModel createModel(Map<String, String> fileContents, Set<String> repositoryDirectories) throws Exception {
        return (new UMLModelASTReader(fileContents, repositoryDirectories)).getUmlModel();
    }

    protected UMLModel createModel(File projectFolder, List<String> filePaths) throws Exception {
        Map<String, String> fileContents = new LinkedHashMap();
        Set<String> repositoryDirectories = new LinkedHashSet();

        for (String path : filePaths) {
            String fullPath = projectFolder + File.separator + path.replaceAll("/", systemFileSeparator);
            String contents = FileUtils.readFileToString(new File(fullPath));
            fileContents.put(path, contents);
            String directory = path;

            while (directory.contains("/")) {
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

    public GHRepository getGitHubRepository(String cloneURL) throws IOException {
        GitHub gitHub = this.connectToGitHub();
        String repoName = extractRepositoryName(cloneURL);
        return gitHub.getRepository(repoName);
    }

}