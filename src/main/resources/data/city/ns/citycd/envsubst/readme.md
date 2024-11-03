# envsubst

`envsubst` is a Go package for expanding variables in a string using `${var}` syntax.
Includes support for bash string replacement functions.

*Note: This is a fork of [drone/envsubst](https://github.com/drone/envsubst/).*

## Supported Functions

| __Expression__                | __Meaning__                                                         |
|-------------------------------|---------------------------------------------------------------------|
| `${var}`                      | Value of `$var`                                                     |
| `${#var}`                     | String length of `$var`                                             |
| `${var^}`                     | Uppercase first character of `$var`                                 |
| `${var^^}`                    | Uppercase all characters in `$var`                                  |
| `${var,}`                     | Lowercase first character of `$var`                                 |
| `${var,,}`                    | Lowercase all characters in `$var`                                  |
| `${var'}`                     | Wrap `$var` with single quotes, escaping `'` and `\` characters     |
| `${var"}`                     | Wrap `$var` with double quotes, escaping `"` and `\` characters     |
| `${var:n}`                    | Offset `$var` `n` characters from start                             |
| `${var:n:len}`                | Offset `$var` `n` characters with max length of `len`               |
| `${var#pattern}`              | Strip shortest `pattern` match from start                           |
| `${var##pattern}`             | Strip longest `pattern` match from start                            |
| `${var%pattern}`              | Strip shortest `pattern` match from end                             |
| `${var%%pattern}`             | Strip longest `pattern` match from end                              |
| `${var-default}`              | If `$var` is not set, evaluate expression as `$default`             |
| `${var:-default}`             | If `$var` is not set or is empty, evaluate expression as `$default` |
| `${var=default}`              | If `$var` is not set, evaluate expression as `$default`             |
| `${var:=default}`             | If `$var` is not set or is empty, evaluate expression as `$default` |
| `${var/pattern/replacement}`  | Replace as few `pattern` matches as possible with `replacement`     |
| `${var//pattern/replacement}` | Replace as many `pattern` matches as possible with `replacement`    |
| `${var/#pattern/replacement}` | Replace `pattern` match with `replacement` from `$var` start        |
| `${var/%pattern/replacement}` | Replace `pattern` match with `replacement` from `$var` end          |

For a deeper reference, see [bash-hackers](https://wiki.bash-hackers.org/syntax/pe#case_modification)
or [gnu pattern matching](https://www.gnu.org/software/bash/manual/html_node/Pattern-Matching.html).

## Strict mode

Use `--strict` if you want `envsubst` to fail, if an undefined variable gets accessed. By default, undefined variables
are substituted with an empty string, but when strict is enabled, an error is thrown when encountering an undefined
variable.

> [!IMPORTANT]   
> In CityCD we currently **always** enable strict mode for all applications. Please be aware of this.
> An example error would be: `✗ variable not set (strict mode): "my_undefined_var"`

## Avoid Substitution

To avoid variable substitution you can use the following formats:

* $var
* $${var}

Variable substitution requires using curly brackets. It is recommended that if you do not want expansion and wish to
keep the literal string, then avoid using curly brackets entirely. The string `$var` will stay as `$var` after
substitution.

In the event you need to keep the curly brackets in the literal string, you can add another $ to escape the
substitution. In other words, `$${var}` will become `${var}` after substitution.

## Unsupported Functions

* `${var+default}`
* `${var:?default}`
* `${var:+default}`
