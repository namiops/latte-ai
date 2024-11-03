# Data team Rust style guide

## Prefer to use `to_owned`/`as_ref` instead of `to_string`/`as_str`

**Rationale:** The main difference between `String` and `&str` is the ownership. As such, `to_owned`
and `as_ref` more closely match the semantics of what we are trying to do in the conversion.

## Prefer custom "conversion" methods instead of implementing `From`/`Into`

Only implement `From`/`Into` when the conversion is called from a generic context. For all other
cases, prefer to implement a custom `MyType::to_some_other_type` method.

**Rationale:** Seeing a `into()` or `from()` method call in code, it can be hard to see which types
are being converted to/from. (It also actually involves writing _more_ code ;p)

## Prefer fine-grained, local error types

Define error types close to the code where the errors occur (i.e. the code that emits errors of that
type). Keep the error type specific and focused to the logic of that unit. As a rule of thumb, an
error type per module is a good starting point.

Don't use one error type to cover error cases across a wide range of the code base (e.g. several
modules).

**Rationale:** Error types and their enum variants are closer to the code where they occur. Their
responsibilities are clearer. Conversions into the error type and from it to other types can be
focused and don't need to cover a large number of irrelevant cases.

## For tests, prefer `Result`-returning test functions, but panicking assertions

Each test case function should return a `Result` that makes it easy to use the `?` on
`Result`-returning expressions. A generic option for the return type is `Result<(), Box<Error>>`.
See [here][result-returning] for technical details.

[result-returning]: https://ebarnard.github.io/2019-06-03-rust-smaller-trait-implementers-docs/edition-guide/rust-2018/error-handling-and-panics/question-mark-in-main-and-tests.html

**Rationale:** This allows us to use the `?` operator when invoking function (e.g. HTTP requests)
instead of having to use .unwrap()/.expect() in the test function, while keeping assertions simple.

Use built-in assertions/macros (panic!, assert!, and assert_eq!), directly in the test function.
This is the basic Rust testing style introduced in [the Rust Book][rust-book-ch11].

[rust-book-ch11]: https://doc.rust-lang.org/book/ch11-01-writing-tests.html

**Rationale:** This makes assertions obvious and requires readers to understand fewer abstractions
to grasp what is being asserted.

## Prefer focused test cases, avoiding repeated assertions

Each test case should be focused on asserting one "thing". Ideally this should be one assertion or
a small group of assertions, and they should be at the end of the test case.

Avoid re-asserting properties in multiple test cases if they're not expected to behave differently
between those test cases.

**Rationale:** This keeps duplication of assertion code low, making test cases easier to understand.
It also minimizes the number of test cases that need to be adjusted for a behavior change in the
main code.
