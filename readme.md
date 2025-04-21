# Scalafix rules for kyo

# Tests
```
sbt ~tests/test
```

# Use
```
sbt rules/publishLocal
```

## using Scala-CLI
add
```
//> using scalafixDependency com.github.ahoy-jon::kyo-scalafix-rules:0.1.0-SNAPSHOT
```
add
```
rules = [
  Kyo
]
```
run
```
scala-cli fix . --power
```



