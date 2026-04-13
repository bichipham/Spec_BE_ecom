import os
import sys
from pathlib import Path
from openai import OpenAI

MAX_DIFF_CHARS = 20000
MAX_SPEC_CHARS = 20000

def read_text(path: str) -> str:
    return Path(path).read_text(encoding="utf-8")

def main() -> int:
    if len(sys.argv) != 3:
        print("Usage: python scripts/ai_review.py <spec_path> <diff_path>", file=sys.stderr)
        return 1

    spec_path = sys.argv[1]
    diff_path = sys.argv[2]

    if "OPENAI_API_KEY" not in os.environ:
        print("OPENAI_API_KEY is missing", file=sys.stderr)
        return 1

    spec_text = read_text(spec_path)[:MAX_SPEC_CHARS]
    diff_text = read_text(diff_path)[:MAX_DIFF_CHARS]

    client = OpenAI(api_key=os.environ["OPENAI_API_KEY"])

    prompt = f"""
You are a senior software reviewer doing preliminary SDD review.

Review this pull request diff against the spec.

Return markdown with exactly these sections:
## Summary
## Spec alignment
## Missing tests
## Contract / API concerns
## Risky changes outside scope
## Reviewer focus

Rules:
- Be concrete and short.
- Only mention issues grounded in the spec or diff.
- If something looks fine, say so.
- Prefer reviewer guidance over generic advice.

=== SPEC PATH ===
{spec_path}

=== SPEC ===
{spec_text}

=== DIFF ===
{diff_text}
"""

    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[{"role": "user", "content": prompt}],
    )

    print(response.choices[0].message.content.strip())
    return 0

if __name__ == "__main__":
    raise SystemExit(main())