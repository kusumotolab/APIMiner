import apidiff.APIDiff;
import apidiff.Change;
import apidiff.Result;
import apidiff.enums.Classifier;
import apidiff.internal.service.git.GitService;
import apidiff.internal.service.git.GitServiceImpl;
import apidiff.util.UtilFile;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    String folder;
    String cloneUrl;
    String commit;
    String branch;

    public Test(String cloneUrl, String commit) {
        this.folder = cloneUrl.substring(19, cloneUrl.length() - 4);
        this.cloneUrl = cloneUrl;
        this.commit = commit;
    }

    public Test(String cloneUrl, String commit, String branch) {
        this.folder = cloneUrl.substring(19, cloneUrl.length() - 4);
        this.cloneUrl = cloneUrl;
        this.commit = commit;
        this.branch = branch;
    }

    public void detectAtCommit() {
        APIDiff diff = new APIDiff(folder, cloneUrl);
        diff.setPath("/Users/m-iriyam/github/");

        Result result = diff.detectChangeAtCommit(commit, Classifier.API);

        for(Change changeType : result.getChangeType()){
            System.out.println("\n" + changeType.getCategory().getDisplayName() + " - " + changeType.getDescription());
        }

        for(Change changeMethod : result.getChangeMethod()){
            System.out.println("\n" + changeMethod.getCategory().getDisplayName() + " - " + changeMethod.getDescription());
        }

        for (Change changeField : result.getChangeField()) {
            System.out.println("\n" + changeField.getCategory().getDisplayName() + " - " + changeField.getDescription());
        }

    }

    public void detectAll() {
        APIDiff diff = new APIDiff(folder, cloneUrl);
        diff.setPath("/Users/m-iriyam/github/");
        Result result = null;

        try {
            result = diff.detectChangeAllHistory(branch, Classifier.API);

            List<String> listChanges = new ArrayList<String>();
            listChanges.add("Commit;Change;Category;isDeprecated;containsJavadoc;Description");


            for (Change changeType : result.getChangeType()) {
                String change = changeType.getRevCommit().getName() + ";" + "Type;" + changeType.getCategory().getDisplayName() + ";" + changeType.isDeprecated() + ";" + changeType.containsJavadoc() + ";" + changeType.getDescription();
                listChanges.add(change);
            }

            for (Change changeMethod : result.getChangeMethod()) {
                String change = changeMethod.getRevCommit().getName() + ";" + "Method;" + changeMethod.getCategory().getDisplayName() + ";" + changeMethod.isDeprecated() + ";" + changeMethod.containsJavadoc() + ";" + changeMethod.getDescription();
                listChanges.add(change);
            }

            for (Change changeFiled : result.getChangeField()) {
                String change = changeFiled.getRevCommit().getName() + ";" + "Field;" + changeFiled.getCategory().getDisplayName() + ";" + changeFiled.isDeprecated() + ";" + changeFiled.containsJavadoc() + ";" + changeFiled.getDescription();
                listChanges.add(change);
            }

            String dirName = "/Users/m-iriyam/data/experiment/" + folder;
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("ディレクトリの作成に成功しました");
                } else {
                    System.out.println("ディレクトリの作成に失敗しました");
                    System.exit(1);
                }
            }
            String fileName = dirName + "/APIDIFF_verRMiner.csv";
            UtilFile.writeFile(fileName, listChanges);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void detectAllCommit() {
        APIDiff diff = new APIDiff(folder, cloneUrl);
        diff.setPath("/Users/m-iriyam/github/");
        Result result = null;

        try {
            result = diff.detectChangeAllHistory(branch, Classifier.API);

            GitService service = new GitServiceImpl();
            Repository repository = service.openRepositoryAndCloneIfNotExists("/Users/m-iriyam/github/" + folder, folder, cloneUrl);
            ObjectId commitId = ObjectId.fromString(commit);
            RevWalk revWalk = new RevWalk(repository);
            RevCommit commitEnd = revWalk.parseCommit(commitId);


            List<String> listChanges = new ArrayList<String>();
            listChanges.add("Commit;Change;Category;isDeprecated;containsJavadoc;Description");

            for (Change changeType : result.getChangeType()) {
                if (commitEnd.getCommitTime() >= changeType.getRevCommit().getCommitTime()) {
                    String change = changeType.getRevCommit().getName() + ";" + "Type;" + changeType.getCategory().getDisplayName() + ";" + changeType.isDeprecated() + ";" + changeType.containsJavadoc() + ";" + changeType.getDescription();
                    listChanges.add(change);
                }
            }

            for (Change changeMethod : result.getChangeMethod()) {
                if (commitEnd.getCommitTime() >= changeMethod.getRevCommit().getCommitTime()) {
                    String change = changeMethod.getRevCommit().getName() + ";" + "Method;" + changeMethod.getCategory().getDisplayName() + ";" + changeMethod.isDeprecated() + ";" + changeMethod.containsJavadoc() + ";" + changeMethod.getDescription();
                    listChanges.add(change);
                }
            }

            for (Change changeFiled : result.getChangeField()) {
                if (commitEnd.getCommitTime() >= changeFiled.getRevCommit().getCommitTime()) {
                    String change = changeFiled.getRevCommit().getName() + ";" + "Field;" + changeFiled.getCategory().getDisplayName() + ";" + changeFiled.isDeprecated() + ";" + changeFiled.containsJavadoc() + ";" + changeFiled.getDescription();
                    listChanges.add(change);
                }
            }

            String dirName = "/Users/m-iriyam/data/allCommits/" + folder;
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("ディレクトリの作成に成功しました");
                } else {
                    System.out.println("ディレクトリの作成に失敗しました");
                    System.exit(1);
                }
            }
            String fileName = dirName + "/APIDIFF_verRMiner.csv";
            UtilFile.writeFile(fileName, listChanges);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
