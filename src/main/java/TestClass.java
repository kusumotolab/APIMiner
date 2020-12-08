import java.util.ArrayList;
import java.util.List;

public class TestClass {
    List<Test> list = new ArrayList<Test>();
    public TestClass(){
        //addTestCaseClass();
        //addTestCaseField();
        addTestCaseMethod();
    }
    private void addTestCaseClass(){
        //move type
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1101310
        //addIfNotContain(new Test("https://github.com/SonarSource/sonarqube.git","abbf32571232db81a5343db17a933a9ce6923b44"));

        //rename type
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1117213
        //addIfNotContain(new Test("https://github.com/geoserver/geoserver.git","182f4d1174036417aad9d6db908ceaf64234fd5f"));

        //move and rename type
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1118645
        //addIfNotContain(new Test("https://github.com/square/okhttp.git","c753d2e41ba667f9b5a31451a16ecbaecdc65d80"));

        //extract supertype
        //addIfNotContain(new Test("https://github.com/mockito/mockito.git","7f20e63a7252f33c888085134d16ee8bf45f183f"));

        //extract interface
        //addIfNotContain(new Test("https://github.com/Graylog2/graylog2-server.git","72acc2126611f0bff9b672de18b9b2f8dacdc03a"));

        //extract type
        //addIfNotContain(new Test("https://github.com/robovm/robovm.git","bf5ee44b3b576e01ab09cae9f50300417b01dc07"));

        //extract subtype
        //addIfNotContain(new Test("https://github.com/linkedin/rest.li.git","ec5ea36faa3dd74585bb339beabdba6149ed63be"));
    }
    private void addTestCaseField(){
        //move field, change in field type
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1117304
        //addIfNotContain(new Test("https://github.com/graphhopper/graphhopper.git","7f80425b6a0af9bdfef12c8a873676e39e0a04a6"));

        //rename field


        //pull up field
        //addIfNotContain(new Test("https://github.com/Activiti/Activiti.git","53036cece662f9c796d2a187b0077059c3d9088a"));

        //push down field
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1143517
        //addIfNotContain(new Test("https://github.com/BuildCraft/BuildCraft.git","a5cdd8c4b10a738cb44819d7cc2fee5f5965d4a0"));

        //extract field
        //addIfNotContain(new Test("https://github.com/brianfrankcooper/YCSB.git","0b024834549c53512ef18bce89f60ef9225d4819"));

        //move and rename field
    }
    private void addTestCaseMethod(){
        //move method
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1119656
        //addIfNotContain(new Test("https://github.com/rackerlabs/blueflood.git","c76e6e1f27a6697b3b88ad4ed710441b801afb3b"));

        //rename method, extract method
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1117765
        //addIfNotContain(new Test("https://github.com/GoClipse/goclipse.git","851ab757698304e9d8d4ae24ab75be619ddae31a"));

        //extract method
        //addIfNotContain(new Test("https://github.com/robovm/robovm.git","bf5ee44b3b576e01ab09cae9f50300417b01dc07"));

        //pull up method
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1108762
        //addIfNotContain(new Test("https://github.com/raphw/byte-buddy.git","f1dfb66a368760e77094ac1e3860b332cf0e4eb5"));

        //push down method
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1143517
        //addIfNotContain(new Test("https://github.com/BuildCraft/BuildCraft.git","a5cdd8c4b10a738cb44819d7cc2fee5f5965d4a0"));

        //inline method
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1130859
        //addIfNotContain(new Test("https://github.com/katzer/cordova-plugin-local-notifications.git","51f498a96b2fa1822e392027982c20e950535fd1"));

        //move and rename method
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1123770
        //addIfNotContain(new Test("https://github.com/bitcoinj/bitcoinj.git","a6601066ddc72ef8e71c46c5a51e1252ea0a1af5"));

        //add parameter, remove parameter, change parameter type
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1122164
        //addIfNotContain(new Test("https://github.com/neo4j/neo4j.git","001de307492df8f84ad15f6aaa0bd1e748d4ce27"));

        //parametrize variable, merge parameter
        //http://refactoring.encs.concordia.ca/oracle/commit-refactorings/1121732
        addIfNotContain(new Test("https://github.com/apache/drill.git","ffae1691c0cd526ed1095fbabbc0855d016790d7"));

        //change return type
        //addIfNotContain(new Test("https://github.com/linkedin/rest.li.git","ec5ea36faa3dd74585bb339beabdba6149ed63be"));

        //move method, change in return type, rename method,rename parameter, change parameter type, extract and move method, extract method, add parameter, move and rename method
        //addIfNotContain(new Test("https://github.com/graphhopper/graphhopper.git","7f80425b6a0af9bdfef12c8a873676e39e0a04a6"));


        //reorder parameter
        //addIfNotContain(new Test("https://github.com/facebook/facebook-android-sdk.git","19d1936c3b07d97d88646aeae30de747715e3248"));
    }

    private void addIfNotContain(Test test){
        if(!list.contains(test)){
            list.add(test);
        }
    }
    public void executeTest(){
        for(Test test:list){
            test.detectAtCommit();
        }
    }
}
