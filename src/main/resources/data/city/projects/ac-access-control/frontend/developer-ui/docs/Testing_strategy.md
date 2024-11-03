# Front-End Testing Strategy Document

This document outlines the testing strategy for our web front-end development using TypeScript and React. We will be utilizing Jest and the Testing Library to implement tests for components.

## Component Testing

### Purpose

- **Functionality Verification**: To ensure that each component functions as intended in isolation and handles user interactions correctly.
- **Error Handling**: To check that components handle errors appropriately and display the correct feedback to the user.

### Scope

- **Event Handling**: To ensure that user actions correctly trigger the intended functionality.
- **Conditional Rendering**: To verify that components behave correctly under different conditions and render the correct output.
- **Mocking External Modules**: To mock external modules and side-effects, such as API calls, to test the component in a controlled environment.

### Sample tests

#### Button

```tsx
// Button.tsx
import React from 'react';

type Props = {
  label: string;
  onClick: () => void;
};

const Button: React.FC<Props> = ({ label, onClick }) => {
  return <button onClick={onClick}>{label}</button>;
};

export default Button;
```

```tsx
// Button.test.tsx
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Button from './Button';

describe('Button', () => {
  it('renders the button with the label', () => {
    render(<Button label="Click me" onClick={() => {}} />);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });

  it('calls the onClick handler when clicked', () => {
    const handleClick = jest.fn();
    render(<Button label="Click me" onClick={handleClick} />);
    fireEvent.click(screen.getByText('Click me'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
```

### Counter

```tsx
// Counter.tsx
import React, { useState } from 'react';
import Button from './Button';

const Counter: React.FC = () => {
  const [count, setCount] = useState(0);

  return (
    <div>
      <p>Count: {count}</p>
      <Button label="Increment" onClick={() => setCount(count + 1)} />
    </div>
  );
};

export default Counter;
```

```tsx
// Counter.test.tsx
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Counter from './Counter';

describe('Counter', () => {
  it('increments the count when the button is clicked', () => {
    render(<Counter />);
    fireEvent.click(screen.getByText('Increment'));
    expect(screen.getByText('Count: 1')).toBeInTheDocument();
  });
});
```

#### Login form

```tsx
// LoginForm.tsx
import React, { useState } from 'react';

const LoginForm: React.FC = () => {
  const [username, setUsername] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    alert(`Welcome, ${username}!`);
  };

  return (
    <form onSubmit={handleSubmit}>
      <label htmlFor="username">Username:</label>
      <input
        id="username"
        type="text"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <button type="submit">Submit</button>
    </form>
  );
};

export default LoginForm;
```

```tsx
// LoginForm.test.tsx
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import LoginForm from './LoginForm';

describe('LoginForm', () => {
  it('allows the user to login', () => {
    window.alert = jest.fn(); // Mocking the window.alert function
    render(<LoginForm />);
    fireEvent.change(screen.getByLabelText(/username/i), { target: { value: 'johndoe' } });
    fireEvent.click(screen.getByText(/submit/i));
    expect(window.alert).toHaveBeenCalledWith('Welcome, johndoe!');
  });
});
```
