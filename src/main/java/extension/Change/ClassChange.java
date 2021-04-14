package extension.Change;

import extension.RefactoringElement;
import org.eclipse.jgit.revwalk.RevCommit;

public class ClassChange extends Change{
    public ClassChange(RevCommit revCommit){
        super(revCommit);
    }
}
