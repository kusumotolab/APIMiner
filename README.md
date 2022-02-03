# APIMiner

APIMiner is a fork for [APIDiff](https://github.com/aserg-ufmg/apidiff). APIMiner is a tool to identify API breaking and maintainig changes between two versions of a Java library. APIMiner analyses libraries hosted on the distributed version control system _git_. While APIDiff uses [RefDiff](https://github.com/aserg-ufmg/RefDiff) to detect refactorings between two versions of a Java library,  APIMiner uses [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner).

## Catalog

_Breaking Changes_ are modifications performed in API elements such as types, methods, and fields that may break client applications:

| Element  | Breaking Changes (BC) |
| ------------- | ------------- |
| Type  | rename, move, move and rename, remove, lost visibility, add final modifier,  remove static modifier, change in supertype, remove supertype, extract type, extract subtype |
| Method  | move, rename, remove, push down, inline, change in parameter list, change in exception list, change in return type, lost visibility, add final modifier, remove static modifier, move and rename  | 
| Field  |  remove, move, push down field, change in default value, change in type field,  lost visibility, add final modifier, rename, move and rename | 

_Maintainig-breaking Changes_ are modifications that do not break clients:

| Element  | Maintaing Changes (MC) |
| ------------- | ------------- |
| Type  | add, extract supertype, gain visibility, remove final modifier, add static modifier, add supertype, deprecated type|
| Method  | pull up, gain visibility, remove final modifier, add static modifier, deprecated method, add, extract| 
| Field  | pull up, add, deprecated field, gain visibility, remove final modifier, extract field |


The refactorings catalog is reused from [APIDiff](https://github.com/aserg-ufmg/apidiff). The refactorings only APIMiner can detect is reused form [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner).

## Examples

* Detecting changes in version histories:

```java
APIMiner diff = new APIMiner("bumptech/glide", "https://github.com/bumptech/glide.git");
diff.setPath("/home/projects/github");

Result result = diff.detectChangeAllHistory("master", Classifier.API);
for(Change changeMethod : result.getChangeMethod()){
    System.out.println("\n" + changeMethod.getCategory().getDisplayName() + " - " + changeMethod.getDescription());
}
```
* Detecting changes in specific commit:

```java
APIMiner diff = new APIMiner("mockito/mockito", "https://github.com/mockito/mockito.git");
diff.setPath("/home/projects/github");

Result result = diff.detectChangeAtCommit("4ad5fdc14ca4b979155d10dcea0182c82380aefa", Classifier.API);
for(Change changeMethod : result.getChangeMethod()){
    System.out.println("\n" + changeMethod.getCategory().getDisplayName() + " - " + changeMethod.getDescription());
}
```
* Fetching new commits:

```java
APMiner diff = new APIMiner("bumptech/glide", "https://github.com/bumptech/glide.git");
diff.setPath("/home/projects/github");
    
Result result = diff.fetchAndDetectChange(Classifier.API);
for(Change changeMethod : result.getChangeMethod()){
    System.out.println("\n" + changeMethod.getCategory().getDisplayName() + " - " + changeMethod.getDescription());
}
```

* Writing a CSV file:

```java
APIMiner diff = new APIMiner("mockito/mockito", "https://github.com/mockito/mockito.git");
diff.setPath("/home/projects/github");
Result result = diff.detectChangeAtCommit("4ad5fdc14ca4b979155d10dcea0182c82380aefa", Classifier.API);
		
List<String> listChanges = new ArrayList<String>();
listChanges.add("Category;isDeprecated;containsJavadoc");
for(Change changeMethod : result.getChangeMethod()){
    String change = changeMethod.getCategory().getDisplayName() + ";" + changeMethod.isDeprecated()  + ";" + changeMethod.containsJavadoc() ;
    listChanges.add(change);
}
UtilFile.writeFile("output.csv", listChanges);
```

* Filtering Packages according to their names:

```java 
Classifier.INTERNAL: Elements that are in packages with the term "internal".

Classifier.TEST: Elements that are in packages with the terms "test"|"tests", or is in source file "src/test", or ends with "test.java"|"tests.java".

Classifier.EXAMPLE: Elements that are in packages with the terms "example"|"examples"|"sample"|"samples"|"demo"|"demos"

Classifier.EXPERIMENTAL: Elements that are in packages with the term "experimental".

Classifier.NON_API: Internal, test, example or experimental elements.

Classifier.API: Elements that are not non-APIs.
``` 

## Usage

In order to use APIMiner as a maven dependency in your project, add the following snippet to your project's build configuration file:

```xml
<repositories>
   <repository>
        <id>apiminer</id>
        <url>https://github.com/kusumotolab/APIMiner/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.m-iriyam</groupId>
        <artifactId>apiminer</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
