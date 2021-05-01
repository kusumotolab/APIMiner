package apiminer.internal.analysis.diff;

import apiminer.Change;
import apiminer.enums.Category;
import apiminer.enums.RefClassifier;
import apiminer.internal.analysis.category.FieldChange;
import apiminer.internal.analysis.category.field.*;
import apiminer.internal.analysis.model.CommonField;
import apiminer.internal.analysis.model.RefIdentifier;
import apiminer.internal.util.UtilTools;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.decomposition.AbstractExpression;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FieldDiff {
    private final UMLClass originalClass;
    private final UMLClass nextClass;
    private final RevCommit revCommit;
    private final List<FieldChange> changeList = new ArrayList<>();
    private final UMLAttribute originalAttribute;
    private final UMLAttribute nextAttribute;
    private boolean isBreakingChange = false;
    private Map.Entry<RefIdentifier, List<FieldChange>> entry;
    private CommonField commonField;


    public FieldDiff(Map.Entry<RefIdentifier, List<FieldChange>> entry, CommonField commonField, RevCommit revCommit) {
        this.entry = entry;
        this.commonField = commonField;
        this.originalClass = entry.getKey().getOriginalClass();
        this.originalAttribute = entry.getKey().getOriginalAttribute();
        this.nextClass = entry.getKey().getNextClass();
        this.nextAttribute = entry.getKey().getNextAttribute();
        this.changeList.addAll(entry.getValue());
        this.revCommit = revCommit;
        detectOtherChange();
    }

    public FieldDiff(UMLClass originalClass, UMLAttribute originalAttribute, UMLClass nextClass, UMLAttribute nextAttribute, RevCommit revCommit) {
        this.originalClass = originalClass;
        this.originalAttribute = originalAttribute;
        this.nextClass = nextClass;
        this.nextAttribute = nextAttribute;
        this.revCommit = revCommit;
        detectOtherChange();
    }

    public UMLClass getOriginalClass() {
        return originalClass;
    }

    public UMLClass getNextClass() {
        return nextClass;
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public UMLAttribute getOriginalAttribute() {
        return originalAttribute;
    }

    public UMLAttribute getNextAttribute() {
        return nextAttribute;
    }

    public List<FieldChange> getChangeList() {
        return changeList;
    }

    public boolean isBreakingChange() {
        return isBreakingChange;
    }

    private void detectOtherChange() {
        if (originalClass != null && nextClass != null) {
            detectVisibilityChange();
            detectFinalModifierChange();
            detectStaticModifierChange();
            detectDefaultValueChange();
            detectDeprecatedChange();
            if (entry == null || commonField == null) {
                boolean isAPIOriginal = UtilTools.isAPIClass(originalClass) && UtilTools.isAPIField(originalAttribute);
                boolean isAPINext = UtilTools.isAPIClass(nextClass) && UtilTools.isAPIField(nextAttribute);
                if (isAPIOriginal && isAPINext) {
                    for (Change change : changeList) {
                        if (change.getBreakingChange()) {
                            isBreakingChange = true;
                            break;
                        }
                    }
                } else isBreakingChange = isAPIOriginal;
                for (Change change : changeList) {
                    change.setBreakingChange(isBreakingChange);
                }
            } else {
                isBreakingChange = commonField.getFieldDiff().isBreakingChange();
                for (Change change : changeList) {
                    change.setBreakingChange(isBreakingChange);
                }
            }
        } else if (entry != null) {
            if (entry.getKey().getRefClassifier().equals(RefClassifier.ADD) && commonField != null) {
                if (commonField.getFieldDiff().isBreakingChange()) {
                    isBreakingChange = true;
                    for (Change change : changeList) {
                        change.setBreakingChange(isBreakingChange);
                    }
                } else {
                    isBreakingChange = changeList.get(0).getBreakingChange();
                }
            }
        }
    }

    private void detectVisibilityChange() {
        if (UtilTools.isAPIClass(originalClass) && UtilTools.isAPIClass(nextClass)) {
            String originalAccessModifier = originalAttribute.getVisibility();
            String nextAccessModifier = nextAttribute.getVisibility();
            if (!originalAccessModifier.equals(nextAccessModifier)) {
                switch (originalAccessModifier) {
                    case "private":
                    case "package":
                        if (nextAccessModifier.equals("public") || nextAccessModifier.equals("protected")) {
                            changeList.add(new VisibilityFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, Category.FIELD_GAIN_VISIBILITY, revCommit));
                        }
                        break;
                    case "protected":
                        if (nextAccessModifier.equals("public")) {
                            changeList.add(new VisibilityFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, Category.FIELD_GAIN_VISIBILITY, revCommit));
                        } else {
                            changeList.add(new VisibilityFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, Category.FIELD_LOST_VISIBILITY, revCommit));
                        }
                        break;
                    case "public":
                        changeList.add(new VisibilityFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, Category.FIELD_LOST_VISIBILITY, revCommit));
                        break;
                }
            }
        }
    }

    private void detectFinalModifierChange() {
        if (originalAttribute.isFinal() && !nextAttribute.isFinal()) {
            changeList.add(new FinalFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, Category.FIELD_REMOVE_MODIFIER_FINAL, revCommit));
        } else if (!originalAttribute.isFinal() && nextAttribute.isFinal()) {
            changeList.add(new FinalFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, Category.FIELD_ADD_MODIFIER_FINAL, revCommit));
        }
    }

    private void detectStaticModifierChange() {
        if (originalAttribute.isStatic() && !nextAttribute.isStatic()) {
            changeList.add(new StaticFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, Category.FIELD_REMOVE_MODIFIER_FINAL, revCommit));
        } else if (!originalAttribute.isStatic() && nextAttribute.isStatic()) {
            changeList.add(new StaticFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, Category.FIELD_ADD_MODIFIER_FINAL, revCommit));
        }
    }

    private void detectDefaultValueChange() {
        AbstractExpression originalDefault = originalAttribute.getVariableDeclaration().getInitializer();
        AbstractExpression nextDefault = nextAttribute.getVariableDeclaration().getInitializer();
        if (originalDefault == null) {
            if (nextDefault != null) {
                changeList.add(new ChangeInDefaultValue(originalClass, originalAttribute, nextClass, nextAttribute, revCommit));
            }
        } else {
            if (nextDefault == null) {
                changeList.add(new ChangeInDefaultValue(originalClass, originalAttribute, nextClass, nextAttribute, revCommit));
            } else if (!originalDefault.getExpression().equals(nextDefault.getExpression())) {
                changeList.add(new ChangeInDefaultValue(originalClass, originalAttribute, nextClass, nextAttribute, revCommit));
            }
        }
    }

    private void detectDeprecatedChange() {
        boolean isOriginalDeprecated = UtilTools.isDeprecated(originalClass.getAnnotations()) || UtilTools.isDeprecated(originalAttribute.getAnnotations());
        boolean isNextDeprecated = UtilTools.isDeprecated(nextClass.getAnnotations()) || UtilTools.isDeprecated(nextAttribute.getAnnotations());
        if (!isOriginalDeprecated && isNextDeprecated) {
            changeList.add(new DeprecateFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, revCommit));
        }
    }
}
