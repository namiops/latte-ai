## Setting for vscode

### Code formatting

1. create .vscode directory.
2. create settings.json file.
    1. This settings file works for only `developer-ui`.


Following code is recommended.
```json
{
    "editor.tabSize": 2,
    "editor.codeActionsOnSave": {
        "source.fixAll.eslint": true
    },
    "editor.formatOnType": true,
    "editor.formatOnPaste": true,
}
```