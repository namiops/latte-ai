<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [AMA Channel Operations](#ama-channel-operations)
  - [Information](#information)
  - [How to handle a question in the AMA Channel](#how-to-handle-a-question-in-the-ama-channel)
    - [Determine the nature of the question](#determine-the-nature-of-the-question)
    - [Solution: If you do not know the answer](#solution-if-you-do-not-know-the-answer)
    - [Solution: If you know the answer](#solution-if-you-know-the-answer)
  - [Amend or Create Runbook](#amend-or-create-runbook)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# AMA Channel Operations

## Information

| Last Update | 2024-04-24                          |
|-------------|-------------------------------------|
| Tags        | Developer Relations, Debugging, SRE |

## How to handle a question in the AMA Channel

### Determine the nature of the question

Questions typically fall into two types

**"Can I do/use/work with X?"**

These questions are advisory, or looking for additional details. Ask for the
following details:

* What is the use-case or workflow being done?
* What environment is this being done in (development, preproduction,
  production)
* Is X being used already or being considered for use?

**"X is not working"**

These questions are incidental or debugging. Ask for the following details:

* What is the environment the issue is occurring in?
* If X has an API: what is the API call?
  * Request for a `curl` with details where possible
* What are the steps to reproduce?
* Are there any additional details?
  * Logs, screenshots, or other information where possible

### Solution: If you do not know the answer

**Note: You should default to this answer unless you are 100% of the answer to
the question**

1) With the provided information, escalate the question to the responsible team
   1) The AMA Workflow will have a section in it about the problem area, which
      can help to narrow down which team to mention
   2) If the AMA Workflow is not used, determine the team to ping via the
      information gathered
2) At this point, DevRel's job is done for the time being while the issue is
   being resolved by the relative Subject-Matter Experts
3) When the thread or issue is resolved, proceed
   to [Amend or Create Runbook](#amend-or-create-runbook)

### Solution: If you know the answer

1) Answer the question concisely, use links to relative documents, runbooks,
   FAQs, or official documents that fix the issue.
2) If the answer does not appear in any runbook, faq, or other document refer
   to [Amend or Create Document](#amend-or-create-runbook)

## Amend or Create Runbook

1) If there is an existing runbook, first make a determination if the runbook
   needs to be updated.
   1) If the runbook needs updating, or if there is no
      existing runbook, create a ticket to track the creation of the runbook
2) Use one of the existing [templates](../templates) to quickly create a runbook
   and submit a PR once finished
3) Upon approval, merge the PR
