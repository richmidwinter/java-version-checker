# java-version-checker

### Summary

Checks a java version against the latest available, online or cacched, and returns a boolean indicating if an upgrade is available with a list of messages that include such details as the newest available version. Checks against major and minor version numbers.


### Use

```
JavaVersion.check(javaVersion)
    .getMessages()
    .forEach(System.out::println);
```

You can run the jar as a main class and it will check the JVM it runs in.

### Maven

```
<dependency>
  <groupId>uk.co.wansdykehouse</groupId>
  <artifactId>java-version-checker</artifactId>
  <version>0.5</version>
</dependency>
```
