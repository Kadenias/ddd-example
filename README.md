# Case Study: Developing ??? with DDD

This project aims to showcase a domain driven approach at software architecture of a fictional business case. We explore different techniques of domain driven design and ways of their technical implementation using SpringModulith and JMolecules as core depdencies.

1. [Terms and Concepts](#terms-and-concepts)
   1. [Domain Driven Design](#domain-driven-design)
   2. [Bounded Context](#bounded-context)
   3. [Ubiquitous Language](#ubiquitous-language)
   4. [Aggregates](#aggregates)
   5. [Entities](#entities)
   6. [Value Objects](#value-objects)
2. [Case study](#case-study)
3. [Project phases](#project-phases)

## Terms and Concepts

### Domain Driven Design

The concept of `Domain Driven Design` was described by Eric Evans in 2003 in his book `Domain-Driven Design: Tackling Complexity in the Heart of Software`. In his book Evans formulated an approach to software modeling tightly bound to the problem space. Instead of focusing the design of complex software projects on technical architecture or data structures, DDD leverages the knowledge of business experts and channels this knowledge into domain objects and their interactions.

### Bounded Context

A bounded context defines a clear boundary within which a particular model and its ubiquitous language apply. Large systems usually contain multiple bounded contexts. Different parts of a business may interpret the same term differently. For example `customer` in marketing has a very different meaning then `customer` in terms of billing. DDD encourages identifying those boundaries explicitly and designing integration between contexts intentionally (e.g., via anti-corruption layers, context maps).

### Ubiquitous Language

Ubiquitous language is the shared vocabulary used by developers and domain experts. It is used consistently in conversations, models, documentation, and in the code . The goal is to reduce ambiguity: when a stakeholder says `order` everyone understands the same thing. This language is discovered through collaborative modelling and continuously refined. Using it helps keep requirements, design, and implementation aligned.

### Aggregates, Entities and Value Objects

##### Aggregates

An aggregate is a cluster of domain objects (entities and value objects) that are treated as a single consistency boundary. One entity inside the aggregate is the aggregate root — all external references access members of the aggregate via that root. Aggregates help decide transactional boundaries and how to structure persistence and concurrency.

#### Entities

TODO: find another example, we use Order as an aggregate

Entities are domain objects defined by their identity, not by their attributes. An entity’s attributes may change, but its identity persists. The positions pf an `Order` may change but it is still the same thing, identified by an `orderId`. Entities encapsulate behavior and invariants tied to that identity. In code they often have lifecycle logic, equality based on identity, and mutable state governed by business rules.

### Value Objects

Value objects represent descriptive aspects of the domain with no conceptual identity — they’re defined by their attributes and are immutable by convention. For example `Money` amount or `Address`. Two value objects with the same data are interchangeable. We use value objects to encapsulate validation and behavior for those attributes (e.g. currency arithmetic inside Money).

### Case study

TODO: Describe the fictional business

### Project phases

TODO: just GPTs ideas:

#### 1. Discovery & alignment (start here)

Goal: understand the business, find the core domain, and create a shared vocabulary.

Run collaborative workshops with domain experts, developers, product owners, and QA.

Deliverables: a living ubiquitous language glossary, rough domain notes, and a prioritized list of business outcomes.

Techniques: Event Storming, Domain Storytelling, and Example Mapping (see techniques section).

#### 2. Identify subdomains & the core domain

Goal: split the problem into subdomains (Core, Supporting, Generic) and pick where to invest effort.

Ask: which subdomain drives competitive advantage? That’s your core domain.

Deliverable: labeled subdomain map (Core / Supporting / Generic).

#### 3. Define bounded contexts and sketch a context map

Goal: decide where different models live and how they interact.

Map bounded contexts explicitly; show upstream/downstream relationships and integration patterns.

Choose context relationships: Shared Kernel, Customer–Supplier, Conformist, Anti-Corruption Layer (ACL), Published Language, etc.

Deliverable: Context Map with integration styles and contracts.

#### 4. Big-picture modeling and validation

Goal: create an initial domain model for each bounded context and validate assumptions.

Use lightweight models and concrete examples to validate with domain experts.

Deliverables: domain model sketches, key aggregates, invariants, a set of real examples/scenarios.

#### 5. Design tactical building blocks (per bounded context)

Goal: convert the model into code structure and boundaries.

Identify aggregates, aggregate roots, entities, value objects, and domain services.

Define domain events, repositories, factories, and invariants that aggregates must enforce.

Decide transactional boundaries and consistency guarantees (strong vs eventual consistency).

Deliverables: model specification, aggregate diagrams, repository interfaces.

#### 6. Integration & architecture decisions

Goal: pick integration approaches and architecture patterns.

Choose hexagonal (ports & adapters) or layered architecture to protect the domain model.

Decide on persistence pattern: ORM with transactional aggregates, CQRS, Event Sourcing, or hybrid.

Define anti-corruption layers for integrating with legacy or foreign models.

Deliverable: architecture sketch, integration contracts, API/Message schemas.

##### 7. Implement iteratively with model fidelity

Goal: build small, behavior-driven slices that preserve the model and language.

Implement a single aggregate and its use cases end-to-end (UI → Application Service → Aggregate → Repo).

Use tests (unit, integration) focused on behavior and domain invariants.

Keep refactoring the model as you learn.

Deliverables: working increments, tests that document domain rules, updated ubiquitous language.

### Techniques to use (workshops, modeling, engineering)

#### Collaborative discovery techniques

**Event Storming** — fast, broad discovery of domain events, commands, hot spots and domain processes using sticky notes. Excellent to reveal aggregates and bounded context boundaries.

**Domain Storytelling** — narrative-focused diagrams that show how roles and systems interact through events and commands.

**Example Mapping** — turn user stories into rules and examples (Rules / Examples / Questions). Great for clarifying edge cases.

**Story Mapping** — structure the backlog around user journeys to keep the model grounded in real flows.

#### Strategic DDD techniques

**Context Mapping** — explicitly draw bounded contexts and their relationships; annotate integration styles (ACL, Conformist, Shared Kernel, etc.).

**Subdomain classification** — label subdomains as Core, Supporting, Generic to focus design effort.

**Distillation** — find the kernel of the model (the most valuable concepts) and protect it.

#### Tactical DDD patterns to apply in code

**Entities** — identity-bearing objects with lifecycle.

**Value Objects** — small immutable objects modeling concepts (Money, Address). Use immutability and encapsulate validation.

**Aggregates & Aggregate Root** — enforce invariants and transactional boundaries. External references point to the root only.

**Repositories** — abstract data access for aggregates (interface in domain layer, adapter in infra).

**Factories** — create complex aggregates in a consistent way.

**Domain Services** — behavior that doesn’t fit naturally on an entity/value object (stateless domain logic).

**Domain Events & Event Handlers** — capture things that happened; useful for decoupling and eventual consistency.

**Specifications** — encapsulate complex query/validation rules when needed.

#### Architectural / integration patterns

**Hexagonal / Ports & Adapters** — keep domain pure and testable.

**CQRS** — separate read and write models when read complexity diverges from write needs.

**Event Sourcing** — consider only for domains where audit/history and rebuilding state from events are valuable (adds complexity).

**Anti-Corruption Layer (ACL)** — translate foreign models to keep your context’s model pure.

#### Testing / validation approaches

**Behavior-Driven Development (BDD)** — scenarios written in domain language (Gherkin or example mapping) that become automated tests.

**Unit tests for domain rules** — test invariants at aggregate root level (no mocking of repositories for rule tests).

**Integration tests** — validate application flows including repositories and integration points.

**Property-based testing** — useful for domain invariants with many combinations.