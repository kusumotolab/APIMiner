package apiminer.internal.analysis;

import apiminer.Result;
import apiminer.enums.Classifier;
import apiminer.internal.analysis.model.ModelDiff;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class DiffProcessor {
    public Result detectChange(final Repository repository, final RevCommit revCommit, Classifier classifierAPI) {
        Result result = new Result();
        DiffMiner diffMiner = new DiffMiner();
        ModelDiff modelDiff = diffMiner.createModelDiff(repository, revCommit.getName(), classifierAPI, revCommit);
        modelDiff.detectChanges();
        result.getChangeType().addAll(modelDiff.getChangeTypeList());
        result.getChangeMethod().addAll(modelDiff.getChangeMethodList());
        result.getChangeField().addAll(modelDiff.getChangeFieldList());
        return result;
    }
}
