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
- evaluation.xlsx (only MPAndroidChart)
  - visibly checking API changes except for Add/Remove Type, Method, and Field
    - The other change types are likely to be mistakenly classified into Add/Remove Type, Method, and Field.
    - The numbers of detected API changes of Add/Remove Type, Method, and Field may be not useful to evaluate our tool and APIDiff.
