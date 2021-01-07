package apidiff.internal.analysis;

import apidiff.Change;
import apidiff.enums.Category;
import apidiff.enums.ElementType;
import apidiff.internal.analysis.description.MethodDescription;
import apidiff.internal.util.UtilTools;
import apidiff.internal.util.UtilToolsForRef;
import apidiff.internal.visitor.APIVersion;
import gr.uom.java.xmi.UMLOperation;
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

public class MethodDiff {

    private List<Change> listChange = new ArrayList<Change>();

    private Logger logger = LoggerFactory.getLogger(MethodDiff.class);

    private MethodDescription description = new MethodDescription();

    private Map<RefactoringType, List<Refactoring>> refactorings = new HashMap<RefactoringType, List<Refactoring>>();

    private List<String> methodsWithPathChanged = new ArrayList<String>();

    private RevCommit revCommit;

    public List<Change> detectChange(final APIVersion version1, final APIVersion version2, final Map<RefactoringType, List<Refactoring>> refactorings, final RevCommit revCommit) {
        this.logger.info("Processing Methods...");
        this.refactorings = refactorings;
        this.revCommit = revCommit;
        this.findRemoveAndRefactoringMethods(version1, version2);
        this.findChangedVisibilityMethods(version1, version2);
        //this.findChangedReturnTypeMethods(version1, version2);
        this.findChangedExceptionTypeMethods(version1, version2);
        this.findChangedFinalAndStatic(version1, version2);
        this.findAddedMethods(version1, version2);
        this.findAddedDeprecatedMethods(version1, version2);
        return this.listChange;
    }

    private void addChange(final TypeDeclaration type, final MethodDeclaration method, Category category, Boolean isBreakingChange, final String description) {
        Change change = new Change();
        change.setJavadoc(UtilTools.containsJavadoc(type, method));
        change.setDeprecated(this.isDeprecated(method, type));
        change.setBreakingChange(this.isDeprecated(method, type) ? false : isBreakingChange);
        change.setPath(UtilTools.getPath(type));
        change.setElement(this.getFullNameMethod(method));
        change.setCategory(category);
        change.setDescription(description);
        change.setRevCommit(this.revCommit);
        change.setElementType(method.isConstructor() ? ElementType.CONSTRUCTOR : ElementType.METHOD);
        this.listChange.add(change);
    }

    private List<Refactoring> getChangeInNameOrPathOperation() {
        List<Refactoring> listChangeInNameOrPath = new ArrayList<Refactoring>();
        if (this.refactorings.containsKey(RefactoringType.MOVE_OPERATION)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.MOVE_OPERATION));
        }
        if (this.refactorings.containsKey(RefactoringType.RENAME_METHOD)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.RENAME_METHOD));
        }
        if (this.refactorings.containsKey(RefactoringType.PULL_UP_OPERATION)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.PULL_UP_OPERATION));
        }
        if (this.refactorings.containsKey(RefactoringType.PUSH_DOWN_OPERATION)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.PUSH_DOWN_OPERATION));
        }
        if (this.refactorings.containsKey(RefactoringType.INLINE_OPERATION)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.INLINE_OPERATION));
        }
        if (this.refactorings.containsKey(RefactoringType.MOVE_AND_INLINE_OPERATION)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.MOVE_AND_INLINE_OPERATION));
        }
        if (this.refactorings.containsKey(RefactoringType.MOVE_AND_RENAME_OPERATION)) {
            listChangeInNameOrPath.addAll(this.refactorings.get(RefactoringType.MOVE_AND_RENAME_OPERATION));
        }
        return listChangeInNameOrPath;
    }

    private List<Refactoring> getInlineOperation() {
        List<Refactoring> listInline = new ArrayList<Refactoring>();
        if (this.refactorings.containsKey(RefactoringType.INLINE_OPERATION)) {
            listInline.addAll(this.refactorings.get(RefactoringType.INLINE_OPERATION));
        }
        if (this.refactorings.containsKey(RefactoringType.MOVE_AND_INLINE_OPERATION)) {
            listInline.addAll(this.refactorings.get(RefactoringType.MOVE_AND_INLINE_OPERATION));
        }
        return listInline;
    }

    private List<Refactoring> getChangeInParameterListOperation() {
        List<Refactoring> listChangeInParameterList = new ArrayList<Refactoring>();
        if (this.refactorings.containsKey(RefactoringType.ADD_PARAMETER)) {
            listChangeInParameterList.addAll(this.refactorings.get(RefactoringType.ADD_PARAMETER));
        }
        if (this.refactorings.containsKey(RefactoringType.REMOVE_PARAMETER)) {
            listChangeInParameterList.addAll(this.refactorings.get(RefactoringType.REMOVE_PARAMETER));
        }
        if (this.refactorings.containsKey(RefactoringType.REORDER_PARAMETER)) {
            listChangeInParameterList.addAll(this.refactorings.get(RefactoringType.REORDER_PARAMETER));
        }
        if (this.refactorings.containsKey(RefactoringType.SPLIT_PARAMETER)) {
            listChangeInParameterList.addAll(this.refactorings.get(RefactoringType.SPLIT_PARAMETER));
        }
        if (this.refactorings.containsKey(RefactoringType.MERGE_PARAMETER)) {
            listChangeInParameterList.addAll(this.refactorings.get(RefactoringType.MERGE_PARAMETER));
        }
        if (this.refactorings.containsKey(RefactoringType.CHANGE_PARAMETER_TYPE)) {
            listChangeInParameterList.addAll(this.refactorings.get(RefactoringType.CHANGE_PARAMETER_TYPE));
        }
        if (this.refactorings.containsKey(RefactoringType.PARAMETERIZE_VARIABLE)) {
            listChangeInParameterList.addAll(this.refactorings.get(RefactoringType.PARAMETERIZE_VARIABLE));
        }
        return listChangeInParameterList;
    }

    private List<Refactoring> getExtractOperation() {
        List<Refactoring> listExtract = new ArrayList<Refactoring>();
        if (this.refactorings.containsKey(RefactoringType.EXTRACT_OPERATION)) {
            listExtract.addAll(this.refactorings.get(RefactoringType.EXTRACT_OPERATION));
        }
        if (this.refactorings.containsKey(RefactoringType.EXTRACT_AND_MOVE_OPERATION)) {
            listExtract.addAll(this.refactorings.get(RefactoringType.EXTRACT_AND_MOVE_OPERATION));
        }
        return listExtract;
    }

    private Category getCategory(RefactoringType refactoringType) {
        Category category;
        switch (refactoringType) {
            case ADD_PARAMETER:
            case REMOVE_PARAMETER:
            case REORDER_PARAMETER:
            case SPLIT_PARAMETER:
            case MERGE_PARAMETER:
            case CHANGE_PARAMETER_TYPE:
            case PARAMETERIZE_VARIABLE:
                category = Category.METHOD_CHANGE_PARAMETER_LIST;
                break;
            case CHANGE_RETURN_TYPE:
                category = Category.METHOD_CHANGE_RETURN_TYPE;
                break;
            case MOVE_OPERATION:
                category = Category.METHOD_MOVE;
                break;
            case PUSH_DOWN_OPERATION:
                category = Category.METHOD_PUSH_DOWN;
                break;
            case INLINE_OPERATION:
                category = Category.METHOD_INLINE;
                break;
            case RENAME_METHOD:
                category = Category.METHOD_RENAME;
                break;
            case EXTRACT_OPERATION:
            case EXTRACT_AND_MOVE_OPERATION:
                category = Category.METHOD_EXTRACT;
                break;
            case MOVE_AND_RENAME_OPERATION:
                category = Category.METHOD_MOVE_AND_RENAME;
                break;
            default:
                category = Category.METHOD_PULL_UP;
                break;
        }
        return category;
    }

    private void addChangeOfPushDown(final MethodDeclaration method, final TypeDeclaration type) {
        String fullNameMethodAndPath = this.getFullNameMethodAndPath(method, type);
        List<Refactoring> listPushDown = this.refactorings.get(RefactoringType.PUSH_DOWN_OPERATION);
        if (listPushDown != null) {
            String nameMethod="";
            String nameClassBefore = UtilTools.getPath(type);
            for (Refactoring ref : listPushDown) {
                UMLOperation operationBefore = UtilToolsForRef.getOperationBefore(ref);
                nameMethod = UtilToolsForRef.getSimpleNameMethod(operationBefore);
                String fullNameMethodAndPathBefore = UtilToolsForRef.getFullNameMethodAndPath(operationBefore);
                if (fullNameMethodAndPathBefore.equals(fullNameMethodAndPath)) {
                    UMLOperation operationAfter = UtilToolsForRef.getOperationAfter(ref);
                    String fullNameMethodAndPathAfter = UtilToolsForRef.getFullNameMethodAndPath(operationAfter);
                    String nameClassAfter = UtilToolsForRef.getClassPathOfMethod(operationAfter);
                    String description = this.description.pushDown(nameMethod, nameClassBefore, nameClassAfter);
                    this.addChange(type, method, Category.METHOD_PUSH_DOWN, true, description);
                    this.methodsWithPathChanged.add(fullNameMethodAndPathAfter);
                }
            }
        }
    }

    private void addChangeOfInline(final MethodDeclaration method, final TypeDeclaration type) {
        String fullNameMethodAndPath = this.getFullNameMethodAndPath(method, type);
        List<Refactoring> listInline = this.getInlineOperation();
        String nameMethodBefore="";
        String nameClassBefore = UtilTools.getPath(type);
        if (listInline != null) {
            for (Refactoring ref : listInline) {
                UMLOperation operationBefore = UtilToolsForRef.getOperationBefore(ref);
                nameMethodBefore = UtilToolsForRef.getSimpleNameMethod(operationBefore);
                String fullNameMethodAndPathBefore = UtilToolsForRef.getFullNameMethodAndPath(operationBefore);
                if (fullNameMethodAndPathBefore.equals(fullNameMethodAndPath)) {
                    UMLOperation operationAfter = UtilToolsForRef.getOperationAfter(ref);
                    String nameMethodAfter = UtilToolsForRef.getSimpleNameMethod(operationAfter);
                    String nameClassAfter = UtilToolsForRef.getClassPathOfMethod(operationAfter);
                    String description = this.description.inline(nameMethodBefore, nameMethodAfter,nameClassBefore, nameClassAfter);
                    this.addChange(type, method, Category.METHOD_INLINE, true, description);
                }
            }
        }
    }

    private Boolean processChangeInNameOrPath(final MethodDeclaration method, final TypeDeclaration type) {
        List<Refactoring> listChangeInNameOrPath = this.getChangeInNameOrPathOperation();
        if (listChangeInNameOrPath != null) {
            String fullNameMethodAndPath = this.getFullNameMethodAndPath(method, type);
            for (Refactoring ref : listChangeInNameOrPath) {
                UMLOperation operationBefore = UtilToolsForRef.getOperationBefore(ref);
                String fullNameMethodAndPathBefore = UtilToolsForRef.getFullNameMethodAndPath(operationBefore);
                if (fullNameMethodAndPathBefore.equals(fullNameMethodAndPath)) {
                    switch (ref.getRefactoringType()) {
                        case PUSH_DOWN_OPERATION:
                            this.addChangeOfPushDown(method, type);
                            break;
                        case INLINE_OPERATION:
                        case MOVE_AND_INLINE_OPERATION:
                            this.addChangeOfInline(method, type);
                            break;
                        default:
                            UMLOperation operationAfter = UtilToolsForRef.getOperationAfter(ref);
                            String fullNameMethodAndPathAfter = UtilToolsForRef.getFullNameMethodAndPath(operationAfter);
                            Boolean isBreakingChange = (RefactoringType.PULL_UP_OPERATION.equals(ref.getRefactoringType())) ? false : true;
                            Category category = this.getCategory(ref.getRefactoringType());
                            String description = this.description.changeInNameOrPath(category, ref);
                            this.addChange(type, method, category, isBreakingChange, description);
                            this.methodsWithPathChanged.add(fullNameMethodAndPathAfter);
                            break;
                    }
                    return true;
                }
            }
        }
        return false;
    }


    private Boolean processChangeInParameterList(final MethodDeclaration method, final TypeDeclaration type) {
        List<Refactoring> listChangeInParameterList = this.getChangeInParameterListOperation();
        if (listChangeInParameterList != null) {
            String fullNameMethodAndPath = this.getFullNameMethodAndPath(method, type);
            for (Refactoring ref : listChangeInParameterList) {
                UMLOperation operationBefore = UtilToolsForRef.getOperationBefore(ref);
                String fullNameMethodAndPathBefore = UtilToolsForRef.getFullNameMethodAndPath(operationBefore);
                if (fullNameMethodAndPathBefore.equals(fullNameMethodAndPath)) {
                    String nameMethodBefore = UtilToolsForRef.getSimpleNameMethod(operationBefore);
                    String classPathBefore = UtilToolsForRef.getClassPathOfMethod(operationBefore);
                    UMLOperation operationAfter = UtilToolsForRef.getOperationAfter(ref);
                    String fullNameMethodAndPathAfter = UtilToolsForRef.getFullNameMethodAndPath(operationAfter);
                    String nameMethodAfter = UtilToolsForRef.getSimpleNameMethod(operationAfter);
                    String classPathAfter = UtilToolsForRef.getClassPathOfMethod(operationAfter);
                    String description = this.description.parameter(nameMethodAfter, nameMethodBefore, classPathBefore,classPathAfter);
                    this.addChange(type, method, Category.METHOD_CHANGE_PARAMETER_LIST, true, description);
                    this.methodsWithPathChanged.add(fullNameMethodAndPathAfter);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finding Change Return Type
     *
     * @param method
     * @param method
     * @param type
     * @return
     */
    private Boolean processChangeReturnType(final MethodDeclaration method, final TypeDeclaration type) {
        List<Refactoring> listChangeReturnType = this.refactorings.get(RefactoringType.CHANGE_RETURN_TYPE);
        if (listChangeReturnType != null) {
            String fullNameMethodAndPath = this.getFullNameMethodAndPath(method, type);
            for (Refactoring ref : listChangeReturnType) {
                UMLOperation operationBefore = UtilToolsForRef.getOperationBefore(ref);
                String fullNameMethodAndPathBefore = UtilToolsForRef.getFullNameMethodAndPath(operationBefore);
                if (fullNameMethodAndPathBefore.equals(fullNameMethodAndPath)) {
                    UMLOperation operationAfter = UtilToolsForRef.getOperationAfter(ref);
                    String pathAndFullNameAfter = UtilToolsForRef.getFullNameMethodAndPath(operationAfter);
                    String nameMethod = UtilToolsForRef.getSimpleNameMethodWithParamName(operationAfter);
                    String nameClass = UtilToolsForRef.getClassPathOfMethod(operationAfter);
                    String description = this.description.returnType(nameMethod, nameClass);
                    this.addChange(type, method, Category.METHOD_CHANGE_RETURN_TYPE, true, description);
                    this.methodsWithPathChanged.add(pathAndFullNameAfter);
                    return true;
                }
            }
        }
        return false;
    }

    private void processRemoveMethod(final MethodDeclaration method, final TypeDeclaration type) {
        String description = this.description.remove(this.getSimpleNameMethod(method), UtilTools.getPath(type));
        this.addChange(type, method, Category.METHOD_REMOVE, true, description);
    }

    /**
     * Finding refactoring in method()
     *
     * @param method
     * @param type
     * @return
     */
    private Boolean checkAndProcessRefactoring(final MethodDeclaration method, final TypeDeclaration type) {
        Boolean changeInNameAndPath = this.processChangeInNameOrPath(method, type);
        Boolean changeInParameterList = this.processChangeInParameterList(method, type);
        Boolean changeReturnType = this.processChangeReturnType(method, type);
        return changeInNameAndPath || changeInParameterList || changeReturnType;
    }

    private boolean checkAndProcessExtractMethod(final MethodDeclaration method, final TypeDeclaration type) {
        Boolean isExist= false;
        List<Refactoring> listExtract = this.getExtractOperation();
        if (listExtract != null) {
            String fullNameMethodAndPath = this.getFullNameMethodAndPath(method, type);
            String nameMethodAfter="";
            String classPathAfter = UtilTools.getPath(type);
            for (Refactoring ref : listExtract) {
                UMLOperation operationAfter = UtilToolsForRef.getOperationAfter(ref);
                nameMethodAfter = UtilToolsForRef.getSimpleNameMethod(operationAfter);
                String fullNameMethodAndPathAfter = UtilToolsForRef.getFullNameMethodAndPath(operationAfter);
                if (fullNameMethodAndPathAfter.equals(fullNameMethodAndPath)) {
                    UMLOperation operationBefore = UtilToolsForRef.getOperationBefore(ref);
                    String nameMethodBefore = UtilToolsForRef.getSimpleNameMethod(operationBefore);
                    String classPathBefore = UtilToolsForRef.getClassPathOfMethod(operationBefore);
                    String description = this.description.extract(nameMethodBefore,classPathBefore,nameMethodAfter, classPathAfter);
                    this.addChange(type, method, Category.METHOD_EXTRACT, false, description);
                    isExist=true;
                }
            }
        }
        return isExist;
    }

    private void processAddMethod(final MethodDeclaration method, final TypeDeclaration type) {
        String nameMethod = this.getSimpleNameMethod(method);
        String nameClass = UtilTools.getPath(type);
        String description = this.description.addition(nameMethod, nameClass);
        this.addChange(type, method, Category.METHOD_ADD, false, description);
    }

    /**
     * @param parameterListException
     * @param exception
     * @return true, if the list contains the "exception"
     */
    private boolean containsExceptionList(List<SimpleType> parameterListException, SimpleType exception) {
        for (SimpleType simpleType : parameterListException) {
            if (simpleType.getName().toString().equals(exception.getName().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param methodDeclaration
     * @return true, if is a accessible field by external systems
     */
    private boolean isMethodAcessible(MethodDeclaration methodDeclaration) {
        return methodDeclaration != null && methodDeclaration.resolveBinding() != null && (UtilTools.isVisibilityProtected(methodDeclaration) || UtilTools.isVisibilityPublic(methodDeclaration)) ? true : false;
    }

    /**
     * @param listExceptionsVersion1
     * @param listExceptionsVersion2
     * @return true, if the exception does not exist in exception list 2.
     */
    private boolean diffListExceptions(List<SimpleType> listExceptionsVersion1, List<SimpleType> listExceptionsVersion2) {
        for (SimpleType exceptionVersion1 : listExceptionsVersion1) {
            if (!this.containsExceptionList(listExceptionsVersion2, exceptionVersion1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param method
     * @param type
     * @return true, method is deprecated or type is deprecated
     */
    private Boolean isDeprecated(MethodDeclaration method, AbstractTypeDeclaration type) {
        Boolean isMethodDeprecated = (method != null && method.resolveBinding() != null && method.resolveBinding().isDeprecated()) ? true : false;
        Boolean isTypeDeprecated = (type != null && type.resolveBinding() != null && type.resolveBinding().isDeprecated()) ? true : false;

        return isMethodDeprecated || isTypeDeprecated;
    }

    /**
     * Finding methods with change in exception list
     *
     * @param version1
     * @param version2
     */
    private void findChangedExceptionTypeMethods(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeVersion1 : version1.getApiAcessibleTypes()) {
            if (version2.containsAccessibleType(typeVersion1)) {
                for (MethodDeclaration methodVersion1 : typeVersion1.getMethods()) {
                    if (this.isMethodAcessible(methodVersion1)) {
                        MethodDeclaration methodVersion2 = version2.getEqualVersionMethod(methodVersion1, typeVersion1);
                        if (this.isMethodAcessible(methodVersion2)) {
                            List<SimpleType> exceptionsVersion1 = methodVersion1.thrownExceptionTypes();
                            List<SimpleType> exceptionsVersion2 = methodVersion2.thrownExceptionTypes();
                            if (exceptionsVersion1.size() != exceptionsVersion2.size() || (this.diffListExceptions(exceptionsVersion1, exceptionsVersion2))) {
                                String nameMethod = this.getSimpleNameMethod(methodVersion1);
                                String nameClass = UtilTools.getPath(typeVersion1);
                                String description = this.description.exception(nameMethod, exceptionsVersion1, exceptionsVersion2, nameClass);
                                this.addChange(typeVersion1, methodVersion1, Category.METHOD_CHANGE_EXCEPTION_LIST, true, description);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Finding methods with change in the return type
     *
     * @param version1
     * @param version2
     */
    private void findChangedReturnTypeMethods(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeVersion1 : version1.getApiAcessibleTypes()) {
            if (version2.containsAccessibleType(typeVersion1)) {
                for (MethodDeclaration methodVersion1 : typeVersion1.getMethods()) {
                    if (this.isMethodAcessible(methodVersion1)) {
                        MethodDeclaration methodVersion2 = version2.findMethodByNameAndParametersAndReturn(methodVersion1, typeVersion1);
                        if (methodVersion2 == null) {
                            methodVersion2 = version2.findMethodByNameAndParameters(methodVersion1, typeVersion1);
                            if (methodVersion2 != null) {
                                String description = this.description.returnType(this.getSimpleNameMethod(methodVersion1), UtilTools.getPath(typeVersion1));
                                this.addChange(typeVersion1, methodVersion1, Category.METHOD_CHANGE_RETURN_TYPE, true, description);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Finding methods with changed visibility
     *
     * @param typeVersion1
     * @param methodVersion1
     * @param methodVersion2
     */
    private void checkGainOrLostVisibility(TypeDeclaration typeVersion1, MethodDeclaration methodVersion1, MethodDeclaration methodVersion2) {
        if (methodVersion2 != null && methodVersion1 != null) {
            String visibilityMethod1 = UtilTools.getVisibility(methodVersion1);
            String visibilityMethod2 = UtilTools.getVisibility(methodVersion2);
            if (!visibilityMethod1.equals(visibilityMethod2)) {
                String description = this.description.visibility(this.getSimpleNameMethod(methodVersion2), UtilTools.getPath(typeVersion1), visibilityMethod1, visibilityMethod2);

                if (this.isMethodAcessible(methodVersion1) && !UtilTools.isVisibilityPublic(methodVersion2)) {
                    this.addChange(typeVersion1, methodVersion2, Category.METHOD_LOST_VISIBILITY, true, description);
                } else {

                    Category category = UtilTools.isVisibilityDefault(methodVersion1) && UtilTools.isVisibilityPrivate(methodVersion2) ? Category.METHOD_LOST_VISIBILITY : Category.METHOD_GAIN_VISIBILITY;
                    this.addChange(typeVersion1, methodVersion2, category, false, description);
                }
            }
        }
    }

    /**
     * Finding methods with changed visibility
     *
     * @param version1
     * @param version2
     */
    private void findChangedVisibilityMethods(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeVersion1 : version1.getApiAcessibleTypes()) {
            if (version2.containsAccessibleType(typeVersion1)) {
                for (MethodDeclaration methodVersion1 : typeVersion1.getMethods()) {
                    MethodDeclaration methodVersion2 = version2.getEqualVersionMethod(methodVersion1, typeVersion1);
                    this.checkGainOrLostVisibility(typeVersion1, methodVersion1, methodVersion2);
                }
            }
        }
    }

    /**
     * Finding deprecated methods
     *
     * @param version1
     * @param version2
     */
    private void findAddedDeprecatedMethods(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeVersion2 : version2.getApiAcessibleTypes()) {
            for (MethodDeclaration methodVersion2 : typeVersion2.getMethods()) {
                if (this.isMethodAcessible(methodVersion2) && this.isDeprecated(methodVersion2, typeVersion2)) {
                    MethodDeclaration methodInVersion1 = version1.findMethodByNameAndParametersAndReturn(methodVersion2, typeVersion2);
                    if (methodInVersion1 == null || !this.isDeprecated(methodInVersion1, version1.getVersionAccessibleType(typeVersion2))) {
                        String description = this.description.deprecate(this.getSimpleNameMethod(methodVersion2), UtilTools.getPath(typeVersion2));
                        this.addChange(typeVersion2, methodVersion2, Category.METHOD_DEPRECATED, false, description);
                    }
                }
            }
        }
    }

    /**
     * Finding removed methods. If class was removed, class removal is a breaking change.
     *
     * @param version1
     * @param version2
     */
    private void findRemoveAndRefactoringMethods(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeInVersion1 : version1.getApiAcessibleTypes()) {
            if (version2.containsAccessibleType(typeInVersion1)) {
                for (MethodDeclaration methodInVersion1 : typeInVersion1.getMethods()) {
                    if (this.isMethodAcessible(methodInVersion1)) {
                        MethodDeclaration methodInVersion2 = version2.findMethodByNameAndParametersAndReturn(methodInVersion1, typeInVersion1);
                        if (methodInVersion2 == null) {
                            Boolean refactoring = this.checkAndProcessRefactoring(methodInVersion1, typeInVersion1);
                            if (!refactoring) {
                                this.processRemoveMethod(methodInVersion1, typeInVersion1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Finding added methods
     *
     * @param version1
     * @param version2
     */
    private void findAddedMethods(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeInVersion2 : version2.getApiAcessibleTypes()) {
            if (version1.containsType(typeInVersion2)) {
                for (MethodDeclaration methodInVersion2 : typeInVersion2.getMethods()) {
                    if (this.isMethodAcessible(methodInVersion2)) {
                        MethodDeclaration methodInVersion1 = version1.findMethodByNameAndParametersAndReturn(methodInVersion2, typeInVersion2);
                        String fullNameAndPathMethodVersion2 = this.getFullNameMethodAndPath(methodInVersion2, typeInVersion2);
                        if (methodInVersion1 == null && !this.methodsWithPathChanged.contains(fullNameAndPathMethodVersion2)) {
                            Boolean refactoring = this.checkAndProcessExtractMethod(methodInVersion2, typeInVersion2);
                            if (!refactoring) {
                                this.processAddMethod(methodInVersion2, typeInVersion2);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Finding change in final modifier
     *
     * @param methodVersion1
     * @param methodVersion2
     */
    private void diffModifierFinal(TypeDeclaration typeVersion1, MethodDeclaration methodVersion1, MethodDeclaration methodVersion2) {

        if ((UtilTools.isFinal(methodVersion1) && UtilTools.isFinal(methodVersion2)) || ((!UtilTools.isFinal(methodVersion1) && !UtilTools.isFinal(methodVersion2)))) {
            return;
        }
        String nameClass = UtilTools.getPath(typeVersion1);
        String nameMethod = this.getSimpleNameMethod(methodVersion2);

        if ((!UtilTools.isFinal(methodVersion1) && UtilTools.isFinal(methodVersion2))) {
            String description = this.description.modifierFinal(nameMethod, nameClass, true);
            this.addChange(typeVersion1, methodVersion1, Category.METHOD_ADD_MODIFIER_FINAL, true, description);
        } else {

            String description = this.description.modifierFinal(nameMethod, nameClass, false);
            this.addChange(typeVersion1, methodVersion1, Category.METHOD_REMOVE_MODIFIER_FINAL, false, description);
        }
    }

    /**
     * Finding change in static modifier
     *
     * @param methodVersion1
     * @param methodVersion2
     */
    private void diffModifierStatic(TypeDeclaration typeVersion1, MethodDeclaration methodVersion1, MethodDeclaration methodVersion2) {

        if ((UtilTools.isStatic(methodVersion1) && UtilTools.isStatic(methodVersion2)) || ((!UtilTools.isStatic(methodVersion1) && !UtilTools.isStatic(methodVersion2)))) {
            return;
        }
        String nameClass = UtilTools.getPath(typeVersion1);
        String nameMethod = this.getSimpleNameMethod(methodVersion2);

        if ((!UtilTools.isStatic(methodVersion1) && UtilTools.isStatic(methodVersion2))) {
            String description = this.description.modifierStatic(nameMethod, nameClass, true);
            this.addChange(typeVersion1, methodVersion1, Category.METHOD_ADD_MODIFIER_STATIC, false, description);
        } else {

            String description = this.description.modifierStatic(nameMethod, nameClass, false);
            this.addChange(typeVersion1, methodVersion1, Category.METHOD_REMOVE_MODIFIER_STATIC, true, description);
        }
    }

    /**
     * Finding change in final/static modifiers
     *
     * @param version1
     * @param version2
     */
    private void findChangedFinalAndStatic(APIVersion version1, APIVersion version2) {
        for (TypeDeclaration typeVersion1 : version1.getApiAcessibleTypes()) {
            if (version2.containsType(typeVersion1)) {//Se type ainda existe.
                for (MethodDeclaration methodVersion1 : typeVersion1.getMethods()) {
                    MethodDeclaration methodVersion2 = version2.findMethodByNameAndParametersAndReturn(methodVersion1, typeVersion1);
                    if (this.isMethodAcessible(methodVersion1) && (methodVersion2 != null)) {
                        this.diffModifierFinal(typeVersion1, methodVersion1, methodVersion2);
                        this.diffModifierStatic(typeVersion1, methodVersion1, methodVersion2);
                    }
                }
            }
        }
    }


    /**
     * Returning method full name. [access modifier + return + name + (parameters list)]
     *
     * @param methodVersion
     * @return
     */
    private String getFullNameMethod(MethodDeclaration methodVersion) {
        String nameMethod = "";
        if (methodVersion != null) {
            String modifiersMethod = (methodVersion.modifiers() != null) ? (StringUtils.join(methodVersion.modifiers(), " ") + " ") : " ";
            String returnMethod = (methodVersion.getReturnType2() != null) ? (methodVersion.getReturnType2().toString() + " ") : "";
            String parametersMethod = (methodVersion.parameters() != null) ? StringUtils.join(methodVersion.parameters(), ", ") : " ";
            nameMethod = modifiersMethod + returnMethod + methodVersion.getName() + "(" + parametersMethod + ")";
        }
        return nameMethod;
    }

    /**
     * Returning method name. Example: [name(parameters list)]
     *
     * @param methodVersion
     * @return
     */
    private String getSimpleNameMethod(MethodDeclaration methodVersion) {
        String nameMethod = "";
        if (methodVersion != null) {
            String parametersMethod = (methodVersion.parameters() != null) ? StringUtils.join(methodVersion.parameters(), ", ") : " ";
            nameMethod = methodVersion.getName() + "(" + parametersMethod + ")";
        }
        return nameMethod;
    }

    /**
     * Returning type + method. Example: org.felines.Tiger#setAge(int)
     *
     * @param method
     * @param type
     * @return
     */
    private String getFullNameMethodAndPath(final MethodDeclaration method, final TypeDeclaration type) {
        String nameMethod = "";
        if (method != null) {
            String path = UtilTools.getPath(type);
            String returnMethod = (method.getReturnType2() != null) ? (method.getReturnType2().toString() + " ") : "";
            String signature = this.getSignatureMethod(method);
            nameMethod = path + "#" + returnMethod + method.getName() + "(" + signature + ")";
        }
        return nameMethod;
    }

    private String getSignatureMethod(final MethodDeclaration method) {
        String signature = "";
        if (method != null) {
            List<SingleVariableDeclaration> list = method.parameters();
            List<String> paramList = new ArrayList<String>();
            for (SingleVariableDeclaration param : list) {
                paramList.add(param.getType().toString());
            }
            signature = StringUtils.join(paramList, ", ");
        }
        return signature;
    }
}