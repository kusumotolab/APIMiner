package apiminer.internal.analysis.diff;

import apiminer.enums.Category;
import apiminer.internal.analysis.category.field.*;
import apiminer.internal.util.UtilTools;
import apiminer.util.Change;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.decomposition.AbstractExpression;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class FieldDiff {
    private final UMLClass originalClass;
    private final UMLClass nextClass;
    private final RevCommit revCommit;
    private final List<Change> changeList = new ArrayList<>();
    private final UMLAttribute originalAttribute;
    private final UMLAttribute nextAttribute;

    public FieldDiff(UMLClass originalClass, UMLAttribute originalAttribute, UMLClass nextClass, UMLAttribute nextAttribute, List<Change> changeList, RevCommit revCommit) {
        this.originalClass = originalClass;
        this.originalAttribute = originalAttribute;
        this.nextClass = nextClass;
        this.nextAttribute = nextAttribute;
        this.changeList.addAll(changeList);
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

    public List<Change> getChangeList() {
        return changeList;
    }

    private void detectOtherChange() {
        boolean isBreakingChange = false;
        if (originalClass != null && nextClass != null) {
            detectVisibilityChange();
            detectFinalModifierChange();
            detectStaticModifierChange();
            detectDefaultValueChange();
            detectDeprecatedChange();
            for (Change change : changeList) {
                if (change.getBreakingChange()) {
                    isBreakingChange = true;
                    break;
                }
            }
            for (Change change : changeList) {
                change.setBreakingChange(isBreakingChange);
            }
        }
    }

    private void detectVisibilityChange() {
        if(UtilTools.isAPIClass(originalClass)&& UtilTools.isAPIClass(nextClass)){
            String originalAccessModifier = originalAttribute.getVisibility();
            String nextAccessModifier = nextAttribute.getVisibility();
            if (!originalAccessModifier.equals(nextAccessModifier)) {
                switch (originalAccessModifier) {
                    case "private":
                    case "default":
                        if (nextAccessModifier.equals("public") || nextAccessModifier.equals("protected")) {
                            changeList.add(new VisibilityFieldChange(originalClass, originalAttribute,nextClass, nextAttribute,Category.FIELD_GAIN_VISIBILITY, revCommit));
                        }
                        break;
                    case "protected":
                        if (nextAccessModifier.equals("public")) {
                            changeList.add(new VisibilityFieldChange(originalClass,originalAttribute, nextClass, nextAttribute,Category.FIELD_GAIN_VISIBILITY, revCommit));
                        } else {
                            changeList.add(new VisibilityFieldChange(originalClass, originalAttribute,nextClass, nextAttribute,Category.FIELD_LOST_VISIBILITY, revCommit));
                        }
                        break;
                    case "pubic":
                        changeList.add(new VisibilityFieldChange(originalClass,originalAttribute, nextClass, nextAttribute,Category.FIELD_LOST_VISIBILITY, revCommit));
                        break;
                }
            }
        }
    }

    private void detectFinalModifierChange() {
        if (originalAttribute.isFinal() && !nextAttribute.isFinal()) {
            changeList.add(new FinalFieldChange(originalClass,originalAttribute,nextClass,nextAttribute,Category.FIELD_REMOVE_MODIFIER_FINAL,revCommit));
        } else if (!originalAttribute.isFinal() && nextAttribute.isFinal()) {
            changeList.add(new FinalFieldChange(originalClass,originalAttribute,nextClass,nextAttribute,Category.FIELD_ADD_MODIFIER_FINAL,revCommit));
        }
    }

    private void detectStaticModifierChange() {
        if (originalAttribute.isStatic() && !nextAttribute.isStatic()) {
            changeList.add(new StaticFieldChange(originalClass,originalAttribute,nextClass,nextAttribute,Category.FIELD_REMOVE_MODIFIER_FINAL,revCommit));
        } else if (!originalAttribute.isStatic() && nextAttribute.isStatic()) {
            changeList.add(new StaticFieldChange(originalClass,originalAttribute,nextClass,nextAttribute,Category.FIELD_ADD_MODIFIER_FINAL,revCommit));
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
        boolean isOriginalDeprecated = UtilTools.isDeprecatedClass(originalClass) || UtilTools.isDeprecatedField(originalAttribute);
        boolean isNextDeprecated = UtilTools.isDeprecatedClass(nextClass) || UtilTools.isDeprecatedField(nextAttribute);
        if (!isOriginalDeprecated && isNextDeprecated) {
            changeList.add(new DeprecateFieldChange(originalClass, originalAttribute, nextClass, nextAttribute, revCommit));
        }
    }
}
