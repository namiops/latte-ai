# Template Testing

This project allows you to test your HTML template with sample data using a Go program. It reads data from a JSON file, unmarshals it into a generic interface{}, parses an HTML template, and executes the template with the provided data.

## Installation

1. Clone the repository:

```shell
git clone https://github.com/yourusername/your-repository.git
```

2. Change into the project directory:

```shell
cd your-repository
```

3. Install the dependencies:

```shell
brew install go
go run render.go
```

## Usage

1. Prepare your JSON data file:

   - Create a JSON file with the required data for your template.
   - Save the file as `data.json` in the project directory.
   - Make sure the JSON structure matches the expected data structure of your template.

2. Prepare your HTML template file:

   - Create an HTML template file with the desired template structure and placeholders for data.
   - Save the file as `email_template.html` in the project directory.
   - Use Go template syntax to access and display the data in the template.

3. Run the program:

```shell
go run main.go
```

4. View the output:
   - The program will execute the template with the provided data and write the HTML output to the standard output.
   - You can view the output in the terminal or redirect it to a file for further analysis or testing.

## Example

For example, let's assume you have a `data.json` file with the following JSON content:

```json
{
  "name": "John Doe",
  "items": ["apple", "banana", "orange"],
  "hashItems": {
    "a": "apple",
    "b": "banana",
    "o": "orange"
  }
}
```

And an `email_template.html` file with the following template content:

```html
<!doctype html>
<html>
  <head>
    <title>Email Template</title>
  </head>
  <body>
    <h1>Hello, {{.name}}!</h1>
    <ul>
      {{range .items}}
      <li>{{.}}</li>
      {{end}}
    </ul>
  </body>
</html>
```

Running the program will execute the template with the provided data and produce the following HTML output:

```html
<!doctype html>
<html>
  <head>
    <title>Email Template</title>
  </head>
  <body>
    <h1>Hello, John Doe!</h1>
    <ul>
      <li>apple</li>
      <li>banana</li>
      <li>orange</li>
    </ul>
  </body>
</html>
```

You can customize the `data.json` file and `email_template.html` file according to your needs to test different data and template combinations.

## Demo

Template and output can be tested here as well : https://ataylor.io/exp/go-templates/

## License

```
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
```
