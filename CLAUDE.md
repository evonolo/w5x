# Claude Working Instructions

## 1. Purpose

This repository follows a Document Driven Development approach.

Project knowledge is made explicit, versioned and maintained in the repository.
Documentation, implementation and tests form one consistent system.

Claude must use authoritative project knowledge to understand intended behaviour
before making non-trivial changes.

Claude must also feed relevant new knowledge back into the documentation when
new requirements, rules, decisions, terminology or constraints emerge during
the work.

`CLAUDE.md` defines the universal working model for Claude in this repository.

It must remain independent of:

* programming languages,
* frameworks,
* infrastructure technologies,
* build tools,
* concrete repository layouts,
* and project-specific domain knowledge.

Technology-specific, architectural and project-local rules belong under
`.claude/`.

---

## 2. Core Principle

The repository is the authoritative and versioned source of project knowledge.

Chats, tickets, meetings, prompts and informal discussions may introduce new
information, but they are not automatically authoritative project knowledge.

Relevant information must be:

1. identified,
2. classified,
3. validated where necessary,
4. written into the appropriate authoritative document,
5. and kept consistent with implementation and tests.

Code must not become the only place where important project knowledge exists.

---

## 3. Separation of Concerns

The project distinguishes between four kinds of information.

### 3.1 Working instructions

Describe how Claude works in the repository.

Examples:

* this `CLAUDE.md`,
* methodology modules under `.claude/`,
* project-local agent rules.

### 3.2 Authoritative project knowledge

Describes what the system is, why it exists and how it is expected to behave.

Examples:

* vision,
* domain models,
* requirements,
* business rules,
* accepted decisions,
* architecture,
* APIs,
* workflows,
* UI specifications,
* quality constraints.

### 3.3 Supporting context

Provides background but does not independently authorize implementation.

Examples:

* research,
* ideas,
* explorations,
* meeting notes,
* drafts,
* alternatives,
* experiments.

### 3.4 Implementation evidence

Shows how the current system behaves.

Examples:

* source code,
* tests,
* schemas,
* configuration,
* generated contracts,
* runtime behaviour.

Implementation evidence does not automatically override authoritative project
knowledge.

---

## 4. Authority by Concern

Do not use one universal priority list for all documents.

Use the authoritative source that owns the relevant concern.

### 4.1 Product direction

Authoritative sources may include:

* product vision,
* goals,
* non-goals,
* defined scope,
* accepted roadmap or release scope.

Product vision guides interpretation and prioritization but does not by itself
authorize an implementation task.

### 4.2 Domain behaviour

Authoritative sources may include:

* domain documents,
* domain models,
* object-specific rules,
* cross-cutting business rules,
* accepted domain decisions,
* feature specifications.

### 4.3 Architecture

Authoritative sources may include:

* accepted Architecture Decision Records,
* architecture documents,
* system context,
* component boundaries,
* integration specifications,
* deployment architecture.

### 4.4 Interfaces and interaction

Authoritative sources may include:

* API contracts,
* event contracts,
* workflows,
* state models,
* UI specifications,
* user flows.

### 4.5 Engineering conventions

Authoritative sources may include:

* project-local standards,
* selected methodology modules,
* selected capability rules,
* established implementation conventions.

Specific rules override general rules only when both govern the same concern.

A technical convention must never override a business requirement merely
because it is more specific in wording.

---

## 5. Modular Rule System

The universal working model is defined by this file.

Additional behaviour may be composed through modules under `.claude/`.

A recommended structure is:

```text
.claude/
  methodology/
  capabilities/
  profiles/
  project/
  rules/
  commands/
  agents/
  templates/
```

Possible responsibilities:

```text
.claude/methodology/
  documentation.md
  requirements.md
  adr.md
  testing.md
  traceability.md
  review.md

.claude/capabilities/
  api-first.md
  security.md
  event-driven.md
  relational-database.md
  web-frontend.md

.claude/profiles/
  prototype.md
  enterprise-application.md
  reusable-library.md

.claude/project/
  repository-layout.md
  build-and-test.md
  naming.md
  module-boundaries.md
  current-constraints.md
```

Modules must not duplicate authoritative project knowledge.

They define working conventions, reusable capability rules or project-local
constraints.

Concrete business requirements belong in the project documentation.

---

## 6. Task Entry

At the beginning of each task, determine the task source.

Possible sources include:

* explicit user request,
* changed documentation,
* referenced feature specification,
* new requirement,
* defect or failing test,
* maintenance request,
* refactoring request,
* design result,
* architectural decision,
* research finding.

Do not assume every task is driven by uncommitted documentation changes.

Run documentation diffs only when relevant, for example when:

* the user asks to implement changed documentation,
* documentation changes are present and related to the task,
* or pending specification changes must be identified.

For a specifically referenced document, begin with that document and load only
the directly relevant context.

---

## 7. Knowledge Ingestion

New information introduced during a task must be evaluated as potential project
knowledge.

This includes information introduced through:

* user prompts,
* conversations,
* issue descriptions,
* design discussions,
* code reviews,
* test failures,
* implementation discoveries,
* stakeholder feedback,
* external constraints,
* or existing undocumented behaviour.

For each relevant new item, classify it as one of the following:

* accepted requirement,
* proposed requirement,
* clarification,
* domain concept,
* domain rule,
* cross-cutting business rule,
* quality requirement,
* technical constraint,
* architecture decision,
* interface or contract change,
* terminology change,
* assumption,
* idea,
* research finding,
* open question,
* defect,
* implementation detail.

Do not silently convert an idea, assumption or research finding into an
accepted requirement.

---

## 8. Requirement Intake

When the user introduces a new requirement, determine whether it affects:

* externally observable behaviour,
* domain behaviour,
* business rules,
* terminology,
* architecture,
* interfaces,
* workflows,
* data,
* security,
* operations,
* quality attributes,
* or project scope.

If the requirement is relevant and sufficiently clear, it must be added to the
appropriate authoritative project documentation.

The documentation update must occur:

* before implementation when the requirement materially defines new behaviour,
* or together with implementation when the change is small and unambiguous.

Implementation must not become the only record of the requirement.

A new requirement may be treated as accepted when:

* the user explicitly instructs Claude to implement it,
* the request is sufficiently clear,
* it does not conflict with authoritative project knowledge,
* and it does not require a separate approval process defined by project rules.

When a new request is exploratory, optional, hypothetical or presented as an
idea, record it only in a suitable non-authoritative location unless the user
explicitly adopts it.

---

## 9. Requirement Placement

Place new knowledge in the document that owns the concern.

Examples:

```text
Product goal or non-goal
→ product or vision documentation

Domain concept or terminology
→ glossary or domain documentation

Object-specific business rule
→ the relevant domain object document

Cross-cutting business rule
→ cross-cutting rules documentation

Feature behaviour
→ feature specification

Quality requirement
→ requirements or quality documentation

API change
→ API contract or interface specification

Workflow or state transition
→ flow or workflow documentation

UI behaviour
→ UI specification

Significant architectural decision
→ ADR and affected architecture documentation

Technical working convention
→ project-local or capability rule under .claude/

Unresolved question
→ open-questions documentation

Idea or research finding
→ research, ideas or backlog documentation
```

Do not create a new document when an existing authoritative document clearly
owns the information.

Do not duplicate the same requirement in multiple documents.

Use references where multiple documents depend on the same knowledge.

---

## 10. Requirement Quality

Before treating a new requirement as an implementation source, check that it is:

* understandable,
* sufficiently precise,
* internally consistent,
* testable where applicable,
* correctly scoped,
* and assigned to an authoritative location.

When useful, requirements should state:

* intent,
* affected actor or system,
* preconditions,
* expected behaviour,
* business rules,
* constraints,
* acceptance criteria,
* relevant examples,
* and non-goals.

Do not invent missing business rules merely to make a requirement implementable.

If missing information prevents a safe implementation, report the gap.

If a reasonable, low-risk interpretation is possible and the user has already
requested implementation, document the interpretation as an explicit
assumption and proceed only when project rules permit it.

---

## 11. Context Selection

Read the minimum authoritative context required for the task.

Start with:

1. the explicit task,
2. the directly affected feature or domain document,
3. relevant business rules,
4. applicable accepted decisions,
5. applicable interface, architecture or UI documents,
6. relevant project-local rules,
7. the affected implementation and tests.

Do not load unrelated documents merely because they exist.

Follow references only when needed to:

* understand the task,
* validate a dependency,
* resolve a contradiction,
* or assess impact.

Prefer focused documents over large general documents.

---

## 12. Change Classification

When documentation changes or new knowledge are part of the task, classify each
relevant item.

Possible classifications include:

* accepted requirement,
* proposed requirement,
* domain model change,
* business rule change,
* accepted decision,
* proposed decision,
* architecture change,
* interface change,
* workflow change,
* clarification,
* terminology update,
* technical constraint,
* idea,
* research finding,
* assumption,
* open question,
* obsolete information.

Only the following may directly drive implementation:

* explicit user instructions,
* accepted requirements,
* authoritative specifications,
* accepted decisions,
* confirmed defect descriptions,
* and applicable project rules.

Ideas, drafts, research findings and open questions must not be implemented
unless explicitly adopted.

---

## 13. Consistency Rules

Authoritative documentation, code and tests must remain consistent.

When they differ, do not automatically assume that one side is correct.

Determine whether:

* the implementation is incorrect,
* the documentation is outdated,
* the tests are outdated,
* the difference is intentional,
* the requirement changed,
* or required information is missing.

Do not silently resolve material contradictions.

Stop and report a contradiction when it prevents safe and unambiguous work.

Minor inconsistencies that can be resolved directly from a clear authoritative
source may be corrected as part of the task.

When a new accepted requirement supersedes existing documented behaviour:

1. update the authoritative requirement,
2. update affected rules and specifications,
3. update or supersede affected decisions where necessary,
4. implement the change,
5. update tests,
6. perform a final consistency check.

---

## 14. Impact Analysis

Perform a proportional impact analysis before non-trivial changes.

### 14.1 Lightweight impact analysis

Use for local, low-risk changes.

Identify:

* affected code,
* affected tests,
* documentation impact.

### 14.2 Full impact analysis

Use for changes involving domain behaviour, features, APIs, architecture,
persistence, security, workflows or multiple modules.

Identify:

* affected goals or scope,
* affected domain concepts,
* affected business rules,
* affected feature specifications,
* affected decisions,
* affected architecture,
* affected interfaces,
* affected workflows and UI behaviour,
* affected data and migrations,
* affected backend modules,
* affected frontend modules,
* affected tests,
* affected documentation,
* compatibility impact,
* operational impact,
* security and privacy impact.

The analysis may remain concise when the impact is narrow and obvious.

---

## 15. Approval Rules

Proceed without additional approval when:

* the user explicitly requested implementation,
* the requirement is clear,
* relevant knowledge has been classified,
* authoritative documentation is consistent,
* the change remains within scope,
* and no destructive or high-risk action is required.

Present a plan and request clarification or approval when:

* material ambiguity exists,
* authoritative sources conflict,
* the task would materially expand scope,
* a breaking interface change is required,
* an accepted decision must be superseded,
* a destructive migration is required,
* data loss is possible,
* legal, security or privacy implications are unclear,
* or multiple materially different interpretations remain valid.

Do not request approval merely because an implementation plan was produced.

---

## 16. Domain Modelling

Business concepts take precedence over accidental technical representations.

Before changing domain behaviour, understand the relevant:

* entities,
* value objects,
* aggregates where applicable,
* domain services,
* business rules,
* invariants,
* workflows,
* states,
* transitions,
* relationships,
* events,
* and terminology.

Do not introduce technical implementation details into domain documentation.

Do not derive business behaviour solely from the existing code when
authoritative domain documentation exists.

Do not invent business rules.

---

## 17. Rules Convention

Object-specific business rules belong in the authoritative documentation of the
corresponding domain concept.

Cross-cutting rules belong in dedicated cross-cutting rule documents.

Do not create a separate rule file for a single domain object when the rule can
be maintained coherently in that object's domain documentation.

Business rules should be:

* explicit,
* testable where possible,
* uniquely identifiable where traceability is required,
* and independent of technical implementation details.

---

## 18. ADR Handling

Accepted ADRs are binding for the concerns they address.

Rejected and superseded ADRs are historical context and must not drive new
implementation.

Create or propose an ADR for significant and durable decisions that:

* have meaningful alternatives,
* affect multiple modules or systems,
* impose long-term constraints,
* establish a major domain-modelling approach,
* affect security, deployment or integration strategy,
* or are costly to reverse.

Do not create ADRs for:

* routine implementation details,
* ordinary business rules,
* simple naming decisions,
* or changes already fully governed by an existing accepted standard.

When a task requires deviation from an accepted ADR:

1. identify the conflict,
2. explain why the existing decision is insufficient,
3. propose a new or superseding ADR,
4. update affected architecture documentation,
5. do not silently implement the deviation.

---

## 19. Implementation Workflow

For non-trivial changes, follow this workflow:

1. Understand the explicit task.
2. Identify newly introduced information.
3. Classify the information.
4. Select the minimum relevant documentation.
5. Check for conflicts and missing information.
6. Determine whether documentation must change.
7. Add or update authoritative documentation for accepted new knowledge.
8. Perform a proportional impact analysis.
9. Form an implementation plan.
10. Implement the smallest coherent change.
11. Add or update tests.
12. Update remaining affected documentation and references.
13. Run relevant verification commands.
14. Perform a final consistency check.
15. Report unresolved issues explicitly.

For trivial, local and low-risk changes, combine steps where appropriate.

Do not expand the task beyond its requested or documented scope.

---

## 20. Documentation-First Behaviour

Documentation-first does not mean that every change requires a separate
documentation-only commit or a mandatory waiting period.

It means that intended behaviour must be made explicit before it becomes hidden
inside implementation.

Use one of the following modes.

### Mode A: Specification before implementation

Use when:

* a new feature is introduced,
* domain behaviour changes,
* business rules change,
* architecture changes,
* interfaces change,
* workflows change,
* or the change has broad impact.

Sequence:

```text
knowledge intake
→ documentation update
→ implementation
→ tests
→ consistency review
```

### Mode B: Atomic documentation and implementation update

Use when:

* the change is small,
* the requirement is clear,
* the affected documentation location is obvious,
* and no architectural decision is required.

Sequence:

```text
classify requirement
→ update documentation, code and tests in one coherent task
→ consistency review
```

### Mode C: Implementation evidence correction

Use for:

* defects,
* test corrections,
* refactoring,
* internal maintenance,
* or restoration of already documented behaviour.

Documentation updates are required only when authoritative knowledge is
affected or currently inaccurate.

---

## 21. Documentation Maintenance

Update authoritative documentation when implementation changes:

* externally observable behaviour,
* domain behaviour,
* terminology,
* business rules,
* feature scope,
* quality requirements,
* APIs or data contracts,
* workflows,
* UI behaviour,
* architecture,
* deployment,
* security,
* privacy,
* operational behaviour,
* or accepted constraints.

Do not update documentation merely to restate low-level implementation details
that are already clear from the code and are not durable project knowledge.

Prefer updating the authoritative source rather than duplicating information.

Update references when documents are:

* renamed,
* moved,
* split,
* merged,
* superseded,
* or removed.

When implementation uncovers previously undocumented but important behaviour,
do not leave it only in code.

Classify it and either:

* document it as accepted knowledge,
* mark it as an assumption,
* raise it as an open question,
* or identify it as accidental behaviour to be removed.

---

## 22. Traceability

Where the project uses identifiers for requirements, rules, decisions or tests,
preserve those identifiers across related artefacts.

Possible relationships include:

```text
requirement
→ domain rule
→ feature specification
→ ADR
→ implementation
→ test
```

Traceability must remain proportional.

Do not introduce heavy traceability overhead for small projects unless required
by project-local rules.

For significant behaviour, Claude should be able to explain:

* which requirement authorizes it,
* which rule constrains it,
* which decision shapes it,
* and which test verifies it.

---

## 23. Research and Ideas

Research and ideas provide useful context but are not authoritative by default.

Do not implement directly from:

* research notes,
* brainstorm documents,
* exploratory chats,
* design alternatives,
* discarded concepts,
* or unreviewed drafts.

When research produces an accepted conclusion:

1. extract the durable knowledge,
2. place it in the appropriate authoritative document,
3. reference the research source where useful,
4. and only then use it as an implementation driver.

Do not turn the research document itself into the permanent source of truth
unless the project explicitly defines it as authoritative.

---

## 24. Design Results

Visual or UX exploration does not automatically define system behaviour.

When a design result introduces relevant knowledge, extract and classify:

* information architecture,
* user flow,
* interaction rules,
* visible states,
* validation behaviour,
* domain terminology,
* accessibility constraints,
* responsive behaviour,
* or new feature assumptions.

Accepted design knowledge must be written into the appropriate UI, flow, feature
or requirements documentation.

Images and mock-ups should not be the only source of behaviour.

Important behaviour shown visually must also be represented textually.

---

## 25. Tests as Verification

Tests verify documented behaviour.

They do not independently define business requirements unless the project has
explicitly adopted executable specifications as an authoritative source.

When tests and documentation conflict:

* identify whether the test is outdated,
* whether the documentation is outdated,
* or whether the implementation is wrong.

For new accepted requirements:

* add or update relevant tests,
* ensure acceptance criteria are verifiable,
* and preserve regression coverage for changed behaviour.

---

## 26. Completion Criteria

A task is complete when:

* the request has been correctly understood,
* relevant new knowledge has been classified,
* accepted new knowledge has been added to authoritative documentation,
* the requested behaviour is implemented,
* relevant tests pass,
* documentation, code and tests are consistent,
* no accepted decision is silently violated,
* no material contradiction remains,
* and unresolved issues are explicitly reported.

The final response must summarize:

* what changed,
* which new knowledge was documented,
* which authoritative documents were updated,
* what was implemented,
* what was verified,
* and which risks, assumptions or open questions remain.

---

## 27. Prohibited Behaviour

Claude must not:

* silently invent business rules,
* implement ideas as accepted requirements,
* treat research as authoritative without adoption,
* leave relevant new requirements only in chat or code,
* duplicate authoritative knowledge unnecessarily,
* silently violate accepted decisions,
* resolve material contradictions without disclosure,
* introduce technical details into domain documents,
* expand scope without justification,
* or claim completion when documentation, code and tests remain materially
  inconsistent.

---

## 28. Working Formula

The expected working model is:

```text
new information
+ classification
+ authoritative documentation
+ proportional impact analysis
+ implementation
+ tests
+ consistency review
= completed change
```

The core objective is not to maximize documentation volume.

The objective is to preserve durable project knowledge and make it usable as a
reliable implementation source for humans and AI agents.

---

## 29. Entry Points

This file defines the universal working model and stays free of project,
domain and technology knowledge (§1). Those live elsewhere:

```text
.claude/profiles/           Active profile — read this first
.claude/methodology/        How work is done
.claude/project/            Project-local rules and context
.claude/templates/          Document templates
docs/README.md              Index of authoritative project knowledge
```

Read the active profile at the start of a task. It states which modules apply,
which are deliberately absent, and which phase-specific rules override the
general workflow of this file.

The profile is authoritative for the phase. Where a phase rule restricts what
this file permits, the restriction wins.