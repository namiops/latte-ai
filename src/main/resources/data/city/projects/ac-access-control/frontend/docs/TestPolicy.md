# Front End Test Policy

## Scope

- Front-End system implemented by the FSS `Access Control Team`.
- Unit Test and Integration Test by engineer.

![test strategy](./images/testStrategy.png)

## Target Coverage 

- FSS have a coverage target of `85%`.
  - There is no need to force commit, just use it as a guideline.

## Unit Test

### Test Tool

- [Vitest](https://vitest.dev/)

### Testing Subject

#### Conduct

- Component tests for common, reusable components
  - f.g. `/comopnent`
- Write unit tests for normal functions
  - f.g. `/utils`

#### Not conduct

- hooks
  - Because basicaly only call apis. If the hook file contains logic, testing should be considered.
- Other (pages, layout and so on)
  - Because that test is often broken.

### Measure Coverage 

- Coverage is measured only in the areas listed in the [Conduct](#Conduct) of the Testing Subject.
- When `creating a PR`, measure the coverage and include the results in the PR.
```
$ pnpm code:coverage
```

## Integration Test (End to End Test) 

### Test Tool

- TBD

### Testing Subject

- TBD

### Test Coverage 

- TBD
