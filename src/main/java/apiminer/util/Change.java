package apiminer.util;

import apiminer.enums.Category;
import apiminer.enums.ElementType;
import org.eclipse.jgit.revwalk.RevCommit;

public class Change {
    private String originalPath;
    private String nextPath;
    private String originalElement;
    private String nextElement;
    private String element;
    private Category category;
    private Boolean breakingChange;
    private String description;
    private Boolean javadoc;
    private Boolean deprecated;
    private RevCommit revCommit;
    private ElementType elementType;

    public Change(RevCommit revCommit){
        this.revCommit = revCommit;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getNextPath() {
        return nextPath;
    }

    public void setNextPath(String nextPath) {
        this.nextPath = nextPath;
    }

    public String getOriginalElement() {
        return originalElement;
    }

    public void setOriginalElement(String originalElement) {
        this.originalElement = originalElement;
    }

    public String getNextElement() {
        return nextElement;
    }

    public void setNextElement(String nextElement) {
        this.nextElement = nextElement;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getBreakingChange() {
        return breakingChange;
    }

    public void setBreakingChange(Boolean breakingChange) {
        this.breakingChange = breakingChange;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(Boolean javadoc) {
        this.javadoc = javadoc;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public void setRevCommit(RevCommit revCommit) {
        this.revCommit = revCommit;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }
}
