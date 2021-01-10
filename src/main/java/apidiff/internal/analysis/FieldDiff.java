package apidiff.internal.analysis;

import apidiff.Change;
import apidiff.enums.Category;
import apidiff.internal.analysis.description.FieldDescription;
import apidiff.internal.exception.BindingException;
import apidiff.internal.util.UtilTools;
import apidiff.internal.util.UtilToolsForRef;
import apidiff.internal.visitor.APIVersion;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import sun.jvm.hotspot.memory.ReferenceType;

public class FieldDiff {

    private Logger logger = LoggerFactory.getLogger(FieldDiff.class);

    private Map<RefactoringType, List<Refactoring>> refactorings = new HashMap<RefactoringType, List<Refactoring>>();

    private List<String> fieldWithPathChanged = new ArrayList<String>();

    private List<Change> listChange = new ArrayList<Change>();

    private FieldDescription description = new FieldDescription();

    private RevCommit revCommit;

    public List<Change> detectChange(final APIVersion version1, final APIVersion version2, final Map<RefactoringType, List<Refactoring>> refactorings, final RevCommit revCommit) {
        this.logger.info("Processing Fields...");
        this.refactorings = refactorings;
        this.revCommit = revCommit;
        this.findDefaultValueFields(version1, version2);
        //this.findChangedTypeFields(version1, version2);
        this.findRemoveAndRefactoringFields(version1, version2);
        this.findChangedVisibilityFields(version1, version2);
        this.findChangedFinal(version1, version2);
        this.findAddedAndExtractFields(version1, version2);
        this.findAddedDeprecatedFields(version1, version2);
        return this.listChange;
    }

    private void addChange(final TypeDeclaration type, final FieldDeclaration field, Category category, Boolean isBreakingChange, final String description) {
        String name = UtilTools.getFieldName(field);
        if (name != null) {
            Change change = new Change();
            change.setJavadoc(UtilTools.containsJavadoc(type, field));
            change.setDeprecated(this.isDeprecated(field, type));
            change.setBreakingChange(this.isDeprecated(field, type) ? false : isBreakingChange);
            change.setPath(UtilTools.getPath(type));
            change.setElement(name);
            change.setCategory(category);
            change.setDescription(description);
            change.setRevCommit(this.revCommit);
            //isBreakingChange = this.isDeprecated(field, type) ? false : isBreakingChange;
            this.listChange.add(change);
        } else {
            this.logger.error("Removing field with null name " + field);
        }

    }

    private List<Refactoring> getChangeInNameOrPathOperation(){
        List<Refactoring> listChangeInNameOrPath = new ArrayList<Refactoring>();
        if (this.refactorings.containsKey(RefactoringType.MOVE_ATTRIBUTE)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.MOVE_ATTRIBUTE));
        }
        if (this.refactorings.containsKey(RefactoringType.RENAME_ATTRIBUTE)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.RENAME_ATTRIBUTE));
        }
        if (this.refactorings.containsKey(RefactoringType.MOVE_RENAME_ATTRIBUTE)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.MOVE_RENAME_ATTRIBUTE));
        }
        if (this.refactorings.containsKey(RefactoringType.PULL_UP_ATTRIBUTE)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.PULL_UP_ATTRIBUTE));
        }
        if (this.refactorings.containsKey(RefactoringType.PUSH_DOWN_ATTRIBUTE)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.PUSH_DOWN_ATTRIBUTE));
        }
        return listChangeInNameOrPath;
    }

    private Category getCategory(RefactoringType refactoringType) {
        switch (refactoringType) {
            case PULL_UP_ATTRIBUTE:
                return Category.FIELD_PULL_UP;
            case PUSH_DOWN_ATTRIBUTE:
                return Category.FIELD_PUSH_DOWN;
            case MOVE_ATTRIBUTE:
                return Category.FIELD_MOVE;
            case MOVE_RENAME_ATTRIBUTE:
                return Category.FIELD_MOVE_AND_RENAME;
            case RENAME_ATTRIBUTE:
                return Category.FIELD_RENAME;
            case CHANGE_ATTRIBUTE_TYPE:
                return Category.FIELD_CHANGE_TYPE;
            default:
                return Category.FIELD_EXTRACT;
        }
    }

    /**
     * Finding push down field
     *
     * @param field
     * @param type
     * @return
     */
    private void addChangeOfPushDown(final FieldDeclaration field, final TypeDeclaration type) {
        String fullNameFieldAndPath=this.getFullNameAndPath(field,type);
        String nameField=UtilTools.getFieldName(field);
        String nameClassBefore = this.getPath(type);
        List<Refactoring> listPushDown = this.refactorings.get(RefactoringType.PUSH_DOWN_ATTRIBUTE);
        if (listPushDown != null) {
            for (Refactoring ref : listPushDown) {
                String[] attributeBefore = UtilToolsForRef.getAttributeBefore(ref);
                String fullNameFieldAndPathBefore = UtilToolsForRef.getFullNameFieldAndPath(attributeBefore);
                if(fullNameFieldAndPathBefore.equals(fullNameFieldAndPath)){
                    String[] attributeAfter = UtilToolsForRef.getAttributeAfter(ref);
                    String nameClassAfter = UtilToolsForRef.getClassPathOfField(attributeAfter);
                    String fullNameFieldAndPathAfter = UtilToolsForRef.getFullNameFieldAndPath(attributeAfter);
                    String description = this.description.pushDown(nameField, nameClassBefore, nameClassAfter);
                    this.addChange(type, field, Category.FIELD_PUSH_DOWN, true, description);
                    this.fieldWithPathChanged.add(fullNameFieldAndPathAfter);
                }
            }
        }
    }

    /**
     * Finding Change in field type
     *
     * @param field
     * @param type
     * @return
     */
    private Boolean processChangeTypeField(final FieldDeclaration field, final TypeDeclaration type) {
        List<Refactoring> listChangeType = this.refactorings.get(RefactoringType.CHANGE_ATTRIBUTE_TYPE);
        if (listChangeType != null) {
            String fullNameFieldAndPath = this.getFullNameAndPath(field, type);
            for (Refactoring ref : listChangeType) {
                String[] attributeBefore = UtilToolsForRef.getAttributeBefore(ref);
                String fullNameFiledAndPathBefore = UtilToolsForRef.getFullNameFieldAndPath(attributeBefore);
                if(fullNameFiledAndPathBefore.equals(fullNameFieldAndPath)){
                    String nameFieldBefore = UtilToolsForRef.getSimpleNameFieldAndType(fullNameFiledAndPathBefore);
                    String[] attributeAfter = UtilToolsForRef.getAttributeAfter(ref);
                    String fullNameFieldAndPathAfter = UtilToolsForRef.getFullNameFieldAndPath(attributeAfter);
                    String nameFieldAfter = UtilToolsForRef.getSimpleNameFieldAndType(fullNameFieldAndPathAfter);
                    String nameClass = UtilToolsForRef.getClassPathOfField(attributeAfter);
                    String description = this.description.changeType(nameFieldBefore,nameFieldAfter,nameClass);
                    this.addChange(type, field, Category.FIELD_CHANGE_TYPE, true, description);
                    this.fieldWithPathChanged.add(fullNameFieldAndPathAfter);
                    return true;
                }
            }
        }
        return false;
    }
    private String getFullNameAndPath(final FieldDeclaration field, final TypeDeclaration type){
        String fullNameAndPath="";
        if(field!=null&&type!=null){
            String path = UtilTools.getPath(type);
            String fieldType = field.getType().toString()+" ";
            String fieldName = UtilTools.getFieldName(field);
            fullNameAndPath=path+"#"+fieldType+fieldName;
        }
        return fullNameAndPath;
    }


    /**
     * @param field
     * @param type
     * @return Return Name class + name field (e.g. : org.lib.Math#value)
     */
    private String getNameAndPath(final FieldDeclaration field, final TypeDeclaration type) {
        return UtilTools.getPath(type) + "#" + UtilTools.getFieldName(field);
    }

    private String getPath(final TypeDeclaration type) {
        return UtilTools.getPath(type);
    }

    private Boolean processRemoveField(final FieldDeclaration field, final TypeDeclaration type) {
        String description = this.description.remove(UtilTools.getFieldName(field), UtilTools.getPath(type));
        this.addChange(type, field, Category.FIELD_REMOVE, true, description);
        return false;
    }

    private Boolean processChangeInNameOrPath(final FieldDeclaration field, final TypeDeclaration type){
        List<Refactoring> listChangeInNameOrPath = this.getChangeInNameOrPathOperation();
        if (listChangeInNameOrPath != null) {
            String fullNameFieldAndPath = this.getFullNameAndPath(field, type);
            for (Refactoring ref : listChangeInNameOrPath) {
                String[] attributeBefore = UtilToolsForRef.getAttributeBefore(ref);
                String fullNameFieldAndPathBefore = UtilToolsForRef.getFullNameFieldAndPath(attributeBefore);
                if(fullNameFieldAndPathBefore.equals(fullNameFieldAndPath)){
                    switch (ref.getRefactoringType()){
                        case PUSH_DOWN_ATTRIBUTE:
                            addChangeOfPushDown(field,type);
                            break;
                        default:
                            Boolean isBreakingChange = RefactoringType.PULL_UP_OPERATION.equals(ref.getRefactoringType()) ? false : true;
                            Category category = this.getCategory(ref.getRefactoringType());
                            String description = this.description.changeInNameOrPath(category, ref);
                            this.addChange(type, field, category, isBreakingChange, description);
                            String[] attributeAfter = UtilToolsForRef.getAttributeAfter(ref);
                            String fullNameFieldAndPathAfter = UtilToolsForRef.getFullNameFieldAndPath(attributeAfter);
                            this.fieldWithPathChanged.add(fullNameFieldAndPathAfter);
                            break;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finding refactoring operations in field (move, pull up, push down, move and rename, rename, change in type field)
     *
     * @param field
     * @param type
     * @return
     */
    private Boolean checkAndProcessRefactoring(final FieldDeclaration field, final TypeDeclaration type) {
        Boolean changeInNameOrPath = this.processChangeInNameOrPath(field,type);
        Boolean changeType = this.processChangeTypeField(field,type);
        return changeInNameOrPath||changeType;
    }

    /**
     * Finding extract field
     *
     * @param field
     * @param type
     * @return
     */
    private Boolean checkAndProcessExtractField(final FieldDeclaration field, final TypeDeclaration type) {
        List<Refactoring> listExtract = refactorings.get(RefactoringType.EXTRACT_ATTRIBUTE);
        if (listExtract != null) {
            String fullNameFieldAndPath = this.getFullNameAndPath(field, type);
            for (Refactoring ref : listExtract) {
                String[] attributeAfter = UtilToolsForRef.getAttributeAfter(ref);
                String fullNameFieldAndPathAfter = UtilToolsForRef.getFullNameFieldAndPath(attributeAfter);
                if(fullNameFieldAndPathAfter.equals(fullNameFieldAndPath)) {
                    String nameFieldExtracted = UtilToolsForRef.getSimpleNameField(attributeAfter);
                    String[] attributeBefore = UtilToolsForRef.getAttributeBefore(ref);
                    String classPath = UtilToolsForRef.getClassPathOfField(attributeBefore);
                    String description = this.description.extract(nameFieldExtracted,classPath);
                    this.addChange(type, field, Category.FIELD_EXTRACT, false, description);
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean processAddField(final FieldDeclaration field, final TypeDeclaration type) {
        String description = this.description.addition(UtilTools.getFieldName(field), UtilTools.getPath(type));
        this.addChange(type, field, Category.FIELD_ADD, false, description);
        return false;
    }

    /**
     * @param fieldInVersion1
     * @param fieldInVersion2
     * @return True, if there is a difference between the fields.
     */
    private Boolean thereAreDifferentDefaultValueField(FieldDeclaration fieldInVersion1, FieldDeclaration fieldInVersion2) {

        List<VariableDeclarationFragment> variable1Fragments = fieldInVersion1.fragments();
        List<VariableDeclarationFragment> variable2Fragments = fieldInVersion2.fragments();

        Expression valueVersion1 = variable1Fragments.get(0).getInitializer();
        Expression valueVersion2 = variable2Fragments.get(0).getInitializer();

        //If default value was removed/changed
        if ((valueVersion1 == null && valueVersion2 != null) || (valueVersion1 != null && valueVersion2 == null)) {
            return true;
        }

        //If fields have default value and they are different
        if ((valueVersion1 != null && valueVersion2 != null) && (!valueVersion1.toString().equals(valueVersion2.toString()))) {
            return true;
        }

        return false;
    }

    /**
     * Searching changed default values
     *
     * @param version1
     * @param version2
     */
    private void findDefaultValueFields(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration type : version1.getApiAcessibleTypes()) {
            if (version2.containsAccessibleType(type)) {
                for (FieldDeclaration fieldInVersion1 : type.getFields()) {
                    if (this.isFieldAccessible(fieldInVersion1)) {
                        FieldDeclaration fieldInVersion2 = version2.getVersionField(fieldInVersion1, type);
                        if (this.isFieldAccessible(fieldInVersion2) && this.thereAreDifferentDefaultValueField(fieldInVersion1, fieldInVersion2)) {
                            String description = this.description.changeDefaultValue(UtilTools.getFieldName(fieldInVersion2), UtilTools.getPath(type));
                            this.addChange(type, fieldInVersion2, Category.FIELD_CHANGE_DEFAULT_VALUE, true, description);
                        }
                    }
                }
            }
        }
    }

    /**
     * Finding fields with changed visibility
     *
     * @param typeVersion1
     * @param fieldVersion1
     * @param fieldVersion2
     */
    private void checkGainOrLostVisibility(TypeDeclaration typeVersion1, FieldDeclaration fieldVersion1, FieldDeclaration fieldVersion2) {
        if (fieldVersion2 != null && fieldVersion1 != null) {//The method exists in the current version
            String visibilityMethod1 = UtilTools.getVisibility(fieldVersion1);
            String visibilityMethod2 = UtilTools.getVisibility(fieldVersion2);
            if (!visibilityMethod1.equals(visibilityMethod2)) { //The access modifier was changed.
                String description = this.description.visibility(UtilTools.getFieldName(fieldVersion1), UtilTools.getPath(typeVersion1), visibilityMethod1, visibilityMethod2);
                //Breaking change: public >> private, default, protected  ||  protected >> private, default
                if (this.isFieldAccessible(fieldVersion1) && !UtilTools.isVisibilityPublic(fieldVersion2)) {
                    this.addChange(typeVersion1, fieldVersion1, Category.FIELD_LOST_VISIBILITY, true, description);
                } else {
                    //non-breaking change: private or default --> all modifiers
                    Category category = UtilTools.isVisibilityDefault(fieldVersion1) && UtilTools.isVisibilityPrivate(fieldVersion2) ? Category.FIELD_LOST_VISIBILITY : Category.FIELD_GAIN_VISIBILITY;
                    this.addChange(typeVersion1, fieldVersion1, category, false, description);
                }
            }
        }
    }

    /**
     * Finding fields with changed visibility
     *
     * @param version1
     * @param version2
     */
    private void findChangedVisibilityFields(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeVersion1 : version1.getApiAcessibleTypes()) {
            if (version2.containsAccessibleType(typeVersion1)) {
                for (FieldDeclaration fieldVersion1 : typeVersion1.getFields()) {
                    FieldDeclaration fieldVersion2 = version2.getVersionField(fieldVersion1, typeVersion1);
                    this.checkGainOrLostVisibility(typeVersion1, fieldVersion1, fieldVersion2);
                }
            }
        }
    }

    /**
     * Finding deprecated fields
     *
     * @param version1
     * @param version2
     */
    private void findAddedDeprecatedFields(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeVersion2 : version2.getApiAcessibleTypes()) {
            for (FieldDeclaration fieldVersion2 : typeVersion2.getFields()) {
                if (this.isFieldAccessible(fieldVersion2) && this.isDeprecated(fieldVersion2, typeVersion2)) {
                    FieldDeclaration fieldInVersion1 = version1.getVersionField(fieldVersion2, typeVersion2);
                    if (fieldInVersion1 == null || !this.isDeprecated(fieldInVersion1, version1.getVersionAccessibleType(typeVersion2))) {
                        String description = this.description.deprecate(UtilTools.getFieldName(fieldVersion2), UtilTools.getPath(typeVersion2));
                        this.addChange(typeVersion2, fieldVersion2, Category.FIELD_DEPRECATED, false, description);
                    }
                }
            }
        }
    }

    /**
     * Finding added fields
     *
     * @param version1
     * @param version2
     */
    private void findAddedAndExtractFields(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeVersion2 : version2.getApiAcessibleTypes()) {
            if (version1.containsAccessibleType(typeVersion2)) {
                for (FieldDeclaration fieldInVersion2 : typeVersion2.getFields()) {
                    String fullNameAndPath = this.getFullNameAndPath(fieldInVersion2, typeVersion2);
                    if (!UtilTools.isVisibilityPrivate(fieldInVersion2) && !UtilTools.isVisibilityDefault(fieldInVersion2)) {
                        FieldDeclaration fieldInVersion1 = version1.findFieldByTypeAndName(fieldInVersion2, typeVersion2);
                        if (fieldInVersion1 == null && !this.fieldWithPathChanged.contains(fullNameAndPath)) {
                            Boolean refactoring = checkAndProcessExtractField(fieldInVersion2, typeVersion2);
                            if (!refactoring) {
                                this.processAddField(fieldInVersion2, typeVersion2);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Finding removed fields. If class was removed, class removal is a breaking change.
     *
     * @param version1
     * @param version2
     */
    private void findRemoveAndRefactoringFields(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration type : version1.getApiAcessibleTypes()) {
            if (version2.containsAccessibleType(type)) {
                for (FieldDeclaration fieldInVersion1 : type.getFields()) {
                    if (!UtilTools.isVisibilityPrivate(fieldInVersion1)&&!UtilTools.isVisibilityDefault(fieldInVersion1)) {
                        FieldDeclaration fieldInVersion2 = version2.findFieldByTypeAndName(fieldInVersion1, type);
                        if (fieldInVersion2 == null) {
                            Boolean refactoring = this.checkAndProcessRefactoring(fieldInVersion1, type);
                            if (!refactoring) {
                                this.processRemoveField(fieldInVersion1, type);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * @param field
     * @param type
     * @return true, type is deprecated or field is deprecated
     */
    private Boolean isDeprecated(FieldDeclaration field, AbstractTypeDeclaration type) {
        Boolean isFieldDeprecated = this.isDeprecatedField(field);
        Boolean isTypeDeprecated = (type != null && type.resolveBinding() != null && type.resolveBinding().isDeprecated()) ? true : false;
        return isFieldDeprecated || isTypeDeprecated;
    }

    /**
     * Checking deprecated fields
     *
     * @param field
     * @return
     */
    private Boolean isDeprecatedField(FieldDeclaration field) {
        if (field != null) {
            List<VariableDeclarationFragment> variableFragments = field.fragments();
            for (VariableDeclarationFragment variableDeclarationFragment : variableFragments) {
                if (variableDeclarationFragment.resolveBinding() != null && variableDeclarationFragment.resolveBinding().isDeprecated()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param field
     * @return true, if is a accessible field by external systems
     */
    private boolean isFieldAccessible(FieldDeclaration field) {
        return field != null && (UtilTools.isVisibilityProtected(field) || UtilTools.isVisibilityPublic(field)) ? true : false;
    }

    /**
     * Finding change in final modifier
     *
     * @param fieldVersion1
     * @param fieldVersion2
     * @throws BindingException
     */
    private void diffModifierFinal(TypeDeclaration typeVersion1, FieldDeclaration fieldVersion1, FieldDeclaration fieldVersion2) {
        //There is not change.
        if ((UtilTools.isFinal(fieldVersion1) && UtilTools.isFinal(fieldVersion2)) || ((!UtilTools.isFinal(fieldVersion1) && !UtilTools.isFinal(fieldVersion2)))) {
            return;
        }
        String description = "";
        //Gain "final"
        if ((!UtilTools.isFinal(fieldVersion1) && UtilTools.isFinal(fieldVersion2))) {
            description = this.description.modifierFinal(UtilTools.getFieldName(fieldVersion2), UtilTools.getPath(typeVersion1), true);
            this.addChange(typeVersion1, fieldVersion2, Category.FIELD_ADD_MODIFIER_FINAL, true, description);
        } else {
            //Lost "final"
            description = this.description.modifierFinal(UtilTools.getFieldName(fieldVersion2), UtilTools.getPath(typeVersion1), false);
            this.addChange(typeVersion1, fieldVersion2, Category.FIELD_REMOVE_MODIFIER_FINAL, false, description);
        }
    }

    /**
     * Finding change in final modifier
     *
     * @param version1
     * @param version2
     */
    private void findChangedFinal(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeInVersion1 : version1.getApiAcessibleTypes()) {
            if (version2.containsType(typeInVersion1)) {//Se type ainda existe.
                for (FieldDeclaration fieldVersion1 : typeInVersion1.getFields()) {
                    FieldDeclaration fieldVersion2 = version2.getVersionField(fieldVersion1, typeInVersion1);
                    if (this.isFieldAccessible(fieldVersion1) && (fieldVersion2 != null)) {
                        this.diffModifierFinal(typeInVersion1, fieldVersion1, fieldVersion2);
                    }
                }
            }
        }
    }

}
