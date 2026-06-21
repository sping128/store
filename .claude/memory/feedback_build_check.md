---
name: feedback-build-check
description: "Always compile the project after reviewing the user's code changes during lessons"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 3454400d-acae-4fc9-bb03-e41207ac037c
---

After each step in a /teach lesson, run `./mvnw compile` to verify the user's code builds before giving feedback.

**Why:** User asked for this explicitly — they want confirmation the code is correct, not just conceptually sound.

**How to apply:** Every time the user pastes or writes code during a teaching session, compile and report the result before moving to the next step.
