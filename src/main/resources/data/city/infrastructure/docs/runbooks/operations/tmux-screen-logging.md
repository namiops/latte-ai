# Record screen output during operations

## using TMUX

It is often useful to record all terminal interactions during an operation.

Described here is a way to do so using TMUX.

Once set up, you can create a new pane, toggle logging and (optionally) change
the prompt for better screen-sharing readability and time-stamping.

### Cheat cheat for when everything has been configured
These are the essentials. For more details check out the [tmux-logging] page.
| shortcut key   | action         |
| -------------- | -------------- |
| `prefix` + `H` | Change prompt  |
| `prefix` + `P` | Toggle logging |

### Required plugins

  - [tmux-logging]

### Required changes to .tmux.conf

#### Enable plugin
This requires that you can use the tmux plugin-manager

```
set -g @plugin 'tmux-plugins/tmux-logging'
```
_Don't forget to hit `prefix + I` to install the plugin_

#### Add a keyboard shortcut to change the prompt
```
# Key-bind for prod prompt (screenshare and logfriendly)
bind-key -T prefix H select-pane -P fg=colour235,bg=colour195\; set-buffer -b prodprompt "source ~/.tmux/prodprompt.zsh"\; paste-buffer -b prodprompt
```
_You might want to adjust the colors to your likings. There should be good
contrast to enhance the screen-sharing readability_

#### Helper script
The above example is for zsh. Place a simple script that sets `$PROMPT` in `~/.tmux/prodprompt.zsh"`
The prompt should include a time-stamp, so that each operation is logged with it.

Example:

```sh
PROMPT='%(?.%F{green}🍏.%F{red}🍎=%?)%F{yellow}[%F{green}%D %T%F{yellow}]⦗%F{#00afff}%n%F{white}:$(kube_ps1)$vcs_info_msg_0_%F{magenta}%~%F{yellow}⦘%f
%(!.#.$) '
```
_This example adds more details, like return code, kubernetes and git prompt. Adjust to your need_

## Shell history
### BASH
Essentially same as ZSH. TODO by somebody who uses bash.

### ZSH
You can also configure ZSH to keep a history of your commands by time-stamp.
Here some recommendations to do so:
```sh
setopt EXTENDED_HISTORY
setopt SHARE_HISTORY
setopt APPEND_HISTORY
setopt INC_APPEND_HISTORY

# Kind of unlimited history
HISTFILESIZE=1000000000
HISTSIZE=1000000000
HISTFILE=${ZDOTDIR:-$HOME}/.zsh_history
HIST_STAMPS="yyyy-mm-dd"
```
If you want to record __all__ commands, ensure that you __do not ignore duplicats__ etc
```
unsetopt HIST_EXPIRE_DUPS_FIRST
unsetopt HIST_FIND_NO_DUPS
unsetopt HIST_IGNORE_ALL_DUPS
```

<!-- Below are the links used in the document -->
[tmux-logging]:https://github.com/tmux-plugins/tmux-logging
