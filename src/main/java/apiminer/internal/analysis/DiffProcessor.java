package apiminer.internal.analysis;

import apiminer.Result;
import apiminer.enums.Classifier;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class DiffProcessor {
    public Result detectChange(final Repository repository, final RevCommit revCommit, Classifier classifierAPI) {
        Result result = new Result();
        DiffMiner diffMiner = new DiffMiner();
        APIModelDiff APIModelDiff = diffMiner.createModelDiff(repository, revCommit.getName(), classifierAPI, revCommit);
        APIModelDiff.detectChanges();
        result.getChangeType().addAll(APIModelDiff.getChangeTypeList());
        result.getChangeMethod().addAll(APIModelDiff.getChangeMethodList());
        result.getChangeField().addAll(APIModelDiff.getChangeFieldList());
        return result;
    }
}
