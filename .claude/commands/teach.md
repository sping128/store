# Teach a Spring Boot concept

Suggest 3 next topics for the user to learn, let them pick, then teach it
in a quiz/guided format — the user writes the code, you review it.

## How to run this skill

### Step 1 — Read context
Read NOTES.md to see what has been covered so far.

### Step 2 — Suggest 3 topics
Propose exactly 3 options at different difficulty levels, framed around
**improving this store project** and building toward advanced topics
(security, caching, async messaging with Kafka, microservices, etc.).
Go step by step — don't jump to Kafka if Spring Security hasn't been covered.

Present them like this:

---
**Pick your next topic:**

🟢 **Easy — [Topic Name]**
[One sentence: what it is and why it matters for this project]

🟡 **Medium — [Topic Name]**
[One sentence: what it is and why it matters for this project]

🔴 **Hard — [Topic Name]**
[One sentence: what it is and why it matters for this project]

---

Wait for the user to choose before doing anything else.

### Step 3 — Do any plumbing
If the chosen topic requires a new Maven dependency, add it to pom.xml.
This is the one thing Claude does — it's setup, not the lesson itself.

### Step 4 — Teach in quiz format
Do NOT write implementation code for the user. Instead:
- Describe what to do in plain language
- Give annotation/class names as hints when helpful
- Ask the user to try it and paste their code
- Review their answer: confirm what's correct, explain the why, point out
  anything missing or improvable
- Only show full code when the user is stuck or to confirm a completed answer

### Step 5 — One sub-task at a time
Don't front-load all steps. After the user completes each step, reveal
the next one.

### Step 6 — Explain the why
After each step is confirmed correct, explain what Spring is doing under
the hood and why the pattern exists.

### Step 7 — End of lesson
Offer a stretch exercise, then ask if the user wants to update NOTES.md
and commit.

### Step 8 — Update NOTES.md
When the lesson is done, add the new topic to NOTES.md in the correct
numbered order.
