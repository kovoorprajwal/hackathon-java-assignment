# Questions

Here are 2 questions related to the codebase. There's no right or wrong answer - we want to understand your reasoning.

## Question 1: API Specification Approaches

When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded everything directly. 

What are your thoughts on the pros and cons of each approach? Which would you choose and why?

**Answer:**
```txt
OpenAPI YAML + Code Generation (Warehouse approach):
Pros:
- Single source of truth: the spec drives both server code and client SDKs
- Enables contract-first design — consumers can review the API before implementation
- Auto-generated models and interfaces reduce boilerplate and human error
- Swagger UI is available out-of-the-box for documentation and manual testing
- Makes API versioning and breaking-change detection easier

Cons:
- Initial setup overhead (YAML file, generator config, build plugin)
- Generated code can feel rigid — customising behaviour requires workarounds
- Developers need to understand the OpenAPI spec format
- Regeneration on every build can slow down the feedback loop

Hand-coded (Product/Store approach):
Pros:
- Full control over every line of code — easy to add custom logic
- Faster to start for small or simple APIs
- No tooling dependency

Cons:
- Documentation easily goes out of sync with implementation
- No enforced contract — consumers cannot validate expectations upfront
- More repetitive boilerplate for request/response models

My choice: OpenAPI spec + code generation for any API that is consumed externally or by other teams.
The contract-first approach enforces clear communication between teams, and auto-generated
interfaces ensure the implementation always matches the spec. For purely internal/utility
endpoints I would still prefer spec-first to keep documentation up to date automatically.
```

---

## Question 2: Testing Strategy

Given the need to balance thorough testing with time and resource constraints, how would you prioritize tests for this project? 

Which types of tests (unit, integration, parameterized, etc.) would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
Priority order and reasoning:

1. Unit Tests (highest priority, fastest feedback)
   - Test business logic in isolation: use cases (CreateWarehouseUseCase, ArchiveWarehouseUseCase, etc.)
   - Mock all external dependencies (WarehouseStore, LocationResolver)
   - Catches bugs in validation rules and business constraints cheaply and quickly
   - Example: WarehouseValidationTest, ArchiveWarehouseUseCaseTest, ReplaceWarehouseUseCaseTest

2. Integration Tests with Testcontainers (second priority)
   - Test the full stack with a real database (PostgreSQL via Testcontainers)
   - Catches transaction/concurrency issues that unit tests cannot simulate
   - Essential for this project given the complex transaction management requirements
   - Example: WarehouseConcurrencyIT — verifies optimistic locking and concurrent reads

3. Parameterized Tests (medium priority)
   - Use for boundary conditions: capacity limits, stock validation, edge cases
   - Covers many input combinations with little extra code
   - Reduces the risk of missing edge cases in validation logic

4. Transaction / Event Tests (high priority for this project specifically)
   - Verify that legacy system notifications fire only on successful commits
   - Example: StoreTransactionIntegrationTest
   - Critical because event-driven side effects are hard to reason about without tests

5. End-to-End / API Tests (lower priority, run less frequently)
   - Use RestAssured to test full HTTP request/response cycle
   - Valuable for smoke testing before deployment but slow to run

Keeping coverage effective over time:
- Run unit tests on every commit (fast, < 1 minute)
- Run integration tests on every pull request (slower but thorough)
- Use code coverage tools (JaCoCo) with a minimum threshold (e.g. 80%)
- Review and update tests whenever business rules change
- Treat a failing test as a blocker — never skip or delete tests to make builds pass
```
