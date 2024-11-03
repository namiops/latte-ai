# Archived Projects

Projects inside this folder are archived. That means they are not expected to generate
extra maintenance moving forward. There are 3 categories of projects, however, where
the normal path for depreaction would be:

```
  Deprecation => Soft Archive => Archive
```

## Deprecation
Project is scheduled to be removed in the future. Build system is maintained, including
fixes to dependency updates.

All build targets have attribute `deprecration` with a proper message set to describe what 
is happening. Preferably includes a date on when the project will be removed.

## Soft Archive

Project has all of its BUILD files set to manual, package/target visibility set to private
or the archive package subpackages only.

## Archive

Project is fully deleted and a README.md is left in place to indicate that a project did 
exist here before, with details on when it was removed for resurrection purposes via git history.
