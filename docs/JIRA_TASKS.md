# Jira Backlog — Messagram MVP

This file is structured so you can copy each section into Jira as a story/task. It follows the project decisions in `docs/PROJECT_CONTEXT_DECISION_LOG.md` and `docs/INSTRUCTION_COMPACT.md`.

## Jira workflow recommendation
- Backlog: product and technical tasks
- Ready: requirements clarified, dependencies mapped
- In Progress: one active epic at a time
- Review: implementation + tests complete
- Done: acceptance criteria satisfied

## Epic overview

### EPIC-1: User Foundation
Goal: onboard invited users securely and establish identity/session contracts for the private messaging app.

### EPIC-2: Messaging Core
Goal: support private 1-to-1 conversation creation, message persistence, history retrieval, and delivery semantics.

### EPIC-3: Real-time & Reliability
Goal: deliver messages in real time, track read state, and ensure the system is observable and resilient.

---

## Jira Copy Template

Use this structure when creating tasks in Jira:

Title:
Summary:
Description:
Acceptance Criteria:
Priority:
Labels:
Story Points:
Dependencies:

---

## 1) EPIC: Foundation & Identity

### Issue: [EPIC-1] Build private user identity and auth foundation
Type: Epic
Priority: Highest
Labels: `messaging`, `mvp`, `identity`, `auth`

Summary:
As a founder-led private messaging platform, we need a secure identity and authentication foundation so only invited users can access the app and use conversation features.

Description:
- Support invite-only registration through founder-managed allowlist
- Use globally unique immutable email as primary identity
- Secure authentication with JWT-based session flow
- Enforce minimum validation and clear account lifecycle rules
- Prepare for future extension to social login or recovery when needed

Acceptance Criteria:
- Users can register only if email is allowlisted
- Email is immutable after account creation
- Passwords are hashed with a secure algorithm before persistence
- JWT authentication is required for protected API endpoints
- Invalid or expired tokens are rejected with clear errors
- Unauthorized requests return correct HTTP status and sanitized payload

Dependencies:
None

---

### Issue: [JIRA-101] Implement invite-only registration and login flow
Type: Story
Priority: Highest
Labels: `messaging`, `auth`, `user-management`
Story Points: 5

Summary:
As a registered user, I want to sign up and log in securely so I can access my direct messages in a private environment.

Description:
Build the user registration and login flow for invited users using email + password. Keep the design simple and private. The flow must validate email format, enforce allowlist, and prevent duplicate accounts.

Acceptance Criteria:
- New account registration succeeds only for allowlisted email addresses
- Duplicate email registration is rejected
- Password strength rules are enforced
- Login returns a valid auth token for authenticated endpoints
- Login fails with clear validation errors for invalid credentials
- Auth/token behavior is covered by automated tests

Dependencies:
EPIC-1

---

### Issue: [JIRA-102] Add JWT auth middleware and protected route enforcement
Type: Story
Priority: Highest
Labels: `messaging`, `security`, `backend`
Story Points: 3

Summary:
As a protected resource, I need JWT-based authentication so only authenticated users can access messaging APIs.

Description:
Add auth middleware to validate request tokens, enforce authorization on all messaging endpoints, and reject malformed or expired credentials consistently.

Acceptance Criteria:
- Request to protected endpoint without token is rejected
- Expired or malformed JWTs are rejected
- Authenticated user identity is available in request context
- Protected endpoints return consistent 401/403 responses
- Unauthorized attempts are logged for investigation

Dependencies:
JIRA-101

---

### Issue: [JIRA-103] Create user profile and account session model
Type: Story
Priority: Medium
Labels: `messaging`, `user-model`, `database`
Story Points: 3

Summary:
As a product admin and user, I need a stable user model so accounts can be identified, authenticated, and safely associated with conversations and messages.

Description:
Define core user data model with secure storage and minimal profile fields needed for MVP. Align with the project decision to use immutable email and allowlist-based onboarding.

Acceptance Criteria:
- User table stores unique email, password hash, and system metadata
- User ID is immutable and non-reusable
- Creation timestamp and updated timestamp are recorded
- User model is persisted with database constraints
- API contract for profile data is documented

Dependencies:
JIRA-101

---

## 2) EPIC: Messaging Core

### Issue: [EPIC-2] Build direct conversation and message flow
Type: Epic
Priority: Highest
Labels: `messaging`, `mvp`, `conversation`, `message`

Summary:
As a user, I need a reliable direct-message workflow so I can create one-to-one conversations, send messages, and view conversation history.

Description:
Create the minimum viable messaging experience: users can start a direct conversation with another known user, send messages, and retrieve messages in order with stable semantics. Only 1-to-1 conversation is in scope for this MVP.

Acceptance Criteria:
- One direct conversation exists per user pair
- Self-conversation is rejected
- Messages are stored with a conversation ID and sender ID
- Message ordering is deterministic and based on server-assigned sequence
- Conversation history can be retrieved with pagination
- Message creation and retrieval are protected by auth

Dependencies:
EPIC-1

---

### Issue: [JIRA-201] Create conversation domain model and pair uniqueness rules
Type: Story
Priority: Highest
Labels: `messaging`, `domain-model`, `conversation`
Story Points: 5

Summary:
As a user, I need a single direct conversation for each user pair so my messaging history is consistent and not duplicated.

Description:
Model the conversation aggregate and enforce domain rules for user pairs. Keep the rules explicit and testable to avoid duplicate or invalid conversation states.

Acceptance Criteria:
- Conversation record contains two participants for a direct chat
- Duplicate pair conversation is prevented
- Self-pair conversation is rejected
- Conversation creation is idempotent for the same pair when repeated
- Domain invariants are tested at the model/service layer

Dependencies:
JIRA-103

---

### Issue: [JIRA-202] Implement conversation creation API and participant validation
Type: Story
Priority: Highest
Labels: `messaging`, `api`, `conversation`
Story Points: 5

Summary:
As a user, I want to start a direct conversation with another user so we can exchange messages.

Description:
Expose API endpoints to create or fetch a direct conversation between two valid users. Validate user existence and ensure both participants are active identities before allowing message flow.

Acceptance Criteria:
- API creates a conversation for a valid pair of users
- API rejects nonexistent users
- API rejects a request for same-user conversation
- API returns existing conversation for the same pair without duplication
- Validation errors are surfaced clearly to the client

Dependencies:
JIRA-201

---

### Issue: [JIRA-203] Persist messages with sequence number and conversation history
Type: Story
Priority: Highest
Labels: `messaging`, `message`, `database`
Story Points: 8

Summary:
As a user, I need my messages saved reliably and returned in a consistent order so conversation history is trustworthy.

Description:
Persist every sent message with conversation ID, sender, sequence number, content, and timestamps. Return message history with clear ordering and pagination semantics.

Acceptance Criteria:
- Message insert is transactional and relation-safe
- Sequence number is assigned by the server per conversation
- Messages are returned in the correct order for conversation history
- Pagination works for older messages
- Payload validation prevents empty/invalid message content
- Lost or duplicate message requests are handled gracefully

Dependencies:
JIRA-202

---

### Issue: [JIRA-204] Add message send and fetch endpoints for direct chat
Type: Story
Priority: Highest
Labels: `messaging`, `api`, `message`
Story Points: 5

Summary:
As a user, I want to send and fetch messages in a conversation so I can communicate in real time.

Description:
Expose send and read endpoints for messages in a direct conversation. Keep the API simple and consistent with the domain contract, including validation and error handling.

Acceptance Criteria:
- User can send a valid message to a valid direct conversation
- Sender must be a participant in the conversation
- Invalid conversation or permission errors are rejected
- API returns conversation history or message payload with correct metadata
- Errors are traceable in logs and response payloads

Dependencies:
JIRA-203

---

## 3) EPIC: Real-time & Reliability

### Issue: [EPIC-3] Deliver messages in real time and track read state
Type: Epic
Priority: High
Labels: `messaging`, `realtime`, `read-receipts`, `reliability`

Summary:
As a user, I need immediate delivery of new messages and visibility into whether the other user has read them so the communication feels live and dependable.

Description:
Implement real-time message delivery through WebSocket or equivalent push mechanism, track message read status, and ensure users can recover from disconnects or reconnects without losing context.

Acceptance Criteria:
- New message delivery reaches the recipient without page refresh
- Reconnect logic resumes active chat state safely
- Read state is stored and queryable for messages in a conversation
- System errors and disconnect events are observable in logs
- Delivery and read semantics are documented for MVP

Dependencies:
EPIC-2

---

### Issue: [JIRA-301] Implement WebSocket connection lifecycle and auth
Type: Story
Priority: High
Labels: `messaging`, `websocket`, `realtime`
Story Points: 8

Summary:
As a connected user, I need a secure real-time channel so I can receive incoming messages immediately after they are sent.

Description:
Set up authenticated WebSocket connections for message delivery. The connection must validate identity, keep sessions clean on reconnect, and support message routing to the right user.

Acceptance Criteria:
- WebSocket connection requires valid auth token
- Invalid or expired sessions are rejected
- User receives messages targeted to their active session
- Connection disconnect/reconnect does not corrupt session state
- WebSocket events are covered by integration tests

Dependencies:
JIRA-102, JIRA-204

---

### Issue: [JIRA-302] Broadcast sent messages to recipient in real time
Type: Story
Priority: High
Labels: `messaging`, `websocket`, `delivery`
Story Points: 5

Summary:
As a sender and recipient, I want newly sent messages to appear in real time so the chat feels immediate and synchronous.

Description:
After a successful message save, publish the event to the relevant recipient connection and the sender UI channel. Keep the flow deterministic and operationally visible.

Acceptance Criteria:
- Sent message appears in recipient chat immediately
- Sender receives confirmation or optimistic state update as designed
- Delivery failure produces a clear traceable error
- Messages are not lost when a recipient is temporarily disconnected
- Event fan-out behavior is tested

Dependencies:
JIRA-203, JIRA-301

---

### Issue: [JIRA-303] Implement read receipt and conversation read state tracking
Type: Story
Priority: Medium
Labels: `messaging`, `read-status`, `analytics`
Story Points: 5

Summary:
As a user, I need to know if a message has been read so I understand message status and chat activity.

Description:
Track when a user reads a conversation and mark appropriate messages as read. Keep the model simple but reliable for MVP.

Acceptance Criteria:
- Read timestamp is stored for the recipient in the conversation context
- Message read state can be derived for conversation history
- Read event is idempotent when triggered multiple times
- API exposes current read state without exposing unnecessary internals
- Read behavior has integration test coverage

Dependencies:
JIRA-204, JIRA-301

---

### Issue: [JIRA-304] Add resilience: reconnect, retry, and failure handling
Type: Story
Priority: Medium
Labels: `messaging`, `reliability`, `operations`
Story Points: 5

Summary:
As a user, I need the chat to remain stable when connections fail or server processes retry so the app does not silently break.

Description:
Add robust handling for transient network errors, reconnect events, duplicate requests, and message processing edge cases. Focus on core reliability without overbuilding infrastructure.

Acceptance Criteria:
- Reconnection logic restores active chat context safely
- Duplicate message submission is handled without corruption
- Retryable failures are logged and bounded
- Client/server contract remains predictable under transient errors
- Critical failure cases are covered by tests

Dependencies:
JIRA-301, JIRA-302

---

## Subtask breakdown for each story

### JIRA-101: Invite-only registration and login flow
- Create user registration request/response contract
- Add allowlist validation for invited emails
- Implement password hashing and storage
- Create login endpoint with credential validation
- Return JWT token on successful login
- Add tests for duplicate user, invalid format, and invalid login

### JIRA-102: JWT auth middleware and protected route enforcement
- Define JWT config and token claims
- Add authentication filter for protected endpoints
- Validate expiry and signature on every request
- Expose authenticated user context to downstream services
- Reject unauthorized requests with correct error codes
- Add security tests for valid/expired/malformed tokens

### JIRA-103: User profile and account session model
- Define DB schema for users and account metadata
- Add unique email constraint and immutable identity rules
- Add user repository and persistence layer
- Create profile response model for API usage
- Validate account lifecycle rules and migration assumptions
- Add integration test for user creation and retrieval

### JIRA-201: Conversation domain model and pair uniqueness rules
- Define conversation aggregate and participant relationship model
- Add rules for exactly one conversation per user pair
- Reject self-conversation and invalid participant combinations
- Implement pair ordering helper for deterministic uniqueness checks
- Add repository/service tests for duplicate conversation prevention
- Document invariants for future extension to group chat

### JIRA-202: Conversation creation API and participant validation
- Create conversation request DTO and validation rules
- Validate both user IDs exist and active state passes
- Implement create/retrieve conversation service method
- Return existing conversation when pair already exists
- Add API tests for valid/invalid conversation creation flows
- Add error mapping for bad requests and not-found cases

### JIRA-203: Persist messages with sequence number and conversation history
- Define message schema with conversation, sender, and body
- Add server-assigned sequence number generation per conversation
- Implement transaction-safe message insert and persistence
- Add message history query with pagination support
- Validate empty/invalid payload handling
- Add integration tests for ordering and history retrieval

### JIRA-204: Message send and fetch endpoints for direct chat
- Define send-message request contract and validation
- Enforce sender is participant in the conversation
- Implement send-message API endpoint
- Implement fetch-chat-history endpoint with pagination
- Add unauthorized/invalid-conversation handling
- Add API integration tests for successful and failed sends

### JIRA-301: WebSocket connection lifecycle and auth
- Add authenticated WebSocket handshake flow
- Bind session to user identity
- Manage connect/disconnect lifecycle and cleanup
- Route active connection by user ID
- Add reconnect handling and stale-session cleanup
- Add integration tests for auth and connection state

### JIRA-302: Broadcast sent messages to recipient in real time
- Publish message event after successful persistence
- Map message recipient to active connection
- Send outbound event to recipient session
- Include payload format and ack/notification contract
- Add failure handling for offline/disconnected recipients
- Add integration tests for broadcast and fan-out behavior

### JIRA-303: Read receipt and conversation read state tracking
- Define read-state model and event contract
- Add endpoint to mark conversation as read
- Compute last-read markers for message history
- Ensure repeated read events are idempotent
- Expose read status in API response payloads
- Add tests for read state and duplicate read events

### JIRA-304: Resilience: reconnect, retry, and failure handling
- Define retry policy for transient network and processing failures
- Add duplicate-request detection and deduplication strategy
- Implement graceful reconnect flow for clients
- Log bounded retry failures without noisy loops
- Add edge-case tests for disconnect, retry, and duplicates
- Document failure modes and operational expectations

## Cross-cutting tasks

### Issue: [JIRA-401] Add observability, logging, and request tracing for messaging flow
Type: Task
Priority: High
Labels: `observability`, `backend`, `ops`
Story Points: 3

Summary:
As a developer, I need logs and tracing so the messaging system is debuggable and production-ready enough for early invited-user testing.

Description:
Capture authentication events, message send lifecycle, connection events, and error states with structured logs. Trace important workflows from request to persistence and delivery.

Acceptance Criteria:
- Logs include request ID and user ID for relevant flows
- Message send and delivery lifecycle are auditable
- High-value failures are structured for debugging
- Logs are searchable and not noisy
- Key endpoints are traceable for support investigations

Dependencies:
EPIC-1, EPIC-2, EPIC-3

---

### Issue: [JIRA-402] Create security and input validation hardening checklist
Type: Task
Priority: High
Labels: `security`, `validation`, `qa`
Story Points: 3

Summary:
As a product owner, I need security validation so the MVP is safe enough for private invited-user testing.

Description:
Review API input validation, secrets management, auth boundaries, and abuse prevention before launch. Document the security baseline and any deferred features.

Acceptance Criteria:
- All user inputs are validated before persistence
- Secrets are not stored in source code
- Auth endpoints reject invalid/malicious payloads
- Security review notes are captured in project docs
- Risk register is updated for unresolved security concerns

Dependencies:
EPIC-1, EPIC-2

---

### Issue: [JIRA-403] Build MVP test suite for core messaging invariants
Type: Task
Priority: High
Labels: `testing`, `quality`, `mvp`
Story Points: 5

Summary:
As a developer, I need automated tests that protect the domain invariants and critical flows of the messaging system.

Description:
Build unit and integration tests covering user auth, conversation integrity, message persistence, read semantics, WebSocket delivery, and failure paths. Focus on business-critical invariants first.

Acceptance Criteria:
- Auth tests cover valid and invalid token flow
- Conversation tests cover duplicate prevention and pair validation
- Message tests cover persistence and ordering
- Realtime tests cover connection auth and message broadcast
- Regression failures are caught by CI

Dependencies:
EPIC-1, EPIC-2, EPIC-3

---

## Suggested Jira board columns
- Backlog
- Ready
- In Progress
- In Review
- Done
- Blocked

## Definition of Done (MVP)
- Feature works end-to-end for invited users
- Domain constraints are enforced in code and tests
- Auth and authorization are validated
- Real-time message flow is stable under normal conditions
- Necessary logs/metrics exist
- No obvious security holes for private MVP usage
- Documentation updated for decision and usage notes

## Recommended priority order
1. JIRA-101: registration/login
2. JIRA-102: JWT protection
3. JIRA-201: conversation rules
4. JIRA-202: conversation API
5. JIRA-203: message persistence
6. JIRA-204: message API
7. JIRA-301: WebSocket auth
8. JIRA-302: realtime delivery
9. JIRA-303: read state
10. JIRA-304: resilience
11. JIRA-401 to JIRA-403: operational hardening and tests

This backlog is intentionally scoped to the project decision of MVP 1-to-1 messaging, not broad product expansion.
