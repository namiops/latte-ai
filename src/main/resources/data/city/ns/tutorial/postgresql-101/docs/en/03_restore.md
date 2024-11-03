# Recovery
## In Progress
Unfortunately, the pgbackrest-restore functionality requires patching the image
used for restoring in a way similar to how the pgbackrest-backup image was
patched. Currently the image is unable to access the other pods when using
istio. This work is currently in progress and updates will go here once the
patching is finished.

