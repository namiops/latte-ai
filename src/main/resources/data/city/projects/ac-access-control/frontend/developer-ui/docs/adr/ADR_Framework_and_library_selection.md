# ADR-DEVELOP-0001 Framework and Build tool selection

| Status | Last Updated |
|---|---|
|Drafted| 2023-10-23 |

## Context and Problem Statement

We prefer to use [clib](https://github.com/wp-wcm/clib) as our UI component. By using it, we can easily implement UI similar to other products.
Since clib is provided for React, we will adopt React.
Therefore, we need to consider what to use for build tools.

---

## Considered Options

- Framework
    - React
- Build tools
    - [Vite](https://ja.vitejs.dev/)
    - [Turbopack](https://turbo.build/pack)
    - [ESBuild](https://esbuild.github.io/)
    - Webpack
        - Development has been ended.

### Turbopack Pros and Cons

- Pros
    - Turbopack supports hot module reloading
- Cons
    - Few examples of adoption in city repository
    - Maybe, we should use Turbopack with [Next](https://nextjs.org/)
        - Few example of Next in city repository
    

### ESBuild Pros and Cons

- Pros
    - esbuild is significantly faster than other bundlers, which can lead to improved developer productivity, especially in larger codebases.
    - A huge number of users, with weekly downloads approaching 14 million at the time of writing
    - Many example in city repository
- Cons
    - Plugin Ecosystem: As a newer tool, esbuild’s plugin ecosystem still isn’t as extensive as Webpack or Rollup's.

### Vite Pros and Cons

- Pros
    - Fast development builds using native ES modules and hot module replacement.
    - Faster than Turbopack
        - ref: [続・Turbopack vs Vite 次世代バンドルツールの競争の今](https://recruit.gmo.jp/engineer/jisedai/blog/turbopack-vs-vite-continued/)
    - According to the results of the [State of JS 2022](https://2022.stateofjs.com/ja-JP/libraries/) survey, this is the most satisfactory
    - No configuration required, saving developers time and hassle.
- Cons
    - Few examples of adoption in city repositories
    - Limited plugin ecosystem compared to other build tools.
    - Limited production build features compared to other build tools.
    - Not suitable for large monolithic applications requiring complex build processes.

I cannot start dev server properly. I can start it without error, but when I connect to it from a browser, I get a Not Found error.
    

### Reference

- [Comparing 6 JavaScript Bundling Tools: Webpack, TurboPack, Parcel, Rollup, Vite and esbuild](https://javascript.plainenglish.io/comparing-5-javascript-bundling-tools-webpack-turbopack-parcel-rollup-and-esbuild-ce9f8af4753d)

---

## Decision Outcome

I choose **ESBuild** because Vite can't start a development server in Bazel.
Also, Turbopack is recommended to be used with Next, as it is not widely adopted by City libraries.

---

## Note

- 2023-10-23 : Drafted, Originator: Daiki Watanabe