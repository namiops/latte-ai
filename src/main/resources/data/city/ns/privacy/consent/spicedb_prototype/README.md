# SpiceDB and Authzed playground
SpiceDB is an open-source database designed to store fine-grained permissions. 
Authzed playground is an online tool allowing to develop SpiceDB schema on the fly and easily test it with example data and assertions.

You can find the `schema_playground.yaml` file in this folder which is a generated file from the Authzed playground. To use the file go to [https://play.authzed.com/schema](https://play.authzed.com/schema), click "Load from file" and choose `schema_playground.yaml`. After loading you can browse the example by going through "Schema", "Test relationships" and "Assertions" tabs.

For more in detail description of schema development the please visit [TN-0152](https://docs.google.com/document/d/1PTIB_kTFMs7bL2JZDXxZr5Mz7yKfV02OO3-1eACQSfI/edit).

## Test relationships
Authzed playground visualization focuses on representing definition relations and permissions. To make it easier to understand the relationships between the "actual" data please check the diagram below.
![Test relationships](./test_relationships_diagrams/test_relationships-Page-1.jpg)
Each entity is represented by a circle with its unique ID inside. Brackets below the circle contain which definition it's representing. Text on arrows tells us which relation it represents.
This diagram represents all of the information from the playground "Test relationships" tab.

## How assertions work
Since we have all of the actual data visualized we can now walk through how assertions work in the playground and how to read/write them.
### Resource sharing example
Let's start with simple example of resource sharing. At the bottom of `assertTrue` section we can find the following line:
```
resource:robert-calendar-1#read@user:tal
```
This can read it as "Resource of id 'robert-calendar-1' can be 'read' by user of id 'tal'".

The starting point of this assertion is `resource:robert-calendar-1`. After finding it in test relationships diagram we can start checking if the assertion is actually true.
The permission that is being checked in this example is `read` which is defined in the resource definition as:
```
permission read = reader + reader->membership + edit
```
This line means `read` is granted to entities that are related to the resource through `reader` relation **OR**("+") `membership` entities of these `reader` related **OR**("+") entities coming out of `edit` permission.\
To check which "entities" are we talking about here we have to take a look at `reader` relation in the definition.
```
relation reader: user | resource_access_group
```
In short - read can be done by users or members of resource_access_group related to the resource through `reader` relation. Additionally it can be done by something with `edit` permissions.

Going back to the diagram. We can track the relation between the `robert-calendar-1` and `tal` in the following way:
![Assertion 1](./test_relationships_diagrams/test_relationships-Page-2.drawio.png)
Which shows that `resource:robert-calendar-1#read@user:tal` is true.
### User consent example
Moving on to another example:
```
user:robert#name_consent@namespaces:demo_app
```
We can see that in this case we are trying to check if `name_consent` permission is true starting from user `robert` and finishing on `demo_app` namespace.\
Checking the definition for `name_consent`:
```
permission name_consent = personal_details_consent_sum
```
Which leads us to checking following:
```
permission personal_details_consent_sum = personal_details_consent + personal_details_consent->membership + managed_by + managed_by->membership
```
...and:
```
relation managed_by: namespaces
relation personal_details_consent: namespaces
```
Which in short translates to "name_consent permission can be given to namespaces or namespace groups related to user through `managed_by` or `personal_details_consent` relations.\
Let's check on test data if that is the case:
![Assertion 2](./test_relationships_diagrams/test_relationships-Page-3.drawio.png)
That proves that `demo_app` was given consent for accesing robert's name.
### Namespace permission example
Let's check following assertion:
```
data_attribute:name#read@namespaces:calendar_app
```
Definitions that we should focus on are as follows:
```
definition data_attribute {
	relation parent: data_kind

	permission read = parent->read
    (...)
}

definition data_kind {
	relation reader: namespaces
	(...)

	permission read = reader + reader->membership + edit
    (...)
}
```
Which can be read as "Read permission can be given to namespaces or namespace group members related through reader relation to parent data_kind of data_attribute in question. Additionally to those who can be given edit permissions"\
Following the pattern let's check how situation looks like on the actual data:
![Assertion 3](./test_relationships_diagrams/test_relationships-Page-4.drawio.png)
Which proves that
```
data_attribute:name#read@namespaces:calendar_app
```
is true.
### assertFalse section
Simirarly to previous examples lines from the assertFalse section can be checked, however lack of needed relationships between entities will prove that these are false.
