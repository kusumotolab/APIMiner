# Descriptin
## OSS
  - [OkHttp](https://github.com/square/okhttp)
  - [Retrofit](https://github.com/square/retrofit)
  - [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
  - [LeakCanary](https://github.com/square/leakcanary)
  - [Hystrix](https://github.com/Netflix/Hystrix)
  - [iosched](https://github.com/google/iosched)
  - [Fresco](https://github.com/facebook/fresco)
  - [Logger](https://github.com/orhanobut/logger)
  
### Data
- rawData_APIDiff.csv/_APIMiner.csv
  - all changes detected by APIDiff/APIMiner
- formattedData_APIDiff.csv
  - formatted rawData_APIDiff.csv to compare rawData_APIMiner.csv
- common.csv
  - detected changes by APIDiff and APIMiner
- only_APIDiff.csv
  - detected changes by APIDiff alone 
- only_APIMiner.csv
  - detected changes by APIMiner alone 
- count.csv
  - The number of detected changes by change type
- visualVM_APIDiff.nps/_APIMiner.nps
  - execution time
- validation_only_APIDiff.xlsx/_APIMiner.xlsx
  - visibly checking
- validation_count.xlsx
  - infomation about visibly checking (i.e., sample size or precision, etc.)
- validateClassification_APIMiner.xlsx
  - visibly checking whether the change is breaking change
