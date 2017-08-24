 Auto Update Demo for Android app
====

`How to using this demo`
----

  `first` get remote versioncode 
  
  `second` get local VersionCode 
  
  `third` compare  VersionCode and then start downLoadApk Task 
  
   when downLoad success install Apk 
  
  
  
`Notes`
----
    `first` Dialog mast show on the activity , so when you build Dialog should using Activity context ,but not getApplicationContext
    
    `second` remote apk and local apk must have a same signed key , if not you will install remote apk failed
