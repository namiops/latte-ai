# Setting for vscode

## Code formatting

1. create .vscode directory.
2. create settings.json file.
    1. This settings file works for only `visitor-user-app`.

Recommended settings:

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
