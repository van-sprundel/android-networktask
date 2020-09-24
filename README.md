# Android Networktask
A Network module to pull data from any API and converts it to java objects. (e.g. JSONObject, Drawables etc.)
This module was made to support the upcoming Android 11 TaskRunner. (since ASync has been deprecated)

## Installation
Add this to your **build.gradle/app:**

```implementation 'com.github.ramones156:android-networktask:version'```

You can find the current version in tags

## Usage
#### Example Kotlin:
```kotlin
  val tr = NetworkTask(RequestMethod.GET, URL)
  tr.addParameter("name",data)
  tr.executeAsync(tr, { data: NetworkResult, success: Boolean ->
    if (success) {
      try {
        val json = data.toJSONObject()
      } catch (e: JSONException) {
        e.printStackTrace()
      }
    }
  })
```
#### Example Java:
```java
  NetworkTask tr = new Networktask(RequestMethod.GET, URL);
  tr.addParameter("name",data);
  tr.executeAsync(tr, (data, success) -> {
    try {
      JSONObject json = data.toJSONObject();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  });
  ```
  
  After getting the content, you can convert the data to a java/android object in the try/catch like this:
  #### Kotlin:
  ```kotlin
  val content = json.getJSONObject(JSON_OBJECT_NAME)
  val name:String = content.optString(JSON_NAME);
  ```
  #### Java:
  ```java
    JSONObject content = json.getJSONObject(JSON_OBJECT_NAME);
    String name = content.optString(JSON_NAME);
  ```
  
       
