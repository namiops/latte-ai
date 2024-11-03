# Styleguide

Welcome to the STYLEGUIDE! Here you'll find helpful recommendations to
answer any questions that may arise during your development journey.

## Table of contents

1. [General recommendations](#general-recommendations)
    1. [Try to avoid making large files](#try-to-avoid-making-large-files)
    2. [Use `index.ts` for re-export only (when it's possible)](#use-indexts-for-re-export-only-when-its-possible)
    3. [Consider using objects for method parameters with multiple or boolean attributes](#consider-using-objects-for-method-parameters-with-multiple-or-boolean-attributes)
2. [React components](#react-components)
    1. [Stick to the Atomic Design Methodology](#stick-to-the-atomic-design-methodology)
    2. [Use custom hooks to write business logic](#use-custom-hooks-to-write-business-logic)
    3. [Name component props interface according to the component](#name-component-props-interface-according-to-the-component)
    4. [Consider using the spread operator for custom attributes](#consider-using-the-spread-operator-for-custom-attributes)
    5. [Use useQuery and useMutation for GraphQL requests](#use-usequery-and-usemutation-for-graphql-requests)
3. [E2e tests](#e2e-tests)
    1. [Test case description should meet the pattern `it should` or
       `it should not` without capital letters](#test-case-description-should-meet-the-pattern-it-should-or-it-should-not-without-capital-letters)
    2. [Test cases should be written using business logic terms not development ones](#test-cases-should-be-written-using-business-logic-terms-not-development-ones)
    3. [Avoid using qualitative characteristics like "correctly", "well", etc](#avoid-using-qualitative-characteristics-like-correctly-well-etc)
    4. [Each Test case has some assumptions, and then we build on top of the assumptions to verify the behavior](#each-test-case-has-some-assumptions-and-then-we-build-on-top-of-the-assumptions-to-verify-the-behavior)
    5. [Use
       `data-testid` attribute to define test identifiers for elements](#use-data-testid-attribute-to-define-test-identifiers-for-elements)
    6. [Use page objects to access elements on the page](#use-page-objects-to-access-elements-on-the-page)
    7. [Use fixtures for static test datasets](#use-fixtures-for-static-test-datasets)
    8. [Use Cypress commands to create top-level interactions related to Cypress](#use-cypress-commands-to-create-top-level-interactions-related-to-cypress)
    9. [Use pseudo-random values for e2e tests](#use-pseudo-random-values-for-e2e-tests)
4. [Test suites generation functions](#test-suites-generation-functions)
    1. [Functions should test some generic and repeatable functionality that happens more than 1 time](#functions-should-test-some-generic-and-repeatable-functionality-that-happens-more-than-1-time)
    2. [Functions should be independent](#functions-should-be-independent)
    3. [Functions should always return the same result with the same parameters](#functions-should-always-return-the-same-result-with-the-same-parameters)
    4. [Functions should be placed at the very end of the test suite](#functions-should-be-placed-at-the-very-end-of-the-test-suite)
    5. [Using if-conditions/loops inside functions is not prohibited, but highly unadvised](#using-if-conditionsloops-inside-functions-is-not-prohibited-but-highly-unadvised)

## General recommendations

### Try to avoid making large files

In JavaScript applications, maintaining large files that contain
utilities, constants, data, etc. can lead to clutter and make
the codebase difficult to navigate. By separating these into
distinct, logically grouped modules, you improve organization
and maintainability.

```ts
// utils/mathUtils.ts
export const add = (a, b) => a + b;
export const subtract = (a, b) => a - b;

// utils/stringUtils.ts
export const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);
export const toLowerCase = (str) => str.toLowerCase();

// constants/apiEndpoints.ts
export const USER_API = '/api/user';
export const PRODUCT_API = '/api/product';

// constants/errorMessages.ts
export const NETWORK_ERROR = 'Network error occurred';
export const VALIDATION_ERROR = 'Validation failed';
```

### Use `index.ts` for re-export only (when it's possible)

In TypeScript (or JavaScript) projects, `index.ts` files should
primarily be used for re-exporting modules. This approach keeps
valuable content in specifically named files, enhancing clarity
and organization. It also simplifies imports, making the
codebase more maintainable.

```ts
// utils/index.ts
export * from "mathUtils";
export * from "stringUtils";
```

### Consider using objects for method parameters with multiple or boolean attributes

When a method has more than three parameters or includes boolean
attributes, consider using an object to pass arguments. This
approach enhances clarity and maintainability by allowing named
parameters, making the code more self-descriptive.

```ts
// Function to configure a tooltip
function configureTooltip({text, position = 'top', isVisible = true, delay = 300}) {
  console.log(`Text: ${text}, Position: ${position}, Visible: ${isVisible}, Delay: ${delay}ms`);
  // Logic to configure and display the tooltip
}

// Usage
configureTooltip({
  text: "This is a tooltip",
  position: "bottom",
  isVisible: true,
  delay: 500,
});
```

## React components

### Stick to the Atomic Design Methodology

We are following [Atomic Design Methodology](https://atomicdesign.bradfrost.com/chapter-2/).
Please keep this in mind when creating React components to ensure consistency and modularity.

And we recommend _breaking down React components into smaller, manageable pieces_.
This approach aligns with the principles of Atomic Design, promoting reusability and easier maintenance.
By focusing on building small, functional components, you create a more scalable and flexible codebase.

```
src/
└─ components/
   ├─ atoms/
   ├─ molecules/
   ├─ organisms/
   └─ templates/
```

### Use custom hooks to write business logic

We encourage using custom hooks to separate template and business logic,
allowing for easier maintenance and clearer code structure. In that case,
the logic can be reused with another template and even mocked when needed.

Each custom hook should focus on a single task, avoiding the complexity of
a universal solution. This ensures clarity and simplicity.

```tsx
// AutoCompleteInput.jsx
interface AutoCompleteInputProps {
  palceholder: string;
  initialValue?: string;
}

export const AutoCompleteInput: React.FC<AutoCompleteInputProps> =
  ({palceholder, initialValue, ...otherProps}) => {
    const {value, handleChange} =
      useAutoCompleteInputValue(initialValue);
    const {isLoading, suggestions} = useAutoCompleteInputFetchData(value);

    return (
      <div {...otherProps}>
        <input
          type="text"
          palceholder={palceholder}
          value={value}
          onChange={handleChange}
        />
        {isLoading ? (
          <Loader/>
        ) : (
          <Suggestions
            suggestions={suggestion}
            onChange={handleChange}
          />
        )}
      </div>
    )
  }

// useAutoCompleteInputValue.ts
export const useAutoCompleteInputValue = (initialValue = "") => {
  const [value, setValue] = useState(initialValue);
  const handleChange = useCallback((e) => {
    const nextValue = e.target.value;
    setValue(nextValue);
  }, []);

  return {
    value,
    handleChange,
  }
}

// useAutoCompleteInputFetchData.ts
export const useAutoCompleteInputFetchData = (value = "") => {
  const [isLoading, setIsLoading] = useState(false);
  const [suggestions, setSuggestions] = useState([]);

  const getSuggestions = useCallback(async () => {
    setIsLoading(true);

    try {
      const response = await fetch(url);
      const result = await response.json();
      setSuggestions(result);
    } catch (error: unknown) {
      console.error("Error fetching suggestions:", error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (value.length < 3) {
      return;
    }
    void getSuggestions();
  }, [value]);

  return {
    isLoading,
    suggestions
  }
}
```

### Name component props interface according to the component

We recommend naming component props interfaces by appending the
`Props` suffix to the component name, such as `<Component>Props`.
This approach ensures unique and descriptive interface names,
especially when they need to be used outside the component.

```tsx
export interface GreetingProps {
  name: string;
}

export const Greeting: React.FC<GreetingProps> = ({name}) => (
  <h1>Welcome, {name}</h1>
);
```

### Consider using the spread operator for custom attributes

In React, you may encounter scenarios where you need to pass
along a variety of custom attributes, such as `data-*`, `aria-*`,
and other non-standard attributes. The spread operator (`...`)
can streamline this process, making your code cleaner and easier
to maintain.

```tsx
const Button = ({children, ...customAttributes}) => (
  <button {...customAttributes}>{children}</button>
);

// Usage
<Button
  data-testid="woven_ec-AddressForm_save-button"
  aria-label="Address Form Save Button"
>Save</Button>
```

### Use `useQuery` and `useMutation` for GraphQL requests

When working with a GraphQL API in a React application,
using `useQuery` and `useMutation` hooks from Apollo Client
is recommended over direct API calls. These hooks automatically
utilize the configuration provided by the `ApolloProvider`,
streamlining request management and enhancing consistency.

## E2e tests

More information
[here](https://docs.google.com/document/d/1ri2gnRthX2MeyixiYm9OJGFffdxMrk8cJ3eRaei0zWo/edit)

### Test case description should meet the pattern `it should` or `it should not` without capital letters

The row starts with the keyword `it` and the description continues the sentence.
This is a common way to describe test cases, as it explains the module's features
(what it does or doesn't do), effectively serving as documentation.

```ts
it("should display banner on the top of the page", () => {
  // ...
});
```

### Test cases should be written using business logic terms not development ones

Originally test cases should come from business requests
and verify business logic.

### Avoid using qualitative characteristics like "correctly", "well", etc

It raises questions like: what does it mean, _correctly_?
How is it checked? Be more descriptive.

### Each Test case has some assumptions, and then we build on top of the assumptions to verify the behavior

It is important to clearly make assumptions visible and
make sure they are valid before writing the actual test cases.

```ts
it("should render list of all products", () => {
  // Assumption: System contains data of 5 products 
  // <Action>: Add code to add and verify there are 5 products in system
  // Expectation: List of 5 products is rendered
  // <Action>: Add code to verify table with 5 items is rendered
});
```

### Use `data-testid` attribute to define test identifiers for elements

It works perfectly for both, cypress and jest environments.
And there is a utility `makeTestId()` that can help you with this.

```tsx
export const Greeting = ({name}) => (
  <h1>
    Welcome,
    <span data-testid={makeTestId(Greeting, "name")}>{name}</span>
  </h1>
);
```

**Note:** it will create a test ID in the format
`${TEST_ID_PREFIX}_${Component.name}_${shortId}`,
where `TEST_ID_PREFIX = "woven-ec"`.

Components from other libs can use their own test identifiers,
so we'll use `woven-ec_` prefix to recognize our own.
And `Component.name` to define component’s namespace.

### Use page objects to access elements on the page

Page Objects - encapsulate UI interactions and selector/locator
patterns of components. Good example of a Page Object is -
`Api Table`, `Pagination`, `Search`, `Filters` etc.
Useful to not repeat the same interactions multiple times.

### Use fixtures for static test datasets

Fixtures - Data required for certain test-suite/suites.
Should be one of: JS Object, JSON, JS function that returns Object.

### Use Cypress commands to create top-level interactions related to Cypress

Cypress Commands - Generic commands to work with the BE,
Test-Utils, Dates, or application-with functionality
(like authorization).

### Use pseudo-random values for e2e tests

For e2e tests, avoid using purely random values, as they can lead
to inconsistent and irreproducible results. Instead, use
pseudo-random values generated by `faker` library.

## Test suites generation functions

Functions that generate test-cases.
Useful to test the same components on every page.
Good example is - category side menu, footer, header etc.

### Functions should test some generic and repeatable functionality that happens more than 1 time

### Functions should be independent

It should not accept parameters related to the test-suite it executed in.

### Functions should always return the same result with the same parameters

### Functions should be placed at the very end of the test suite

So test-cases specific to this test-suit will be visible to any developer who reads this

### Using if-conditions/loops inside functions is not prohibited, but highly unadvised

Most likely something wrong is happening if you need to use
if-conditions/loops inside these functions. But if you have
a good case for using this - it's also OK.
