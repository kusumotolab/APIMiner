package apiminer.internal.refactor;

import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RefactoringProcessorImpl implements RefactorProcessor {

    private Logger logger = LoggerFactory.getLogger(RefactoringProcessorImpl.class);

    private Map<RefactoringType, List<Refactoring>> format(final List<Refactoring> refactorings) {
        Map<RefactoringType, List<Refactoring>> result = new HashMap<RefactoringType, List<Refactoring>>();
        for (Refactoring ref : refactorings) {
            RefactoringType refactoringName = ref.getRefactoringType();
            if (result.containsKey(refactoringName)) {
                result.get(refactoringName).add(ref);
            } else {
                List<Refactoring> listRefactorings = new ArrayList<Refactoring>();
                listRefactorings.add(ref);
                result.put(refactoringName, listRefactorings);
            }
        }
        return result;
    }

    @Override
    public Map<RefactoringType, List<Refactoring>> detectRefactoringAtCommit(final Repository repository, final String commit) throws Exception {
        Map<RefactoringType, List<Refactoring>> result = new HashMap<RefactoringType, List<Refactoring>>();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        try {
            miner.detectAtCommit(repository, commit, new RefactoringHandler() {
                public void handle(String commitId, List<Refactoring> refactorings) {
                    for (Refactoring ref : refactorings) {
                        RefactoringType refactoringName = ref.getRefactoringType();
                        if (result.containsKey(refactoringName)) {
                            result.get(refactoringName).add(ref);
                        } else {
                            List<Refactoring> listRefactorings = new ArrayList<Refactoring>();
                            listRefactorings.add(ref);
                            result.put(refactoringName, listRefactorings);
                        }
                    }
                }
            });
        } catch (Exception e) {
            this.logger.error("Error in refactoring process [repository=" + repository + "][commit=" + commit + "]", e);
        }
        return result;
    }


}
