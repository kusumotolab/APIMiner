package apiminer.internal.analysis;

import apiminer.Change;
import apiminer.enums.Category;
import apiminer.internal.analysis.description.TypeDescription;
import apiminer.internal.util.UtilTools;
import apiminer.internal.util.UtilToolsForRef;
import apiminer.internal.visitor.APIVersion;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeDiff {

    private List<Change> listChange = new ArrayList<Change>();

    private Logger logger = LoggerFactory.getLogger(TypeDiff.class);

    private TypeDescription description = new TypeDescription();

    private APIVersion version1;

    private APIVersion version2;

    private Map<RefactoringType, List<Refactoring>> refactorings = new HashMap<RefactoringType, List<Refactoring>>();

    private List<String> typesWithPathChanged = new ArrayList<String>();

    private RevCommit revCommit;

    public List<Change> detectChange(final APIVersion version1, final APIVersion version2, final Map<RefactoringType, List<Refactoring>> refactorings, final RevCommit revCommit) {
        this.version1 = version1;
        this.version2 = version2;
        this.revCommit = revCommit;
        this.refactorings = refactorings;
        this.logger.info("Processing Types ...");
        this.findRemovedAndRenameAndMoveTypes();
        this.findAddedAndExtractedTypes();
        this.findChangedVisibilityTypes();
        this.findAddTypeDeprecate();
        this.findChangedSuperTypes();
        this.findChangedFinalAndStatic();
        return this.listChange;
    }

    private void addChange(final AbstractTypeDeclaration type, Category category, Boolean isBreakingChange, final String description) {
        Change change = new Change();
        change.setJavadoc(UtilTools.containsJavadoc(type));
        change.setDeprecated(this.isDeprecated(type));
        change.setBreakingChange(this.isDeprecated(type) ? false : isBreakingChange);
        change.setPath(UtilTools.getPath(type));
        change.setElement(type.getName().toString());
        change.setCategory(category);
        change.setDescription(description);
        change.setRevCommit(this.revCommit);
        this.listChange.add(change);
    }

    private List<Refactoring> getMoveRefactoring() {
        List<Refactoring> listMove = new ArrayList<Refactoring>();
        if (this.refactorings.containsKey(RefactoringType.MOVE_CLASS)) {
            listMove.addAll(this.refactorings.get(RefactoringType.MOVE_CLASS));
        }
        if (this.refactorings.containsKey(RefactoringType.RENAME_CLASS)) {
            listMove.addAll(this.refactorings.get(RefactoringType.RENAME_CLASS));
        }
        if (this.refactorings.containsKey(RefactoringType.MOVE_RENAME_CLASS)) {
            listMove.addAll(this.refactorings.get(RefactoringType.MOVE_RENAME_CLASS));
        }
        return listMove;
    }
    private List<Refactoring> getExtractRefactoring() {
        List<Refactoring> listExtract = new ArrayList<Refactoring>();
        if (this.refactorings.containsKey(RefactoringType.EXTRACT_SUPERCLASS)) {
            listExtract.addAll(this.refactorings.get(RefactoringType.EXTRACT_SUPERCLASS));
        }
        if (this.refactorings.containsKey(RefactoringType.EXTRACT_INTERFACE)) {
            listExtract.addAll(this.refactorings.get(RefactoringType.EXTRACT_INTERFACE));
        }
        if (this.refactorings.containsKey(RefactoringType.EXTRACT_CLASS)) {
            listExtract.addAll(this.refactorings.get(RefactoringType.EXTRACT_CLASS));
        }
        if (this.refactorings.containsKey(RefactoringType.EXTRACT_SUBCLASS)) {
            listExtract.addAll(this.refactorings.get(RefactoringType.EXTRACT_SUBCLASS));
        }
        return listExtract;
    }

    private Category getCategory(RefactoringType refactoringType) {
        Category category;
        switch (refactoringType) {
            case RENAME_CLASS:
                category = Category.TYPE_RENAME;
                break;
            case MOVE_RENAME_CLASS:
                category = Category.TYPE_MOVE_AND_RENAME;
                break;
            case EXTRACT_SUPERCLASS:
            case EXTRACT_INTERFACE:
                category = Category.TYPE_EXTRACT_SUPERTYPE;
                break;
            case EXTRACT_CLASS:
                category = Category.TYPE_EXTRACT_TYPE;
                break;
            case EXTRACT_SUBCLASS:
                category = Category.TYPE_EXTRACT_SUBTYPE;
                break;
            default:
                category = Category.TYPE_MOVE;
                break;
        }
        return category;
    }

    /**
     * Finding Move Type(move,rename,move and rename)
     *
     * @param type
     * @return
     */
    private Boolean checkAndProcessMove(final AbstractTypeDeclaration type) {
        List<Refactoring> listMove = this.getMoveRefactoring();
        if (listMove != null) {
            String classPath = UtilTools.getPath(type);
            for (Refactoring ref : listMove) {
                String classPathBefore = UtilToolsForRef.getTypeBefore(ref);
                if(classPathBefore.equals(classPath)){
                    Category category = this.getCategory(ref.getRefactoringType());
                    String classPathAfter = UtilToolsForRef.getTypeAfter(ref);
                    String description = this.description.moveRefactoring(category,classPathBefore,classPathAfter);
                    this.typesWithPathChanged.add(classPathAfter);
                    this.addChange(type, category, true, description);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finding extract(extract supertype, extract type, extract subtype)
     *
     * @param type
     * @return
     */
    private Boolean checkAndProcessExtract(final AbstractTypeDeclaration type) {
        List<Refactoring> listExtract = this.getExtractRefactoring();
        if (listExtract != null) {
            String classPath = UtilTools.getPath(type);
            for (Refactoring ref : listExtract) {
                String classPathAfter = UtilToolsForRef.getTypeAfter(ref);
                if(classPathAfter.equals(classPath)){
                    String classPathBefore = UtilToolsForRef.getTypeBefore(ref);
                    Category category = this.getCategory(ref.getRefactoringType());
                    Boolean isBreakingChange = (RefactoringType.EXTRACT_CLASS.equals(ref.getRefactoringType())||RefactoringType.EXTRACT_SUBCLASS.equals(ref.getRefactoringType())) ? false : true;
                    String description = this.description.extract(category,classPathAfter,classPathBefore);
                    this.addChange(type,category, isBreakingChange, description);
                    return true;
                }
            }
        }
        return false;
    }

    private void processRemoveType(final AbstractTypeDeclaration type) {
        String description = this.description.remove(UtilTools.getPath(type));
        this.addChange(type, Category.TYPE_REMOVE, true, description);
    }

    private void findChangedSuperTypes() {
        for (AbstractTypeDeclaration accessibleTypeVersion1 : version1.getTypesPublicAndProtected()) {
            AbstractTypeDeclaration accessibleTypeVersion2 = version2.getVersionAccessibleType(accessibleTypeVersion1);
            if (accessibleTypeVersion2 != null) {
                String super1 = this.getNameSuperClass(accessibleTypeVersion1);
                String super2 = this.getNameSuperClass(accessibleTypeVersion2);

                if (super1 != null && super2 != null) {
                    Boolean isBreakingChange = true;
                    String nameClassComplete = UtilTools.getPath(accessibleTypeVersion2);
                    String description = "";

                    if (this.containsSuperClass(accessibleTypeVersion1) && !this.containsSuperClass(accessibleTypeVersion2)) {
                        description = this.description.changeSuperType(nameClassComplete, super1, "");
                        this.addChange(accessibleTypeVersion2, Category.TYPE_REMOVE_SUPERCLASS, isBreakingChange, description);
                    }

                    if (!this.containsSuperClass(accessibleTypeVersion1) && this.containsSuperClass(accessibleTypeVersion2)) {
                        description = this.description.changeSuperType(nameClassComplete, "", super2);
                        this.addChange(accessibleTypeVersion2, Category.TYPE_ADD_SUPER_CLASS, false, description);
                    }

                    if (this.containsSuperClass(accessibleTypeVersion1) && this.containsSuperClass(accessibleTypeVersion2) && !super1.equals(super2)) {
                        description = this.description.changeSuperType(nameClassComplete, super1, super2);
                        this.addChange(accessibleTypeVersion2, Category.TYPE_CHANGE_SUPERCLASS, false, description);
                    }
                }
            }
        }
    }

    /**
     * Busca classes que foram depreciadas [CATEGORY_TYPE_DEPRECIATED].
     * //@param version1
     * //@param version2
     */
    private void findAddTypeDeprecate() {
        for (AbstractTypeDeclaration accessibleTypeVersion1 : version1.getTypesPublicAndProtected()) {
            AbstractTypeDeclaration accessibleTypeVersion2 = version2.getVersionAccessibleType(accessibleTypeVersion1);
            if (accessibleTypeVersion2 != null) {
                if (!this.isDeprecated(accessibleTypeVersion1) && this.isDeprecated(accessibleTypeVersion2)) {
                    String description = this.description.deprecate(UtilTools.getPath(accessibleTypeVersion2));
                    this.addChange(accessibleTypeVersion1, Category.TYPE_DEPRECATED, false, description);
                }
            }
        }
    }

    private AbstractTypeDeclaration findTypeDeclarationInList(List<AbstractTypeDeclaration> list, AbstractTypeDeclaration type) {
        for (int i = 0; i < list.size(); i++) {
            AbstractTypeDeclaration typeDeclaration = list.get(i);
            if (UtilTools.getPath(type).equals(UtilTools.getPath(typeDeclaration))) {
                return typeDeclaration;
            }
        }
        return null;
    }

    private void findChangedVisibilityTypes() {
        List<AbstractTypeDeclaration> listTypesVersion1 = version1.getAllTypes();
        List<AbstractTypeDeclaration> listTypesVersion2 = version2.getAllTypes();

        for (AbstractTypeDeclaration type1 : listTypesVersion1) {
            AbstractTypeDeclaration type2 = this.findTypeDeclarationInList(listTypesVersion2, type1);
            if (type2 != null) {
                String visibilityType1 = UtilTools.getVisibility(type1);
                String visibilityType2 = UtilTools.getVisibility(type2);
                if (!visibilityType1.equals(visibilityType2)) {
                    String description = this.description.visibility(UtilTools.getPath(type2), visibilityType1, visibilityType2);

                    if (UtilTools.isVisibilityPublic(type1) || (UtilTools.isVisibilityProtected(type1) && !UtilTools.isVisibilityPublic(type2))) {
                        this.addChange(type2, Category.TYPE_LOST_VISIBILITY, true, description);
                    } else {

                        Category category = UtilTools.isVisibilityDefault(type1) && UtilTools.isVisibilityPrivate(type2) ? Category.TYPE_LOST_VISIBILITY : Category.TYPE_GAIN_VISIBILITY;
                        this.addChange(type2, category, false, description);
                    }
                }
            }
        }
    }

    private void findAddedAndExtractedTypes() {
        List<AbstractTypeDeclaration> listTypesVersion2 = UtilTools.getAcessibleTypes(version2);
        for (AbstractTypeDeclaration typeVersion2 : listTypesVersion2) {
            if (!version1.containsAccessibleType(typeVersion2) && !version1.containsNonAccessibleType(typeVersion2) && !this.typesWithPathChanged.contains(UtilTools.getPath(typeVersion2))) {
                Boolean refactoring = checkAndProcessExtract(typeVersion2);
                if(!refactoring){
                    String description = this.description.addition(UtilTools.getPath(typeVersion2));
                    this.addChange(typeVersion2, Category.TYPE_ADD, false, description);
                }
            }
        }
    }


    private void findRemovedAndRenameAndMoveTypes() {
        for (AbstractTypeDeclaration type : version1.getTypesPublicAndProtected()) {
            if (!version2.containsAccessibleType(type) && !version2.containsNonAccessibleType(type)) {
                Boolean refactoring = this.checkAndProcessMove(type);
                if (!refactoring) {
                    this.processRemoveType(type);
                }
            }
        }
    }

    private Boolean isDeprecated(final AbstractTypeDeclaration type) {
        return (type != null && type.resolveBinding() != null && type.resolveBinding().isDeprecated()) ? true : false;
    }

    private String getNameSuperClass(AbstractTypeDeclaration type) {
        if (type.resolveBinding() != null && type.resolveBinding().getSuperclass() != null) {
            return type.resolveBinding().getSuperclass().getQualifiedName().toString();
        }
        return null;
    }

    private boolean containsSuperClass(AbstractTypeDeclaration type) {
        String nameSuperClass = this.getNameSuperClass(type);
        return (nameSuperClass != null && !"java.lang.Object".equals(nameSuperClass)) ? true : false;
    }

    private void diffModifierFinal(AbstractTypeDeclaration typeVersion1, AbstractTypeDeclaration typeVersion2) {

        if ((UtilTools.isFinal(typeVersion1) && UtilTools.isFinal(typeVersion2)) || ((!UtilTools.isFinal(typeVersion1) && !UtilTools.isFinal(typeVersion2)))) {
            return;
        }

        if ((!UtilTools.isFinal(typeVersion1) && UtilTools.isFinal(typeVersion2))) {
            String description = this.description.modifierFinal(UtilTools.getPath(typeVersion2), true);
            this.addChange(typeVersion2, Category.TYPE_ADD_MODIFIER_FINAL, true, description);
        } else {

            String description = this.description.modifierFinal(UtilTools.getPath(typeVersion2), false);
            this.addChange(typeVersion2, Category.TYPE_REMOVE_MODIFIER_FINAL, false, description);
        }
    }

    private void diffModifierStatic(AbstractTypeDeclaration typeVersion1, AbstractTypeDeclaration typeVersion2) {

        if ((UtilTools.isStatic(typeVersion1) && UtilTools.isStatic(typeVersion2)) || ((!UtilTools.isStatic(typeVersion1) && !UtilTools.isStatic(typeVersion2)))) {
            return;
        }

        if ((!UtilTools.isStatic(typeVersion1) && UtilTools.isStatic(typeVersion2))) {
            String description = this.description.modifierStatic(UtilTools.getPath(typeVersion2), true);
            this.addChange(typeVersion2, Category.TYPE_ADD_MODIFIER_STATIC, false, description);
        } else {

            String description = this.description.modifierStatic(UtilTools.getPath(typeVersion2), false);
            this.addChange(typeVersion2, Category.TYPE_REMOVE_MODIFIER_STATIC, true, description);
        }

    }

    private void findChangedFinalAndStatic() {
        for (AbstractTypeDeclaration typeVersion1 : version1.getTypesPublicAndProtected()) {
            AbstractTypeDeclaration typeVersion2 = version2.getVersionAccessibleType(typeVersion1);
            if (typeVersion2 != null) {
                this.diffModifierFinal(typeVersion1, typeVersion2);
                this.diffModifierStatic(typeVersion1, typeVersion2);
            }
        }
    }

    private String getSimpleNameClass(AbstractTypeDeclaration typeVersion) {
        return typeVersion.getName().toString();
    }

    private String getNamePackage(AbstractTypeDeclaration typeVersion) {
        String simpleName = this.getSimpleNameClass(typeVersion);
        String nameComplete = UtilTools.getPath(typeVersion);
        nameComplete = nameComplete.replaceAll("." + simpleName, "");
        return nameComplete;
    }
}
