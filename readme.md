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

# Content

Scalafix rule to help with https://github.com/getkyo/kyo/issues/903.
Changes: 
```
val x: Unit < IO = defer { ...} 
```
into
```
val x: Unit < IO = (defer { ...}).unit 
```
to avoid any issues with silently discarded effects

# Roadmap
* move the rule to kyo
* add the documentation for SBT
* detect other cases where Unit is behind a type alias
